package mmb.bi.model;


public class BiProductLineDeliverInfo {
    private Integer id;

    private String createDate;

    private Integer stockArea;

    private Integer provincesId;

    private Integer productLineId;

    private Integer productCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Integer getStockArea() {
        return stockArea;
    }

    public void setStockArea(Integer stockArea) {
        this.stockArea = stockArea;
    }

    public Integer getProvincesId() {
        return provincesId;
    }

    public void setProvincesId(Integer provincesId) {
        this.provincesId = provincesId;
    }

    public Integer getProductLineId() {
        return productLineId;
    }

    public void setProductLineId(Integer productLineId) {
        this.productLineId = productLineId;
    }

    public Integer getProductCount() {
        return productCount;
    }

    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }
}