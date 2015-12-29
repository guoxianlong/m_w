package mmb.rec.stat.bean;

public class CheckStockinMissionFormBean {

	private String productLine;
	private String beginStockinTime;
	private String endStockinTime;
	private String beginCompleteTime;
	private String endCompleteTime;
	private String productCode;
	private String missionCode;
	private String buyStockCode;//预计单号
	private String missionStatus;
	private String priority;//优先级
	private String beginConsumTime;//耗时起始时间
	private String endConsumTime;//耗时结束时间
	private String productLoad;//产能负荷
	private String createUserName;
	private String supplyId;//供应商id
	private String wareArea;
	
	public String getProductLine() {
		return productLine;
	}
	public void setProductLine(String productLine) {
		this.productLine = productLine;
	}
	public String getBeginStockinTime() {
		return beginStockinTime;
	}
	public void setBeginStockinTime(String beginStockinTime) {
		this.beginStockinTime = beginStockinTime;
	}
	public String getEndStockinTime() {
		return endStockinTime;
	}
	public void setEndStockinTime(String endStockinTime) {
		this.endStockinTime = endStockinTime;
	}
	public String getBeginCompleteTime() {
		return beginCompleteTime;
	}
	public void setBeginCompleteTime(String beginCompleteTime) {
		this.beginCompleteTime = beginCompleteTime;
	}
	public String getEndCompleteTime() {
		return endCompleteTime;
	}
	public void setEndCompleteTime(String endCompleteTime) {
		this.endCompleteTime = endCompleteTime;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getMissionCode() {
		return missionCode;
	}
	public void setMissionCode(String missionCode) {
		this.missionCode = missionCode;
	}
	public String getBuyStockCode() {
		return buyStockCode;
	}
	public void setBuyStockCode(String buyStockCode) {
		this.buyStockCode = buyStockCode;
	}
	public String getMissionStatus() {
		return missionStatus;
	}
	public void setMissionStatus(String missionStatus) {
		this.missionStatus = missionStatus;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getProductLoad() {
		return productLoad;
	}
	public void setProductLoad(String productLoad) {
		this.productLoad = productLoad;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public String getBeginConsumTime() {
		return beginConsumTime;
	}
	public void setBeginConsumTime(String beginConsumTime) {
		this.beginConsumTime = beginConsumTime;
	}
	public String getEndConsumTime() {
		return endConsumTime;
	}
	public void setEndConsumTime(String endConsumTime) {
		this.endConsumTime = endConsumTime;
	}
	public String getSupplyId() {
		return supplyId;
	}
	public void setSupplyId(String supplyId) {
		this.supplyId = supplyId;
	}
	public String getWareArea() {
		return wareArea;
	}
	public void setWareArea(String wareArea) {
		this.wareArea = wareArea;
	}
}

