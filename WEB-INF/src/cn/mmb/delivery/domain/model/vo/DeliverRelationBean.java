package cn.mmb.delivery.domain.model.vo;

public class DeliverRelationBean {
	
	/**未发送*/
	public static final int STATUS0 = 0;
	/**已发送*/
	public static final int STATUS1 = 1;
	/**取消*/
	public static final int STATUS2 = 2;
	/**已成功发送到快递公司*/
	public static final int STATUS3 = 3;
	
	private int id;
	private int deliverId;
	private String orderCode;
	/**状态[0:未发送; 1:已发送; 2:取消; 3:已成功发送到快递公司]*/
	private int status;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDeliverId() {
		return deliverId;
	}
	public void setDeliverId(int deliverId) {
		this.deliverId = deliverId;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
