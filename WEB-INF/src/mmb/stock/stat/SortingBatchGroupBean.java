package mmb.stock.stat;

import java.util.List;

import adultadmin.bean.cargo.CargoStaffBean;

public class SortingBatchGroupBean {
	
	/**
	 * 未打单
	 */
	public static int STATUS0 = 0;
	/**
	 * 分拣中
	 */
	public static int STATUS1 = 1;
	/**
	 * 已完成
	 */
	public static int STATUS2 = 2;
	/**
	 * 已废弃
	 */
	public static int STATUS3 = 3;
	
	/**
	 * 二次分拣未开始
	 */
	public static int SORTING_STATUS0 = 0;
	/**
	 * 二次分拣已开始
	 */
	public static int SORTING_STATUS1 = 1;
	/**
	 * 二次分拣已结批
	 */
	public static int SORTING_STATUS2 = 2;
	
	public int id;
	
    //波次号 code
	public String code;
	
	//分拣批次号：sorting_batch_id
	public int sortingBatchId;
	
	//创建时间 create_datetime,datetime
	public String createDatetime;
	
	//完成时间 complete_datetime,datetime
	public String completeDatetime;
	
	//快递公司 deliver,int(11)////
	public int deliver;
	
	//分拣人id staff_id,int(11)
	public int staffId;
	
	//分拣人姓名 staff_name,varchar(45)
	public String staffName;
	//分播人姓名 staff_name2,varchar(45)
	public String staffName2;
	
	//作业仓 storage,int(11)
	public int storage;
	
	//分拣状态 status
	public int status;
	
	// 分拣波次订单列表 sorting_batch_order，表中无此字段	
	public List sortingBatchOrder;
	
	//SKU数 skuCount ，表中无此字段	
	public int skuCount;
	
	//商品件数 productCount ，表中无此字段	
	public int productCount;
	
	//订单总数orderCount，表中无此字段
	public int orderCount;
	
//	//分拣波次 sorting_batch_group
//	public int sortingGroupId;
	
	//明细中的时间	
	public String sortingTime;
	
	public List orderList;
	
	//快递公司名称：deliverName; //表中无此字段
	public String deliverName;
	
	//仓库区域名称，如增城：storageName;//表中无此字段
	public String storageName;
	
	//分拣员：CargoStaffBean cargoStaff;
	public CargoStaffBean cargoStaff;
	
	//巷道数量(分拣量统计时使用该字段)
	public int passageCount;
	
	//波次数量(分拣量统计时使用该字段)
	public int groupCount;
	//hp 波次商品集合
	public List<SortingBatchOrderProductBean> sbpblist;
	
	
	public List<SortingBatchOrderProductBean> getSbpblist() {
		return sbpblist;
	}
	public void setSbpblist(List<SortingBatchOrderProductBean> sbpblist) {
		this.sbpblist = sbpblist;
	}
	//出勤天数(分拣量统计时使用该字段)
	public int attendanceCount;
	
	//员工数(分拣量统计时使用该字段)
	public int staffCount;
	
	//员工号(分拣量统计时使用该字段)
	public String staffCode;
	
	//领单时间 receive_datetime
	public String receiveDatetime;
	
	//分拣超时订单数量
	public  int overTimeOrderCount;
	
	//作业开始时间
	public  String begindatetime;
	
	//最后领单时间
	public  String finallReceiveOrderTime;
	
	//完成订单数量
	public  int completeOrderCount;
	
	//未完成订单数量
	public  int noCompleteOrderCount;
	
	//小组起始时间
	public String teamBeginTime;
	
	//小组结束时间
	public String teamEndTime;
	
	//订单编号
	public String orderCode;
	//判断该波次是多sku批次还是单sku批次,0:单，1：多
	public int type1;
	//判断该波次是否是ems波次,0:是，1：不是
	public int type2;
	
	//二次分拣操作员工id
	public int staffId2;
	
	//二次分拣状态
	public int status2;
	
	//二次分拣开始时间
	public String receiveDatetime2;
	//二次分拣结束时间
	public String completeDatetime2;
	
	//分播耗时
	public int secondeSortingTime;
	//分拣状态  0是未开始，1是分拣中，2是分拣完成
	public int sortingStatus;
	//分拣类型  0是手工分拣，1是pda分拣
	public int sortingType;
	//分拣完成时间
	public String sortingCompleteDatetime;
	
	//sku行数
	public  int skuRowCount;
	
	//pda分拣量
	public  int sortingCount;
		
	//分播量
	public  int completeCount;
	
	private int cargoId; //关联巷道的货位id

	public int getCargoId() {
		return cargoId;
	}
	public void setCargoId(int cargoId) {
		this.cargoId = cargoId;
	}
	/**
	 * 撤单量
	 */
	public int cancelOrderCount;
	public CargoStaffBean getCargoStaff() {
		return cargoStaff;
	}
	public void setCargoStaff(CargoStaffBean cargoStaff) {
		this.cargoStaff = cargoStaff;
	}
	
	public String getDeliverName() {
		return deliverName;
	}
	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
	}
	public String getStorageName() {
		return storageName;
	}
	public void setStorageName(String storageName) {
		this.storageName = storageName;
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
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public String getCompleteDatetime() {
		return completeDatetime;
	}
	public void setCompleteDatetime(String completeDatetime) {
		this.completeDatetime = completeDatetime;
	}
	public int getDeliver() {
		return deliver;
	}
	public void setDeliver(int deliver) {
		this.deliver = deliver;
	}
	public int getStaffId() {
		return staffId;
	}
	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}
	public String getStaffName() {
		return staffName;
	}
	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}
	public int getStorage() {
		return storage;
	}
	public void setStorage(int storage) {
		this.storage = storage;
	}
	public int getStatus() {
		return status;
	}
	public String getStatusName() {
		if (status == STATUS0) {
			return "未打单";
		}else if(status == STATUS1){
			return "分拣中";
		}else if(status == STATUS2){
			return "已完成";
		}else if(status == STATUS3){
			return "已废弃";
		}
		else{
			return "";
		}
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public List getSortingBatchOrder() {
		return sortingBatchOrder;
	}
	public void setSortingBatchOrder(List sortingBatchOrder) {
		this.sortingBatchOrder = sortingBatchOrder;
	}
	public int getSkuCount() {
		return skuCount;
	}
	public void setSkuCount(int skuCount) {
		this.skuCount = skuCount;
	}
	public int getProductCount() {
		return productCount;
	}
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	public int getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}
	public int getSortingBatchId() {
		return sortingBatchId;
	}
	public void setSortingBatchId(int sortingBatchId) {
		this.sortingBatchId = sortingBatchId;
	}
//	public int getSortingGroupId() {
//		return sortingGroupId;
//	}
//	public void setSortingGroupId(int sortingGroupId) {
//		this.sortingGroupId = sortingGroupId;
//	}
	public List getOrderList() {
		return orderList;
	}
	public void setOrderList(List orderList) {
		this.orderList = orderList;
	}
	public int getPassageCount() {
		return passageCount;
	}
	public void setPassageCount(int passageCount) {
		this.passageCount = passageCount;
	}
	public int getGroupCount() {
		return groupCount;
	}
	public void setGroupCount(int groupCount) {
		this.groupCount = groupCount;
	}
	public int getAttendanceCount() {
		return attendanceCount;
	}
	public void setAttendanceCount(int attendanceCount) {
		this.attendanceCount = attendanceCount;
	}
	public int getStaffCount() {
		return staffCount;
	}
	public void setStaffCount(int staffCount) {
		this.staffCount = staffCount;
	}
	public String getStaffCode() {
		return staffCode;
	}
	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}
	public String getSortingTime() {
		return sortingTime;
	}
	public void setSortingTime(String sortingTime) {
		this.sortingTime = sortingTime;
	}
	public String getReceiveDatetime() {
		return receiveDatetime;
	}
	public void setReceiveDatetime(String receiveDatetime) {
		this.receiveDatetime = receiveDatetime;
	}
	public int getOverTimeOrderCount() {
		return overTimeOrderCount;
	}
	public void setOverTimeOrderCount(int overTimeOrderCount) {
		this.overTimeOrderCount = overTimeOrderCount;
	}
	public String getBegindatetime() {
		return begindatetime;
	}
	public void setBegindatetime(String begindatetime) {
		this.begindatetime = begindatetime;
	}
	public String getFinallReceiveOrderTime() {
		return finallReceiveOrderTime;
	}
	public void setFinallReceiveOrderTime(String finallReceiveOrderTime) {
		this.finallReceiveOrderTime = finallReceiveOrderTime;
	}
	public int getCompleteOrderCount() {
		return completeOrderCount;
	}
	public void setCompleteOrderCount(int completeOrderCount) {
		this.completeOrderCount = completeOrderCount;
	}
	public int getNoCompleteOrderCount() {
		return noCompleteOrderCount;
	}
	public void setNoCompleteOrderCount(int noCompleteOrderCount) {
		this.noCompleteOrderCount = noCompleteOrderCount;
	}
	public String getTeamBeginTime() {
		return teamBeginTime;
	}
	public void setTeamBeginTime(String teamBeginTime) {
		this.teamBeginTime = teamBeginTime;
	}
	public String getTeamEndTime() {
		return teamEndTime;
	}
	public void setTeamEndTime(String teamEndTime) {
		this.teamEndTime = teamEndTime;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public int getType1() {
		return type1;
	}
	public void setType1(int type1) {
		this.type1 = type1;
	}
	public int getType2() {
		return type2;
	}
	public void setType2(int type2) {
		this.type2 = type2;
	}
	public int getStaffId2() {
		return staffId2;
	}
	public void setStaffId2(int staffId2) {
		this.staffId2 = staffId2;
	}
	public int getStatus2() {
		return status2;
	}
	public void setStatus2(int status2) {
		this.status2 = status2;
	}
	public String getReceiveDatetime2() {
		return receiveDatetime2;
	}
	public void setReceiveDatetime2(String receiveDatetime2) {
		this.receiveDatetime2 = receiveDatetime2;
	}
	public String getCompleteDatetime2() {
		return completeDatetime2;
	}
	public void setCompleteDatetime2(String completeDatetime2) {
		this.completeDatetime2 = completeDatetime2;
	}
	public String getStaffName2() {
		return staffName2;
	}
	public void setStaffName2(String staffName2) {
		this.staffName2 = staffName2;
	}
	public int getSecondeSortingTime() {
		return secondeSortingTime;
	}
	public void setSecondeSortingTime(int secondeSortingTime) {
		this.secondeSortingTime = secondeSortingTime;
	}
	public int getCancelOrderCount() {
		return cancelOrderCount;
	}
	public void setCancelOrderCount(int cancelOrderCount) {
		this.cancelOrderCount = cancelOrderCount;
	}
	public int getSortingStatus() {
		return sortingStatus;
	}
	public void setSortingStatus(int sortingStatus) {
		this.sortingStatus = sortingStatus;
	}
	public int getSortingType() {
		return sortingType;
	}
	public void setSortingType(int sortingType) {
		this.sortingType = sortingType;
	}
	public String getSortingCompleteDatetime() {
		return sortingCompleteDatetime;
	}
	public void setSortingCompleteDatetime(String sortingCompleteDatetime) {
		this.sortingCompleteDatetime = sortingCompleteDatetime;
	}
	public int getSkuRowCount() {
		return skuRowCount;
	}
	public void setSkuRowCount(int skuRowCount) {
		this.skuRowCount = skuRowCount;
	}
	public int getSortingCount() {
		return sortingCount;
	}
	public void setSortingCount(int sortingCount) {
		this.sortingCount = sortingCount;
	}
	public int getCompleteCount() {
		return completeCount;
	}
	public void setCompleteCount(int completeCount) {
		this.completeCount = completeCount;
	}
	
}
