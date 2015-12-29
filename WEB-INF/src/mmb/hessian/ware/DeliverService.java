package mmb.hessian.ware;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mmb.delivery.domain.DeliverPackageCode;
import mmb.stock.stat.DeliverCorpInfoBean;
import adultadmin.action.vo.voOrder;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class DeliverService {
	
	public static String DEFULT_DATETIME = "2013-08-27 00:00:00";
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 根据包裹单号查询物流最近信息
	 * 
	 * @param single
	 * @return
	 */
	public String getDeliverInfo(String single) {
		if (single == null || single.equals("")) {
			return "";
		}
		DbOperation db = null;
		try {
			db = new DbOperation();
			db.init(DbOperation.DB_SLAVE);
			String sql = "select deliver_info from deliver_order where deliver_no='"
					+ StringUtil.toSql(single) + "' order by id desc limit 1";
			ResultSet rs = db.executeQuery(sql);
			while (rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.release();
		}
		return "";
	}
	
	/**
	 * 根据包裹单号查询POP物流最近信息
	 * @param single 包裹单号
	 * @return
	 * @author likaige
	 * @create 2015年5月18日 下午2:19:06
	 */
	public String getPopDeliverInfo(String single) {
		if (single == null || single.equals("")) {
			return "";
		}
		DbOperation db = null;
		try {
			db = new DbOperation();
			db.init(DbOperation.DB_SLAVE);
			String sql = "select deliver_info from deliver_info_pop where deliver_code='"
					+ StringUtil.toSql(single) + "' order by id desc limit 1";
			ResultSet rs = db.executeQuery(sql);
			while (rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.release();
		}
		return "";
	}

	public List<String> getDeliverInfoList(String single) {
		if (single == null || single.equals("")) {
			return null;
		}
		DbOperation db = null;
		try {
			db = new DbOperation();
			db.init(DbOperation.DB_SLAVE);
			int id = db.getInt("select id from deliver_order where deliver_no='"
							+ StringUtil.toSql(single) + "' order by id desc limit 1");
			if (id == 0) {
				return null;
			}
			String sql = "select deliver_time,deliver_info from deliver_order_info where deliver_id=" + id + " order by id desc";
			ResultSet rs = db.executeQuery(sql);
			List<String> list = new ArrayList<String>();
			while (rs.next()) {
				Date time = new Date(rs.getLong(1));
				list.add(sdf.format(time) + ":" + rs.getString(2));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.release();
		}
		return null;
	}
	
	/**
	 * 根据包裹单号，获取所有的配送信息
	 * @param single 包裹单号
	 * @return
	 * @author likaige
	 * @create 2015年5月18日 下午2:24:47
	 */
	public List<String> getPopDeliverInfoList(String single) {
		if (single == null || single.equals("")) {
			return null;
		}
		DbOperation db = null;
		try {
			db = new DbOperation();
			db.init(DbOperation.DB_SLAVE);
			single = StringUtil.toSql(single);
			String sql = "select time,deliver_info from deliver_info_pop where deliver_code='" + single + "' order by id desc";
			ResultSet rs = db.executeQuery(sql);
			List<String> list = new ArrayList<String>();
			while (rs.next()) {
				Date time = rs.getTimestamp(1);
				list.add(sdf.format(time) + ":" + rs.getString(2));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.release();
		}
		return null;
	}

	/**
	 * 根据订单号查询物流最近信息,注意必须传类
	 * @param orderId
	 * @return
	 */
	public DeliverOrderInfoBean getDeliverInfoByOrderId(Integer orderId) {
		if (orderId == null || orderId.intValue() == 0) {
			return null;
		}
		
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, db);
		try {
			AuditPackageBean auditPackage = stockService.getAuditPackage("order_id = "+orderId);
			String sql = "select * from deliver_order where order_id="
					+ orderId.intValue() + " order by id desc limit 1";
			ResultSet rs = db.executeQuery(sql);
			while (rs.next()) {
				String deliverInfo = rs.getString("deliver_info");
				if(!deliverInfo.startsWith("2")){
					if(DateUtil.compareTime(rs.getString("receive_time"), DEFULT_DATETIME) == 1){
						deliverInfo = rs.getString("receive_time").substring(0, 19) + ":" + deliverInfo;
					}else if(DateUtil.compareTime(rs.getString("post_time"), DEFULT_DATETIME) == 1){
						deliverInfo = rs.getString("post_time").substring(0, 19) + ":" + deliverInfo;
					}
				}
				
				DeliverCorpInfoBean deliver = voOrder.deliverInfoMapAll.get(auditPackage.getDeliver());
				DeliverOrderInfoBean bean = new DeliverOrderInfoBean();
				bean.setDeliverId(deliver.getId());
				bean.setDeliverName(deliver.getNameWap());
				bean.setDeliverNo(rs.getString("deliver_no"));
				bean.setDeliverPhone(deliver.getPhone());
				bean.setOrderId(orderId);
				bean.setDeliverInfo(deliverInfo);
				bean.setDeliverState(rs.getInt("deliver_state"));
				
				//赛澳递特殊处理 2013-11-04 zhaolin
//				if(auditPackage.getDeliver() == 17 || auditPackage.getDeliver() == 18){
//					String deliverTime = DateUtil.compareTime(rs.getString("post_time"), rs.getString("receive_time"))>=0?rs.getString("post_time"):rs.getString("receive_time");
//					bean.setDeliverInfo(deliverTime.substring(0, 19)+":"+rs.getString("deliver_info"));
//				}
				
				return bean;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.release();
		}
		return null;
	}
	
	/**
	 * 根据订单号查询物流最近信息,注意必须传类
	 * @param orderId 订单id
	 * @return
	 * @author likaige
	 * @create 2015年5月18日 下午2:32:05
	 */
	public DeliverOrderInfoBean getPopDeliverInfoByOrderId(Integer orderId) {
		if (orderId == null || orderId.intValue() == 0) {
			return null;
		}
		
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select *");
			sql.append(" from deliver_info_pop");
			sql.append(" where order_id=").append(orderId);
			sql.append(" order by id desc limit 1");
			ResultSet rs = db.executeQuery(sql.toString());
			if (rs.next()) {
				String deliverInfo = rs.getString("time").substring(0, 19) + ":" + rs.getString("deliver_info");
				DeliverOrderInfoBean bean = new DeliverOrderInfoBean();
				bean.setDeliverId(DeliverPackageCode.POP_JD_DELIVER_ID);
				bean.setDeliverName("京东快递");
				bean.setDeliverNo(rs.getString("deliver_code"));
				bean.setDeliverPhone("4008869499");
				bean.setOrderId(orderId);
				bean.setDeliverInfo(deliverInfo);
				bean.setDeliverState(rs.getInt("deliver_state"));
				return bean;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.release();
		}
		return null;
	}
	
	/**
	 * 根据订单号查询物流最近信息,注意必须传类
	 * @param orderId
	 * @return
	 */
	public DeliverOrderInfoBean getDeliverInfoListByOrderId(Integer orderId) {
		if (orderId == null || orderId.intValue() == 0) {
			return null;
		}
		
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, db);
		try {
			AuditPackageBean auditPackage = stockService.getAuditPackage("order_id = "+orderId);
			String sql = "select * from deliver_order where order_id="
					+ orderId.intValue() + " order by id desc limit 1";
			ResultSet rs = db.executeQuery(sql);
			while (rs.next()) {
				String deliverInfo = rs.getString("deliver_info");
				if(!deliverInfo.startsWith("2")){
					if(DateUtil.compareTime(rs.getString("receive_time"), DEFULT_DATETIME) == 1){
						deliverInfo = rs.getString("receive_time").substring(0, 19) + ":" + deliverInfo;
					}else if(DateUtil.compareTime(rs.getString("post_time"), DEFULT_DATETIME) == 1){
						deliverInfo = rs.getString("post_time").substring(0, 19) + ":" + deliverInfo;
					}
				}
				
				DeliverCorpInfoBean deliver = voOrder.deliverInfoMapAll.get(auditPackage.getDeliver());
				DeliverOrderInfoBean bean = new DeliverOrderInfoBean();
				bean.setDeliverId(deliver.getId());
				bean.setDeliverName(deliver.getNameWap());
				bean.setDeliverNo(rs.getString("deliver_no"));
				bean.setDeliverPhone(deliver.getPhone());
				bean.setOrderId(orderId);
				bean.setDeliverInfo(deliverInfo);
				bean.setDeliverState(rs.getInt("deliver_state"));
				
				//赛澳递特殊处理 2013-11-04 zhaolin
//				if(auditPackage.getDeliver() == 17 || auditPackage.getDeliver() == 18){
//					String deliverTime = DateUtil.compareTime(rs.getString("post_time"), rs.getString("receive_time"))>=0?rs.getString("post_time"):rs.getString("receive_time");
//					bean.setDeliverInfo(deliverTime.substring(0, 19)+":"+rs.getString("deliver_info"));
//				}
				
				//完整物流信息
				ResultSet rs2 = db
						.executeQuery("select deliver_time,deliver_info from deliver_order_info where deliver_id="
								+ rs.getInt("id") + " order by id asc");
				List<String> list = new ArrayList<String>();
				while (rs2.next()) {
					Date time = new Date(rs2.getLong(1));
					list.add("\"deliverTime\":\""+sdf.format(time)+"\",\"info\":\""+rs2.getString(2)+"\"");
//					list.add(sdf.format(time) + ":" + rs2.getString(2));
				}
				rs2.close();
				bean.setDeliverInfoList(list);
				
				return bean;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.release();
		}
		return null;
	}
	
	/**
	 * 根据订单号查询物流最近信息,注意必须传类
	 * @param orderId
	 * @return
	 * @author likaige
	 * @create 2015年5月18日 下午2:44:36
	 */
	public DeliverOrderInfoBean getPopDeliverInfoListByOrderId(Integer orderId) {
		if (orderId == null || orderId.intValue() == 0) {
			return null;
		}
		
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select *");
			sql.append(" from deliver_info_pop");
			sql.append(" where order_id=").append(orderId);
			sql.append(" order by id desc");
			
			DeliverOrderInfoBean deliverOrder = null;
			ResultSet rs = db.executeQuery(sql.toString());
			while(rs.next()) {
				String time = rs.getString("time").substring(0, 19);
				String deliverInfo = rs.getString("deliver_info");
				if(deliverOrder == null){
					deliverOrder = new DeliverOrderInfoBean();
					deliverOrder.setDeliverId(DeliverPackageCode.POP_JD_DELIVER_ID);
					deliverOrder.setDeliverName("京东快递");
					deliverOrder.setDeliverNo(rs.getString("deliver_code"));
					deliverOrder.setDeliverPhone("4008869499");
					deliverOrder.setOrderId(orderId);
					deliverOrder.setDeliverInfo(time+ ":" +deliverInfo);
					deliverOrder.setDeliverState(rs.getInt("deliver_state"));
					deliverOrder.setDeliverInfoList(new ArrayList<String>());
				}
				deliverOrder.getDeliverInfoList().add("\"deliverTime\":\""+time +"\",\"info\":\""+deliverInfo+"\"");
			}
			return deliverOrder;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.release();
		}
		return null;
	}

}
