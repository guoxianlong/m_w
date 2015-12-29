package mmb.stock.fitting.model;

import java.util.Date;

public class BuyStockin {
    private Integer id;

    private String code;

    private String name;

    private Byte stockArea;

    private Byte stockType;

    private Integer createUserId;

    private Integer auditingUserId;

    private Date createDatetime;

    private Date confirmDatetime;

    private Integer buyStockId;

    private String remark;

    private Integer status;

    private Integer supplierId;

    private Integer buyOrderId;

    private Integer type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Byte getStockArea() {
        return stockArea;
    }

    public void setStockArea(Byte stockArea) {
        this.stockArea = stockArea;
    }

    public Byte getStockType() {
        return stockType;
    }

    public void setStockType(Byte stockType) {
        this.stockType = stockType;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getAuditingUserId() {
        return auditingUserId;
    }

    public void setAuditingUserId(Integer auditingUserId) {
        this.auditingUserId = auditingUserId;
    }

    public Date getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    public Date getConfirmDatetime() {
        return confirmDatetime;
    }

    public void setConfirmDatetime(Date confirmDatetime) {
        this.confirmDatetime = confirmDatetime;
    }

    public Integer getBuyStockId() {
        return buyStockId;
    }

    public void setBuyStockId(Integer buyStockId) {
        this.buyStockId = buyStockId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(Integer buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}