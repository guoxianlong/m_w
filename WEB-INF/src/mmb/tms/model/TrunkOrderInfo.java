package mmb.tms.model;

import java.util.Date;

public class TrunkOrderInfo {
    private Integer id;

    private Integer trunkOrderId;

    private Date nodeTime;

    private String status;

    private Float size;

    private Float weight;

    private Integer mode;

    private Integer opUser;
    
    private String opUserName;
    
    private String statusName;
    
    private String modeName;
    
    private Integer sysOpUser;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTrunkOrderId() {
        return trunkOrderId;
    }

    public void setTrunkOrderId(Integer trunkOrderId) {
        this.trunkOrderId = trunkOrderId;
    }

    public Date getNodeTime() {
        return nodeTime;
    }

    public void setNodeTime(Date nodeTime) {
        this.nodeTime = nodeTime;
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

    public Integer getOpUser() {
        return opUser;
    }

    public void setOpUser(Integer opUser) {
        this.opUser = opUser;
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
}