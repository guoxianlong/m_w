/*
 * Created on 2007-11-14
 *
 */
package adultadmin.bean.stock;


/**
 * 作者：张陶
 * 
 * 创建日期：2007-11-14
 * 
 * 说明：
 */
public class StockProductHistoryBean {

    public int id;

    public int operId;

    public int productId;

    public String productCode;

    public String oriname;

    public int proxyId;

    /**
     * <pre>
     * 采购价格
     * 采购入库的时候，根据这个价格设置批发价(price3)，同时计算库存价格(price5)
     * </pre>
     */
    public float price3;

    public String proxyName;

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOperId() {
		return operId;
	}

	public void setOperId(int operId) {
		this.operId = operId;
	}

	public String getOriname() {
		return oriname;
	}

	public void setOriname(String oriname) {
		this.oriname = oriname;
	}

	public float getPrice3() {
		return price3;
	}

	public void setPrice3(float price3) {
		this.price3 = price3;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getProxyId() {
		return proxyId;
	}

	public void setProxyId(int proxyId) {
		this.proxyId = proxyId;
	}

	public String getProxyName() {
		return proxyName;
	}

	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}

    
}
