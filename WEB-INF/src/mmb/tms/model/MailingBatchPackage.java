package mmb.tms.model;

import java.util.Date;

public class MailingBatchPackage {
    private Integer id;

    private Integer mailingBatchId;

    private String mailingBatchCode;

    private Integer mailingBatchParcelId;

    private String mailingBatchParcelCode;

    private String orderCode;

    private String packageCode;

    private Date createDatetime;

    private String address;

    private Float totalPrice;

    private Float weight;

    private Integer deliver;

    private Integer orderId;

    private Date stockInDatetime;

    private Integer postStaffId;

    private String postStaffName;

    private Integer stockInAdminId;

    private String stockInAdminName;

    private Integer mailingStatus;

    private Integer returnStatus;

    private Integer balanceStatus;

    private Integer mailingBalanceAuditingId;

    private Date assignTime;

    private Integer payType;

    private Integer mailingChargeAuditingId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMailingBatchId() {
        return mailingBatchId;
    }

    public void setMailingBatchId(Integer mailingBatchId) {
        this.mailingBatchId = mailingBatchId;
    }

    public String getMailingBatchCode() {
        return mailingBatchCode;
    }

    public void setMailingBatchCode(String mailingBatchCode) {
        this.mailingBatchCode = mailingBatchCode == null ? null : mailingBatchCode.trim();
    }

    public Integer getMailingBatchParcelId() {
        return mailingBatchParcelId;
    }

    public void setMailingBatchParcelId(Integer mailingBatchParcelId) {
        this.mailingBatchParcelId = mailingBatchParcelId;
    }

    public String getMailingBatchParcelCode() {
        return mailingBatchParcelCode;
    }

    public void setMailingBatchParcelCode(String mailingBatchParcelCode) {
        this.mailingBatchParcelCode = mailingBatchParcelCode == null ? null : mailingBatchParcelCode.trim();
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode == null ? null : orderCode.trim();
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode == null ? null : packageCode.trim();
    }

    public Date getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Integer getDeliver() {
        return deliver;
    }

    public void setDeliver(Integer deliver) {
        this.deliver = deliver;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Date getStockInDatetime() {
        return stockInDatetime;
    }

    public void setStockInDatetime(Date stockInDatetime) {
        this.stockInDatetime = stockInDatetime;
    }

    public Integer getPostStaffId() {
        return postStaffId;
    }

    public void setPostStaffId(Integer postStaffId) {
        this.postStaffId = postStaffId;
    }

    public String getPostStaffName() {
        return postStaffName;
    }

    public void setPostStaffName(String postStaffName) {
        this.postStaffName = postStaffName == null ? null : postStaffName.trim();
    }

    public Integer getStockInAdminId() {
        return stockInAdminId;
    }

    public void setStockInAdminId(Integer stockInAdminId) {
        this.stockInAdminId = stockInAdminId;
    }

    public String getStockInAdminName() {
        return stockInAdminName;
    }

    public void setStockInAdminName(String stockInAdminName) {
        this.stockInAdminName = stockInAdminName == null ? null : stockInAdminName.trim();
    }

    public Integer getMailingStatus() {
        return mailingStatus;
    }

    public void setMailingStatus(Integer mailingStatus) {
        this.mailingStatus = mailingStatus;
    }

    public Integer getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(Integer returnStatus) {
        this.returnStatus = returnStatus;
    }

    public Integer getBalanceStatus() {
        return balanceStatus;
    }

    public void setBalanceStatus(Integer balanceStatus) {
        this.balanceStatus = balanceStatus;
    }

    public Integer getMailingBalanceAuditingId() {
        return mailingBalanceAuditingId;
    }

    public void setMailingBalanceAuditingId(Integer mailingBalanceAuditingId) {
        this.mailingBalanceAuditingId = mailingBalanceAuditingId;
    }

    public Date getAssignTime() {
        return assignTime;
    }

    public void setAssignTime(Date assignTime) {
        this.assignTime = assignTime;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getMailingChargeAuditingId() {
        return mailingChargeAuditingId;
    }

    public void setMailingChargeAuditingId(Integer mailingChargeAuditingId) {
        this.mailingChargeAuditingId = mailingChargeAuditingId;
    }
}