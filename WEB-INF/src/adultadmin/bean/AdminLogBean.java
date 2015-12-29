/*
 * Created on 2007-6-28
 *
 */
package adultadmin.bean;

/**
 * 作者：李北金
 * 
 * 创建日期：2007-6-28
 * 
 * 说明：
 */
public class AdminLogBean {
    public static int DEAL_REIMBURSE = 1; //保存应返款 value1为订单id value2为应返款金额 value3为新应返款金额

    public static int DEAL_ORDER_REIMBURSE = 2; //保存退货款 value1为订单id value2为退货款金额 value3为新退货款金额
    
    public static int DEAL_REALPAYFROM_REIMBURSE = 3;	//从应返款里扣已到款 value1为订单id value2为所扣金额 value3为扣后应返款
    
    public static int DEAL_REALPAYFROM_ORDER_REIMBURSE = 4;	//从退货款里扣已到款 value1为订单id value2为所扣金额 value3为扣后退货款

    public int id;

    public int operateType;

    public float value1;

    public float value2;

    public float value3;

    public int userId;

    public String remark;

    public String createTime;

    /**
     * @return Returns the createTime.
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     *            The createTime to set.
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return Returns the operateType.
     */
    public int getOperateType() {
        return operateType;
    }

    /**
     * @param operateType
     *            The operateType to set.
     */
    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }

    /**
     * @return Returns the remark.
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark
     *            The remark to set.
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return Returns the userId.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId
     *            The userId to set.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return Returns the value1.
     */
    public float getValue1() {
        return value1;
    }

    /**
     * @param value1
     *            The value1 to set.
     */
    public void setValue1(float value1) {
        this.value1 = value1;
    }

    /**
     * @return Returns the value2.
     */
    public float getValue2() {
        return value2;
    }

    /**
     * @param value2
     *            The value2 to set.
     */
    public void setValue2(float value2) {
        this.value2 = value2;
    }

    /**
     * @return Returns the value3.
     */
    public float getValue3() {
        return value3;
    }

    /**
     * @param value3
     *            The value3 to set.
     */
    public void setValue3(float value3) {
        this.value3 = value3;
    }
}
