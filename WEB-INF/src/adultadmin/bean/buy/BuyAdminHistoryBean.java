/*
 * Created on 2009-3-3
 *
 */
package adultadmin.bean.buy;

public class BuyAdminHistoryBean {

	public static int LOGTYPE_BUY_ORDER = 3;
	public static int LOGTYPE_BUY_PLAN = 1;
	public static int LOGTYPE_BUY_STOCK = 2;
	public static int LOGTYPE_BUY_STOCKIN = 4;
	public static int LOGTYPE_BUY_RETURN = 5;
	public static int LOGTYPE_STOCK_BATCH_PIRCE = 6;

	public static int TYPE_ADD = 1;
	public static int TYPE_MODIFY = 2;
	public static int TYPE_DELETE = 3;

	public int id;

	public int type;

	public int logType;

	public int logId;

	public int adminId;

	public String adminName;

	public String remark;

	public String operDatetime;

	public int deleted;

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

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
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

	public int getLogType() {
		return logType;
	}

	public void setLogType(int logType) {
		this.logType = logType;
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
