package cn.mmb.delivery.infrastructrue.adp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.springframework.stereotype.Service;

import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import cn.mmb.delivery.domain.model.WayBill;
import cn.mmb.delivery.domain.model.WayBillTrace;
import cn.mmb.delivery.domain.model.YtWayBill;
import cn.mmb.delivery.domain.model.YtWayBillTrace;
import cn.mmb.delivery.domain.model.vo.SenderBean;
import cn.mmb.delivery.domain.model.vo.TraceInfo;

import com.mmb.framework.support.SpringHandler;

@Service(value="YtWayBillAdaperImpl")
public class YtWayBillAdaperImpl implements WayBillAdaper {

	private static Log log = LogFactory.getLog("debug.Log");
	
	SenderBean senderBean=null;
	@Override
	public String parseWayBillToXml(WayBill waybill) {
		if("无锡".equals(ProductStockBean.areaMap.get(waybill.getStockArea()))){
			senderBean= SpringHandler.getBean("sender_wx");
		}else{
			senderBean= SpringHandler.getBean("sender_cd");
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("<RequestOrder>");
		
		sb.append("<clientID>");
		sb.append(waybill.getClientId());
		sb.append("</clientID>");
		
		sb.append("<logisticProviderID>");
		sb.append("YTO");
		sb.append("</logisticProviderID>");
		
		sb.append("<customerId>");
		sb.append(waybill.getClientId());
		sb.append("</customerId>");
		
		sb.append("<txLogisticID>");
		sb.append(StringUtil.replaceStr(waybill.getOrderCode()));//mmb订单号
		sb.append("</txLogisticID>");
		
		sb.append("<tradeNo>");
		sb.append("");
		sb.append("</tradeNo>");
		
		sb.append("<totalServiceFee>");
		sb.append("0.0");
		sb.append("</totalServiceFee>");
		
		sb.append("<codSplitFee>");
		sb.append("0.0");
		sb.append("</codSplitFee>");
		
		sb.append("<type>");
		sb.append("0");
		sb.append("</type>");
		
		sb.append("<orderType>");
		if("0".equals(waybill.getOrderType())){
			sb.append("0");//购买方式 0：cod 1:在线支付
		}else{
			sb.append("1");//购买方式 0：cod 1:在线支付
		}
		sb.append("</orderType>");
		
		sb.append("<serviceType>");
		sb.append("1");//服务类型 1 上门揽收
		sb.append("</serviceType>");
		
		sb.append("<flag>");
		sb.append("0");
		sb.append("</flag>");
		
		sb.append("<sender>");//发件人(公司)，从配置文件中读取
			sb.append("<name>");
			sb.append(StringUtil.replaceStr(senderBean.getName()));
			sb.append("</name>");
			
			sb.append("<postCode>");
			sb.append(StringUtil.replaceStr(senderBean.getPostCode()+""));
			sb.append("</postCode>");
			
			sb.append("<phone>");
			sb.append(StringUtil.replaceStr(senderBean.getPhone()));
			sb.append("</phone>");
			
			sb.append("<mobile>");
			sb.append("");
			sb.append("</mobile>");
			
			sb.append("<prov>");
			sb.append(StringUtil.replaceStr(senderBean.getProv()));
			sb.append("</prov>");
			
			sb.append("<city>");
			sb.append(StringUtil.replaceStr(senderBean.getCity()));//发件人的地址，如果有区，中间要用 ","隔开
			sb.append("</city>");
			
			sb.append("<address>");
			sb.append(StringUtil.replaceStr(senderBean.getAddress()));
			sb.append("</address>");
		sb.append("</sender>");
		
		sb.append("<receiver>");
			sb.append("<name>");
			sb.append(StringUtil.replaceStr(waybill.getName()));
			sb.append("</name>");
			
			sb.append("<postCode>");
			sb.append(StringUtil.replaceStr(waybill.getPostCode()));
			sb.append("</postCode>");
			
			sb.append("<phone>");
			sb.append("");
			sb.append("</phone>");
			
			sb.append("<mobile>");
			sb.append(StringUtil.replaceStr(waybill.getMobile()));
			sb.append("</mobile>");
			
			sb.append("<prov>");
			sb.append(StringUtil.replaceStr(waybill.getProv()));
			sb.append("</prov>");
			
			sb.append("<city>");
			sb.append(StringUtil.replaceStr(waybill.getCity()));
			sb.append("</city>");
			
			sb.append("<address>");
			sb.append(StringUtil.replaceStr(waybill.getAddress()));
			sb.append("</address>");
		sb.append("</receiver>");
		
		sb.append("<sendStartTime>");
		sb.append("");
		sb.append("</sendStartTime>");
		
		sb.append("<sendEndTime>");
		sb.append("");
		sb.append("</sendEndTime>");
		
		sb.append("<goodsValue>");
		sb.append("0");
		sb.append("</goodsValue>");
		
		sb.append("<itemsValue>");//代收金额，
		if(waybill.getOrderType().equals("0")){//货到付款方式
			sb.append(waybill.getItemsValue());
		}else{
			sb.append("0.0");
		}
		sb.append("</itemsValue>");
		
		sb.append("<agencyFund>");
		if(waybill.getOrderType().equals("0")){//和itemValue值保持相同
			sb.append(waybill.getItemsValue());
		}else{
			sb.append("0.0");
		}
		sb.append("</agencyFund>");
		
		sb.append("<items>");
		sb.append("<item>");
			sb.append("<itemName>");
			sb.append(StringUtil.replaceStr(waybill.getItemName()));
			sb.append("</itemName>");
			
			sb.append("<number>");
			sb.append("1");
			sb.append("</number>");
			
			sb.append("<itemValue>");
			sb.append("0.0");
			sb.append("</itemValue>");
		sb.append("</item>");
	sb.append("</items>");
		
		sb.append("<insuranceValue>");
		sb.append("0.0");
		sb.append("</insuranceValue>");
		
		sb.append("<special>");
		sb.append("0");
		sb.append("</special>");
		
		sb.append("<remark>");
		sb.append("");
		sb.append("</remark>");
		
		sb.append("</RequestOrder>");
		return sb.toString();
	}

	@Override
	public List<WayBill> parseXmlToWayBill(List<String> xmlList){
		List<WayBill> list = new ArrayList<WayBill>();
		
		String mailNo = null;//运单号
		String position = null;//大笔
		String orderCode = null;//mmb订单编号
		for(String xml:xmlList){
			WayBill wayBill = new YtWayBill();
			try {
				Document doc = DocumentHelper.parseText(xml);
				Element root = doc.getRootElement();
				Iterator<?> iter = root.elementIterator("orderMessage");
				// 遍历orderMessage节点  
		        while (iter.hasNext()) {
		            Element recordEle = (Element) iter.next();  

		            mailNo =recordEle.elementTextTrim("mailNo");
		            position =recordEle.elementTextTrim("bigPen");
		            orderCode =recordEle.elementTextTrim("txLogisticID");
		        }
				
			} catch (DocumentException e1) {
				log.error(DateUtil.getNow()+"-快递公司[圆通]-解析xml失败："+xml);
				e1.printStackTrace();
			} catch(Exception e){
				log.error(DateUtil.getNow()+"-快递公司[圆通]-没有获取有效信息："+xml);
				e.printStackTrace();
			}
			
			if(mailNo!=null){
				 wayBill.setMailNo(mailNo);//获取指定节点的值
			}else{
				 if(log.isWarnEnabled()){
		        		log.warn(DateUtil.getNow()+"-快递公司[圆通]-解析数据异常：该订单号"+orderCode+"的运单号没有获取到！");
				 }
			}
			if(position!=null){
				  wayBill.setPosition(position);
			}else{
				 if(log.isWarnEnabled()){
		        		log.warn(DateUtil.getNow()+"-快递公司[圆通]-解析数据异常：该订单号"+orderCode+"的大笔没有获取到！");
				 }
			}
			if(orderCode!=null){
				 wayBill.setOrderCode(orderCode);//获取指定节点的值
			}
			list.add(wayBill);
		}
		
		return list;
	}

	@Override
	public Map<String, Object> parseWayBillToMap(WayBill wb) {
		// TODO Auto-generated method stub
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("money",wb.getItemsValue());
		parameters.put("code",wb.getPosition());
		parameters.put("receive_name",wb.getName());
		parameters.put("receive_mobile",wb.getMobile());
		parameters.put("receive_address",wb.getAddress());
		parameters.put("sender_name",wb.getSender());
		parameters.put("sender_address",wb.getSenderAddress());
		parameters.put("sender_mobile",wb.getSenderMobile());
		parameters.put("send_address",wb.getSenderAddress());
		parameters.put("send_user",wb.getSender());
		parameters.put("send_mobile",wb.getSenderMobile());
		parameters.put("send_code",wb.getDeliverId());
		parameters.put("express_com",wb.getDeliverName());
		parameters.put("type",wb.getOrderTypeName());
		parameters.put("custom_address",wb.getAddress());
		parameters.put("custom_name",wb.getName());
		parameters.put("custom_zip_code",wb.getPostCode());
		parameters.put("order_price",wb.getdPrice());
		parameters.put("custom_mobile",wb.getMobile());
		parameters.put("package_weight",wb.getWeight());
		parameters.put("up_code",wb.getMailNo());
		parameters.put("down_code",wb.getMailNo());
		parameters.put("order_id",wb.getOrderCode());
		parameters.put("mmbImgPath", wb.getMmbImgPath());
		parameters.put("ytoImgPath", wb.getYtoImgPath());
		return parameters;
	}
	
	@Override
	public String parseWayBillToJson(WayBill waybill) {
		return null;
	}

	@Override
	public Map<String, Object> parseDQWayBillToMap(WayBill wb) {
		// TODO Auto-generated method stub
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("code1",wb.getMailNo());
		parameters.put("deliveryCompany",wb.getDeliverName());
		parameters.put("customerAddress",wb.getProv());
		parameters.put("detailAddress",wb.getAddress());
		parameters.put("username",wb.getName());
		parameters.put("money",wb.getdPrice());
		parameters.put("zipCode",wb.getPostCode());
		parameters.put("telephone",wb.getMobile());
		parameters.put("weight",wb.getWeight());
		parameters.put("address",wb.getSenderAddress());
		parameters.put("postName",wb.getSender());
		parameters.put("postTelephone",wb.getSenderMobile());
		parameters.put("expressCompany",wb.getDeliverName());
		parameters.put("type",wb.getOrderTypeName());
		parameters.put("custAddress",wb.getAddress());
		parameters.put("order_id",wb.getOrderCode());
		parameters.put("send_code",wb.getDeliverId());
		parameters.put("dqImg",wb.getMmbImgPath());
		return parameters;
	}

	@SuppressWarnings("unchecked")
	@Override
	public WayBillTrace parseXmlToWayBillTrace(String xml) throws Exception {
		WayBillTrace wayBillTrace = new YtWayBillTrace();
		wayBillTrace.setSucess(false);
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		List<DefaultElement> content = root.content();
		for (DefaultElement c : content) {
			if (c.getName().equals("reason")) {
				wayBillTrace.setResultInfo(c.getText());
				break;
			}
		}
		return wayBillTrace;
	}

	@Override
	public WayBillTrace parseJsonToWayBillTrace(String json) throws Exception {
		WayBillTrace wayBillTrace = new YtWayBillTrace();
		ObjectMapper mapper= new ObjectMapper();
		wayBillTrace.setSucess(true);
		json = json.replaceAll("Waybill_No", "deliverNo");
		json = json.replaceAll("Upload_Time", "time");
		json = json.replaceAll("ProcessInfo", "info");
		TraceInfo[] s = mapper.readValue(json, TraceInfo[].class);
		List<TraceInfo> infos = new ArrayList<TraceInfo>();
		for (TraceInfo info : s) {
			long tim = DateUtil.getTime(info.getTime(), "yyyy-MM-dd HH:mm:ss");
			if (tim == 0) {
				tim = DateUtil.getTime(info.getTime(), "yyyy/MM/dd HH:mm:ss");
			}
			info.setTime(DateUtil.formatTime(new Date(tim)));
			infos.add(info);
		}
		wayBillTrace.setTraceInfo(infos);
		return wayBillTrace;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String parseWayBillTraceParam(Object param) {
		StringBuilder paramNumber = new StringBuilder();
		for (WayBill wayBill : (List<WayBill>)param) {
			paramNumber.append(wayBill.getMailNo()).append(",");
		}
		return "[{\"Number\":\""+paramNumber.substring(0, paramNumber.length() - 1) + "\"}]";
	}

	@Override
	public List<WayBill> parseJsonToWayBill(List<String> jsonList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseWayBillToJsonCancel(String packageCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WayBill> parseJsonToWayBillForCancel(List<String> jsonList) {
		WayBill wayBill = new YtWayBill();
		List<WayBill> list = new ArrayList<WayBill>();
		for(String id:jsonList){
			wayBill.setId(id);
		}
		list.add(wayBill);
		return list;
	}

	@Override
	public String parseWayBillToXmlCancel(WayBill waybill) {
		// TODO Auto-generated method stub
		return null;
	}

}
