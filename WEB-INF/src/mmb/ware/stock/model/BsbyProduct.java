package mmb.ware.stock.model;

public class BsbyProduct {
    private Integer id;

    private String productName;

    private Integer productId;

    private Integer bsbyCount;

    private Float bsbyPrice;

    private Integer operationId;

    private String productCode;

    private Integer beforeChange;

    private Integer afterChange;

    private String oriname;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getBsbyCount() {
        return bsbyCount;
    }

    public void setBsbyCount(Integer bsbyCount) {
        this.bsbyCount = bsbyCount;
    }

    public Float getBsbyPrice() {
        return bsbyPrice;
    }

    public void setBsbyPrice(Float bsbyPrice) {
        this.bsbyPrice = bsbyPrice;
    }

    public Integer getOperationId() {
        return operationId;
    }

    public void setOperationId(Integer operationId) {
        this.operationId = operationId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode == null ? null : productCode.trim();
    }

    public Integer getBeforeChange() {
        return beforeChange;
    }

    public void setBeforeChange(Integer beforeChange) {
        this.beforeChange = beforeChange;
    }

    public Integer getAfterChange() {
        return afterChange;
    }

    public void setAfterChange(Integer afterChange) {
        this.afterChange = afterChange;
    }

    public String getOriname() {
        return oriname;
    }

    public void setOriname(String oriname) {
        this.oriname = oriname == null ? null : oriname.trim();
    }
}