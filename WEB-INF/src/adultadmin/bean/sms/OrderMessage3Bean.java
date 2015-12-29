package adultadmin.bean.sms;

import java.util.HashMap;

/**
 * 作者：赵林
 * 
 * 说明：发货短信信息(包裹单导入)
 *
 */
public class OrderMessage3Bean {

	public int id;      //ID
	public int orderId;  //订单ID
	public String orderCode; //订单号
	public String packageNum; //包裹单号
	public String mobile; //手机号
	public String consigner;  //发货人
	public String sendDatetime; //短信下发时间
	public int sendUserId;     //下发管理员ID
	public String sendUsername; //下发管理员名称
	public String sendContent;  //下发内容
	public String receiveDatetime;  //回复时间
	public String receiveContent;   //回复内容
	public int status;              //状态
	
	/**
	 * 状态：未处理
	 */
	public static final int STATUS0 = 0;
	
	/**
	 * 状态：已处理
	 */
	public static final int STATUS1 = 1;
	
	public static final HashMap statusMap = new HashMap();
	
	static{
		statusMap.put(Integer.valueOf(STATUS0), "未处理");
		statusMap.put(Integer.valueOf(STATUS1), "已处理");
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getPackageNum() {
		return packageNum;
	}

	public void setPackageNum(String packageNum) {
		this.packageNum = packageNum;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSendDatetime() {
		return sendDatetime;
	}

	public void setSendDatetime(String sendDatetime) {
		this.sendDatetime = sendDatetime;
	}

	public int getSendUserId() {
		return sendUserId;
	}

	public void setSendUserId(int sendUserId) {
		this.sendUserId = sendUserId;
	}

	public String getSendUsername() {
		return sendUsername;
	}

	public void setSendUsername(String sendUsername) {
		this.sendUsername = sendUsername;
	}

	public String getSendContent() {
		return sendContent;
	}

	public void setSendContent(String sendContent) {
		this.sendContent = sendContent;
	}

	public String getReceiveDatetime() {
		return receiveDatetime;
	}

	public void setReceiveDatetime(String receiveDatetime) {
		this.receiveDatetime = receiveDatetime;
	}

	public String getReceiveContent() {
		return receiveContent;
	}

	public void setReceiveContent(String receiveContent) {
		this.receiveContent = receiveContent;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getConsigner() {
		return consigner;
	}

	public void setConsigner(String consigner) {
		this.consigner = consigner;
	}
	
	
}
