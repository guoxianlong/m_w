package mmb.stock.spare.model;

import adultadmin.bean.stock.ProductStockBean;

public class SpareUpShelves {
	private int id;
	private String productCode;
	private String oriname;
	private String spareCode;
	private String supplierName;
	private int areaId;
	private String createDatetime;
	
	public String getAreaName() {
		return ProductStockBean.getAreaName(this.areaId);
	}
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
	public String getOriname() {
		return oriname;
	}
	public void setOriname(String oriname) {
		this.oriname = oriname;
	}
	public String getSpareCode() {
		return spareCode;
	}
	public void setSpareCode(String spareCode) {
		this.spareCode = spareCode;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	
}
