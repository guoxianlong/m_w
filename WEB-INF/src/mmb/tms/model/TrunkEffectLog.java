package mmb.tms.model;

public class TrunkEffectLog {
	
	private int id;
	private int operationUserId;
	private String operationUserName;
	private int trunkId;
	private int deliverAdminId;
	private int stockArea;
	private int deliverId;
	private int mode;
	private int time;
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
	public int getStockArea() {
		return stockArea;
	}
	public void setStockArea(int stockArea) {
		this.stockArea = stockArea;
	}
	public int getDeliverId() {
		return deliverId;
	}
	public void setDeliverId(int deliverId) {
		this.deliverId = deliverId;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	
	
}
