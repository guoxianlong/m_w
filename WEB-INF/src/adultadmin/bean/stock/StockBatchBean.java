package adultadmin.bean.stock;

import adultadmin.action.vo.voProduct;

/**
 * 作者：赵林
 * 
 * 创建时间：2009-08-06
 * 
 * 说明：库存批次Bean
 *
 */
public class StockBatchBean {
	
	public int id;              //ID
	
	public String code;         //批次号
	
	public int productId;       //产品ID
	
	public voProduct product;   //对应产品
	
	public float price;         //入库价、退货价
	
	public float notaxPrice; //不含税库存均价
	
	public int batchCount;      //批次货量
	
	public int stockArea;       //库地区类别
	
	public int stockType;       //库类别
	
	public int productStockId;  //库存ID
	
	public String createDateTime;   //添加时间
	
	public String productLineName;  //产品线
	
	public String supplierName; //供应商名称
	
	public int availableCount; //可退货量
	
	public int applierCount; //正在申请中
	
	public int supplierId; //供应商id
	
	public float tax; //供应商税率
	
	public int ticket;//有票无票
	
	public int getAvailableCount() {
		return availableCount;
	}

	public void setAvailableCount(int availableCount) {
		this.availableCount = availableCount;
	}

	public int getApplierCount() {
		return applierCount;
	}

	public void setApplierCount(int applierCount) {
		this.applierCount = applierCount;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getProductLineName() {
		return productLineName;
	}

	public void setProductLineName(String productLineName) {
		this.productLineName = productLineName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public voProduct getProduct() {
		return product;
	}

	public void setProduct(voProduct product) {
		this.product = product;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getBatchCount() {
		return batchCount;
	}

	public void setBatchCount(int batchCount) {
		this.batchCount = batchCount;
	}

	public int getStockType() {
		return stockType;
	}

	public void setStockType(int stockType) {
		this.stockType = stockType;
	}
	
	public int getStockArea() {
		return stockArea;
	}

	public void setStockArea(int stockArea) {
		this.stockArea = stockArea;
	}
	
	public String getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(String createDateTime) {
		this.createDateTime = createDateTime;
	}

	public int getProductStockId() {
		return productStockId;
	}

	public void setProductStockId(int productStockId) {
		this.productStockId = productStockId;
	}
	
	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	public float getTax() {
		return tax;
	}

	public void setTax(float tax) {
		this.tax = tax;
	}

	public float getNotaxPrice() {
		return notaxPrice;
	}

	public void setNotaxPrice(float notaxPrice) {
		this.notaxPrice = notaxPrice;
	}

	public int getTicket() {
		return ticket;
	}

	public void setTicket(int ticket) {
		this.ticket = ticket;
	}
	
}
