package mmb.stock.spare.model;

public class SpareStockinProductBean {
    private int id;

    private int spareStockinId;

    private int productId;

    private String code;

    private String imei;

    
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
}