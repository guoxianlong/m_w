package adultadmin.bean.balance;

import java.util.List;

import adultadmin.action.vo.voUser;

/**
 * 作者：赵林
 * 
 * 说明：结算数据确认
 *
 */
public class MailingBalanceAuditingBean {
	
	/**
	 * 未处理
	 */
	public static int STATUS0 = 0;

	/**
	 * 待财务核对
	 */
	public static int STATUS1 = 1;
	
	/**
	 * 核对未通过
	 */
	public static int STATUS2 = 2;
	
	/**
	 * 已完成
	 */
	public static int STATUS3 = 3;
	
	/**
	 * 已作废
	 */
	public static int STATUS4 = 4;
	
	
	public int id;        //ID
	
	/**
	 * 编号
	 */
	public String code;
	
	/**
	 * 结算类型
	 */
	public int balanceType;
	
	/**
	 * 结算时间
	 */
	public String balanceDatetime;
	
	/**
	 * 运费+单侧费+保险费
	 */
	public double carriageInsureBills;
	
	/**
	 * 折扣返回
	 */
	public float discountReturn;
	
	/**
	 * 折扣起始日
	 */
	public String discountDatetimeStart;
	
	/**
	 * 折扣结束日 
	 */
	public String discountDatetimeEnd;
	
	/**
	 * 结算确认用户ID
	 */
	public int confirmUserid;
	
	/**
	 * 财务核对用户ID
	 */
	public int auditingUserid;
	
	/**
	 * 状态
	 */
	public int status;
	
	/**
	 * 备注
	 */
	public String remark;
	
	/**
	 * 订单总额
	 */
	public double orderAmount;
	
	/**
	 * 妥投订单总额
	 */
	public double mailingOrderAmount;
	
	/**
	 * 退单订单总额
	 */
	public double untreadOrderAmount;
	
	/**
	 * 结算费
	 */
	public double balanceCharge;
	
	/**
	 * 妥投费
	 */
	public double mailingCharge;
	
	/**
	 * 退回费
	 */
	public double untreadCharge;
	
	/**
	 * 应收金额
	 */
	public double shouldCollection;
	
	/**
	 * 应付金额
	 */
	public double shouldPay;
	
	/**
	 * 支付方式
	 */
	public int payType;
	
	/**
	 * 实收金额
	 */
	public double actualCollection;
	
	public voUser confirmUser;
	
	public voUser auditingUser;
	
	public List packageList;//结算批次相关包裹单,MailingBatchPackage
	
	public int balanceArea;		// 结算地点：0-北库，1-芳村，2-广速，3-增城，4-无锡
	
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

	public int getBalanceType() {
		return balanceType;
	}

	public void setBalanceType(int balanceType) {
		this.balanceType = balanceType;
	}

	public String getBalanceDatetime() {
		return balanceDatetime;
	}

	public void setBalanceDatetime(String balanceDatetime) {
		this.balanceDatetime = balanceDatetime;
	}

	public double getCarriageInsureBills() {
		return carriageInsureBills;
	}

	public void setCarriageInsureBills(double carriageInsureBills) {
		this.carriageInsureBills = carriageInsureBills;
	}

	public float getDiscountReturn() {
		return discountReturn;
	}

	public void setDiscountReturn(float discountReturn) {
		this.discountReturn = discountReturn;
	}

	public String getDiscountDatetimeStart() {
		return discountDatetimeStart;
	}

	public void setDiscountDatetimeStart(String discountDatetimeStart) {
		this.discountDatetimeStart = discountDatetimeStart;
	}

	public String getDiscountDatetimeEnd() {
		return discountDatetimeEnd;
	}

	public void setDiscountDatetimeEnd(String discountDatetimeEnd) {
		this.discountDatetimeEnd = discountDatetimeEnd;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getConfirmUserid() {
		return confirmUserid;
	}

	public void setConfirmUserid(int confirmUserid) {
		this.confirmUserid = confirmUserid;
	}

	public int getAuditingUserid() {
		return auditingUserid;
	}

	public void setAuditingUserid(int auditingUserid) {
		this.auditingUserid = auditingUserid;
	}

	public double getBalanceCharge() {
		return balanceCharge;
	}

	public void setBalanceCharge(double balanceCharge) {
		this.balanceCharge = balanceCharge;
	}

	public double getMailingCharge() {
		return mailingCharge;
	}

	public void setMailingCharge(double mailingCharge) {
		this.mailingCharge = mailingCharge;
	}

	public double getUntreadCharge() {
		return untreadCharge;
	}

	public void setUntreadCharge(double untreadCharge) {
		this.untreadCharge = untreadCharge;
	}

	public double getShouldCollection() {
		return shouldCollection;
	}

	public void setShouldCollection(double shouldCollection) {
		this.shouldCollection = shouldCollection;
	}

	public double getShouldPay() {
		return shouldPay;
	}

	public void setShouldPay(double shouldPay) {
		this.shouldPay = shouldPay;
	}

	public double getActualCollection() {
		return actualCollection;
	}

	public void setActualCollection(double actualCollection) {
		this.actualCollection = actualCollection;
	}

	public voUser getConfirmUser() {
		return confirmUser;
	}

	public void setConfirmUser(voUser confirmUser) {
		this.confirmUser = confirmUser;
	}

	public voUser getAuditingUser() {
		return auditingUser;
	}

	public void setAuditingUser(voUser auditingUser) {
		this.auditingUser = auditingUser;
	}
	
	public double getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(double orderAmount) {
		this.orderAmount = orderAmount;
	}

	public double getMailingOrderAmount() {
		return mailingOrderAmount;
	}

	public void setMailingOrderAmount(double mailingOrderAmount) {
		this.mailingOrderAmount = mailingOrderAmount;
	}

	public double getUntreadOrderAmount() {
		return untreadOrderAmount;
	}

	public void setUntreadOrderAmount(double untreadOrderAmount) {
		this.untreadOrderAmount = untreadOrderAmount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStatusName() {
		String statusName = "";
		if(this.status == STATUS0){
			statusName = "未处理";
		} else if(this.status == STATUS1){
			statusName = "待财务核对";
		} else if(this.status == STATUS2){
			statusName = "核对未通过";
		} else if(this.status == STATUS3){
			statusName = "已完成";
		} else if(this.status == STATUS4){
			statusName = "已作废";
	}
		return statusName;
	}

	public List getPackageList() {
		return packageList;
	}

	public void setPackageList(List packageList) {
		this.packageList = packageList;
	}

	public int getPayType() {
		return payType;
	}

	public void setPayType(int payType) {
		this.payType = payType;
	}

	public int getBalanceArea() {
		return balanceArea;
	}

	public void setBalanceArea(int balanceArea) {
		this.balanceArea = balanceArea;
	}
	
	
}
