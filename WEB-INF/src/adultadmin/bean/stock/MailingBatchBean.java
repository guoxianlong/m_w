package adultadmin.bean.stock;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 物流自配送，发货波次
 * @author Administrator
 *
 */
public class MailingBatchBean {
	public int id;
	public String code;//发货波次号
	public String createDatetime;//创建时间
	public int deliver;//快递公司
	public String carrier;//承运商
	public int createAdminId;//创建人id
	public String transitDatetime; //交接时间
	public String createAdminName;//创建人姓名
	public int transitAdminId;//交接人id
	public String transitAdminName;//交接人姓名
	public String store;//发货仓库
	public int status;//状态，0为待出库，1为已出库
	public int orderCount;//订单总数
	public double totalPrice;//总金额
	public float totalWeight;//总重量
	public String receiverTime;//接收时间
	public String recipient;//接收人
	public List mailingBatchParcelList;//发货邮包列表
	public int area;//地区
	//方便easyui的属性
	public String orderCode; //订单号
	public String deliverName;
	public String statusName;
	
	/***
	 * 芳村
	 */
	public static final int AREA1 = 1;
	/**
	 * 增城
	 */
	public static final int AREA3 = 3;
	/**
	 * 无锡
	 */
	public static final int AREA4 = 4;
	
	public static HashMap<Integer, String> areaNameMap = new LinkedHashMap<Integer, String>();
	static{
		areaNameMap.put(AREA1, "芳村");
		areaNameMap.put(AREA3, "增城");
		areaNameMap.put(AREA4, "无锡");
	}
	public String getAreaName(int area) {
		String areaName = "";
		if(areaNameMap.containsKey(area)){
			return areaNameMap.get(area).toString();
		}else{
			return areaName;
		}
	}
	
	public String getTransitDatetime() {
		return transitDatetime;
	}

	public void setTransitDatetime(String transitDatetime) {
		this.transitDatetime = transitDatetime;
	}

	public String getDeliverName() {
		return deliverName;
	}

	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	
	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getReceiverTime() {
		return receiverTime;
	}
	public void setReceiverTime(String receiverTime) {
		this.receiverTime = receiverTime;
	}
	public String getRecipient() {
		return recipient;
	}
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public int getDeliver() {
		return deliver;
	}
	public void setDeliver(int deliver) {
		this.deliver = deliver;
	}
	public String getCarrier() {
		return carrier;
	}
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	public int getCreateAdminId() {
		return createAdminId;
	}
	public void setCreateAdminId(int createAdminId) {
		this.createAdminId = createAdminId;
	}
	public String getCreateAdminName() {
		return createAdminName;
	}
	public void setCreateAdminName(String createAdminName) {
		this.createAdminName = createAdminName;
	}
	public int getTransitAdminId() {
		return transitAdminId;
	}
	public void setTransitAdminId(int transitAdminId) {
		this.transitAdminId = transitAdminId;
	}
	public String getTransitAdminName() {
		return transitAdminName;
	}
	public void setTransitAdminName(String transitAdminName) {
		this.transitAdminName = transitAdminName;
	}
	public String getStore() {
		return store;
	}
	public void setStore(String store) {
		this.store = store;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public List getMailingBatchParcelList() {
		return mailingBatchParcelList;
	}
	public void setMailingBatchParcelList(List mailingBatchParcelList) {
		this.mailingBatchParcelList = mailingBatchParcelList;
	}
	public int getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}
	public float getTotalWeight() {
		return totalWeight;
	}
	public void setTotalWeight(float totalWeight) {
		this.totalWeight = totalWeight;
	}
	
	/**
	 * 待出库
	 */
	public static int STATUS0=0;
	
	/**
	 * 已出库
	 */
	public static int STATUS1=1;
	/**
	 * 已交接
	 */
	public static int STATUS2=2;
	
	public static HashMap<Integer, String> statusNameMap= new LinkedHashMap<Integer, String>();
	static{
		statusNameMap.put(Integer.valueOf(STATUS0), "待出库");
		statusNameMap.put(Integer.valueOf(STATUS1), "已出库");
		statusNameMap.put(Integer.valueOf(STATUS2), "已交接");
	}
	
	public String getStatusName(int status){
		String result="";
		if(statusNameMap.containsKey(Integer.valueOf(status))){
			result=statusNameMap.get(Integer.valueOf(status)).toString();
		}
		return result;
	}
	
}
