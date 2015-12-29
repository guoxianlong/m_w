package mmb.stock.afStock.model;

import java.util.Date;

public class AfterSaleDetectProductBean {
    private Integer id;

    private Integer afterSaleDetectPackageId;

    private Integer productId;

    private Integer afterSaleOrderId;

    private String afterSaleOrderCode;

    private Byte inBuyOrder;

    private Byte inUserOrder;

    private String remark;

    private String code;

    private String imei;

    private Byte status;

    private Byte lockStatus;

    private String cargoWholeCode;

    private Date createDatetime;

    private Integer createUserId;

    private String createUserName;

    private Byte bsStatus;

    private Byte areaId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAfterSaleDetectPackageId() {
        return afterSaleDetectPackageId;
    }

    public void setAfterSaleDetectPackageId(Integer afterSaleDetectPackageId) {
        this.afterSaleDetectPackageId = afterSaleDetectPackageId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getAfterSaleOrderId() {
        return afterSaleOrderId;
    }

    public void setAfterSaleOrderId(Integer afterSaleOrderId) {
        this.afterSaleOrderId = afterSaleOrderId;
    }

    public String getAfterSaleOrderCode() {
        return afterSaleOrderCode;
    }

    public void setAfterSaleOrderCode(String afterSaleOrderCode) {
        this.afterSaleOrderCode = afterSaleOrderCode == null ? null : afterSaleOrderCode.trim();
    }

    public Byte getInBuyOrder() {
        return inBuyOrder;
    }

    public void setInBuyOrder(Byte inBuyOrder) {
        this.inBuyOrder = inBuyOrder;
    }

    public Byte getInUserOrder() {
        return inUserOrder;
    }

    public void setInUserOrder(Byte inUserOrder) {
        this.inUserOrder = inUserOrder;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei == null ? null : imei.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(Byte lockStatus) {
        this.lockStatus = lockStatus;
    }

    public String getCargoWholeCode() {
        return cargoWholeCode;
    }

    public void setCargoWholeCode(String cargoWholeCode) {
        this.cargoWholeCode = cargoWholeCode == null ? null : cargoWholeCode.trim();
    }

    public Date getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName == null ? null : createUserName.trim();
    }

    public Byte getBsStatus() {
        return bsStatus;
    }

    public void setBsStatus(Byte bsStatus) {
        this.bsStatus = bsStatus;
    }

    public Byte getAreaId() {
        return areaId;
    }

    public void setAreaId(Byte areaId) {
        this.areaId = areaId;
    }
}