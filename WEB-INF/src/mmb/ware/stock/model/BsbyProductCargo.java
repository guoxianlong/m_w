package mmb.ware.stock.model;

import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;

public class BsbyProductCargo {
    private Integer id;

    private Integer bsbyOperId;

    private Integer bsbyProductId;

    private Integer count;

    private Integer cargoProductStockId;

    private Integer cargoId;
    
    private CargoProductStockBean cps;
	private CargoInfoBean cargoInfo;

    public CargoProductStockBean getCps() {
		return cps;
	}

	public void setCps(CargoProductStockBean cps) {
		this.cps = cps;
	}

	public CargoInfoBean getCargoInfo() {
		return cargoInfo;
	}

	public void setCargoInfo(CargoInfoBean cargoInfo) {
		this.cargoInfo = cargoInfo;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBsbyOperId() {
        return bsbyOperId;
    }

    public void setBsbyOperId(Integer bsbyOperId) {
        this.bsbyOperId = bsbyOperId;
    }

    public Integer getBsbyProductId() {
        return bsbyProductId;
    }

    public void setBsbyProductId(Integer bsbyProductId) {
        this.bsbyProductId = bsbyProductId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCargoProductStockId() {
        return cargoProductStockId;
    }

    public void setCargoProductStockId(Integer cargoProductStockId) {
        this.cargoProductStockId = cargoProductStockId;
    }

    public Integer getCargoId() {
        return cargoId;
    }

    public void setCargoId(Integer cargoId) {
        this.cargoId = cargoId;
    }
}