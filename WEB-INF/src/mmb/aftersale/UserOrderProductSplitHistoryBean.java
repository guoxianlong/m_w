package mmb.aftersale;



/**
 * 把套装拆分后的产品信息
 * @author zhangtao
 *
 */
public class UserOrderProductSplitHistoryBean {

	/**
	 * 普通商品
	 */
	public static int TYPE_NORMAL = 0;
	/**
	 * 套装商品
	 */
	public static int TYPE_PACKAGE = 1;

	/**
	 * 老用户折扣
	 */
	public static int TYPE_FREQUENT = 2;

	/**
	 * 老的团购活动
	 */
	public static int TYPE_GROUPBUY = 3;

	public int id;
	/**
	 * 订单ID
	 */
	public int orderId;
	/**
	 * 产品ID
	 */
	public int productId;

	/**
	 * 所属套装的产品ID
	 */
	public int productParentId1;

	/**
	 * 备用的相关产品ID
	 */
	public int productParentId2;

	/**
	 * 商品类型：
	 * 根据不同位的值，判断不同的类型，可以组合
	 * 0 - 普通
	 * 第0位 - 套装中的商品
	 * 第1位 - 老用户折扣
	 * 第2位 - 老团购
	 */
	public int type;

	/**
	 * 订单中的数量
	 */
	public int count;
	/**
	 * 小店价格
	 */
	public float price;
	/**
	 * 订单折扣均摊到每个商品后，商品的价格
	 */
	public float dprice;
	/**
	 * 库存价格
	 */
	public float price5;

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

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getDprice() {
		return dprice;
	}

	public void setDprice(float dprice) {
		this.dprice = dprice;
	}

	public float getPrice5() {
		return price5;
	}

	public void setPrice5(float price5) {
		this.price5 = price5;
	}

	public int getProductParentId1() {
		return productParentId1;
	}

	public void setProductParentId1(int productParentId1) {
		this.productParentId1 = productParentId1;
	}

	public int getProductParentId2() {
		return productParentId2;
	}

	public void setProductParentId2(int productParentId2) {
		this.productParentId2 = productParentId2;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}

