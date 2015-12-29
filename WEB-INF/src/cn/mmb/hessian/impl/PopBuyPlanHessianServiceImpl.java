package cn.mmb.hessian.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.mmb.hessian.PopBuyPlanHessianService;
import cn.mmb.order.domain.exception.ExceptionCode;
import cn.mmb.order.domain.exception.PopBuyPlanException;
import cn.mmb.order.domain.service.PopBuyPlanService;
import net.sf.json.JSONObject;

/**
 * 处理POP采购计划的Hessian接口
 * @author likaige
 * @create 2015年9月16日 上午9:13:39
 */
public class PopBuyPlanHessianServiceImpl implements PopBuyPlanHessianService {
	
	private static Log log = LogFactory.getLog("debug.Log");
	
	@Resource
	private PopBuyPlanService popBuyPlanService;

	/**
	 * 下采购计划单
	 * <br/>调用方：前端和销售后台
	 * @param json JSON格式的字符串，例如：{orderCode:"D13022079668",addr1:201,addr2:202,addr3:203,addr4:204,
	 * productList:[{pop:0,skuId:233285,num:2,bNeedAnnex:true,bNeedGift:false}]}
	 * @return
	 * @author likaige
	 * @create 2015年9月16日 上午9:14:46
	 */
	@Override
	public String submitBuyPlan(String json) {
		log.info("submitBuyPlan request: " + json);
		String jsonResult;
		try {
			jsonResult = popBuyPlanService.submitBuyPlan(json);
		} catch (PopBuyPlanException e) {
			e.printStackTrace();
			jsonResult = this.createJsonResult(e.getLocalCode(), e.getResultMessage());
		} catch (Exception e) {
			e.printStackTrace();
			jsonResult = this.createJsonResult(ExceptionCode.CODE_001, e.getMessage());
		}
		log.info("submitBuyPlan response:" + jsonResult);
		return jsonResult;
	}
	
	/**
	 * 确认采购计划单
	 * <br/>调用方：销售后台
	 * @param jdOrderId JSON格式的字符串，例如：{"jdOrderId":"911"}
	 * @return {"success":true,"resultMessage":"确认成功！","resultCode":"201"}
	 * @author likaige
	 * @create 2015年9月16日 上午9:14:46
	 */
	@Override
	public String confirmBuyPlan(String jdOrderId){
		log.info("confirmBuyPlan request:"+jdOrderId);
		String jsonString;
		try {
			jsonString = popBuyPlanService.confirmBuyPlan(jdOrderId);
		} catch (PopBuyPlanException e) {
			jsonString = this.createJsonResult(e.getLocalCode(), e.getResultMessage());
			e.printStackTrace();
		}catch(Exception e){
			jsonString = this.createJsonResult(ExceptionCode.CODE_001, e.getMessage());
			e.printStackTrace();
		}
		log.info("confirmBuyPlan response:"+jsonString);
		return jsonString;
	}
	
	/**
	 * 取消采购计划单
	 * <br/>调用方：销售后台
	 * @param jdOrderId JSON格式的字符串，例如：{"jdOrderId":"911"}
	 * @return @return {"success":true,"resultMessage":"取消成功！","resultCode":"301"}
	 * @author likaige
	 * @create 2015年9月16日 上午9:14:46
	 */
	@Override
	public String cancleBuyPlan(String jdOrderId){
		log.info("cancelBuyPlan request:"+jdOrderId);
		String jsonString;
		try {
			jsonString = popBuyPlanService.cancelBuyPlan(jdOrderId);
		} catch (PopBuyPlanException e) {
			jsonString = this.createJsonResult(e.getLocalCode(), e.getResultMessage());
			e.printStackTrace();
		}catch(Exception e){
			jsonString = this.createJsonResult(ExceptionCode.CODE_001, e.getMessage());
			e.printStackTrace();
		}
		log.info("cancelBuyPlan response:"+jsonString);
		return jsonString;
	}
	
	/**
	 * 查询采购计划单
	 * <br/>调用方：销售后台
	 * @param json JSON格式的字符串，例如：{"orderCode":"B11"}，orderCode：表示MMB订单号
	 * @return JSON数据：{"success":true,"resultMessage":"成功！","resultCode":"501",
	 * "result":{"mmbOrderId":"B001","sku":[{"skuId":852431,"num":1,"type":0,"oid":0},
	 * {"skuId":852431,"num":1,"type":2,"oid":852431}]}}
	 * @author likaige
	 * @create 2015年9月16日 上午9:14:46
	 */
	@Override
	public String queryBuyPlan(String json) {
		log.info("queryBuyPlan request: " + json);
		String jsonResult;
		try {
			jsonResult = popBuyPlanService.queryBuyPlan(json);
		} catch (PopBuyPlanException e) {
			jsonResult = this.createJsonResult(e.getLocalCode(), e.getResultMessage());
			e.printStackTrace();
		} catch (Exception e) {
			jsonResult = this.createJsonResult(ExceptionCode.CODE_001, e.getMessage());
			e.printStackTrace();
		}
		log.info("queryBuyPlan response:" + jsonResult);
		return jsonResult;
	}
	
	private String createJsonResult(String resultCode, String resultMessage){
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("success", false);
		m.put("resultCode", resultCode);
		m.put("resultMessage", resultMessage);
		JSONObject json = JSONObject.fromObject(m);
		return json.toString();
	}
}
