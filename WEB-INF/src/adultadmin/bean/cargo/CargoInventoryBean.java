package adultadmin.bean.cargo;

import java.util.HashMap;

import adultadmin.util.StringUtil;

/**
 * 货位盘点作业单
 *
 */
public class CargoInventoryBean {

	public int id;   //ID
	
	public String code;  //编号
	
	public int type;     //类型
	
	public String createDatetime;  //生成时间
	
	public int createAdminId;     //生成人ID
	
	public String createAdminName;  //生成人用户名
	
	public String startDatetime;   //盘点开始时间(初盘启动)
	
	public int startAdminId;    //盘点开始操作人id(初盘启动)
	
	public String startAdminName;    //盘点开始操作人用户名(初盘启动)
	
	public String endDatetime;   //盘点结束时间(盘点结束)
	
	public int endAdminId;    //盘点结束操作人id(盘点强制结束时记录)
	
	public String endAdminName;    //盘点结束操作人用户名(盘点强制结束时记录)
	
	public int status;           //状态
	
	public int stage;            //阶段
	
	public String remark;        //备注
	
	public static HashMap typeMap = new HashMap();
	
	public static HashMap statusMap = new HashMap();
	
	public int stockAreaCount = 0;
	public int shelfCount = 0;
	public int cargoCount = 0;
	public int memberCount = 0;
	public int stockType;
	public String storageCode;
	public String stockAreaCode;

	/**
	 * 类型：大盘
	 */
	public static final int TYPE0 = 0;
	
	/**
	 * 类型：动碰货位盘点
	 */
	public static final int TYPE1 = 1;
	
	/**
	 * 类型：随机盘点
	 */
	public static final int TYPE2 = 2;
	
	/**
	 * 状态：未处理
	 */
	public static final int STATUS0 = 0;
	
	/**
	 * 状态：任务分配中
	 */
	public static final int STATUS1 = 1;
	
	/**
	 * 状态：任务未启动
	 */
	public static final int STATUS2 = 2;
	
	/**
	 * 状态：任务已启动
	 */
	public static final int STATUS3 = 3;
	
	/**
	 * 状态：任务已结束
	 */
	public static final int STATUS4 = 4;
	
	/**
	 * 状态：库存调整单已申请
	 */
	public static final int STATUS5 = 5;
	
	/**
	 * 状态：盘点结束
	 */
	public static final int STATUS6 = 6;
	
	/**
	 * 状态：盘点强制结束
	 */
	public static final int STATUS7 = 7;
	
	static{
		typeMap.put(Integer.valueOf(TYPE0), "大盘");
		typeMap.put(Integer.valueOf(TYPE1), "动碰货位盘点");
		typeMap.put(Integer.valueOf(TYPE2), "随机盘点");
		
		statusMap.put(Integer.valueOf(STATUS0), "未处理");
		statusMap.put(Integer.valueOf(STATUS1), "任务分配中");
		statusMap.put(Integer.valueOf(STATUS2), "任务未启动");
		statusMap.put(Integer.valueOf(STATUS3), "任务已启动");
		statusMap.put(Integer.valueOf(STATUS4), "任务已结束");
		statusMap.put(Integer.valueOf(STATUS5), "库存调整单已申请");
		statusMap.put(Integer.valueOf(STATUS6), "盘点结束");
		statusMap.put(Integer.valueOf(STATUS7), "盘点强制结束");
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public int getCreateAdminId() {
		return createAdminId;
	}

	public void setCreateAdminId(int createAdminId) {
		this.createAdminId = createAdminId;
	}

	public String getCreateAdminName() {
		return createAdminName;
	}

	public void setCreateAdminName(String createAdminName) {
		this.createAdminName = createAdminName;
	}

	public String getStartDatetime() {
		return startDatetime;
	}

	public void setStartDatetime(String startDatetime) {
		this.startDatetime = startDatetime;
	}

	public String getStartAdminName() {
		return startAdminName;
	}

	public void setStartAdminName(String startAdminName) {
		this.startAdminName = startAdminName;
	}

	public String getEndDatetime() {
		return endDatetime;
	}

	public void setEndDatetime(String endDatetime) {
		this.endDatetime = endDatetime;
	}

	public String getEndAdminName() {
		return endAdminName;
	}

	public void setEndAdminName(String endAdminName) {
		this.endAdminName = endAdminName;
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
	
	public String getTypeName(){
		return StringUtil.convertNull((String)typeMap.get(Integer.valueOf(this.type)));
	}
	
	public String getStatusName(){
		return StringUtil.convertNull((String)statusMap.get(Integer.valueOf(this.status)));
	}
	
	public String getStageName(){
		String name = "";
		if(stage <= 1){
			name = "初盘";
		}else if(stage > 1){
			name = "复盘"+(stage-1);
		}
		return name;
	}

	public int getStartAdminId() {
		return startAdminId;
	}

	public void setStartAdminId(int startAdminId) {
		this.startAdminId = startAdminId;
	}

	public int getEndAdminId() {
		return endAdminId;
	}

	public void setEndAdminId(int endAdminId) {
		this.endAdminId = endAdminId;
	}

	public int getStockAreaCount() {
		return stockAreaCount;
	}

	public void setStockAreaCount(int stockAreaCount) {
		this.stockAreaCount = stockAreaCount;
	}

	public int getShelfCount() {
		return shelfCount;
	}

	public void setShelfCount(int shelfCount) {
		this.shelfCount = shelfCount;
	}

	public int getCargoCount() {
		return cargoCount;
	}

	public void setCargoCount(int cargoCount) {
		this.cargoCount = cargoCount;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public int getStockType() {
		return stockType;
	}

	public void setStockType(int stockType) {
		this.stockType = stockType;
	}

	public String getStorageCode() {
		return storageCode;
	}

	public void setStorageCode(String storageCode) {
		this.storageCode = storageCode;
	}

	public String getStockAreaCode() {
		return stockAreaCode;
	}

	public void setStockAreaCode(String stockAreaCode) {
		this.stockAreaCode = stockAreaCode;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}
	
}
