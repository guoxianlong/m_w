package mmb.stock.aftersale;

/**
 * 功能：售后耗时记录
 * @author lining
 *
 */
public class AfterSaleCycleStatBean {
	public int id;
	public int afterSaleOrderId;
	public String afterSaleOrderCode;
	public int afterSaleDetectProductId;
	public String afterSaleDetectProductCode;
	public int afterSaleType;
	public String afterSaleOrderCreateDatetime;//售后单的创建时间
	public String createDatetime;
	//记录的是耗时秒数
	public long customerReturnConsuming;//用户寄回耗时
	public long preAfterSaleConsuming;//售后前期耗时
	public long customerConfirmConsuming;//与客户确认耗时
	public long confirmCostsConsuming;//重新申请确认费用耗时
	public long sApplyDeliveryConsuming;//s单申请发货耗时
	public long qualitySupportConsuming;//质检支撑耗时
	public long matchPackageConsuming;//匹配包裹耗时
	public long detectConsuming;//检测耗时
	public long enterAfterSaleStockConsuming;//入售后库耗时
	public long afterSaleShippingConsuming;//售后发货耗时
	public long backSupplierConsuming;//返厂耗时
	public long financialRefundConsuming;//财务退款耗时
	public long repairsConsuming;//维修耗时
	public long customerPayMoneyConsuming;//用户打款耗时
	public long sShippingConsuming;//s单发货耗时
	public long totalConsuming;//总耗时
	
	/**
	 * 退货
	 */
	public static int AFTER_SALE_TYPE_RETURN=1;
	/**
	 * 换货
	 */
	public static int AFTER_SALE_TYPE_REPLACE=2;
	/**
	 * 维修
	 */
	public static int AFTER_SALE_TYPE_REPAIR=3;
	/**
	 * 原品返回
	 */
	public static int AFTER_SALE_TYPE_ORIGINAL=4;
	/**
	 * 补偿退款
	 */
	public static int AFTER_SALE_TYPE_COMPENSATION_REFUND=5;
	/**
	 * 漏发补发
	 */
	public static int AFTER_SALE_TYPE_REISSUE=6;
	/**
	 * 已取消
	 */
	public static int AFTER_SALE_TYPE_CANCLE=7;
	
	//用于显示耗时的时间
	private String customerReturnConsumingStr;
	private String preAfterSaleConsumingStr;
	private String customerConfirmConsumingStr;
	private String confirmCostsConsumingStr;
	private String sApplyDeliveryConsumingStr;
	private String qualitySupportConsumingStr;
	private String matchPackageConsumingStr;
	private String detectConsumingStr;
	private String enterAfterSaleStockConsumingStr;
	private String afterSaleShippingConsumingStr;
	private String backSupplierConsumingStr;
	private String financialRefundConsumingStr;
	private String repairsConsumingStr;
	private String customerPayMoneyConsumingStr;
	private String sShippingConsumingStr;
	private String afterSaleAllocationIntactConsumingStr;
	private String afterSaleAllocationBadConsumingStr;
	private String totalConsumingStr;
	
	
	public int getAfterSaleOrderId() {
		return afterSaleOrderId;
	}
	public void setAfterSaleOrderId(int afterSaleOrderId) {
		this.afterSaleOrderId = afterSaleOrderId;
	}
	public int getAfterSaleDetectProductId() {
		return afterSaleDetectProductId;
	}
	public void setAfterSaleDetectProductId(int afterSaleDetectProductId) {
		this.afterSaleDetectProductId = afterSaleDetectProductId;
	}
	public long getTotalConsuming() {
		return totalConsuming;
	}
	public void setTotalConsuming(long totalConsuming) {
		this.totalConsuming = totalConsuming;
	}
	public String getTotalConsumingStr() {
		return totalConsumingStr;
	}
	public void setTotalConsumingStr(String totalConsumingStr) {
		this.totalConsumingStr = totalConsumingStr;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAfterSaleOrderCreateDatetime() {
		return afterSaleOrderCreateDatetime;
	}
	public void setAfterSaleOrderCreateDatetime(String afterSaleOrderCreateDatetime) {
		this.afterSaleOrderCreateDatetime = afterSaleOrderCreateDatetime;
	}
	public String getAfterSaleOrderCode() {
		return afterSaleOrderCode;
	}
	public void setAfterSaleOrderCode(String afterSaleOrderCode) {
		this.afterSaleOrderCode = afterSaleOrderCode;
	}
	public String getAfterSaleDetectProductCode() {
		return afterSaleDetectProductCode;
	}
	public void setAfterSaleDetectProductCode(String afterSaleDetectProductCode) {
		this.afterSaleDetectProductCode = afterSaleDetectProductCode;
	}
	public int getAfterSaleType() {
		return afterSaleType;
	}
	public void setAfterSaleType(int afterSaleType) {
		this.afterSaleType = afterSaleType;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public long getCustomerReturnConsuming() {
		return customerReturnConsuming;
	}
	public void setCustomerReturnConsuming(long customerReturnConsuming) {
		this.customerReturnConsuming = customerReturnConsuming;
	}
	public long getPreAfterSaleConsuming() {
		return preAfterSaleConsuming;
	}
	public void setPreAfterSaleConsuming(long preAfterSaleConsuming) {
		this.preAfterSaleConsuming = preAfterSaleConsuming;
	}

	public long getCustomerConfirmConsuming() {
		return customerConfirmConsuming;
	}
	public void setCustomerConfirmConsuming(long customerConfirmConsuming) {
		this.customerConfirmConsuming = customerConfirmConsuming;
	}
	public long getConfirmCostsConsuming() {
		return confirmCostsConsuming;
	}
	public void setConfirmCostsConsuming(long confirmCostsConsuming) {
		this.confirmCostsConsuming = confirmCostsConsuming;
	}
	public long getsApplyDeliveryConsuming() {
		return sApplyDeliveryConsuming;
	}
	public void setsApplyDeliveryConsuming(long sApplyDeliveryConsuming) {
		this.sApplyDeliveryConsuming = sApplyDeliveryConsuming;
	}
	public long getQualitySupportConsuming() {
		return qualitySupportConsuming;
	}
	public void setQualitySupportConsuming(long qualitySupportConsuming) {
		this.qualitySupportConsuming = qualitySupportConsuming;
	}
	public long getMatchPackageConsuming() {
		return matchPackageConsuming;
	}
	public void setMatchPackageConsuming(long matchPackageConsuming) {
		this.matchPackageConsuming = matchPackageConsuming;
	}
	public long getDetectConsuming() {
		return detectConsuming;
	}
	public void setDetectConsuming(long detectConsuming) {
		this.detectConsuming = detectConsuming;
	}
	public long getEnterAfterSaleStockConsuming() {
		return enterAfterSaleStockConsuming;
	}
	public void setEnterAfterSaleStockConsuming(long enterAfterSaleStockConsuming) {
		this.enterAfterSaleStockConsuming = enterAfterSaleStockConsuming;
	}
	public long getAfterSaleShippingConsuming() {
		return afterSaleShippingConsuming;
	}
	public void setAfterSaleShippingConsuming(long afterSaleShippingConsuming) {
		this.afterSaleShippingConsuming = afterSaleShippingConsuming;
	}
	public long getBackSupplierConsuming() {
		return backSupplierConsuming;
	}
	public void setBackSupplierConsuming(long backSupplierConsuming) {
		this.backSupplierConsuming = backSupplierConsuming;
	}
	public long getFinancialRefundConsuming() {
		return financialRefundConsuming;
	}
	public void setFinancialRefundConsuming(long financialRefundConsuming) {
		this.financialRefundConsuming = financialRefundConsuming;
	}
	public long getRepairsConsuming() {
		return repairsConsuming;
	}
	public void setRepairsConsuming(long repairsConsuming) {
		this.repairsConsuming = repairsConsuming;
	}
	public long getCustomerPayMoneyConsuming() {
		return customerPayMoneyConsuming;
	}
	public void setCustomerPayMoneyConsuming(long customerPayMoneyConsuming) {
		this.customerPayMoneyConsuming = customerPayMoneyConsuming;
	}
	public long getsShippingConsuming() {
		return sShippingConsuming;
	}
	public void setsShippingConsuming(long sShippingConsuming) {
		this.sShippingConsuming = sShippingConsuming;
	}
	public String getCustomerReturnConsumingStr() {
		return customerReturnConsumingStr;
	}
	public void setCustomerReturnConsumingStr(String customerReturnConsumingStr) {
		this.customerReturnConsumingStr = customerReturnConsumingStr;
	}
	public String getPreAfterSaleConsumingStr() {
		return preAfterSaleConsumingStr;
	}
	public void setPreAfterSaleConsumingStr(String preAfterSaleConsumingStr) {
		this.preAfterSaleConsumingStr = preAfterSaleConsumingStr;
	}
	public String getCustomerConfirmConsumingStr() {
		return customerConfirmConsumingStr;
	}
	public void setCustomerConfirmConsumingStr(String customerConfirmConsumingStr) {
		this.customerConfirmConsumingStr = customerConfirmConsumingStr;
	}
	public String getConfirmCostsConsumingStr() {
		return confirmCostsConsumingStr;
	}
	public void setConfirmCostsConsumingStr(String confirmCostsConsumingStr) {
		this.confirmCostsConsumingStr = confirmCostsConsumingStr;
	}
	public String getsApplyDeliveryConsumingStr() {
		return sApplyDeliveryConsumingStr;
	}
	public void setsApplyDeliveryConsumingStr(String sApplyDeliveryConsumingStr) {
		this.sApplyDeliveryConsumingStr = sApplyDeliveryConsumingStr;
	}
	public String getQualitySupportConsumingStr() {
		return qualitySupportConsumingStr;
	}
	public void setQualitySupportConsumingStr(String qualitySupportConsumingStr) {
		this.qualitySupportConsumingStr = qualitySupportConsumingStr;
	}
	public String getMatchPackageConsumingStr() {
		return matchPackageConsumingStr;
	}
	public void setMatchPackageConsumingStr(String matchPackageConsumingStr) {
		this.matchPackageConsumingStr = matchPackageConsumingStr;
	}
	public String getDetectConsumingStr() {
		return detectConsumingStr;
	}
	public void setDetectConsumingStr(String detectConsumingStr) {
		this.detectConsumingStr = detectConsumingStr;
	}
	public String getEnterAfterSaleStockConsumingStr() {
		return enterAfterSaleStockConsumingStr;
	}
	public void setEnterAfterSaleStockConsumingStr(
			String enterAfterSaleStockConsumingStr) {
		this.enterAfterSaleStockConsumingStr = enterAfterSaleStockConsumingStr;
	}
	public String getAfterSaleShippingConsumingStr() {
		return afterSaleShippingConsumingStr;
	}
	public void setAfterSaleShippingConsumingStr(
			String afterSaleShippingConsumingStr) {
		this.afterSaleShippingConsumingStr = afterSaleShippingConsumingStr;
	}
	public String getBackSupplierConsumingStr() {
		return backSupplierConsumingStr;
	}
	public void setBackSupplierConsumingStr(String backSupplierConsumingStr) {
		this.backSupplierConsumingStr = backSupplierConsumingStr;
	}
	public String getFinancialRefundConsumingStr() {
		return financialRefundConsumingStr;
	}
	public void setFinancialRefundConsumingStr(String financialRefundConsumingStr) {
		this.financialRefundConsumingStr = financialRefundConsumingStr;
	}
	public String getRepairsConsumingStr() {
		return repairsConsumingStr;
	}
	public void setRepairsConsumingStr(String repairsConsumingStr) {
		this.repairsConsumingStr = repairsConsumingStr;
	}
	public String getCustomerPayMoneyConsumingStr() {
		return customerPayMoneyConsumingStr;
	}
	public void setCustomerPayMoneyConsumingStr(String customerPayMoneyConsumingStr) {
		this.customerPayMoneyConsumingStr = customerPayMoneyConsumingStr;
	}
	public String getsShippingConsumingStr() {
		return sShippingConsumingStr;
	}
	public void setsShippingConsumingStr(String sShippingConsumingStr) {
		this.sShippingConsumingStr = sShippingConsumingStr;
	}
	public String getAfterSaleAllocationIntactConsumingStr() {
		return afterSaleAllocationIntactConsumingStr;
	}
	public void setAfterSaleAllocationIntactConsumingStr(
			String afterSaleAllocationIntactConsumingStr) {
		this.afterSaleAllocationIntactConsumingStr = afterSaleAllocationIntactConsumingStr;
	}
	public String getAfterSaleAllocationBadConsumingStr() {
		return afterSaleAllocationBadConsumingStr;
	}
	public void setAfterSaleAllocationBadConsumingStr(
			String afterSaleAllocationBadConsumingStr) {
		this.afterSaleAllocationBadConsumingStr = afterSaleAllocationBadConsumingStr;
	}
	
}
