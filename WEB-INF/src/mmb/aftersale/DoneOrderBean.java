package mmb.aftersale;


/**
 * 作者：曹续
 * 
 * 创建日期：2009-8-25
 * 
 * 说明：
 */
public class DoneOrderBean {
	public int id;
	public int userId;   // 用户ID
	public int actType;  // 进出帐类型 0，进账 1，出账
	public int orderId;  // 对应 in_order 或者 out_order中的 记录id
	public int orderType;  // 具体类型 例如 mmb订单 充值卡充值
	public float orderAmount;
	public String time;
	public float totalAmountOld ;  // 成交前总余额
	public float totalAmountNew;  //成交后总余额
	public String logDate;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
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
	 * @param userId the userId to set
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
	 * @param actType the actType to set
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
	 * @param orderId the orderId to set
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
	 * @param orderType the orderType to set
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
	 * @param orderAmount the orderAmount to set
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
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}
	/**
	 * @return the totalAmountOld
	 */
	public float getTotalAmountOld() {
		return totalAmountOld;
	}
	/**
	 * @param totalAmountOld the totalAmountOld to set
	 */
	public void setTotalAmountOld(float totalAmountOld) {
		this.totalAmountOld = totalAmountOld;
	}
	/**
	 * @return the totalAmountNew
	 */
	public float getTotalAmountNew() {
		return totalAmountNew;
	}
	/**
	 * @param totalAmountNew the totalAmountNew to set
	 */
	public void setTotalAmountNew(float totalAmountNew) {
		this.totalAmountNew = totalAmountNew;
	}
	/**
	 * @return the logDate
	 */
	public String getLogDate() {
		return logDate;
	}
	/**
	 * @param logDate the logDate to set
	 */
	public void setLogDate(String logDate) {
		this.logDate = logDate;
	}
	
	
}
