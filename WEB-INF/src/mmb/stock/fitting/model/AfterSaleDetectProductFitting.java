package mmb.stock.fitting.model;

public class AfterSaleDetectProductFitting {
    private Integer id;

    private Integer afterSaleDetectProductId;

    private Integer fittingId;
    
    private String fittingName;

    private Short intactCount;

    private Short damageCount;

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

    public Integer getFittingId() {
        return fittingId;
    }

    public void setFittingId(Integer fittingId) {
        this.fittingId = fittingId;
    }

    public Short getIntactCount() {
        return intactCount;
    }

    public void setIntactCount(Short intactCount) {
        this.intactCount = intactCount;
    }

    public Short getDamageCount() {
        return damageCount;
    }

    public void setDamageCount(Short damageCount) {
        this.damageCount = damageCount;
    }

	public String getFittingName() {
		return fittingName;
	}

	public void setFittingName(String fittingName) {
		this.fittingName = fittingName;
	}
    
}