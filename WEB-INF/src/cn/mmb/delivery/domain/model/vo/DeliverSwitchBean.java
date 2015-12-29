package cn.mmb.delivery.domain.model.vo;

public class DeliverSwitchBean {
	
	private int id;
	
	private String stockArea;
	
	private String orderCode;
	
	private String originDeliverName;//原快递公司
	
	private String deliverName;//现快递公司
	
	private String originPackageCode;//原包裹单号
	
	private String packageCode;//现包裹单号
	
	private String createDateTime;
	
	private String modifyDatetime;
	
	private String remark;
	
	private String operUserId;
	
	private String operUserName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStockArea() {
		return stockArea;
	}

	public void setStockArea(String stockArea) {
		this.stockArea = stockArea;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getOriginDeliverName() {
		return originDeliverName;
	}

	public void setOriginDeliverName(String originDeliverName) {
		this.originDeliverName = originDeliverName;
	}

	public String getDeliverName() {
		return deliverName;
	}

	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
	}

	public String getOriginPackageCode() {
		return originPackageCode;
	}

	public void setOriginPackageCode(String originPackageCode) {
		this.originPackageCode = originPackageCode;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public String getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(String createDateTime) {
		this.createDateTime = createDateTime;
	}

	public String getModifyDatetime() {
		return modifyDatetime;
	}

	public void setModifyDatetime(String modifyDatetime) {
		this.modifyDatetime = modifyDatetime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOperUserId() {
		return operUserId;
	}

	public void setOperUserId(String operUserId) {
		this.operUserId = operUserId;
	}

	public String getOperUserName() {
		return operUserName;
	}

	public void setOperUserName(String operUserName) {
		this.operUserName = operUserName;
	}


}
