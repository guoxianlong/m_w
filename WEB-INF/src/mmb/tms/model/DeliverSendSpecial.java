package mmb.tms.model;

public class DeliverSendSpecial {
    private Integer id;

    private Integer stockAreaId;

    private Byte source;

    private Integer targetId;

    private Integer deliverCorpId;

    private Integer sendCountLimit;

    private Integer sendCountCurrent;

    private Integer index;
    
    private String deliverName;

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

    public Byte getSource() {
        return source;
    }

    public void setSource(Byte source) {
        this.source = source;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
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

	public String getDeliverName() {
		return deliverName;
	}

	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
	}
}