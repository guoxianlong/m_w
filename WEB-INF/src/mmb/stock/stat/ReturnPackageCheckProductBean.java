package mmb.stock.stat;

public class ReturnPackageCheckProductBean {
	
	public int id;
	
	public int returnPackageCheckId;  //对应的包裹核查的id
	
	public String productCode; //商品原名称
	
	public int productId; //商品id
	
	public int count;  //核查时的扫描数量

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getReturnPackageCheckId() {
		return returnPackageCheckId;
	}

	public void setReturnPackageCheckId(int returnPackageCheckId) {
		this.returnPackageCheckId = returnPackageCheckId;
	}

}
