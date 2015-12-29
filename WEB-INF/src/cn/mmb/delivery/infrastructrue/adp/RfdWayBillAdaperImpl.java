package cn.mmb.delivery.infrastructrue.adp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import adultadmin.util.StringUtil;
import cn.mmb.delivery.domain.model.RfdWayBill;
import cn.mmb.delivery.domain.model.WayBill;
import cn.mmb.delivery.domain.model.WayBillTrace;
import cn.mmb.delivery.domain.model.vo.BasicParamBean;

@Service(value="RfdWayBillAdaperImpl")
public class RfdWayBillAdaperImpl implements WayBillAdaper {

	@Resource(name ="rfdConfig")
	private BasicParamBean basicParamBean;

	@Override
	public String parseWayBillToXml(WayBill waybill) {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> list2 = new ArrayList<Map<String,Object>>();
		Map<String,Object> message = new HashMap<String,Object>();
		message.put("MerchantCode",basicParamBean.getMerchantcode());
		message.put("FormCode", waybill.getOrderCode());
		message.put("TotalAmount", waybill.getdPrice());
		if("0".equals(waybill.getOrderType())){//0:cod
			message.put("PaidAmount",0);
		}else{
			message.put("PaidAmount",waybill.getItemsValue());
		}
		if("0".equals(waybill.getOrderType())){
			message.put("ReceiveAmount",waybill.getItemsValue());
		}else{
			message.put("ReceiveAmount",0);
		}
		message.put("RefundAmount", 0);
		message.put("PackageCount", 1);
		message.put("InsureAmount", 0);
		message.put("Weight",1.123);
		message.put("CashType", 1);
		message.put("ToName", waybill.getName());
		message.put("ToAddress", waybill.getAddress());
		message.put("ToProvince", waybill.getProv());
		message.put("ToCity", waybill.getCity());
		message.put("ToArea", waybill.getArea());
		message.put("ToPostCode", waybill.getPostCode());
		message.put("ToMobile",StringUtil.replaceStr(waybill.getMobile()));
		message.put("ToPhone", null);
		message.put("ToEmail", null);
		message.put("BoxCount", 0);
		message.put("GoodsProperty", "0");
		message.put("OrderType", 0);
		message.put("CustomerLevel", null);
		message.put("WarehouseName", waybill.getStockName());
		
		Map<String,Object> OrderDetail = new HashMap<String,Object>();
		OrderDetail.put("ProductName", "");
		OrderDetail.put("ProductCode", "");
		OrderDetail.put("Count", "");
		OrderDetail.put("Unit", "");
		OrderDetail.put("SellPrice", "");
		OrderDetail.put("Size", "");
		list2.add(OrderDetail);
		JSONArray jsonArray2 = JSONArray.fromObject(list2);
		message.put("OrderDetails", jsonArray2);
		
		list.add(message);
		JSONArray jsonArray = JSONArray.fromObject(list);
		return jsonArray.toString();
	}

	@Override
	public List<WayBill> parseXmlToWayBill(List<String> xmlList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseWayBillToJson(WayBill waybill) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> parseWayBillToMap(WayBill waybill) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> parseDQWayBillToMap(WayBill waybill) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WayBillTrace parseXmlToWayBillTrace(String xml) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WayBillTrace parseJsonToWayBillTrace(String json) throws Exception {
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
		List<WayBill> list = new ArrayList<WayBill>();
		for(String json:jsonList){
			WayBill wayBill = new RfdWayBill();
			JSONObject obj = JSONObject.fromObject(json);
			String mailNo =null;//运单号
			String orderCode=null;//订单号
	        if (obj.has("IsSuccess")&&"true".equals(String.valueOf(obj.get("IsSuccess")))) {
	        	JSONArray jsonObject =obj.getJSONArray("ResultData");
	        	for(int i = 0 ; i <jsonObject.size(); i++ ){
	        		JSONObject obj2 =jsonObject.getJSONObject(i);
	        		mailNo =obj2.getString("WaybillNo");
	        		orderCode =obj2.getString("FormCode");
	        	}
	        }
		    wayBill.setMailNo(mailNo);
		    wayBill.setOrderCode(orderCode);
			list.add(wayBill);
		}
		return list;
	}

	@Override
	public String parseWayBillToJsonCancel(String packageCode) {
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			Map<String,Object> message = new HashMap<String,Object>();
			message.put("OrderType",0);
			message.put("OrderNo", packageCode);
			message.put("Message", "取消订单");
			list.add(message);
			JSONArray jsonArray = JSONArray.fromObject(list);
			return jsonArray.toString();
	}

	@Override
	public List<WayBill> parseJsonToWayBillForCancel(List<String> jsonList) {
		List<WayBill> list = new ArrayList<WayBill>();
		if(jsonList!=null && !jsonList.isEmpty()){
			for(String json:jsonList){
				WayBill wayBill = new RfdWayBill();
				JSONObject obj = JSONObject.fromObject(json);
				String mailNo =null;//运单号
		        if (obj.has("IsSuccess")&&"true".equals(String.valueOf(obj.get("IsSuccess")))) {
		        	JSONArray jsonObject =obj.getJSONArray("ResultData");
		        	for(int i = 0 ; i <jsonObject.size(); i++ ){
		        		JSONObject obj2 =jsonObject.getJSONObject(i);
	        		    if (obj2.has("IsSuccess")&&"true".equals(String.valueOf(obj2.get("IsSuccess")))) {
	        		    	mailNo =obj2.getString("OrderNo");
	        			    wayBill.setMailNo(mailNo);
	        				list.add(wayBill);
	        		    }
		        	}
		        }
			}
		}
		return list;
	}

	@Override
	public String parseWayBillToXmlCancel(WayBill waybill) {
		// TODO Auto-generated method stub
		return null;
	}
}
