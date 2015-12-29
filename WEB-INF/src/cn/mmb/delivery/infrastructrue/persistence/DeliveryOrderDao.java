package cn.mmb.delivery.infrastructrue.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import adultadmin.action.vo.voOrder;
import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.util.DateUtil;
import cn.mmb.delivery.domain.model.dao.YdMailingDao;
import mmb.hessian.ware.DeliverOrderInfoBean;

/**
 * 订单状态更新接口
 * @author yaoliang 
 * @create 2015年5月7日 上午8:40:22
 */
@Repository
public class DeliveryOrderDao extends AbstractDaoSupport {
	
	/**
	 * 修改订单状态
	 * @author yaoliang 
	 * @create 2015年5月7日 上午8:40:22
	 * @param orderId 要修改的订单id
	 * @param orderStatus 订单状态
	 */
	public int updateOrderStatus(int orderId, int orderStatus) throws Exception{
		StringBuffer sql = new StringBuffer(); 
		sql.append("update user_order set status=?");
		if(orderStatus == voOrder.STATUS6){
			sql.append(",stockout=1,confirm_datetime='").append(DateUtil.getNow()).append("'");
		}
		else if(orderStatus == voOrder.STATUS3){
			sql.append(",stockout=0");
		}
		sql.append(" where id=?");
		return this.getJdbcTemplate().update(sql.toString(), orderStatus, orderId);
	}
	
	/**
	 * 如果运单状态里有[妥投]、[拒收]，两种状态时，需要将对应订单的时间更新为当前时间
	 * @author yaoliang 
	 * @create 2015年5月15日 上午8:40:22
	 * @param orderCodePOP 订单号
	 * @param status 状态
	 */
	public int updatePOPOrderInfo(String orderCodePOP, int status) throws Exception{
		if(orderCodePOP == null||orderCodePOP.equals("")){
			throw new Exception("请输入订单信息");
		}
		StringBuffer sql = new StringBuffer();
		sql.append("update pop_order_info set status=?,");
		if(status == DeliverOrderInfoBean.DELIVER_STATE7){
			sql.append(" delivery_time = now()");
		}else if(status == DeliverOrderInfoBean.DELIVER_STATE8){
			sql.append(" refused_time = now()");
		}
		sql.append(" where pop_order_code=?");
		return this.getJdbcTemplate().update(sql.toString(), status, orderCodePOP);
	}
	
	/**
	 * 根据订单id获取订单信息
	 * @param orderId 订单id
	 * @return
	 * @throws Exception
	 * @author likaige
	 * @create 2015年6月4日 下午3:39:06
	 */
	public voOrder getUserOrder(int orderId) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT t.id,t.code,t.status");
		sql.append(" FROM user_order t");
		sql.append(" WHERE t.id=?");
		RowMapper<voOrder> rm = new BeanPropertyRowMapper<voOrder>(voOrder.class);
		return this.getJdbcTemplate().queryForObject(sql.toString(), rm, orderId);
	}

	/**
	 * 保存订单日志
	 * @param log 日志信息
	 * @author likaige
	 * @create 2015年6月4日 下午4:03:27
	 */
	@Deprecated
	public void saveOrderAdminLog(OrderAdminLogBean log) throws Exception {
		String sql = "INSERT INTO order_admin_log(create_datetime,content,type,"
				+ "user_id,username,order_id,order_code) VALUES(?,?,?,?,?,?,?)";
		Object[] args = {log.getCreateDatetime(),log.getContent(),log.getType(),
				log.getUserId(),log.getUsername(),log.getOrderId(),log.getOrderCode()};
		this.getJdbcTemplate().update(sql, args);
	}
	/**
	 * 保存关联表
	* @Description: 
	* @author ahc
	 */
	public int addDeliverRelation(int deliverId, OrderStockBean os){
		StringBuffer sql = new StringBuffer();
		sql.append("insert into deliver_relation (deliver_id,order_code,status,package_code,create_datetime,stock_area)");
		sql.append(" values (?,?,?,?,?,?)");
		Object[] args ={deliverId,os.getOrderCode(),0,os.getPackageCode(),DateUtil.getNow(), os.getStockArea()};
		return this.getJdbcTemplate().update(sql.toString(), args);
	}
	
	/**
	 * 获取关联表
	* @Description: 
	* @author ahc
	 */
	public List getDeliverRelationList(String condition){
		String sql="SELECT * FROM deliver_relation  WHERE "+condition;
		return this.getJdbcTemplate().queryForList(sql);
	}
	
	/**
	 * 根据订单号获取订单地址
	* @Description: 
	* @author ahc
	 */
	public voOrder getUserOrderByCode(String condition) throws Exception {
		String sql=" SELECT address FROM user_order WHERE "+condition;
		List<Map<String,Object>> list =this.getJdbcTemplate().queryForList(sql);
		voOrder vo =null;
		for(Map<String,Object> map : list){
			vo = new voOrder();
			vo.setAddress(map.get("address")+"");
		}
		return vo;
	}
	/**
	 * 保存关联表明细
	* @Description: 
	* @author ahc
	 */
	public void addDeliverRelationInfo(int deliverId,List<YdMailingDao> list) throws DataAccessException{
		for(YdMailingDao ymb :list){
			StringBuffer sql = new StringBuffer();
			sql.append("insert into deliver_relation_info(deliver_id,order_code,type,info,info_type,create_datetime) values (?,?,?,?,?,?)");
			Object[]args ={deliverId,ymb.getId(),"1",ymb.getDistricenterCode(),null,DateUtil.getNow()};
			this.getJdbcTemplate().update(sql.toString(), args);
			
			StringBuffer sql2 = new StringBuffer();
			sql2.append("insert into deliver_relation_info(deliver_id,order_code,type,info,info_type,create_datetime) values (?,?,?,?,?,?)");
			Object[]args2 ={deliverId,ymb.getId(),"2",ymb.getDistricenterName(),null,DateUtil.getNow()};
			this.getJdbcTemplate().update(sql2.toString(), args2);
			
			StringBuffer sql3 = new StringBuffer();
			sql3.append("insert into deliver_relation_info(deliver_id,order_code,type,info,info_type,create_datetime) values (?,?,?,?,?,?)");
			Object[]args3 ={deliverId,ymb.getId(),"3",ymb.getBigpenCode(),null,DateUtil.getNow()};
			this.getJdbcTemplate().update(sql3.toString(), args3);
			
			StringBuffer sql4 = new StringBuffer();
			sql4.append("insert into deliver_relation_info(deliver_id,order_code,type,info,info_type,create_datetime) values (?,?,?,?,?,?)");
			Object[]args4 ={deliverId,ymb.getId(),"4",ymb.getPosition(),null,DateUtil.getNow()};
			this.getJdbcTemplate().update(sql4.toString(), args4);
			
			StringBuffer sql5 = new StringBuffer();
			sql5.append("insert into deliver_relation_info(deliver_id,order_code,type,info,info_type,create_datetime) values (?,?,?,?,?,?)");
			Object[]args5 ={deliverId,ymb.getId(),"5",ymb.getPositionNo(),null,DateUtil.getNow()};
			this.getJdbcTemplate().update(sql5.toString(), args5);
		}
	}
	
	/**
	 * 更新关联表
	* @Description: 
	* @author ahc
	 */
	public void updateDeliverRelation(List<YdMailingDao> list) throws DataAccessException{
		StringBuffer sql =null;
		for(YdMailingDao ymb :list){
			sql = new StringBuffer();
			sql.append("UPDATE `deliver_relation` SET `status`='1' WHERE (`order_code`='"+ymb.getId()+"')");
			//Object[]args ={ymb.getId()};
			this.getJdbcTemplate().update(sql.toString());
		}
	}
	
	public List<OrderStockBean> getOrderStock(int deliverId,String startTime,String endTime){
		String sql="SELECT * FROM order_stock  WHERE deliver ="+deliverId+" and status <> "+OrderStockBean.STATUS4+" and create_datetime BETWEEN '"+startTime+"' and '"+endTime+"'";
		List<Map<String,Object>> list= this.getJdbcTemplate().queryForList(sql);
		OrderStockBean os = null;
		List<OrderStockBean> list2 = new ArrayList<OrderStockBean>();
		for(Map<String,Object> map : list){
			os = new OrderStockBean();
			os.setOrderCode(map.get("order_code")+"");
			os.setStockArea(Integer.parseInt(String.valueOf(map.get("stock_area")+"")));
			list2.add(os);
		}
		return list2;
	}
}
