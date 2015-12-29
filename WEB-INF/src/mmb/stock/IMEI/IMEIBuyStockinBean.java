package mmb.stock.IMEI;

/**
 * IMEI 采购入库关联表
 * 2018-12-16
 * @author HaoYabin
 */
public class IMEIBuyStockinBean {
	
	
	public int id;
	/**
	 * 采购入库单id
	 */
	public int buyStockinId;
	/**
	 * 商品id
	 */
	public int productId;
	/**
	 * IMEI码
	 */
	public String IMEI;
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getBuyStockinId() {
		return buyStockinId;
	}
	public void setBuyStockinId(int buyStockinId) {
		this.buyStockinId = buyStockinId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getIMEI() {
		return IMEI;
	}
	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}

}
