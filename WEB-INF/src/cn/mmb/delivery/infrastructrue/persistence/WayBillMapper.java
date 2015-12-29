package cn.mmb.delivery.infrastructrue.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.stock.stat.DeliverCorpInfoBean;

import org.apache.commons.lang.time.DateUtils;
import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import adultadmin.action.vo.voOrder;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;
import cn.mmb.delivery.domain.model.JdWayBill;
import cn.mmb.delivery.domain.model.WayBill;
import cn.mmb.delivery.domain.model.WayBillTrace;
import cn.mmb.delivery.domain.model.YtWayBill;
import cn.mmb.delivery.domain.model.vo.TraceInfo;

@Repository
public class WayBillMapper extends AbstractDaoSupport {

	/**
	 * 保存关联表明细
	* @Description: 
	* @author ahc
	 */
	public void addDeliverRelationInfo(int deliverId, List<WayBill> list) throws DataAccessException {
		for(WayBill wayBill :list){
			StringBuffer sql = new StringBuffer();
			if(wayBill.getDistricenterCode()!=null){
				sql.append("insert into deliver_relation_info(deliver_id,order_code,type,info,info_type,create_datetime) values (?,?,?,?,?,?)");
				Object[]args ={deliverId,wayBill.getOrderCode(),"1",wayBill.getDistricenterCode(),null,DateUtil.getNow()};
				this.getJdbcTemplate().update(sql.toString(), args);
			}
			if(wayBill.getDistricenterName()!=null){
				StringBuffer sql2 = new StringBuffer();
				sql2.append("insert into deliver_relation_info(deliver_id,order_code,type,info,info_type,create_datetime) values (?,?,?,?,?,?)");
				Object[]args2 ={deliverId,wayBill.getOrderCode(),"2",wayBill.getDistricenterName(),null,DateUtil.getNow()};
				this.getJdbcTemplate().update(sql2.toString(), args2);
			}
			if(wayBill.getBigpenCode()!=null){
				StringBuffer sql3 = new StringBuffer();
				sql3.append("insert into deliver_relation_info(deliver_id,order_code,type,info,info_type,create_datetime) values (?,?,?,?,?,?)");
				Object[]args3 ={deliverId,wayBill.getOrderCode(),"3",wayBill.getBigpenCode(),null,DateUtil.getNow()};
				this.getJdbcTemplate().update(sql3.toString(), args3);
			}
			if(wayBill.getPosition()!=null){
				StringBuffer sql4 = new StringBuffer();
				sql4.append("insert into deliver_relation_info(deliver_id,order_code,type,info,info_type,create_datetime) values (?,?,?,?,?,?)");
				Object[]args4 ={deliverId,wayBill.getOrderCode(),"4",wayBill.getPosition(),null,DateUtil.getNow()};
				this.getJdbcTemplate().update(sql4.toString(), args4);
			}
			if(wayBill.getPositionNo()!=null){
				StringBuffer sql5 = new StringBuffer();
				sql5.append("insert into deliver_relation_info(deliver_id,order_code,type,info,info_type,create_datetime) values (?,?,?,?,?,?)");
				Object[]args5 ={deliverId,wayBill.getOrderCode(),"5",wayBill.getPositionNo(),null,DateUtil.getNow()};
				this.getJdbcTemplate().update(sql5.toString(), args5);
			}
		}
		
	}

	/**
	 * 保存关联表
	* @Description: 
	* @author ahc
	 */
	public void addDeliverRelation(int deliverId, List<WayBill> list) throws DataAccessException {
		for(WayBill wayBill :list){
			StringBuffer sql = new StringBuffer();
			if(wayBill.getMailNo()!=null && !"0".equals(wayBill.getMailNo()) && !"".equals(wayBill.getMailNo())){
				sql.append("insert into deliver_relation (deliver_id,order_code,status,package_code,create_datetime,stock_area) values (?,?,?,?,?,?)");
				Object[]args ={deliverId,wayBill.getOrderCode(),"1",wayBill.getMailNo(),DateUtil.getNow(),wayBill.getStockArea()};
				this.getJdbcTemplate().update(sql.toString(), args);
			}
		}
	}
	
	/**
	 * 更新关联表
	* @Description: 
	* @author ahc
	 */
	public void updateDeliverRelation(int deliverId,List<WayBill> list)
			throws DataAccessException {
		StringBuffer sql =null;
		for(WayBill wayBill :list){
			sql = new StringBuffer();
			if(wayBill.getMailNo()!=null && !"0".equals(wayBill.getMailNo()) && !"".equals(wayBill.getMailNo())){
				//发送取消和下单同步的情况，如果先取消失败，只更新包裹单号
				sql.append("UPDATE `deliver_relation` SET `package_code`='"+wayBill.getMailNo()
						+"' WHERE `order_code`='"+wayBill.getOrderCode()+"' and deliver_id="+deliverId+" and status=4 and package_code is null ");
				this.getJdbcTemplate().update(sql.toString());
				
				sql=new StringBuffer("");
				sql.append("UPDATE `deliver_relation` SET `status`='1',`package_code`='"+wayBill.getMailNo()+"',create_datetime='"+DateUtil.getNow()
						+"'  WHERE `order_code`='"+wayBill.getOrderCode()
						+"' and deliver_id="+deliverId+" and status=0 ");
				this.getJdbcTemplate().update(sql.toString());
			}
		}
	}
	/**
	 * 更新关联表状态
	* @Description: 
	* @author ahc
	 */
	public int updateDeliverRelationForStatus(int deliverId,List<WayBill> list,String status){
		StringBuffer sql =null;
		for(WayBill wayBill :list){
			sql = new StringBuffer();
//			if(wayBill.getOrderCode()!=null && !"0".equals(wayBill.getOrderCode()) && !"".equals(wayBill.getOrderCode())){
				sql.append("UPDATE  deliver_relation  SET  status ='"+status+"' WHERE  id ="+wayBill.getId());
				return this.getJdbcTemplate().update(sql.toString());
//			}
		}
		return 0;
	}
	
	/**
	 * 
	 * @descripion  更新关联表状态为取消接口调用失败
	 * @author 刘仁华
	 * @time  2015年9月9日
	 */
	public int updateDeliverRelationForFailedStatus(int deliverId,List<WayBill> list){
		StringBuffer sql =null;
		for(WayBill wayBill :list){
			sql = new StringBuffer();
			if(wayBill.getMailNo()!=null && !"0".equals(wayBill.getMailNo()) && !"".equals(wayBill.getMailNo())){
				sql.append("UPDATE  deliver_relation  SET  status ='3' WHERE ( package_code ='"+wayBill.getMailNo()+"')");
				return this.getJdbcTemplate().update(sql.toString());
			}
		}
		return 0;
	}
	
	
	/**
	 * 获取关联表
	* @Description: 
	* @author ahc
	 */
	public List<Map<String, Object>> getDeliverRelationList(String condition)
			throws DataAccessException {
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
	 * 根据订单号获取省、市、区、购买方式、电话、用户名等
	* @Description: 
	* @author ahc
	 */
	public Map<String,Object> getUserOrderInfo(String condition) throws Exception {
		String sql="SELECT uo.buy_mode,uo.`name`,uo.postcode,uo.phone,p.`name` as prov,"
				+ "pc.city,ca.area,uo.address,uo.dprice,os.stock_area,sa.name as stock_name from user_order uo "
				+ "left JOIN user_order_extend_info uoei on uo.`code` = uoei.order_code "
				+ "left join provinces p on p.Id = uoei.add_id1 "
				+ "left JOIN province_city pc ON pc.id= uoei.add_id2 "
				+ "left JOIN city_area ca ON ca.id = uoei.add_id3 "
				+ "left join order_stock os ON os.order_code = uo.code "
				+ "left join stock_area sa on os.stock_area = sa.id "
				+ "where "+condition+" and os.status<>3";
		List<Map<String,Object>> result =this.getJdbcTemplate().queryForList(sql);
		Map<String,Object> map = new HashMap<String,Object>();
		for(Map<String,Object> map2 : result){
			map = map2;
		}
		return map;
	}
	
	/**
	 * 需要向京东快递发送的面单数据(无运单号)
	* @Description: 
	* @author ahc
	 */
	public Map<String,Object> getUserOrderInfoToJD(String condition) throws Exception {
		String sql="SELECT sa.id as stock_area,uo.`code`,uo.`name`,uo.address,uo.phone as mobile,uo.buy_mode as orderType," +
				"uo.dprice,dws.sender_name sender,dws.sender_mobile senderMobile,dws.sender_address senderAddress" +
				" from user_order uo " +
				"left join order_stock os ON os.order_code = uo.code " +
				"left join stock_area sa on os.stock_area = sa.id " +
				"left join deliver_waybill_sender dws on dws.stock_area=sa.id " +
				"where "+condition+" and os.status<>3";
		List<Map<String,Object>> result =this.getJdbcTemplate().queryForList(sql);
		Map<String,Object> map = new HashMap<String,Object>();
		for(Map<String,Object> map2 : result){
			map = map2;
		}
		return map;
	}

	/** 
	 * @Description: 更新物流状态
	 * @return void 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月7日 下午4:07:21 
	 */
	public int updateDeliverOrder(String sql) {
		return this.getJdbcTemplate().update(sql);
	}

	/** 
	 * @Description: 保存物流信息
	 * @return void 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月7日 下午4:17:31 
	 */
	public void insertDeliverOrderInfo(WayBillTrace deliver) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO `deliver_order_info` (`deliver_id`, `deliver_info`, `deliver_time`, `deliver_state`, `add_time`, `source`)");
		sql.append(" VALUES(?,?,?,?,?,?)");
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		long nowTime = System.currentTimeMillis();
		for (TraceInfo info : deliver.getTraceInfo()) {
			long time = DateUtil.parseDate(info.getTime(), DateUtil.normalTimeFormat).getTime();
			Object[] args = {deliver.getDeliverOrderId(), info.getInfo(), time, info.getStatus(), nowTime, 0};
			batchArgs.add(args);
		}
		this.getJdbcTemplate().batchUpdate(sql.toString(), batchArgs);
	}

	/** 
	 * @Description: 获取圆通需要更新物流信息的单号
	 * @return List<WayBillTraceParam> 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月10日 下午3:21:14 
	 */
	public List<WayBill> getYtoWayBillCode() {
		Date date = DateUtils.addDays(new Date(), -60);
		String sql = "SELECT dr.package_code mailNo FROM deliver_relation dr JOIN deliver_order dor ON dr.package_code=dor.deliver_no WHERE dor.deliver_state<7 AND dr.deliver_id in("+DeliverCorpInfoBean.DELIVER_ID_YT_WX+","+DeliverCorpInfoBean.DELIVER_ID_YT_CD+") AND dr.create_datetime>='"+DateUtil.formatDate(date)+"'";
		RowMapper<YtWayBill> rm = new BeanPropertyRowMapper<YtWayBill>(YtWayBill.class);
		List<YtWayBill> rs = this.getJdbcTemplate(DbOperation.DB_SLAVE2).query(sql, rm);
		List<WayBill> rsd = new ArrayList<WayBill>();
		rsd.addAll(rs);
		return rsd;
	}
	
	/**
	 * 获取需要向京东快递发送的面单数据
	 * @param deliverId 快递公司id
	 * @return
	 * @throws Exception
	 * @author likaige
	 * @create 2015年8月10日 下午1:30:48
	 */
	public List<JdWayBill> getNeedSendWaybillToJD(int deliverId) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.id,t.stock_area,t.package_code mailNo,t.order_code,uo.name,uo.address address,");
		sql.append(" uo.phone mobile,uo.buy_mode orderType,uo.dprice,");
		sql.append(" dws.sender_name sender,dws.sender_mobile senderMobile,dws.sender_address senderAddress");
		sql.append(" from deliver_relation t");
		sql.append(" JOIN user_order uo on t.order_code=uo.code");
		sql.append(" JOIN deliver_waybill_sender dws on dws.stock_area=t.stock_area");
		sql.append(" WHERE t.status=0");
		sql.append(" and t.deliver_id=").append(deliverId);
		return this.getJdbcTemplate().query(sql.toString(), new BeanPropertyRowMapper<JdWayBill>(JdWayBill.class));
	}

	public List<WayBillTrace> getWayBillTrace(List<WayBill> wayBill) {
		return this.getSession().selectList(wayBill);
	}
	
}
