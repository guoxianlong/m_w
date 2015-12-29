package adultadmin.bean.bybs;

import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;

/**
 * 说明：报损报溢产品货位库存信息
 *
 */
public class BsbyProductCargoBean {
	
	public int id;   //ID
	public int bsbyOperId;  //报损报溢操作单ID
	public int bsbyProductId;   //报损报溢产品ID
	public int count;       //报损报溢数量
	public int cargoProductStockId;  //产品货位库存ID
	public int cargoId;              //货位信息ID
	public CargoProductStockBean cps;
	public CargoInfoBean cargoInfo;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getBsbyOperId() {
		return bsbyOperId;
	}
	public void setBsbyOperId(int bsbyOperId) {
		this.bsbyOperId = bsbyOperId;
	}
	public int getBsbyProductId() {
		return bsbyProductId;
	}
	public void setBsbyProductId(int bsbyProductId) {
		this.bsbyProductId = bsbyProductId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getCargoProductStockId() {
		return cargoProductStockId;
	}
	public void setCargoProductStockId(int cargoProductStockId) {
		this.cargoProductStockId = cargoProductStockId;
	}
	public CargoProductStockBean getCps() {
		return cps;
	}
	public void setCps(CargoProductStockBean cps) {
		this.cps = cps;
	}
	public int getCargoId() {
		return cargoId;
	}
	public void setCargoId(int cargoId) {
		this.cargoId = cargoId;
	}
	public CargoInfoBean getCargoInfo() {
		return cargoInfo;
	}
	public void setCargoInfo(CargoInfoBean cargoInfo) {
		this.cargoInfo = cargoInfo;
	}
	
}
