package adultadmin.bean.order;

public class EachProductLineBean {
	
	private String productLineName;//产品线名称
	private int productLineId; //产品线id
	
	private int shipped; //商品数量 发货
	private float shippedSale; //销售金额
	private float shippedCost; //成本金额
	
	private int dealOrder; //商品数量 成交
	private float dealOrderSale; //销售金额
	private float dealOrderCost; //成本金额
	
	private int backOrder; //商品数量 退单
	private float backOrderSale; //销售金额
	private float backOrderCost; //成本金额
	
	public String getProductLineName() {
		return productLineName;
	}

	public void setProductLineName(String productLineName) {
		this.productLineName = productLineName;
	}

	public int getProductLineId() {
		return productLineId;
	}

	public void setProductLineId(int productLineId) {
		this.productLineId = productLineId;
	}

	public int getShipped() {
		return shipped;
	}

	public void setShipped(int shipped) {
		this.shipped = shipped;
	}

	public float getShippedSale() {
		return shippedSale;
	}

	public void setShippedSale(float shippedSale) {
		this.shippedSale = shippedSale;
	}

	public float getShippedCost() {
		return shippedCost;
	}

	public void setShippedCost(float shippedCost) {
		this.shippedCost = shippedCost;
	}

	public int getDealOrder() {
		return dealOrder;
	}

	public void setDealOrder(int dealOrder) {
		this.dealOrder = dealOrder;
	}

	public float getDealOrderSale() {
		return dealOrderSale;
	}

	public void setDealOrderSale(float dealOrderSale) {
		this.dealOrderSale = dealOrderSale;
	}

	public float getDealOrderCost() {
		return dealOrderCost;
	}

	public void setDealOrderCost(float dealOrderCost) {
		this.dealOrderCost = dealOrderCost;
	}

	public int getBackOrder() {
		return backOrder;
	}

	public void setBackOrder(int backOrder) {
		this.backOrder = backOrder;
	}

	public float getBackOrderSale() {
		return backOrderSale;
	}

	public void setBackOrderSale(float backOrderSale) {
		this.backOrderSale = backOrderSale;
	}

	public float getBackOrderCost() {
		return backOrderCost;
	}

	public void setBackOrderCost(float backOrderCost) {
		this.backOrderCost = backOrderCost;
	}
}
