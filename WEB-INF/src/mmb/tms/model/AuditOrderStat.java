package mmb.tms.model;

import java.util.Date;

public class AuditOrderStat {
    private Integer id;

    private Integer deliverId;

    private Byte areaId;

    private Date date;

    private Integer auditCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDeliverId() {
        return deliverId;
    }

    public void setDeliverId(Integer deliverId) {
        this.deliverId = deliverId;
    }

    public Byte getAreaId() {
        return areaId;
    }

    public void setAreaId(Byte areaId) {
        this.areaId = areaId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getAuditCount() {
        return auditCount;
    }

    public void setAuditCount(Integer auditCount) {
        this.auditCount = auditCount;
    }
}