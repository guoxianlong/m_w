/*
 * Created on 2007-1-24
 *
 */
package adultadmin.bean;

/**
 * 作者：张陶
 * 
 * 创建日期：2007-1-24
 * 
 * 说明：
 */
public class UserCardBean {
    public static int C_8ZHE = 1; //八折卡

    public static int C_JC10B = 2; //乐酷100亿乐币兑换券

    public static int C_JC20B = 3; //乐酷200亿乐币兑换券

    public static int C_JC50B = 4; //乐酷500亿乐币兑换券
    
    public static int C_20DJQ = 5;	//20元代金券
    
    public static int C_50DJQ = 6;	//50元代金券

    public int id;

    public String phone;

    public int typeId;

    public int cardCount;

    public int userId;

    public String code;

    public String getTime;

    public int status;

    public String useTime;

    /**
     * @return Returns the code.
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code
     *            The code to set.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return Returns the getTime.
     */
    public String getGetTime() {
        return getTime;
    }

    /**
     * @param getTime
     *            The getTime to set.
     */
    public void setGetTime(String getTime) {
        this.getTime = getTime;
    }

    /**
     * @return Returns the status.
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(int status) {
        this.status = status;
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
     * @return Returns the useTime.
     */
    public String getUseTime() {
        return useTime;
    }

    /**
     * @param useTime
     *            The useTime to set.
     */
    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }

    public int getCardCount() {
        return cardCount;
    }

    public void setCardCount(int cardCount) {
        this.cardCount = cardCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

}
