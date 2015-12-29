package mmb.ware.stock.model;

import java.util.Date;

public class SortingBatchOrderProduct {
    private Integer id;

    private Integer sortingBatchGroupId;

    private Integer sortingBatchOrderId;

    private Integer productId;

    private Integer count;

    private Integer completeCount;

    private Integer orderSkuCount;

    private String boxCode;

    private Byte isDelete;

    private Integer cargoId;

    private Integer sortingCount;

    private Integer sortingUserId;

    private String sortingUsername;

    private Date sortingDatetime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSortingBatchGroupId() {
        return sortingBatchGroupId;
    }

    public void setSortingBatchGroupId(Integer sortingBatchGroupId) {
        this.sortingBatchGroupId = sortingBatchGroupId;
    }

    public Integer getSortingBatchOrderId() {
        return sortingBatchOrderId;
    }

    public void setSortingBatchOrderId(Integer sortingBatchOrderId) {
        this.sortingBatchOrderId = sortingBatchOrderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(Integer completeCount) {
        this.completeCount = completeCount;
    }

    public Integer getOrderSkuCount() {
        return orderSkuCount;
    }

    public void setOrderSkuCount(Integer orderSkuCount) {
        this.orderSkuCount = orderSkuCount;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode == null ? null : boxCode.trim();
    }

    public Byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getCargoId() {
        return cargoId;
    }

    public void setCargoId(Integer cargoId) {
        this.cargoId = cargoId;
    }

    public Integer getSortingCount() {
        return sortingCount;
    }

    public void setSortingCount(Integer sortingCount) {
        this.sortingCount = sortingCount;
    }

    public Integer getSortingUserId() {
        return sortingUserId;
    }

    public void setSortingUserId(Integer sortingUserId) {
        this.sortingUserId = sortingUserId;
    }

    public String getSortingUsername() {
        return sortingUsername;
    }

    public void setSortingUsername(String sortingUsername) {
        this.sortingUsername = sortingUsername == null ? null : sortingUsername.trim();
    }

    public Date getSortingDatetime() {
        return sortingDatetime;
    }

    public void setSortingDatetime(Date sortingDatetime) {
        this.sortingDatetime = sortingDatetime;
    }
}