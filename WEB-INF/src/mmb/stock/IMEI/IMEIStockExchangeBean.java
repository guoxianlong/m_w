package mmb.stock.IMEI;

import adultadmin.bean.stock.StockExchangeProductBean;


/**
 *IMEI与调拨单关联表
 */
public class IMEIStockExchangeBean {
	/**
	 * id
	 */
	public int id;
	/**
	 * IMEI码
	 */
	public String IMEI;
	/**
	 * 调拨单id
	 */
	public int stockExchangeId;
	/**
	 * 商品id
	 */
	public int productId;
	public IMEIBean imeiBean;
	public StockExchangeProductBean stockExchangeProductBean;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIMEI() {
		return IMEI;
	}
	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}
	public int getStockExchangeId() {
		return stockExchangeId;
	}
	public void setStockExchangeId(int stockExchangeId) {
		this.stockExchangeId = stockExchangeId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public IMEIBean getImeiBean() {
		return imeiBean;
	}
	public void setImeiBean(IMEIBean imeiBean) {
		this.imeiBean = imeiBean;
	}
	public StockExchangeProductBean getStockExchangeProductBean() {
		return stockExchangeProductBean;
	}
	public void setStockExchangeProductBean(
			StockExchangeProductBean stockExchangeProductBean) {
		this.stockExchangeProductBean = stockExchangeProductBean;
	}
	
	
}
