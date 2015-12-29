package mmb.ware.cargo.model;

import java.util.Date;

public class CargoOperationLog {
    private Integer id;

    private Integer operId;

    private String operDatetime;

    private Integer operAdminId;

    private String operAdminName;

    private String remark;

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}