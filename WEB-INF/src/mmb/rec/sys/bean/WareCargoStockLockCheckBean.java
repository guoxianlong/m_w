package mmb.rec.sys.bean;

/**
 * 排查合格库货位锁定库存和单据锁定库存是否对的上的问题用到的记录Bean
 * @author 郝亚斌
 *
 */
public class WareCargoStockLockCheckBean {
	
	public int id;
	public int cargoId;
	public int cargoProductStockId;
	public int productId;
	public int wareArea;
	public String cargoWholeCode;
	public int cargoLockCount;
	
	public int orderLockCount;
	public String createDatetime;
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
	public int getCargoProductStockId() {
		return cargoProductStockId;
	}
	public void setCargoProductStockId(int cargoProductStockId) {
		this.cargoProductStockId = cargoProductStockId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getWareArea() {
		return wareArea;
	}
	public void setWareArea(int wareArea) {
		this.wareArea = wareArea;
	}
	public String getCargoWholeCode() {
		return cargoWholeCode;
	}
	public void setCargoWholeCode(String cargoWholeCode) {
		this.cargoWholeCode = cargoWholeCode;
	}
	public int getCargoLockCount() {
		return cargoLockCount;
	}
	public void setCargoLockCount(int cargoLockCount) {
		this.cargoLockCount = cargoLockCount;
	}
	public int getOrderLockCount() {
		return orderLockCount;
	}
	public void setOrderLockCount(int orderLockCount) {
		this.orderLockCount = orderLockCount;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

}

