package adultadmin.bean.stat;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *作者：石远飞
 *
 *日期：2013-4-3
 *
 *说明：异常入库单Bean
 * */
public class WarehousingAbnormalBean {
	public int id;
	public String code; //异常入库单编号
	public String createTime; //创建时间
	public int operatorId; //操作人id
	public String operatorName;//操作人姓名
	public int status; // 状态
	public int wareArea;//库地区
	public int orderId; //订单id
	
	public String statusName;
	//下面属性不是表内属性
	public String orderCode; //订单编号
	public String packageCode; //包裹单号
	public int deliver;	//快递公司
	public String deliverName;
	public String sortingDatetime; //发货日期
	public int bsbyType; //报损报溢单类型
	public int bsbyId;	//报损报溢单id
	public int bsbyStatus;	//报损报溢单状态
	public String receiptsNumber;	//报损报溢单编号
	public int bsbyOperatorId;//报损报溢单操作人
	
	//改为easyui时判断权限
	//作者：张晔
	//添加权限
	public String isAdd = "false";
	//编辑和删除权限
	public String isEditAndDel = "false" ;
	//审核权限
	public String isAudit = "false";
	
	public String receiptsNumberString;
	public String bsbyIdString;
	public String lookupString;
	
	/**
	 * 未处理
	 */
	public static final int STATUS0 = 0;
	/**
	 * 已提交
	 */
	public static final int STATUS1 = 1;
	/**
	 * 已审核
	 */
	public static final int STATUS2 = 2;
	
	public static HashMap<Integer,String>  statusMap = new LinkedHashMap<Integer, String>();
	static{
		statusMap.put(-1, "未选择");
		statusMap.put(STATUS0, "未处理");
		statusMap.put(STATUS1, "已提交");
		statusMap.put(STATUS2, "已审核");
	}
	public String getStatusName(int key){
		String result = null;
		result = statusMap.get(this.status);
		if(result == null){
			result = "";
		}
		return result;
	}
	public HashMap<Integer, String> getStatusMap() {
		return statusMap;
	}
	
	public int getBsbyOperatorId() {
		return bsbyOperatorId;
	}

	public void setBsbyOperatorId(int bsbyOperatorId) {
		this.bsbyOperatorId = bsbyOperatorId;
	}

	public String getReceiptsNumberString() {
		return receiptsNumberString;
	}
	public void setReceiptsNumberString(String receiptsNumberString) {
		this.receiptsNumberString = receiptsNumberString;
	}
	public String getBsbyIdString() {
		return bsbyIdString;
	}
	public void setBsbyIdString(String bsbyIdString) {
		this.bsbyIdString = bsbyIdString;
	}
	public String getLookupString() {
		return lookupString;
	}
	public void setLookupString(String lookupString) {
		this.lookupString = lookupString;
	}
	public String getIsAdd() {
		return isAdd;
	}
	public void setIsAdd(String isAdd) {
		this.isAdd = isAdd;
	}
	public String getIsEditAndDel() {
		return isEditAndDel;
	}
	public void setIsEditAndDel(String isEditAndDel) {
		this.isEditAndDel = isEditAndDel;
	}
	public String getIsAudit() {
		return isAudit;
	}
	public void setIsAudit(String isAudit) {
		this.isAudit = isAudit;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getDeliverName() {
		return deliverName;
	}
	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
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
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public int getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	public int getWareArea() {
		return wareArea;
	}

	public void setWareArea(int wareArea) {
		this.wareArea = wareArea;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public int getDeliver() {
		return deliver;
	}

	public void setDeliver(int deliver) {
		this.deliver = deliver;
	}

	public String getSortingDatetime() {
		return sortingDatetime;
	}

	public void setSortingDatetime(String sortingDatetime) {
		this.sortingDatetime = sortingDatetime;
	}

	public int getBsbyType() {
		return bsbyType;
	}

	public void setBsbyType(int bsbyType) {
		this.bsbyType = bsbyType;
	}

	public int getBsbyId() {
		return bsbyId;
	}

	public void setBsbyId(int bsbyId) {
		this.bsbyId = bsbyId;
	}

	public int getBsbyStatus() {
		return bsbyStatus;
	}

	public void setBsbyStatus(int bsbyStatus) {
		this.bsbyStatus = bsbyStatus;
	}

	public String getReceiptsNumber() {
		return receiptsNumber;
	}

	public void setReceiptsNumber(String receiptsNumber) {
		this.receiptsNumber = receiptsNumber;
	}
	
}
