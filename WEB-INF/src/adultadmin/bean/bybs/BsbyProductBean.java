package adultadmin.bean.bybs;

public class BsbyProductBean {
	public int id;
	public String product_name;
	public int product_id;
	public int bsby_count;
	public float bsby_price; 
	public int operation_id;
	public String product_code;
	public int after_change;//操作后产品数量
	public int before_change;//操作前产品数量
	public String oriname;//原名称
	public float price; //税前
	public float notaxPrice; //税后
	public BsbyProductCargoBean bsbyCargo;
	public String bsby_before_after_count;
	public String whole_code;
	
	public String parentName1;//商品一级分类名称
	public String parentName2;//商品二级分类名称
	public String parentName3;//商品三级分类名称
	public String productLine;//产品线名称
	public String getWhole_code() {
		return whole_code;
	}
	public void setWhole_code(String whole_code) {
		this.whole_code = whole_code;
	}
	public String getBsby_before_after_count() {
		return bsby_before_after_count;
	}
	public void setBsby_before_after_count(String bsby_before_after_count) {
		this.bsby_before_after_count = bsby_before_after_count;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public int getProduct_id() {
		return product_id;
	}
	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}
	public int getBsby_count() {
		return bsby_count;
	}
	public void setBsby_count(int bsby_count) {
		this.bsby_count = bsby_count;
	}
	public int getOperation_id() {
		return operation_id;
	}
	public void setOperation_id(int operation_id) {
		this.operation_id = operation_id;
	}
	public String getProduct_code() {
		return product_code;
	}
	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}
	public int getAfter_change() {
		return after_change;
	}
	public void setAfter_change(int after_change) {
		this.after_change = after_change;
	}
	public int getBefore_change() {
		return before_change;
	}
	public void setBefore_change(int before_change) {
		this.before_change = before_change;
	}
	public String getOriname() {
		return oriname;
	}
	public void setOriname(String oriname) {
		this.oriname = oriname;
	}
	public float getBsby_price() {
		return bsby_price;
	}
	public void setBsby_price(float bsby_price) {
		this.bsby_price = bsby_price;
	}
	public BsbyProductCargoBean getBsbyCargo() {
		return bsbyCargo;
	}
	public void setBsbyCargo(BsbyProductCargoBean bsbyCargo) {
		this.bsbyCargo = bsbyCargo;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public float getNotaxPrice() {
		return notaxPrice;
	}
	public void setNotaxPrice(float notaxPrice) {
		this.notaxPrice = notaxPrice;
	}
	public String getParentName1() {
		return parentName1;
	}
	public void setParentName1(String parentName1) {
		this.parentName1 = parentName1;
	}
	public String getParentName2() {
		return parentName2;
	}
	public void setParentName2(String parentName2) {
		this.parentName2 = parentName2;
	}
	public String getParentName3() {
		return parentName3;
	}
	public void setParentName3(String parentName3) {
		this.parentName3 = parentName3;
	}
	public String getProductLine() {
		return productLine;
	}
	public void setProductLine(String productLine) {
		this.productLine = productLine;
	}
	
}
