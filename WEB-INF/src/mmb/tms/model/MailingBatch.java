package mmb.tms.model;

import java.util.Date;

public class MailingBatch {
    private Integer id;

    private String code;

    private Date createDatetime;

    private Integer deliver;

    private String carrier;

    private Integer createAdminId;

    private String createAdminName;

    private Date transitDatetime;

    private Integer transitAdminId;

    private String transitAdminName;

    private String recipient;

    private Date receiverDatetime;

    private String store;

    private Integer status;

    private Integer area;

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

    public Date getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    public Integer getDeliver() {
        return deliver;
    }

    public void setDeliver(Integer deliver) {
        this.deliver = deliver;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier == null ? null : carrier.trim();
    }

    public Integer getCreateAdminId() {
        return createAdminId;
    }

    public void setCreateAdminId(Integer createAdminId) {
        this.createAdminId = createAdminId;
    }

    public String getCreateAdminName() {
        return createAdminName;
    }

    public void setCreateAdminName(String createAdminName) {
        this.createAdminName = createAdminName == null ? null : createAdminName.trim();
    }

    public Date getTransitDatetime() {
        return transitDatetime;
    }

    public void setTransitDatetime(Date transitDatetime) {
        this.transitDatetime = transitDatetime;
    }

    public Integer getTransitAdminId() {
        return transitAdminId;
    }

    public void setTransitAdminId(Integer transitAdminId) {
        this.transitAdminId = transitAdminId;
    }

    public String getTransitAdminName() {
        return transitAdminName;
    }

    public void setTransitAdminName(String transitAdminName) {
        this.transitAdminName = transitAdminName == null ? null : transitAdminName.trim();
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient == null ? null : recipient.trim();
    }

    public Date getReceiverDatetime() {
        return receiverDatetime;
    }

    public void setReceiverDatetime(Date receiverDatetime) {
        this.receiverDatetime = receiverDatetime;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store == null ? null : store.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }
}