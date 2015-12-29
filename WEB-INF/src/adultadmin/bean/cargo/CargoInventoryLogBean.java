package adultadmin.bean.cargo;

/**
 * 说明：盘点作业单日志
 *
 */
public class CargoInventoryLogBean {

	public int id;   //ID
	
	public int cargoInventoryId;   //盘点作业单ID
	
	public int logType;     //作业日志类型
	
	public String operDatetime;   //操作时间
	
	public int operAdminId;   //操作人ID
	
	public String operAdminName;   //操作人用户名
	
	public String remark;   //操作日志内容
	
	/**
	 * 类型：作业单基本操作
	 */
	public static final int LOG_TYPE0 = 0;
	
	/**
	 * 类型：初盘 
	 *       logType>1表示复盘1、2、3 ……
	 */
	public static final int LOG_TYPE1 = 1;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCargoInventoryId() {
		return cargoInventoryId;
	}

	public void setCargoInventoryId(int cargoInventoryId) {
		this.cargoInventoryId = cargoInventoryId;
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

	public int getOperAdminId() {
		return operAdminId;
	}

	public void setOperAdminId(int operAdminId) {
		this.operAdminId = operAdminId;
	}

	public String getOperAdminName() {
		return operAdminName;
	}

	public void setOperAdminName(String operAdminName) {
		this.operAdminName = operAdminName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
