package adultadmin.bean.sms;

/**
 * 作者：赵林
 * 
 * 说明：订单下行短信
 *
 */
public class OrderMessageBean {

	public int id;      //ID
	public int orderId; //订单ID
	public String mobile; //手机号
	public String createTime; //创建时间
	public int admin;     //用户ID
	public String adminName; //用户名
	public int type; // 北京单-0、无锡单-1、下订单发短信-2  老用户 服装 成人订单 变成 待发货发送的短信 -3
	
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
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public int getAdmin() {
		return admin;
	}
	public void setAdmin(int admin) {
		this.admin = admin;
	}
	public String getAdminName() {
		return adminName;
	}
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
}
