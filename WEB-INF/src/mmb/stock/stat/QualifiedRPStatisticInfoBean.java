package mmb.stock.stat;

public class QualifiedRPStatisticInfoBean {

	private String passageCode;//巷道号
	private int productSKU;//产品sku数量
	private int productCount;//产品总数量
	private String passageWholeCode;//全称巷道号
	private boolean selectFlag;//是否可以选择
	
	public String getPassageCode() {
		return passageCode;
	}
	public void setPassageCode(String passageCode) {
		this.passageCode = passageCode;
	}
	public int getProductSKU() {
		return productSKU;
	}
	public void setProductSKU(int productSKU) {
		this.productSKU = productSKU;
	}
	public int getProductCount() {
		return productCount;
	}
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	public String getPassageWholeCode() {
		return passageWholeCode;
	}
	public void setPassageWholeCode(String passageWholeCode) {
		this.passageWholeCode = passageWholeCode;
	}
	public boolean isSelectFlag() {
		return selectFlag;
	}
	public void setSelectFlag(boolean selectFlag) {
		this.selectFlag = selectFlag;
	}
}
