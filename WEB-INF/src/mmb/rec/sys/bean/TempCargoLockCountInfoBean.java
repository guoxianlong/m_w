package mmb.rec.sys.bean;

public class TempCargoLockCountInfoBean {
	
	public int type = 0;
	public int lockCount = 0;
	public int productId;
	public int cargoProductStockId;
	public String cargoWholeCode;
	public String productCode;
	public int cargoInfoId;
	public int area;
	public int cargoArea;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getCargoProductStockId() {
		return cargoProductStockId;
	}
	public void setCargoProductStockId(int cargoProductStockId) {
		this.cargoProductStockId = cargoProductStockId;
	}
	public String getCargoWholeCode() {
		return cargoWholeCode;
	}
	public void setCargoWholeCode(String cargoWholeCode) {
		this.cargoWholeCode = cargoWholeCode;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public int getCargoInfoId() {
		return cargoInfoId;
	}
	public void setCargoInfoId(int cargoInfoId) {
		this.cargoInfoId = cargoInfoId;
	}
	public int getLockCount() {
		return lockCount;
	}
	public void setLockCount(int lockCount) {
		this.lockCount = lockCount;
	}
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	public int getCargoArea() {
		return cargoArea;
	}
	public void setCargoArea(int cargoArea) {
		this.cargoArea = cargoArea;
	}

}
