/**
 * 说明：发货信息bean
 * 
 * 日期：2012-8-31
 * 
 * 作者：liuruilan
 */
package mmb.finance.stat;

import java.util.LinkedHashMap;
import java.util.Map;

public class FinanceSellBean {
	
	public int id;
	
	/**
	 * 订单id
	 */
	public int orderId;
	
	/**
	 * 单据号
	 */
	public String code;
	
	/**
	 * 调整单号
	 */
	public String adjustCode;
	
	/**
	 * 实际发生金额,其中负数表示退款金额
	 */
	public float price;
	
	/**
	 * 运费
	 */
	public float carriage;
	
	/**
	 * 其他费用
	 */
	public float charge;
	
	/**
	 * 购买方式：0-货到付款，1-钱包支付，2-银行汇款，3-售后换货
	 */
	public int buyMode;
	
	/**
	 * 付款方式，备用字段，取值暂同buyMode
	 */
	public int payMode;
	
	/**
	 * 结算快递公司
	 */
	public int deliverType;
	
	/**
	 * 操作发生时间
	 */
	public String createDatetime;
	
	/**
	 * 包裹单号
	 */
	public String packageNum;
	
	/**
	 * 数据类别，详见dataTypeMap
	 */
	public int dataType;
	
	/**
	 * 一级地址id
	 */
	public int addrSub1;
	
	/**
	 * 二级地址id
	 */
	public int addrSub2;
	
	/**
	 * 三级地址id
	 */
	public int addrSub3;
	
	/**
	 * 订单数，1 或 -1
	 */
	public int count;
	
	/**
	 * 发货库区
	 */
	public int stockArea;
	
	/**
	 * 退货库区
	 */
	public int backStockArea;
	
	/**
	 * 订单分类
	 */
	public int orderTypeId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAdjustCode() {
		return adjustCode;
	}
	public void setAdjustCode(String adjustCode) {
		this.adjustCode = adjustCode;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public float getCarriage() {
		return carriage;
	}
	public void setCarriage(float carriage) {
		this.carriage = carriage;
	}
	public float getCharge() {
		return charge;
	}
	public void setCharge(float charge) {
		this.charge = charge;
	}
	public int getBuyMode() {
		return buyMode;
	}
	public void setBuyMode(int buyMode) {
		this.buyMode = buyMode;
	}
	public int getPayMode() {
		return payMode;
	}
	public void setPayMode(int payMode) {
		this.payMode = payMode;
	}
	public int getDeliverType() {
		return deliverType;
	}
	public void setDeliverType(int deliverType) {
		this.deliverType = deliverType;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public String getPackageNum() {
		return packageNum;
	}
	public void setPackageNum(String packageNum) {
		this.packageNum = packageNum;
	}
	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	public int getAddrSub1() {
		return addrSub1;
	}
	public void setAddrSub1(int addrSub1) {
		this.addrSub1 = addrSub1;
	}
	public int getAddrSub2() {
		return addrSub2;
	}
	public void setAddrSub2(int addrSub2) {
		this.addrSub2 = addrSub2;
	}
	public int getAddrSub3() {
		return addrSub3;
	}
	public void setAddrSub3(int addrSub3) {
		this.addrSub3 = addrSub3;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getStockArea() {
		return stockArea;
	}
	public void setStockArea(int stockArea) {
		this.stockArea = stockArea;
	}
	public int getBackStockArea() {
		return backStockArea;
	}
	public void setBackStockArea(int backStockArea) {
		this.backStockArea = backStockArea;
	}
	public int getOrderTypeId() {
		return orderTypeId;
	}
	public void setOrderTypeId(int orderTypeId) {
		this.orderTypeId = orderTypeId;
	}


	/**
	 * 数据状态
	 */
	public static Map dataTypeMap = new LinkedHashMap();
	/**
	 * 查看发货订单明细之单据类型
	 */
	public static Map orderModeMap = new LinkedHashMap();
	static{
		dataTypeMap.put(String.valueOf(0), "销售出库");
		dataTypeMap.put(String.valueOf(1), "未妥投退回");
		dataTypeMap.put(String.valueOf(2), "售后退回");
		dataTypeMap.put(String.valueOf(3), "销售出库废弃");
		dataTypeMap.put(String.valueOf(4), "未妥投退回废弃");
		dataTypeMap.put(String.valueOf(5), "售后退回废弃");
		dataTypeMap.put(String.valueOf(6), "销售出库冲抵");
		dataTypeMap.put(String.valueOf(7), "未妥投退回冲抵");
		dataTypeMap.put(String.valueOf(8), "售后退回冲抵");
		
		orderModeMap.put(String.valueOf(0), "发货单");
		orderModeMap.put(String.valueOf(3), "发货单");
		orderModeMap.put(String.valueOf(6), "发货单");
		orderModeMap.put(String.valueOf(1), "销售退货");
		orderModeMap.put(String.valueOf(4), "销售退货");
		orderModeMap.put(String.valueOf(7), "销售退货");
		orderModeMap.put(String.valueOf(2), "售后退货");
		orderModeMap.put(String.valueOf(5), "售后退货");
		orderModeMap.put(String.valueOf(8), "售后退货");
	}
	
	public static String getOrderModeName(int orderMode){
		String name = "";
		if(orderModeMap.get(String.valueOf(orderMode)) != null){
			name = (String) orderModeMap.get(String.valueOf(orderMode));
		}
		return name;
	}
}
