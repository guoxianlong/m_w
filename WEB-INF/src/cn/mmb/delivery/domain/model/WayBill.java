package cn.mmb.delivery.domain.model;

public abstract class WayBill {
	
	private String id;
	private String districenterCode; //集包分拨中心编码
	private String districenterName; //集包分拨中心名称
	private String bigpenCode;//大笔编码
	private String position; //大笔
	private String positionNo; //格口号
	private String padMailno;//集包分拨中心编码
	private String clientId;//商家代码
	private String logisticProviderId;//物流公司id(必须是YTO)
	private String cusomerId;//商家代码（建议和clientId相同）
	private String orderCode;//物流订单号(订单号)
	private String orderType;//订单类型(0:货到付款 1:在线支付)
	private String serviceType;//服务类型（默认写1）
	private String name;//姓名
	private String mobile;//手机(加密)
	private String prov;//用户所在省
	private String city;//用户所在市县
	private String area;//用户所在区
	private String address;//用户详细地址
	private String itemName;//商品名称
	private String number;//商品数量(默认为1)
	private String itemsValue;//代收金额
	private String dPrice;//订单金额
	private String postCode;//邮编
	private String mailNo;//运单号
	private String sender;//寄件人
	private String senderMobile;//寄件人电话
	private String senderAddress;//寄件人地址
	private String deliverName;//快递公司
	private int deliverId;//快递公司Id
	private String orderTypeName;//商品类型
	private Float weight;//包裹重量
	private int stockArea; //库区域
	private String stockName;//库名称
	private String msg;//返回信息
	
	private String reportPath;//模版路径
	private String mmbImgPath;//买卖宝logo图片路径
	private String ytoImgPath;//圆通logo图片路径
	
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getdPrice() {
		return dPrice;
	}
	public void setdPrice(String dPrice) {
		this.dPrice = dPrice;
	}
	public int getDeliverId() {
		return deliverId;
	}
	public void setDeliverId(int deliverId) {
		this.deliverId = deliverId;
	}
	
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public Float getWeight() {
		return weight;
	}
	public void setWeight(Float weight) {
		this.weight = weight;
	}
	public String getDeliverName() {
		return deliverName;
	}
	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
	}
	public String getOrderTypeName() {
		return orderTypeName;
	}
	public void setOrderTypeName(String orderTypeName) {
		this.orderTypeName = orderTypeName;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getSenderMobile() {
		return senderMobile;
	}
	public void setSenderMobile(String senderMobile) {
		this.senderMobile = senderMobile;
	}
	public String getSenderAddress() {
		return senderAddress;
	}
	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}
	public String getMailNo() {
		return mailNo;
	}
	public void setMailNo(String mailNo) {
		this.mailNo = mailNo;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public String getItemsValue() {
		return itemsValue;
	}
	public void setItemsValue(String itemsValue) {
		this.itemsValue = itemsValue;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDistricenterCode() {
		return districenterCode;
	}
	public void setDistricenterCode(String districenterCode) {
		this.districenterCode = districenterCode;
	}
	public String getDistricenterName() {
		return districenterName;
	}
	public void setDistricenterName(String districenterName) {
		this.districenterName = districenterName;
	}
	public String getBigpenCode() {
		return bigpenCode;
	}
	public void setBigpenCode(String bigpenCode) {
		this.bigpenCode = bigpenCode;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getPositionNo() {
		return positionNo;
	}
	public void setPositionNo(String positionNo) {
		this.positionNo = positionNo;
	}
	public String getPadMailno() {
		return padMailno;
	}
	public void setPadMailno(String padMailno) {
		this.padMailno = padMailno;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getLogisticProviderId() {
		return logisticProviderId;
	}
	public void setLogisticProviderId(String logisticProviderId) {
		this.logisticProviderId = logisticProviderId;
	}
	public String getCusomerId() {
		return cusomerId;
	}
	public void setCusomerId(String cusomerId) {
		this.cusomerId = cusomerId;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getProv() {
		return prov;
	}
	public String getStockName() {
		return stockName;
	}
	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
	public void setProv(String prov) {
		this.prov = prov;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getReportPath() {
		return reportPath;
	}
	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}
	public String getMmbImgPath() {
		return mmbImgPath;
	}
	public void setMmbImgPath(String mmbImgPath) {
		this.mmbImgPath = mmbImgPath;
	}
	public String getYtoImgPath() {
		return ytoImgPath;
	}
	public void setYtoImgPath(String ytoImgPath) {
		this.ytoImgPath = ytoImgPath;
	}
	public int getStockArea() {
		return stockArea;
	}
	public void setStockArea(int stockArea) {
		this.stockArea = stockArea;
	}
}
