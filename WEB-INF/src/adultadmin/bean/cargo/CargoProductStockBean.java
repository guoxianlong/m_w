package adultadmin.bean.cargo;

import java.util.List;

import adultadmin.action.vo.voProduct;

public class CargoProductStockBean {
	public int id;              //ID
	public int cargoId;         //货位信息ID
	public int productId;       //产品ID
	public int stockCount;      //货位库存量
	public int stockLockCount;  //货位库存锁定量
	public CargoInfoBean cargoInfo;  //货位信息
	public voProduct product;   //产品信息
	public List cartonningList; //关联的装箱单列表
	
	private CargoInfoBean targetCargoInfo;//目的货位
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCargoId() {
		return cargoId;
	}
	public void setCargoId(int cargoId) {
		this.cargoId = cargoId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
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
	public CargoInfoBean getCargoInfo() {
		return cargoInfo;
	}
	public void setCargoInfo(CargoInfoBean cargoInfo) {
		this.cargoInfo = cargoInfo;
	}
	public voProduct getProduct() {
		return product;
	}
	public void setProduct(voProduct product) {
		this.product = product;
	}
	public List getCartonningList() {
		return cartonningList;
	}
	public void setCartonningList(List cartonningList) {
		this.cartonningList = cartonningList;
	}
	public CargoInfoBean getTargetCargoInfo() {
		return targetCargoInfo;
	}
	public void setTargetCargoInfo(CargoInfoBean targetCargoInfo) {
		this.targetCargoInfo = targetCargoInfo;
	}
	
}
