package mmb.ware.cargo.model;

public class CargoOperationProcess {
    private Integer id;

    private Integer operationType;

    private Integer process;

    private Integer useStatus;

    private Integer handleType;

    private Integer confirmType;

    private String operName;

    private String statusName;

    private Integer effectTime;

    private Integer deptId1;

    private Integer deptId2;

    private Integer storageId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOperationType() {
        return operationType;
    }

    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
    }

    public Integer getProcess() {
        return process;
    }

    public void setProcess(Integer process) {
        this.process = process;
    }

    public Integer getUseStatus() {
        return useStatus;
    }

    public void setUseStatus(Integer useStatus) {
        this.useStatus = useStatus;
    }

    public Integer getHandleType() {
        return handleType;
    }

    public void setHandleType(Integer handleType) {
        this.handleType = handleType;
    }

    public Integer getConfirmType() {
        return confirmType;
    }

    public void setConfirmType(Integer confirmType) {
        this.confirmType = confirmType;
    }

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName == null ? null : operName.trim();
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName == null ? null : statusName.trim();
    }

    public Integer getEffectTime() {
        return effectTime;
    }

    public void setEffectTime(Integer effectTime) {
        this.effectTime = effectTime;
    }

    public Integer getDeptId1() {
        return deptId1;
    }

    public void setDeptId1(Integer deptId1) {
        this.deptId1 = deptId1;
    }

    public Integer getDeptId2() {
        return deptId2;
    }

    public void setDeptId2(Integer deptId2) {
        this.deptId2 = deptId2;
    }

    public Integer getStorageId() {
        return storageId;
    }

    public void setStorageId(Integer storageId) {
        this.storageId = storageId;
    }
}