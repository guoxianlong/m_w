package mmb.stock.afStock.model;

public class AfterSaleBackUserProductBean {
    private Integer id;

    private Integer afterSaleDetectProductId;

    private Integer packageId;

    private Integer productId;

    private Byte type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAfterSaleDetectProductId() {
        return afterSaleDetectProductId;
    }

    public void setAfterSaleDetectProductId(Integer afterSaleDetectProductId) {
        this.afterSaleDetectProductId = afterSaleDetectProductId;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }
}