package mmb.tms.model;


public class DeliverKpi {
    private Integer id;

    private Integer deliverId;

    private Short areaId;

    private String lastestTransitTime;

    private String collectTime;
    
    private String arriveTime;

    private String mailingTime;

    private String sendTime;
    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDeliverId() {
        return deliverId;
    }

    public void setDeliverId(Integer deliverId) {
        this.deliverId = deliverId;
    }

    public Short getAreaId() {
        return areaId;
    }

    public void setAreaId(Short areaId) {
        this.areaId = areaId;
    }


    public String getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}

	public String getLastestTransitTime() {
		return lastestTransitTime;
	}

	public void setLastestTransitTime(String lastestTransitTime) {
		this.lastestTransitTime = lastestTransitTime;
	}

	public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime == null ? null : collectTime.trim();
    }

    public String getMailingTime() {
        return mailingTime;
    }

    public void setMailingTime(String mailingTime) {
        this.mailingTime = mailingTime == null ? null : mailingTime.trim();
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime == null ? null : sendTime.trim();
    }
}