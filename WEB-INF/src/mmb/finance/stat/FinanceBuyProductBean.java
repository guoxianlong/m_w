package mmb.finance.stat;

public class FinanceBuyProductBean {
	public int id;
	public int productId;
	public int type;
	public int productCount;
	public String createDateTime;
	public int supplierId;
	public double productPrice;
	public String billsNumCode;
	public String buyOrderCode;
	public int balanceMode;
	public double taxPoint;
	public String tCode;
	public int productLineId;
	/**
	 * 供应商税率
	 */
	public double tax;
	
	
	public double getTax() {
		return tax;
	}
	public void setTax(double tax) {
		this.tax = tax;
	}
	public int getProductLineId() {
		return productLineId;
	}
	public void setProductLineId(int productLineId) {
		this.productLineId = productLineId;
	}
	public double getTaxPoint() {
		return taxPoint;
	}
	public void setTaxPoint(double taxPoint) {
		this.taxPoint = taxPoint;
	}
	public String gettCode() {
		return tCode;
	}
	public void settCode(String tCode) {
		this.tCode = tCode;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getProductCount() {
		return productCount;
	}
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	public String getCreateDateTime() {
		return createDateTime;
	}
	public void setCreateDateTime(String createDateTime) {
		this.createDateTime = createDateTime;
	}
	public int getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}
	public double getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
	}
	public String getBillsNumCode() {
		return billsNumCode;
	}
	public void setBillsNumCode(String billsNumCode) {
		this.billsNumCode = billsNumCode;
	}
	public String getBuyOrderCode() {
		return buyOrderCode;
	}
	public void setBuyOrderCode(String buyOrderCode) {
		this.buyOrderCode = buyOrderCode;
	}
	public int getBalanceMode() {
		return balanceMode;
	}
	public void setBalanceMode(int balanceMode) {
		this.balanceMode = balanceMode;
	}
	
	
}
