package mmb.stock.spare.model;

public class SpareBean {
    private int id;

    private int spareStockinId;

    private int productId;

    private String code;

    private String imei;

    private String cargoWholeCode;

    private int status;

    /**
     * 可出库
     */
    public static int STATUS_STOCK_OUT = 1;
    /**
     * 返回供应商
     */
    public static int STATUS_BACK_SUPPLIER = 2;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSpareStockinId() {
        return spareStockinId;
    }

    public void setSpareStockinId(int spareStockinId) {
        this.spareStockinId = spareStockinId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
		this.code = code;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getImei() {
        return imei;
    }

    public String getCargoWholeCode() {
		return cargoWholeCode;
	}

	public void setCargoWholeCode(String cargoWholeCode) {
		this.cargoWholeCode = cargoWholeCode;
	}

	public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}