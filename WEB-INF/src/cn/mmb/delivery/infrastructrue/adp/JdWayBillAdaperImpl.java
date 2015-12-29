package cn.mmb.delivery.infrastructrue.adp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import cn.mmb.delivery.domain.model.JdWayBill;
import cn.mmb.delivery.domain.model.WayBill;
import cn.mmb.delivery.domain.model.WayBillTrace;

@Service
public class JdWayBillAdaperImpl implements WayBillAdaper {

	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog("debug.Log");
	
	@Override
	public String parseWayBillToJson(WayBill waybill) {
		JSONObject json = new JSONObject();
		json.put("deliveryId", waybill.getMailNo());
		json.put("orderId", waybill.getOrderCode());
		json.put("selfPrintWayBill", 1); //是否客户打印运单
		json.put("senderName", waybill.getSender());
		json.put("senderAddress", waybill.getSenderAddress());
		json.put("senderMobile", waybill.getSenderMobile());
		json.put("receiveName", waybill.getName());
		json.put("receiveAddress", waybill.getAddress());
		json.put("receiveMobile", waybill.getMobile());
		json.put("packageCount", 1);
		json.put("weight", 0);
		json.put("vloumn", 0);
		int collectionValue = Integer.parseInt(waybill.getOrderType())==0 ? 1 : 0;
		json.put("collectionValue", collectionValue);
		json.put("collectionMoney", collectionValue==1 ? waybill.getdPrice() : 0.0);
		return json.toString();
	}
	
	@Override
	public String parseWayBillToXml(WayBill waybill) {
		return null;
	}

	@Override
	public List<WayBill> parseXmlToWayBill(List<String> xmlList){
		return null;
	}

	@Override
	public Map<String, Object> parseWayBillToMap(WayBill waybill) {
		return null;
	}

	@Override
	public Map<String, Object> parseDQWayBillToMap(WayBill waybill) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WayBillTrace parseXmlToWayBillTrace(String xml) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WayBillTrace parseJsonToWayBillTrace(String json) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseWayBillTraceParam(Object param) {
		// TODO Auto-generated method stub
		return null;
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
		WayBill wayBill = new JdWayBill();
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
