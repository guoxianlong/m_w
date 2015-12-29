package mmb.stock.spare.model;

import adultadmin.bean.stock.ProductStockBean;

public class SpareCargoProductStock {
	private int id;
	private String cargoCode;
	private String spareCode;
	private String productCode;
	private int stockCount;
	private String productOriName;
	private int areaId;
	
	public String getAreaName()	{
		return ProductStockBean.getAreaName(this.areaId);		
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCargoCode() {
		return cargoCode;
	}
	public void setCargoCode(String cargoCode) {
		this.cargoCode = cargoCode;
	}
	public String getSpareCode() {
		return spareCode;
	}
	public void setSpareCode(String spareCode) {
		this.spareCode = spareCode;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public int getStockCount() {
		return stockCount;
	}
	public void setStockCount(int stockCount) {
		this.stockCount = stockCount;
	}
	public String getProductOriName() {
		return productOriName;
	}
	public void setProductOriName(String productOriName) {
		this.productOriName = productOriName;
	}
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
}
