package mmb.rec.oper.bean;

import adultadmin.bean.stock.ProductStockBean;

public class ConsignmentProductTempBean {
	
	/**
	 * 产品编号
	 */
	public String productCode;
	/**
	 * 产品名称
	 */
	public String productName;
	/**
	 * 货位库存量
	 */
	public int stockCount;
	/**
	 * 货位锁定量
	 */
	public int stockLockCount;
	/**
	 * 商品id
	 */
	public int productId;
	/**
	 * 货位库存id
	 */
	public int CargoProductStockId;
	/**
	 * 货位id
	 */
	public int cargoInfoId;
	/**
	 * 地区
	 */
	public int areaId;
	
	/**
	 * 地区名称
	 */
	public String areaName;
	/**
	 * 货位编号全
	 */
	public String wholeCode;
	
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getStockCount() {
		return stockCount;
	}
	public void setStockCount(int stockCount) {
		this.stockCount = stockCount;
	}
	public int getStockLockCount() {
		return stockLockCount;
	}
	public void setStockLockCount(int stockLockCount) {
		this.stockLockCount = stockLockCount;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getCargoProductStockId() {
		return CargoProductStockId;
	}
	public void setCargoProductStockId(int cargoProductStockId) {
		CargoProductStockId = cargoProductStockId;
	}
	public int getCargoInfoId() {
		return cargoInfoId;
	}
	public void setCargoInfoId(int cargoInfoId) {
		this.cargoInfoId = cargoInfoId;
	}
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
		this.setAreaName(ProductStockBean.getAreaName(areaId));
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getWholeCode() {
		return wholeCode;
	}
	public void setWholeCode(String wholeCode) {
		this.wholeCode = wholeCode;
	}
	

}
