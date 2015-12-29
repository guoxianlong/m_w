package cn.mmb.delivery.domain.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import mmb.delivery.dao.DeliveryDao;
import mmb.delivery.domain.DeliverPackageCode;
import mmb.hessian.ware.DeliverOrderInfoBean;
import mmb.sale.order.stat.OrderAdminStatusLogBean;
import mmb.stock.stat.DeliverCorpInfoBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.action.vo.voOrder;
import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;
import cn.mmb.delivery.domain.adp.DateXml;
import cn.mmb.delivery.domain.model.dao.YdMailingDao;
import cn.mmb.delivery.infrastructrue.persistence.DeliverMailUtil;
import cn.mmb.delivery.infrastructrue.persistence.DeliveryOrderDao;

/**
 * 订单状态更新接口
 * @author yaoliang 
 * @create 2015年5月7日 上午8:40:22
 */
@Service
public class DeliveryOrderService {
	
	private static Log log = LogFactory.getLog("debug.Log");
	
	@Resource
	private DeliveryOrderDao deliveryOrderDao;
	
	@Resource
	private DeliveryDao deliveryDao;

	/**
	 * 如果运单状态里有[妥投]、[拒收]，两种状态时，需要将对应订单的状态更新为：妥投或者拒收。
	 * @author yaoliang 
	 * @create 2015年5月7日 上午8:40:22
	 * @param orderId 要修改的订单id
	 * @param deliverStatus 配送状态
	 */
	public int updateOrderStatus(int orderId, int deliverStatus) throws Exception {
		//获取订单信息
		voOrder order = deliveryOrderDao.getUserOrder(orderId);
		
		//配送状态是已出库，如果订单状态不是‘已到款’，则不更新
		if(deliverStatus==DeliverOrderInfoBean.DELIVER_STATE0 && order.getStatus()!=voOrder.STATUS3){
			return 0;
		}
		//配送状态是已妥投，如果订单状态不是‘已发货’，则不更新
		if(deliverStatus==DeliverOrderInfoBean.DELIVER_STATE7 && order.getStatus()!=voOrder.STATUS6){
			log.info("配送状态是已妥投，订单状态不是已发货，不更新：orderId="+orderId);
			return 0;
		}
		
		//根据配送状态，计算订单状态
		int orderStatus = order.getStatus();
		if(deliverStatus == DeliverOrderInfoBean.DELIVER_STATE0){
			orderStatus = voOrder.STATUS6;
		}else if(deliverStatus == DeliverOrderInfoBean.DELIVER_STATE7){
			orderStatus = voOrder.STATUS14;
		}else if(deliverStatus == DeliverOrderInfoBean.DELIVER_STATE8){
			orderStatus = voOrder.STATUS11;
		}
		//还原到出库之前的状态
		else if(deliverStatus == -1){
			orderStatus = voOrder.STATUS3;
		}
		
		//修改订单状态
		int rows = deliveryOrderDao.updateOrderStatus(orderId, orderStatus);
		
		//记录日志
		OrderAdminLogBean log = new OrderAdminLogBean();
		log.setCreateDatetime(DateUtil.getNow());
		log.setContent("[订单状态:"+order.getStatus()+"->"+orderStatus+"]");
		log.setType(OrderAdminLogBean.ORDER_ADMIN_PROP);
		log.setUserId(0);
		log.setUsername("system");
		log.setOrderId(orderId);
		log.setOrderCode(order.getCode());
		this.saveOrderAdminLog(log);
		
		//记录订单状态日志
		if(deliverStatus==DeliverOrderInfoBean.DELIVER_STATE7 || deliverStatus==DeliverOrderInfoBean.DELIVER_STATE8){
			OrderAdminStatusLogBean statusLog = new OrderAdminStatusLogBean();
			statusLog.setCreateDatetime(DateUtil.getNow());
			statusLog.setOriginStatus(order.getStatus());
			statusLog.setNewStatus(orderStatus);
			statusLog.setType(1);
			statusLog.setUsername("system");
			statusLog.setOrderId(orderId);
			this.saveOrderAdminStatusLog(statusLog);
		}
		
		return rows;
	}
	
	//记录订单日志
	private void saveOrderAdminLog(OrderAdminLogBean log) {
		DbOperation dbOp = new DbOperation(DbOperation.DB_SMS);
		IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			logService.addOrderAdminLog(log);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			logService.releaseAll();
		}
	}
	//记录订单状态日志
	private void saveOrderAdminStatusLog(OrderAdminStatusLogBean statusLog) {
		DbOperation dbOp = new DbOperation(DbOperation.DB_SMS);
		IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			logService.addOrderAdminStatusLog(statusLog);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			logService.releaseAll();
		}
	}
	
	/**
	 * 如果运单状态里有[妥投]、[拒收]，两种状态时，需要将对应订单的时间更新为当前时间
	 * @author yaoliang 
	 * @create 2015年5月15日 上午8:40:22
	 * @param orderCodePOP 订单号
	 * @param status 配送状态
	 */
	public int updatePOPOrderInfo(String orderCodePOP, int status) throws Exception {
		int rows = deliveryOrderDao.updatePOPOrderInfo(orderCodePOP, status);
		if(rows<1) log.info("订单："+orderCodePOP+"的状态未修改成功");
		return rows;
	}
	
	/**
	 * 添加"快递公司获取个性化信息"关联表
	* @Description: 
	* @author ahc
	 */
	@SuppressWarnings("rawtypes")
	public void addDeliverRelation(int deliverIds[],String startTime,String endTime){
		int result =0;
		try {
			for(int deliverId :deliverIds){
				List<OrderStockBean> list =deliveryOrderDao.getOrderStock(deliverId,startTime,endTime);
				
				//特殊处理，如果快递公司为京东，则要分配运单号
				if(deliverId==DeliverCorpInfoBean.DELIVER_ID_JD_CD || deliverId==DeliverCorpInfoBean.DELIVER_ID_JD_WX){
					//获取未使用的运单号
					List<DeliverPackageCode> codeList = deliveryDao.getUnusedDeliverPackageCodeList(deliverId, list.size());
					for(int i=0; i<list.size(); i++){
						OrderStockBean os = list.get(i);
						os.setPackageCode(codeList.get(i).getPackageCode());
					}
					
					//把运单号标记成已使用状态
					deliveryDao.updateDeliverPackageCodeUsed(codeList);
				}
				
				for(OrderStockBean os : list){
					//查找是否已存在
					List list2=deliveryOrderDao.getDeliverRelationList("order_code ='"+os.getOrderCode()+"' and deliver_id="+deliverId + " and status not in (2,4) ");
					if(list2.isEmpty()){
						result =deliveryOrderDao.addDeliverRelation(deliverId, os);
						if(result <= 0){
							log.info("快递公司："+deliverId+"和订单号："+os.getOrderCode()+"未关联成功！");
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("定时任务添加快递公司个性化关联表异常：", e);
		}
		
	}
	
	/**
	 * 添加"快递公司获取个性化信息"明细表
	* @Description: 
	* @author ahc
	 * @throws Exception 
	 */
	@Transactional
	public void addDeliverRelationInfo(int deliverId,List<YdMailingDao> list) throws DataAccessException{
		deliveryOrderDao.addDeliverRelationInfo(deliverId, list);
		deliveryOrderDao.updateDeliverRelation(list);
	}
	/**
	 * 获取待发送状态的数据
	* @Description: 
	* @author ahc
	 */
	public List<Map<String,String>> getDeliverRelationInfo(int deliverId){
		List<Map<String,Object>> list=deliveryOrderDao.getDeliverRelationList(" status =0 and deliver_id="+deliverId);
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		
		for(Map<String,Object> map:list){
			String orderCode = (String) map.get("order_code");
			try {
				voOrder order =deliveryOrderDao.getUserOrderByCode(" code='"+orderCode+"'");
				Map<String,String> map2 = new HashMap<String,String>();
				map2.put("id",orderCode);
				map2.put("address",order.getAddress());
				result.add(map2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	/**
	 * 发送接口信息
	* @Description: 
	* @author ahc
	 * @throws UnsupportedEncodingException 
	 */
	public String sendInterfaceInfo(String url,String partnerid,String password,String version,String request,List<Map<String,String>> sendData) throws UnsupportedEncodingException {
		String xmldata=DateXml.Ydxmldata(sendData);
		log.error("韵达接口-发送封装且过滤特殊字符的数据："+xmldata);
		String xml =DeliverMailUtil.getYdInterface(url, partnerid, password, version, request, xmldata);
		return xml;
	}
	
	/**
	 * 获取接口信息
	* @Description: 
	* @author ahc
	 * @throws DocumentException 
	 */
	public List<YdMailingDao> getInterfaceInfo(String xml) throws DocumentException{
		List<YdMailingDao> list =DeliverMailUtil.addYunDaInfo(xml);
		return list;
	}
}
