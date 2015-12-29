/*
 * Created on 2009-2-19
 *
 */
package adultadmin.bean.buy;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voSelect;

/**
 * @author Administrator
 *
 * 说明：进货单所含产品Bean
 */
public class BuyStockProductBean {

	public static int UNDEAL = 0;         //状态：未处理
	public static int DEALED = 1;         //状态：已处理

	public int id;                        //ID

	public int buyStockId;                //所属进货单ID

	public int productId;                 //产品ID

	public int buyCount;                  //进货量
	
	public int stockinCount;              //入库量

	public float purchasePrice;           //预计进货价格

	public int productProxyId;            //代理商ID

	public int status;                    //状态

	public String createDatetime;         //添加时间

	public String remark;                 //备注

	public String confirmDatetime;        //确认时间
	
	public String productLineName;		  //产品所属产品线

	public BuyStockBean buyStock;         //对应进货单

	public voProduct product;             //对应产品

	public voSelect proxy;                //对应代理商
	
	public int getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(int buyCount) {
		this.buyCount = buyCount;
	}

	public int getBuyStockId() {
		return buyStockId;
	}

	public void setBuyStockId(int buyStockId) {
		this.buyStockId = buyStockId;
	}

	public String getConfirmDatetime() {
		return confirmDatetime;
	}

	public void setConfirmDatetime(String confirmDatetime) {
		this.confirmDatetime = confirmDatetime;
	}

	public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public float getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(float purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public BuyStockBean getBuyStock() {
		return buyStock;
	}

	public void setBuyStock(BuyStockBean buyStock) {
		this.buyStock = buyStock;
	}

	public voProduct getProduct() {
		return product;
	}

	public void setProduct(voProduct product) {
		this.product = product;
	}

	public int getProductProxyId() {
		return productProxyId;
	}

	public void setProductProxyId(int productProxyId) {
		this.productProxyId = productProxyId;
	}

	public voSelect getProxy() {
		return proxy;
	}

	public void setProxy(voSelect proxy) {
		this.proxy = proxy;
	}

	public int getStockinCount() {
		return stockinCount;
	}

	public void setStockinCount(int stockinCount) {
		this.stockinCount = stockinCount;
	}

	public String getProductLineName() {
		return productLineName;
	}

	public void setProductLineName(String productLineName) {
		this.productLineName = productLineName;
	}

}
