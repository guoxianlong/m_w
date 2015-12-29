package mmb.stock.spare.model;

public class ImeiSpareStockinBean {
    private int id;

    private String imei;

    private int spareStockinId;

    private int spareStockinProductId;

    private int productId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }
    
    public void setImei(String imei) {
		this.imei = imei;
	}

	public int getSpareStockinId() {
        return spareStockinId;
    }

    public void setSpareStockinId(int spareStockinId) {
        this.spareStockinId = spareStockinId;
    }

    public int getSpareStockinProductId() {
        return spareStockinProductId;
    }

    public void setSpareStockinProductId(int spareStockinProductId) {
        this.spareStockinProductId = spareStockinProductId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}