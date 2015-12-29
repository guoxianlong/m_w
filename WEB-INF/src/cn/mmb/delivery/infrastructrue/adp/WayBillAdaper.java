package cn.mmb.delivery.infrastructrue.adp;

import java.util.List;
import java.util.Map;

import cn.mmb.delivery.domain.model.WayBill;
import cn.mmb.delivery.domain.model.WayBillTrace;

public interface WayBillAdaper {

	/**
	 * 生成xml格式，用于封装快递公司接口的数据格式(发送订单)
	* @author ahc
	 */
	public String parseWayBillToXml(WayBill waybill);
	
	/**
	 * 生成json格式，用于封装快递公司接口的数据格式(取消订单)
	* @author ahc
	 */
	public String parseWayBillToJsonCancel(String packageCode);
	
	/**
	 * 解析xml格式，快递公司接口的数据格式
	* @Description: 
	* @author ahc
	 */
	public List<WayBill> parseXmlToWayBill(List<String> xmlList);
	
	/**
	 * 解析json格式，(发送订单)
	* @Description: 
	* @author ahc
	 */
	public List<WayBill> parseJsonToWayBill(List<String> jsonList);
	
	/**
	 * 解析json格式，(取消订单)
	* @Description: 
	* @author ahc
	 */
	public List<WayBill> parseJsonToWayBillForCancel(List<String> jsonList);
	
	/**
	 * 把面单数据封装成JSON格式的字符串
	 * @param waybill 面单对象
	 * @return
	 * @author likaige
	 * @create 2015年8月10日 下午3:15:38
	 */
	String parseWayBillToJson(WayBill waybill);

	/**
	 * 面单waybill里的参数转化为IReport需要的map(圆通)
	 * @param waybill
	 * @author anchao
	 */
	public Map<String,Object> parseWayBillToMap(WayBill waybill);
	
	/**
	 * 面单waybill里的参数转化为IReport需要的map(大Q)
	 * @param waybill
	 * @author anchao
	 */
	public Map<String,Object> parseDQWayBillToMap(WayBill waybill);
	
	
	/** 
	 * @Description: xml串转换为物流信息
	 * @return WayBillTrace 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月11日 下午6:24:23 
	 */
	public WayBillTrace parseXmlToWayBillTrace(String xml) throws Exception;
	
	/** 
	 * @Description: json转换为物流信息
	 * @return WayBillTrace 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月11日 下午6:24:26 
	 */
	public WayBillTrace parseJsonToWayBillTrace(String json) throws Exception;

	/** 
	 * @Description: 调用物流接口时需要的参数
	 * @return String 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月12日 下午1:29:03 
	 */
	public String parseWayBillTraceParam(Object param);
	
	/**
	 * 生成xml格式，用于封装快递公司接口的数据格式(取消订单)
	* @author ahc
	 */
	public String parseWayBillToXmlCancel(WayBill waybill);
}
