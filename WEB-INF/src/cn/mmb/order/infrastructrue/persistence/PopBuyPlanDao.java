package cn.mmb.order.infrastructrue.persistence;

import cn.mmb.order.domain.entity.PopBuyPlan;
import cn.mmb.order.domain.entity.PopBuyPlanProduct;
import cn.mmb.order.domain.entity.PopStockArea;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * POP采购计划单持久化操作
 * @author likaige
 * @create 2015年9月16日 上午9:23:03
 */
@Repository
public class PopBuyPlanDao extends AbstractDaoSupport {

	private static Log log = LogFactory.getLog("debug.Log");
	
	public int getOrderIdByOrderCode(String orderCode) {
		int orderId = 0;
		String sql = "select id from user_order where code=?";
		List<Map<String, Object>> mapList = this.getJdbcTemplate().queryForList(sql, orderCode);
		if(!mapList.isEmpty()){
			orderId = Integer.valueOf(mapList.get(0).get("id").toString());
		}
		return orderId;
	}

	/**
	 * 获取京东的所有发货仓，第一个为就近发货仓
	 * @param addr1 一级地址
	 * @param addr2 二级地址
	 * @return
	 * @author likaige
	 * @create 2015年9月16日 下午2:25:35
	 */
	public List<PopStockArea> getPopStockAreaList(int addr1, int addr2) throws Exception{
		//获取仓库id
		String sql = "select area_id from stock_area_popec_add_link where add_p_id="+addr1+" and add_c_id="+addr2;
		List<Map<String,Object>> areaList= this.getJdbcTemplate().queryForList(sql);
		long area = 0;
		if(!areaList.isEmpty()){
			area = (Long)areaList.get(0).get("area_id");
		}
		
		//获取京东7大仓
		String areaSql = "select * from stock_area_popec";
		List<PopStockArea> list = this.getJdbcTemplate().query(areaSql.toString(), new BeanPropertyRowMapper<PopStockArea>(PopStockArea.class));
		PopStockArea tmp = null;
		for (Iterator<PopStockArea> iterator = list.iterator(); iterator.hasNext();) {
			PopStockArea popStockArea = iterator.next();
			//获取就近仓
			if(popStockArea.getId()==area){
				tmp = popStockArea;
				iterator.remove();
			}
		}
		
		if(tmp != null){
			//将就近仓放在首位
			list.add(0, tmp);
		}
		
		return list;
	}

	/**
	 * 根据京东订单号更新采购计划单
	 * @param status
	 * @param jdOrderCode
	 * @author yaoliang
	 * @time 2015年9月16日 上午10:30:10
	 * @throws Exception
	 */
	public int updateBuyPlanByOrderCode(String status, String jdOrderCode) throws Exception{
		StringBuilder sql = new StringBuilder();
		
		sql.append("UPDATE pop_buy_plan set ")
		.append(status)
		.append(" where pop_order_code='")
		.append(jdOrderCode)
		.append("'");
		 
		return this.getJdbcTemplate().update(sql.toString());
	}

	//保存计划单
	public void insertPopBuyPlan(PopBuyPlan plan) throws Exception{
		String sql = "INSERT INTO pop_buy_plan(order_code,pop_order_code,order_price,order_naked_price,freight,"
				+"status,create_time,type,pop_type,stock_area_popec_id) VALUES(?,?,?,?,?,?,?,?,?,?)";
		 
		Object[] args = {plan.getOrderCode(),plan.getPopOrderCode(),plan.getOrderPrice(),
				plan.getOrderNakedPrice(),plan.getFreight(),plan.getStatus(),plan.getCreateTime(),
				plan.getType(),plan.getPopType(),plan.getStockAreaPopecId()};
		 
		this.getJdbcTemplate().update(sql.toString(), args);
		
		int id = this.getJdbcTemplate().queryForInt("SELECT LAST_INSERT_ID()");
		plan.setId(id);
	}

	//保存计划单商品
	public void insertPopBuyPlanProduct(PopBuyPlan buyPlan) throws Exception{
		
		List<PopBuyPlanProduct> list = buyPlan.getProductList();
		
		String sql = "INSERT INTO pop_buy_plan_product(pop_buy_plan_id,pop_product_id,my_product_id,name,pop_category,count,"
				+"price,tax,naked_price,type,oid) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
		
		List<Object[]> argsList = new ArrayList<Object[]>();
		
		for (PopBuyPlanProduct product : list) {
			Object[] args = {buyPlan.getId(),product.getPopProductId(),product.getMyProductId(),product.getName(),
					product.getPopCategory(),product.getCount(),product.getPrice(),product.getTax(),product.getNakedPrice(),
					product.getType(),product.getOid()};
			argsList.add(args);
		}
		
		this.getJdbcTemplate().batchUpdate(sql, argsList);

	}

	/**
	 * 根据MMB订单号获取计划单中的商品信息
	 * @param orderCode MMB订单号
	 * @return
	 * @author likaige
	 * @create 2015年9月17日 上午11:23:35
	 */
	public List<PopBuyPlanProduct> getPopBuyPlanProductList(String orderCode) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT p.*");
		sql.append(" FROM pop_buy_plan t");
		sql.append(" JOIN pop_buy_plan_product p on p.pop_buy_plan_id=t.id");
		sql.append(" WHERE t.order_code=?");
		RowMapper<PopBuyPlanProduct> rm = new BeanPropertyRowMapper<PopBuyPlanProduct>(PopBuyPlanProduct.class);
		return this.getJdbcTemplate().query(sql.toString(), rm, orderCode);
	}

	/**
	 * 获取MMB商品对应的POP商品id
	 * @param mySkuIdList MMB商品id
	 * @return
	 * @author likaige
	 * @create 2015年9月17日 下午2:12:10
	 */
	public Map<Integer, Integer> getPopSkuIdByMySkuId(List<Integer> mySkuIdList) {
		Map<Integer, Integer> skuIdMap = new HashMap<Integer, Integer>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.product_info_id,t.sku_id");
		sql.append(" FROM tp_mmb_product t");
		sql.append(" WHERE t.product_info_id in(").append(StringUtils.join(mySkuIdList, ',')).append(")");
		List<Map<String, Object>> mapList = this.getJdbcTemplate().queryForList(sql.toString());
		for(Map<String, Object> map : mapList){
			Integer skuId = Integer.valueOf(map.get("product_info_id").toString());
			Integer popSkuId = Integer.valueOf(map.get("sku_id").toString());
			skuIdMap.put(skuId, popSkuId);
		}
		return skuIdMap;
	}
	
	/**
	 * 获取POP商品对应的MMB商品id
	 * @param popSkuIdList POP商品id
	 * @return
	 * @author likaige
	 * @create 2015年11月2日 上午11:34:44
	 */
	public Map<Integer, Integer> getMySkuIdByPopSkuId(List<Integer> popSkuIdList) {
		Map<Integer, Integer> skuIdMap = new HashMap<Integer, Integer>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.product_info_id,t.sku_id");
		sql.append(" FROM tp_mmb_product t");
		sql.append(" WHERE t.sku_id in(").append(StringUtils.join(popSkuIdList, ',')).append(")");
		List<Map<String, Object>> mapList = this.getJdbcTemplate().queryForList(sql.toString());
		for(Map<String, Object> map : mapList){
			Integer skuId = Integer.valueOf(map.get("product_info_id").toString());
			Integer popSkuId = Integer.valueOf(map.get("sku_id").toString());
			skuIdMap.put(popSkuId, skuId);
		}
		return skuIdMap;
	}

	/**
	 * 更新user_order_product_split_history的价格(含税)
	 * @param list 采购计划单商品信息
	 * @param orderId MMB订单id
	 * @throws Exception
	 * @author yaoliang
	 * @time 2015年9月17日 上午10:30:10
	 */
	public void updateSplitHistory(List<PopBuyPlanProduct> list, int orderId) throws Exception{
		String sql = "update user_order_product_split_history set price5 = ? where order_id = ? and product_id = ?";
		List<Object[]> argsList = new ArrayList<Object[]>();
		for (PopBuyPlanProduct product : list) {
			Object[] args = {product.getPrice(),orderId,product.getMyProductId()};
			argsList.add(args);
			log.info("updateSplitHistory orderId="+orderId+", price="+product.getPrice()+", productId="+product.getMyProductId());
		}

		//调试日志
		int[] updateCounts = this.getJdbcTemplate().batchUpdate(sql, argsList);
		log.info("updateSplitHistory orderId="+orderId+", updateCounts="+Arrays.toString(updateCounts));
	}

	/**
	 * 通过父id获取其子id
	 * @throws Exception
	 * @author yaoliang
	 * @time 2015年10月10日 上午10:30:10
	 */
	public List<Integer> getChildernIdsByOrderId(int orderId) throws Exception{
		String sql = "select child_id from user_order_sub_list where parent_id = "+orderId;
		return getJdbcTemplate().queryForList(sql, Integer.class);
	}
	
	/**
	 * 根据MMB订单号获取计划单信息
	 * @param orderCode MMB订单号
	 * @return
	 * @author likaige
	 * @create 2015年11月26日 上午11:23:35
	 */
	public PopBuyPlan getPopBuyPlan(String orderCode) {
		String sql = "SELECT t.* FROM pop_buy_plan t WHERE t.order_code=?";
		RowMapper<PopBuyPlan> rm = new BeanPropertyRowMapper<PopBuyPlan>(PopBuyPlan.class);
		try {
			return this.getJdbcTemplate().queryForObject(sql, rm, orderCode);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
