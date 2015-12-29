package mmb.stock.fitting.model;

public class AfterSaleReceiveFittingDetail {
    private Integer id;

    private Integer receiveFittingId;

    private Integer fittingId;

    private Integer count;
    
    private Integer detectProductId;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDetectProductId() {
		return detectProductId;
	}

	public void setDetectProductId(Integer detectProductId) {
		this.detectProductId = detectProductId;
	}

	public Integer getReceiveFittingId() {
        return receiveFittingId;
    }

    public void setReceiveFittingId(Integer receiveFittingId) {
        this.receiveFittingId = receiveFittingId;
    }

    public Integer getFittingId() {
        return fittingId;
    }

    public void setFittingId(Integer fittingId) {
        this.fittingId = fittingId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}