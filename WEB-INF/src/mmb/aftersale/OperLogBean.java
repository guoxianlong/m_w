package mmb.aftersale;


/**
 * 作者：曹续
 * 
 * 创建日期：2009-8-25
 * 
 * 说明：
 */
public class OperLogBean {
	public int id;
	public int userId;
	public int actType;
	public int orderId;
	public int orderType;
	public float orderAmount;
	public String time;
	public float amountOld;
	public float amountNew;
	public float freezeOld;
	public float freezeNew;
	public String logDate;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * @return the actType
	 */
	public int getActType() {
		return actType;
	}

	/**
	 * @param actType
	 *            the actType to set
	 */
	public void setActType(int actType) {
		this.actType = actType;
	}

	/**
	 * @return the orderId
	 */
	public int getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId
	 *            the orderId to set
	 */
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return the orderType
	 */
	public int getOrderType() {
		return orderType;
	}

	/**
	 * @param orderType
	 *            the orderType to set
	 */
	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

	/**
	 * @return the orderAmount
	 */
	public float getOrderAmount() {
		return orderAmount;
	}

	/**
	 * @param orderAmount
	 *            the orderAmount to set
	 */
	public void setOrderAmount(float orderAmount) {
		this.orderAmount = orderAmount;
	}

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @return the amountOld
	 */
	public float getAmountOld() {
		return amountOld;
	}

	/**
	 * @param amountOld
	 *            the amountOld to set
	 */
	public void setAmountOld(float amountOld) {
		this.amountOld = amountOld;
	}

	/**
	 * @return the amountNew
	 */
	public float getAmountNew() {
		return amountNew;
	}

	/**
	 * @param amountNew
	 *            the amountNew to set
	 */
	public void setAmountNew(float amountNew) {
		this.amountNew = amountNew;
	}

	/**
	 * @return the freezeOld
	 */
	public float getFreezeOld() {
		return freezeOld;
	}

	/**
	 * @param freezeOld
	 *            the freezeOld to set
	 */
	public void setFreezeOld(float freezeOld) {
		this.freezeOld = freezeOld;
	}

	/**
	 * @return the freezeNew
	 */
	public float getFreezeNew() {
		return freezeNew;
	}

	/**
	 * @param freezeNew
	 *            the freezeNew to set
	 */
	public void setFreezeNew(float freezeNew) {
		this.freezeNew = freezeNew;
	}

	/**
	 * @return the logDate
	 */
	public String getLogDate() {
		return logDate;
	}

	/**
	 * @param logDate
	 *            the logDate to set
	 */
	public void setLogDate(String logDate) {
		this.logDate = logDate;
	}

}

