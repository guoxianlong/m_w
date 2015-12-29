package mmb.tms.model;

/**
 * 结算公司
 * @author 李宁
 * @date 2014-3-28下午2:55:38
 */
public class BalanceCorpInfo {
    private Integer id;

    private String name;

    private Short balanceWay;

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

    public Short getBalanceWay() {
        return balanceWay;
    }

    public void setBalanceWay(Short balanceWay) {
        this.balanceWay = balanceWay;
    }
}