package mmb.stock.stat;


/**
 * @name 质检入库明细
 * @author LB
 *
 */
public class CheckStockinMissionDetailBean {
	
	public String buyStockinCode; //采购入库单编号
	public int buyStockinCount; //采购入库单商品数量
	public int buyStockinId; //采购入库单id
	public int id;	
	public int missionId; //质检任务id
	public String buyStockinCreateDateTime; //入库单生成时间
	public String productCode; //产品条码
	public int productId; //产品id
	
	private String buyStockinStatus;//入库状态
	private String cartonningName;//装箱单号
	
	public int getId() {
		return id;
	}
	public int getMissionId() {
		return missionId;
	}
	public String getProductCode() {
		return productCode;
	}
	public int getProductId() {
		return productId;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setMissionId(int missionId) {
		this.missionId = missionId;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getBuyStockinCode() {
		return buyStockinCode;
	}
	public void setBuyStockinCode(String buyStockinCode) {
		this.buyStockinCode = buyStockinCode;
	}
	public int getBuyStockinCount() {
		return buyStockinCount;
	}
	public void setBuyStockinCount(int buyStockinCount) {
		this.buyStockinCount = buyStockinCount;
	}
	public int getBuyStockinId() {
		return buyStockinId;
	}
	public void setBuyStockinId(int buyStockinId) {
		this.buyStockinId = buyStockinId;
	}
	public String getBuyStockinCreateDateTime() {
		return buyStockinCreateDateTime;
	}
	public void setBuyStockinCreateDateTime(String buyStockinCreateDateTime) {
		this.buyStockinCreateDateTime = buyStockinCreateDateTime;
	}
	public String getBuyStockinStatus() {
		return buyStockinStatus;
	}
	public void setBuyStockinStatus(String buyStockinStatus) {
		this.buyStockinStatus = buyStockinStatus;
	}
	public String getCartonningName() {
		return cartonningName;
	}
	public void setCartonningName(String cartonningName) {
		this.cartonningName = cartonningName;
	}
}
