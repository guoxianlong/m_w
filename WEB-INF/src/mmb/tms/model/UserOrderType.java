package mmb.tms.model;

import java.util.Date;

public class UserOrderType {
    private Integer id;

    private String name;

    private Integer typeId;

    private String productCatalogs;

    private String productIds;

    private Integer checkOrder;

    private String hideUser;

    private Date hideDate;

    private Boolean hideStatus;

    private String operator;

    private Date operateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getProductCatalogs() {
        return productCatalogs;
    }

    public void setProductCatalogs(String productCatalogs) {
        this.productCatalogs = productCatalogs == null ? null : productCatalogs.trim();
    }

    public String getProductIds() {
        return productIds;
    }

    public void setProductIds(String productIds) {
        this.productIds = productIds == null ? null : productIds.trim();
    }

    public Integer getCheckOrder() {
        return checkOrder;
    }

    public void setCheckOrder(Integer checkOrder) {
        this.checkOrder = checkOrder;
    }

    public String getHideUser() {
        return hideUser;
    }

    public void setHideUser(String hideUser) {
        this.hideUser = hideUser == null ? null : hideUser.trim();
    }

    public Date getHideDate() {
        return hideDate;
    }

    public void setHideDate(Date hideDate) {
        this.hideDate = hideDate;
    }

    public Boolean getHideStatus() {
        return hideStatus;
    }

    public void setHideStatus(Boolean hideStatus) {
        this.hideStatus = hideStatus;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}