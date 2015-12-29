package mmb.bsby.model;

public class BsbyReasonLog {

	
	private int id;
	public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public int getType() {
	return type;
}
public void setType(int type) {
	this.type = type;
}
public String getOperType() {
	return operType;
}
public void setOperType(String operType) {
	this.operType = operType;
}
public String getReason() {
	return reason;
}
public void setReason(String reason) {
	this.reason = reason;
}
public int getOperUserId() {
	return operUserId;
}
public void setOperUserId(int operUserId) {
	this.operUserId = operUserId;
}
public String getOperUserName() {
	return operUserName;
}
public void setOperUserName(String operUserName) {
	this.operUserName = operUserName;
}
public String getOperDateTime() {
	return operDateTime;
}
public void setOperDateTime(String operDateTime) {
	this.operDateTime = operDateTime;
}
	private int type;
	private String operType;
	private String reason;
	private int  operUserId;
	private String operUserName;
	private String operDateTime;
	
}
