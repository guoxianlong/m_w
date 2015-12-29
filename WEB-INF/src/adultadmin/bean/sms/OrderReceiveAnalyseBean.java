package adultadmin.bean.sms;

/**
 * 作者：赵林
 * 
 * 说明：短信回复内容分析
 *
 */
public class OrderReceiveAnalyseBean {

	public String firstReceiveContent;    //订单第一次回复内容
	
	public int orderCount;             //订单数量
	
	public int orderDealedCount;      //订单成交数量
	
	public float orderDealRate;     //订单成交率

	public String getFirstReceiveContent() {
		return firstReceiveContent;
	}

	public void setFirstReceiveContent(String firstReceiveContent) {
		this.firstReceiveContent = firstReceiveContent;
	}

	public int getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}

	public int getOrderDealedCount() {
		return orderDealedCount;
	}

	public void setOrderDealedCount(int orderDealedCount) {
		this.orderDealedCount = orderDealedCount;
	}

	public float getOrderDealRate() {
		return orderDealRate;
	}

	public void setOrderDealRate(float orderDealRate) {
		this.orderDealRate = orderDealRate;
	}
	
	
}
