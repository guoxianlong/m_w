package mmb.delivery.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.delivery.dao.DeliveryDao;
import mmb.delivery.domain.DeliverInfo;
import mmb.delivery.domain.DeliverPackageCode;
import mmb.delivery.domain.PopBussiness;
import mmb.delivery.domain.PopOrderInfo;
import mmb.delivery.domain.Waybill;
import mmb.delivery.service.DeliveryService;
import mmb.hessian.ware.DeliverOrderInfoBean;
import mmb.stock.stat.DeliverCorpInfoBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.action.vo.voOrder;
import adultadmin.bean.sms.SendMessage3Bean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ISMSService;
import adultadmin.util.Constants;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cn.mmb.delivery.application.DeliveryOrderFacade;
import cn.mmb.delivery.domain.model.vo.DeliverRelationBean;
import cn.mmb.hessian.HessianServlet;
import cn.mmb.hessian.SaleHessianServlet;

import com.chinasms.sms.SenderSMS3;

/**
 * 处理运单相关业务
 * @author likaige
 * @create 2015年4月28日 下午5:40:22
 */
@Service
public class DeliveryServiceImpl implements DeliveryService {
	
	private static Log log = LogFactory.getLog("debug.Log");
	
	@Resource
	private DeliveryDao deliveryDao;
	@Resource
	private HessianServlet deliveryAPI;
	@Resource
	private SaleHessianServlet saleHessianServlet;
	@Resource
	private DeliveryOrderFacade deliveryOrderFacade;
	
	/**
	 * 获取运单号
	 * @throws Exception
	 * @author likaige
	 * @create 2015年4月28日 下午5:41:10
	 */
	@Override
	public void getDeliveryCode() throws Exception {
		//获取买卖宝订单使用的运单号,无锡仓
		this.getDeliveryCode(DeliverCorpInfoBean.DELIVER_ID_JD_WX, Waybill.CARRIER_MMB_WX);
		
		//获取买卖宝订单使用的运单号,成都仓
		this.getDeliveryCode(DeliverCorpInfoBean.DELIVER_ID_JD_CD, Waybill.CARRIER_MMB_CD);
		
		//获取POP京东订单使用的运单号
		this.getDeliveryCode(DeliverPackageCode.POP_JD_DELIVER_ID, Waybill.CARRIER_JD);
	}
	
	//获取指定快递公司的运单号
	private void getDeliveryCode(int deliverId, int type) throws Exception {
		//获取快递公司的未使用的运单号数量
		int unusedCount = deliveryDao.getUnusedDeliveryCodeCount(deliverId);
		
		//从配置文件中获取设置的最小的未使用运单数量
		int minUnusedCount = this.getMinUnusedDeliveryCodeCount();
		System.out.println(unusedCount+ "=>" +minUnusedCount);
		
		//剩余运单号比较充足时，不再获取
		if(unusedCount >= minUnusedCount){
			return;
		}
		
		//调用接口
		String jsonStr = deliveryAPI.service("etmsWaybillcodeGet", String.valueOf(type));
//		log.info("getDeliveryCode return: " + jsonStr);
		JSONObject json = JSONObject.fromObject(jsonStr);
		
		//判断返回状态是否成功
		int code = json.getInt("code");
		if(code != 100){
			log.error("获取运单号时出现异常："+ json.getString("message"));
			return;
		}
		
		//判断运单号是否为空
		JSONArray codes = json.getJSONArray("deliveryIdList");
		if(codes.isEmpty()){
			log.error("获取的运单号为空！");
			return;
		}
		
		//保存新的运单号
		List<DeliverPackageCode> deliverPackageCodeList = new ArrayList<DeliverPackageCode>();
		for(int i=0; i<codes.size(); i++){
			DeliverPackageCode deliverPackageCode = new DeliverPackageCode();
			deliverPackageCode.setPackageCode(codes.getString(i));
			deliverPackageCode.setDeliver(deliverId);
			deliverPackageCodeList.add(deliverPackageCode);
		}
		deliveryDao.saveDeliverPackageCode(deliverPackageCodeList);
	}
	
	/**
	 * 从配置文件中获取设置的最小的未使用运单数量
	 * <br/>如果配置文件中未配置，则默认设置为800
	 * @return
	 * @author likaige
	 * @create 2015年4月28日 下午5:56:11
	 */
	private int getMinUnusedDeliveryCodeCount(){
		int maxUnusedCount = 800;
		String confCount = Constants.config.getProperty("min_unused_delivery_code_count");
		if(StringUtils.isNotBlank(confCount)){
			try {
				maxUnusedCount = Integer.parseInt(confCount);
			} catch (NumberFormatException e) {
				log.error("conf.properties中未配置min_unused_delivery_code_count参数");
			}
		}
		return maxUnusedCount;
	}
	
	/**
	 * 发送MMB面单信息
	 * @throws Exception
	 * @author likaige
	 * @create 2015年4月30日 下午2:10:56
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void sendWaybill() throws Exception {
		//快递公司
		Integer[] deliverIds = {DeliverCorpInfoBean.DELIVER_ID_JD_WX, DeliverCorpInfoBean.DELIVER_ID_JD_CD};
				
		//获取需要发送的面单数据
		List<Waybill> waybillList = deliveryDao.getNeedSendWaybill(deliverIds);
		
		//没有需要发送的面单数据
		if(waybillList.isEmpty()){
			log.info("没有需要发送的MMB面单数据！");
			return;
		}
		
		//调用接口
		String[] paramList = new String[waybillList.size()];
		for(int i=0; i<waybillList.size(); i++){
			Waybill waybill = waybillList.get(i);
			if(waybill.getStockArea() == ProductStockBean.AREA_WX){
				waybill.setCarrier(Waybill.CARRIER_MMB_WX);
			}else if(waybill.getStockArea() == ProductStockBean.AREA_CD){
				waybill.setCarrier(Waybill.CARRIER_MMB_CD);
			}
			JSONObject json = JSONObject.fromObject(waybill);
			paramList[i] = waybill.getCarrier() + json.toString();
		}
		log.info("sendWaybill send: " + JSONObject.fromObject(waybillList).toString());
		String jsonStr = deliveryAPI.service("etmsWaybillSend", paramList);
		log.info("sendWaybill return: " + jsonStr);
		JSONObject json = JSONObject.fromObject(jsonStr);
		
		//判断返回状态是否成功
		int sendStatus = 1;
		int code = json.getInt("code");
		if(code != 200){
			sendStatus = 0;
		}
		
		//保存mailing_batch_package_waybill
		deliveryDao.saveMailingBatchPackageWaybill(waybillList, sendStatus);
		
		//修改订单状态
		for(Waybill waybill : waybillList){
			deliveryOrderFacade.updateOrderStatus(waybill.getUserOrderId(), DeliverOrderInfoBean.DELIVER_STATE0);
		}
	}
	
	//调用接口，获取代收货款
	@SuppressWarnings("unchecked")
	private Map<String, Double> getPopWaybillCollectionMoney(List<String> popOrderCodeList) throws Exception{
		//调用接口
		String codes = StringUtils.join(popOrderCodeList, ",");
		log.info("getDpriceByPopCode send: " + codes);
		String result = saleHessianServlet.getDpriceByPopCode(codes);
		log.info("getDpriceByPopCode return: " + result);
		
		//解析数据
		JSONArray jsonArray = JSONArray.fromObject(result);
		Map<String, Double> moneyMap = new HashMap<String, Double>();
		for(int i=0; i<jsonArray.size(); i++){
			JSONObject json = jsonArray.getJSONObject(i);
			Set<String> keySet = json.keySet();
			for(String key : keySet){
				moneyMap.put(key, json.getDouble(key));
			}
		}
		return moneyMap;
	}
	
	private double getMinDouble(){
		return 10E-10;
	}
	
	//获取需要发送的POP面单数据
	private List<Waybill> getNeedSendPopWaybill() throws Exception {
		//获取需要发送的面单数据
		List<Waybill> waybillList = deliveryDao.getNeedSendPopWaybill();
		
		//没有需要发送的面单数据
		if(waybillList.isEmpty()){
			return waybillList;
		}
		
		//获取代收货款
		List<String> popOrderCodeList = new ArrayList<String>();
		for(Waybill waybill : waybillList){
			popOrderCodeList.add(waybill.getOrderId());
		}
		//Map<String, Double> moneyMap = deliveryDao.getPopWaybillCollectionMoney(popOrderCodeList);
		Map<String, Double> moneyMap = this.getPopWaybillCollectionMoney(popOrderCodeList);
		
		//零元单数据
		List<Waybill> zeroWayBillList = new ArrayList<Waybill>();
		//过滤数据
		for(Iterator<Waybill> iter=waybillList.iterator(); iter.hasNext(); ){
			//过滤掉无拆单数据的订单
			Waybill waybill = iter.next();
			if(!moneyMap.containsKey(waybill.getOrderId())){
				iter.remove();
				continue;
			}
			
			//如果是代收货款的非S订单，且为未发送状态，但是代收货款为0，则不发送
			if(waybill.getCollectionValue() == 1 && !waybill.getUserOrderCode().startsWith("S")){
				double money = moneyMap.get(waybill.getOrderId());
				if("0".equals(waybill.getSendStatus())&&money <= this.getMinDouble()){
					zeroWayBillList.add(waybill);
					iter.remove();
					continue;
				}
				waybill.setCollectionMoney(money);
			}
		}
		this.operSpecialPopWaybill(zeroWayBillList, -4);
		return waybillList;
	}
	
	/**
	 * 
	 * @descripion 处理采销表无数据、零元单等情况的异常单据
	 * @author 刘仁华
	 * @time  2015年9月6日
	 */
	private void operSpecialPopWaybill(List<Waybill> list,int status){
		if(list!=null&&list.size()>0){
			StringBuilder sb = new StringBuilder("");
			sb.append(",");
			sb.append(list.get(0).getPoiId());
			for(int i=1;i<list.size();i++){
				Waybill waybill = list.get(i);
				sb.append(",");
				sb.append(waybill.getPoiId());
				if(i%800==0){
					try {
						deliveryDao.updateOrderStatusToSpecial(sb.substring(1), status);
					} catch (Exception e) {
						e.printStackTrace();
					}
					sb = new StringBuilder("");
				}
			}
			if(sb.length()>0){
				try {
					deliveryDao.updateOrderStatusToSpecial(sb.substring(1), status);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 发送POP面单信息
	 * @throws Exception
	 * @author likaige
	 * @create 2015年4月30日 下午2:10:56
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void sendPopWaybill() throws Exception {
		//获取需要发送的面单数据
		List<Waybill> waybillList = this.getNeedSendPopWaybill();
		
		//获取需要发送的S订单的面单数据
		//List<Waybill> sWaybillList = deliveryDao.getNeedSendPopWaybill_S();
		
		//合并数据
		//waybillList.addAll(sWaybillList);
		
		//没有需要发送的面单数据
		if(waybillList.isEmpty()){
			log.info("没有需要发送的POP面单数据！");
			return;
		}
		
		//获取未使用的运单号
		List<DeliverPackageCode> deliverCodeList = deliveryDao.getUnusedDeliverPackageCodeList(
				DeliverPackageCode.POP_JD_DELIVER_ID, waybillList.size());
		
		//如果获取的运单号不够用，则调用一下获取运单的接口
		if(deliverCodeList.size() < waybillList.size()){
			//获取POP京东订单使用的运单号
			this.getDeliveryCode(DeliverPackageCode.POP_JD_DELIVER_ID, Waybill.CARRIER_JD);
			log.info("运单号不够用，本次先获取运单号，下次循环再发送面单！");
			return;
		}
		
		//保存配送信息deliver_info_pop
		List<DeliverInfo> deliverInfoList = new ArrayList<DeliverInfo>();
		List<Integer> afterSaleAgentIdList = new ArrayList<Integer>();
		for(int i=0; i<waybillList.size(); i++){
			Waybill waybill = waybillList.get(i);
			waybill.setCarrier(Waybill.CARRIER_JD);
			waybill.setDeliveryId(deliverCodeList.get(i).getPackageCode());
			
			//获取订单地址信息
			Map<String, Object> address = deliveryDao.getUserOrderAddress(waybill.getUserOrderId());
			//获取订单仓库，如果是子订单则获取对应的父订单仓库
			Map<String, Object> buyOrderPopec = deliveryDao.getBuyOrderPopecByCode(waybill.getOrderId());
			DeliverInfo deliverInfo = new DeliverInfo();
			//deliverInfo.setOrderCode(waybill.getUserOrderCode());
			//deliverInfo.setOrderId(waybill.getUserOrderId());
			deliverInfo.setPopOrderCode(waybill.getOrderId());
			deliverInfo.setDeliverCode(waybill.getDeliveryId());
			deliverInfo.setDeliverInfo("订单已出库");
			deliverInfo.setDeliverState(DeliverOrderInfoBean.DELIVER_STATE0);
			deliverInfo.setTime(DateUtil.getNow());
			deliverInfo.setStorageId(buyOrderPopec!=null&&buyOrderPopec.get("storage_id")!=null?Integer.parseInt(buyOrderPopec.get("storage_id").toString()):0);
			deliverInfo.setProvince((String) address.get("province"));
			deliverInfo.setCity((String) address.get("city"));
			deliverInfo.setDistrict((String) address.get("district"));
			deliverInfo.setType(0);
			deliverInfo.setPopType(PopBussiness.POP_JD);
			deliverInfo.setDeliverType(1);
			deliverInfoList.add(deliverInfo);
			
			//修改订单状态
//			deliveryOrderFacade.updateOrderStatus(waybill.getUserOrderId(), DeliverOrderInfoBean.DELIVER_STATE0);
			
			//判断是否为S单
			if(waybill.getUserOrderCode().startsWith("S")){
				afterSaleAgentIdList.add(waybill.getAgentId());
			}
		}
		deliveryDao.savePopDeliverInfo(deliverInfoList);
		
		//把运单号标记成已使用状态
		deliveryDao.updateDeliverPackageCodeUsed(deliverCodeList);
		
		//更新pop_order_info
		deliveryDao.updatePopOrderInfo(waybillList, 1);
		
		//更新after_sale_agent
		if(!afterSaleAgentIdList.isEmpty()){
			deliveryDao.updateAfterSaleAgentStatus(afterSaleAgentIdList);
		}
		
		//调用接口
		String[] paramList = new String[waybillList.size()];
		for(int i=0; i<waybillList.size(); i++){
			Waybill waybill = waybillList.get(i);
			waybill.setReceiveAddress(retainCharacters(waybill.getReceiveAddress())); //过滤特殊字符
			JSONObject json = JSONObject.fromObject(waybill);
			paramList[i] = waybill.getCarrier() + json.toString();
		}
		try {
			log.info("sendWaybill send: " + JSONArray.fromObject(waybillList).toString());
			String jsonStr = deliveryAPI.service("etmsWaybillSend", paramList);
			log.info("sendWaybill return: " + jsonStr);
			JSONObject json = JSONObject.fromObject(jsonStr);
			
			//判断返回状态是否成功
			int code = json.getInt("code");
			if(code != 200){
				throw new RuntimeException("接口端出现异常");
			}
		} catch (RemoteConnectFailureException e) {
			//如果是readTimeout异常则不回滚事务
			if(e.getMessage().contains("Read timed out")){
				if(log.isInfoEnabled()){
					log.info("出现readTimeout异常:"+e);
				}
			}else{
				throw e;
			}
		}
	}
	
	/**
	 * 
	 * @descripion 只保留地址中的以下字符
	 * 中文汉字，数字，英文，-，_，(，)
	 * @author 刘仁华
	 * @time  2015年9月6日
	 */
	public static String retainCharacters(String str){
		if(str!=null&&!"".equals(str.trim())){
			StringBuilder newStr=new StringBuilder("");
			char[] ch = str.trim().toCharArray();//[a-zA-Z]
			Pattern pattern = Pattern.compile("[\\u4E00-\\u9FBF\\w\\-\\(\\)]+");
			for(char c:ch){
				if(pattern.matcher(String.valueOf(c)).find()){
					newStr.append(c);
				}
			}
			return newStr.toString();
		}else{
			return str;
		}
	}
	
	/**
	 * 过滤掉地址中的特殊字符，避免向京东发送面单时出现异常
	 * <br/>目前发现的需要过滤的字符包括："&nbsp;", "&", "<"
	 * @param address 地址
	 * @return
	 * @author likaige
	 * @create 2015年7月21日 下午1:45:23
	 */
	public static String filterSpecialCharacters(String address){
		address = StringUtils.remove(address, "&nbsp;");
		address = StringUtils.remove(address, '&');
		address = StringUtils.remove(address, '<');
		address = StringUtils.remove(address, '>');
		address = StringUtils.remove(address, '"');
		address = StringUtils.remove(address, '\'');
		return address;
	}
	
	/**
	 * 处理异常面单数据
	 * @param json JSON格式的运单编号数据，如：{popType:0,deliverCodeList:['P01','P02']}
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:100,message:'错误信息'}
	 * @author likaige
	 * @create 2015年5月6日 上午11:06:50
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public String processExceptionWaybill(String jsonStr) throws Exception {
		JSONObject json = JSONObject.fromObject(jsonStr);
		int popType = json.getInt("popType");
		JSONArray jsonArray = json.getJSONArray("deliverCodeList");
		
		//运单号为空
		if(jsonArray==null || jsonArray.isEmpty()){
			return this.createJsonResult(100, "运单号为空");
		}
		
		//获取运单号
		List<String> deliverCodeList = new ArrayList<String>();
		for(int i=0; i<jsonArray.size(); i++){
			deliverCodeList.add(jsonArray.getString(i));
		}
		
		if(popType == PopBussiness.POP_JD){
			//更改发送状态，以便下次扫描时再次发送
			deliveryDao.revertPopOrderInfo(deliverCodeList);
			
			//删除配送信息
			deliveryDao.deletePopDeliverInfoByDeliverCode(deliverCodeList);
			
			//还原订单状态，二期处理
//			this.revertOrderStatus(deliverCodeList);
		}else{
			//更改发送状态，以便下次扫描时再次发送
			int count = deliveryDao.revertDeliverRelation(deliverCodeList);
			if(count <= 0){
				log.info("异常单据更新失败："+ StringUtils.join(deliverCodeList, ','));
			}
		}
		
		return this.createJsonResult(200, "成功");
	}
	
	//还原订单状态
	@SuppressWarnings("unused")
	private void revertOrderStatus(List<String> deliverCodeList) throws Exception {
		for(String deliverCode : deliverCodeList){
			int orderId = deliveryDao.getOrderIdByDeliverCode(deliverCode);
			deliveryOrderFacade.updateOrderStatus(orderId, -1);
		}
	}

	//创建JSON返回结果
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
	 * POP配送状态查询
	 * @param param前台传的参数
	 * @return 配送信息列表
	 * @author yaoliang 
	 * @create 2015年5月8日 上午11:06:50
	 */
	@Override
	public List<DeliverInfo> getPOPDeliverInfoList(Map<String,Object> paramMap)throws Exception {
		List<DeliverInfo> deliverInfoList = deliveryDao.getPOPDeliverInfoList(paramMap);
		int deliverState = (Integer)paramMap.get("deliverState");
		if(!deliverInfoList.isEmpty()) {
			List<String> popOrderTemplist = new ArrayList<String>();
			//组织popOrderCode集合
			for (DeliverInfo deliverInfo : deliverInfoList) {
				popOrderTemplist.add(deliverInfo.getPopOrderCode());
			}
			
			if(deliverState != -1){
				List<Map<String,Object>> popOrderlist = deliveryDao.getPOPCodeByPOPCode(popOrderTemplist,deliverState);
				Map<String,Object> tempMap = new HashMap<String, Object>();
				for (Map<String,Object> map : popOrderlist) {
					tempMap.put((String)map.get("pop_order_code"), map.get("pop_order_code"));
				}
				//过滤不符合状态的单子
				for (Iterator<DeliverInfo> in = deliverInfoList.iterator();in.hasNext();) {
					DeliverInfo deliverInfo = in.next();
					if(tempMap.get(deliverInfo.getPopOrderCode()) == null||((String) tempMap.get(deliverInfo.getPopOrderCode())).trim().equals("")){
						in.remove();
					}
				}
			}
			
			//获取最新的状态，时间，信息
			List<Integer> idList = deliveryDao.getLastDeliverInfoId(popOrderTemplist);
			List<DeliverInfo> infoList = deliveryDao.getLastDeliverInfo(idList);
			Map<String,DeliverInfo> infoMap = new HashMap<String,DeliverInfo>();
			for (DeliverInfo deliverInfo : infoList) {
				infoMap.put(deliverInfo.getPopOrderCode(), deliverInfo);
			}
			
			//获取交付京东时的发货时间
			List<Map<String,Object>> deliverTimeMapList = deliveryDao.getPOPDeliverTime(StringUtils.join(popOrderTemplist, "','"));
			Map<String,Map<String,Object>> deliverTimeMap = new HashMap<String,Map<String,Object>>();
			for (Map<String, Object> map : deliverTimeMapList) {
				deliverTimeMap.put((String)map.get("pop_order_code"), map);
			}
			
			//赋值
			for (DeliverInfo deliverInfo : deliverInfoList) {
				Map<String, Object> map = deliverTimeMap.get(deliverInfo.getPopOrderCode());
				DeliverInfo info = infoMap.get(deliverInfo.getPopOrderCode());
				if(map !=null&&!map.isEmpty()){
					Timestamp time = (Timestamp)map.get("time");
					deliverInfo.setDeliverTime(DateUtil.formatTime(time));
				}
				if(info!=null&&!info.equals("")){
					deliverInfo.setTime(info.getTime());
					deliverInfo.setDeliverState(info.getDeliverState());
					deliverInfo.setDeliverInfo(info.getDeliverInfo());
				}
			}
		}
		
		return deliverInfoList;
	}
	
	/**
	 * 导出POP配送状态查询列表
	 * @param param前台传的参数
	 * @return配送信息列表
	 * @author yaoliang 
	 * @create 2015年5月14日 上午11:06:50
	 */
	public void exportPOPDeliverInfoList(HttpServletRequest request,HttpServletResponse response,Map<String,Object> paramMap) throws Exception{
		try {
			Workbook wb = new SXSSFWorkbook();
			Sheet sheet = wb.createSheet();
			
			for (int colNum=0; colNum<14; colNum++) {
	    		sheet.setColumnWidth(colNum, 14*256);
			}
			
			//第一行
			int rowNum = 0;
			Row row = sheet.createRow(0);
			row.createCell(rowNum++).setCellValue("POP商家");
			row.createCell(rowNum++).setCellValue("发货仓");
			row.createCell(rowNum++).setCellValue("快递公司");
			row.createCell(rowNum++).setCellValue("省");
			row.createCell(rowNum++).setCellValue("市");
			row.createCell(rowNum++).setCellValue("区");
			row.createCell(rowNum++).setCellValue("乡镇");
			row.createCell(rowNum++).setCellValue("包裹单号");
			row.createCell(rowNum++).setCellValue("mmb订单号");
			row.createCell(rowNum++).setCellValue("京东订单号");
			row.createCell(rowNum++).setCellValue("发货时间");
			row.createCell(rowNum++).setCellValue("状态");
			row.createCell(rowNum++).setCellValue("节点时间");
			row.createCell(rowNum++).setCellValue("配送信息");
			
			int rowNumber = 1;
			List<DeliverInfo> deliverInfoList = this.getPOPDeliverInfoList(paramMap);
			for (DeliverInfo deliverInfo : deliverInfoList) {
				int lineNum = 0;
				row = sheet.createRow(rowNumber++);
				row.createCell(lineNum++).setCellValue(deliverInfo.getPopName());
				row.createCell(lineNum++).setCellValue(deliverInfo.getStorageName());
				row.createCell(lineNum++).setCellValue(deliverInfo.getDeliveryName());
				row.createCell(lineNum++).setCellValue(deliverInfo.getProvince());
				row.createCell(lineNum++).setCellValue(deliverInfo.getCity());
				row.createCell(lineNum++).setCellValue(deliverInfo.getDistrict());
				row.createCell(lineNum++).setCellValue("");
				row.createCell(lineNum++).setCellValue(deliverInfo.getDeliverCode());
				row.createCell(lineNum++).setCellValue(deliverInfo.getOrderCode());
				row.createCell(lineNum++).setCellValue(deliverInfo.getPopOrderCode());
				row.createCell(lineNum++).setCellValue(deliverInfo.getDeliverTime());
				row.createCell(lineNum++).setCellValue(deliverInfo.getDeliverStateName());
				row.createCell(lineNum++).setCellValue(deliverInfo.getTime());
				row.createCell(lineNum++).setCellValue(deliverInfo.getDeliverInfo());
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String fileName = "配送信息列表";
			String newFileName = "";
			try {
				newFileName = new String(fileName.getBytes("gb2312"), "iso8859-1");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String excelFileName=newFileName+sdf.format(new Date()); 
			
			String agent = request.getHeader("User-Agent");
			boolean isMSIE = (agent != null && agent.indexOf("MSIE") != -1);
	
			if (isMSIE) {
			    fileName = URLEncoder.encode(fileName, "UTF-8");
			} else {
			    fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
			}
			response.setHeader("Content-disposition","attachment; filename=\"" + excelFileName + ".xlsx\"");
			response.setContentType("application/msxls");
			wb.write(response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	@Transactional(rollbackFor=Exception.class)
	public void processDeliverInformation(String jsonStr) throws Exception {
		//解析配送信息数据
		List<DeliverInfo> deliverInfoList = this.parseDelierInfoData(jsonStr);
		
		JSONObject json = JSONObject.fromObject(jsonStr);
		int popType = json.getInt("pop");
		if(popType == PopBussiness.POP_JD){
			this.doPopDeliver(deliverInfoList);
		}else{
			this.doMmbDeliver(deliverInfoList);
		}
	}

	//处理MMB配送信息
	private void doMmbDeliver(List<DeliverInfo> deliverInfoList) throws Exception {
		DeliverInfo deliverInfo = deliverInfoList.get(0);
		String deliverCode = deliverInfo.getDeliverCode();
		
		DeliverOrderInfoBean deliverOrder = deliveryDao.getDeliverOrderByDeliverCode(deliverCode);
		
		for(DeliverInfo d : deliverInfoList) {
			int status = d.getDeliverState();
			
			//删除原来的配送信息
			deliveryDao.deleteDeliverOrderInfo(deliverOrder.getId(), status);
			
			//已妥投或拒收状态
			if(status==DeliverOrderInfoBean.DELIVER_STATE7 || status==DeliverOrderInfoBean.DELIVER_STATE8) {
				//更新订单状态
				deliveryOrderFacade.updateOrderStatus(deliverOrder.getOrderId(), status);
			}
		}
		
		//保存配送信息
		deliveryDao.saveDeliverOrderInfo(deliverInfoList, deliverOrder.getId());
	}

	//处理POP配送信息
	private void doPopDeliver(List<DeliverInfo> deliverInfoList) throws Exception {
		DeliverInfo deliverInfo = deliverInfoList.get(0);
		
		//获取第一条的POP配送信息
		//DeliverInfo first = deliveryDao.getFirstDeliverInfoPOP(deliverInfo.getDeliverCode());
		
		//获取已保存的POP配送信息
		List<DeliverInfo> savedDeliverInfoList = deliveryDao.getDeliverInfoPOPList(deliverInfo.getDeliverCode());
		
		//没有该运单号的配送信息
		if(savedDeliverInfoList.isEmpty()){
			log.info("没有该运单号的配送信息:"+deliverInfo.getDeliverCode());
			return ;
		}
		
		DeliverInfo first = savedDeliverInfoList.get(0);
		for(Iterator<DeliverInfo> iter=deliverInfoList.iterator(); iter.hasNext(); ) {
			DeliverInfo d = iter.next();
			int status = d.getDeliverState();
			
			//删除原来的配送信息
			//deliveryDao.deleteDeliverInfo(first.getDeliverCode(), status);
			
			//判断本条配送信息是否已经存在
			for(DeliverInfo saved : savedDeliverInfoList){
				if(this.equalsDeliverInfo(d, saved)){
					iter.remove();
					break;
				}
			}
			
			//已妥投或拒收状态
			if(status==DeliverOrderInfoBean.DELIVER_STATE7 || status==DeliverOrderInfoBean.DELIVER_STATE8) {
				//更新订单状态
				if(first.getOrderId() > 0){
					deliveryOrderFacade.updateOrderStatus(first.getOrderId(), status);
				}
				
				//写拒收数据
				deliveryOrderFacade.updatePOPOrderInfo(first.getPopOrderCode(), status);
			}
		}
		
		//保存配送信息
		deliveryDao.saveDeliverInfo(deliverInfoList, first);
	}
	
	/**
	 * 判断两条配送信息是否一致
	 * @param dt 接口传递的对象
	 * @param db 数据库中查询的对象
	 * @return
	 * @author likaige
	 * @create 2015年7月29日 下午2:26:51
	 */
	private boolean equalsDeliverInfo(DeliverInfo dt, DeliverInfo db){
		if(dt.getDeliverState() != db.getDeliverState()){
			return false;
		}
		if(!dt.getDeliverInfo().equals(db.getDeliverInfo())){
			return false;
		}
		String time1 = dt.getTime().replace("/", "-");
		if(DateUtil.compareTime(time1, db.getTime()) != 0){
			return false;
		}
		return true;
	}

	/**
	 * 查询运单信息
	 * @param param前台传的参数
	 * @return配送信息列表
	 * @author yaoliang 
	 * @return 
	 * @throws Exception 
	 * @create 2015年5月14日 上午11:06:50
	 */
	@Override
	public List<DeliverInfo> getPOPDeliverInfo(Map<String,Object> paramMap) throws Exception {
		Pattern p2 = Pattern.compile("\\S{0,}(市|区|县)\\S{0,}(乡|镇)\\S{0,}");
		Pattern p3 = Pattern.compile("\\S{0,}(市|区|县)\\S{0,}(乡|镇)\\S{0,}(村|屯|邮)\\S{0,}");
		
		List<DeliverInfo> deliverInfoList = deliveryDao.getPOPDeliverInfo(paramMap);
		if(!deliverInfoList.isEmpty()){
			int timeLevel = 0;
			DeliverInfo deliverInfo = deliverInfoList.get(0);
			String popCode = deliverInfo.getPopOrderCode();
			String code = deliverInfo.getOrderCode();
			List<Map<String, Object>> addressMapList = deliveryDao.getAddressByCode(code);
			if(!addressMapList.isEmpty()){
				Matcher m3 = p3.matcher((String)addressMapList.get(0).get("address"));
				if (m3.find()) {
					timeLevel = 3;
				} else {
					Matcher m2 = p2.matcher((String)addressMapList.get(0).get("address"));
					if (m2.find()) {
						timeLevel = 2;
					} else {
						timeLevel = 1;
					}
				}
			}
			
			List<Map<String, Object>> agingMapList = deliveryDao.getAgingByCode(popCode,deliverInfo.getPopType());
			if(!agingMapList.isEmpty()){
				if(timeLevel == 3){
					deliverInfo.setEffectTime((Long)agingMapList.get(0).get("village_time"));
				}else if(timeLevel == 2){
					deliverInfo.setEffectTime((Long)agingMapList.get(0).get("town_time"));
				}else if(timeLevel == 1){
					deliverInfo.setEffectTime((Long)agingMapList.get(0).get("city_area_time"));
				}
			}
		}
		
		return deliverInfoList;
		
	}
	
	/** 
	 * 解析配送信息数据
	 * @param json JSON格式的订单配送数据，如：{deliveryId:'P01',pop:0,trace_api_dtos:[
	 * {ope_remark:"您的订单已分配",ope_time:"2013/06/25 09:16:09",ope_status:0,ope_name:"张三"},{}]}
	 */
	private List<DeliverInfo> parseDelierInfoData(String jsonStr) {
		List<DeliverInfo> list = new ArrayList<DeliverInfo>();
		JSONObject json = JSONObject.fromObject(jsonStr);
		String deliverCode = json.getString("deliveryId");
		int popType = json.getInt("pop");
		
		JSONArray details = json.getJSONArray("trace_api_dtos");
		for(int i=0; i<details.size(); i++){
			DeliverInfo deliverInfo = new DeliverInfo();
			deliverInfo.setDeliverCode(deliverCode);
			deliverInfo.setPopType(popType);
			
			JSONObject detail = details.getJSONObject(i);
			deliverInfo.setDeliverInfo(detail.getString("ope_remark"));
			deliverInfo.setTime(detail.getString("ope_time"));
			deliverInfo.setDeliverState(detail.getInt("ope_status"));
			list.add(deliverInfo);
		}
		return list;
	}
	
	/**
	 * 初始化大客户运单数据的接口
	 * @param json JSON格式的运单数据，如：{popType:1,popOrderCode:'JD01',time:'2015-01-01 01:01:01'}
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:-1,message:'错误信息'}
	 * @author likaige
	 * @create 2015年5月15日 上午8:48:34
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public String initPOPDeliverInfoData(String json) throws Exception {
		JSONObject obj = JSONObject.fromObject(json);
		DeliverInfo deliver = (DeliverInfo) JSONObject.toBean(obj, DeliverInfo.class);
		
		//判断订单是否已推送过
		int count = deliveryDao.getPopOrderInfoCount(deliver.getPopOrderCode());
		if(count > 0){
			return this.createJsonResult(200, "该订单已推送过");
		}
		
		//获取订单信息
		//Map<String, Object> userOrder = deliveryDao.getUserOrderByPopOrderCode(deliver.getPopOrderCode());
		//String orderCode = (String) userOrder.get("order_code");
		//Timestamp createTime = (Timestamp) userOrder.get("create_datetime");
		
		//保存订单配送信息
		deliveryDao.initPopOrderInfo(deliver.getPopOrderCode());
		
		return this.createJsonResult(200, "成功");
	}

	/**
	 * 把对象中的属性和值转化为URL中的参数
	 * @param obj 对象信息
	 * @return 字符串格式：name=mmb&price=100
	 * @author likaige
	 * @create 2015年5月13日 上午9:07:02
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private static String swapObject2UrlParam(Object obj){
		StringBuilder urlParam = new StringBuilder();
		JSONObject json = JSONObject.fromObject(obj);
		Set<String> keySet = json.keySet();
		for(String key : keySet){
			urlParam.append(key).append("=").append(json.get(key)).append("&");
		}
		if(urlParam.toString().endsWith("&")){
			urlParam.deleteCharAt(urlParam.length()-1);
		}
		return urlParam.toString();
	}
	
	/**
	 * 获取订单号或包裹单号
	 * @author yaoliang 
	 * @create 2015年5月16日  上午8:40:22
	 * @param code 单号
	 * @param scanType 区分订单号和包裹单号
	 */
	@Override
	public String getPOPDeliverOrderCode(String code, int scanType) throws Exception {
		 String DeliverOrderCode = deliveryDao.getPOPDeliverOrderCode(code,scanType);
		 return DeliverOrderCode==null?"":DeliverOrderCode;
	}

	/**
	 * 组装配送信息
	 * @param code
	 * @return
	 * @create yaoliang
	 * @time 2105-05-23
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List<HashMap<String, String>> getPOPDeliverInfo(List<HashMap<String, String>> listRows,String date) {
		
		Pattern p2 = Pattern.compile("\\S{0,}(市|区|县)\\S{0,}(乡|镇)\\S{0,}");
		Pattern p3 = Pattern.compile("\\S{0,}(市|区|县)\\S{0,}(乡|镇)\\S{0,}(村|屯|邮)\\S{0,}");
		List<String> popOrderCodes = new ArrayList<String>();
		List<String> orderCodes = new ArrayList<String>();
		if(listRows.isEmpty()) return new ArrayList<HashMap<String, String>>();
		//组装订单编号
		for (HashMap<String, String> deliverInfoMap : listRows) {
			popOrderCodes.add(deliverInfoMap.get("pop_order_code"));
			orderCodes.add(deliverInfoMap.get("order_code"));
		}
		Map map2 = listRows.get(0);
		int i = StringUtil.toInt(map2.get("pop_type")+"");
		//获取配送的地址
		List<Map<String,Object>> addressMapList = deliveryDao.getAddressByCode(StringUtils.join(orderCodes, "','"));
		Map<String, Map<String, Object>> addressMap = new HashMap<String, Map<String,Object>>();
		for (Map<String,Object> map : addressMapList) {
			addressMap.put((String)map.get("orderCode"), map);
		}
		//获取时效
		List<Map<String, Object>> agingMapList = deliveryDao.getAgingByCode(StringUtils.join(popOrderCodes, "','"),i);
		Map<String, Map<String, Object>> agingMap = new HashMap<String, Map<String,Object>>();
		for (Map<String,Object> map : agingMapList) {
			agingMap.put((String)map.get("popOrderCode"), map);
		}
		//最新状态的时间与已揽收的时间差
		List<Map<String,Object>> timeDistanceMapList = deliveryDao.getTimeDistance(StringUtils.join(popOrderCodes, "','"));
		Map<String, Map<String, Object>> timeDistanceMap = new HashMap<String, Map<String,Object>>();
		for (Map<String,Object> map : timeDistanceMapList) {
			timeDistanceMap.put((String)map.get("popOrderCode"), map);
		}
		
		//获取最新配送信息
		Map<String, DeliverInfo> lastDeliverInfoMap = new HashMap<String,DeliverInfo>();
		
		//获取最新的状态，时间，信息
		List<Integer> idList = deliveryDao.getLastDeliverInfoId(popOrderCodes);
		List<DeliverInfo> deliverInfoList = deliveryDao.getLastDeliverInfo(idList);
		for (DeliverInfo deliverInfo : deliverInfoList) {
			lastDeliverInfoMap.put(deliverInfo.getPopOrderCode(), deliverInfo);
		}
		
		int timeLevel = 0;
		
		for (Iterator<HashMap<String, String>> iterator = listRows.iterator(); iterator.hasNext();) {
			Map<String, String> deliverInfoMap = iterator.next();
			String popOrderCode = deliverInfoMap.get("pop_order_code");
			String orderCode = deliverInfoMap.get("order_code");
			//填充地址、级别、时效
			Map<String, Object> address= addressMap.get(orderCode);
			if(address!=null&&!address.isEmpty()){
				Matcher m3 = p3.matcher((String)address.get("address"));
				if (m3.find()) {
					timeLevel = 3;
				} else {
					Matcher m2 = p2.matcher((String)address.get("address"));
					if (m2.find()) {
						timeLevel = 2;
					} else {
						timeLevel = 1;
					}
				}
			}
			
			Map<String, Object> aging = agingMap.get(popOrderCode);
			Map<String, Object> timeDistance = timeDistanceMap.get(popOrderCode);
			if(aging!=null&&timeDistance!=null){
				if(aging.get("popOrderCode").equals(deliverInfoMap.get("pop_order_code"))&&timeDistance.get("popOrderCode").equals(aging.get("popOrderCode"))){
					long time = 0;//时效,单位：小时
					long timeDistances = (Long)timeDistance.get("hours");
					if(timeLevel == 3){
						time = (Long)aging.get("village_time");
					}else if(timeLevel == 2){
						time = (Long)aging.get("town_time");
					}else if(timeLevel == 1){
						time = (Long)aging.get("city_area_time");
					}
					
					if(date.equals("1")){
						if(timeDistances-time<=0){
							iterator.remove();
							continue;
						}
					}else if(date.equals("2")){
						if(timeDistances-time<=0||timeDistances-time>24){
							iterator.remove();
							continue;
						}
					}else if(date.equals("3")){
						if(timeDistances-time<=24||timeDistances-time>48){
							iterator.remove();
							continue;
						}
					}else if(date.equals("4")){
						if(timeDistances-time<=48||timeDistances-time>72){
							iterator.remove();
							continue;
						}
					}else if(date.equals("5")){
						if(timeDistances-time<=72){
							iterator.remove();
							continue;
						}
					}else if(date.equals("6")){
						if(timeDistances-time<-24||timeDistances-time>0){
							iterator.remove();
							continue;
						}
					}
					deliverInfoMap.put("time",time+"");
					deliverInfoMap.put("time_level",timeLevel+"");
					deliverInfoMap.put("address", (String)address.get("address"));
				}
			}
			
			//填充最新的状态、最新的状态的配送信息、最新状态的时间
			DeliverInfo info = lastDeliverInfoMap.get(deliverInfoMap.get("pop_order_code"));
			if(info !=null){
				deliverInfoMap.put("deliver_state", info.getDeliverStateName());
				deliverInfoMap.put("deliver_info", info.getDeliverInfo());
				deliverInfoMap.put("post_time", info.getTime());
			}
		}
	   	return listRows;
	}

	/**
	 * 根据POPId获取省份
	 * @param code
	 * @return
	 * @create yaoliang
	 * @time 2105-05-25
	 */
	@Override
	public List<Map<String, Object>> getProvicesByPOPId(int popId) {
		return deliveryDao.getProvicesByPOPId(popId);
	}

	/**
	 * 根据省id获取市集合
	 * @param code
	 * @return
	 * @create yaoliang
	 * @time 2105-05-26 上午9:01:10
	 */
	@Override
	public List<Map<String, Object>> getCitysByProvinceId(int provinceId) {
		return deliveryDao.getCitysByProvinceId(provinceId);
	}

	/**
	 * 根据市id获取区集合
	 * @param code
	 * @return
	 * @create yaoliang
	 * @time 2105-05-26 上午9:01:10
	 */
	@Override
	public List<Map<String, Object>> getDistrictsByCityId(int cityId) {
		return deliveryDao.getDistrictsByCityId(cityId);
	}
	
	/**
	 * 发送大客户订单出库短信
	 * @param json JSON格式的POP订单号数据，如：{popType:1,orderId:"9527460481"}
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:-1,message:'错误信息'}
	 * @author likaige
	 * @create 2015年6月1日 上午9:42:04
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public String sendPopShortMessage(String json) throws Exception {
		JSONObject jsonObj = JSONObject.fromObject(json);
		String orderCode = jsonObj.getString("orderId");
		int popType = jsonObj.getInt("popType");
		
		if(popType == PopBussiness.POP_JD){
			//获取POP订单信息
			PopOrderInfo popOrderInfo = deliveryDao.getPopOrderInfo(orderCode);
			
			//无订单信息
			if(popOrderInfo == null){
				return this.createJsonResult(-1, "根据订单号未查询到订单信息");
			}
			
			//把发送状态标记成：2:发送成功(已成功发送到POP)
			deliveryDao.updatePopOrderInfoSendStatusByPopOrderCode(orderCode, 2);
		}else{
			//把发送状态标记成：3:已成功发送到快递公司
			int count = deliveryDao.updateDeliverRelationStatusByOrderCode(orderCode, DeliverRelationBean.STATUS3);
			if(count <= 0){
				return this.createJsonResult(-1, "未更新数据："+orderCode);
			}
		}
		
		return this.createJsonResult(200, "成功");
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
//	@Transactional(rollbackFor=Exception.class)
//	public String repairPopOrderInfo(String json) throws Exception {
//		JSONArray jsons = JSONArray.fromObject(json);
//		
//		//订单号为空
//		if(jsons==null || jsons.isEmpty()){
//			return this.createJsonResult(-1, "订单数据为空");
//		}
//		
//		List<String> orderCodeList = new ArrayList<String>();
//		List<String> popOrderCodeList = new ArrayList<String>();
//		for(int i=0; i<jsons.size(); i++){
//			JSONObject jsonObj = jsons.getJSONObject(i);
//			int orderId = jsonObj.getInt("orderId");
//			String orderCode = jsonObj.getString("orderCode");
//			String popOrderCode = jsonObj.getString("popOrderCode");
//			orderCodeList.add(orderCode);
//			popOrderCodeList.add(popOrderCode);
//			
//			//修补deliver_info_pop,此时user_order中还没有对应的订单数据(事务还未提交)
//			deliveryDao.repairDeliverInfoPop(orderId, orderCode, popOrderCode);
//		}
//		
//		//修补pop_order_info
//		deliveryDao.repairPopOrderInfo(orderCodeList, popOrderCodeList);
//		
//		return this.createJsonResult(200, "成功");
//	}
	
	/**
	 * 发送大客户订单出库短信
	 * @throws Exception
	 * @author likaige
	 * @create 2015年6月3日 上午10:30:32
	 */
	@Override
	public void sendPopShortMessage() throws Exception {
		//获取需要发送短信的订单信息
		List<voOrder> orderList = deliveryDao.getNeedSendShortMessageUserOrderList();
		
		//没有需要发短信的订单
		if(orderList.isEmpty()){
			log.info("没有需要发短信的POP订单");
			return;
		}
		
		//发短信
		List<String> deliverCodeList = new ArrayList<String>();
		for(voOrder order : orderList){
			this.sendSMS(order);
			deliverCodeList.add(order.getPackageNum());
		}
		
		//标记为短信已发送
		deliveryDao.updatePopOrderInfoMessageSendStatus(deliverCodeList, 1);
	}
	
	//大客户出库短信
	private void sendSMS(voOrder order) {
		DbOperation dbOp_sms = new DbOperation(DbOperation.DB_SMS);
		ISMSService smsService = ServiceFactory.createSMSService(IBaseService.CONN_IN_SERVICE, dbOp_sms);
		try{
			String msg = "您好，您的商品已发货，包裹单号："+order.getPackageNum()
					+"，您可拨打快递热线：4008869499进行催投查询，感谢您的支持！";
			SenderSMS3.send(0, order.getPhone(), msg);
			
			SendMessage3Bean sendMessage3 = new SendMessage3Bean();
			sendMessage3.setOrderId(order.getId());
			sendMessage3.setOrderCode(order.getCode());
			sendMessage3.setPackageNum(order.getPackageNum());
			sendMessage3.setMobile(order.getPhone());
			sendMessage3.setSendDatetime(DateUtil.getNow());
			sendMessage3.setSendUserId(0);
			sendMessage3.setSendUsername("接口发送");
			sendMessage3.setContent(msg);
			smsService.addSendMessage3(sendMessage3);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			smsService.releaseAll();
		}
	}
	
	/**
	 * 修补POP订单信息
	 * @author likaige
	 * @create 2015年6月6日 下午1:06:18
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void repairPopOrderInfo() throws Exception {
		//获取需要补订单号的数据
		List<PopOrderInfo> orderList = deliveryDao.getNeedRepairPopOrderInfo();
		
		//没有需要补的订单
		if(orderList.isEmpty()){
			log.info("没有需要修补的POP订单！");
			return ;
		}
		
		//修补数据
		List<String> orderCodeList = new ArrayList<String>();
		List<String> popOrderCodeList = new ArrayList<String>();
		for(PopOrderInfo order : orderList){
			orderCodeList.add(order.getOrderCode());
			popOrderCodeList.add(order.getPopOrderCode());
			
			//修补deliver_info_pop
			deliveryDao.repairDeliverInfoPop(order.getOrderId(), order.getOrderCode(), order.getPopOrderCode());
		}
		//修补pop_order_info
		deliveryDao.repairPopOrderInfo(orderCodeList, popOrderCodeList);
		
		//处理已妥投的订单
		this.doDeliveredPopOrder(orderList);
	}
	
	//处理已妥投的订单
	private void doDeliveredPopOrder(List<PopOrderInfo> orderList) throws Exception {
		for(PopOrderInfo order : orderList){
			if(order.getStatus() == DeliverOrderInfoBean.DELIVER_STATE7){
				//更新订单状态
				deliveryOrderFacade.updateOrderStatus(order.getOrderId(), order.getStatus());
			}
		}
	}

	/**
	 * POP订单监控
	 * @throws Exception
	 * @author likaige
	 * @create 2015年7月28日 下午4:44:14
	 */
	@Override
	public void popOrderMonitor() throws Exception {
		//获取发送失败的面单数量
		//Date date = DateUtils.addHours(new Date(), -1);
		int count = deliveryDao.getSendFailWaybillCount();
		
		//没有发送失败的面单
		if(count <= 0){
			log.info("没有发送失败的面单！");
			return ;
		}
		
		//获取其中的一个POP订单号
		String popOrderCode = deliveryDao.getSendFailWaybillPopOrderCode();
		
		//发送监控短信
		StringBuilder msg = new StringBuilder();
		msg.append("面单下发接口出现异常，异常单号：").append(popOrderCode);
		if(count > 1){
			msg.append("(共").append(count).append("单)");
		}
		String[] phoneList = {"13718431535"};
		for(String phone : phoneList){
			SenderSMS3.send(0, phone, msg.toString());
		}
	}
	
	/**
	 * 更改订单状态  当当前时间减去订单创建时间>=1小时时将订单状态改为-1即发送失败
	 * @author lml
	 * @create 2015-08-11 10:55:01
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void updateOrderStatusToFail() throws Exception {
		deliveryDao.updateOrderStatusToFail();
	}

	
	@Override
	public void markNoBuyOrderPopInfo() throws Exception {
		List<Integer> result = new ArrayList<Integer>();
		List<Map<String, Object>> mapList = deliveryDao.queryNoBuyOrderPopInfoId();
		for(Map<String, Object> map : mapList){
			if(Integer.parseInt(map.get("bopId")+"") == -1){
				result.add(Integer.parseInt(map.get("poiId")+""));
			}
		}
		if(!result.isEmpty()){
			StringBuilder str = new StringBuilder();
			for(Integer id : result){
				if(str.length()>0){
					str.append(",");
				}
				str.append(id);
			}
			deliveryDao.updateOrderStatusToSpecial(str.toString(), -3);
		}
	}
	
}
