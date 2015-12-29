package mmb.delivery.domain;

import java.io.Serializable;

/**
 * @description 配送信息
 * @create 2015-5-7 上午11:17:47
 * @author gel
 */
public class DeliverInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**标识，1：运单*/
	public static final int SCAN_TYPE1 = 1;
	/**标识，2：买卖宝订单*/
	public static final int SCAN_TYPE2 = 2;
	/**标识，3：京东订单*/
	public static final int SCAN_TYPE3 = 3;
	
	/**订单编号**/
	private String orderCode;
	/**订单ID*/
	private int orderId;
	/**pop订单编号*/
	private String popOrderCode;
	/**pop订单ID*/
	private int popOrderId;
	/**运单号**/
	private String deliverCode;
	/**甲方类型:,现在为mmb**/
	private int type;
	/**乙方类型,现为jd*/
	private int popType;

	private int id;
	private String deliverInfo;//运单信息
	private String time;//各个状态对应的时间
	private int storageId;//仓库id
	private String province;//省
	private String city;//市
	private String district;//区
	private int deliverType;//快递公司
	private int deliverState;//状态名
	private String deliverTime;//发货时间
	private double usedTime;//配送用时
	private double effectTime;//标准时效
	
	public double getUsedTime() {
		return usedTime;
	}
	public void setUsedTime(double usedTime) {
		this.usedTime = usedTime;
	}
	public double getEffectTime() {
		return effectTime;
	}
	public void setEffectTime(double effectTime) {
		this.effectTime = effectTime;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDeliverState() {
		return deliverState;
	}
	public void setDeliverState(int deliverState) {
		this.deliverState = deliverState;
	}
	public int getDeliverType() {
		return deliverType;
	}
	public void setDeliverType(int deliverType) {
		this.deliverType = deliverType;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getDeliverTime() {
		return deliverTime;
	}
	public void setDeliverTime(String deliverTime) {
		this.deliverTime = deliverTime;
	}
	 
	public String getDeliverInfo() {
		return deliverInfo;
	}
	public void setDeliverInfo(String deliverInfo) {
		this.deliverInfo = deliverInfo;
	}
	 
	public int getStorageId() {
		return storageId;
	}
	public void setStorageId(int storageId) {
		this.storageId = storageId;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	 
	public String getDeliverCode() {
		return deliverCode;
	}
	public void setDeliverCode(String deliverCode) {
		this.deliverCode = deliverCode;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public String getPopOrderCode() {
		return popOrderCode;
	}
	public void setPopOrderCode(String popOrderCode) {
		this.popOrderCode = popOrderCode;
	}
	public int getPopOrderId() {
		return popOrderId;
	}
	public void setPopOrderId(int popOrderId) {
		this.popOrderId = popOrderId;
	}
	public int getPopType() {
		return popType;
	}
	public void setPopType(int popType) {
		this.popType = popType;
	}
	
	/**
	 * 根据发货仓Id获取发货仓名称
	 * @param deliveryType
	 * @return
	 * @creator yaoliang
	 * @time 2015-5-8 下午17:52:20
	 */
	public String getStorageName(){
		String storageName = "";
		switch(storageId){
			case 1:
			storageName = "京东仓";
			break;
		}
		return storageName;
	}
	
	/**
	 * 根据快递公司Id获取快递公司名称
	 * @param deliveryType
	 * @return
	 * @creator yaoliang
	 * @time 2015-5-8 下午17:52:20
	 */
	public String getDeliveryName(){
		String deliveryName = "";
		switch(deliverType){
			case 1:
				deliveryName = "京东快递";
			break;
		}
		return deliveryName;
	}
	
	/**
	 * 根据状态值获取状态名称
	 * @param status
	 * @return
	 * @creator yaoliang
	 * @time 2015-5-8 下午17:52:20
	 */
	public String getDeliverStateName(){
		String deliverStateName = "";
		switch(deliverState){
			case 0:
				deliverStateName = "已出库";
			break;
			case 1:
				deliverStateName = "已揽收";
			break;
			case 2:
				deliverStateName = "已在途";
			break;
			case 7:
				deliverStateName = "已签收";
			break;
			case 8:
				deliverStateName = "未妥投开始退回";
			break;
		}
		return deliverStateName;
	}
	
	/**
	 * 根据popId获取pop商家名称
	 * @param status
	 * @return
	 * @creator yaoliang
	 * @time 2015-5-13 下午17:52:20
	 */
	public String getPopName(){
		String popName = "";
		switch(popType){
			case PopBussiness.POP_JD:
				popName = "京东";
			break;
		}
		return popName;
	}
	
}
