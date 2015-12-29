package adultadmin.bean.afterSales;

/**
 * 
 *  <code>AfterSaleOrderProduct.java</code>
 *  <p>功能:售后订单产品
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 李双 lishuang@ebinf.com 时间 Dec 1, 2011 11:20:20 AM	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class AfterSaleOrderProduct {
	
	public int id;
	public int afterSaleOrderId;
	public int orderId;
	public int productId;
 	public int acount=1;// 默认添加的产品数量为 1 在退换货的时候修改
	private String productName;
 	private String productCode;
 	private String barcode;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
 
	public int getAfterSaleOrderId() {
		return afterSaleOrderId;
	}
	public void setAfterSaleOrderId(int afterSaleOrderId) {
		this.afterSaleOrderId = afterSaleOrderId;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getAcount() {
		return acount;
	}
	public void setAcount(int acount) {
		this.acount = acount;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	
}
