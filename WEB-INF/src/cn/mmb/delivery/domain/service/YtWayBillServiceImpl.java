package cn.mmb.delivery.domain.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import mmb.common.dao.mappers.UserOrderMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mmb.framework.utils.MD5;

import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.util.DateUtil;
import adultadmin.util.HttpKit;
import adultadmin.util.MD5Util;
import adultadmin.util.StringUtil;
import cn.mmb.delivery.domain.model.WayBill;
import cn.mmb.delivery.domain.model.WayBillTrace;
import cn.mmb.delivery.domain.model.YtWayBill;
import cn.mmb.delivery.domain.model.YtWayBillTrace;
import cn.mmb.delivery.domain.model.vo.BasicParamBean;
import cn.mmb.delivery.domain.model.vo.TraceInfo;
import cn.mmb.delivery.infrastructrue.adp.WayBillAdaper;
import cn.mmb.delivery.infrastructrue.adp.WayBillAdaperFactory;
import cn.mmb.delivery.infrastructrue.persistence.WayBillMapper;
import cn.mmb.orderpackage.infrastructrue.persistence.PackageMapper;
@Service( value= "YtWayBillServiceImpl")
public class YtWayBillServiceImpl implements WayBillService {
	
	private static Log log = LogFactory.getLog("debug.Log");
	
	@Resource
	private WayBillMapper wayBillMapper;
	
	@Resource
	private PackageMapper packageMapper;
	
	@Resource
	private UserOrderMapper userOrderMapper;
	
	@Resource(name ="ytConfig")
	private BasicParamBean basicParamBean;
	
	@Resource
	private DeliveryOrderService deliveryOrderService;
	
	@Resource
	private WayBillAdaperFactory wayBillAdaperFactory;
	
//	private JasperReport jasperReport;
	
//	private JasperReport jasperDqReport;
	
	@Override
	public List<WayBill> getDeliverRelation(int deliverId) throws Exception {
		List<Map<String,Object>> list=wayBillMapper.getDeliverRelationList(" status =0 and deliver_id="+deliverId);
		List<WayBill> result = new ArrayList<WayBill>();
		WayBill wayBill = null;
		for(Map<String,Object> map:list){
			String orderCode = (String) map.get("order_code");
			Map<String,Object> mapOrder =wayBillMapper.getUserOrderInfo(" uo.code='"+orderCode+"'");
			if(mapOrder == null || mapOrder.isEmpty()){
				continue;
			}
			wayBill = new YtWayBill();
			if(Integer.parseInt(String.valueOf(mapOrder.get("stock_area")))==4){
				wayBill.setClientId(basicParamBean.getClientId());
				wayBill.setCusomerId(basicParamBean.getClientId());
			}else{
				wayBill.setClientId(basicParamBean.getClientIdcd());
				wayBill.setCusomerId(basicParamBean.getClientIdcd());
			}
			wayBill.setLogisticProviderId("YTO");
			wayBill.setOrderCode(orderCode);
			wayBill.setOrderType(mapOrder.get("buy_mode")+"");
			wayBill.setPostCode(mapOrder.get("postcode")+"");
			wayBill.setServiceType("1");
			wayBill.setName(mapOrder.get("name")+"");
			wayBill.setMobile(StringUtil.concealPhone(mapOrder.get("phone")+""));
			wayBill.setProv(mapOrder.get("prov")+"");
			wayBill.setStockArea(Integer.parseInt(String.valueOf(mapOrder.get("stock_area"))));
			String city="";
			if("null".equals(mapOrder.get("area")+"")){
				city = mapOrder.get("city")+"";
			}else{
				city =  mapOrder.get("city")+""+","+mapOrder.get("area")+"";
			}
			wayBill.setCity(city);
			wayBill.setAddress(mapOrder.get("address")+"");
			wayBill.setItemName("买卖宝商品");
			wayBill.setNumber("1");
			wayBill.setItemsValue(mapOrder.get("dprice")+"");
			result.add(wayBill);
		}
		return result;
	}

	@Override
	public List<String> sendWayBillInfo(List<WayBill> sendData)throws Exception {
		
		WayBillAdaper wayBillAdaper =wayBillAdaperFactory.create("yt");

		String xml="";
		String partnerId = "";
		String clientId = "";
		String type = basicParamBean.getType();
		
		/**
		 * 遍历发送信息
		 */
		List<String> list = new ArrayList<String>();//用于存放返回的xml的集合
		for(WayBill waybill:sendData){
			if(waybill.getStockArea()==4){
				partnerId = basicParamBean.getPartnerid();
				clientId = basicParamBean.getClientId();
			}else{
				partnerId = basicParamBean.getPartneridcd();
				clientId = basicParamBean.getClientIdcd();
			}
			String xmldata=wayBillAdaper.parseWayBillToXml(waybill);
			if(log.isWarnEnabled()){
				log.warn(DateUtil.getNow()+"-快递公司[圆通]-发送封装数据："+xmldata);
			}
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("logistics_interface", xmldata);
			params.put("data_digest",MD5Util.getKeyedDigestForYt(xmldata+partnerId));
			params.put("type",type);
			params.put("clientId",clientId);
			xml = HttpKit.post(basicParamBean.getUrl(),params);
			if(log.isWarnEnabled()){
				log.warn(DateUtil.getNow()+"-快递公司[圆通]-接收封装数据："+xml);
			}
			list.add(xml);
		}
		return list;
	}


	@Override
	@Transactional
	public void updateDeliverRelation(int deliverId, List<WayBill> list)throws DataAccessException {
		wayBillMapper.addDeliverRelationInfo(deliverId, list);
		wayBillMapper.updateDeliverRelation(deliverId,list);
	}


	@Override
	public List<WayBill> parseToWayBill(List<WayBill> sendData, List<String> xmlList) {
		WayBillAdaper wayBillAdaper =WayBillAdaperFactory.getWayBillAByServiceName("YtWayBillAdaperImpl");
		return wayBillAdaper.parseXmlToWayBill(xmlList);
	}

	/** 
	 * @Description: 调用圆通走件流程查询接口，API参见圆通开发者文档；
	 * 网址 http://open.yto.net.cn:8007/OpenPlatform/doc#Menu51
	 * @return YtoWayBillResponse 返回类型 
	 * @param param list长度最大不能超过10，圆通接口规定
	 * @author 叶二鹏
	 * @date 2015年8月6日 上午10:06:53 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getWayBillTrace() {
		String timestamp = DateUtil.getNow();
		Map<String, Object> params = initYtWayBillTraceParam(timestamp);
		WayBillTrace wayBillTrace = new YtWayBillTrace();
		List<WayBill> list = wayBillMapper.getYtoWayBillCode();
		wayBillTrace.setWayBill(list);
		wayBillTrace.setTraceInfo(new ArrayList<TraceInfo>());
		if (null != list && !list.isEmpty()) {
			List<List<WayBill>> splitByTen = this.splitList(list, 10);
			for (List<WayBill> param : splitByTen) {
				WayBillAdaper adaper = wayBillAdaperFactory.create("yt");
				String traceParams = adaper.parseWayBillTraceParam(param);
				params.put("param", traceParams);
				try {
					String result = HttpKit.post(basicParamBean.getYtWaybillTraceUrl(), params);
//					log.info("调用圆通物流接口result："+result);
					if (!(result.indexOf("Response")!=-1)) {
						WayBillTrace traceResult = adaper.parseJsonToWayBillTrace(result);
						wayBillTrace.getTraceInfo().addAll(traceResult.getTraceInfo());
					}else{
						log.info("调用圆通物流接口param："+traceParams);
						log.info("调用圆通物流接口result："+result);
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.info("调用圆通物流接口param："+traceParams);
				}
			}
		}
		return (T) wayBillTrace;
	}

	/** 
	 * @Description: 初始化调用圆通接口参数
	 * @return Map<String, Object> 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月12日 下午1:19:53 
	 */
	private Map<String, Object> initYtWayBillTraceParam(String timestamp) {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder signs = new StringBuilder();
		signs.append(basicParamBean.getYtCommonPrivateKey());//私钥
		signs.append("app_key"+basicParamBean.getYtCommonAppKey());//app_key
		signs.append("formatjson");//format
		signs.append("method"+basicParamBean.getYtWaybillTraceMethod());//method
		signs.append("timestamp"+timestamp);//timestamp
		signs.append("user_id"+basicParamBean.getYtCommonUserId());//user_id
		signs.append("v1.0");//v
		String sign = MD5.md5s(signs.toString()).toUpperCase();
		
		params.put("sign", sign);
		params.put("user_id", basicParamBean.getYtCommonUserId());
		params.put("app_key", basicParamBean.getYtCommonAppKey());
		params.put("format", "json");
		params.put("method", basicParamBean.getYtWaybillTraceMethod());
		params.put("timestamp", timestamp);
		params.put("v", "1.0");
		return params;
	}
	
	/** 
	 * @Description: 单号按照十个分组
	 * @return List<List<WayBillTraceParam>> 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月10日 下午3:42:14 
	 */
	private List<List<WayBill>> splitList(List<WayBill> list, int spNum) {
		int size = list.size()/spNum;
		List<List<WayBill>> rs = new ArrayList<List<WayBill>>();
		for (int i = 0; i < size; i++) {
			List<WayBill> rsc = new ArrayList<WayBill>();
			for (int j = 0; j < spNum; j++) {
				rsc.add(list.get(spNum*i+j));
			}
			rs.add(rsc);
		}
		
		int last = list.size()%spNum;
		List<WayBill> rsc = new ArrayList<WayBill>();
		for (int j = 0; j < last; j++) {
			rsc.add(list.get(spNum*size+j));
		}
		if (!rsc.isEmpty()) {
			rs.add(rsc);
		}
		return rs;
	}
	
	@Override
	public boolean printWayBill(WayBill wb)throws Exception{
		// TODO Auto-generated method stub
		boolean result = false;
		/*
		WayBillAdaper wayBillAdaper =wayBillAdaperFactory.create("yt");
		Map<String,Object> parameters = wayBillAdaper.parseWayBillToMap(wb);
		JasperPrint jasperPrint = null;  
		try{
			File reportFile = new File(wb.getReportPath());
			
			if(jasperReport == null){
				jasperReport = (JasperReport) JRLoader.loadObject(reportFile); 
			}
			
			// 将集合对象数据填充到JasperReport中.  
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters);
            
            //直接打印    参数fasle直接调用默认打印机，true表示选择打印机框
            result = JasperPrintManager.printReport(jasperPrint,false);
		}catch(Exception e){
			throw e;
		}
		*/
		return result;
	}


	@Override
	public boolean printWayBillDq(WayBill wb) throws Exception {
		// TODO Auto-generated method stub
		boolean result = false;
		/*
		WayBillAdaper wayBillAdaper =wayBillAdaperFactory.create("yt");
		Map<String,Object> parameters = wayBillAdaper.parseDQWayBillToMap(wb);
		JasperPrint jasperPrint = null;  
		try{
			File reportFile = new File(wb.getReportPath());
			
			if(jasperDqReport == null){
				jasperDqReport = (JasperReport) JRLoader.loadObject(reportFile); 
			}
			
			// 将集合对象数据填充到JasperReport中.  
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters);
            
            //直接打印    参数fasle直接调用默认打印机，true表示选择打印机框
            result = JasperPrintManager.printReport(jasperPrint,false);
		}catch(Exception e){
			throw e;
		}
		*/
		return result;
	}

	@Override
	public List<WayBillTrace> getNeedAddWayBillInfo(WayBillTrace wayBillTrace)
			throws Exception {
		List<WayBillTrace> InfoRsList = new ArrayList<WayBillTrace>();
		List<WayBillTrace> traceList = wayBillMapper.getWayBillTrace(wayBillTrace.getWayBill());
		//去掉数据库已存在的物流信息
		for (WayBillTrace tr : traceList) {
			for (TraceInfo tri : tr.getTraceInfo()) {
				tri.setDeliverNo(tr.getDeliverNo());
				wayBillTrace.getTraceInfo().remove(tri);
			}
		}
		//将去掉已存在物流信息后需要保存的物流数据封装
		for (WayBillTrace tr : traceList) {
			tr.getTraceInfo().clear();
			for (TraceInfo tri : wayBillTrace.getTraceInfo()) {
				if (tr.getDeliverNo().equals(tri.getDeliverNo())) {
					tri.setStatus(this.getDeliverStatusFromInfo(tri.getInfo()));
					tr.getTraceInfo().add(tri);
				}
			}
			//物流信息按时间排序
			Collections.sort(tr.getTraceInfo(), new Comparator<TraceInfo>(){
				@Override
				public int compare(TraceInfo arg0,
						TraceInfo arg1) {
					long compare = DateUtil.getTime(arg0.getTime(), "yyyy-MM-dd HH:mm:ss") - 
							DateUtil.getTime(arg1.getTime(), "yyyy-MM-dd HH:mm:ss");
					return compare>0?1:-1;
				}
			});
			//剩余需要更新的物流信息
			if (!tr.getTraceInfo().isEmpty()) {
				//状态取最新物流的
				tr.setStatus(this.getDeliverStatusFromInfo(tr.getTraceInfo().get(tr.getTraceInfo().size() - 1).getInfo()));
				InfoRsList.add(tr);
			}
		}
		return InfoRsList;
	}
	
	/** 
	 * @Description: 通过物流信息解析物流状态
	 * @return int 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月7日 下午2:51:38 
	 */
	private int getDeliverStatusFromInfo(String info) {
		for(Map.Entry<String, Integer> entry:TraceInfo.ytDeliverStatus.entrySet()){
			if (info.indexOf(entry.getKey()) != -1) {
				return entry.getValue();
			}
		} 
		//默认返回状态2，在途
		return 2;
	}

	@Override
	public String updateWayBillInfo(List<WayBillTrace> wayBillTrace) {
		String result = "";
		for (WayBillTrace deliver : wayBillTrace) {
			try {
				this.saveWayBillTraceInfo(deliver);
			} catch (Exception e) {
				result += "更新物流信息失败【"+deliver.getDeliverNo()+"】 msg"+ e.toString() + "\n";
				e.printStackTrace();
			}
		}
		return StringUtils.isNotBlank(result)?result:"OK";
	}

	/** 
	 * @Description: 
	 * @return String 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月12日 下午5:58:18 
	 */
	@Transactional(rollbackFor=Exception.class)
	private void saveWayBillTraceInfo(WayBillTrace deliver) throws Exception {
		//更新物流状态
		StringBuilder updateDeliver = new StringBuilder();
		TraceInfo trace = deliver.getTraceInfo().get(deliver.getTraceInfo().size() - 1);
		//deliver.getTraceInfo()要确保是按照时间排序的，最新的物流信息在最后
		updateDeliver.append("update deliver_order set deliver_info='").append(trace.getInfo());
		updateDeliver.append("',deliver_state=").append(deliver.getStatus());
		if (deliver.getStatus() == 7) {
			updateDeliver.append(",receive_time='").append(trace.getTime()).append("'");
		}
		if (deliver.getStatus() == 8) {
			updateDeliver.append(",return_time='").append(trace.getTime()).append("'");
		}
		updateDeliver.append(" where id=").append(deliver.getDeliverOrderId());
		wayBillMapper.updateDeliverOrder(updateDeliver.toString());
		//添加物流信息
		wayBillMapper.insertDeliverOrderInfo(deliver);
		
		//已妥投的修改订单状态和核对包裹签收时间
		if (deliver.getStatus() == 7) {
			Map<String, Object> userOrder = userOrderMapper.getUserOrderInfo(deliver.getOrderId());
			StringBuilder updateOrder = new StringBuilder("update user_order set status=14 where status=6 and id=").append(deliver.getOrderId());
			if(userOrderMapper.updateOrderStatus(updateOrder.toString()) > 0) {
				//添加日志
				OrderAdminLogBean log = new OrderAdminLogBean();
				log.setType(OrderAdminLogBean.ORDER_ADMIN_PROP);
				log.setUserId(0);
				log.setUsername("system");
				log.setOrderId(deliver.getOrderId());
				log.setOrderCode(userOrder.get("code").toString());
				log.setCreateDatetime(DateUtil.getNow());
				log.setContent("[订单状态:6->14]");
				userOrderMapper.addOrderAdminLog(log);
			}
			StringBuilder updateAudit = new StringBuilder("update audit_package set receive_datetime='").append(deliver.getTraceInfo().get(deliver.getTraceInfo().size() - 1).getTime())
					.append("' where order_id=").append(deliver.getOrderId());
			packageMapper.updateAuditPackage(updateAudit.toString());
		}
	}

	@Override
	public int cancelWayBill(int deliverId,String orderCode)throws Exception {

		List<String> list = new ArrayList<String>();
//		String packageCode =null;
		List<Map<String,Object>> deliverRelaList=wayBillMapper.getDeliverRelationList(" status in (0,1,3) and deliver_id="+deliverId+" and order_code='"+orderCode+"'");
		if(deliverRelaList.isEmpty()){
			throw new RuntimeException("订单["+orderCode+"]没有关联运单号的信息!");
		}
		String id="";
		for(Map<String,Object> map:deliverRelaList){
//			packageCode = (String) map.get("package_code");
			id=String.valueOf(map.get("id"));
		}
		list.add(id);
		/**
		 * 封装结果
		 */
		List<WayBill> resultWayBill = this.parseToWayBill(list);
		/**
		 * 更新关联表
		 */
		int result =this.updateDeliverRelationForStatus(deliverId, resultWayBill,"2");
		
		return result;
	}

	@Override
	public List<WayBill> parseToWayBill(List<String> list) throws Exception {
		WayBillAdaper wayBillAdaper =WayBillAdaperFactory.getWayBillAByServiceName("YtWayBillAdaperImpl");
		return wayBillAdaper.parseJsonToWayBillForCancel(list);
	}

	@Override
	public int updateDeliverRelationForStatus(int deliverId, List<WayBill> list,String status) throws DataAccessException {
		return wayBillMapper.updateDeliverRelationForStatus(deliverId,list,status);
		
	}

	@Override
	public List<WayBill> getNeedWayBillInfo(int deliverId,String orderCode) throws Exception {
		List<WayBill> result = new ArrayList<WayBill>();
		WayBill wayBill = null;
		Map<String,Object> mapOrder =wayBillMapper.getUserOrderInfo(" uo.code='"+orderCode+"'");
		wayBill = new YtWayBill();
		if(Integer.parseInt(String.valueOf(mapOrder.get("stock_area")))==4){
			wayBill.setClientId(basicParamBean.getClientId());
			wayBill.setCusomerId(basicParamBean.getClientId());
		}else{
			wayBill.setClientId(basicParamBean.getClientIdcd());
			wayBill.setCusomerId(basicParamBean.getClientIdcd());
		}
		wayBill.setLogisticProviderId("YTO");
		wayBill.setOrderCode(orderCode);
		wayBill.setOrderType(mapOrder.get("buy_mode")+"");
		wayBill.setPostCode(mapOrder.get("postcode")+"");
		wayBill.setServiceType("1");
		wayBill.setName(mapOrder.get("name")+"");
		wayBill.setMobile(StringUtil.concealPhone(mapOrder.get("phone")+""));
		wayBill.setProv(mapOrder.get("prov")+"");
		wayBill.setStockArea(Integer.parseInt(String.valueOf(mapOrder.get("stock_area"))));
		String city="";
		if("null".equals(mapOrder.get("area")+"")){
			city = mapOrder.get("city")+"";
		}else{
			city =  mapOrder.get("city")+""+","+mapOrder.get("area")+"";
		}
		wayBill.setCity(city);
		wayBill.setAddress(mapOrder.get("address")+"");
		wayBill.setItemName("买卖宝商品");
		wayBill.setNumber("1");
		wayBill.setItemsValue(mapOrder.get("dprice")+"");
		result.add(wayBill);
		return result;
	}

	@Override
	public void addDeliverRelation(int deliverId, List<WayBill> list) throws DataAccessException {
		wayBillMapper.addDeliverRelationInfo(deliverId, list);
		wayBillMapper.addDeliverRelation(deliverId,list);
	}

}
