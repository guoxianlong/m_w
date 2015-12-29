package mmb.bi.model;

import java.util.Date;

import adultadmin.util.DateUtil;
 
/**
 * 标准产能
 * @author mengqy
 *
 */
public class BIStandardCapacityBean {
	/**
	 * 自增id
	 */
    private Integer id;

    /**
     * 库地区id
     */
    private Integer areaId;

    /**
     * 作业环节
     */
    private EBIOperType operType;

    /**
     * 生效日期
     */
    private String startTime;

    /**
     * 停用日期
     */
    private String stopTime;

    /**
     * 录入时间
     */
    private String createTime;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 标准产能
     */
    private Float standardCapacity;

    
    /**
     * 状态枚举
     */
    public enum EStatus {
        /**
         * 0 未生效
         */
    	Status0("未生效", 0),    	
        /**
         * 1 已生效
         */
    	Status1("已生效", 1),    	
    	/**
         * 2 已停用
         */
    	Status2("已停用", 2);
    	
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
    
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Float getStandardCapacity() {
        return standardCapacity;
    }

    public void setStandardCapacity(Float standardCapacity) {
        this.standardCapacity = standardCapacity;
    }
    
    /**
     * 虚拟属性，状态转换由js完成
     * @return
     */
    public String getStatusName() {
    	return "";
    }   
}