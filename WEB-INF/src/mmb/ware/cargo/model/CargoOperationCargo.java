package mmb.ware.cargo.model;

public class CargoOperationCargo {
    private Integer id;

    private Integer operId;

    private Integer productId;

    private Integer inCargoProductStockId;

    private String inCargoWholeCode;

    private Integer outCargoProductStockId;

    private String outCargoWholeCode;

    private Integer stockCount;

    private Integer type;

    private Integer useStatus;

    private Integer completeCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOperId() {
        return operId;
    }

    public void setOperId(Integer operId) {
        this.operId = operId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getInCargoProductStockId() {
        return inCargoProductStockId;
    }

    public void setInCargoProductStockId(Integer inCargoProductStockId) {
        this.inCargoProductStockId = inCargoProductStockId;
    }

    public String getInCargoWholeCode() {
        return inCargoWholeCode;
    }

    public void setInCargoWholeCode(String inCargoWholeCode) {
        this.inCargoWholeCode = inCargoWholeCode == null ? null : inCargoWholeCode.trim();
    }

    public Integer getOutCargoProductStockId() {
        return outCargoProductStockId;
    }

    public void setOutCargoProductStockId(Integer outCargoProductStockId) {
        this.outCargoProductStockId = outCargoProductStockId;
    }

    public String getOutCargoWholeCode() {
        return outCargoWholeCode;
    }

    public void setOutCargoWholeCode(String outCargoWholeCode) {
        this.outCargoWholeCode = outCargoWholeCode == null ? null : outCargoWholeCode.trim();
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getUseStatus() {
        return useStatus;
    }

    public void setUseStatus(Integer useStatus) {
        this.useStatus = useStatus;
    }

    public Integer getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(Integer completeCount) {
        this.completeCount = completeCount;
    }
}