package mmb.rec.sys.bean;

/**
 * 用于计算订单发货时锁定量用到的临时Bean
 * @author Administrator
 *
 */
public class TempOrderStockLockCountInfoBean {
	
	public int orderStockId;
	public int productId;
	public int lockCount;
	public int count;
	public int area;
	public int cargoProductStockId;
	public String cargoWholeCode;
	
	
	public int getOrderStockId() {
		return orderStockId;
	}
	public void setOrderStockId(int orderStockId) {
		this.orderStockId = orderStockId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
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
	public String getCargoWholeCode() {
		return cargoWholeCode;
	}
	public void setCargoWholeCode(String cargoWholeCode) {
		this.cargoWholeCode = cargoWholeCode;
	}
	public int getCargoProductStockId() {
		return cargoProductStockId;
	}
	public void setCargoProductStockId(int cargoProductStockId) {
		this.cargoProductStockId = cargoProductStockId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	

}
