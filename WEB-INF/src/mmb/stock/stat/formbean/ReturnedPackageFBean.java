package mmb.stock.stat.formbean;

import org.apache.struts.action.ActionForm;

public class ReturnedPackageFBean extends ActionForm {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String packageCode;//包裹编号
	private String orderCode;//订单编号
	private String productBarCode;//商品条码
	private String type;//表示正常入库还是异常入库，如果为0正常入库，1，包裹和订单不匹配异常入库，2，订单中商品数量不足，异常入库
	private String exceptionPCode;//异常入库商品编号
	private String payFlag;//是否索赔
	private String remark;//异常备注
	private int deliver=-1;//邮局,默认为未选择
	private String operator;//操作人
	private String storageTime = null;
	private String storageStartTime=null;//入库时间开始
	private String storageEndTime=null;//入库时间结束
	private String endTime=null;//hp入库时间
	
	private String checkStartTime;//复核查询开始时间
	public String getCheckStartTime() {
		return checkStartTime;
	}

	public void setCheckStartTime(String checkStartTime) {
		this.checkStartTime = checkStartTime;
	}

	public String getCheckEndTime() {
		return checkEndTime;
	}

	public void setCheckEndTime(String checkEndTime) {
		this.checkEndTime = checkEndTime;
	}

	private String checkEndTime;//复核查询结束时间
	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	private int storageStatus = 1;//入库状态
	private int reasonId;//退货原因Id
	private int wareArea = -1; //库地区
	private int cvStatus = -1;		//理赔单状态
	private int orderStatus = -1;	//订单状态  暂时只有  已退回 和 待退回
	private int returnedPackageStatus = -1; //退货包裹状态
	
	
	public String getPackageCode() {
		return packageCode;
	}
	
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}
	
	public String getOrderCode() {
		return orderCode;
	}
	
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	
	public String getProductBarCode() {
		return productBarCode;
	}
	
	public void setProductBarCode(String productBarCode) {
		this.productBarCode = productBarCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getExceptionPCode() {
		return exceptionPCode;
	}

	public void setExceptionPCode(String exceptionPCode) {
		this.exceptionPCode = exceptionPCode;
	}

	public String getPayFlag() {
		return payFlag;
	}

	public void setPayFlag(String payFlag) {
		this.payFlag = payFlag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getDeliver() {
		return deliver;
	}

	public void setDeliver(int deliver) {
		this.deliver = deliver;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public int getStorageStatus() {
		return storageStatus;
	}

	public void setStorageStatus(int storageStatus) {
		this.storageStatus = storageStatus;
	}

	public int getReasonId() {
		return reasonId;
	}

	public void setReasonId(int reasonId) {
		this.reasonId = reasonId;
	}

	public int getWareArea() {
		return wareArea;
	}

	public void setWareArea(int wareArea) {
		this.wareArea = wareArea;
	}

	public int getCvStatus() {
		return cvStatus;
	}

	public void setCvStatus(int cvStatus) {
		this.cvStatus = cvStatus;
	}

	public int getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(int orderStatus) {
		this.orderStatus = orderStatus;
	}

	public int getReturnedPackageStatus() {
		return returnedPackageStatus;
	}

	public void setReturnedPackageStatus(int returnedPackageStatus) {
		this.returnedPackageStatus = returnedPackageStatus;
	}

	public String getStorageStartTime() {
		return storageStartTime;
	}

	public void setStorageStartTime(String storageStartTime) {
		this.storageStartTime = storageStartTime;
	}

	public String getStorageEndTime() {
		return storageEndTime;
	}

	public void setStorageEndTime(String storageEndTime) {
		this.storageEndTime = storageEndTime;
	}

	public String getStorageTime() {
		return storageTime;
	}

	public void setStorageTime(String storageTime) {
		this.storageTime = storageTime;
	}
	
}
