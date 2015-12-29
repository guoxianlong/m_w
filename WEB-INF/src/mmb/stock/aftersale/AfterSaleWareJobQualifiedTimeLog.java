package mmb.stock.aftersale;

/**
 * 仓内作业合格时间设置的操作日志
 * @author lining
 *
 */
public class AfterSaleWareJobQualifiedTimeLog {
	public int id;
	public int operationQualifiedTimeId;
	public String operateTime;
	public String remark;
	public int userId;
	public String username;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getOperationQualifiedTimeId() {
		return operationQualifiedTimeId;
	}
	public void setOperationQualifiedTimeId(int operationQualifiedTimeId) {
		this.operationQualifiedTimeId = operationQualifiedTimeId;
	}
	public String getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
