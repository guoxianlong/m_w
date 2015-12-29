package mmb.tms.model;

public class DeliverSendDefault {
    private Integer id;

    private Integer stockAreaId;

    private Integer deliverCorpId;

    private Integer sendCountLimit;

    private Integer sendCountCurrent;

    private Integer index;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStockAreaId() {
        return stockAreaId;
    }

    public void setStockAreaId(Integer stockAreaId) {
        this.stockAreaId = stockAreaId;
    }

    public Integer getDeliverCorpId() {
        return deliverCorpId;
    }

    public void setDeliverCorpId(Integer deliverCorpId) {
        this.deliverCorpId = deliverCorpId;
    }

    public Integer getSendCountLimit() {
        return sendCountLimit;
    }

    public void setSendCountLimit(Integer sendCountLimit) {
        this.sendCountLimit = sendCountLimit;
    }

    public Integer getSendCountCurrent() {
        return sendCountCurrent;
    }

    public void setSendCountCurrent(Integer sendCountCurrent) {
        this.sendCountCurrent = sendCountCurrent;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}