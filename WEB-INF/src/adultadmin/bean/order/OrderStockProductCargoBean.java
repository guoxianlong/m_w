package adultadmin.bean.order;

/**
 * 说明：订单出库商品货位信息
 * 
 * 创建时间：2011-04-21
 *
 */
public class OrderStockProductCargoBean {

	public int id;    //ID
	public int orderStockId;  //订单出库ID
	public int orderStockProductId;   //订单出库商品ID
	public int count;         //出库数量
	public int cargoProductStockId;  //货位库存ID
	public String cargoWholeCode;    //货位号
	
	public OrderStockProductBean orderStockProductBean;  // 对应的order_stock_product表中的信息
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOrderStockId() {
		return orderStockId;
	}
	public void setOrderStockId(int orderStockId) {
		this.orderStockId = orderStockId;
	}
	public int getOrderStockProductId() {
		return orderStockProductId;
	}
	public void setOrderStockProductId(int orderStockProductId) {
		this.orderStockProductId = orderStockProductId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getCargoProductStockId() {
		return cargoProductStockId;
	}
	public void setCargoProductStockId(int cargoProductStockId) {
		this.cargoProductStockId = cargoProductStockId;
	}
	public String getCargoWholeCode() {
		return cargoWholeCode;
	}
	public void setCargoWholeCode(String cargoWholeCode) {
		this.cargoWholeCode = cargoWholeCode;
	}
	public OrderStockProductBean getOrderStockProductBean() {
		return orderStockProductBean;
	}
	public void setOrderStockProductBean(OrderStockProductBean orderStockProductBean) {
		this.orderStockProductBean = orderStockProductBean;
	}
	
}
