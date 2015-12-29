package mmb.stock.spare.model;

public class SpareStockinProductUpshelfBean {
    private Integer id;

    private Integer spareStockinProductId;

    private Integer operId;

    private Byte type;

    /**
     * 0 未完成  1 已完成
     */
    private Byte operStatus;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSpareStockinProductId() {
        return spareStockinProductId;
    }

    public void setSpareStockinProductId(Integer spareStockinProductId) {
        this.spareStockinProductId = spareStockinProductId;
    }

    public Integer getOperId() {
        return operId;
    }

    public void setOperId(Integer operId) {
        this.operId = operId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Byte getOperStatus() {
        return operStatus;
    }

    public void setOperStatus(Byte operStatus) {
        this.operStatus = operStatus;
    }
}