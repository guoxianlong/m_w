package adultadmin.bean.order;

/**
 * 在查询亚马逊订单状态时用到的暂存bean
 * @author 郝亚斌
 *
 */
public class AmazonOrderStatusDeal {

	private int orderId;
	
	private int id;
	
	private String amazonOrdreCode;
	
	private String orderStatusCode;

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAmazonOrdreCode() {
		return amazonOrdreCode;
	}

	public void setAmazonOrdreCode(String amazonOrdreCode) {
		this.amazonOrdreCode = amazonOrdreCode;
	}

	public String getOrderStatusCode() {
		return orderStatusCode;
	}

	public void setOrderStatusCode(String orderStatusCode) {
		this.orderStatusCode = orderStatusCode;
	}
	
}
