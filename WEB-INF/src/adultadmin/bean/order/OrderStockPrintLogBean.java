package adultadmin.bean.order;

public class OrderStockPrintLogBean {
	
	public static int TYPE1=1;//两地发货查询时的打印
	
	public static int TYPE2=2;//补打印
	
	public int id;
	
	/**
	 * 批次
	 */
	public int batch;
	
	/**
	 * 打印类型，1是两地发货查询时的打印，2是补打印
	 */
	public int type;
	
	/**
	 * 操作人Id
	 */
	public int userId;
	
	/**
	 * 操作人姓名
	 */
	public String userName;
	
	/**
	 * 操作时间
	 */
	public String time;
	
	/**
	 * 备注
	 */
	public String remark;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBatch() {
		return batch;
	}

	public void setBatch(int batch) {
		this.batch = batch;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	
}
