package mmb.tms.model;

import java.util.Date;

public class TrunkOrder {
    private Integer id;

    private String code;

    private Integer mailingBatchId;

    private Integer trunkCorpId;

    private Integer stockArea;

    private Integer deliver;

    private String status;

    private Float size;

    private Float weight;

    private Integer mode;

    private Date receiveTime;

    private Date nodeTime;

    private Integer time;

    private Date expectTime;

    private Integer opUser;
    
    private String trunkCorpName;
    
    private String stockAreaName;
    
    private String deliverName;
    
    private String remainTime;
    
    private Date updTime;
    
    private String opUserName;
    
    private String statusName;
    
    private String modeName;
    
    private Integer sysOpUser;
    
    private String overTime;
    
    private Integer orderCount;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getMailingBatchId() {
        return mailingBatchId;
    }

    public void setMailingBatchId(Integer mailingBatchId) {
        this.mailingBatchId = mailingBatchId;
    }

    public Integer getTrunkCorpId() {
        return trunkCorpId;
    }

    public void setTrunkCorpId(Integer trunkCorpId) {
        this.trunkCorpId = trunkCorpId;
    }

    public Integer getStockArea() {
        return stockArea;
    }

    public void setStockArea(Integer stockArea) {
        this.stockArea = stockArea;
    }

    public Integer getDeliver() {
        return deliver;
    }

    public void setDeliver(Integer deliver) {
        this.deliver = deliver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Float getSize() {
        return size;
    }

    public void setSize(Float size) {
        this.size = size;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Date getNodeTime() {
        return nodeTime;
    }

    public void setNodeTime(Date nodeTime) {
        this.nodeTime = nodeTime;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Date getExpectTime() {
        return expectTime;
    }

    public void setExpectTime(Date expectTime) {
        this.expectTime = expectTime;
    }

    public Integer getOpUser() {
        return opUser;
    }

    public void setOpUser(Integer opUser) {
        this.opUser = opUser;
    }

	public String getTrunkCorpName() {
		return trunkCorpName;
	}

	public void setTrunkCorpName(String trunkCorpName) {
		this.trunkCorpName = trunkCorpName;
	}

	public String getStockAreaName() {
		return stockAreaName;
	}

	public void setStockAreaName(String stockAreaName) {
		this.stockAreaName = stockAreaName;
	}

	public String getDeliverName() {
		return deliverName;
	}

	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
	}

	public String getRemainTime() {
		return remainTime;
	}

	public void setRemainTime(String remainTime) {
		this.remainTime = remainTime;
	}

	public Date getUpdTime() {
		return updTime;
	}

	public void setUpdTime(Date updTime) {
		this.updTime = updTime;
	}

	public String getOpUserName() {
		return opUserName;
	}

	public void setOpUserName(String opUserName) {
		this.opUserName = opUserName;
	}

	public String getStatusName() {
		if("1".equals(status)){
			statusName="已交接";
		}else if("2".equals(status)){
			statusName="正常配载";
		}else if("3".equals(status)){
			statusName="运输中";
		}else if("4".equals(status)){
			statusName="派送中";
		}else if("5".equals(status)){
			statusName="派送完成";
		}
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getModeName() {
		if(mode!=null){
			if(mode==1){
				modeName="公路";
			}else if(mode==2){
				modeName="铁路";
			}else if(mode==3){
				modeName="空运";
			}
		}
		return modeName;
	}

	public void setModeName(String modeName) {
		this.modeName = modeName;
	}

	public Integer getSysOpUser() {
		return sysOpUser;
	}

	public void setSysOpUser(Integer sysOpUser) {
		this.sysOpUser = sysOpUser;
	}

	public String getOverTime() {
		return overTime;
	}

	public void setOverTime(String overTime) {
		this.overTime = overTime;
	}

	public Integer getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(Integer orderCount) {
		this.orderCount = orderCount;
	}
}