package mmb.delivery.domain;

/**
 * 快递面单数据
 * @author likaige
 * @create 2015年5月14日 上午9:51:35
 */
public class Waybill {
	
	/** 承运商:买卖宝无锡仓 */
	public static final int CARRIER_MMB_WX = 0;
	/** 承运商:买卖宝成都仓 */
	public static final int CARRIER_MMB_CD = 1;
	/** 承运商:京东自营 */
	public static final int CARRIER_JD = 2;

	private int id;
	private int carrier = 0; //承运商
	private String deliveryId; //运单号
	private String orderId; //商家订单号
	private int selfPrintWayBill = 0; //是否客户打印运单(是：1，否：0)
	private String pickMethod = "1"; //取件方式(上门收货：1，自送：2)
	private String packageRequired = "1"; //包装要求(不需包装：1，简单包装：2，特殊包装：3) 
	private String senderName; //寄件人姓名
	private String senderAddress; //寄件人地址
	//private String senderTel ; //寄件人电话
	private String senderMobile; //寄件人手机
	//private String senderPostcode ; //寄件人邮编
	private String receiveName; //收件人姓名
	private String receiveAddress; //收件人地址
	private String province ; //收件人省 
	private String city ; //收件人市
	private String county ; //收件人县
	private String town ; //收件人镇 
	private String receiveTel ; //收件人电话
	private String receiveMobile; //收件人手机
	private String postcode; //收件人邮编
	private int packageCount = 1; //包裹数量
	private double weight; //重量
	//private double vloumLong ; //包裹长度
	//private double vloumWidth ; //包裹宽度
	//private double vloumHeight  ; //包裹高
	private double vloumn = 0; //包裹体积[默认可传为0]
	//private String description; //商品描述
	private int collectionValue; //是否代收货款[1为代收货款，0为非代收货款]
	private double collectionMoney; //代收货款金额
	private int guaranteeValue = 0; //是否保价(是：1，否：0) 
	//private double guaranteeValueAmount  ; //保价金额
	private int signReturn = 0; //是否签单返还(是：1，否：0) 
	private int aging = 1; //运单时效(普通：1，工作日：2，非工作日：3，晚间：4) 
	private int transType = 1; //运输业务类型(陆运：1，航空：2)
	private String shopCode; //门店编号
	private String orderSendTime; //预约送单时间
	private String warehouseCode; //发货仓编号,京东对应7个备件仓编码
	private String remark; //运单备注
	private int goodsType; //配送产品
	private int orderType; //运单类型
	
	private int stockArea; //库区域
	private int userOrderId; //MMB订单id
	private String userOrderCode; //MMB订单编号
	private int agentId; //委托单id
	
	private String sendStatus;//面单发送状态
	private int buyId;//采销表id
	private int poiId;//运单表id
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCarrier() {
		return carrier;
	}
	public void setCarrier(int carrier) {
		this.carrier = carrier;
	}
	public String getDeliveryId() {
		return deliveryId;
	}
	public void setDeliveryId(String deliveryId) {
		this.deliveryId = deliveryId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public int getSelfPrintWayBill() {
		return selfPrintWayBill;
	}
	public void setSelfPrintWayBill(int selfPrintWayBill) {
		this.selfPrintWayBill = selfPrintWayBill;
	}
	public String getPickMethod() {
		return pickMethod;
	}
	public void setPickMethod(String pickMethod) {
		this.pickMethod = pickMethod;
	}
	public String getPackageRequired() {
		return packageRequired;
	}
	public void setPackageRequired(String packageRequired) {
		this.packageRequired = packageRequired;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getSenderAddress() {
		return senderAddress;
	}
	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}
	public String getSenderMobile() {
		return senderMobile;
	}
	public void setSenderMobile(String senderMobile) {
		this.senderMobile = senderMobile;
	}
	public String getReceiveName() {
		return receiveName;
	}
	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}
	public String getReceiveAddress() {
		return receiveAddress;
	}
	public void setReceiveAddress(String receiveAddress) {
		this.receiveAddress = receiveAddress;
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
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getTown() {
		return town;
	}
	public void setTown(String town) {
		this.town = town;
	}
	public String getReceiveTel() {
		return receiveTel;
	}
	public void setReceiveTel(String receiveTel) {
		this.receiveTel = receiveTel;
	}
	public String getReceiveMobile() {
		return receiveMobile;
	}
	public void setReceiveMobile(String receiveMobile) {
		this.receiveMobile = receiveMobile;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public int getPackageCount() {
		return packageCount;
	}
	public void setPackageCount(int packageCount) {
		this.packageCount = packageCount;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public double getVloumn() {
		return vloumn;
	}
	public void setVloumn(double vloumn) {
		this.vloumn = vloumn;
	}
	public int getCollectionValue() {
		return collectionValue;
	}
	public void setCollectionValue(int collectionValue) {
		this.collectionValue = collectionValue;
	}
	public double getCollectionMoney() {
		if(this.collectionValue == 1){
			return collectionMoney;
		}else{
			return 0;
		}
	}
	public void setCollectionMoney(double collectionMoney) {
		this.collectionMoney = collectionMoney;
	}
	public int getGuaranteeValue() {
		return guaranteeValue;
	}
	public void setGuaranteeValue(int guaranteeValue) {
		this.guaranteeValue = guaranteeValue;
	}
	public int getSignReturn() {
		return signReturn;
	}
	public void setSignReturn(int signReturn) {
		this.signReturn = signReturn;
	}
	public int getAging() {
		return aging;
	}
	public void setAging(int aging) {
		this.aging = aging;
	}
	public int getTransType() {
		return transType;
	}
	public void setTransType(int transType) {
		this.transType = transType;
	}
	public String getShopCode() {
		return shopCode;
	}
	public void setShopCode(String shopCode) {
		this.shopCode = shopCode;
	}
	public String getOrderSendTime() {
		return orderSendTime;
	}
	public void setOrderSendTime(String orderSendTime) {
		this.orderSendTime = orderSendTime;
	}
	public String getWarehouseCode() {
		return warehouseCode;
	}
	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getGoodsType() {
		return goodsType;
	}
	public void setGoodsType(int goodsType) {
		this.goodsType = goodsType;
	}
	public int getOrderType() {
		return orderType;
	}
	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}
	public int getStockArea() {
		return stockArea;
	}
	public void setStockArea(int stockArea) {
		this.stockArea = stockArea;
	}
	public int getUserOrderId() {
		return userOrderId;
	}
	public void setUserOrderId(int userOrderId) {
		this.userOrderId = userOrderId;
	}
	public String getUserOrderCode() {
		return userOrderCode;
	}
	public void setUserOrderCode(String userOrderCode) {
		this.userOrderCode = userOrderCode;
	}
	public int getAgentId() {
		return agentId;
	}
	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}
	public String getSendStatus() {
		return sendStatus;
	}
	public void setSendStatus(String sendStatus) {
		this.sendStatus = sendStatus;
	}
	public int getBuyId() {
		return buyId;
	}
	public void setBuyId(int buyId) {
		this.buyId = buyId;
	}
	public int getPoiId() {
		return poiId;
	}
	public void setPoiId(int poiId) {
		this.poiId = poiId;
	}
}
