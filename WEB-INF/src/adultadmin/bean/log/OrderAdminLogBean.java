/*
 * Created on 2008-8-7
 *
 */
package adultadmin.bean.log;

/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2008-8-7
 * 
 * 说明：订单管理日志
 */
public class OrderAdminLogBean {

	public int id;

	public static final int ORDER_ADMIN_PROP = 1;
	public static final int ORDER_ADMIN_PRODUCT = 2;
	public static final int ORDER_ADMIN_PRESENT = 3;
	public static final int ORDER_ADMIN_UNITE = 4;
	public static final int ORDER_ADMIN_USER = 5;

	/**
	 * <pre>
	 * 订单操作类型：
	 * 1 - 订单属性修改
	 * 2 - 订单商品修改
	 * 3 - 订单赠品修改
	 * 4 - 合并订单
	 * </pre>
	 */
	public int type;

	public String createDatetime;

	public String content;

	public int userId;

	public String username;

	public int orderId;

	public String orderCode;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTypeName(){
		String typeName = null;
		switch(this.type){
			case ORDER_ADMIN_PROP:
				typeName = "订单属性修改";
				break;
			case ORDER_ADMIN_PRODUCT:
				typeName = "订单产品修改";
				break;
			case ORDER_ADMIN_PRESENT:
				typeName = "订单赠品修改";
				break;
			case ORDER_ADMIN_UNITE:
				typeName = "订单合并";
				break;
			case ORDER_ADMIN_USER:
				typeName = "用户信息修改";
				break;
			default:
				typeName = "无此操作";
		}
		return typeName;
	}
}
