/*
 * Created on 2007-6-28
 *
 */
package adultadmin.bean.buy;

import java.util.HashMap;
import java.util.List;

import adultadmin.action.vo.voUser;
import adultadmin.util.DateUtil;
/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2009-2-19
 * 
 * 说明：采购订单Bean
 */
public class BuyOrderBean {
	
	
	public static int BALANCE_MODE1=1;//显示代销模式
	public static int BALANCE_MODE2=2;//显示经销模式
	

	/**
	 * 状态：未处理
	 */
	public static int STATUS0 = 0;

	/**
	 * 状态：处理中
	 */
	public static int STATUS1 = 1;

	/**
	 * 状态：已计划未审核
	 */
	public static int STATUS2 = 2;

	/**
	 * 状态：已审核
	 */
	public static int STATUS3 = 3;


	/**
	 * 状态：审核未通过
	 */
	public static int STATUS4 = 4;

	/**
	 * 状态：已指派
	 */
	public static int STATUS5 = 5;
	
	/**
	 * 状态：采购已完成
	 */
	public static int STATUS6 = 6;
	
	/**
	 * 状态：申请完成
	 */
	public static int STATUS7 = 7;
	
	/**
	 * 状态：已删除
	 */
	public static int STATUS8 = 8;
	
	/**
	 * 打款状态：未打款
	 */
	public static int PAY_STATUS0 = 0;
	
	/**
	 * 打款状态：与预计总金额不相等
	 */
	public static int PAY_STATUS1 = 1;

	/**
	 * 打款状态：与预计总金额相等
	 */
	public static int PAY_STATUS2 = 2;
	
	/**
	 * 打款状态：打款已完成
	 */
	public static int PAY_STATUS3 = 3;
	
	/**
	 * 打款方式：有预付款
	 */
	public static int PAY_MODE0 = 0;
	
	/**
	 * 打款方式：无预付款
	 */
	public static int PAY_MODE1 = 1;
	
	/**
	 * 打款方式：无
	 */
	public static int PAY_MODE2 = 2;
	
	public int id;           //ID

	public int buyPlanId;    //所属采购计划ID
	
	public String name;      //名称
	
	public String code;      //编号

	public int status;       //状态	
	
	public int payStatus;    //打款状态
	
	public int payMode;      //打款方式

	public String createDatetime;  //添加时间

	public String remark;          //备注
	
	public String remark2;         //强制打款完成备注

	public String deadline;        //最后时间期限

	public String confirmDatetime; //确认时间
	
	public double money;            //已打款
	
	public double lockMoney;        //付款锁定金额
	
	public double deposit;          //订金
	
	public double lock_deposit;	   //订金锁定值
	
	public float portage;          //预计运费

	public int printCount;         //打印次数
	
	public int transformCount;      //转换次数
	
	public String productType;     //产品线
	
	public String proxyName;       //代理商名称
	
	public double totalPurchasePrice; //预计订单总额
	
	public double totalStockinPrice;  //总入库金额
	
	public int proxyId;            //代理商ID
	
	public String payUser;         //付款人
	
	public double taxPoint;         //税点

	public List<BuyOrderProductBean> buyOrderProductList;  //产品List

	public int buyStockCount;      //采购订单所包含的入库单数量

	public int createUserId;       //添加次采购订单用户ID
	public int auditingUserId;     //审核用户ID
	public int assignUserId;       //指派用户ID
	
	public voUser creatUser;        //添加计划用户
	public voUser auditingUser;     //审核用户
	public int balanceMode;         //结算模式
	public int ticket;            // 是否有票 0有票，1无票
	public int completeStatus;	//是否到货完成
	
	public int getCompleteStatus() {
		return completeStatus;
	}

	public void setCompleteStatus(int completeStatus) {
		this.completeStatus = completeStatus;
	}

	public int getTicket() {
		return ticket;
	}

	public void setTicket(int ticket) {
		this.ticket = ticket;
	}

	public int getBalanceMode() {
		return balanceMode;
	}

	public void setBalanceMode(int balanceMode) {
		this.balanceMode = balanceMode;
	}

	public static HashMap payModeMap = new HashMap();      //打款方式
	
	static{
		payModeMap.put(Integer.valueOf(PAY_MODE0), "有预付款");
		payModeMap.put(Integer.valueOf(PAY_MODE1), "无预付款");
		payModeMap.put(Integer.valueOf(PAY_MODE2), "无");
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public int getPrintCount() {
		return printCount;
	}

	public void setPrintCount(int printCount) {
		this.printCount = printCount;
	}

	public int getBuyStockCount() {
		return buyStockCount;
	}

	public void setBuyStockCount(int buyStockCount) {
		this.buyStockCount = buyStockCount;
	}

	public boolean isOvertime(){
		long deadline = DateUtil.parseDate(this.deadline).getTime();
		long now = DateUtil.getNowDate().getTime();
		return (deadline < now);
	}

	public int getAssignUserId() {
		return assignUserId;
	}

	public void setAssignUserId(int assignUserId) {
		this.assignUserId = assignUserId;
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

	public int getBuyPlanId() {
		return buyPlanId;
	}

	public void setBuyPlanId(int buyPlanId) {
		this.buyPlanId = buyPlanId;
	}

	public String getStatusName() {
		String statusName = "";
		if(this.status == STATUS0){
			statusName = "未处理";
		} else if(this.status == STATUS1){
			statusName = "处理中";
		} else if(this.status == STATUS2){
			statusName = "已订购未审核";
		} else if(this.status == STATUS3){
			statusName = "已审核";
//			if(this.buyStockCount <= 0){
//				long deadline = DateUtil.parseDate(this.deadline).getTime();
//				long now = DateUtil.getNowDate().getTime();
//				if(deadline < now){
//					statusName = "处理进货";
//				}
//			}
		} else if(this.status == STATUS4){
			statusName = "审核未通过";
		} else if(this.status == STATUS5){
			statusName = "已指派";
		} else if(this.status == STATUS6){
			statusName = "采购已完成";
		} else if(this.status == STATUS7){
			statusName = "申请完成";
		}
		
		return statusName;
	}
	
	public static String getStatusName(int status) {
		String statusName = "";
		if(status == STATUS0){
			statusName = "未处理";
		} else if(status == STATUS1){
			statusName = "处理中";
		} else if(status == STATUS2){
			statusName = "已订购未审核";
		} else if(status == STATUS3){
			statusName = "已审核";
//			if(this.buyStockCount <= 0){
//				long deadline = DateUtil.parseDate(this.deadline).getTime();
//				long now = DateUtil.getNowDate().getTime();
//				if(deadline < now){
//					statusName = "处理进货";
//				}
//			}
		} else if(status == STATUS4){
			statusName = "审核未通过";
		} else if(status == STATUS5){
			statusName = "已指派";
		} else if(status == STATUS6){
			statusName = "采购已完成";
		} else if(status == STATUS7){
			statusName = "申请完成";
		}
		
		return statusName;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public voUser getCreatUser() {
		return creatUser;
	}

	public void setCreatUser(voUser creatUser) {
		this.creatUser = creatUser;
	}

	public voUser getAuditingUser() {
		return auditingUser;
	}

	public void setAuditingUser(voUser auditingUser) {
		this.auditingUser = auditingUser;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getTotalPurchasePrice() {
		return totalPurchasePrice;
	}

	public void setTotalPurchasePrice(double totalPurchasePrice) {
		this.totalPurchasePrice = totalPurchasePrice;
	}

	public float getPortage() {
		return portage;
	}

	public void setPortage(float portage) {
		this.portage = portage;
	}

	public int getTransformCount() {
		return transformCount;
	}

	public void setTransformCount(int transformCount) {
		this.transformCount = transformCount;
	}

	public String getProxyName() {
		return proxyName;
	}

	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}

	public int getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(int payStatus) {
		this.payStatus = payStatus;
	}

	public int getProxyId() {
		return proxyId;
	}

	public void setProxyId(int proxyId) {
		this.proxyId = proxyId;
	}

	public String getPayUser() {
		return payUser;
	}

	public void setPayUser(String payUser) {
		this.payUser = payUser;
	}

	public double getTaxPoint() {
		return taxPoint;
	}

	public void setTaxPoint(double taxPoint) {
		this.taxPoint = taxPoint;
	}

	public List<BuyOrderProductBean> getBuyOrderProductList() {
		return buyOrderProductList;
	}

	public void setBuyOrderProductList(List<BuyOrderProductBean> buyOrderProductList) {
		this.buyOrderProductList = buyOrderProductList;
	}

	public int getPayMode() {
		return payMode;
	}

	public void setPayMode(int payMode) {
		this.payMode = payMode;
	}

	public double getDeposit() {
		return deposit;
	}

	public void setDeposit(double deposit) {
		this.deposit = deposit;
	}

	public double getLock_deposit() {
		return lock_deposit;
	}

	public void setLock_deposit(double lock_deposit) {
		this.lock_deposit = lock_deposit;
	}

	public double getLockMoney() {
		return lockMoney;
	}

	public void setLockMoney(double lockMoney) {
		this.lockMoney = lockMoney;
	}

	public String getRemark2() {
		return remark2;
	}

	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}

	public double getTotalStockinPrice() {
		return totalStockinPrice;
	}

	public void setTotalStockinPrice(double totalStockinPrice) {
		this.totalStockinPrice = totalStockinPrice;
	}
	
}
