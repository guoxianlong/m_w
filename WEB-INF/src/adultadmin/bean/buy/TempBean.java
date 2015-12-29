package adultadmin.bean.buy;

public class TempBean {
	public String productLineName = "";
	public float startStockPriceSum = 0;
	public float totalStockinPrice = 0;
	public float totalStockoutPrice = 0;
	public int totalStockoutCount = 0;
	public String getProductLineName() {
		return productLineName;
	}
	public void setProductLineName(String productLineName) {
		this.productLineName = productLineName;
	}
	public float getStartStockPriceSum() {
		return startStockPriceSum;
	}
	public void setStartStockPriceSum(float startStockPriceSum) {
		this.startStockPriceSum = startStockPriceSum;
	}
	public float getTotalStockinPrice() {
		return totalStockinPrice;
	}
	public void setTotalStockinPrice(float totalStockinPrice) {
		this.totalStockinPrice = totalStockinPrice;
	}
	public float getTotalStockoutPrice() {
		return totalStockoutPrice;
	}
	public void setTotalStockoutPrice(float totalStockoutPrice) {
		this.totalStockoutPrice = totalStockoutPrice;
	}
	public int getTotalStockoutCount() {
		return totalStockoutCount;
	}
	public void setTotalStockoutCount(int totalStockoutCount) {
		this.totalStockoutCount = totalStockoutCount;
	}
	
	
}
