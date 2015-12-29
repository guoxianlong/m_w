package mmb.tms.model;

import java.util.Date;

public class DeliverAdminUser {
    private Integer id;

    private String username;

    private String password;

    private String createDatetime;

    private String lastModifyDatetime;

    private Byte status;

    private Byte flag;

    private String name;

    private Integer deliverId;

    private String phone;

    private Integer pvLimit;

    private Integer currentSearchCount;

    private Integer allSearchCount;

    private Date lastSearchTime;

    private Integer type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
    }

    public String getLastModifyDatetime() {
        return lastModifyDatetime;
    }

    public void setLastModifyDatetime(String lastModifyDatetime) {
        this.lastModifyDatetime = lastModifyDatetime;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getFlag() {
        return flag;
    }

    public void setFlag(Byte flag) {
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getDeliverId() {
        return deliverId;
    }

    public void setDeliverId(Integer deliverId) {
        this.deliverId = deliverId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Integer getPvLimit() {
        return pvLimit;
    }

    public void setPvLimit(Integer pvLimit) {
        this.pvLimit = pvLimit;
    }

    public Integer getCurrentSearchCount() {
        return currentSearchCount;
    }

    public void setCurrentSearchCount(Integer currentSearchCount) {
        this.currentSearchCount = currentSearchCount;
    }

    public Integer getAllSearchCount() {
        return allSearchCount;
    }

    public void setAllSearchCount(Integer allSearchCount) {
        this.allSearchCount = allSearchCount;
    }

    public Date getLastSearchTime() {
        return lastSearchTime;
    }

    public void setLastSearchTime(Date lastSearchTime) {
        this.lastSearchTime = lastSearchTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}