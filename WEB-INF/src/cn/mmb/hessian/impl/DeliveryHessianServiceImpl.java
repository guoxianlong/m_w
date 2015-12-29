package cn.mmb.hessian.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import mmb.delivery.service.DeliveryService;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.mmb.hessian.DeliveryHessianService;

public class DeliveryHessianServiceImpl implements DeliveryHessianService {
	
	private static Log log = LogFactory.getLog("debug.Log");
	
	@Resource
	private DeliveryService deliveryService;

	/**
	 * 处理异常面单数据
	 * @param json JSON格式的运单编号数据，如：{popType:0,deliverCodeList:['P01','P02']}
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:100,message:'错误信息'}
	 * @author likaige
	 * @create 2015年5月6日 上午11:06:50
	 */
	@Override
	public String processExceptionWaybill(String json) {
		log.info("processExceptionWaybill request: " + json);
		String jsonResult;
		try {
			jsonResult = deliveryService.processExceptionWaybill(json);
		} catch (Exception e) {
			jsonResult = this.createJsonResult(100, e.toString());
			e.printStackTrace();
		}
		log.info("processExceptionWaybill response:" + jsonResult);
		return jsonResult;
	}
	
	private String createJsonResult(int code, String message){
//		JSONObject json = new JSONObject();
//		json.put("code", code);
//		json.put("message", message);
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("code", code);
		m.put("message", message);
		JSONObject json = JSONObject.fromObject(m);
		return json.toString();
	}

	/** 
	 * 配送信息接收接口
	 * @param json JSON格式的订单配送数据，如：{deliveryId:'P01',pop:0,trace_api_dtos:[
	 * {ope_remark:"您的订单已分配",ope_time:"2013/06/25 09:16:09",ope_status:0,ope_name:"张三"},{}]}
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:-1,message:'错误信息'}
	 * @create 2015-5-7 上午11:11:23
	 * @author gel
	 */
	@Override
	public String processDeliverInformation(String json) {
//		log.info("processDeliverInformation request: " + json);
		String jsonResult = this.createJsonResult(200, "配送信息接口调用成功！");
		try {
			deliveryService.processDeliverInformation(json);
		} catch (Exception e) {
			jsonResult = this.createJsonResult(-1, "配送信息接口调用失败，msg:" + e.toString()+json);
			e.printStackTrace();
		}
//		log.info("processDeliverInformation response:" + jsonResult);
		return jsonResult;
	}

	/**
	 * 初始化大客户运单数据的接口
	 * @param json JSON格式的运单数据，如：{popType:1,popOrderCode:'JD01',time:'2015-01-01 01:01:01'}
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:-1,message:'错误信息'}
	 * @author likaige
	 * @create 2015年5月15日 上午8:48:34
	 */
	@Override
	public String initPOPDeliverInfoData(String json) {
		log.info("initPOPDeliverInfoData request: " + json);
		String jsonResult;
		try {
			jsonResult = deliveryService.initPOPDeliverInfoData(json);
		} catch (Exception e) {
			jsonResult = this.createJsonResult(-1, e.toString());
			e.printStackTrace();
		}
		log.info("initPOPDeliverInfoData response:" + jsonResult);
		return jsonResult;
	}
	
	/**
	 * 发送大客户订单出库短信
	 * @param json JSON格式的POP订单号数据，如：{popType:1,orderId:"9527460481"}
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:-1,message:'错误信息'}
	 * @author likaige
	 * @create 2015年6月1日 上午9:42:04
	 */
	@Override
	public String sendPopShortMessage(String json) {
		log.info("sendPopShortMessage request: " + json);
		String jsonResult;
		try {
			jsonResult = deliveryService.sendPopShortMessage(json);
		} catch (Exception e) {
			jsonResult = this.createJsonResult(-1, e.toString());
			e.printStackTrace();
		}
		log.info("sendPopShortMessage response:" + jsonResult);
		return jsonResult;
	}
	
	/**
	 * 修补POP订单信息
	 * @param json JSON格式的订单数据，如：[{orderId:123,orderCode:"B01",popOrderCode:"JD01"},{}]
	 * <br/>orderCode:表示MMB子订单编号；popOrderCode:表示POP子订单编号
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:-1,message:'错误信息'}
	 * @author likaige
	 * @create 2015年6月2日 上午9:50:34
	 */
//	@Override
//	public String repairPopOrderInfo(String json) {
//		log.info("repairPopOrderInfo request: " + json);
//		String jsonResult;
//		try {
//			jsonResult = deliveryService.repairPopOrderInfo(json);
//		} catch (Exception e) {
//			jsonResult = this.createJsonResult(-1, e.toString());
//			e.printStackTrace();
//		}
//		log.info("repairPopOrderInfo response:" + jsonResult);
//		return jsonResult;
//	}

}
