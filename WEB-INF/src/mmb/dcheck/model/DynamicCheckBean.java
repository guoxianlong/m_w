package mmb.dcheck.model;

public class DynamicCheckBean {
	/**
	 * 自增长id
	 */
    private int id;

    /**
     * 库地区id
     */
    private int areaId=-1;//默认值 -1
     
    public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	private String areaName;//地区名称  列表显示用  非数据库字段
	
	private String beginDatetime;//开始时间  列表查询条件用  非数据库字段
	private String endDatetime;//结束时间  列表查询条件用  非数据库字段
	
	public int getCompleteCount() {
		return completeCount;
	}

	public void setCompleteCount(int completeCount) {
		this.completeCount = completeCount;
	}

	public int getDifferenceCount() {
		return differenceCount;
	}

	public void setDifferenceCount(int differenceCount) {
		this.differenceCount = differenceCount;
	}

	private int completeCount;//盘点完成量  非数据库字段
	private int differenceCount;//盘点差异量  非数据库字段
	public String getBeginDatetime() {
		return beginDatetime;
	}

	public void setBeginDatetime(String beginDatetime) {
		this.beginDatetime = beginDatetime;
	}

	public String getEndDatetime() {
		return endDatetime;
	}

	public void setEndDatetime(String endDatetime) {
		this.endDatetime = endDatetime;
	}



    /**
     * 盘点类型， 1动碰盘， 2大盘
     */
    private int checkType;

    /**
     * 盘点计划号
     */
    private String code;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 创建人id
     */
    private int createUserId;

    /**
     * 创建人用户名
     */
    private String createUsername;

    /**
     * 结束时间
     */
    private String completeTime;

    /**
     * 结束人id
     */
    private int completeUserId;

    /**
     * 结束人用户名
     */
    private String completeUsername;

    /**
     * 状态
     */
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public int getCheckType() {
        return checkType;
    }

    public void setCheckType(int checkType) {
        this.checkType = checkType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUsername() {
        return createUsername;
    }

    public void setCreateUsername(String createUsername) {
        this.createUsername = createUsername == null ? null : createUsername.trim();
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    public int getCompleteUserId() {
        return completeUserId;
    }

    public void setCompleteUserId(int completeUserId) {
        this.completeUserId = completeUserId;
    }

    public String getCompleteUsername() {
        return completeUsername;
    }

    public void setCompleteUsername(String completeUsername) {
        this.completeUsername = completeUsername == null ? null : completeUsername.trim();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}