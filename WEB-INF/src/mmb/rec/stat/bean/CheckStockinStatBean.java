package mmb.rec.stat.bean;
/**
 * 
 * @author 张晔
 * 2013-09-24
 * 
 * 商品入库统计表
 */
public class CheckStockinStatBean {

	public int id;//
	public int area;//地区
	public String date;//日期
	public int productLineId;//产品线id
	public int checkProductCount;//收货质检商品数
	public int checkSkuCount;//收货质检SKU数
	public int upshelfProductCount;//上架商品数
	public int upshelfSkuCount;//上架SKU数
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
	public int getCheckProductCount() {
		return checkProductCount;
	}
	public void setCheckProductCount(int checkProductCount) {
		this.checkProductCount = checkProductCount;
	}
	public int getCheckSkuCount() {
		return checkSkuCount;
	}
	public void setCheckSkuCount(int checkSkuCount) {
		this.checkSkuCount = checkSkuCount;
	}
	public int getUpshelfProductCount() {
		return upshelfProductCount;
	}
	public void setUpshelfProductCount(int upshelfProductCount) {
		this.upshelfProductCount = upshelfProductCount;
	}
	public int getUpshelfSkuCount() {
		return upshelfSkuCount;
	}
	public void setUpshelfSkuCount(int upshelfSkuCount) {
		this.upshelfSkuCount = upshelfSkuCount;
	}
	
}

