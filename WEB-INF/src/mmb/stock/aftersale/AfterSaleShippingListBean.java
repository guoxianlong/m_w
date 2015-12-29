package mmb.stock.aftersale;

/**
 * 维修商品返回用户的发货清单
 * @author 李宁
 * @date 2014-5-26
 */
public class AfterSaleShippingListBean {
	private String productName;//商品名称
	private String productCode;//商品编号
	private String faultDescription;//故障描述
	private String repairCost;//维修费用（报价项）
	private String imei;//imei码
	private String fittings;//配件
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getFaultDescription() {
		return faultDescription;
	}
	public void setFaultDescription(String faultDescription) {
		this.faultDescription = faultDescription;
	}
	public String getRepairCost() {
		return repairCost;
	}
	public void setRepairCost(String repairCost) {
		this.repairCost = repairCost;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getFittings() {
		return fittings;
	}
	public void setFittings(String fittings) {
		this.fittings = fittings;
	}
	
	
}
