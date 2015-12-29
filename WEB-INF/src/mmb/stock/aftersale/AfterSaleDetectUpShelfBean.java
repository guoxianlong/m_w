package mmb.stock.aftersale;


/**已检测商品上架任务列表
 * 列表bean-与数据表无对应关系。
 * @author hp
 *
 */

public class AfterSaleDetectUpShelfBean {
	
	private int productId;//商品Id
	private String productCode;//商品编号
	private String shopName;//小店名称
	private String afterSaleOrderCode;//售后单号	
	private String afterSaleOpinion;//处理意见
	private String afterSaleStatus;//售后处理单状态
	private String afterSaleCode;//售后处理单号
	private int cargoId;//货位id
	private String cargoWholeCode;//目的货位号
	private String checkTime;//检测完成时间
	private String backSupplierStatus;// 返厂状态
	

	public void setCargoId(int cargoId) {
		this.cargoId = cargoId;
	}	
	public int getCargoId() {
		return cargoId;
	}
	public String getBackSupplierStatus() {
		return backSupplierStatus;
	}
	public void setBackSupplierStatus(String backSupplierStatus) {
		this.backSupplierStatus = backSupplierStatus;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public String getAfterSaleOrderCode() {
		return afterSaleOrderCode;
	}
	public void setAfterSaleOrderCode(String afterSaleOrderCode) {
		this.afterSaleOrderCode = afterSaleOrderCode;
	}
	public String getAfterSaleOpinion() {
		return afterSaleOpinion;
	}
	public void setAfterSaleOpinion(String afterSaleOpinion) {
		this.afterSaleOpinion = afterSaleOpinion;
	}
	public String getAfterSaleStatus() {
		return afterSaleStatus;
	}
	public void setAfterSaleStatus(String afterSaleStatus) {
		this.afterSaleStatus = afterSaleStatus;
	}
	public String getAfterSaleCode() {
		return afterSaleCode;
	}
	public void setAfterSaleCode(String afterSaleCode) {
		this.afterSaleCode = afterSaleCode;
	}
	public String getCargoWholeCode() {
		return cargoWholeCode;
	}
	public void setCargoWholeCode(String cargoWholeCode) {
		this.cargoWholeCode = cargoWholeCode;
	}
	public String getCheckTime() {
		return checkTime;
	}
	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}
	
	
	

}
