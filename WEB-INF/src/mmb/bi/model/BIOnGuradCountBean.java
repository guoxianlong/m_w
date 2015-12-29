package mmb.bi.model;

/**
 * 在岗位人力
 * @author mengqy
 */
public class BIOnGuradCountBean {
	/**
	 * 自增id
	 */
    private Integer id;

    /**
     * 库地区id
     */
    private Integer areaId;

    /**
     * 时间
     */
    private String datetime;

    /**
     * 统计时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;
    
    /**
     * 人员类型
     * 0：作业人员
     * 1：职能管理人员
     */
    private EType type;
    
	/**
     * 部门
     */
    private EDepartment department;

    /**
     * 作业环节
     */
    private EBIOperType operType;

    /**
     * 实际在岗人数
     */
    private Float onGuradCount;

    /**
     * 出勤人数
     */
    private Integer turnOut;

    /**
     * 在岗位总工时
     */
    private Float onGuradTimeLength;

    /**
     * 外派支援人数
     */
    private Integer supportCount;

    /**
     * 外派支援工时
     */
    private Float supportTimeLength;

    /**
     * 接受支援人数
     */
    private Integer beSupportCount;

    /**
     * 接受支援工时
     */
    private Float beSupportTimeLength;

    /**
     * 临时工人数
     */
    private Integer tempCount;

    /**
     * 状态
     * 0:未审核
     * 1:已生效
     */
    private EStatus status;

    
    /**
     * 状态枚举
     */
    public enum EStatus {
        /**
         * 0 未审核
         */
    	Status0("未审核", 0),
        /**
         * 1 已生效
         */
    	Status1("已生效", 1);
    	
        // 成员变量
        private String name;
        private Integer index;

        private EStatus(String name, Integer index) {
            this.name = name;
            this.index = index;
        }
        
        public static EStatus getEnum(Integer index) {
            for (EStatus c : EStatus.values()) {
                if (c.getIndex() == index) {
                    return c;
                }
            }
            return null;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }
    }
    
    /**
     * 人员类型枚举
     */
    public enum EType {
        /**
         * 0 作业人员
         */
    	Type0("作业人员", 0),
        /**
         * 1 职能管理人员
         */
    	Type1("职能管理人员", 1);
    	
        // 成员变量
        private String name;
        private Integer index;

        private EType(String name, Integer index) {
            this.name = name;
            this.index = index;
        }

        public static EType getEnum(Integer index) {
            for (EType c : EType.values()) {
                if (c.getIndex() == index) {
                    return c;
                }
            }
            return null;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }
    }
    
    /**
     * 部门枚举
     */
    public enum EDepartment {
        /**
         * 0 总裁办
         */
    	Depart0("总裁办", 0),
        /**
         * 1 物流中心
         */
    	Depart1("物流中心", 1),
    	/**
    	 * 2 仓储部
    	 */
    	Depart2("仓储部", 2),
    	/**
    	 * 发货部
    	 */
    	Depart3("发货部", 3),
    	/**
    	 * 退货理赔部
    	 */
    	Depart4("退货理赔部", 4),
    	/**
    	 * 收货质检部
    	 */
    	Depart5("收货质检部", 5),
    	/**
    	 * 配送部
    	 */
    	Depart6("配送部", 6),
    	/**
    	 * 运营部
    	 */
    	Depart7("运营部", 7),
    	/**
    	 * 人事部
    	 */
    	Depart8("人事部", 8),
    	/**
    	 * 行政部
    	 */
    	Depart9("行政部", 9),
    	/**
    	 * 产品部门
    	 */
    	Depart10("产品部", 10);
    	
        // 成员变量
        private String name;
        private Integer index;

        private EDepartment(String name, Integer index) {
            this.name = name;
            this.index = index;
        }
        
        public static EDepartment getEnum(Integer index) {
            for (EDepartment c : EDepartment.values()) {
                if (c.getIndex() == index) {
                    return c;
                }
            }
            return null;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }
    }
        
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getType() {
    	if(this.type == null)
    		return -1;
		return type.getIndex();
	}

	public void setType(Integer type) {
		this.type = EType.getEnum(type);
	}
	
	public void setTypeByEnum(EType type) {
		this.type = type;
	}
	
	public String getTypeName() {
		if(this.type == null)
			return "";
		return this.type.getName();
	}
	
    public Integer getDepartment() {
    	if(this.department == null)
    		return -1;
        return this.department.getIndex();
    }

    public void setDepartment(Integer department) {
		this.department = EDepartment.getEnum(department);
    }
    
    public void setDepartmentByEnum(EDepartment department) {
        this.department = department;
    }

    public String getDepartmentName() {
    	if(this.department == null)
    		return "";
    	return this.department.getName();
    }
    
    public Integer getOperType() {
    	if(this.operType == null)
    		return -1;
        return operType.getIndex();
    }

    public void setOperType(Integer operType) {
        this.operType = EBIOperType.getEnum(operType);
    }
    
    public void setOperTypeByEnum(EBIOperType operType) {
        this.operType = operType;
    }
    
    public String getOperTypeName() {
    	if(this.operType == null)
    		return "";
    	return this.operType.getName();
    }

    public Float getOnGuradCount() {
        return onGuradCount;
    }

    public void setOnGuradCount(Float onGuradCount) {
        this.onGuradCount = onGuradCount;
    }

    public Integer getTurnOut() {
        return turnOut;
    }

    public void setTurnOut(Integer turnOut) {
        this.turnOut = turnOut;
    }

    public Float getOnGuradTimeLength() {
        return onGuradTimeLength;
    }

    public void setOnGuradTimeLength(Float onGuradTimeLength) {
        this.onGuradTimeLength = onGuradTimeLength;
    }

    public Integer getSupportCount() {
        return supportCount;
    }

    public void setSupportCount(Integer supportCount) {
        this.supportCount = supportCount;
    }

    public Float getSupportTimeLength() {
        return supportTimeLength;
    }

    public void setSupportTimeLength(Float supportTimeLength) {
        this.supportTimeLength = supportTimeLength;
    }

    public Integer getBeSupportCount() {
        return beSupportCount;
    }

    public void setBeSupportCount(Integer beSupportCount) {
        this.beSupportCount = beSupportCount;
    }

    public Float getBeSupportTimeLength() {
        return beSupportTimeLength;
    }

    public void setBeSupportTimeLength(Float beSupportTimeLength) {
        this.beSupportTimeLength = beSupportTimeLength;
    }

    public Integer getTempCount() {
        return tempCount;
    }

    public void setTempCount(Integer tempCount) {
        this.tempCount = tempCount;
    }

    public Integer getStatus() {
    	if(this.status == null)
    		return -1;
    	return this.status.getIndex();
    }
    
    public void setStatus(Integer status) {
        this.status = EStatus.getEnum(status);
    }
    
    public void setStatusByEnum(EStatus status) {
        this.status = status;
    }
    
    public String getStatusName() {
    	if(this.status == null)
    		return "";
    	return this.status.getName();
    }
}