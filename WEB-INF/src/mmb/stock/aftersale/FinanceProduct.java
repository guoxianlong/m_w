package mmb.stock.aftersale;

public class FinanceProduct {
	public int id;
	public int productId;//产品id',
	public float price;//原库存价',
	public double priceSum;//结存金额',
	public double notaxPrice;//不含税价格
	public float priceHasticket;
	public float priceNoticket;
	public float priceSumHasticket;
	public float priceSumNoticket;
	public int countSum;//结存数量',
	public double notaxPriceSum;
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
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public double getPriceSum() {
		return priceSum;
	}
	public void setPriceSum(double priceSum) {
		this.priceSum = priceSum;
	}
	public double getNotaxPrice() {
		return notaxPrice;
	}
	public void setNotaxPrice(double notaxPrice) {
		this.notaxPrice = notaxPrice;
	}
	public float getPriceHasticket() {
		return priceHasticket;
	}
	public void setPriceHasticket(float priceHasticket) {
		this.priceHasticket = priceHasticket;
	}
	public float getPriceNoticket() {
		return priceNoticket;
	}
	public void setPriceNoticket(float priceNoticket) {
		this.priceNoticket = priceNoticket;
	}
	public float getPriceSumHasticket() {
		return priceSumHasticket;
	}
	public void setPriceSumHasticket(float priceSumHasticket) {
		this.priceSumHasticket = priceSumHasticket;
	}
	public float getPriceSumNoticket() {
		return priceSumNoticket;
	}
	public void setPriceSumNoticket(float priceSumNoticket) {
		this.priceSumNoticket = priceSumNoticket;
	}
	public int getCountSum() {
		return countSum;
	}
	public void setCountSum(int countSum) {
		this.countSum = countSum;
	}
	public double getNotaxPriceSum() {
		return notaxPriceSum;
	}
	public void setNotaxPriceSum(double notaxPriceSum) {
		this.notaxPriceSum = notaxPriceSum;
	}
}
