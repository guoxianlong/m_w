package mmb.sale.order.stat;

public class OrderAdminStatusLogBean {
	public int id;
	public String createDatetime;
	public int originStatus;
	public int newStatus;
	//订单操作状态变化类型    1：订单状态变化 2:发货状态变化
	public int type;
	public String username;
	public int orderId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public int getOriginStatus() {
		return originStatus;
	}
	public void setOriginStatus(int originStatus) {
		this.originStatus = originStatus;
	}
	public int getNewStatus() {
		return newStatus;
	}
	public void setNewStatus(int newStatus) {
		this.newStatus = newStatus;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	
	
}
