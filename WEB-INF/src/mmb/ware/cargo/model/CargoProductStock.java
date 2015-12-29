package mmb.ware.cargo.model;

import java.util.List;

import adultadmin.action.vo.voProduct;
import adultadmin.bean.cargo.CargoInfoBean;

public class CargoProductStock {
    private Integer id;

    private Integer cargoId;

    private Integer productId;

    private Integer stockCount;

    private Integer stockLockCount;
	public CargoInfoBean cargoInfo;  //货位信息
	public voProduct product;   //产品信息
	public List cartonningList; //关联的装箱单列表
	
	private CargoInfoBean targetCargoInfo;//目的货位

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

	public Integer getCargoId() {
        return cargoId;
    }

    public void setCargoId(Integer cargoId) {
        this.cargoId = cargoId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Integer getStockLockCount() {
        return stockLockCount;
    }

    public void setStockLockCount(Integer stockLockCount) {
        this.stockLockCount = stockLockCount;
    }
}