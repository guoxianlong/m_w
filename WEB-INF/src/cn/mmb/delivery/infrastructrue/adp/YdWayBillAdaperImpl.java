package cn.mmb.delivery.infrastructrue.adp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import cn.mmb.delivery.domain.model.WayBill;
import cn.mmb.delivery.domain.model.WayBillTrace;
import cn.mmb.delivery.domain.model.YdWayBill;
import cn.mmb.delivery.domain.model.vo.SenderBean;

import com.mmb.framework.support.SpringHandler;

@Service(value="YdWayBillAdaperImpl")
public class YdWayBillAdaperImpl implements WayBillAdaper {

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
		sb.append("<orders>");
			sb.append("<order>");
				sb.append("<order_serial_no>");
				sb.append(waybill.getOrderCode());
				sb.append("</order_serial_no>");
				
				sb.append("<khddh>");
				sb.append(waybill.getOrderCode());
				sb.append("</khddh>");
				
				sb.append("<nbckh>");
				sb.append("");
				sb.append("</nbckh>");
				
				sb.append("<order_type>");
				if("0".equals(waybill.getOrderType())){
					sb.append("COD");
				}else{
					sb.append("common");
				}
				sb.append("</order_type>");
					
					sb.append("<sender>");
						sb.append("<name>");
						sb.append(senderBean.getName());
						sb.append("</name>");
						
						sb.append("<company>");
						sb.append("");
						sb.append("</company>");
						
						sb.append("<city>");
						sb.append("");
						sb.append("</city>");
						
						sb.append("<address>");
						sb.append(senderBean.getProv()+","+senderBean.getCity()+","+senderBean.getAddress());
						sb.append("</address>");
						
						sb.append("<postcode>");
						sb.append("");
						sb.append("</postcode>");
						
						sb.append("<phone>");
						sb.append(senderBean.getPhone());
						sb.append("</phone>");
						
						sb.append("<mobile>");
						sb.append("");
						sb.append("</mobile>");
						
						sb.append("<branch>");
						sb.append("");
						sb.append("</branch>");
					sb.append("</sender>");
					
					sb.append("<receiver>");
						sb.append("<name>");
						sb.append(StringUtil.replaceStr(waybill.getName()));
						sb.append("</name>");
						
						sb.append("<company>");
						sb.append("");
						sb.append("</company>");
						
						sb.append("<city>");
						sb.append("");
						sb.append("</city>");
						
						sb.append("<address>");
						sb.append(StringUtil.replaceStr(waybill.getAddress()));
						sb.append("</address>");
						
						sb.append("<postcode>");
						sb.append("");
						sb.append("</postcode>");
						
						sb.append("<phone>");
						sb.append(StringUtil.replaceStr(waybill.getMobile()));
						sb.append("</phone>");
						
						sb.append("<mobile>");
						sb.append("");
						sb.append("</mobile>");
						
						sb.append("<branch>");
						sb.append("");
						sb.append("</branch>");
					sb.append("</receiver>");
					
					sb.append("<weight>");
					sb.append("");
					sb.append("</weight>");
					
					sb.append("<size>");
					sb.append("");
					sb.append("</size>");
					
					sb.append("<value>");
					sb.append(waybill.getItemsValue());
					sb.append("</value>");
					
					sb.append("<collection_value>");
					sb.append("");
					sb.append("</collection_value>");
					
					sb.append("<special>");
					sb.append("");
					sb.append("</special>");
					
						sb.append("<items>");
							sb.append("<item>");
								sb.append("<name>");
								sb.append("买卖宝商品");	
								sb.append("</name>");
								
								sb.append("<number>");
								sb.append(1);	
								sb.append("</number>");
								
								sb.append("<remark>");
								sb.append("");	
								sb.append("</remark>");
							sb.append("</item>");
						sb.append("</items>");
					sb.append("<remark>");
					sb.append("");
					sb.append("</remark>");
					sb.append("<cus_area1>");
					sb.append("");
					sb.append("</cus_area1>");
					sb.append("<cus_area2>");
					sb.append("");
					sb.append("</cus_area2>");
					sb.append("<callback_id>");
					sb.append("");
					sb.append("</callback_id>");
					sb.append("<wave_no>");
					sb.append("");
					sb.append("</wave_no>");
					
			sb.append("</order>");
		sb.append("</orders>");
		return sb.toString();
	}

	@Override
	public String parseWayBillToJsonCancel(String packageCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WayBill> parseXmlToWayBill(List<String> xmlList) {
		List<WayBill> list = new ArrayList<WayBill>();
		
		String mailNo = null;//运单号
		String status = null;//状态
		String orderCode = null;//mmb订单编号
		String pdfInfo = null;//明细
		String districenterCode = null;//集包地编码
		String districenterName = null;//集包地名称
		String bigpenCode = null;//大笔编码
		String position = null;//大笔
		String positionNo = null;//格口号
		for(String xml:xmlList){
			WayBill wayBill = new YdWayBill();
			try {
				Document doc = DocumentHelper.parseText(xml);
				Element root = doc.getRootElement();
				Iterator<?> iter = root.elementIterator("response");
				// 遍历orderMessage节点  
		        while (iter.hasNext()) {
		            Element recordEle = (Element) iter.next();  
		            mailNo =recordEle.elementTextTrim("mail_no");
		            status =recordEle.elementTextTrim("status");
		            orderCode =recordEle.elementTextTrim("order_serial_no");
		            pdfInfo =recordEle.elementTextTrim("pdf_info");
		        }
				
			}catch(Exception e){
				log.error(DateUtil.getNow()+"-快递公司[韵达]-没有获取有效信息："+xml);
				e.printStackTrace();
			}
			
			if("1".equals(status)){//成功状态
				int beginNum = pdfInfo.indexOf("{");
				int endINum = pdfInfo.lastIndexOf("}");
				String newPdfInfo = pdfInfo.substring(beginNum, endINum+1);
				JSONObject obj = JSONObject.fromObject(newPdfInfo);
				districenterCode = obj.getString("package_wd");//集包地编码
				districenterName = obj.getString("package_wdjc").replaceAll("集包地：", "");//集包地名称
				bigpenCode = obj.getString("bigpen_code");//大笔编码
				position = obj.getString("position");//大笔
				positionNo = obj.getString("lattice_mouth_no");//格口号
				if(mailNo!=null && !"".equals(mailNo)){
					 wayBill.setMailNo(mailNo);
				}
				if(orderCode!=null && !"".equals(orderCode)){
					 wayBill.setOrderCode(orderCode);
				}
				if(districenterCode!=null && !"".equals(districenterCode)){
					wayBill.setDistricenterCode(districenterCode);
				}
				if(districenterName!=null && !"".equals(districenterName)){
					wayBill.setDistricenterName(districenterName);
				}
				if(bigpenCode!=null && !"".equals(bigpenCode)){
					wayBill.setBigpenCode(bigpenCode);
				}
				if(position!=null && !"".equals(position)){
					wayBill.setPosition(position);
				}
				if(positionNo!=null && !"".equals(positionNo)){
					wayBill.setPositionNo(positionNo);
				}
				list.add(wayBill);
			}
		}
		
		return list;
	}

	@Override
	public List<WayBill> parseJsonToWayBill(List<String> jsonList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WayBill> parseJsonToWayBillForCancel(List<String> jsonList) {
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
	public String parseWayBillToXmlCancel(WayBill waybill) {
		StringBuffer sb = new StringBuffer();
		sb.append("<orders>");
			sb.append("<order>");
				sb.append("<order_serial_no>");
				sb.append(waybill.getOrderCode());
				sb.append("</order_serial_no>");
				
				sb.append("<mailno>");
				sb.append(waybill.getMailNo());
				sb.append("</mailno>");
			sb.append("</order>");
		sb.append("</orders>");
		return sb.toString();
	}

}
