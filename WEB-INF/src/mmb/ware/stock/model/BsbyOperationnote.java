package mmb.ware.stock.model;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class BsbyOperationnote {
	public static int dispose = 0;// 处理中
	public static int audit_ing = 1;// 审核中
	public static int audit_Fail = 2;// 审核未通过
	public static int audit_sus = 3; // 审核通过
	public static int audit_end = 4;//结束
	public static int fin_audit_Fail = 5;//财务审核未通过
	public static int fin_audit_sus = 6;//财务审核通过
	public static HashMap<Integer, String> current_typeMap = new LinkedHashMap<Integer, String>();
	/** 报损 **/
	public static int TYPE0 = 0;
	/** 报溢 **/
	public static int TYPE1 = 1;
	
	public static HashMap<Integer, String> typeMap = new HashMap<Integer, String>();
	
	static {
		current_typeMap.put(Integer.valueOf(dispose), "处理中");
		current_typeMap.put(Integer.valueOf(audit_ing), "审核中");
		current_typeMap.put(Integer.valueOf(audit_Fail), "审核未通过");
		current_typeMap.put(Integer.valueOf(audit_end), "已完成");
		current_typeMap.put(Integer.valueOf(fin_audit_Fail), "财务审核未通过");
		current_typeMap.put(Integer.valueOf(fin_audit_sus), "财务审核通过");
		
		typeMap.put(TYPE0, "报损");
		typeMap.put(TYPE1, "报溢");
	}
    private Integer id;

    private String receiptsNumber;

    private Integer warehouseType;

    private Integer warehouseArea;

    private String addTime;

    private String operatorName;

    private Integer operatorId;

    private Integer currentType;

    private String endTime;

    private String remark;

    private Integer type;

    private Integer endOperId;

    private String endOperName;

    private Integer printSum;

    private Integer ifDel;

    private String examinesuggestion;

    private String finAuditDatetime;

    private String finAuditName;

    private String finAuditRemark;

    private Integer finAuditId;

    private Integer source;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReceiptsNumber() {
        return receiptsNumber;
    }

    public void setReceiptsNumber(String receiptsNumber) {
        this.receiptsNumber = receiptsNumber == null ? null : receiptsNumber.trim();
    }

    public Integer getWarehouseType() {
        return warehouseType;
    }

    public void setWarehouseType(Integer warehouseType) {
        this.warehouseType = warehouseType;
    }

    public Integer getWarehouseArea() {
        return warehouseArea;
    }

    public void setWarehouseArea(Integer warehouseArea) {
        this.warehouseArea = warehouseArea;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName == null ? null : operatorName.trim();
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getCurrentType() {
        return currentType;
    }

    public void setCurrentType(Integer currentType) {
        this.currentType = currentType;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getEndOperId() {
        return endOperId;
    }

    public void setEndOperId(Integer endOperId) {
        this.endOperId = endOperId;
    }

    public String getEndOperName() {
        return endOperName;
    }

    public void setEndOperName(String endOperName) {
        this.endOperName = endOperName == null ? null : endOperName.trim();
    }

    public Integer getPrintSum() {
        return printSum;
    }

    public void setPrintSum(Integer printSum) {
        this.printSum = printSum;
    }

    public Integer getIfDel() {
        return ifDel;
    }

    public void setIfDel(Integer ifDel) {
        this.ifDel = ifDel;
    }

    public String getExaminesuggestion() {
        return examinesuggestion;
    }

    public void setExaminesuggestion(String examinesuggestion) {
        this.examinesuggestion = examinesuggestion == null ? null : examinesuggestion.trim();
    }

    public String getFinAuditDatetime() {
        return finAuditDatetime;
    }

    public void setFinAuditDatetime(String finAuditDatetime) {
        this.finAuditDatetime = finAuditDatetime;
    }

    public String getFinAuditName() {
        return finAuditName;
    }

    public void setFinAuditName(String finAuditName) {
        this.finAuditName = finAuditName == null ? null : finAuditName.trim();
    }

    public String getFinAuditRemark() {
        return finAuditRemark;
    }

    public void setFinAuditRemark(String finAuditRemark) {
        this.finAuditRemark = finAuditRemark == null ? null : finAuditRemark.trim();
    }

    public Integer getFinAuditId() {
        return finAuditId;
    }

    public void setFinAuditId(Integer finAuditId) {
        this.finAuditId = finAuditId;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }
}