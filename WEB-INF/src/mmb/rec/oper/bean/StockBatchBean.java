package mmb.rec.oper.bean;

public class StockBatchBean {
    private int id;

    private String code;

    private int productId;

    private float price;

    private int batchCount;

    private int stockArea;

    private int stockType;

    private int productStockId;

    private String createDatetime;

    private int ticket;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getBatchCount() {
        return batchCount;
    }

    public void setBatchCount(int batchCount) {
        this.batchCount = batchCount;
    }

    public int getStockArea() {
        return stockArea;
    }

    public void setStockArea(int stockArea) {
        this.stockArea = stockArea;
    }

    public int getStockType() {
        return stockType;
    }

    public void setStockType(int stockType) {
        this.stockType = stockType;
    }

    public int getProductStockId() {
        return productStockId;
    }

    public void setProductStockId(int productStockId) {
        this.productStockId = productStockId;
    }

    public String getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
    }

    public int getTicket() {
        return ticket;
    }

    public void setTicket(int ticket) {
        this.ticket = ticket;
    }
}