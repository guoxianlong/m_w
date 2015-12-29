package adultadmin.bean.balance;

/**
 * 作者：赵林
 * 
 * 创建时间：2010-05-05
 * 
 * 说明：结算数据确认操作日志
 *
 */
public class MailingBalanceAuditingLogBean {

	public static int TYPE_ADD = 1;
	public static int TYPE_MODIFY = 2;
	public static int TYPE_DELETE = 3;

	public int id;

	public int type;

	public int logId;

	public int adminId;

	public String adminName;

	public String remark;

	public String operDatetime;

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLogId() {
		return logId;
	}

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public String getOperDatetime() {
		return operDatetime;
	}

	public void setOperDatetime(String operDatetime) {
		this.operDatetime = operDatetime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
