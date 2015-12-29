package mmb.ware.cargo.model;

import java.util.Date;

public class CargoOperation {
    private Integer id;

    private Integer status;

    private String createDatetime;

    private String remark;

    private String confirmDatetime;

    private Integer createUserId;

    private String auditingDatetime;

    private Integer auditingUserId;

    private String code;

    private String source;

    private String storageCode;

    private Integer stockInType;

    private Integer stockOutType;

    private String createUserName;

    private String auditingUserName;

    private String confirmUserName;

    private String completeDatetime;

    private Integer completeUserId;

    private String completeUserName;

    private Integer type;

    private Integer printCount;

    private String lastOperateDatetime;

    private Integer effectStatus;

    private Integer stockOutArea;

    private Integer stockInArea;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getConfirmDatetime() {
        return confirmDatetime;
    }

    public void setConfirmDatetime(String confirmDatetime) {
        this.confirmDatetime = confirmDatetime;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public String getAuditingDatetime() {
        return auditingDatetime;
    }

    public void setAuditingDatetime(String auditingDatetime) {
        this.auditingDatetime = auditingDatetime;
    }

    public Integer getAuditingUserId() {
        return auditingUserId;
    }

    public void setAuditingUserId(Integer auditingUserId) {
        this.auditingUserId = auditingUserId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source == null ? null : source.trim();
    }

    public String getStorageCode() {
        return storageCode;
    }

    public void setStorageCode(String storageCode) {
        this.storageCode = storageCode == null ? null : storageCode.trim();
    }

    public Integer getStockInType() {
        return stockInType;
    }

    public void setStockInType(Integer stockInType) {
        this.stockInType = stockInType;
    }

    public Integer getStockOutType() {
        return stockOutType;
    }

    public void setStockOutType(Integer stockOutType) {
        this.stockOutType = stockOutType;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName == null ? null : createUserName.trim();
    }

    public String getAuditingUserName() {
        return auditingUserName;
    }

    public void setAuditingUserName(String auditingUserName) {
        this.auditingUserName = auditingUserName == null ? null : auditingUserName.trim();
    }

    public String getConfirmUserName() {
        return confirmUserName;
    }

    public void setConfirmUserName(String confirmUserName) {
        this.confirmUserName = confirmUserName == null ? null : confirmUserName.trim();
    }

    public String getCompleteDatetime() {
        return completeDatetime;
    }

    public void setCompleteDatetime(String completeDatetime) {
        this.completeDatetime = completeDatetime;
    }

    public Integer getCompleteUserId() {
        return completeUserId;
    }

    public void setCompleteUserId(Integer completeUserId) {
        this.completeUserId = completeUserId;
    }

    public String getCompleteUserName() {
        return completeUserName;
    }

    public void setCompleteUserName(String completeUserName) {
        this.completeUserName = completeUserName == null ? null : completeUserName.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPrintCount() {
        return printCount;
    }

    public void setPrintCount(Integer printCount) {
        this.printCount = printCount;
    }

    public String getLastOperateDatetime() {
        return lastOperateDatetime;
    }

    public void setLastOperateDatetime(String lastOperateDatetime) {
        this.lastOperateDatetime = lastOperateDatetime;
    }

    public Integer getEffectStatus() {
        return effectStatus;
    }

    public void setEffectStatus(Integer effectStatus) {
        this.effectStatus = effectStatus;
    }

    public Integer getStockOutArea() {
        return stockOutArea;
    }

    public void setStockOutArea(Integer stockOutArea) {
        this.stockOutArea = stockOutArea;
    }

    public Integer getStockInArea() {
        return stockInArea;
    }

    public void setStockInArea(Integer stockInArea) {
        this.stockInArea = stockInArea;
    }
}