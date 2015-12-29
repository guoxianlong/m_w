package mmb.stock.stat;

/**
 *	用于发货成功率统计 
 *
 */
public class OrderStockTimelyBean {
	public int id;							//ID
	public String date;						//日期
	public int orderId;						//订单id
	public String orderCode;				//订单编号
	public String firstOrderStockDatetime;	//最后一次申请出库时间
	public int firstOrderStockUserId;		//最后一次申请出库人Id
	public String firstOrderStockUserName;	//最后一次申请出库人姓名
	public String stockOutDatetime;			//复核出库时间
	public int stockOutUserId;				//复核出库人id
	public String stockOutUserName;			//复核出库人姓名
	public int orderStockCount;				//申请出库次数
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
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
	public String getFirstOrderStockDatetime() {
		return firstOrderStockDatetime;
	}
	public void setFirstOrderStockDatetime(String firstOrderStockDatetime) {
		this.firstOrderStockDatetime = firstOrderStockDatetime;
	}
	public int getFirstOrderStockUserId() {
		return firstOrderStockUserId;
	}
	public void setFirstOrderStockUserId(int firstOrderStockUserId) {
		this.firstOrderStockUserId = firstOrderStockUserId;
	}
	public String getFirstOrderStockUserName() {
		return firstOrderStockUserName;
	}
	public void setFirstOrderStockUserName(String firstOrderStockUserName) {
		this.firstOrderStockUserName = firstOrderStockUserName;
	}
	public String getStockOutDatetime() {
		return stockOutDatetime;
	}
	public void setStockOutDatetime(String stockOutDatetime) {
		this.stockOutDatetime = stockOutDatetime;
	}
	public int getStockOutUserId() {
		return stockOutUserId;
	}
	public void setStockOutUserId(int stockOutUserId) {
		this.stockOutUserId = stockOutUserId;
	}
	public String getStockOutUserName() {
		return stockOutUserName;
	}
	public void setStockOutUserName(String stockOutUserName) {
		this.stockOutUserName = stockOutUserName;
	}
	public int getOrderStockCount() {
		return orderStockCount;
	}
	public void setOrderStockCount(int orderStockCount) {
		this.orderStockCount = orderStockCount;
	}
	
	
}
