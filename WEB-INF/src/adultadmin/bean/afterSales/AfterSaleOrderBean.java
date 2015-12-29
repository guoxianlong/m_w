package adultadmin.bean.afterSales;

import java.util.HashMap;
import java.util.List;

public class AfterSaleOrderBean {
	
	public static int STATUS_售后联系中 = 1;
	public static int STATUS_质检支撑时 = 0;
	public static int STATUS_等待包裹寄回 = 2;
	public static int STATUS_售后检测中 = 39;
	public static int STATUS_等待客户确认 = 59;
	public static int STATUS_售后处理中 = 69;
	public static int STATUS_售后已完成 = 79;
	public static int STATUS_售后未妥投 = 89;
	
	
	public static HashMap<Integer,String> STATUS_MAP = new HashMap<Integer,String>();
	static {
		STATUS_MAP.put(Integer.valueOf(STATUS_售后联系中), "售后联系中");
		STATUS_MAP.put(Integer.valueOf(STATUS_质检支撑时), "质检支撑时");
		STATUS_MAP.put(Integer.valueOf(STATUS_等待包裹寄回), "等待包裹寄回");
		STATUS_MAP.put(Integer.valueOf(STATUS_售后检测中), "售后检测中");
		
		STATUS_MAP.put(Integer.valueOf(STATUS_等待客户确认), "等待客户确认");
		STATUS_MAP.put(Integer.valueOf(STATUS_售后处理中), "售后处理中");
		STATUS_MAP.put(Integer.valueOf(STATUS_售后已完成), "售后已完成");
		STATUS_MAP.put(Integer.valueOf(STATUS_售后未妥投), "售后未妥投");
	}
	
	public int id; 
	public String afterSaleOrderCode;
	public int orderId;
	public String orderCode;
	public String productCode;
	public String customerName;
	public String customerPhone;
	public String customerAddress;
	public String customerPostCode;
	public String packageCode;
	public String orderConfirmTime;
	public String productReceiveTime;
	public int status;
	public String statusName;
	public int creatorId;
	public String creatorName;
	public String createTime;
	public int lastOperatorId;
	public String lastOperatorName;
	public String lastOperateTime;
	public int crownerId;
	public String crownerName;
	public String crownTime;
	public float orderCost;
	public int stockAreaId;
	public int amount;
	public String backpackageCode;	//客户寄回包裹单号 2010-03-10 李青
	public int prev_status;			//售后单的上一级状态 2010-03-12 李青
	public String packageCode1;  	// 返厂包裹单号1 不再使用 以后当作售后 维修 是否需要付费维修 1 为是  其他为不是
	public String packageCode2;		// 返厂包裹单号2
	public String packageCode3;		// 返厂包裹单号3
	public String customerBankName ;    // 开户行
	public String customerAccountOwnerName ;  //持卡人姓名
	public String customerAccount ;        //卡号
	public String customerBankAddress ;   //开户行地址
	public int companyBank ;
	public String remittanceTime ;// 汇款日期 没用。 当成备注最后日期。 点击保存全部的时候 记录日期
	public int productId;
	private String productNames;
	private String productCodes;
	private String productLineNames;
	
	private List afterSaleProduct;
	private AfterSaleCostListBean asclBean ;
	private String lastSolverRecord;//财务信息导出表报表。
	private String lastSolverName;
	private String lastSolverTime;
	
	public String problemDescription;//问题描述
	public int complaintTypeId;//text_res   中type为3 的 
	private String complaintTypeName;//投诉名称
	private float balance;
	
	public String mark;
	
	public String[] adds = new String[5];
	
	
	public String refundFailReason;
	public String operator;
	public String refundTime;
	
	public String buyModeName;
	private String flatName;//订单来源
	public String questionDis;//问题描述
	public String demand;//客户要求
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the afterSaleOrderCode
	 */
	public String getAfterSaleOrderCode() {
		return afterSaleOrderCode;
	}

	/**
	 * @param afterSaleOrderCode
	 *            the afterSaleOrderCode to set
	 */
	public void setAfterSaleOrderCode(String afterSaleOrderCode) {
		this.afterSaleOrderCode = afterSaleOrderCode;
	}

	/**
	 * @return the orderId
	 */
	public int getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId
	 *            the orderId to set
	 */
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return the orderCode
	 */
	public String getOrderCode() {
		return orderCode;
	}

	/**
	 * @param orderCode
	 *            the orderCode to set
	 */
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * @param productCode
	 *            the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	/**
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * @param customerName
	 *            the customerName to set
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * @return the customerPhone
	 */
	public String getCustomerPhone() {
		return customerPhone;
	}

	/**
	 * @param customerPhone
	 *            the customerPhone to set
	 */
	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	/**
	 * @return the customerAddress
	 */
	public String getCustomerAddress() {
		return customerAddress;
	}

	/**
	 * @param customerAddress
	 *            the customerAddress to set
	 */
	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	/**
	 * @return the customerPostCode
	 */
	public String getCustomerPostCode() {
		return customerPostCode;
	}

	/**
	 * @param customerPostCode
	 *            the customerPostCode to set
	 */
	public void setCustomerPostCode(String customerPostCode) {
		this.customerPostCode = customerPostCode;
	}

	/**
	 * @return the packageCode
	 */
	public String getPackageCode() {
		return packageCode;
	}

	/**
	 * @param packageCode
	 *            the packageCode to set
	 */
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	/**
	 * @return the orderConfirmTime
	 */
	public String getOrderConfirmTime() {
		return orderConfirmTime;
	}

	/**
	 * @param orderConfirmTime
	 *            the orderConfirmTime to set
	 */
	public void setOrderConfirmTime(String orderConfirmTime) {
		this.orderConfirmTime = orderConfirmTime;
	}

	/**
	 * @return the productReceiveTime
	 */
	public String getProductReceiveTime() {
		if(this.productReceiveTime == null){
			return this.productReceiveTime;
		}
		return productReceiveTime.substring(0, 10);
	}

	/**
	 * @param productReceiveTime
	 *            the productReceiveTime to set
	 */
	public void setProductReceiveTime(String productReceiveTime) {
		this.productReceiveTime = productReceiveTime;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the creatorId
	 */
	public int getCreatorId() {
		return creatorId;
	}

	/**
	 * @param creatorId
	 *            the creatorId to set
	 */
	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	/**
	 * @return the creatorName
	 */
	public String getCreatorName() {
		return creatorName;
	}

	/**
	 * @param creatorName
	 *            the creatorName to set
	 */
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	/**
	 * @return the createTime
	 */
	public String getCreateTime() {
		if(createTime!=null)	
			return createTime.replace(".0","");
		return "";
	}

	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the lastOperatorId
	 */
	public int getLastOperatorId() {
		return lastOperatorId;
	}

	/**
	 * @param lastOperatorId
	 *            the lastOperatorId to set
	 */
	public void setLastOperatorId(int lastOperatorId) {
		this.lastOperatorId = lastOperatorId;
	}

	/**
	 * @return the lastOperatorName
	 */
	public String getLastOperatorName() {
		return lastOperatorName;
	}

	/**
	 * @param lastOperatorName
	 *            the lastOperatorName to set
	 */
	public void setLastOperatorName(String lastOperatorName) {
		this.lastOperatorName = lastOperatorName;
	}

	/**
	 * @return the lastOperatTime
	 */
	public String getLastOperateTime() {
		return lastOperateTime;
	}

	/**
	 * @param lastOperatTime
	 *            the lastOperatTime to set
	 */
	public void setLastOperateTime(String lastOperateTime) {
		this.lastOperateTime = lastOperateTime;
	}

	/**
	 * @return the crownerId
	 */
	public int getCrownerId() {
		return crownerId;
	}

	/**
	 * @param crownerId
	 *            the crownerId to set
	 */
	public void setCrownerId(int crownerId) {
		this.crownerId = crownerId;
	}

	/**
	 * @return the crownerName
	 */
	public String getCrownerName() {
		return crownerName;
	}

	/**
	 * @param crownerName
	 *            the crownerName to set
	 */
	public void setCrownerName(String crownerName) {
		this.crownerName = crownerName;
	}

	/**
	 * @return the crownTime
	 */
	public String getCrownTime() {
		return crownTime;
	}

	/**
	 * @param crownTime
	 *            the crownTime to set
	 */
	public void setCrownTime(String crownTime) {
		this.crownTime = crownTime;
	}

	/**
	 * @return the orderCost
	 */
	public float getOrderCost() {
		return orderCost;
	}

	/**
	 * @param orderCost
	 *            the orderCost to set
	 */
	public void setOrderCost(float orderCost) {
		this.orderCost = orderCost;
	}

	/**
	 * @return the stockAreaId
	 */
	public int getStockAreaId() {
		return stockAreaId;
	}

	/**
	 * @param stockAreaId
	 *            the stockAreaId to set
	 */
	public void setStockAreaId(int stockAreaId) {
		this.stockAreaId = stockAreaId;
	}

	/**
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getBackpackageCode() {
		return backpackageCode;
	}

	public void setBackpackageCode(String backpackageCode) {
		this.backpackageCode = backpackageCode;
	}

	public int getPrev_status() {
		return prev_status;
	}

	public void setPrev_status(int prev_status) {
		this.prev_status = prev_status;
	}

	/**
	 * @return the packageCode1
	 */
	public String getPackageCode1() {
		return packageCode1==null?"":packageCode1;
	}

	/**
	 * @param packageCode1 the packageCode1 to set
	 */
	public void setPackageCode1(String packageCode1) {
		this.packageCode1 = packageCode1;
	}

	/**
	 * @return the packageCode2
	 */
	public String getPackageCode2() {
		return packageCode2==null?"":packageCode2;
	}

	/**
	 * @param packageCode2 the packageCode2 to set
	 */
	public void setPackageCode2(String packageCode2) {
		this.packageCode2 = packageCode2;
	}

	/**
	 * @return the packageCode3
	 */
	public String getPackageCode3() {
		return packageCode3;
	}

	/**
	 * @param packageCode3 the packageCode3 to set
	 */
	public void setPackageCode3(String packageCode3) {
		this.packageCode3 = packageCode3;
	}

	public String getCustomerBankName() {
		return customerBankName;
	}

	public void setCustomerBankName(String customerBankName) {
		this.customerBankName = customerBankName;
	}

	public String getCustomerAccountOwnerName() {
		return customerAccountOwnerName;
	}

	public void setCustomerAccountOwnerName(String customerAccountOwnerName) {
		this.customerAccountOwnerName = customerAccountOwnerName;
	}

	public String getCustomerAccount() {
		return customerAccount;
	}

	public void setCustomerAccount(String customerAccount) {
		this.customerAccount = customerAccount;
	}

	public String getCustomerBankAddress() {
		return customerBankAddress;
	}

	public void setCustomerBankAddress(String customerBankAddress) {
		this.customerBankAddress = customerBankAddress;
	}

	public int getCompanyBank() {
		return companyBank;
	}

	public void setCompanyBank(int companyBank) {
		this.companyBank = companyBank;
	}

	public String getRemittanceTime() {
		return remittanceTime;
	}

	public void setRemittanceTime(String remittanceTime) {
		this.remittanceTime = remittanceTime;
	}

	public String getProductNames() {
		return productNames;
	}

	public void setProductNames(String productNames) {
		this.productNames = productNames;
	}

	public String getProductLineNames() {
		return productLineNames;
	}

	public void setProductLineNames(String productLineNames) {
		this.productLineNames = productLineNames;
	}

	public List getAfterSaleProduct() {
		return afterSaleProduct;
	}

	public void setAfterSaleProduct(List afterSaleProduct) {
		this.afterSaleProduct = afterSaleProduct;
	}
	
	public String getStatusName(){
		return String.valueOf(AfterSaleOrderBean.STATUS_MAP.get(Integer.valueOf(status)));
	}

	public String getProblemDescription() {
		return problemDescription;
	}

	public void setProblemDescription(String problemDescription) {
		this.problemDescription = problemDescription;
	}

	public int getComplaintTypeId() {
		return complaintTypeId;
	}

	public void setComplaintTypeId(int complaintTypeId) {
		this.complaintTypeId = complaintTypeId;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getComplaintTypeName() {
		return complaintTypeName;
	}

	public void setComplaintTypeName(String complaintTypeName) {
		this.complaintTypeName = complaintTypeName;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}

	public AfterSaleCostListBean getAsclBean() {
		return asclBean;
	}

	public void setAsclBean(AfterSaleCostListBean asclBean) {
		this.asclBean = asclBean;
	}

	public String getLastSolverRecord() {
		return lastSolverRecord;
	}

	public void setLastSolverRecord(String lastSolverRecord) {
		this.lastSolverRecord = lastSolverRecord;
	}

	public String getLastSolverName() {
		return lastSolverName;
	}

	public void setLastSolverName(String lastSolverName) {
		this.lastSolverName = lastSolverName;
	}

	public String getLastSolverTime() {
		return lastSolverTime;
	}

	public void setLastSolverTime(String lastSolverTime) {
		this.lastSolverTime = lastSolverTime;
	}

	public String[] getAdds() {
		return adds;
	}

	public void setAdds(int addId1,int addId2,int addId3,int addId4,String add5) {
		adds[0] = String.valueOf(addId1);
		adds[1] = String.valueOf(addId2);
		adds[2] = String.valueOf(addId3);
		adds[3] = String.valueOf(addId4);
		adds[4] = add5;
	}

	public String getRefundFailReason() {
		return refundFailReason;
	}

	public void setRefundFailReason(String refundFailReason) {
		this.refundFailReason = refundFailReason;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getRefundTime() {
		return refundTime;
	}

	public void setRefundTime(String refundTime) {
		this.refundTime = refundTime;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getProductCodes() {
		return productCodes;
	}

	public void setProductCodes(String productCodes) {
		this.productCodes = productCodes;
	}

	public String getBuyModeName() {
		return buyModeName;
	}

	public void setBuyModeName(String buyModeName) {
		this.buyModeName = buyModeName;
	}

	public String getQuestionDis() {
		return questionDis;
	}

	public void setQuestionDis(String questionDis) {
		this.questionDis = questionDis;
	}

	public String getDemand() {
		return demand;
	}

	public void setDemand(String demand) {
		this.demand = demand;
	}

	public String getFlatName() {
		return flatName;
	}

	public void setFlatName(String flatName) {
		this.flatName = flatName;
	}
	
	
}
