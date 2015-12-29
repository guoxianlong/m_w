package adultadmin.bean.cargo;

import adultadmin.action.vo.voProduct;

/**
 * 说明：盘点作业单任务商品库存数据
 *
 */
public class CargoInventoryMissionProductBean {

	public int id;    //ID
	
	public int cargoInventoryId;   //盘点作业单ID
	
	public int cargoInventoryMissionId;   //盘点作业单任务ID
	
	public int productId;   //产品ID
	
	public int productParentId1;  //产品一级分类
	
	public String productCode;   //产品编号
	
	public String productBarcode;  //产品条码
	
	public String productOriname;  //产品原名称
	
	public int stockCount;   //库存数量
	
	public voProduct product;   //产品信息

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCargoInventoryId() {
		return cargoInventoryId;
	}

	public void setCargoInventoryId(int cargoInventoryId) {
		this.cargoInventoryId = cargoInventoryId;
	}

	public int getCargoInventoryMissionId() {
		return cargoInventoryMissionId;
	}

	public void setCargoInventoryMissionId(int cargoInventoryMissionId) {
		this.cargoInventoryMissionId = cargoInventoryMissionId;
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

	public String getProductBarcode() {
		return productBarcode;
	}

	public void setProductBarcode(String productBarcode) {
		this.productBarcode = productBarcode;
	}

	public int getStockCount() {
		return stockCount;
	}

	public void setStockCount(int stockCount) {
		this.stockCount = stockCount;
	}

	public voProduct getProduct() {
		return product;
	}

	public void setProduct(voProduct product) {
		this.product = product;
	}

	public String getProductOriname() {
		return productOriname;
	}

	public void setProductOriname(String productOriname) {
		this.productOriname = productOriname;
	}

	public int getProductParentId1() {
		return productParentId1;
	}

	public void setProductParentId1(int productParentId1) {
		this.productParentId1 = productParentId1;
	}
	
}
