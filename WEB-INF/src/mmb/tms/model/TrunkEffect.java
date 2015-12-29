package mmb.tms.model;

import java.util.Date;

public class TrunkEffect {
    private Integer id;

    private Integer trunkId;

    private String mode;

    private Integer time;

    private String addTime;

    private Integer status;

    private Integer stockAreaId;

    private Integer deliverId;

    private Integer deliverAdminId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTrunkId() {
        return trunkId;
    }

    public void setTrunkId(Integer trunkId) {
        this.trunkId = trunkId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode == null ? null : mode.trim();
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStockAreaId() {
        return stockAreaId;
    }

    public void setStockAreaId(Integer stockAreaId) {
        this.stockAreaId = stockAreaId;
    }

    public Integer getDeliverId() {
        return deliverId;
    }

    public void setDeliverId(Integer deliverId) {
        this.deliverId = deliverId;
    }

    public Integer getDeliverAdminId() {
        return deliverAdminId;
    }

    public void setDeliverAdminId(Integer deliverAdminId) {
        this.deliverAdminId = deliverAdminId;
    }
}