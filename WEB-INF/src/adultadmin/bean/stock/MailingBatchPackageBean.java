package adultadmin.bean.stock;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 物流自配送，邮包中的包裹
 * @author Administrator
 *
 */
public class MailingBatchPackageBean {
	public int id;
	public int mailingBatchId;//发货波次id
	public String mailingBatchCode;//发货波次编号
	public int mailingBatchParcelId;//邮包id
	public String mailingBatchParcelCode;//邮包编号
	public int orderId;//订单Id
	public String orderCode;//订单编号
	public String packageCode;//包裹单号
	public String createDatetime;//添加时间
	public int deliver;
	public String address;//收件地址
	public float totalPrice;//订单总价
	public float weight;//包裹重量
	public String stockInDatetime;//入库时间
	public int postStaffId;//投递员员工id
	public String postStaffName;//投递员员工姓名
	public int stockInAdminId;//接收人id
	public String stockInAdminName;//接收人姓名
	public int mailingStatus;//配送状态
	public int returnStatus;//反库状态
	public int balanceStatus;//结算状态
	public String assignTime;//用来存储该包裹最近一次分派的时间(包裹被分配给上一个投递员的一个小时之内不允许再分配给其他投递员)
	public int payType;//支付方式
	public int buyMode;//付款方式
	public int mailingBalanceAuditingId;//结算批次Id
	public MailingBatchBean mailingBatchBean;//发货波次
	public String orderType;//物流商品分类 
	public String addressTo;//二级收件地址
	public String store;//发货仓
	public String customerName;//客户姓名
	public String phone;//用户电话
	
	public String buyModeName;
	public String deliverName;
	public String totalPriceStr;
	public String orderPostCode;
	/**
	 * 配送状态：未入库
	 */
	public static final int MAILING_STATUS0 = 0;
	
	/**
	 * 配送状态：未分配（已入库）
	 */
	public static final int MAILING_STATUS1 = 1;
	
	/**
	 * 配送状态：投递中
	 */
	public static final int MAILING_STATUS2 = 2;
	
	/**
	 * 配送状态：已妥投
	 */
	public static final int MAILING_STATUS3 = 3;
	
	/**
	 * 配送状态：投递超时
	 */
	public static final int MAILING_STATUS4 = 4;
	
	/**
	 * 配送状态：返库中
	 */
	public static final int MAILING_STATUS5 = 5;
	
	/**
	 * 配送状态：已返库
	 */
	public static final int MAILING_STATUS6 = 6;
	
	/**
	 * 配送状态：结算中
	 */
	public static final int MAILING_STATUS7 = 7;
	
	/**
	 * 配送状态：已结算
	 */
	public static final int MAILING_STATUS8 = 8;
	
	/**
	 * 返库状态：未申请返库
	 */
	public static final int RETURN_STATUS0 = 0;
	
	/**
	 * 返库状态：返库中
	 */
	public static final int RETURN_STATUS1 = 1;
	
	/**
	 * 返库状态：已返库
	 */
	public static final int RETURN_STATUS2 = 2;
	
	/**
	 * 结算状态：未申请结算
	 */
	public static final int BALANCE_STATUS0 = 0;
	
	/**
	 * 结算状态：结算中
	 */
	public static final int BALANCE_STATUS1 = 1;
	
	/**
	 * 结算状态：已结算
	 */
	public static final int BALANCE_STATUS2 = 2;
	
	/**
	 * 结算状态：已废弃
	 */
	public static final int BALANCE_STATUS3 = 3;
	
	public static HashMap mailingStatusNameMap = new LinkedHashMap();//配送状态名
	public static HashMap returnStatusNameMap = new LinkedHashMap();//反库状态名
	public static HashMap balanceStatusNameMap = new LinkedHashMap();//结算状态名
	
	static{
		mailingStatusNameMap.put(Integer.valueOf(0), "未入库");
		mailingStatusNameMap.put(Integer.valueOf(1), "未分配");
		mailingStatusNameMap.put(Integer.valueOf(2), "投递中");
		mailingStatusNameMap.put(Integer.valueOf(3), "已妥投");
		mailingStatusNameMap.put(Integer.valueOf(4), "投递超时");
		mailingStatusNameMap.put(Integer.valueOf(5), "返库中");
		mailingStatusNameMap.put(Integer.valueOf(6), "已返库");
		mailingStatusNameMap.put(Integer.valueOf(7), "结算中");
		mailingStatusNameMap.put(Integer.valueOf(8), "已结算");
		
		returnStatusNameMap.put(Integer.valueOf(0), "未返库");
		returnStatusNameMap.put(Integer.valueOf(1), "返库中");
		returnStatusNameMap.put(Integer.valueOf(2), "已返库");
		
		balanceStatusNameMap.put(Integer.valueOf(0), "未申请结算");
		balanceStatusNameMap.put(Integer.valueOf(1), "结算中");
		balanceStatusNameMap.put(Integer.valueOf(2), "已结算");
		balanceStatusNameMap.put(Integer.valueOf(3), "已废弃");
	}
	
	
	


	public String getOrderPostCode() {
		return orderPostCode;
	}

	public void setOrderPostCode(String orderPostCode) {
		this.orderPostCode = orderPostCode;
	}

	public String getTotalPriceStr() {
		return totalPriceStr;
	}

	public void setTotalPriceStr(String totalPriceStr) {
		this.totalPriceStr = totalPriceStr;
	}

	public String getBuyModeName() {
		return buyModeName;
	}

	public void setBuyModeName(String buyModeName) {
		this.buyModeName = buyModeName;
	}

	public String getDeliverName() {
		return deliverName;
	}

	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
	}

	public String getMailingStatusName(int status){
		String statusName="";
		if(mailingStatusNameMap.containsKey(Integer.valueOf(status))){
			statusName=mailingStatusNameMap.get(Integer.valueOf(status)).toString();
		}
		return statusName;
	}
	
	public String getReturnStatusName(int status){
		String statusName="";
		if(returnStatusNameMap.containsKey(Integer.valueOf(status))){
			statusName=returnStatusNameMap.get(Integer.valueOf(status)).toString();
		}
		return statusName;
	}
	
	public String getBalanceStatusName(int status){
		String statusName="";
		if(balanceStatusNameMap.containsKey(Integer.valueOf(status))){
			statusName=balanceStatusNameMap.get(Integer.valueOf(status)).toString();
		}
		return statusName;
	}
	
	public MailingBatchBean getMailingBatchBean() {
		return mailingBatchBean;
	}
	public void setMailingBatchBean(MailingBatchBean mailingBatchBean) {
		this.mailingBatchBean = mailingBatchBean;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMailingBatchId() {
		return mailingBatchId;
	}
	public void setMailingBatchId(int mailingBatchId) {
		this.mailingBatchId = mailingBatchId;
	}
	public String getMailingBatchCode() {
		return mailingBatchCode;
	}
	public void setMailingBatchCode(String mailingBatchCode) {
		this.mailingBatchCode = mailingBatchCode;
	}
	public int getMailingBatchParcelId() {
		return mailingBatchParcelId;
	}
	public void setMailingBatchParcelId(int mailingBatchParcelId) {
		this.mailingBatchParcelId = mailingBatchParcelId;
	}
	public String getMailingBatchParcelCode() {
		return mailingBatchParcelCode;
	}
	public void setMailingBatchParcelCode(String mailingBatchParcelCode) {
		this.mailingBatchParcelCode = mailingBatchParcelCode;
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
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public float getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}
	public float getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
	public int getDeliver() {
		return deliver;
	}
	public void setDeliver(int deliver) {
		this.deliver = deliver;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public String getStockInDatetime() {
		return stockInDatetime;
	}
	public void setStockInDatetime(String stockInDatetime) {
		this.stockInDatetime = stockInDatetime;
	}
	public int getPostStaffId() {
		return postStaffId;
	}
	public void setPostStaffId(int postStaffId) {
		this.postStaffId = postStaffId;
	}
	public String getPostStaffName() {
		return postStaffName;
	}
	public void setPostStaffName(String postStaffName) {
		this.postStaffName = postStaffName;
	}
	public int getStockInAdminId() {
		return stockInAdminId;
	}
	public void setStockInAdminId(int stockInAdminId) {
		this.stockInAdminId = stockInAdminId;
	}
	public String getStockInAdminName() {
		return stockInAdminName;
	}
	public void setStockInAdminName(String stockInAdminName) {
		this.stockInAdminName = stockInAdminName;
	}
	public int getMailingStatus() {
		return mailingStatus;
	}
	public void setMailingStatus(int mailingStatus) {
		this.mailingStatus = mailingStatus;
	}
	public int getReturnStatus() {
		return returnStatus;
	}
	public void setReturnStatus(int returnStatus) {
		this.returnStatus = returnStatus;
	}
	public int getBalanceStatus() {
		return balanceStatus;
	}
	public void setBalanceStatus(int balanceStatus) {
		this.balanceStatus = balanceStatus;
	}
	public String getAssignTime() {
		return assignTime;
	}

	public void setAssignTime(String assignTime) {
		this.assignTime = assignTime;
	}
	public int getMailingBalanceAuditingId() {
		return mailingBalanceAuditingId;
	}

	public void setMailingBalanceAuditingId(int mailingBalanceAuditingId) {
		this.mailingBalanceAuditingId = mailingBalanceAuditingId;
	}

	public int getPayType() {
		return payType;
	}

	public void setPayType(int payType) {
		this.payType = payType;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getAddressTo() {
		return addressTo;
	}

	public void setAddressTo(String addressTo) {
		this.addressTo = addressTo;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getBuyMode() {
		return buyMode;
	}

	public void setBuyMode(int buyMode) {
		this.buyMode = buyMode;
	}
	
	
}
