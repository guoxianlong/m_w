package mmb.stock.fitting.model;


public class CargoOperationBean {
    private int id;

    private int status;

    private String createDatetime;

    private String remark;

    private String confirmDatetime;

    private int createUserId;

    private String auditingDatetime;

    private int auditingUserId;

    private String code;

    private String source;

    private String storageCode;

    private int stockInType;

    private int stockOutType;

    private String createUserName;

    private String auditingUserName;

    private String confirmUserName;

    private String completeDatetime;

    private int completeUserId;

    private String completeUserName;

    private int type;

    private int printCount;

    private String lastOperateDatetime;

    private int effectStatus;

    private int stockOutArea;

    private int stockInArea;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
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

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public String getAuditingDatetime() {
        return auditingDatetime;
    }

    public void setAuditingDatetime(String auditingDatetime) {
        this.auditingDatetime = auditingDatetime;
    }

    public int getAuditingUserId() {
        return auditingUserId;
    }

    public void setAuditingUserId(int auditingUserId) {
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

    public int getStockInType() {
        return stockInType;
    }

    public void setStockInType(int stockInType) {
        this.stockInType = stockInType;
    }

    public int getStockOutType() {
        return stockOutType;
    }

    public void setStockOutType(int stockOutType) {
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

    public int getCompleteUserId() {
        return completeUserId;
    }

    public void setCompleteUserId(int completeUserId) {
        this.completeUserId = completeUserId;
    }

    public String getCompleteUserName() {
        return completeUserName;
    }

    public void setCompleteUserName(String completeUserName) {
        this.completeUserName = completeUserName == null ? null : completeUserName.trim();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPrintCount() {
        return printCount;
    }

    public void setPrintCount(int printCount) {
        this.printCount = printCount;
    }

    public String getLastOperateDatetime() {
        return lastOperateDatetime;
    }

    public void setLastOperateDatetime(String lastOperateDatetime) {
        this.lastOperateDatetime = lastOperateDatetime;
    }

    public int getEffectStatus() {
        return effectStatus;
    }

    public void setEffectStatus(int effectStatus) {
        this.effectStatus = effectStatus;
    }

    public int getStockOutArea() {
        return stockOutArea;
    }

    public void setStockOutArea(int stockOutArea) {
        this.stockOutArea = stockOutArea;
    }

    public int getStockInArea() {
        return stockInArea;
    }

    public void setStockInArea(int stockInArea) {
        this.stockInArea = stockInArea;
    }
}