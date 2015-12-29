package mmb.delivery.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.delivery.domain.DeliverInfo;
import mmb.delivery.domain.DeliverPackageCode;
import mmb.delivery.domain.PopBussiness;
import mmb.delivery.domain.PopOrderInfo;
import mmb.delivery.domain.Waybill;
import mmb.hessian.ware.DeliverOrderInfoBean;
import mmb.tms.model.Provinces;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import adultadmin.action.vo.voOrder;
import adultadmin.bean.stock.MailingBatchBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;

@Repository
public class DeliveryDao extends AbstractDaoSupport {
	
	/**
	 * 获取快递公司的未使用的运单号数量
	 * @param deliveryId 快递公司ID
	 * @return
	 * @throws Exception
	 * @author likaige
	 * @create 2015年4月28日 下午3:43:26
	 */
	public int getUnusedDeliveryCodeCount(int deliveryId) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*)");
		sql.append(" FROM deliver_package_code t");
		sql.append(" WHERE t.deliver=? and t.used=").append(DeliverPackageCode.USED_NO);
		return this.getJdbcTemplate().queryForInt(sql.toString(), deliveryId);
	}

	/**
	 * 保存运单号
	 * @param deliverPackageCodeList 运单号列表
	 * @throws Exception
	 * @author likaige
	 * @create 2015年4月29日 上午10:49:30
	 */
	public void saveDeliverPackageCode(List<DeliverPackageCode> deliverPackageCodeList) throws Exception {
		String sql = "INSERT INTO deliver_package_code(deliver,package_code,used) VALUES(?,?,?)";
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		for(DeliverPackageCode code : deliverPackageCodeList){
			Object[] args = {code.getDeliver(), code.getPackageCode(), DeliverPackageCode.USED_NO};
			batchArgs.add(args);
		}
		this.getJdbcTemplate().batchUpdate(sql, batchArgs);
	}

	/**
	 * 获取需要发送的面单数据
	 * @param deliveryIds 快递公司id
	 * @return
	 * @throws Exception
	 * @author likaige
	 * @create 2015年5月5日 上午10:05:37
	 */
	public List<Waybill> getNeedSendWaybill(Integer... deliveryIds) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT mbp.id");
		sql.append(" FROM mailing_batch mb");
		sql.append(" JOIN mailing_batch_package mbp on mb.id=mbp.mailing_batch_id");
		sql.append(" LEFT JOIN mailing_batch_package_waybill t on t.mailing_batch_package_id=mbp.id");
		sql.append(" WHERE mb.status=").append(MailingBatchBean.STATUS0);
		sql.append(" and t.id is null");
		sql.append(" and mb.deliver in(").append(StringUtils.join(deliveryIds, ",")).append(")");
		List<Integer> idList = this.getJdbcTemplate().queryForList(sql.toString(), Integer.class);
		if(idList.isEmpty()){
			return Collections.emptyList();
		}
		
		sql = new StringBuilder();
		sql.append(" SELECT mbp.id,mb.area stock_area,mbp.package_code deliveryId,mbp.order_code orderId,");
		sql.append(" uo.id userOrderId,uo.name receiveName,uo.address receiveAddress,uo.phone receiveMobile,");
		sql.append(" mbp.weight,if(uo.buy_mode=0,1,0) collectionValue,uo.dprice collectionMoney,");
		sql.append(" dws.sender_name senderName,dws.sender_mobile senderMobile,dws.sender_address senderAddress");
		sql.append(" FROM mailing_batch_package mbp");
		sql.append(" JOIN user_order uo on mbp.order_id=uo.id");
		sql.append(" JOIN mailing_batch mb on mb.id=mbp.mailing_batch_id");
		sql.append(" LEFT JOIN deliver_waybill_sender dws on dws.stock_area=mb.area");
		sql.append(" WHERE mbp.id in(").append(StringUtils.join(idList, ",")).append(")");
		return this.getJdbcTemplate().query(sql.toString(), new BeanPropertyRowMapper<Waybill>(Waybill.class));
	}
	
	/**
	 * 获取需要发送的POP面单数据
	 * @return
	 * @throws Exception
	 * @author likaige
	 * @create 2015年5月5日 上午10:05:37
	 */
	public List<Waybill> getNeedSendPopWaybill() throws Exception {
		StringBuilder sql = new StringBuilder();
		StringBuilder sqla = new StringBuilder();
		StringBuilder sqlb = new StringBuilder();
		sqla.append("SELECT poi.pop_order_code orderId,uo.name receiveName,uo.address receiveAddress,uo.phone receiveMobile,");
		sqla.append(" if(uo.buy_mode=0,1,0) collectionValue,");
		sqla.append(" dws.sender_name senderName,dws.sender_mobile senderMobile,dws.sender_address senderAddress,");
		sqla.append(" IFNULL(uo.id,0) userOrderId,uo.code userOrderCode,IFNULL(a.agent_id,0) agent_id,");
		sqla.append(" poi.send_status sendStatus,IFNULL(b.id,-1) buyId,poi.id poiId");
		sqla.append(" FROM pop_order_info poi");
		sqla.append(" JOIN buy_order_popec b on poi.pop_order_code=b.code");
		sql.append(sqla);
		
		sql.append(" JOIN user_order_extend_popec uoe on b.code=uoe.popec_order_code");
		//sql.append(" JOIN user_order_extend_popec uoe on b.code=uoe.popec_order_code");
		
		sqlb.append(" JOIN user_order uo on uoe.id=uo.id");
		sqlb.append(" LEFT JOIN after_sale_agent_order a on a.pop_order_code=poi.pop_order_code");
		sqlb.append(" LEFT JOIN deliver_waybill_sender dws on dws.stock_area=").append(ProductStockBean.AREA_JD);
		
		sql.append(sqlb);
		sql.append(" WHERE poi.send_status in (0,-2)  and b.is_parent=0");
		List<Waybill> result = this.getJdbcTemplate().query(sql.toString(), new BeanPropertyRowMapper<Waybill>(Waybill.class));
		
		sql.delete(0, sql.length());
		sql.append(sqla);
		sql.append(" JOIN user_order_extend_popec uoe on b.parent_code=uoe.popec_order_code");
		sql.append(sqlb);
		sql.append(" WHERE poi.send_status in (0,-2)  and b.is_parent=2");
		List<Waybill> result1 = this.getJdbcTemplate().query(sql.toString(), new BeanPropertyRowMapper<Waybill>(Waybill.class));
		
		if(result1 != null && !result1.isEmpty()){
			result.addAll(result1);
		}
		return result;
	}
	
	/**
	 * 获取需要发送的S订单的POP面单数据
	 * @return
	 * @throws Exception
	 * @author likaige
	 * @create 2015年6月12日 下午2:27:00
	 */
	public List<Waybill> getNeedSendPopWaybill_S() throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT t.pop_order_code orderId,uo.name receiveName,uo.address receiveAddress,uo.phone receiveMobile,");
		sql.append(" 0 collectionValue,dws.sender_name senderName,dws.sender_mobile senderMobile,dws.sender_address,");
		sql.append(" uo.id userOrderId,uo.code userOrderCode,a.agent_id");
		sql.append(" FROM pop_order_info t");
		sql.append(" JOIN after_sale_agent_order a on t.pop_order_code=a.pop_order_code");
		sql.append(" JOIN user_order uo on a.order_s_id=uo.id");
		sql.append(" LEFT JOIN deliver_waybill_sender dws on dws.stock_area=").append(ProductStockBean.AREA_JD);
		sql.append(" WHERE t.send_status=0");
		return this.getJdbcTemplate().query(sql.toString(), new BeanPropertyRowMapper<Waybill>(Waybill.class));
	}
	
	//获取代收货款
	public Map<String, Double> getPopWaybillCollectionMoney(List<String> popOrderCodeList) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT b.code orderId,TRUNCATE(SUM(bp.count*s.dprice),1) collectionMoney");
		sql.append(" FROM buy_order_popec b");
		sql.append(" JOIN buy_order_product_popec bp on b.id=bp.buy_order_popec_id");
		sql.append(" JOIN user_order_extend_popec uoe ");
		sql.append(" on ((b.is_parent=0 and b.code=uoe.popec_order_code) or (b.is_parent=2 and b.parent_code=uoe.popec_order_code))");
		sql.append(" JOIN user_order_product_split_history s on uoe.id=s.order_id and bp.my_product_id=s.product_id");
		sql.append(" WHERE b.code in('").append(StringUtils.join(popOrderCodeList, "','")).append("')");
		sql.append(" GROUP BY b.code");
		List<Waybill> list = this.getJdbcTemplate().query(sql.toString(), new BeanPropertyRowMapper<Waybill>(Waybill.class));
		Map<String, Double> resultMap = new HashMap<String, Double>();
		for(Waybill waybill : list){
			resultMap.put(waybill.getOrderId(), waybill.getCollectionMoney());
		}
		return resultMap;
	}

	/**
	 * 保存mailing_batch_package_waybill
	 * @param waybillList 面单数据
	 * @author likaige
	 * @create 2015年5月4日 下午1:24:41
	 */
	public void saveMailingBatchPackageWaybill(List<Waybill> waybillList, int sendStatus) throws Exception {
		String sql = "INSERT INTO mailing_batch_package_waybill(mailing_batch_package_id,send_status) VALUES(?,?)";
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		for(Waybill waybill : waybillList){
			Object[] args = {waybill.getId(), sendStatus};
			batchArgs.add(args);
		}
		this.getJdbcTemplate().batchUpdate(sql, batchArgs);
	}

	/**
	 * 更改发送状态，以便下次扫描时再次发送
	 * @param deliverCodeList 运单编号列表
	 * @throws Exception
	 * @author likaige
	 * @create 2015年5月6日 下午5:50:35
	 */
	public int revertDeliverRelation(List<String> deliverCodeList) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("update deliver_relation set status=0");
		sql.append(" WHERE status=1 and package_code in('").append(StringUtils.join(deliverCodeList, "','")).append("')");
		return this.getJdbcTemplate().update(sql.toString());
	}
	
	/**
	 * POP配送状态查询
	 * @param param前台传的参数
	 * @return配送信息列表
	 * @author yaoliang 
	 * @create 2015年5月8日 上午11:06:50
	 */
	public List<DeliverInfo> getPOPDeliverInfoList(Map<String,Object> paramMap) throws Exception {
		StringBuffer sql = new StringBuffer();
		StringBuffer condition = new StringBuffer();
		
		String orderCodes = (String)paramMap.get("orderCodes");
		String startTime = (String)paramMap.get("startTime");
		String endTime = (String)paramMap.get("endTime");
		int scanType = (Integer)paramMap.get("scanType");
		int popId = (Integer)paramMap.get("popId");
		int storageId = (Integer)paramMap.get("storageId");
		int deliveryId = (Integer)paramMap.get("deliveryId");
		
		if (!orderCodes.trim().equals("")) {
			String[] orderCode = orderCodes.split("\r\n");
			condition.append("(");
			if (scanType == DeliverInfo.SCAN_TYPE1) {
	        	for(int i = 0 ; i < orderCode.length ;i++ ){
	        		if(orderCode[i].trim().length()>0){
	        			condition.append(" dip.deliver_code='");
	        			condition.append(orderCode[i].trim().replaceAll("\n", ""));
	        			condition.append("' or ");
	        		}
	        	}
			} else if (scanType == DeliverInfo.SCAN_TYPE2) {
        		for(int i = 0 ; i < orderCode.length ;i++ ){
	        		if(orderCode[i].trim().length()>0){
	        			condition.append(" dip.order_code='");
	        			condition.append(orderCode[i].trim().replaceAll("\n", ""));
	        			condition.append("' or ");
	        		}
	        	}
        	} else if (scanType == DeliverInfo.SCAN_TYPE3) {
        		for(int i = 0 ; i < orderCode.length ;i++ ){
	        		if(orderCode[i].trim().length()>0){
	        			condition.append(" dip.pop_order_code='");
	        			condition.append(orderCode[i].trim().replaceAll("\n", ""));
	        			condition.append("' or ");
	        		}
	        	}
        	}
        	if( condition.length() > 0 ){
        		condition.replace(condition.length()-3, condition.length(), "");
        	}
        	condition.append(") ");
		}
		if (!"".equals(StringUtil.checkNull(startTime)) && !"".equals(StringUtil.checkNull(endTime))) {
			if (condition.length() > 0) {
				condition.append( " and ");
			}
			condition.append(" dip.time >='").append(startTime).append(" 00:00:00'")
			.append(" and dip.time <='").append(endTime).append(" 23:59:59'")
			.append(" and dip.deliver_state = "+DeliverOrderInfoBean.DELIVER_STATE0);
			
		}
		if(popId != -1) {
			condition.append(" and dip.pop_type=").append(popId);
		}
		if(deliveryId != -1) {
			condition.append(" and dip.deliver_type = ").append(deliveryId);
		}
		if (storageId != -1) {
			condition.append(" and dip.storage_id = ").append(storageId);
		}
		
		sql.append("SELECT dip.id,dip.order_id,dip.pop_type popType,dip.storage_id storageId,dip.deliver_type deliverType")
		.append(" ,dip.province,dip.city,dip.district,dip.order_code orderCode,dip.pop_order_code popOrderCode,dip.deliver_code deliverCode")
		.append(" FROM deliver_info_pop dip");
		if(!condition.equals("")){
			sql.append(" WHERE ").append(condition); 
		}
		sql.append(" ORDER BY dip.id DESC,dip.time DESC");
		 
		List<DeliverInfo> deliverInfoList = this.getJdbcTemplate(DynamicDataSource.SLAVE).query(sql.toString(), new BeanPropertyRowMapper<DeliverInfo>(DeliverInfo.class));
		return deliverInfoList;
	}
	
	/** 
	 * @description 删除配送记录
	 * @param deliverInfo
	 * @create 2015-5-8 上午10:48:52
	 * @author gel
	 */
	public void deleteDeliverInfo(String deliverCode, int status) {
		String sql = "delete from deliver_info_pop where deliver_code=? and deliver_state=?";
		this.getJdbcTemplate().update(sql, deliverCode, status);
	}
	
	/** 
	 * @description 配送信息保存
	 * @param deliverInfo
	 * @throws Exception
	 * @create 2015-5-7 下午01:49:58
	 * @author gel
	 * @param deliverInfoList 
	 */
	public void saveDeliverInfo(List<DeliverInfo> deliverInfoList, DeliverInfo firstDeliverInfo) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO deliver_info_pop(order_code,order_id,pop_order_code,pop_order_id,");
		sql.append("deliver_code,deliver_info,deliver_state,time,storage_id,province,city,district,type,pop_type)");
		sql.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		String orderCode = firstDeliverInfo.getOrderCode();
		int orderId = firstDeliverInfo.getOrderId();
		String popOrderCode = firstDeliverInfo.getPopOrderCode();
		int popOrderId = firstDeliverInfo.getPopOrderId();
		String deliverCode = firstDeliverInfo.getDeliverCode();
		int type = firstDeliverInfo.getType();
		int popType = firstDeliverInfo.getPopType();
		int storageId = firstDeliverInfo.getStorageId();
		String province = firstDeliverInfo.getProvince();
		String city = firstDeliverInfo.getCity();
		String district = firstDeliverInfo.getDistrict();
		for (DeliverInfo d : deliverInfoList) {
			Object[] args = {orderCode, orderId, popOrderCode, popOrderId, deliverCode, d.getDeliverInfo(), 
					d.getDeliverState(), d.getTime(), storageId, province, city, district, type, popType};
			batchArgs.add(args);
		}
		
		this.getJdbcTemplate().batchUpdate(sql.toString(), batchArgs);
	}
	
	/**
	 * 根据运单号，获取第一条的POP配送信息
	 * @param deliverCode 运单号
	 * @return
	 * @throws Exception
	 * @author likaige
	 * @create 2015年5月14日 下午4:38:01
	 */
	public DeliverInfo getFirstDeliverInfoPOP(String deliverCode) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT *");
		sql.append(" FROM deliver_info_pop t");
		sql.append(" WHERE t.deliver_code=?");
		sql.append(" LIMIT 1");
		RowMapper<DeliverInfo> rm = new BeanPropertyRowMapper<DeliverInfo>(DeliverInfo.class);
		return this.getJdbcTemplate().queryForObject(sql.toString(), rm, deliverCode);
	}
	
	/**
	 * 根据运单号，获取所有的POP配送信息
	 * @param deliverCode 运单号
	 * @return
	 * @throws Exception
	 * @author likaige
	 * @create 2015年7月29日 下午1:40:03
	 */
	public List<DeliverInfo> getDeliverInfoPOPList(String deliverCode) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT *");
		sql.append(" FROM deliver_info_pop t");
		sql.append(" WHERE t.deliver_code=?");
		Object[] args = {deliverCode};
		RowMapper<DeliverInfo> rm = new BeanPropertyRowMapper<DeliverInfo>(DeliverInfo.class);
		return this.getJdbcTemplate().query(sql.toString(), args, rm);
	}

	/**
	 * 根据POP订单号获取订单信息
	 * @param popOrderCode POP订单号
	 * @return
	 * @throws Exception
	 * @author likaige
	 * @create 2015年5月19日 上午9:47:28
	 */
	public Map<String, Object> getUserOrderByPopOrderCode(String popOrderCode) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.id,t.order_code,uo.create_datetime,p.name province,pc.city,ca.area district");
		sql.append(" FROM user_order_extend_popec t");
		sql.append(" JOIN user_order uo on uo.id=t.id");
		sql.append(" LEFT JOIN provinces p on t.add_p_id=p.id");
		sql.append(" LEFT JOIN province_city pc on t.add_c_id=pc.id");
		sql.append(" LEFT JOIN city_area ca on t.add_a_id=ca.id");
		sql.append(" WHERE t.popec_order_code=?");
		return this.getJdbcTemplate().queryForMap(sql.toString(), popOrderCode);
	}
	
	/**
	 * 获取订单地址信息
	 * @param userOrderId MMB订单id
	 * @return
	 * @throws Exception
	 * @author likaige
	 * @create 2015年6月2日 上午10:26:40
	 */
	public Map<String, Object> getUserOrderAddress(int userOrderId) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.id,p.name province,pc.city,ca.area district");
		sql.append(" FROM user_order_extend_popec t");
		sql.append(" LEFT JOIN provinces p on t.add_p_id=p.id");
		sql.append(" LEFT JOIN province_city pc on t.add_c_id=pc.id");
		sql.append(" LEFT JOIN city_area ca on t.add_a_id=ca.id");
		sql.append(" WHERE t.id=?");
		return this.getJdbcTemplate().queryForMap(sql.toString(), userOrderId);
	}

	/**
	 * 保存配送信息
	 * @param deliverInfoList
	 * @throws Exception
	 * @author likaige
	 * @create 2015年5月19日 下午2:01:06
	 */
	public void savePopDeliverInfo(List<DeliverInfo> deliverInfoList) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO deliver_info_pop(order_code,order_id,pop_order_code,pop_order_id,");
		sql.append("deliver_code,deliver_info,deliver_state,time,storage_id,province,city,district,type,pop_type)");
		sql.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		for (DeliverInfo deliver : deliverInfoList) {
			Object[] args = {deliver.getOrderCode(),deliver.getOrderId(),deliver.getPopOrderCode(),deliver.getPopOrderId(),
					deliver.getDeliverCode(),deliver.getDeliverInfo(),deliver.getDeliverState(),deliver.getTime(),deliver.getStorageId(),
					deliver.getProvince(),deliver.getCity(),deliver.getDistrict(),deliver.getType(),deliver.getPopType()};
			batchArgs.add(args);
		}
		this.getJdbcTemplate().batchUpdate(sql.toString(), batchArgs);
	}

	/**
	 * 初始化大客户运单数据
	 * @param popOrderCode POP订单号
	 * @author likaige
	 * @create 2015年5月21日 下午6:06:17
	 */
	public void initPopOrderInfo(String popOrderCode) throws Exception {
		String sql = "INSERT INTO pop_order_info(pop_order_code,send_status,order_create_time) VALUES(?,?,?)";
		Object[] args = {popOrderCode, PopOrderInfo.SEND_STATUS0, DateUtil.getNow()};
		this.getJdbcTemplate().update(sql, args);
	}

	/**
	 * 获取指定数量的未使用的运单号
	 * @param deliverId 快递公司id
	 * @param size 数量
	 * @return
	 * @throws Exception
	 * @author likaige
	 * @create 2015年5月26日 下午3:24:31
	 */
	public List<DeliverPackageCode> getUnusedDeliverPackageCodeList(int deliverId, int size) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT *");
		sql.append(" FROM deliver_package_code t");
		sql.append(" WHERE t.deliver=? and t.used=").append(DeliverPackageCode.USED_NO);
		sql.append(" ORDER BY t.id asc");
		sql.append(" limit ?");
		Object[] args = {deliverId, size};
		RowMapper<DeliverPackageCode> rm = new BeanPropertyRowMapper<DeliverPackageCode>(DeliverPackageCode.class);
		return this.getJdbcTemplate().query(sql.toString(), args, rm);
	}

	public void updatePopOrderInfo(List<Waybill> waybillList, int sendStatus) throws Exception {
		String sql = "update pop_order_info set deliver_code=?,status=?,outstock_time=?,send_status=?"
				+ " where pop_order_code=?";
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		int status = DeliverOrderInfoBean.DELIVER_STATE0;
		String nowTime = DateUtil.getNow();
		for(Waybill waybill : waybillList){
			Object[] args = {waybill.getDeliveryId(),status,nowTime,sendStatus,waybill.getOrderId()};
			batchArgs.add(args);
		}
		this.getJdbcTemplate().batchUpdate(sql, batchArgs);
	}

	//把运单号标记成已使用状态
	public void updateDeliverPackageCodeUsed(List<DeliverPackageCode> deliverCodeList) throws Exception {
		String sql = "update deliver_package_code set used=1 where id=?";
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		for(DeliverPackageCode deliverPackageCode : deliverCodeList){
			Object[] args = {deliverPackageCode.getId()};
			batchArgs.add(args);
		}
		this.getJdbcTemplate().batchUpdate(sql, batchArgs);
	}

	//还原POP面单的发送状态
	public void revertPopOrderInfo(List<String> deliverCodeList) throws Exception {
		String sql = "update pop_order_info set send_status=?,order_code=null where deliver_code=?";
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		for(String code : deliverCodeList){
			Object[] args = {PopOrderInfo.SEND_STATUS0, code};
			batchArgs.add(args);
		}
		this.getJdbcTemplate().batchUpdate(sql, batchArgs);
	}
	
	public void updatePopOrderInfoSendStatusByPopOrderCode(String popOrderCode, int sendStatus) throws Exception {
		String sql = "update pop_order_info set send_status=? where pop_order_code=?";
		this.getJdbcTemplate().update(sql, sendStatus, popOrderCode);
	}
	
	public int updateDeliverRelationStatusByOrderCode(String orderCode, int status) throws Exception {
		String sql = "update deliver_relation set status=? where status=1 and order_code=?";
		return this.getJdbcTemplate().update(sql, status, orderCode);
	}
	
	//修改短信发送状态
	public void updatePopOrderInfoMessageSendStatus(List<String> deliverCodeList, int sendStatus) throws Exception {
		String sql = "update pop_order_info set message_send_status=? where deliver_code=?";
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		for(String code : deliverCodeList){
			Object[] args = {sendStatus, code};
			batchArgs.add(args);
		}
		this.getJdbcTemplate().batchUpdate(sql, batchArgs);
	}

	/**
	 * 获取交付京东时的发货时间
	 * @param param前台传的参数
	 * @return配送信息列表
	 * @author yaoliang 
	 * @return 
	 * @create 2015年5月14日 上午11:06:50
	 */
	public List<Map<String, Object>> getPOPDeliverTime(String popOrderCodes) throws Exception {
		String sql = "select pop_order_code,time from deliver_info_pop where pop_order_code in ('"+popOrderCodes+"') and deliver_state = "+DeliverOrderInfoBean.DELIVER_STATE1;
		return this.getJdbcTemplate(DynamicDataSource.SLAVE).queryForList(sql.toString());
	}

	/**
	 * 查询运单信息
	 * @param param前台传的参数
	 * @return配送信息列表
	 * @author yaoliang 
	 * @return 
	 * @create 2015年5月14日 上午11:06:50
	 */
	public List<DeliverInfo> getPOPDeliverInfo(Map<String,Object> paramMap) throws Exception {
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT dip.id, dip.pop_type popType, dip.deliver_type deliverType")
				.append(", dip.order_code orderCode, dip.pop_order_code popOrderCode, dip.deliver_code deliverCode")
				.append(", dip.deliver_state deliverState, DATE_FORMAT(dip.time,'%Y-%m-%d %T') time, dip.deliver_info deliverInfo")
				.append(",IFNULL(TIMESTAMPDIFF(HOUR,(select dip1.time from deliver_info_pop dip1 where dip1.pop_order_code=dip.pop_order_code and dip1.deliver_state=1  limit 1)")
				.append(" ,(select MAX(dip1.time) from deliver_info_pop dip1 where dip1.pop_order_code = dip.pop_order_code  limit 1)),0) usedTime")
				.append(" FROM deliver_info_pop dip  WHERE 1=1 ");
		if(!paramMap.get("deliverCode").equals("")){
			sql.append(" and dip.deliver_code='").append(paramMap.get("deliverCode")).append("' ");
		}
		if(!paramMap.get("orderCode").equals("")){
			sql.append(" and dip.order_code='").append(paramMap.get("orderCode")).append("' ");
		}
		if(!paramMap.get("popOrderCode").equals("")){
			sql.append(" and dip.pop_order_code='").append(paramMap.get("popOrderCode")).append("' ");
		}
		sql.append(" ORDER BY dip.id DESC");
		List<DeliverInfo> deliverInfoList = this.getJdbcTemplate(DynamicDataSource.SLAVE).query(sql.toString(), new BeanPropertyRowMapper<DeliverInfo>(DeliverInfo.class));
		return deliverInfoList;
	}
	
	/**
	 * 获取订单号或包裹单号
	 * @author yaoliang 
	 * @create 2015年5月16日  上午8:40:22
	 * @param code 单号
	 * @param scanType 区分订单号和包裹单号
	 */
	public String getPOPDeliverOrderCode(String code, int scanType) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("select GROUP_CONCAT(DISTINCT ");
		if(scanType == DeliverInfo.SCAN_TYPE1){
			sql.append(" deliver_code)");
		}else if(scanType == DeliverInfo.SCAN_TYPE2){
			sql.append(" order_code)");
		}else if(scanType == DeliverInfo.SCAN_TYPE3){
			sql.append(" pop_order_code)");
		}
		sql.append(" FROM deliver_info_pop where ");
		if(scanType == DeliverInfo.SCAN_TYPE1){
			sql.append(" deliver_code ");
		}else if(scanType == DeliverInfo.SCAN_TYPE2){
			sql.append(" order_code ");
		}else if(scanType == DeliverInfo.SCAN_TYPE3){
			sql.append(" pop_order_code ");
		}
		sql.append(" in('").append(code).append("')");
		
		return this.getJdbcTemplate(DynamicDataSource.SLAVE).queryForObject(sql.toString(), String.class);
	}

	/**
	 * 根据POP订单号获取MMB订单号
	 * @param popOrderCode POP订单号
	 * @return
	 * @throws Exception
	 * @author likaige
	 * @create 2015年5月19日 上午9:24:15
	 */
	public String getOrderCodeByPopOrderCode(String popOrderCode) throws Exception {
		String sql = "SELECT order_code FROM user_order_extend_popec t WHERE t.popec_order_code=?";
		return this.getJdbcTemplate().queryForObject(sql, String.class, popOrderCode);
	}
	
	public int getOrderIdByDeliverCode(String deliverCode) throws Exception {
		String sql = "SELECT t.order_id from deliver_info_pop t WHERE t.deliver_code=? LIMIT 1";
		return this.getJdbcTemplate().queryForInt(sql, deliverCode);
	}
	
	public int getOrderIdByOrderCode(String orderCode) throws Exception {
		String sql = "SELECT t.id from user_order t WHERE t.code=?";
		return this.getJdbcTemplate().queryForInt(sql, orderCode);
	}

	public DeliverOrderInfoBean getDeliverOrderByDeliverCode(String deliverCode) throws Exception {
		String sql = "SELECT * FROM deliver_order WHERE deliver_no=?";
		RowMapper<DeliverOrderInfoBean> rm = new BeanPropertyRowMapper<DeliverOrderInfoBean>(DeliverOrderInfoBean.class);
		return this.getJdbcTemplate().queryForObject(sql.toString(), rm, deliverCode);
	}

	public void deleteDeliverOrderInfo(int deliverOrderId, int status) throws Exception {
		String sql = "delete from deliver_order_info where deliver_id=? and deliver_state=?";
		this.getJdbcTemplate().update(sql, deliverOrderId, status);
	}

	public void saveDeliverOrderInfo(List<DeliverInfo> deliverInfoList, int deliverOrderId) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO deliver_order_info(deliver_id,deliver_info,deliver_time,deliver_state,add_time,source)");
		sql.append(" VALUES(?,?,?,?,?,?)");
		
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		long nowTime = System.currentTimeMillis();
		for (DeliverInfo d : deliverInfoList) {
			long time = DateUtil.parseDate(d.getTime(), DateUtil.normalTimeFormat).getTime();
			Object[] args = {deliverOrderId, d.getDeliverInfo(), time, d.getDeliverState(), nowTime, 0};
			batchArgs.add(args);
		}
		
		this.getJdbcTemplate().batchUpdate(sql.toString(), batchArgs);
	}
	
	//根据运单号删除POP配送信息
	public void deletePopDeliverInfoByDeliverCode(List<String> deliverCodeList) throws Exception {
		String sql = "delete from deliver_info_pop where deliver_code=?";
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		for(String code : deliverCodeList){
			Object[] args = {code};
			batchArgs.add(args);
		}
		this.getJdbcTemplate().batchUpdate(sql, batchArgs);
	}
	
	/**
	 * 通过订单编号获取订单地址
	 * @param code
	 * @return
	 * @create yaoliang
	 * @time 2105-05-23
	 */
	public List<Map<String, Object>> getAddressByCode(String code){
		String sql = "select code orderCode,address from user_order where code in ('"+code+"') group by code" ;
		return this.getJdbcTemplate(DynamicDataSource.SLAVE).queryForList(sql);
	}
	
	/**
	 * 获取时效
	 * @param code
	 * @return
	 * @create yaoliang
	 * @time 2105-05-23
	 */
	public List<Map<String, Object>> getAgingByCode(String code,int popId){
		StringBuffer sql = new StringBuffer();
		sql.append("select uoep.popec_order_code popOrderCode,edt.city_area_time,edt.town_time,edt.village_time from") 
		.append(" effect_deliver_time edt join user_order_extend_popec uoep on uoep.add_p_id = edt.province_id")
		.append(" and uoep.add_c_id = edt.province_city_id and uoep.add_a_id = edt.city_area_id")    
		.append(" where edt.pop_id=").append(popId).append(" and uoep.popec_order_code in ('"+code+"') GROUP BY uoep.popec_order_code");
		
		return this.getJdbcTemplate(DynamicDataSource.SLAVE).queryForList(sql.toString());
	}
	
	/**
	 * 获取订单的最新配送信息
	 * @param code
	 * @return
	 * @create yaoliang
	 * @time 2105-05-23
	 */
	public List<DeliverInfo> getLastDeliverInfo(List<Integer> idList) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT dip.pop_order_code popOrderCode, dip.deliver_state, dip.deliver_info, date_format(dip.time, '%Y-%m-%d %T') time")
		.append(" FROM deliver_info_pop dip")
		.append(" WHERE dip.id in (").append(StringUtils.join(idList, ",")).append(")") ;
		return this.getJdbcTemplate(DynamicDataSource.SLAVE).query(sql.toString(), new BeanPropertyRowMapper<DeliverInfo>(DeliverInfo.class));
	}
	
	/**
	 * 最新状态的时间与已揽收的时间差
	 * @param code
	 * @return
	 * @create yaoliang
	 * @time 2105-05-23
	 */
	public List<Map<String, Object>> getTimeDistance(String code){
		StringBuffer sql = new StringBuffer();
		sql.append("select dip.pop_order_code popOrderCode,IFNULL(")
		.append(" TIMESTAMPDIFF(HOUR,")
        .append(" (SELECT dip1.time FROM deliver_info_pop dip1 WHERE dip1.pop_order_code = dip.pop_order_code AND dip1.deliver_state = 1 LIMIT 1),")
		.append(" NOW()),0")
		.append(" ) hours") 
		.append(" from deliver_info_pop dip where dip.pop_order_code in ('").append(code).append("') GROUP BY dip.pop_order_code");
		return this.getJdbcTemplate(DynamicDataSource.SLAVE).queryForList(sql.toString());
	}

	/**
	 * 根据POPId获取省份
	 * @param code
	 * @return
	 * @create yaoliang
	 * @time 2105-05-25
	 */
	public List<Map<String, Object>> getProvicesByPOPId(int popId) {
		String sql = "select Id id,name from provinces";
		if(popId == PopBussiness.POP_MMB){
			sql += " where Id<"+Provinces.PROVINCE_FLAG;
		}else if(popId == PopBussiness.POP_JD){
			sql += " where Id>"+Provinces.PROVINCE_FLAG;
		}else{
			sql += " where Id=-1";
		}
		return this.getJdbcTemplate(DynamicDataSource.SLAVE).queryForList(sql);
	}

	/**
	 * 根据省id获取市集合
	 * @param code
	 * @return
	 * @create yaoliang
	 * @time 2105-05-26 上午9:01:10
	 */
	public List<Map<String, Object>> getCitysByProvinceId(int provinceId) {
		String sql = "select id,city name from province_city where province_id ="+provinceId;
		return this.getJdbcTemplate(DynamicDataSource.SLAVE).queryForList(sql);
	}

	/**
	 * 根据市id获取区集合
	 * @param code
	 * @return
	 * @create yaoliang
	 * @time 2105-05-26 上午9:01:10
	 */
	public List<Map<String, Object>> getDistrictsByCityId(int cityId) {
		String sql = "select id,area name from city_area where city_id ="+cityId;
		return this.getJdbcTemplate(DynamicDataSource.SLAVE).queryForList(sql);
	}

	public int getPopOrderInfoCount(String popOrderCode) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) from pop_order_info t WHERE t.pop_order_code=?");
		return this.getJdbcTemplate().queryForInt(sql.toString(), popOrderCode);
	}
	
	/**
	 * 根据运单号获取订单信息
	 * @param deliverCode 运单号
	 * @return
	 * @throws Exception
	 * @author likaige
	 * @create 2015年6月1日 上午9:56:48
	 */
	public voOrder getUserOrderByDeliverCode(String deliverCode) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT uo.id,uo.code,poi.deliver_code packageNum,uo.phone");
		sql.append(" FROM pop_order_info poi");
		sql.append(" JOIN buy_order_popec b on poi.pop_order_code=b.code");
		sql.append(" JOIN user_order_extend_popec uoe ");
		sql.append(" on ((b.is_parent=0 and b.code=uoe.popec_order_code) or (b.is_parent=2 and b.parent_code=uoe.popec_order_code))");
		sql.append(" JOIN user_order uo on uoe.id=uo.id");
		sql.append(" WHERE poi.deliver_code=?");
		sql.append(" LIMIT 1");
		RowMapper<voOrder> rm = new BeanPropertyRowMapper<voOrder>(voOrder.class);
		return this.getJdbcTemplate().queryForObject(sql.toString(), rm, deliverCode);
	}

	//修补pop_order_info
	public void repairPopOrderInfo(List<String> orderCodeList, List<String> popOrderCodeList) throws Exception {
		String sql = "UPDATE pop_order_info SET order_code=?,edit_time=? WHERE pop_order_code=?";
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		String nowTime = DateUtil.getNow();
		for(int i=0; i<orderCodeList.size(); i++){
			Object[] args = {orderCodeList.get(i), nowTime, popOrderCodeList.get(i)};
			batchArgs.add(args);
		}
		this.getJdbcTemplate().batchUpdate(sql, batchArgs);
	}

	//修补deliver_info_pop
	public void repairDeliverInfoPop(int orderId, String orderCode, String popOrderCode) throws Exception {
		String sql = "UPDATE deliver_info_pop SET order_id=?,order_code=? WHERE pop_order_code=?";
		Object[] args = {orderId, orderCode, popOrderCode};
		this.getJdbcTemplate().update(sql, args);
	}

	//获取POP订单信息
	public PopOrderInfo getPopOrderInfo(String popOrderCode) throws Exception {
		PopOrderInfo pop = null;
		List<String> param = new ArrayList<String>();
		param.add(popOrderCode);
		List<PopOrderInfo> list = this.getPopOrderInfoList(param);
		if(!list.isEmpty()){
			pop = list.get(0);
		}
		return pop;
	}
	
	public List<PopOrderInfo> getPopOrderInfoList(List<String> popOrderCodeList) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT *");
		sql.append(" FROM pop_order_info t");
		sql.append(" WHERE t.pop_order_code in('").append(StringUtils.join(popOrderCodeList, "','")).append("')");
		RowMapper<PopOrderInfo> rm = new BeanPropertyRowMapper<PopOrderInfo>(PopOrderInfo.class);
		return this.getJdbcTemplate().query(sql.toString(), rm);
	}

	//获取需要发送短信的订单信息
	public List<voOrder> getNeedSendShortMessageUserOrderList() throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT uo.id,uo.code,t.deliver_code packageNum,uo.phone");
		sql.append(" FROM pop_order_info t");
		sql.append(" JOIN user_order uo on t.order_code=uo.code");
		sql.append(" WHERE t.order_code is not null");
		sql.append(" and t.send_status=").append(PopOrderInfo.SEND_STATUS2);
		sql.append(" and t.message_send_status=0");
		RowMapper<voOrder> rm = new BeanPropertyRowMapper<voOrder>(voOrder.class);
		return this.getJdbcTemplate().query(sql.toString(), rm);
	}

	//获取需要补订单号的数据
	public List<PopOrderInfo> getNeedRepairPopOrderInfo() throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT t.pop_order_code,uoe.id orderId,uoe.order_code,t.status");
		sql.append(" FROM pop_order_info t");
		sql.append(" JOIN user_order_extend_popec uoe on t.pop_order_code=uoe.popec_order_code");
		sql.append(" WHERE t.order_code is null and t.send_status>0");
		RowMapper<PopOrderInfo> rm = new BeanPropertyRowMapper<PopOrderInfo>(PopOrderInfo.class);
		return this.getJdbcTemplate().query(sql.toString(), rm);
	}

	/**
	 * 根据市id获取区集合
	 * @param code
	 * @return
	 * @create yaoliang
	 * @time 2105-06-09 15:01:10
	 */
	public List<Map<String, Object>> getPOPCodeByPOPCode(List<String> popOrderTemplist, int deliverState) {
		StringBuffer sql = new StringBuffer();
		sql.append("select b.pop_order_code,b.deliver_state")
		.append(" from") 
		.append(" (")
		.append(" select a.pop_order_code,a.deliver_state from deliver_info_pop a  where a.pop_order_code in ('").append(StringUtils.join(popOrderTemplist, "','")).append("') ORDER BY a.id DESC,a.time DESC")
		.append(" )b")
		.append(" GROUP BY b.pop_order_code HAVING b.deliver_state = ").append(deliverState);
		return this.getJdbcTemplate().queryForList(sql.toString());
	}

	/**
	 * 获取订单最新记录的id
	 * @param code
	 * @return
	 * @create yaoliang
	 * @time 2105-06-16 15:01:10
	 */
	public List<Integer> getLastDeliverInfoId(List<String> popOrderTemplist) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT MAX(id) id FROM deliver_info_pop dip")
		.append(" WHERE dip.pop_order_code IN('").append(StringUtils.join(popOrderTemplist, "','")).append("')") 
		.append(" GROUP BY dip.pop_order_code");
		return this.getJdbcTemplate(DynamicDataSource.SLAVE).queryForList(sql.toString(), Integer.class);
	}

	//更新after_sale_agent的状态
	public void updateAfterSaleAgentStatus(List<Integer> afterSaleAgentIdList) {
		String sql = "update after_sale_agent set status=8 where id=?";
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		for(Integer id : afterSaleAgentIdList){
			Object[] args = {id};
			batchArgs.add(args);
		}
		this.getJdbcTemplate().batchUpdate(sql, batchArgs);
	}

	/**
	 * 获取发送失败的面单数量
	 * @return
	 * @author likaige
	 * @create 2015年7月28日 下午5:04:41
	 */
	public int getSendFailWaybillCount() throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) from pop_order_info t");
		sql.append(" WHERE t.send_status=").append(PopOrderInfo.SEND_STATUS_FAIL);
		return this.getJdbcTemplate().queryForInt(sql.toString());
	}

	/**
	 * 获取发送失败面单的POP订单号，只获取一个
	 * @return
	 * @author likaige
	 * @create 2015年7月28日 下午5:04:41
	 */
	public String getSendFailWaybillPopOrderCode() throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.pop_order_code from pop_order_info t");
		sql.append(" WHERE t.send_status=").append(PopOrderInfo.SEND_STATUS_FAIL);
		sql.append(" ORDER BY id DESC");
		sql.append(" LIMIT 1");
		return this.getJdbcTemplate().queryForObject(sql.toString(), String.class);
	}

	/** 
	 * @Description: 通过京东单号获取
	 * @return Map<String,Object> 返回类型 
	 * @author 叶二鹏
	 * @date 2015年7月30日 下午4:16:14 
	 */
	public Map<String, Object> getBuyOrderPopecByCode(String code) {
		Map<String, Object> rs = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT bop.parent_code, bop.is_parent, uoep.popec_area_code storage_id");
			sql.append(" FROM buy_order_popec bop JOIN user_order_extend_popec uoep ON bop.`code` = uoep.popec_order_code");
			sql.append(" WHERE bop.`code` =?");
			sql.append(" LIMIT 1");
			List<Map<String, Object>> rsList = getJdbcTemplate().queryForList(sql.toString(), code);
			rs = rsList!=null&&!rsList.isEmpty()?rsList.get(0):null;
			if (rs != null) {
				//如果是子订单，重新查询父订单对应的信息
				if (Integer.parseInt(rs.get("is_parent").toString())==2) {
					return this.getBuyOrderPopecByCode(rs.get("parent_code").toString());
				} else {
					return rs;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	/**
	 * @description 更改订单状态  当当前时间减去订单创建时间>=2小时时将订单状态改为-1即发送失败
	 * @author lml
	 * @return 订单ID
	 * @throws Exception
	 */
	public void updateOrderStatusToFail() throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE pop_order_info SET send_status = -1 ")
		.append(" WHERE TIMESTAMPDIFF(HOUR,order_create_time,now()) >= 1 ")
		.append(" AND send_status = 0 ");
		
		this.getJdbcTemplate().execute(sql.toString());
	}
	
	/**
	 * 
	 * @descripion 更新运单发送状态，采销单无数据状态改为“-3”，零元单状态改为“-4”
	 * @author 刘仁华
	 * @time  2015年9月6日
	 */
	public void updateOrderStatusToSpecial(String ids,int status)throws Exception {
		StringBuilder sql = new StringBuilder("");
		sql.append("UPDATE pop_order_info SET send_status="+status+" ")
		.append(" WHERE send_status=0 ")
		.append(" AND id IN (").append(ids).append(") ");
		this.getJdbcTemplate().execute(sql.toString());
	}

	/**
	 * 
	 * @description:查询发送失败的id of pop_order_info
	 * @return
	 * @returnType: List<Integer>
	 * @create:2015年9月18日 下午2:55:26
	 */
	public List<Map<String, Object>> queryNoBuyOrderPopInfoId() {
		StringBuilder sql = new StringBuilder();
		sql.append("select poi.id poiId,IFNULL(bop.id,-1) bopId from pop_order_info poi left join buy_order_popec bop");
		sql.append(" on bop.code=poi.pop_order_code where poi.send_status=-1");
		List<Map<String, Object>> mapList = this.getJdbcTemplate().queryForList(sql.toString());
		return mapList;
	}
	
}
