package mmb.ware.cargo.model;

import java.util.Date;

public class CargoOperLog {
    private Integer id;

    private Integer operId;

    private String operCode;

    private String operName;

    private String operDatetime;

    private Integer operAdminId;

    private String operAdminName;

    private String handlerCode;

    private Integer effectTime;

    private String remark;

    private String preStatusName;

    private String nextStatusName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOperId() {
        return operId;
    }

    public void setOperId(Integer operId) {
        this.operId = operId;
    }

    public String getOperCode() {
        return operCode;
    }

    public void setOperCode(String operCode) {
        this.operCode = operCode == null ? null : operCode.trim();
    }

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName == null ? null : operName.trim();
    }

    public String getOperDatetime() {
        return operDatetime;
    }

    public void setOperDatetime(String operDatetime) {
        this.operDatetime = operDatetime;
    }

    public Integer getOperAdminId() {
        return operAdminId;
    }

    public void setOperAdminId(Integer operAdminId) {
        this.operAdminId = operAdminId;
    }

    public String getOperAdminName() {
        return operAdminName;
    }

    public void setOperAdminName(String operAdminName) {
        this.operAdminName = operAdminName == null ? null : operAdminName.trim();
    }

    public String getHandlerCode() {
        return handlerCode;
    }

    public void setHandlerCode(String handlerCode) {
        this.handlerCode = handlerCode == null ? null : handlerCode.trim();
    }

    public Integer getEffectTime() {
        return effectTime;
    }

    public void setEffectTime(Integer effectTime) {
        this.effectTime = effectTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getPreStatusName() {
        return preStatusName;
    }

    public void setPreStatusName(String preStatusName) {
        this.preStatusName = preStatusName == null ? null : preStatusName.trim();
    }

    public String getNextStatusName() {
        return nextStatusName;
    }

    public void setNextStatusName(String nextStatusName) {
        this.nextStatusName = nextStatusName == null ? null : nextStatusName.trim();
    }
}