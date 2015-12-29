package cn.mmb.delivery.domain.adp;

import java.util.List;
import java.util.Map;

import adultadmin.util.StringUtil;

public class DateXml {
	
	/**
	 * 生成xml格式，用于封装快递公司接口的数据格式，目前仅用于韵达快递
	* @Description: Map<买卖宝编号，订单地址>
	* @author ahc
	 */
	public static String Ydxmldata(List<Map<String,String>> list){
		StringBuffer sb = new StringBuffer();
		sb.append("<orders>");
		for(Map<String,String> map :list){
			sb.append("<order>");
			sb.append("<id>");
			sb.append(map.get("id"));
			sb.append("</id>");
			sb.append("<receiver_address>");
			String s = map.get("address");
			/**
			 * 需要过滤掉订单地址中特殊字符，否则韵达接口id字段返回结果为0，表示接口交互失败。
			 * 目前已知特殊字符为 & < 
			 */
			String address =StringUtil.replaceStr(s);
			sb.append(address);
			sb.append("</receiver_address>");
			sb.append("</order>");
		}
		sb.append("</orders>");
		return sb.toString();
	}
	
}
