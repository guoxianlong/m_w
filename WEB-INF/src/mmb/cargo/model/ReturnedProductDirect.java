package mmb.cargo.model;

import java.util.Date;

public class ReturnedProductDirect {
    private Integer id;

    private String directCode;

    private Integer storageId;

    private Integer stockAreaId;

    private Integer defaultStockAreaId;

    private Date createDatetime;

    private Integer operatorId;

    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDirectCode() {
        return directCode;
    }

    public void setDirectCode(String directCode) {
        this.directCode = directCode;
    }

    public Integer getStorageId() {
        return storageId;
    }

    public void setStorageId(Integer storageId) {
        this.storageId = storageId;
    }

    public Integer getStockAreaId() {
        return stockAreaId;
    }

    public void setStockAreaId(Integer stockAreaId) {
        this.stockAreaId = stockAreaId;
    }

    public Integer getDefaultStockAreaId() {
        return defaultStockAreaId;
    }

    public void setDefaultStockAreaId(Integer defaultStockAreaId) {
        this.defaultStockAreaId = defaultStockAreaId;
    }

    public Date getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}