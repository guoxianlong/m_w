package mmb.stock.fitting.model;

import java.util.HashMap;
import java.util.Map;


public class AfterSaleReceiveFitting {
	/**
	 * 待审核
	 */
	public final static byte STATUS1 = 1;
	/**
	 * 审核未通过
	 */
	public final static byte STATUS2 = 2;
	/**
	 * 出库待确认
	 */
	public final static byte STATUS3 = 3;
	/**
	 * 出库完成
	 */
	public final static byte STATUS4 = 4;
	
	public static Map<Byte,String> statusMap = new HashMap<Byte, String>();
	
	/**
	 * 调拨给理赔部
	 */
	public final static byte TARGET1 = 1;
	/**
	 * 补齐售后商品
	 */
	public final static byte TARGET2 = 2;
	/**
	 * 更换用户商品
	 */
	public final static byte TARGET3 = 3;
	/**
	 * 补齐用户商品
	 */
	public final static byte TARGET4 = 4;
	/**
	 * 其他
	 */
	public final static byte TARGET5 = 5;
	/**
	 * 维修
	 */
	public final static byte TARGET6 = 6;
	
	public static Map<Byte,String> targetMap = new HashMap<Byte, String>();
	
	static {
		statusMap.put(STATUS1, "待审核");
		statusMap.put(STATUS2, "审核未通过");
		statusMap.put(STATUS3, "出库待确认");
		statusMap.put(STATUS4, "出库完成");
		
		targetMap.put(TARGET1, "调拨给理赔部");
		targetMap.put(TARGET2, "补齐售后商品");
		targetMap.put(TARGET3, "更换用户商品");
		targetMap.put(TARGET4, "补齐用户商品");
		targetMap.put(TARGET5, "其他");
		targetMap.put(TARGET6, "维修");
	}
	
    private Integer id;

    private String code;

    private Byte target;

    private Integer createUserId;

    private String createUserName;

    private String createDatetime;

    private Integer auditUserId;

    private String auditUserName;

    private Byte status;

    private Short areaId;

    private Integer afterSaleDetectProductId;
    //用途
    private String targetName;
    
    private String remark;
    
    private String completeDatetime;
    
    private int completeUserId;
    
    private String completeUsername;    

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Byte getTarget() {
		return target;
	}

	public void setTarget(Byte target) {
		this.target = target;
	}

	public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName == null ? null : createUserName.trim();
    }

    public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public Integer getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(Integer auditUserId) {
        this.auditUserId = auditUserId;
    }

    public String getAuditUserName() {
        return auditUserName;
    }

    public void setAuditUserName(String auditUserName) {
        this.auditUserName = auditUserName == null ? null : auditUserName.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Short getAreaId() {
        return areaId;
    }

    public void setAreaId(Short areaId) {
        this.areaId = areaId;
    }

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public Integer getAfterSaleDetectProductId() {
		return afterSaleDetectProductId;
	}

	public void setAfterSaleDetectProductId(Integer afterSaleDetectProductId) {
		this.afterSaleDetectProductId = afterSaleDetectProductId;
	}

    public String getCompleteDatetime() {
		return completeDatetime;
	}

	public void setCompleteDatetime(String completeDatetime) {
		this.completeDatetime = completeDatetime;
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
		this.completeUsername = completeUsername;
	}
}