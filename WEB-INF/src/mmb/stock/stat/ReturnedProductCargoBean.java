package mmb.stock.stat;

import adultadmin.action.vo.voProduct;
import adultadmin.bean.cargo.CargoInfoBean;

public class ReturnedProductCargoBean {
	
	public int cargoId;
	public int count;
	public int id;
	public int productId;
	public voProduct product;
	public CargoInfoBean cargoInfo;
	public int targetCargoId;
	public String targetCargoCode;
	
	public String getTargetCargoCode() {
		return targetCargoCode;
	}
	public void setTargetCargoCode(String targetCargoCode) {
		this.targetCargoCode = targetCargoCode;
	}
	public int getTargetCargoId() {
		return targetCargoId;
	}
	public void setTargetCargoId(int targetCargoId) {
		this.targetCargoId = targetCargoId;
	}
	public voProduct getProduct() {
		return product;
	}
	public void setProduct(voProduct product) {
		this.product = product;
	}
	public CargoInfoBean getCargoInfo() {
		return cargoInfo;
	}
	public void setCargoInfo(CargoInfoBean cargoInfo) {
		this.cargoInfo = cargoInfo;
	}
	public int getCargoId() {
		return cargoId;
	}
	public int getCount() {
		return count;
	}
	public int getId() {
		return id;
	}
	public int getProductId() {
		return productId;
	}
	public void setCargoId(int cargoId) {
		this.cargoId = cargoId;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}

}
