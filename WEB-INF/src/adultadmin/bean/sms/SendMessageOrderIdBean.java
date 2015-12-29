package adultadmin.bean.sms;

//老用户订单。待发货  取消时候 需要发送短信表
public class SendMessageOrderIdBean {
	
	public int id;
	public int orderId;
	public String orderCode;
	public String phone;
	public String addTime;
	public int type;//1老用户取消订单，2老用户待发货订单  3 老用户改为待发货发送短信 延迟一分钟
	
	public int userId ;//用户id
	
	public String userName;//登录账号名字
	
	public String orderName; //用户订单名字
	
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOrderName() {
		return orderName;
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}

}
