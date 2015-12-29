/*
 * Created on 2009-8-17
 *
 */
package adultadmin.bean;

import java.util.LinkedHashMap;
import java.util.Map;

public final class OrderStockStatusBean {

	/**
	 * 订单发货状态：发货未处理
	 */
	public static int STATUS_NODEAL = 0;
	/**
	 * 订单发货状态：发货失败
	 */
	public static int STATUS_STOCKOUT_FAILURE = 1;
	/**
	 * 订单发货状态：发货成功
	 */
	public static int STATUS_STOCKOUT_SUCCESS = 2;
	/**
	 * 订单发货状态：空白
	 */
	public static int STATUS_BLANK = 3;
	/**
	 * 订单发货状态：缺货未处理
	 */
	public static int STATUS_OUTOFSTOCK = 4;
	/**
	 * 订单发货状态：缺货电话失败
	 */
	public static int STATUS_OUTOFSTOCK_FAILURE = 5;
	/**
	 * 订单发货状态：缺货电话成功
	 */
	public static int STATUS_OUTOFSTOCK_SUCCESS = 6;
	/**
	 * 订单发货状态：缺货已补货
	 */
	public static int STATUS_OUTOFSTOCK_REFILL = 7;
	/**
	 * 订单发货状态：缺货发货成功
	 */
	public static int STATUS_OUTOFSTOCK_STOCKOUT = 8;
	/**
	 * 订单发货状态：缺货取消
	 */
	public static int STATUS_OUTOFSTOCK_CANCEL = 9;
	
	public static Map orderStockStatusMap = new LinkedHashMap();

	static {
		orderStockStatusMap.put(Integer.valueOf(STATUS_NODEAL), "发货未处理");
		orderStockStatusMap.put(Integer.valueOf(STATUS_STOCKOUT_FAILURE), "发货失败");
		orderStockStatusMap.put(Integer.valueOf(STATUS_STOCKOUT_SUCCESS), "发货成功");
		orderStockStatusMap.put(Integer.valueOf(STATUS_BLANK), "空白");
		orderStockStatusMap.put(Integer.valueOf(STATUS_OUTOFSTOCK), "缺货未处理");
		orderStockStatusMap.put(Integer.valueOf(STATUS_OUTOFSTOCK_FAILURE), "缺货电话失败");
		orderStockStatusMap.put(Integer.valueOf(STATUS_OUTOFSTOCK_SUCCESS), "缺货电话成功");
		orderStockStatusMap.put(Integer.valueOf(STATUS_OUTOFSTOCK_REFILL), "缺货已补货");
	}

	public static String getOrderStockStatusName(int status){
		String result = (String) orderStockStatusMap.get(Integer.valueOf(status));
		if(result == null){
			result = "-";
		}
		switch (status) {
		case 8:
			result = "缺货发货成功";
			break;
		case 9:
			result = "缺货取消";
			break;
		default:
			break;
		}
		return result;
	}
}