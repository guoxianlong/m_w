package mmb.rec.oper.bean;

public class OuterAbnormalInfoBean {
	public int id;
	public String outerOrderCode;//第三方订单号
	public int sourceId;//订单来源
	public String keepTime;//记录时间
	public int keepUserId;//记录人id
	public String handleUserName;//处理人姓名
	public String keepUserName;//记录人姓名
	public String handleTime;//处理时间
	public int handleUserId;//处理人id
	public int status;//状态
	public String remark;//备注
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOuterOrderCode() {
		return outerOrderCode;
	}
	public void setOuterOrderCode(String outerOrderCode) {
		this.outerOrderCode = outerOrderCode;
	}
	public int getSourceId() {
		return sourceId;
	}
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}
	public String getKeepTime() {
		return keepTime;
	}
	public void setKeepTime(String keepTime) {
		this.keepTime = keepTime;
	}
	public int getKeepUserId() {
		return keepUserId;
	}
	public void setKeepUserId(int keepUserId) {
		this.keepUserId = keepUserId;
	}
	public String getHandleUserName() {
		return handleUserName;
	}
	public void setHandleUserName(String handleUserName) {
		this.handleUserName = handleUserName;
	}
	public String getKeepUserName() {
		return keepUserName;
	}
	public void setKeepUserName(String keepUserName) {
		this.keepUserName = keepUserName;
	}
	public String getHandleTime() {
		return handleTime;
	}
	public void setHandleTime(String handleTime) {
		this.handleTime = handleTime;
	}
	public int getHandleUserId() {
		return handleUserId;
	}
	public void setHandleUserId(int handleUserId) {
		this.handleUserId = handleUserId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
