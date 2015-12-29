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
 * 说明：采购订单产品Bean
 */
public class BuyOrderProductBean {

	public static int UNDEAL = 0;          //状态：未处理
	public static int DEALED = 1;          //状态：已处理
	public static int AUDITINGED = 2;      //状态：已审核未入库
	
	public int id;                         //ID

	public int buyOrderId;                 //所属订单ID

	public int productId;                  //产品ID

	public int orderCountBJ;               //北京订单数量
	public int orderCountGD;               //广东订单数量
	
	public int stockCountBJ;               //北京已进货量
	public int stockCountGD;               //广东已进货量
	
	public int stockinCountBJ;             //北京已入库量
	public int stockinCountGD;             //广东已入库量
	
	public int returnCount;                //采购退货量
	
	public double stockinTotalPrice;        //已入库总货款金额（税后）
	 
	public double purchasePrice;            //预计进货价格
	
	public float minPurchasePrice;         //最低进货价

	public int productProxyId;             //代理商ID

	public int status;                     //状态

	public String createDatetime;          //添加时间

	public String remark;                  //备注

	public String confirmDatetime;         //确认时间
	
	public voProduct product;              //对应产品

	public voSelect proxy;                 //对应代理商

	public BuyOrderBean buyOrder;          //对应采购订单
	
	public String productLineName;         //所属产品线名称

	public int getOrderCountBJ() {
		return orderCountBJ;
	}

	public void setOrderCountBJ(int orderCountBJ) {
		this.orderCountBJ = orderCountBJ;
	}

	public int getOrderCountGD() {
		return orderCountGD;
	}

	public void setOrderCountGD(int orderCountGD) {
		this.orderCountGD = orderCountGD;
	}

	public int getBuyOrderId() {
		return buyOrderId;
	}

	public void setBuyOrderId(int buyOrderId) {
		this.buyOrderId = buyOrderId;
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

	public double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(double purchasePrice) {
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

	public BuyOrderBean getBuyOrder() {
		return buyOrder;
	}

	public void setBuyOrder(BuyOrderBean buyOrder) {
		this.buyOrder = buyOrder;
	}

	public int getStockCountBJ() {
		return stockCountBJ;
	}

	public void setStockCountBJ(int stockCountBJ) {
		this.stockCountBJ = stockCountBJ;
	}

	public int getStockCountGD() {
		return stockCountGD;
	}

	public void setStockCountGD(int stockCountGD) {
		this.stockCountGD = stockCountGD;
	}

	public int getStockinCountBJ() {
		return stockinCountBJ;
	}

	public void setStockinCountBJ(int stockinCountBJ) {
		this.stockinCountBJ = stockinCountBJ;
	}

	public int getStockinCountGD() {
		return stockinCountGD;
	}

	public void setStockinCountGD(int stockinCountGD) {
		this.stockinCountGD = stockinCountGD;
	}

	public double getStockinTotalPrice() {
		return stockinTotalPrice;
	}

	public void setStockinTotalPrice(double stockinTotalPrice) {
		this.stockinTotalPrice = stockinTotalPrice;
	}

	public float getMinPurchasePrice() {
		return minPurchasePrice;
	}

	public void setMinPurchasePrice(float minPurchasePrice) {
		this.minPurchasePrice = minPurchasePrice;
	}

	public String getProductLineName() {
		return productLineName;
	}

	public void setProductLineName(String productLineName) {
		this.productLineName = productLineName;
	}

	public int getReturnCount() {
		return returnCount;
	}

	public void setReturnCount(int returnCount) {
		this.returnCount = returnCount;
	}
	
}
