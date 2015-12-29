package mmb.stock.fitting.model;

public class CargoOperationCargoBean {
    private int id;

    private int operId;

    private int productId;

    private int inCargoProductStockId;

    private String inCargoWholeCode;

    private int outCargoProductStockId;

    private String outCargoWholeCode;

    private int stockCount;

    private int type;

    private int useStatus;

    private int completeCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOperId() {
        return operId;
    }

    public void setOperId(int operId) {
        this.operId = operId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getInCargoProductStockId() {
        return inCargoProductStockId;
    }

    public void setInCargoProductStockId(int inCargoProductStockId) {
        this.inCargoProductStockId = inCargoProductStockId;
    }

    public String getInCargoWholeCode() {
        return inCargoWholeCode;
    }

    public void setInCargoWholeCode(String inCargoWholeCode) {
        this.inCargoWholeCode = inCargoWholeCode == null ? null : inCargoWholeCode.trim();
    }

    public int getOutCargoProductStockId() {
        return outCargoProductStockId;
    }

    public void setOutCargoProductStockId(int outCargoProductStockId) {
        this.outCargoProductStockId = outCargoProductStockId;
    }

    public String getOutCargoWholeCode() {
        return outCargoWholeCode;
    }

    public void setOutCargoWholeCode(String outCargoWholeCode) {
        this.outCargoWholeCode = outCargoWholeCode == null ? null : outCargoWholeCode.trim();
    }

    public int getStockCount() {
        return stockCount;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUseStatus() {
        return useStatus;
    }

    public void setUseStatus(int useStatus) {
        this.useStatus = useStatus;
    }

    public int getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(int completeCount) {
        this.completeCount = completeCount;
    }
}