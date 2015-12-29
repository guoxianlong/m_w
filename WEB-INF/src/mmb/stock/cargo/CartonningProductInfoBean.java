package mmb.stock.cargo;

public class CartonningProductInfoBean {
	public int id;
	public int productId;
	public String productCode;
	public int productCount;
	public int cartonningId;
	public String productName;
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
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public int getProductCount() {
		return productCount;
	}
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	public int getCartonningId() {
		return cartonningId;
	}
	public void setCartonningId(int cartonningId) {
		this.cartonningId = cartonningId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
}
