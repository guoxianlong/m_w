package mmb.tms.model;

import java.util.HashMap;


public class DeliverMail {
	private Integer id;
	private Integer  deliverId; //快递公司id
	private String  mail; //邮箱
	private String  date; //日期
	private Integer  transitCount; //交接数
	private Integer  status; //发送状态，0未发送，1已发送
	private String statusName;
	private String  sendTime; //最后发送时间
	private String  deliverName; //快递公司名称
	/**
	 * 未发送
	 */
	public static int STATUS0 = 0;
	/**
	 * 已发送
	 */
	public static int STATUS1 = 1;
	
	public static HashMap statusMap = new HashMap();
	static {
		statusMap.put(Integer.valueOf(STATUS0), "未发送");
		statusMap.put(Integer.valueOf(STATUS1), "已发送");
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getDeliverId() {
		return deliverId;
	}
	public void setDeliverId(Integer deliverId) {
		this.deliverId = deliverId;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Integer getTransitCount() {
		return transitCount;
	}
	public void setTransitCount(Integer transitCount) {
		this.transitCount = transitCount;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public static String statusMap(String string) {
		// TODO Auto-generated method stub
		return null;
	}
	public String getDeliverName() {
		return deliverName;
	}
	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
	}

}
