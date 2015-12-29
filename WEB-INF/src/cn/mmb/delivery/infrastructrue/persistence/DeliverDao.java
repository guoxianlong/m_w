package cn.mmb.delivery.infrastructrue.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.stock.stat.DeliverCorpInfoBean;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import adultadmin.bean.order.OrderStockBean;
import cn.mmb.delivery.domain.model.vo.DeliverSwitchBean;

@Repository
public class DeliverDao extends AbstractDaoSupport {

	/**
	 * 获取总数量
	 * @param queryParam
	 * @return
	 */
	public int deliverSwitchCount(Map<String,String> map) {
		
		StringBuilder sql = new StringBuilder("");
		sql.append("SELECT COUNT(*) FROM deliver_switch where ").append(map.get("condition"));
		return this.getJdbcTemplate().queryForInt(sql.toString());
	}
	
	/**
	 * 获取列表
	 * @param queryParam
	 * @return
	 */
	public List<DeliverSwitchBean> deliverSwitchList(Map<String,String> map) {
		
		StringBuilder sql = new StringBuilder("");
		sql.append("SELECT * FROM deliver_switch where ").append(map.get("condition"));
		if(map.get("sort")!= null){
			if(map.get("sort").equals("createDateTime")){
				sql.append(" order by create_datetime "+map.get("order"));
			}
			if(map.get("sort").equals("modifyDatetime")){
				sql.append(" order by modify_datetime "+map.get("order"));
			}
		}
		if(map.get("start")!= null){
			sql.append(" limit "+map.get("start"));
		}
		if(Integer.parseInt(map.get("count"))>0){
			sql.append(" , "+map.get("count"));
		}
		List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql.toString());
		List<DeliverSwitchBean> list2 = new ArrayList<DeliverSwitchBean>();
		for(Map<String,Object> map2 : list){
			DeliverSwitchBean deliverSwitch = new DeliverSwitchBean();
			deliverSwitch.setId(Integer.parseInt(String.valueOf(map2.get("id"))));
			deliverSwitch.setStockArea(String.valueOf(map2.get("stock_area")));
			deliverSwitch.setOrderCode(String.valueOf(map2.get("order_code")));
			deliverSwitch.setOriginDeliverName(String.valueOf(map2.get("origin_deliver_name")));
			deliverSwitch.setDeliverName(String.valueOf(map2.get("deliver_name")));
			deliverSwitch.setOriginPackageCode(String.valueOf(map2.get("origin_package_code")));
			deliverSwitch.setPackageCode(String.valueOf(map2.get("package_code")));
			deliverSwitch.setCreateDateTime(String.valueOf(map2.get("create_datetime")));
			deliverSwitch.setModifyDatetime(String.valueOf(map2.get("modify_datetime")));
			deliverSwitch.setRemark(String.valueOf(map2.get("remark")));
			deliverSwitch.setOperUserId(String.valueOf(map2.get("oper_user_id")));
			deliverSwitch.setOperUserName(String.valueOf(map2.get("oper_user_name")));
			list2.add(deliverSwitch);
		}
		return list2;
	}
	
	/**
	 * 获取快递公司信息
	 * @param queryParam
	 * @return
	 */
	public Map<String,Object> getDeliver(String orderCode) {
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		StringBuilder sql = new StringBuilder("");
		sql.append("SELECT dci.`name`,ap.package_code from audit_package ap left join deliver_corp_info dci on ap.deliver = dci.id WHERE ap.order_code= ");
		sql.append("'");
		sql.append(orderCode);
		sql.append("'");
		List<Map<String,Object>> list =this.getJdbcTemplate().queryForList(sql.toString());
		for(Map<String,Object> map :list){
			resultMap = map;
		}
		return resultMap;
	}
	
	/**
	 * 获取出库单
	* @Description: 
	* @author ahc
	 */
	public OrderStockBean getOrderStock(String orderCode){
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * from order_stock WHERE status <>3 and order_code= ");
		sql.append("'");
		sql.append(orderCode);
		sql.append("'");
		List<Map<String,Object>> list =this.getJdbcTemplate().queryForList(sql.toString());
		OrderStockBean os = null;
		for(Map<String,Object> map : list){
			os = new OrderStockBean();
			os.setDeliver(Integer.parseInt(String.valueOf(map.get("deliver"))));
			os.setStockArea(Integer.parseInt(String.valueOf(map.get("stock_area"))));
			os.setOrderCode(String.valueOf(map.get("order_code")));
		}
		return os;
	}
	/**
	 * 更新订单表
	* @Description: 
	* @author ahc
	 */
	public int updateUserOrder(int targetDeliver,String orderCode){
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE user_order SET deliver = "+targetDeliver+" WHERE (code ='"+orderCode+"')");
		return this.getJdbcTemplate().update(sql.toString());
	}
	
	/**
	 * 更新出库单
	* @Description: 
	* @author ahc
	 */
	public int updateOrderStock(int targetDeliver,String orderCode){
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE order_stock SET deliver = "+targetDeliver+" WHERE (order_code ='"+orderCode+"')");
		return this.getJdbcTemplate().update(sql.toString());
	}
	
	/**
	 * 更新复核列表
	* @Description: 
	* @author ahc
	 */
	public void updateAuditPackage(int targetDeliver,String orderCode,int orderType) throws Exception{
		String packageCode ="";
		boolean isdeliverPackageCode = false;//判断是否是从deliver_package_code表中取过包裹单号
		StringBuilder sql = new StringBuilder();
		StringBuffer sql2 = new StringBuffer();
		StringBuffer sql3 = new StringBuffer();
		StringBuffer sql4 = new StringBuffer();
		StringBuffer sql5 = new StringBuffer();
		sql.append("SELECT package_code from audit_package WHERE order_code= ");
		sql.append("'");
		sql.append(orderCode);
		sql.append("'");
		List<Map<String,Object>> list =this.getJdbcTemplate().queryForList(sql.toString());
		if(list==null || list.isEmpty()){
			return;
		}
		/**
		 * 根据是否有包裹单号确定是否复核
		 */
		for(Map<String,Object> map :list){
			packageCode =String.valueOf(map.get("package_code"));
		}
		if(packageCode==null || "".equals(packageCode)){
			sql2.append("UPDATE audit_package SET deliver = "+targetDeliver+" WHERE (order_code ='"+orderCode+"')");
			
		}else{
			//只有目标快递是：如风达、圆通和京东，则从deliver_relation表中取包裹单号
			if(targetDeliver == DeliverCorpInfoBean.DELIVER_ID_YT_WX || targetDeliver ==DeliverCorpInfoBean.DELIVER_ID_YT_CD || targetDeliver ==DeliverCorpInfoBean.DELIVER_ID_RFD_SD || targetDeliver == DeliverCorpInfoBean.DELIVER_ID_JD_CD || targetDeliver == DeliverCorpInfoBean.DELIVER_ID_JD_WX){
				sql3.append("SELECT package_code FROM `deliver_relation` WHERE deliver_id="+targetDeliver+" and status in(1,3) and order_code='"+orderCode+"'");
				List<Map<String,Object>> list2 =this.getJdbcTemplate().queryForList(sql3.toString());
				for(Map<String,Object> map :list2){
					packageCode =String.valueOf(map.get("package_code"));
				}
				if(packageCode==null || "".equals(packageCode)){
					throw new RuntimeException( "没有找到关联表中包裹单号！");
				}
				//如果目标快递是：芝麻开门 ，银捷速递，合肥汇文 则用MMB订单号
			}else if(targetDeliver==53 || targetDeliver==21 || targetDeliver==46){
				packageCode =orderCode;
			}else{
				isdeliverPackageCode = true;
				//EMS等其他快递公司从deliver_package_code中取包裹单号
				if(orderType == 0){//货到付款
					if(targetDeliver == 44){
						sql3.append("SELECT package_code FROM `deliver_package_code` WHERE deliver="+targetDeliver+" and `used`=0 and deliver_type=0 order by id DESC limit 1");	
					}else if(targetDeliver==9||targetDeliver==25||targetDeliver==28||targetDeliver==29||targetDeliver==31||targetDeliver==32||targetDeliver==34||targetDeliver==35||targetDeliver==37||targetDeliver==43||targetDeliver==51||targetDeliver==52||targetDeliver==54){
						sql3.append("SELECT package_code FROM `deliver_package_code` WHERE deliver="+targetDeliver+" and `used`=0 and deliver_type=1 order by id DESC limit 1");
					}else{
						sql3.append("SELECT package_code FROM `deliver_package_code` WHERE deliver="+targetDeliver+" and `used`=0 and deliver_type=0 order by id DESC limit 1");	
					}
				}else{
					sql3.append("SELECT package_code FROM `deliver_package_code` WHERE deliver="+targetDeliver+" and `used`=0 and deliver_type=0 order by id DESC limit 1");	
				}
				
				List<Map<String,Object>> list2 =this.getJdbcTemplate().queryForList(sql3.toString());
				if(list2.size()==0){
					packageCode=null;
				}else{
					for(Map<String,Object> map2 :list2){
						packageCode =String.valueOf(map2.get("package_code"));
					}
				}
				
				if(packageCode == null || "".equals(packageCode)){ //查不到 用9查
					if(targetDeliver != 44 && targetDeliver !=DeliverCorpInfoBean.DELIVER_ID_YT_WX && targetDeliver !=DeliverCorpInfoBean.DELIVER_ID_YT_CD && targetDeliver!=DeliverCorpInfoBean.DELIVER_ID_YD && targetDeliver!=DeliverCorpInfoBean.DELIVER_ID_JD_CD && targetDeliver!=DeliverCorpInfoBean.DELIVER_ID_JD_WX ){ //如果是44和”圆通快递“则不需要再查
						int targetDeliverId = 9;
						sql5.append("SELECT package_code FROM `deliver_package_code` WHERE deliver="+targetDeliverId+" and `used`=0 and deliver_type=0 order by id DESC limit 1");
						List<Map<String,Object>> list3 =this.getJdbcTemplate().queryForList(sql5.toString());
						if(list3.size()==0){
							packageCode=null;
						}else{
							for(Map<String,Object> map :list3){
								packageCode =String.valueOf(map.get("package_code"));
							}
						}
					}
				}
				
				if(packageCode==null || "".equals(packageCode)){
					throw new RuntimeException( "该快递公司下包裹单号不足！");
				}
			}
			sql2.append("UPDATE audit_package SET deliver = "+targetDeliver+",package_code='"+packageCode+"' WHERE (order_code ='"+orderCode+"')");
		}
		//更新复核列表
		if(this.getJdbcTemplate().update(sql2.toString())<=0){
			throw new RuntimeException( "更新复核列表失败！");
		}
		//如果从deliver_package_code表中取包裹单号，则更新已使用
		if(isdeliverPackageCode){
			sql4.append("update deliver_package_code set used=1 where (package_code='"+packageCode+"')");
			if(this.getJdbcTemplate().update(sql4.toString())<=0){
				throw new RuntimeException( "更新包裹单已使用失败！");
			}
		}
	}
	
	/**
	 * 获取包裹单号
	* @Description: 
	* @author ahc
	 */
	public String getDeliverPackageCode(int Deliver,int deliverType){
		StringBuffer sql = new StringBuffer();
		String packageCode ="";
		if(deliverType == 1){
			sql.append("SELECT package_code FROM `deliver_package_code` WHERE deliver="+Deliver+" and `used`=0 and deliver_type=1 order by id DESC limit 1");
		}
		else{
			sql.append("SELECT package_code FROM `deliver_package_code` WHERE deliver="+Deliver+" and `used`=0 and deliver_type=0 order by id DESC limit 1");
		}
		List<Map<String,Object>> list =this.getJdbcTemplate().queryForList(sql.toString());
		for(Map<String,Object> map :list){
			packageCode =String.valueOf(map.get("package_code"));
		}
		return packageCode;
	}
	
	/**
	 * 更新包裹单号
	* @Description: 
	* @author ahc
	 */
	public int updateDeliverPackageCode(String packageCode){
		StringBuffer sql = new StringBuffer();
		sql.append("update deliver_package_code set used=1 where (package_code='"+packageCode+"')");
		return this.getJdbcTemplate().update(sql.toString());
	}
	
	/**
	 * 添加切换记录
	* @Description: 
	* @author ahc
	 */
	public int addDeliverSwitch(DeliverSwitchBean dsb){
		String sql = "INSERT INTO deliver_switch(stock_area,order_code,origin_deliver_name,"
				+ "deliver_name,origin_package_code,package_code,create_datetime,modify_datetime,remark,oper_user_id,oper_user_name) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
		Object[] args = {dsb.getStockArea(),dsb.getOrderCode(),dsb.getOriginDeliverName(),dsb.getDeliverName(),
				dsb.getOriginPackageCode(),dsb.getPackageCode(),dsb.getCreateDateTime(),dsb.getModifyDatetime(),dsb.getRemark(),dsb.getOperUserId(),dsb.getOperUserName()};
		return this.getJdbcTemplate().update(sql, args);
	}
	
	/**
	 * 获取库地区
	* @Description: 
	* @author ahc
	 */
	public String getStockAreaByName(int area){
		String stockAreaName = "";
		StringBuffer sql = new StringBuffer();
		sql.append("select name from stock_area WHERE id = "+area);
		List<Map<String,Object>> list =this.getJdbcTemplate().queryForList(sql.toString());
		for(Map<String,Object> map :list){
			stockAreaName =String.valueOf(map.get("name"));
		}
		return stockAreaName;
	}
	
	/**
	 * 京东、圆通、等获取包裹单号
	* @Description: 
	* @author ahc
	 */
	public String getDeliverRelation(int deliverId,String orderCode){
		String packageCode = "";
		StringBuffer sql = new StringBuffer();
		sql.append("select package_code from deliver_relation where deliver_id="+deliverId+" and status in(1,3) and order_code='"+orderCode+"'");
		List<Map<String,Object>> list =this.getJdbcTemplate().queryForList(sql.toString());
		for(Map<String,Object> map :list){
			packageCode =String.valueOf(map.get("package_code"));
		}
		return packageCode;
	}
}
