package mmb.stock.afStock.model;

import java.util.Date;

public class AfterSaleBackSupplierProductBean {
    private Integer id;

    private Integer afterSaleDetectProductId;

    private Integer productId;

    private String productOriname;

    private Byte guarantee;

    private Integer supplierId;

    private Integer userId;

    private String userName;

    private Date createDatetime;

    private Integer senderId;

    private String senderName;

    private String sendDatetime;

    private String contract;

    private String packageCode;

    private Byte firstRepair;

    private String remark;

    private Date returnDatetime;

    private Byte status;

    private Integer returnUserId;

    private String returnUserName;

    private String imei;

    private String deliveryAddress;

    private String contractPhone;

    private String zipCode;

    private String unqualifiedReasonName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAfterSaleDetectProductId() {
        return afterSaleDetectProductId;
    }

    public void setAfterSaleDetectProductId(Integer afterSaleDetectProductId) {
        this.afterSaleDetectProductId = afterSaleDetectProductId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductOriname() {
        return productOriname;
    }

    public void setProductOriname(String productOriname) {
        this.productOriname = productOriname == null ? null : productOriname.trim();
    }

    public Byte getGuarantee() {
        return guarantee;
    }

    public void setGuarantee(Byte guarantee) {
        this.guarantee = guarantee;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public Date getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName == null ? null : senderName.trim();
    }

    public String getSendDatetime() {
        return sendDatetime;
    }

    public void setSendDatetime(String sendDatetime) {
        this.sendDatetime = sendDatetime == null ? null : sendDatetime.trim();
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract == null ? null : contract.trim();
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode == null ? null : packageCode.trim();
    }

    public Byte getFirstRepair() {
        return firstRepair;
    }

    public void setFirstRepair(Byte firstRepair) {
        this.firstRepair = firstRepair;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getReturnDatetime() {
        return returnDatetime;
    }

    public void setReturnDatetime(Date returnDatetime) {
        this.returnDatetime = returnDatetime;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Integer getReturnUserId() {
        return returnUserId;
    }

    public void setReturnUserId(Integer returnUserId) {
        this.returnUserId = returnUserId;
    }

    public String getReturnUserName() {
        return returnUserName;
    }

    public void setReturnUserName(String returnUserName) {
        this.returnUserName = returnUserName == null ? null : returnUserName.trim();
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei == null ? null : imei.trim();
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress == null ? null : deliveryAddress.trim();
    }

    public String getContractPhone() {
        return contractPhone;
    }

    public void setContractPhone(String contractPhone) {
        this.contractPhone = contractPhone == null ? null : contractPhone.trim();
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode == null ? null : zipCode.trim();
    }

    public String getUnqualifiedReasonName() {
        return unqualifiedReasonName;
    }

    public void setUnqualifiedReasonName(String unqualifiedReasonName) {
        this.unqualifiedReasonName = unqualifiedReasonName == null ? null : unqualifiedReasonName.trim();
    }
}