package mmb.tms.model;

public class DeliverAdminUserLog {
	
	private int id;
	private int operationUserId;
	private String operationUserName;
	private int trunkId;
	private int deliverAdminId;
	private String password;
	private String phone;
	private int type;
	private String addTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOperationUserId() {
		return operationUserId;
	}
	public void setOperationUserId(int operationUserId) {
		this.operationUserId = operationUserId;
	}
	public String getOperationUserName() {
		return operationUserName;
	}
	public void setOperationUserName(String operationUserName) {
		this.operationUserName = operationUserName;
	}
	public int getTrunkId() {
		return trunkId;
	}
	public void setTrunkId(int trunkId) {
		this.trunkId = trunkId;
	}
	public int getDeliverAdminId() {
		return deliverAdminId;
	}
	public void setDeliverAdminId(int deliverAdminId) {
		this.deliverAdminId = deliverAdminId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	
}
