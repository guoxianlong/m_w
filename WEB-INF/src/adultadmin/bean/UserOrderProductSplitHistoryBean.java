package adultadmin.bean;

/**
 * 把套装拆分后的产品信息
 * @author zhangtao
 *
 */
public class UserOrderProductSplitHistoryBean implements Cloneable{

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
	 * 备用的相关产品ID
	 */
	public int productParentId3;

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
	
	/**
	 * 不含税库存均价
	 */
	private float notaxPrice;

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
	
	public int getProductParentId3() {
		return productParentId3;
	}

	public void setProductParentId3(int productParentId3) {
		this.productParentId3 = productParentId3;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public float getNotaxPrice() {
		return notaxPrice;
	}

	public void setNotaxPrice(float notaxPrice) {
		this.notaxPrice = notaxPrice;
	}

	public void setTypeMask(int type) {
		if(type == UserOrderProductHistoryBean.TYPE_NORMAL){
			this.type = UserOrderProductHistoryBean.TYPE_NORMAL;
		} else if(type == UserOrderProductHistoryBean.TYPE_PACKAGE){
			// 0位 ：套装商品
			this.type = this.type | 1;
		} else if(type == UserOrderProductHistoryBean.TYPE_FREQUENT){
			// 1位 ：套装商品
			this.type = this.type | (1 << 1);
		} else if(type == UserOrderProductHistoryBean.TYPE_GROUPBUY){
			// 2位 ：套装商品
			this.type = this.type | (1 << 2);
		}
	}

	public boolean isTypeMask(int type) {
		if(type == UserOrderProductHistoryBean.TYPE_NORMAL){
			return this.type == UserOrderProductHistoryBean.TYPE_NORMAL;
		} else if(type == UserOrderProductHistoryBean.TYPE_PACKAGE){
			// 0位 ：套装商品
			return ((this.type & 1) != 0);
		} else if(type == UserOrderProductHistoryBean.TYPE_FREQUENT){
			// 1位 ：套装商品
			return ((this.type & (1 << 1)) != 0);
		} else if(type == UserOrderProductHistoryBean.TYPE_GROUPBUY){
			// 2位 ：套装商品
			return ((this.type & (1 << 2)) != 0);
		}

		return false;
	}
	
	@Override
	public UserOrderProductSplitHistoryBean clone() throws CloneNotSupportedException {
		return (UserOrderProductSplitHistoryBean)super.clone();
	}
}
