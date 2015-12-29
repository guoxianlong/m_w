package mmb.dcheck.model;

import mmb.ware.cargo.model.CargoInfo;
import mmb.ware.cargo.model.CargoProductStock;
import adultadmin.bean.stock.ProductStockBean;

public class DynamicCheckCargoDifferenceBean {

	/**
	 * 自增id
	 */
    private int id;

    /**
     * 库地区id
     */
    private int areaId;

    /**
     * 货位id
     */
    private int cargoId;

    /**
     * 货位号
     */
    private String cargoWholeCode;

    /**
     * 货位区域id
     */
    private int cargoInfoStockAreaId;

    /**
     * 商品id
     */
    private int productId;

    /**
     * 产品编号 
     */
    private String productCode;

    /**
     * 产品原名称
     */
    private String productName;

    /**
     * 差异量
     */
    private int difference;
    /**
     * 状态
     */
    private int status;
    
    /**
     * 地区名称
     */
    private String areaName;
   
    /**
     * 非实例化
     * 差异量绝对值
     */
    private int absCount;
    
    /**
     * 非实例化
     * CargoInfo类
     */
    private CargoInfo cargoInfo;
    
    /**
     * 非实例化
     * 货位库存类
     */
    private CargoProductStock cargoProductStock;
    
    /**
     * 未处理
     */
    public static final int STATUS1 = 1;
    
    /**
     * 已处理
     */
    public static final int STATUS2 = 2;
    
    /**
     * 已作废
     */
    public static final int STATUS3 = 3;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public int getCargoId() {
        return cargoId;
    }

    public void setCargoId(int cargoId) {
        this.cargoId = cargoId;
    }

    public String getCargoWholeCode() {
        return cargoWholeCode;
    }

    public void setCargoWholeCode(String cargoWholeCode) {
        this.cargoWholeCode = cargoWholeCode == null ? null : cargoWholeCode.trim();
    }

    public int getCargoInfoStockAreaId() {
        return cargoInfoStockAreaId;
    }

    public void setCargoInfoStockAreaId(int cargoInfoStockAreaId) {
        this.cargoInfoStockAreaId = cargoInfoStockAreaId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode == null ? null : productCode.trim();
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public int getDifference() {
        return difference;
    }

    public void setDifference(int difference) {
        this.difference = difference;
    }

	public String getAreaName() {
		this.areaName = ProductStockBean.getAreaName(areaId);
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getAbsCount() {
		return absCount;
	}

	public void setAbsCount(int absCount) {
		this.absCount = absCount;
	}

	public CargoInfo getCargoInfo() {
		return cargoInfo;
	}

	public void setCargoInfo(CargoInfo cargoInfo) {
		this.cargoInfo = cargoInfo;
	}

	public CargoProductStock getCargoProductStock() {
		return cargoProductStock;
	}

	public void setCargoProductStock(CargoProductStock cargoProductStock) {
		this.cargoProductStock = cargoProductStock;
	}
}