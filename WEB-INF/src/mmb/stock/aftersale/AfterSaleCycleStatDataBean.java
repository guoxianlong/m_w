package mmb.stock.aftersale;

/**
 * 功能：定时任务中用到的中间bean
 * @author lining
 *
 */
public class AfterSaleCycleStatDataBean {
	private int afterSaleDetectProductId;
	private String afterSaleDetectProductCode;
	private int afterSaleDetectProductType;
	private int afterSaleOrderId;
	private String afterSaleOrderCode;
	private String afterSaleCreateDatetime;
	private int afterSaleType;//1--补偿退款 2--漏发补货 3---已取消
	private int orderId;
	private String orderCode;
	public int getAfterSaleDetectProductId() {
		return afterSaleDetectProductId;
	}
	public void setAfterSaleDetectProductId(int afterSaleDetectProductId) {
		this.afterSaleDetectProductId = afterSaleDetectProductId;
	}
	public String getAfterSaleDetectProductCode() {
		return afterSaleDetectProductCode;
	}
	public void setAfterSaleDetectProductCode(String afterSaleDetectProductCode) {
		this.afterSaleDetectProductCode = afterSaleDetectProductCode;
	}
	public int getAfterSaleOrderId() {
		return afterSaleOrderId;
	}
	public void setAfterSaleOrderId(int afterSaleOrderId) {
		this.afterSaleOrderId = afterSaleOrderId;
	}
	public String getAfterSaleOrderCode() {
		return afterSaleOrderCode;
	}
	public void setAfterSaleOrderCode(String afterSaleOrderCode) {
		this.afterSaleOrderCode = afterSaleOrderCode;
	}
	public int getAfterSaleDetectProductType() {
		return afterSaleDetectProductType;
	}
	public void setAfterSaleDetectProductType(int afterSaleDetectProductType) {
		this.afterSaleDetectProductType = afterSaleDetectProductType;
	}
	public int getAfterSaleType() {
		return afterSaleType;
	}
	public void setAfterSaleType(int afterSaleType) {
		this.afterSaleType = afterSaleType;
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
	public String getAfterSaleCreateDatetime() {
		return afterSaleCreateDatetime;
	}
	public void setAfterSaleCreateDatetime(String afterSaleCreateDatetime) {
		this.afterSaleCreateDatetime = afterSaleCreateDatetime;
	}
	
}
