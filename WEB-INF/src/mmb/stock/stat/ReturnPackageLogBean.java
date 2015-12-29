package mmb.stock.stat;



public class ReturnPackageLogBean {
	public int id;
	public String orderCode;//订单编号
	public int operId;//操作人id
	public String operName;//操作人用户名
	public String operTime;//操作时间
	public String remark;//操作信息
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
	public int getOperId() {
		return operId;
	}
	public void setOperId(int operId) {
		this.operId = operId;
	}
	public String getOperName() {
		return operName;
	}
	public void setOperName(String operName) {
		this.operName = operName;
	}
	public String getOperTime() {
		return operTime;
	}
	public void setOperTime(String operTime) {
		this.operTime = operTime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
