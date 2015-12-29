package mmb.stock.stat;

import mmb.stock.cargo.CartonningInfoBean;
import adultadmin.bean.buy.BuyStockinBean;
import adultadmin.bean.cargo.CargoOperationBean;

public class BuyStockinUpshelfBean {
	
	public int id;
	
	public String buyStockinDatetime; //入库时间
	
	public int buyStockinId;  //入库单id

	public int cargoOperationId;  //上架单id

	public int cartonningInfoId; // 装箱单id

	public String cartonningInfoName; //装箱人名
	
	public int productId; //商品id

	public String productCode; //商品编号
	
	public int wareArea; //地区
	
	public BuyStockinBean buyStockinBean;  //入库单信息
	
	public CartonningInfoBean cartonningInfoBean; //装箱单信息
	
	public CargoOperationBean cargoOperationBean; //上架单信息 

	
	public String getBuyStockinDatetime() {
		return buyStockinDatetime;
	}

	public int getBuyStockinId() {
		return buyStockinId;
	}

	public int getCargoOperationId() {
		return cargoOperationId;
	}

	public int getCartonningInfoId() {
		return cartonningInfoId;
	}

	public String getCartonningInfoName() {
		return cartonningInfoName;
	}

	public int getId() {
		return id;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setBuyStockinDatetime(String buyStockinDatetime) {
		this.buyStockinDatetime = buyStockinDatetime;
	}

	public void setBuyStockinId(int buyStockinId) {
		this.buyStockinId = buyStockinId;
	}
	
	public void setCargoOperationId(int cargoOperationId) {
		this.cargoOperationId = cargoOperationId;
	}
	
	public void setCartonningInfoId(int cartonningInfoId) {
		this.cartonningInfoId = cartonningInfoId;
	}
	
	public void setCartonningInfoName(String cartonningInfoName) {
		this.cartonningInfoName = cartonningInfoName;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public int getWareArea() {
		return wareArea;
	}

	public void setWareArea(int wareArea) {
		this.wareArea = wareArea;
	}

	public BuyStockinBean getBuyStockinBean() {
		return buyStockinBean;
	}

	public void setBuyStockinBean(BuyStockinBean buyStockinBean) {
		this.buyStockinBean = buyStockinBean;
	}

	public CartonningInfoBean getCartonningInfoBean() {
		return cartonningInfoBean;
	}

	public void setCartonningInfoBean(CartonningInfoBean cartonningInfoBean) {
		this.cartonningInfoBean = cartonningInfoBean;
	}

	public CargoOperationBean getCargoOperationBean() {
		return cargoOperationBean;
	}

	public void setCargoOperationBean(CargoOperationBean cargoOperationBean) {
		this.cargoOperationBean = cargoOperationBean;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

}
