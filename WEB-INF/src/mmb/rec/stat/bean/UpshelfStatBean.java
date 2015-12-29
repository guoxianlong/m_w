package mmb.rec.stat.bean;
/**
 * 
 * @author 张晔
 * 2013-09-24
 * 
 * 上架统计表
 */
public class UpshelfStatBean {

	public int id;//
	public int area;//地区
	public String date;//日期
	public int productLineId;//产品线id
	public int operCount;//上架单数
	public int productCount;//商品件数
	public int skuCount;//SKU数
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
	public int getOperCount() {
		return operCount;
	}
	public void setOperCount(int operCount) {
		this.operCount = operCount;
	}
	public int getProductCount() {
		return productCount;
	}
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	public int getSkuCount() {
		return skuCount;
	}
	public void setSkuCount(int skuCount) {
		this.skuCount = skuCount;
	}
	
}

