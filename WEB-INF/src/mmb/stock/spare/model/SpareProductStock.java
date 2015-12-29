package mmb.stock.spare.model;

import java.util.HashMap;

import adultadmin.bean.stock.ProductStockBean;

public class SpareProductStock {
	private int id;
	private String productCode ;
	private String productOriName;
	private String productName;
	private String supplierName;
	private HashMap<Integer, Integer> stock = new HashMap<Integer, Integer>();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSupplierName() {
		if(supplierName == null || supplierName.indexOf(',') == -1)			
			return supplierName;
		return supplierName.replaceAll(",", "<br/>");
	}	
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getProductOriName() {
		return productOriName;
	}
	public void setProductOriName(String productOriName) {
		this.productOriName = productOriName;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public HashMap<Integer, Integer> getStock() {
		return stock;
	}
	
	
	/**
	 * 获取深圳库存
	 * @return
	 */
	public Integer getSzStock(){
		if(!stock.containsKey(ProductStockBean.AREA_SZ))
			return Integer.valueOf(0);
		return stock.get(ProductStockBean.AREA_SZ);
	}
}
