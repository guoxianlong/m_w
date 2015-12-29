package mmb.finance.balance;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * 说明：用于异常结算数据的记录
 * 
 */
public class FinanceMailingBalanceBean {
	public int id; 							// id
	public int orderId;						// 订单id
	public String orderCode;				// 订单code
	public String stockoutDatetime;			// 订单发货时间
	public String packageNum;				// 包裹单号
	public float orderPrice;				// 订单金额
	public float carriage;					// 运费
	public float mailingCharge;				// 妥投费
	public float untreadCharge;				// 退回费
	public float balanceCharge;				// 结算费
	public float insureCharge;				// 保险费
	public float billsCharge;				// 单册费
	public float insurePriceCharge;			// 保价费
	public int balanceType;					// 结算来源
	public String balanceDate;				// 结算日期
	public int balanceStatus;				// 结算状态
	public int balanceCheck;				// 是否复核
	public int mailingBalanceAuditingId;	// 结算确认ID

	public int importType;					// 导入数据类型
	public int buyMode;						// 购买类型
	public String remark;					// 异常单备注
	public int balanceCause;				// 异常单结算源因
	public int dataType;					// 数据类型 ：0正常，1废弃，2红冲，3异常
	public String rollbackMSG;				// 红冲详细信息，，红冲改变了哪些数据
	public String rollbackUser;				// 红冲操作人员账号
	public String rollbackRemark;			// 红冲备注
	public String createDateTime;			// 添加时间
	public float agencyPrice;				// 代收货款
	public int balanceArea;					// 结算地点：0-北库，1-芳村，2-广速，3-增城，4-无锡

	public static Map balanceCauseMap = new LinkedHashMap();	// 异常单结算源因
	
	static{
		balanceCauseMap.put("1", "同一订单重复发货");
		balanceCauseMap.put("2", "订单已取消发货，但包裹已寄出");
		balanceCauseMap.put("3", "订单的代收货款或结算费用补缴或退还");
		balanceCauseMap.put("4", "非我司单");
	}
	
	public float getAgencyPrice() {
		return agencyPrice;
	}

	public void setAgencyPrice(float agencyPrice) {
		this.agencyPrice = agencyPrice;
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

	public String getStockoutDatetime() {
		return stockoutDatetime;
	}

	public void setStockoutDatetime(String stockoutDatetime) {
		this.stockoutDatetime = stockoutDatetime;
	}

	public String getPackageNum() {
		return packageNum;
	}

	public void setPackageNum(String packageNum) {
		this.packageNum = packageNum;
	}

	public float getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(float orderPrice) {
		this.orderPrice = orderPrice;
	}

	public float getCarriage() {
		return carriage;
	}

	public void setCarriage(float carriage) {
		this.carriage = carriage;
	}

	public float getMailingCharge() {
		return mailingCharge;
	}

	public void setMailingCharge(float mailingCharge) {
		this.mailingCharge = mailingCharge;
	}

	public float getUntreadCharge() {
		return untreadCharge;
	}

	public void setUntreadCharge(float untreadCharge) {
		this.untreadCharge = untreadCharge;
	}

	public float getBalanceCharge() {
		return balanceCharge;
	}

	public void setBalanceCharge(float balanceCharge) {
		this.balanceCharge = balanceCharge;
	}

	public float getInsureCharge() {
		return insureCharge;
	}

	public void setInsureCharge(float insureCharge) {
		this.insureCharge = insureCharge;
	}

	public float getBillsCharge() {
		return billsCharge;
	}

	public void setBillsCharge(float billsCharge) {
		this.billsCharge = billsCharge;
	}
	
	public float getInsurePriceCharge() {
		return insurePriceCharge;
	}
	
	public void setInsurePriceCharge(float insurePriceCharge) {
		this.insurePriceCharge = insurePriceCharge;
	}

	public int getBalanceType() {
		return balanceType;
	}

	public void setBalanceType(int balanceType) {
		this.balanceType = balanceType;
	}

	public String getBalanceDate() {
		return balanceDate;
	}

	public void setBalanceDate(String balanceDate) {
		this.balanceDate = balanceDate;
	}

	public int getBalanceStatus() {
		return balanceStatus;
	}

	public int getBalanceCheck() {
		return balanceCheck;
	}

	public void setBalanceCheck(int balanceCheck) {
		this.balanceCheck = balanceCheck;
	}

	public int getMailingBalanceAuditingId() {
		return mailingBalanceAuditingId;
	}

	public void setBalanceStatus(int balanceStatus) {
		this.balanceStatus = balanceStatus;
	}

	public int getMailing_balance_auditing_id() {
		return mailingBalanceAuditingId;
	}

	public void setMailingBalanceAuditingId(int mailingBalanceAuditingId) {
		this.mailingBalanceAuditingId = mailingBalanceAuditingId;
	}

	public int getImportType() {
		return importType;
	}

	public void setImportType(int importType) {
		this.importType = importType;
	}

	public int getBuyMode() {
		return buyMode;
	}

	public void setBuyMode(int buyMode) {
		this.buyMode = buyMode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getBalanceCause() {
		return balanceCause;
	}

	public void setBalanceCause(int balanceCause) {
		this.balanceCause = balanceCause;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public String getRollbackMSG() {
		return rollbackMSG;
	}

	public void setRollbackMSG(String rollbackMSG) {
		this.rollbackMSG = rollbackMSG;
	}

	public String getRollbackUser() {
		return rollbackUser;
	}

	public void setRollbackUser(String rollbackUser) {
		this.rollbackUser = rollbackUser;
	}

	public String getRollbackRemark() {
		return rollbackRemark;
	}

	public void setRollbackRemark(String rollbackRemark) {
		this.rollbackRemark = rollbackRemark;
	}

	public String getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(String createDateTime) {
		this.createDateTime = createDateTime;
	}

	public int getBalanceArea() {
		return balanceArea;
	}

	public void setBalanceArea(int balanceArea) {
		this.balanceArea = balanceArea;
	}

	public static String getBalanceCauseText(int balanceCause){
		String text = "";
		if(balanceCauseMap.get(String.valueOf(balanceCause)) != null){
			text = balanceCauseMap.get(String.valueOf(balanceCause)).toString();
		}
		return text;
	}
}
