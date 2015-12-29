package mmb.rec.stat.bean;

import java.io.Serializable;

/**
 * 
 * @author 朱爱林
 *	库存占比
 */
public class StockShareBean implements Serializable{

	/**
	 * 
	 */
	public static final long serialVersionUID = 1L;
	public int id;
	public int area;//地区
	public String date;//时间
	public int productLineId;//产品线id
	public float price;//金额
	public int skuCount;//SKU数
	public int productCount;//商品数
	
	public double priceSum;//金额总和
	public double skuSum;//SKU数的和
	public double productSum;//商品数的和
	
	public double getSkuSum() {
		return skuSum;
	}
	public void setSkuSum(double skuSum) {
		this.skuSum = skuSum;
	}
	public double getProductSum() {
		return productSum;
	}
	public void setProductSum(double productSum) {
		this.productSum = productSum;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getProductLineId() {
		return productLineId;
	}
	public void setProductLineId(int productLineId) {
		this.productLineId = productLineId;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public int getSkuCount() {
		return skuCount;
	}
	public void setSkuCount(int skuCount) {
		this.skuCount = skuCount;
	}
	public int getProductCount() {
		return productCount;
	}
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	public double getPriceSum() {
		return priceSum;
	}
	public void setPriceSum(double priceSum) {
		this.priceSum = priceSum;
	}
}
