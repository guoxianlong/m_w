package mmb.tms.model;

public class SortingBatchOrder {
    private Integer id;

    private String orderCode;

    private Integer deliver;

    private Integer orderType;

    private Integer sortingBatchId;

    private Integer sortingGroupId;

    private Integer status;

    private Integer groupNum;

    private Integer orderId;

    private String sortingBatchCode;

    private String sortingGroupCode;

    private Integer deleteStatus;

    private String groupCode;

    private Integer orderStockId;

    private String orderStockCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode == null ? null : orderCode.trim();
    }

    public Integer getDeliver() {
        return deliver;
    }

    public void setDeliver(Integer deliver) {
        this.deliver = deliver;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getSortingBatchId() {
        return sortingBatchId;
    }

    public void setSortingBatchId(Integer sortingBatchId) {
        this.sortingBatchId = sortingBatchId;
    }

    public Integer getSortingGroupId() {
        return sortingGroupId;
    }

    public void setSortingGroupId(Integer sortingGroupId) {
        this.sortingGroupId = sortingGroupId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(Integer groupNum) {
        this.groupNum = groupNum;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getSortingBatchCode() {
        return sortingBatchCode;
    }

    public void setSortingBatchCode(String sortingBatchCode) {
        this.sortingBatchCode = sortingBatchCode == null ? null : sortingBatchCode.trim();
    }

    public String getSortingGroupCode() {
        return sortingGroupCode;
    }

    public void setSortingGroupCode(String sortingGroupCode) {
        this.sortingGroupCode = sortingGroupCode == null ? null : sortingGroupCode.trim();
    }

    public Integer getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(Integer deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode == null ? null : groupCode.trim();
    }

    public Integer getOrderStockId() {
        return orderStockId;
    }

    public void setOrderStockId(Integer orderStockId) {
        this.orderStockId = orderStockId;
    }

    public String getOrderStockCode() {
        return orderStockCode;
    }

    public void setOrderStockCode(String orderStockCode) {
        this.orderStockCode = orderStockCode == null ? null : orderStockCode.trim();
    }
}