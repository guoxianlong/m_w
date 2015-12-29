package adultadmin.bean.productProperty;

public class CashTicketInfoBean {
	
	public int id;//标识
	
	public String code;//产品编号
	
	public int productId;//产品Id
	
	public String productName;//小店名称

	public String oriname;//产品原名称
	
	public int fullPrice;//赠送规则（满）
	
	public int backPrice;//赠送规则（返）
	
	public int sumStatus;//累加状态  0表示累加,1表示不累加
	
	public String statusName;//产品状态
	
	public int fcStock;//芳村库存
	
	public int gsStock;//广速库存
	
	public String startTime;//赠送开始时间
	
	public String endTime;//赠送结束时间
	
	public int activeStatus;//活动状态  1表示已过期,2表示正使用,3表示未开始,4表示已关闭
	
	public String createDateTime;//创建时间

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(String createDateTime) {
		this.createDateTime = createDateTime;
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

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getOriname() {
		return oriname;
	}

	public void setOriname(String oriname) {
		this.oriname = oriname;
	}

	public int getFullPrice() {
		return fullPrice;
	}

	public void setFullPrice(int fullPrice) {
		this.fullPrice = fullPrice;
	}

	public int getBackPrice() {
		return backPrice;
	}

	public void setBackPrice(int backPrice) {
		this.backPrice = backPrice;
	}

	public int getSumStatus() {
		return sumStatus;
	}

	public void setSumStatus(int sumStatus) {
		this.sumStatus = sumStatus;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public int getFcStock() {
		return fcStock;
	}

	public void setFcStock(int fcStock) {
		this.fcStock = fcStock;
	}

	public int getGsStock() {
		return gsStock;
	}

	public void setGsStock(int gsStock) {
		this.gsStock = gsStock;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(int activeStatus) {
		this.activeStatus = activeStatus;
	}
	
}
