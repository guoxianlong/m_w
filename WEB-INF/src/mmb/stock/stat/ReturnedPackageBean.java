package mmb.stock.stat;

import adultadmin.bean.cargo.ReturnsReasonBean;

/**
 * 退货包裹信息
 */
public class ReturnedPackageBean {
	
	public int id;
	public int orderId;//订单id
	public String orderCode;//订单编号
	public String packageCode;//包裹编号
	public int deliver;//快递公司
	public String deliverName;//快递公司名称
	public int operatorId;//操作人员id
	public String operatorName;//操作人名称
	public String storageTime;//入库时间
	public int storageStatus;//入库状态
	public String remark;//备注
	public int reasonId;//退货原因Id
	public String reasonName; //退货原因名称
	public int claimsVerificationId; //对应的理赔核销单id
	public ClaimsVerificationBean claimsVerificationBean;
	public ReturnsReasonBean returnsReasonBean;
	public int area; //退货包裹的入库地区
	public String areaName;//退货包裹的入库地区名称
	public int status; //退货包裹状态
	public String importTime;  //导入时间
	public String importUserName;	//导入用户名
	public int importUserId;		//导入用户id
	
	public String orderStatusName;//订单状态名称
	public String claimsVerificationCode;//对应的理赔核销单编号
	public String claimsVerificationStatusName;//对应的理赔核销单的状态
	public String returnedReason;//???
	
	/**
	 * 正常入库
	 */
	public static int NORMALENTER=0;
	/**
	 * 异常入库,商品缺失
	 */
	public static int LOSTPRODUCTENTER=1;
	/**
	 * 异常入库，订单和包裹不匹配
	 */
	public static int UNMATCHENTER=2;
	
	/**
	 * 退货包裹状态   待退回
	 */
	public static int STATUS_TO_RETURN = 0;
	/**
	 * 退货包裹状态   已退回
	 */
	public static int STATUS_HAS_RETURN = 1;
	

	//hp 复核时间
	public String checkDatetime; 
	
	
	public String getCheckDatetime() {
		return checkDatetime;
	}
	public void setCheckDatetime(String checkDatetime) {
		this.checkDatetime = checkDatetime;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getClaimsVerificationCode() {
		return claimsVerificationCode;
	}
	public void setClaimsVerificationCode(String claimsVerificationCode) {
		this.claimsVerificationCode = claimsVerificationCode;
	}
	public String getClaimsVerificationStatusName() {
		return claimsVerificationStatusName;
	}
	public void setClaimsVerificationStatusName(String claimsVerificationStatusName) {
		this.claimsVerificationStatusName = claimsVerificationStatusName;
	}
	public String getReturnedReason() {
		return returnedReason;
	}
	public void setReturnedReason(String returnedReason) {
		this.returnedReason = returnedReason;
	}
	public String getDeliverName() {
		return deliverName;
	}
	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
	}
	public String getReasonName() {
		return reasonName;
	}
	public void setReasonName(String reasonName) {
		this.reasonName = reasonName;
	}
	public int getReasonId() {
		return reasonId;
	}
	public void setReasonId(int reasonId) {
		this.reasonId = reasonId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
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
	public String getStorageTime() {
		return storageTime;
	}
	public void setStorageTime(String storageTime) {
		this.storageTime = storageTime;
	}
	public int getStorageStatus() {
		return storageStatus;
	}
	public void setStorageStatus(int storageStatus) {
		this.storageStatus = storageStatus;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getClaimsVerificationId() {
		return claimsVerificationId;
	}
	public void setClaimsVerificationId(int claimsVerificationId) {
		this.claimsVerificationId = claimsVerificationId;
	}
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	public ClaimsVerificationBean getClaimsVerificationBean() {
		return claimsVerificationBean;
	}
	public void setClaimsVerificationBean(
			ClaimsVerificationBean claimsVerificationBean) {
		this.claimsVerificationBean = claimsVerificationBean;
	}
	public ReturnsReasonBean getReturnsReasonBean() {
		return returnsReasonBean;
	}
	public void setReturnsReasonBean(ReturnsReasonBean returnsReasonBean) {
		this.returnsReasonBean = returnsReasonBean;
	}
	public String getOrderStatusName() {
		return orderStatusName;
	}
	public void setOrderStatusName(String orderStatusName) {
		this.orderStatusName = orderStatusName;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getReturnedPackageStatusName() {
		if( this.status == ReturnedPackageBean.STATUS_TO_RETURN ) {
			return "待退回";
		} else if( this.status == ReturnedPackageBean.STATUS_HAS_RETURN ) {
			return "已退回";
		} else {
			return "";
		}
	}
	public String getImportTime() {
		return importTime;
	}
	public void setImportTime(String importTime) {
		this.importTime = importTime;
	}
	public String getImportUserName() {
		return importUserName;
	}
	public void setImportUserName(String importUserName) {
		this.importUserName = importUserName;
	}
	public int getImportUserId() {
		return importUserId;
	}
	public void setImportUserId(int importUserId) {
		this.importUserId = importUserId;
	}
}
