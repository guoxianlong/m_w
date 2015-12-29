/*
 * Created on 2007-6-28
 *
 */
package adultadmin.bean.stock;

import adultadmin.action.vo.voUser;
import adultadmin.util.DateUtil;
/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2009-2-19
 * 
 * 说明：库存调配，从一个库调配商品到另外一个库；
 * <pre>
 * 过程如下：
 * 	1.生成调配单
 * 	2.选择出货库，添加调配商品，并且填写数量
 * 	3.选择入货库
 * 	4.确认出库以后，减少库中商品的数量，将商品数量转移到 lock中，锁定待用量。
 * 	5.出库审核
 * 	6.确认入库
 * 	7.入库审核，通过审核以后，转移商品库lock中的待用量到新的库中，增加库存。
 * </pre>
 */
public class StockExchangeBean {

	/**
	 * 状态：未处理
	 */
	public static int STATUS0 = 0;

	/**
	 * 状态：出库处理中
	 */
	public static int STATUS1 = 1;

	/**
	 * 状态：已出库未审核
	 */
	public static int STATUS2 = 2;

	/**
	 * 状态：出库已审核待入库
	 */
	public static int STATUS3 = 3;

	/**
	 * 状态：出库审核未通过
	 */
	public static int STATUS4 = 4;

	/**
	 * 状态：入库处理中
	 */
	public static int STATUS5 = 5;
	
	/**
	 * 状态：已入库未审核
	 */
	public static int STATUS6 = 6;
	
	/**
	 * 状态：入库审核通过
	 */
	public static int STATUS7 = 7;

	/**
	 * 状态：入库审核未通过
	 */
	public static int STATUS8 = 8;
	
	/**
	 * 上架状态：未上架
	 */
	public static int UP_SHELF_STATUS0 = 0;
	
	/**
	 * 上架状态：上架中
	 */
	public static int UP_SHELF_STATUS1 = 1;
	
	/**
	 * 上架状态：已上架
	 */
	public static int UP_SHELF_STATUS2 = 2;
	
	/**
	 * 紧急程度：一般
	 */
	public static int PRIOR_STATUS0  = 0;
	
	/**
	 * 紧急程度：紧急
	 */
	public static int PRIOR_STATUS1  = 1;
	
	public int id;

	public String name;

	public int status;

	public String createDatetime;

	public String remark;

	public String deadline;

	public String confirmDatetime;

	public int printCount;

	
	public int stockInArea; // 目标库 地区
	public int stockOutArea; // 原库地区
	public int stockInType; //目标库类型
	public int stockOutType; //原库类型 

	public int createUserId;
	/**
	 * 出库的审核人
	 */
	public int auditingUserId;

	/**
	 * 入库的审核人
	 */
	public int auditingUserId2;

	/**
	 * 出库操作人ID
	 */
	public int stockOutOper;
	/**
	 * 入库操作人ID
	 */
	public int stockInOper;
	/**
	 * 出库操作人姓名
	 */
	public String stockOutOperName;
	/**
	 * 出库审核人姓名
	 */
	public String auditingUserName;
	/**
	 * 入库操作人姓名
	 */
	public String stockInOperName;
	/**
	 * 入库审核人姓名
	 */
	public String auditingUserName2;
	/**
	 * 调拨单创建人姓名
	 */
	public String createUserName;

	public String code;
	
	/**
	 * 上架状态
	 */
	public int upShelfStatus;

	public voUser createUser;

	/**
	 * 出库审核人
	 */
	public voUser auditingUser;

	/**
	 * 入库审核人
	 */
	public voUser auditingUser2;

	/**
	 * 调拨紧急程度
	 */
	public int priorStatus;
	private int operationNum;
	
	private int exchangeCount;//调拨量
	
	public voUser stockOutOperUser;
	public voUser stockInOperUser;
	
	private boolean afterSaleFlag;

	public String getStockOutOperName() {
		return stockOutOperName;
	}
	public void setStockOutOperName(String stockOutOperName) {
		this.stockOutOperName = stockOutOperName;
	}

	public String getAuditingUserName() {
		return auditingUserName;
	}

	public void setAuditingUserName(String auditingUserName) {
		this.auditingUserName = auditingUserName;
	}

	public String getStockInOperName() {
		return stockInOperName;
	}

	public void setStockInOperName(String stockInOperName) {
		this.stockInOperName = stockInOperName;
	}

	public String getAuditingUserName2() {
		return auditingUserName2;
	}

	public void setAuditingUserName2(String auditingUserName2) {
		this.auditingUserName2 = auditingUserName2;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getConfirmDatetime() {
		return confirmDatetime;
	}

	public void setConfirmDatetime(String confirmDatetime) {
		this.confirmDatetime = confirmDatetime;
	}

	public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getUpShelfStatus() {
		return upShelfStatus;
	}

	public void setUpShelfStatus(int upShelfStatus) {
		this.upShelfStatus = upShelfStatus;
	}

	public int getPrintCount() {
		return printCount;
	}

	public void setPrintCount(int printCount) {
		this.printCount = printCount;
	}

	public boolean isOvertime(){
		long deadline = DateUtil.parseDate(this.deadline).getTime();
		long now = DateUtil.getNowDate().getTime();
		return (deadline < now);
	}

	public int getAuditingUserId() {
		return auditingUserId;
	}

	public void setAuditingUserId(int auditingUserId) {
		this.auditingUserId = auditingUserId;
	}

	public int getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getStockInArea() {
		return stockInArea;
	}

	public void setStockInArea(int stockInArea) {
		this.stockInArea = stockInArea;
	}

	public int getStockInType() {
		return stockInType;
	}

	public void setStockInType(int stockInType) {
		this.stockInType = stockInType;
	}

	public int getStockOutArea() {
		return stockOutArea;
	}

	public void setStockOutArea(int stockOutArea) {
		this.stockOutArea = stockOutArea;
	}

	public int getStockOutType() {
		return stockOutType;
	}

	public void setStockOutType(int stockOutType) {
		this.stockOutType = stockOutType;
	}

	public voUser getCreateUser() {
		return createUser;
	}

	public void setCreateUser(voUser createUser) {
		this.createUser = createUser;
	}

	public voUser getAuditingUser2() {
		return auditingUser2;
	}

	public void setAuditingUser2(voUser auditingUser2) {
		this.auditingUser2 = auditingUser2;
	}

	public voUser getAuditingUser() {
		return auditingUser;
	}

	public void setAuditingUser(voUser auditingUser) {
		this.auditingUser = auditingUser;
	}

	public int getAuditingUserId2() {
		return auditingUserId2;
	}

	public void setAuditingUserId2(int auditingUserId2) {
		this.auditingUserId2 = auditingUserId2;
	}

	public int getStockInOper() {
		return stockInOper;
	}

	public void setStockInOper(int stockInOper) {
		this.stockInOper = stockInOper;
	}

	public voUser getStockInOperUser() {
		return stockInOperUser;
	}

	public void setStockInOperUser(voUser stockInOperUser) {
		this.stockInOperUser = stockInOperUser;
	}

	public int getStockOutOper() {
		return stockOutOper;
	}

	public void setStockOutOper(int stockOutOper) {
		this.stockOutOper = stockOutOper;
	}

	public voUser getStockOutOperUser() {
		return stockOutOperUser;
	}

	public void setStockOutOperUser(voUser stockOutOperUser) {
		this.stockOutOperUser = stockOutOperUser;
	}

	public String getStatusName() {
		String statusName = "";
		if(this.status == STATUS0){
			statusName = "未处理";
		} else if(this.status == STATUS1){
			statusName = "出库处理中";
		} else if(this.status == STATUS2){
			statusName = "出库审核中";
		} else if(this.status == STATUS3){
			statusName = "已审核待入库";
		} else if(this.status == STATUS4){
			statusName = "出库审核未通过";
		} else if(this.status == STATUS5){
			statusName = "入库处理中";
		} else if(this.status == STATUS6){
			statusName = "入库审核中";
		} else if(this.status == STATUS7){
			statusName = "调拨完成";
		} else if(this.status == STATUS8){
			statusName = "入库审核未通过";
		}
		return statusName;
	}
	
	public String getUpShelfStatusName() {
		String statusName = "";
		if(this.upShelfStatus == UP_SHELF_STATUS0){
			statusName = "未上架";
		} else if(this.upShelfStatus == UP_SHELF_STATUS1){
			statusName = "上架中";
		} else if(this.upShelfStatus == UP_SHELF_STATUS2){
			statusName = "已上架";
		}
		return statusName;
	}
	   public String getPriorStatusName(){
	    	String statusName = "";
	    	if(this.priorStatus == PRIOR_STATUS0){
				statusName = "一般";
			} else if(this.priorStatus == PRIOR_STATUS1){
				statusName = "紧急";
			}
	    	return statusName;
	    }
	public int getOperationNum() {
		return operationNum;
	}

	public void setOperationNum(int operationNum) {
		this.operationNum = operationNum;
	}
	public int getPriorStatus() {
		return priorStatus;
	}

	public void setPriorStatus(int priorStatus) {
		this.priorStatus = priorStatus;
	}
	public int getExchangeCount() {
		return exchangeCount;
	}
	public void setExchangeCount(int exchangeCount) {
		this.exchangeCount = exchangeCount;
	}
	
	public boolean isAfterSaleFlag() {
		return afterSaleFlag;
	}
	public void setAfterSaleFlag(boolean afterSaleFlag) {
		this.afterSaleFlag = afterSaleFlag;
	}
	public boolean equals(Object otherObject) {
		if (this == otherObject)
			return true;
		if (otherObject == null)
			return false;
		if (getClass() != otherObject.getClass())
			return false;
		StockExchangeBean other = (StockExchangeBean) otherObject;
		return id==other.id;
	}

	public int hashCode() {
		int result = 17;
		int idValue = this.getId() == 0 ? 0 : Integer.valueOf(this.getId()).hashCode();
		result = result * 37 + idValue;
		return result;
	}
	
}
