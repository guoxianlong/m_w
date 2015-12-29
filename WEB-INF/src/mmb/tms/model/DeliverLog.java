package mmb.tms.model;


public class DeliverLog {
    private Integer id;

    private Integer deliverId;

    private String createDatetime;

    private Integer userId;

    private String userName;

    private String content;
    
    private int type;
    
    

    public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

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


    public String getCreateDatetime() {
    	if(this.createDatetime!=null && this.createDatetime.length()>0){
			return createDatetime.substring(0,19);
		}else{
			return "";
		}
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}