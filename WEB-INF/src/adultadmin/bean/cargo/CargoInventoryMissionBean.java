package adultadmin.bean.cargo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adultadmin.util.StringUtil;

/**
 * 说明：盘点作业单任务明细
 *
 */
public class CargoInventoryMissionBean {

	public int id;   //ID
	
	public int cargoInventoryId;   //盘点作业单ID
	
	public int type;    //作业任务类型
	
	public int status;   //任务状态
	
	public int cargoId;  //货位ID
	
	public String cargoWholeCode;   //货位号
	
	public int stockType;   //库存类型
	
	public int cargoPassageId;   //甬道ID
	
	public String cargoPassageCode;  //甬道号
	
	public int cargoShelfId;  //货架ID
	
	public String cargoShelfCode;  //货架号
	
	public int cargoStockAreaId;   //货位区域ID
	
	public String cargoStockAreaCode;  //货位区域号
	
	public int cargoStorageId;  //仓库ID
	
	public String cargoStorageCode; //仓库号
	
	public int staffId;   //员工id
	
	public String staffCode;   //员工号
	
	public String staffName;   //员工姓名
	
	public String startDatetime;   //任务启动时间
	
	public String endDatetime;     //任务结束时间
	
	public int oriMissionId;    //原任务关联ID
	
	public int sortIndex;      //排序索引
	
	public int inventoryResult;   //盘点结果
	
	public static HashMap typeMap = new HashMap();
	
	public static HashMap statusMap = new HashMap();
	
	public List cargoInventoryMissionProductList = new ArrayList();   //商品库存盘点数据
	
	/**
	 * 类型：原始数据快照
	 */
	public static final int TYPE0 = 0;
	
	/**
	 * 类型：初盘
	 */
	public static final int TYPE1 = 1;
	
	/**
	 * 状态：任务未分配
	 */
	public static final int STATUS0 = 0;
	
	/**
	 * 状态：任务未启动
	 */
	public static final int STATUS1 = 1;
	
	/**
	 * 状态：任务已启动
	 */
	public static final int STATUS2 = 2;
	
	/**
	 * 状态：任务进行中
	 */
	public static final int STATUS3 = 3;
	
	/**
	 * 状态：待重新盘点
	 */
	public static final int STATUS4 = 4;
	
	/**
	 * 状态：任务已结束
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
		typeMap.put(Integer.valueOf(TYPE0), "原始数据快照");
		typeMap.put(Integer.valueOf(TYPE1), "初盘");
		
		statusMap.put(Integer.valueOf(STATUS0), "任务未分配");
		statusMap.put(Integer.valueOf(STATUS1), "任务未启动");
		statusMap.put(Integer.valueOf(STATUS2), "任务已启动");
		statusMap.put(Integer.valueOf(STATUS3), "任务进行中");
		statusMap.put(Integer.valueOf(STATUS4), "待重新盘点");
		statusMap.put(Integer.valueOf(STATUS5), "任务已结束");
		statusMap.put(Integer.valueOf(STATUS6), "盘点结束");
		statusMap.put(Integer.valueOf(STATUS7), "盘点强制结束");
	}
	
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCargoId() {
		return cargoId;
	}

	public void setCargoId(int cargoId) {
		this.cargoId = cargoId;
	}

	public String getCargoWholeCode() {
		return cargoWholeCode;
	}

	public void setCargoWholeCode(String cargoWholeCode) {
		this.cargoWholeCode = cargoWholeCode;
	}

	public int getStockType() {
		return stockType;
	}

	public void setStockType(int stockType) {
		this.stockType = stockType;
	}

	public int getCargoShelfId() {
		return cargoShelfId;
	}

	public void setCargoShelfId(int cargoShelfId) {
		this.cargoShelfId = cargoShelfId;
	}

	public String getCargoShelfCode() {
		return cargoShelfCode;
	}

	public void setCargoShelfCode(String cargoShelfCode) {
		this.cargoShelfCode = cargoShelfCode;
	}

	public int getCargoPassageId() {
		return cargoPassageId;
	}

	public void setCargoPassageId(int cargoPassageId) {
		this.cargoPassageId = cargoPassageId;
	}

	public String getCargoPassageCode() {
		return cargoPassageCode;
	}

	public void setCargoPassageCode(String cargoPassageCode) {
		this.cargoPassageCode = cargoPassageCode;
	}

	public int getCargoStockAreaId() {
		return cargoStockAreaId;
	}

	public void setCargoStockAreaId(int cargoStockAreaId) {
		this.cargoStockAreaId = cargoStockAreaId;
	}

	public String getCargoStockAreaCode() {
		return cargoStockAreaCode;
	}

	public void setCargoStockAreaCode(String cargoStockAreaCode) {
		this.cargoStockAreaCode = cargoStockAreaCode;
	}

	public int getCargoStorageId() {
		return cargoStorageId;
	}

	public void setCargoStorageId(int cargoStorageId) {
		this.cargoStorageId = cargoStorageId;
	}

	public String getCargoStorageCode() {
		return cargoStorageCode;
	}

	public void setCargoStorageCode(String cargoStorageCode) {
		this.cargoStorageCode = cargoStorageCode;
	}

	public int getStaffId() {
		return staffId;
	}

	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getStartDatetime() {
		return startDatetime;
	}

	public void setStartDatetime(String startDatetime) {
		this.startDatetime = startDatetime;
	}

	public String getEndDatetime() {
		return endDatetime;
	}

	public void setEndDatetime(String endDatetime) {
		this.endDatetime = endDatetime;
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

	public int getOriMissionId() {
		return oriMissionId;
	}

	public void setOriMissionId(int oriMissionId) {
		this.oriMissionId = oriMissionId;
	}

	public List getCargoInventoryMissionProductList() {
		return cargoInventoryMissionProductList;
	}

	public void setCargoInventoryMissionProductList(
			List cargoInventoryMissionProductList) {
		this.cargoInventoryMissionProductList = cargoInventoryMissionProductList;
	}

	public int getInventoryResult() {
		return inventoryResult;
	}

	public void setInventoryResult(int inventoryResult) {
		this.inventoryResult = inventoryResult;
	}

	public String getTypeName(){
		String name = StringUtil.convertNull((String)typeMap.get(Integer.valueOf(this.type)));
		if(this.type>=2){
			name = "复盘"+(type-1);
		}
		return name;
	}
	
	public String getStatusName(){
		return StringUtil.convertNull((String)statusMap.get(Integer.valueOf(this.status)));
	}
}
