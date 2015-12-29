package mmb.stock.spare.model;

public class SpareBackSupplierProduct {
    private int id;

    private int spareBackSupplierId;

    private String code;

    private String imei;

    private int productId;
    
    private String productOriname;



	public String getProductOriname() {
		return productOriname;
	}

	public void setProductOriname(String productOriname) {
		this.productOriname = productOriname;
	}

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSpareBackSupplierId() {
        return spareBackSupplierId;
    }

    public void setSpareBackSupplierId(int spareBackSupplierId) {
        this.spareBackSupplierId = spareBackSupplierId;
    }

    public String getCode() {
        return code;
    }

    public String getImei() {
        return imei;
    }
    
    public void setCode(String code) {
		this.code = code;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}