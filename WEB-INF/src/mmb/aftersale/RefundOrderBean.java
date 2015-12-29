package mmb.aftersale;


/**
 * 作者：曹续
 * 
 * 创建日期：2009-9-28
 * 
 * RefundOrderBean.java
 * 
 */
public class RefundOrderBean {
	public int id;
	public int userId;
	public int orderType;
	public int orderId;
	public String orderCode;
	public float orderAmount;
	public float refundAmount;
	public String operTime;
	public int operId;
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
	 * @return the orderCode
	 */
	public String getOrderCode() {
		return orderCode;
	}

	/**
	 * @param orderCode
	 *            the orderCode to set
	 */
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
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
	 * @return the refundAmount
	 */
	public float getRefundAmount() {
		return refundAmount;
	}

	/**
	 * @param refundAmount
	 *            the refundAmount to set
	 */
	public void setRefundAmount(float refundAmount) {
		this.refundAmount = refundAmount;
	}

	/**
	 * @return the operTime
	 */
	public String getOperTime() {
		return operTime;
	}

	/**
	 * @param operTime
	 *            the operTime to set
	 */
	public void setOperTime(String operTime) {
		this.operTime = operTime;
	}

	/**
	 * @return the operId
	 */
	public int getOperId() {
		return operId;
	}

	/**
	 * @param operId
	 *            the operId to set
	 */
	public void setOperId(int operId) {
		this.operId = operId;
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

