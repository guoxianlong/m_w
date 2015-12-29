package adultadmin.bean.stock;

import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;

/**
 * 说明：调拨产品货位信息
 *
 */
public class StockExchangeProductCargoBean {
	
	public int id;      //ID
	public int stockExchangeId;   //调拨单ID
	public int stockExchangeProductId;   //调拨产品ID
	public int cargoProductStockId;      //调拨产品货位信息
	public int cargoInfoId;      //调拨产品货位信息
	public int stockCount;               //货位调拨量
	public int type;                     //类型(0——出库，1——入库)
	public CargoProductStockBean cargoProductStock; //货位库存信息
	public CargoInfoBean cargoInfo;      //货位信息
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStockExchangeProductId() {
		return stockExchangeProductId;
	}
	public void setStockExchangeProductId(int stockExchangeProductId) {
		this.stockExchangeProductId = stockExchangeProductId;
	}
	public int getCargoProductStockId() {
		return cargoProductStockId;
	}
	public void setCargoProductStockId(int cargoProductStockId) {
		this.cargoProductStockId = cargoProductStockId;
	}
	public int getStockCount() {
		return stockCount;
	}
	public void setStockCount(int stockCount) {
		this.stockCount = stockCount;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getStockExchangeId() {
		return stockExchangeId;
	}
	public void setStockExchangeId(int stockExchangeId) {
		this.stockExchangeId = stockExchangeId;
	}
	public CargoProductStockBean getCargoProductStock() {
		return cargoProductStock;
	}
	public void setCargoProductStock(CargoProductStockBean cargoProductStock) {
		this.cargoProductStock = cargoProductStock;
	}
	public CargoInfoBean getCargoInfo() {
		return cargoInfo;
	}
	public void setCargoInfo(CargoInfoBean cargoInfo) {
		this.cargoInfo = cargoInfo;
	}
	public int getCargoInfoId() {
		return cargoInfoId;
	}
	public void setCargoInfoId(int cargoInfoId) {
		this.cargoInfoId = cargoInfoId;
	}
	
}
