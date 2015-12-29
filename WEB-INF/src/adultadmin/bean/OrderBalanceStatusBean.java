/*
 * Created on 2009-8-17
 *
 */
package adultadmin.bean;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2009-8-19
 * 
 * 说明：订单结算状态<br/>
 * 退回未结算、退回已结算、用户退货结算、妥投已结算
 */
public final class OrderBalanceStatusBean {

	/**
	 * 订单结算状态：退回未结算
	 */
	public static int STATUS_1 = 1;
	/**
	 * 订单发货状态：退回已结算
	 */
	public static int STATUS_2 = 2;
	/**
	 * 订单结算状态：用户退货结算
	 */
	public static int STATUS_3 = 3;
	/**
	 * 订单结算状态：妥投已结算
	 */
	public static int STATUS_4 = 4;

	public static Map orderBalanceStatusMap = new LinkedHashMap();

	static {
		orderBalanceStatusMap.put(Integer.valueOf(STATUS_1), "退回未结算");
		orderBalanceStatusMap.put(Integer.valueOf(STATUS_2), "退回已结算");
		orderBalanceStatusMap.put(Integer.valueOf(STATUS_3), "用户退货结算");
		orderBalanceStatusMap.put(Integer.valueOf(STATUS_4), "妥投已结算");
	}

	public static String getOrderBalanceStatusName(int status){
		String result = (String) orderBalanceStatusMap.get(Integer.valueOf(status));
		if(result == null){
			result = "-";
		}
		return result;
	}
}