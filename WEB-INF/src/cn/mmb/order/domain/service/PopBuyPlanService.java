package cn.mmb.order.domain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.util.DateUtil;
import cn.mmb.config.ConfigInfo;
import cn.mmb.delivery.util.HttpClientUtil;
import cn.mmb.hessian.HessianServlet;
import cn.mmb.hessian.SaleHessianServlet;
import cn.mmb.order.domain.dto.SubmitOrder;
import cn.mmb.order.domain.dto.SubmitOrderProduct;
import cn.mmb.order.domain.entity.PopBuyPlan;
import cn.mmb.order.domain.entity.PopBuyPlanProduct;
import cn.mmb.order.domain.entity.PopStockArea;
import cn.mmb.order.domain.exception.ExceptionCode;
import cn.mmb.order.domain.exception.PopBuyPlanException;
import cn.mmb.order.infrastructrue.persistence.PopBuyPlanDao;

/**
 * 处理POP采购计划单相关业务
 * @author likaige
 * @create 2015年9月16日 上午9:23:38
 */
@Service
public class PopBuyPlanService {
	
	private static Log log = LogFactory.getLog("debug.Log");

	@Resource
	private PopBuyPlanDao popBuyPlanDao;
	@Resource
	private SaleHessianServlet saleHessianServlet;
	@Resource
	private HessianServlet mosHessianServlet;
	@Resource
	private ConfigInfo configInfo;

	/**
	 * 下采购计划单
	 * <br/>调用方：前端
	 * @param json JSON格式的字符串，例如：{orderCode:"B001",addr1:201,addr2:202,addr3:203,addr4:204,
	 * productList:[{pop:1,skuId:1234,num:2,bNeedAnnex:true,bNeedGift:false}]}
	 * @return
	 * @author likaige
	 * @create 2015年9月16日 上午9:14:46
	 */
	@Transactional(rollbackFor=Exception.class)
	public String submitBuyPlan(String json) throws Exception {
		//解析传递参数
		SubmitOrder order = this.parseSubmitBuyPlanJson(json);
		
		//判断订单是否为混合单
		int isMixed = this.isMixedOrder(order);
		
		//通过订单编号获取订单id
		int orderId = popBuyPlanDao.getOrderIdByOrderCode(order.getOrderCode());
		
		//纯MMB单
		if(isMixed == SubmitOrderProduct.IS_MIXED0){
			//调接口
			initSplitHistory(orderId);
			
			JSONObject mmbjson = new JSONObject();
			mmbjson.put("success", true);
			mmbjson.put("resultCode", "101");
			mmbjson.put("resultMessage", "下单成功");
			return mmbjson.toString();
		}
		
		List<Integer> orderIdList = new ArrayList<Integer>();
		if(isMixed == SubmitOrderProduct.IS_MIXED2) {
			//调接口
			initSplitHistory(orderId);
			Map<String,Map<String,Integer>> newOrderCodeMap = this.splitOrder(orderId);
			String newOrderCode = newOrderCodeMap.get("JD").keySet().iterator().next();
			order.setOrderCode(newOrderCode);
			// 过滤掉MMB商品
			this.filterMmbProduct(order);
			//获取子id
			orderIdList.add(newOrderCodeMap.get("JD").values().iterator().next());
			orderId = orderIdList.get(0);
			orderIdList.add(newOrderCodeMap.get("MMB").values().iterator().next());
		}else{
			orderIdList.add(orderId);
			initSplitHistory(orderId);
		}
		
		//设置MMB商品对应的POP商品
		this.setPopSkuId(order);
		
		//批量获取库存
		this.getStock(order);
		
		//下单
		String result = this.submitOrder(order);
		
		try {
			//保存采购计划单
			PopBuyPlan buyPlan = this.savePopBuyPlan(order, result);
			
			//下单后订单处理
			this.updateOrderStatus(result, order.getStockArea().getId(),orderId);
			
			//更新user_order_product_split_history
			for (Integer id : orderIdList) {
				this.updateSplitHistory(buyPlan,id);
			}
			
			//构造返回参数
			return this.buildReturnParam(result, order.getStockArea().getId());
		} catch (Exception e) {
			e.printStackTrace();
			//调用取消接口
			try{
				this.cancelBuyPlan("{\"jdOrderId\":\""+order.getPopOrderCode()+"\"}");
			}catch(Exception e1){
				e1.printStackTrace();
			}
			
			throw new PopBuyPlanException(ExceptionCode.CODE_108);
		}
	}
	
	/**
	 * 调用前端的接口
	 * @param orderId
	 * @throws Exception
	 * @author yaoliang
	 * @time 2015年10月10日 上午10:30:10
	 */
	private void initSplitHistory(int orderId) throws Exception{
		log.info("initSplitHistory/init.mmx send: " + orderId);
		//调用前端接口
		String s = HttpClientUtil.getHttpClientInformation(configInfo.getSaleHttpServiceUrl(), orderId);
		log.info("initSplitHistory/init.mmx return: " + s);
		
		if(!s.equals("1")){
			throw new PopBuyPlanException(ExceptionCode.CODE_111);
		}
	}

	/**
	 * 更新user_order_product_split_history的价格(含税)
	 * @param buyPlan
	 * @param orderId
	 * @throws Exception
	 * @author yaoliang
	 * @time 2015年9月17日 上午10:30:10
	 */
	private void updateSplitHistory(PopBuyPlan buyPlan,int orderId) throws Exception{
		List<PopBuyPlanProduct> newList = new ArrayList<PopBuyPlanProduct>();
		for (PopBuyPlanProduct product : buyPlan.getProductList()) {
			if(product.getType() == 0){
				newList.add(product);
			}
		}
		
		if(!newList.isEmpty()){
			popBuyPlanDao.updateSplitHistory(newList,orderId);
		}
	}

	//设置MMB商品对应的POP商品
	private void setPopSkuId(SubmitOrder order) {
		List<Integer> mySkuIdList = new ArrayList<Integer>();
		for(SubmitOrderProduct product : order.getProductList()){
			mySkuIdList.add(product.getSkuId());
		}
		
		//获取MMB商品对应的POP商品id
		Map<Integer, Integer> skuIdMap = popBuyPlanDao.getPopSkuIdByMySkuId(mySkuIdList);
		for(SubmitOrderProduct product : order.getProductList()){
			Integer popSkuId = skuIdMap.get(product.getSkuId());
			if(popSkuId == null){
				throw new PopBuyPlanException(ExceptionCode.CODE_109);
			}
			product.setPopSkuId(popSkuId);
		}
	}
	
	//设置POP商品对应的MMB商品
	private void setMmbSkuId(PopBuyPlan buyPlan) {
		List<Integer> popSkuIdList = new ArrayList<Integer>();
		for(PopBuyPlanProduct product : buyPlan.getProductList()){
			popSkuIdList.add(product.getPopProductId());
			if(product.getOid() != 0){
				popSkuIdList.add(product.getOid());
			}
		}
		
		//获取POP商品对应的MMB商品id
		Map<Integer, Integer> skuIdMap = popBuyPlanDao.getMySkuIdByPopSkuId(popSkuIdList);
		for(PopBuyPlanProduct product : buyPlan.getProductList()){
			Integer mySkuId = skuIdMap.get(product.getPopProductId());
			product.setMyProductId(mySkuId);
			if(product.getOid() != 0){
				Integer mySkuOid = skuIdMap.get(product.getOid());
				product.setOid(mySkuOid);
			}
		}
	}

	/**
	 * 下单处理接口，调用销售那边的接口
	 * @param result
	 * @param stockAreaId
	 * @param orderId
	 * @return
	 */
	private String updateOrderStatus(String result, int stockAreaId, int orderId) throws Exception{
		JSONObject json = JSONObject.fromObject(result);
		JSONObject data = json.getJSONObject("result");
	 
		JSONObject newJson = new JSONObject();
		newJson.put("success", json.getBoolean("success"));
		newJson.put("mmbOrderId", orderId);
		newJson.put("jdOrderId", data.getString("jdOrderId"));
		newJson.put("areaid", stockAreaId);
		newJson.put("resultCode", ExceptionCode.exceptionCodeMap.get(json.getString("resultCode")));
		newJson.put("resultMessage", json.getString("resultMessage"));
		newJson.put("userId", 0);
		newJson.put("userName","system");
		
		log.info("下单处理 send: " + newJson.toString());
		String resultData = saleHessianServlet.updateUserOrderAfterSubmit(newJson.toString());
		log.info("下单处理 return: " + resultData);
		
		JSONObject resultJson= JSONObject.fromObject(resultData);
		boolean isSuccess = resultJson.getBoolean("success");
		String resultCode = resultJson.getString("resultCode");
		String localCode = ExceptionCode.exceptionCodeMap.get(resultCode);
		
		if(!isSuccess){
			throw new PopBuyPlanException(localCode);
		}
		
		return resultData;
	}

	//构造返回参数
	private String buildReturnParam(String jsonStr, int areaCode) {
		JSONObject json = JSONObject.fromObject(jsonStr);
		String resultCode = json.getString("resultCode");
		String localCode = ExceptionCode.exceptionCodeMap.get(resultCode);
		String localMsg = ExceptionCode.localCodeMsgMap.get(localCode);
		json.put("resultCode", localCode);
		json.put("resultMessage", resultCode+":"+localMsg);
		JSONObject result = json.getJSONObject("result");
		result.put("areaCode", areaCode);
		return json.toString();
	}

	//保存采购计划单
	private PopBuyPlan savePopBuyPlan(SubmitOrder order, String result) throws Exception{
		//判断是否已经存在计划单
		PopBuyPlan popBuyPlan = popBuyPlanDao.getPopBuyPlan(order.getOrderCode());
		if(popBuyPlan != null){
			List<PopBuyPlanProduct> productList = popBuyPlanDao.getPopBuyPlanProductList(order.getOrderCode());
			popBuyPlan.setProductList(productList);
			return popBuyPlan;
		}
		
		//解析下单返回结果
		PopBuyPlan buyPlan = this.parseSubmitOrderResult(result);
		
		//设置POP商品对应的MMB商品
		this.setMmbSkuId(buyPlan);
		
		//保存计划单
		buyPlan.setOrderCode(order.getOrderCode());
		buyPlan.setCreateTime(DateUtil.getNow());
		buyPlan.setStatus(PopBuyPlan.STATUS1);
		buyPlan.setType(0);
		buyPlan.setPopType(1);
		buyPlan.setStockAreaPopecId(order.getStockArea().getId());
		popBuyPlanDao.insertPopBuyPlan(buyPlan);
		
		//保存计划单商品
		popBuyPlanDao.insertPopBuyPlanProduct(buyPlan);
		
		return buyPlan;
	}

	//解析下单返回结果
	private PopBuyPlan parseSubmitOrderResult(String jsonStr) {
		PopBuyPlan buyPlan = new PopBuyPlan();
		
		JSONObject json = JSONObject.fromObject(jsonStr);
		JSONObject resultObj = json.getJSONObject("result");
		buyPlan.setPopOrderCode(resultObj.getString("jdOrderId"));
		buyPlan.setFreight(resultObj.getDouble("freight"));
		buyPlan.setOrderPrice(resultObj.getDouble("orderPrice"));
		buyPlan.setOrderNakedPrice(resultObj.getDouble("orderNakedPrice"));
		
		JSONArray jsonArray = resultObj.getJSONArray("sku");
		for(int i=0; i<jsonArray.size(); i++){
			JSONObject sku = jsonArray.getJSONObject(i);
			PopBuyPlanProduct product = new PopBuyPlanProduct();
			product.setPopProductId(sku.getInt("skuId"));
			product.setCount(sku.getInt("num"));
			product.setPopCategory(sku.getInt("category"));
			product.setPrice(sku.getDouble("price"));
			product.setName(sku.getString("name"));
			product.setTax(sku.getDouble("tax"));
			product.setNakedPrice(sku.getDouble("nakedPrice"));
			product.setType(sku.getInt("type"));
			product.setOid(sku.getInt("oid"));
			buyPlan.getProductList().add(product);
		}
		return buyPlan;
	}

	//下单
	private String submitOrder(SubmitOrder order) throws Exception{
		//构建下单的请求参数
		String jsonParam = this.buildSubmitOrderParam(order);
		
		//调用接口下单
		log.info("orderSubmit send: " + jsonParam);
		String result = mosHessianServlet.service("orderSubmit", jsonParam);
		log.info("orderSubmit return: " + result);
		
		JSONObject json = JSONObject.fromObject(result);
		boolean success = json.getBoolean("success");
		if(success){
			JSONObject resultObj = json.getJSONObject("result");
			order.setPopOrderCode(resultObj.getString("jdOrderId"));
		}else{
			String resultCode = json.getString("resultCode");
			//特殊情况处理：重复下单
			if("0008".equals(resultCode)){
				json.put("success", true); //修改成功状态
			}else{
				String localCode = ExceptionCode.exceptionCodeMap.get(resultCode);
				throw new PopBuyPlanException(localCode, resultCode);
			}
		}
		
		return result;
	}
	
	//构建下单的请求参数
	private String buildSubmitOrderParam(SubmitOrder order) {
		
		StringBuffer result = new StringBuffer();
		result.append("thirdOrder=" + order.getOrderCode());
		
		result.append("&sku=");
		JSONArray skuArray = new JSONArray();
		for(SubmitOrderProduct product : order.getProductList()){
			JSONObject sku = new JSONObject();
			sku.put("num", product.getNum());
			sku.put("skuId", product.getPopSkuId());
			sku.put("bNeedAnnex", product.isbNeedAnnex());
			sku.put("bNeedGift", product.isbNeedGift());
			skuArray.add(sku);
		}
		result.append(skuArray.toString());
		
		result.append("&name="         + "买卖宝");
		result.append("&mobile="       + "13146145818");
		result.append("&email="        + "jdmessage@ebinf.com");
		result.append("&invoiceState=" + 2);
		result.append("&invoiceType="  + 2);
		result.append("&selectedInvoiceTitle="   + 5);
		result.append("&invoiceContent="         + 1);
		result.append("&paymentType="  + 101);
		result.append("&submitState="  + 0);
		result.append("&companyName="  + "无锡买卖宝信息技术有限公司");
		result.append("&remark="       + "");
		result.append("&province="     + order.getStockArea().getAddPId());
		result.append("&city="         + order.getStockArea().getAddCId());
		result.append("&county="       + order.getStockArea().getAddAId());
		result.append("&town="         + order.getStockArea().getAddSId());
		result.append("&address="      + order.getStockArea().getAddressAll());
		result.append("&zip="          + order.getStockArea().getZip());
//		result.append("&invoicePhone="  + "0510-85385195");
//		result.append("&invoiceProvice="  + "江苏省");
//		result.append("&invoiceCity="  + "无锡市");
//		result.append("&invoiceAddress="  + "江苏省无锡市新区震泽路18号无锡（国家）软件园狮子座B座1层");
		
		return result.toString();
	}

	//批量获取库存
	private void getStock(SubmitOrder order) throws Exception{
		//获取京东的所有发货仓，第一个为就近发货仓
		List<PopStockArea> areaList = this.getPopStockAreaList(order.getAddr1(), order.getAddr2());
		
		for(Iterator<PopStockArea> iter=areaList.iterator(); iter.hasNext();){
			PopStockArea area = iter.next();
			
			//构建获取库存的请求参数
			String json = this.buildGetStockParam(order, area.getStockArea());
			log.info("getFiveStockById send: " + json);
			String result = mosHessianServlet.service("productStockGet", json);
			log.info("getFiveStockById return: " + result);
			
			//解析返回参数
			this.parseGetStockResult(area, result);
			
			//全部有货
			if(area.allHaveGoods()){
				order.setStockArea(area);
				return ;
			}
			
			//包含无货商品
			if(area.containsNoGoods()){
				iter.remove();
			}
		}
		
		//所以发货仓都缺货
		if(areaList.isEmpty()){
			throw new PopBuyPlanException(ExceptionCode.CODE_103);
		}else{
			//获取优先级最高的发货仓
			PopStockArea area = this.getPriorityStockArea(areaList);
			order.setStockArea(area);
		}
	}

	//获取优先级最高的发货仓
	private PopStockArea getPriorityStockArea(List<PopStockArea> areaList) {
		TreeMap<Integer, PopStockArea> priorityMap = new TreeMap<Integer, PopStockArea>();
		for(PopStockArea area : areaList){
			priorityMap.put(area.calPriority(), area);
		}
		return priorityMap.firstEntry().getValue();
	}

	//解析获取库存接口的返回结果
	private void parseGetStockResult(PopStockArea area, String result) {
		JSONObject json = JSONObject.fromObject(result);
		JSONArray jsonArray = json.getJSONArray("result");
		for(int i=0; i<jsonArray.size(); i++){
			JSONObject product = jsonArray.getJSONObject(i);
			int skuId = product.getInt("skuId");
			int stockStateId = product.getInt("stockStateId");
			area.getProductStockStateMap().put(skuId, stockStateId);
		}
	}

	//获取京东的所有发货仓，第一个为就近发货仓
	private List<PopStockArea> getPopStockAreaList(int addr1, int addr2) throws Exception{
		List<PopStockArea> areaList = popBuyPlanDao.getPopStockAreaList(addr1, addr2);
		return areaList;
	}

	//构建获取库存的请求参数
	private String buildGetStockParam(SubmitOrder order, String area) {
		area = "&area="+area;
		JSONArray skuNums = new JSONArray();
		for(SubmitOrderProduct product : order.getProductList()){
			JSONObject p = new JSONObject();
			p.put("num", product.getNum());
			p.put("skuId", product.getPopSkuId());
			skuNums.add(p);
		}
		
		return "skuNums="+skuNums.toString()+area;
	}

	//过滤掉MMB商品
	private void filterMmbProduct(SubmitOrder order) {
		for(Iterator<SubmitOrderProduct> iter=order.getProductList().iterator(); iter.hasNext();){
			SubmitOrderProduct product = iter.next();
			if(product.getPop() == 0){
				iter.remove();
			}
		}
	}

	//调用销售接口进行拆单
	private Map<String,Map<String,Integer>> splitOrder(int orderId) {
		String newOrderCode = null;
		Map<String,Map<String,Integer>> map = new HashMap<String,Map<String,Integer>>();
		JSONObject obj = new JSONObject();
		obj.put("orderId", orderId);
		log.info("firstDemolitionOrder send: " + obj.toString());
		String result = saleHessianServlet.firstDemolitionOrder(obj.toString());
		log.info("firstDemolitionOrder return: " + result);
		
		JSONObject json = JSONObject.fromObject(result);
		boolean success = json.getBoolean("success");
		if(success){
			Map<String,Integer> orderMap = null;
			JSONArray jsonArray = json.getJSONArray("result");
			for(int i=0; i<jsonArray.size(); i++){
				orderMap = new HashMap<String,Integer>();
				JSONObject order = jsonArray.getJSONObject(i);
				String type = order.getString("type");
				if("JD".equals(type)){
					newOrderCode = order.getString("code");
					orderMap.put(newOrderCode, order.getInt("id"));
					map.put("JD", orderMap);
				}else{
					newOrderCode = order.getString("code");
					orderMap.put(newOrderCode, order.getInt("id"));
					map.put("MMB", orderMap);
				}
			}
		}else{
			String resultCode = json.getString("resultCode");
			String localCode = ExceptionCode.exceptionCodeMap.get(resultCode);
			throw new PopBuyPlanException(localCode, resultCode);
		}
		return map;
	}

	//判断订单是否为混合单
	private int isMixedOrder(SubmitOrder order) {
		SubmitOrderProduct product = new SubmitOrderProduct();
		List<SubmitOrderProduct> list = order.getProductList();
		if(!list.isEmpty()){
			product = order.getProductList().get(0);
		}
		
		for(SubmitOrderProduct p : order.getProductList()){
			if(p.getPop() != product.getPop()){
				return SubmitOrderProduct.IS_MIXED2;
			}
		}
		return product.getPop();
	}

	//解析传递参数
	private SubmitOrder parseSubmitBuyPlanJson(String json) {
		SubmitOrder order = null;
		try {
			Map<String, Class<SubmitOrderProduct>> classMap = new HashMap<String, Class<SubmitOrderProduct>>();
			classMap.put("productList", SubmitOrderProduct.class);
			JSONObject obj = JSONObject.fromObject(json);
			order = (SubmitOrder) JSONObject.toBean(obj, SubmitOrder.class, classMap);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PopBuyPlanException(ExceptionCode.CODE_107);
		}
		
		//判断MMB订单是否存在
		int orderId = popBuyPlanDao.getOrderIdByOrderCode(order.getOrderCode());
		if(orderId == 0){
			throw new PopBuyPlanException(ExceptionCode.CODE_110);
		}
		return order;
	}
	
	/**
	 * 确认订单接口.获取数据
	 * @param json 参数形式为json类型的String：{"jdOrderId":"42596254319"}
	 * @return {"success":true,"resultMessage":"下单成功！","resultCode":null}
	 * @author yaoliang
	 * @time 2015年9月16日 上午10:30:10
	 * @throws Exception
	 */
	public String confirmBuyPlan(String json) throws Exception{
		
		String jsonData = this.parseJson(json);
		JSONObject paramJson = JSONObject.fromObject(jsonData);
		String jdOrderCode = paramJson.getString("jdOrderId");
		
		log.info("确认下单接口 confirmOrder send: jdOrderId" + jdOrderCode);
		String jsonString = mosHessianServlet.service("confirmOrder","jdOrderId=" + jdOrderCode);
		log.info("确认下单接口 confirmOrder return: " + jsonString);
		
		JSONObject jSONObject = JSONObject.fromObject(jsonString);
		boolean b = jSONObject.getBoolean("success");
		String resultCode = jSONObject.getString("resultCode");
		String resultMessage = jSONObject.getString("resultMessage");
		String localCode = ExceptionCode.exceptionCodeMap.get(resultCode);
		String localMsg = ExceptionCode.localCodeMsgMap.get(localCode);
		if(b){
			//更新采购计划单状态
			this.updateBuyPlanByOrderCode(" status="+PopBuyPlan.STATUS2,jdOrderCode);
			jSONObject.put("resultCode", localCode);
			jSONObject.put("resultMessage", resultMessage+":"+localMsg);
		}else{
			throw new PopBuyPlanException(localCode, resultCode);
		}
		
		return jSONObject.toString();
	}

	/**
	 * 取消订单接口.获取数据
	 * @param json 参数形式为json类型的String：{"jdOrderId":"42596254319"}
	 * @return {"success":true,"resultMessage":"下单成功！","resultCode":null}
	 * @author yaoliang
	 * @time 2015年9月16日 上午10:30:10
	 * @throws Exception
	 */
	public String cancelBuyPlan(String json) throws Exception{
		String jsonData = this.parseJson(json);
		JSONObject paramJson = JSONObject.fromObject(jsonData);
		String jdOrderCode = paramJson.getString("jdOrderId");
		
		log.info("取消下单接口 cancelBuyPlan send: jdOrderId=" + jdOrderCode);
		String jsonString = mosHessianServlet.service("cancel","jdOrderId=" + jdOrderCode);
		log.info("取消下单接口 cancelBuyPlan return: " + jsonString);
		
		JSONObject jSONObject = JSONObject.fromObject(jsonString);
		String resultCode = jSONObject.getString("resultCode");
		//特殊情况处理：该订单已经被取消
		if("3203".equals(resultCode)){
			jSONObject.put("success", true); //修改成功状态
		}

		String resultMessage = jSONObject.getString("resultMessage");
		String localCode = ExceptionCode.exceptionCodeMap.get(resultCode);
		String localMsg = ExceptionCode.localCodeMsgMap.get(localCode);

		if(jSONObject.getBoolean("success")){
			//更新采购计划单状态
			this.updateBuyPlanByOrderCode(" status="+PopBuyPlan.STATUS3,jdOrderCode);
			jSONObject.put("resultCode", localCode);
			jSONObject.put("resultMessage", resultMessage+":"+localMsg);
		}else{
			throw new PopBuyPlanException(localCode, resultCode);
		}
		
		return jSONObject.toString();
	}
	
	/**
	 * 更新计划单
	 * @param status
	 * @param jdOrderCode
	 * @throws Exception
	 * @author yaoliang
	 * @time 2015年9月16日 上午10:30:10
	 */
	private void updateBuyPlanByOrderCode(String status, String jdOrderCode) throws Exception{
		try {
			popBuyPlanDao.updateBuyPlanByOrderCode(status,jdOrderCode);
		} catch (PopBuyPlanException e) {
			e.printStackTrace();
			throw new PopBuyPlanException(ExceptionCode.CODE_206);
		}
	}

	/**
	 * 转换字符串为json
	 * @param json
	 * @return {"jdOrderCode":"B11"}
	 * @throws PopBuyPlanException
	 * @author yaoliang
	 * @time 2015年9月16日 上午10:30:10
	 */
	private String parseJson(String json) throws PopBuyPlanException{
		JSONObject jSONObject;
		try {
			jSONObject = JSONObject.fromObject(json);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PopBuyPlanException(ExceptionCode.CODE_107);
		}
		
		return jSONObject.toString();
	}

	/**
	 * 查询采购计划单
	 * @param json JSON格式的字符串，例如：{"orderCode":"D150915100007"}，orderCode：表示MMB订单号
	 * @return
	 * @author likaige
	 * @create 2015年9月16日 上午9:14:46
	 */
	public String queryBuyPlan(String json) {
		JSONObject jSONObject = JSONObject.fromObject(json);
		String orderCode = jSONObject.getString("orderCode");
		
		//查询计划单商品
		List<PopBuyPlanProduct> productList = popBuyPlanDao.getPopBuyPlanProductList(orderCode);
		
		//构造返回结果
		JSONObject jsonResult = new JSONObject();
		jsonResult.put("success", true);
		jsonResult.put("resultCode", ExceptionCode.CODE_501);
		jsonResult.put("resultMessage", ExceptionCode.localCodeMsgMap.get(ExceptionCode.CODE_501));
		JSONObject result = new JSONObject();
		result.put("mmbOrderId", orderCode);
		JSONArray sku = new JSONArray();
		for(PopBuyPlanProduct p : productList){
			JSONObject obj = new JSONObject();
			obj.put("skuId", p.getMyProductId());
			obj.put("num", p.getCount());
			obj.put("type", p.getType());
			obj.put("oid", p.getOid());
			sku.add(obj);
		}
		result.put("sku", sku);
		jsonResult.put("result", result);
		return jsonResult.toString();
	}
}
