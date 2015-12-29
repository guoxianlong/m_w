package mmb.stock.fitting.model;

/**
 *用于接收配件入库列表
 */
public class FittingStockinBean {

	private String code;
	private String area;
	private String count;
	private String status;
	private String createDatetime;
	private String createUserName;
	private String affirmUserId;
	private String auditingUserId;
	private String productCode;
	private String oriname;
	private String productCount;
	private String remark;
	private String price;
	private String name;
	private boolean chakan;
	private boolean queren;
	private boolean shenhe;
	private boolean bianji;
	private int type;
	private int fittingType;
	

	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public void setFittingType(int fittingType) {
		this.fittingType = fittingType;
	}

	public String getTypeName() {
		return  FittingBuyStockInBean.typeMap.get((byte)this.type);
	}

	public int getFittingType() {
		return fittingType;
	}

	public String getFittingTypeName() {
		return FittingBuyStockInBean.fittingTypeMap.get((byte)this.fittingType);
	}

	public boolean isChakan() {
		return chakan;
	}
	public void setChakan(boolean chakan) {
		this.chakan = chakan;
	}
	public boolean isQueren() {
		return queren;
	}
	public void setQueren(boolean queren) {
		this.queren = queren;
	}
	public boolean isShenhe() {
		return shenhe;
	}
	public void setShenhe(boolean shenhe) {
		this.shenhe = shenhe;
	}
	public boolean isBianji() {
		return bianji;
	}
	public void setBianji(boolean bianji) {
		this.bianji = bianji;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getOriname() {
		return oriname;
	}
	public void setOriname(String oriname) {
		this.oriname = oriname;
	}
	public String getProductCount() {
		return productCount;
	}
	public void setProductCount(String productCount) {
		this.productCount = productCount;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public String getAffirmUserId() {
		return affirmUserId;
	}
	public void setAffirmUserId(String affirmUserId) {
		this.affirmUserId = affirmUserId;
	}
	public String getAuditingUserId() {
		return auditingUserId;
	}
	public void setAuditingUserId(String auditingUserId) {
		this.auditingUserId = auditingUserId;
	}

}
