package adultadmin.bean.sms;

/**
 * 作者：赵林
 * 
 * 说明：电话失败订单处理
 *
 */
public class OrderDealBean {
	
	public int id;         //ID
	public int orderId;    //订单ID
	public String mobile;  //手机号
	public String createTime;  //创建时间
	public int admin;      //管理员ID
	public String adminName;  //管理员用户名
	public String content; //内容
	public String receiveTime;  //短信收取时间
	public String nextProcessTime; //订单下一次处理时间
	public int status;     //状态
	public int receiveProcessAdmin;  //回复短信后订单弹出处理人ID
	public String receiveProcessAdminName;  //回复短信后订单弹出处理人
	public String receiveProcessDatetime; //回复短信后，订单弹出时间
	public int preinstallProcessAdmin;  //到达预设订单处理时间后，订单弹出处理人ID
	public String preinstallProcessAdminName;  //到达预设订单处理时间后，订单弹出处理人
	public String preinstallProcessDatetime; //到达预设订单处理时间后，订单弹出时间
	public int type; //订单类型： 北京单-0、无锡单-1、下订单发短信-2  老用户订单 短信-3

	public static int STATUS_未处理 = 0;
	public static int STATUS_已经回复短信 = 1;
	public static int STATUS_已经设置下次处理时间 = 2;
	public static int STATUS_回复短信已经处理 = 3;
	public static int STATUS_预设时间已经处理 = 4;

	public static int STATUS_CONTENT_电话 = 1;
	public static int STATUS_CONTENT_是 = 2;
	public static int STATUS_CONTENT_其他 = 3;
	public static int STATUS_CONTENT_未处理订单短信回复已处理 = 9;

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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}
	public String getNextProcessTime() {
		return nextProcessTime;
	}
	public void setNextProcessTime(String nextProcessTime) {
		this.nextProcessTime = nextProcessTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getReceiveProcessAdmin() {
		return receiveProcessAdmin;
	}
	public void setReceiveProcessAdmin(int receiveProcessAdmin) {
		this.receiveProcessAdmin = receiveProcessAdmin;
	}
	public String getReceiveProcessDatetime() {
		return receiveProcessDatetime;
	}
	public void setReceiveProcessDatetime(String receiveProcessDatetime) {
		this.receiveProcessDatetime = receiveProcessDatetime;
	}
	public int getPreinstallProcessAdmin() {
		return preinstallProcessAdmin;
	}
	public void setPreinstallProcessAdmin(int preinstallProcessAdmin) {
		this.preinstallProcessAdmin = preinstallProcessAdmin;
	}
	public String getPreinstallProcessDatetime() {
		return preinstallProcessDatetime;
	}
	public void setPreinstallProcessDatetime(String preinstallProcessDatetime) {
		this.preinstallProcessDatetime = preinstallProcessDatetime;
	}
	public String getReceiveProcessAdminName() {
		return receiveProcessAdminName;
	}
	public void setReceiveProcessAdminName(String receiveProcessAdminName) {
		this.receiveProcessAdminName = receiveProcessAdminName;
	}
	public String getPreinstallProcessAdminName() {
		return preinstallProcessAdminName;
	}
	public void setPreinstallProcessAdminName(String preinstallProcessAdminName) {
		this.preinstallProcessAdminName = preinstallProcessAdminName;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
}
