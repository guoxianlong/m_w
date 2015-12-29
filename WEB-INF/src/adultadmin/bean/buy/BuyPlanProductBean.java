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
 * 说明：采购计划产品Bean
 */
public class BuyPlanProductBean {

	public static int UNDEAL = 0;  //状态：未处理
	public static int DEALED = 1;  //状态：已处理
	public static int TRANSFORMED = 2; //状态：已转换
	public static int NOTRANSFORM = 3; //状态：未转换
	
	public int id;                 //ID

	public int buyPlanId;          //所属采购计划ID

	public int productId;          //产品ID

	public int planCountBJ;        //北京计划采购数量
	public int planCountGD;        //广东计划采购数量
	public int planCountBJFinished;//北京采购在途量
	public int planCountGDFinished;//广东采购在途量
	public int stockinCountBJ;     //北京已入库量
	public int stockinCountGD;     //广东已入库量
	
	public int lastWeekSellCountGD;  //广东上周销量
	public int lastMonthSellCountGD; //广东上月销量
	public int dealingCountGD;       //广东处理中量
	public int stockCountQualifiedGD;//广东可发货数(合格库量)
	public int stockCountCheckGD;    //广东待验库量
	
	public float purchasePrice;    //产品预计进货价格

	public int productProxyId;     //产品所属代理商ID

	public int status;             //状态

	public String createDatetime;  //产品添加入采购计划时间

	public String remark;          //备注

	public String confirmDatetime; //确认时间 

	public String productLineName; //对应的产品线名称
	
	public voProduct product;      //对应产品

	public voSelect proxy;         //对应代理商

	public BuyPlanBean buyPlan;    //对应采购计划

	public int getPlanCountBJ() {
		return planCountBJ;
	}

	public void setPlanCountBJ(int planCountBJ) {
		this.planCountBJ = planCountBJ;
	}

	public int getPlanCountGD() {
		return planCountGD;
	}

	public void setPlanCountGD(int planCountGD) {
		this.planCountGD = planCountGD;
	}

	public int getBuyPlanId() {
		return buyPlanId;
	}

	public void setBuyPlanId(int buyPlanId) {
		this.buyPlanId = buyPlanId;
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

	public BuyPlanBean getBuyPlan() {
		return buyPlan;
	}

	public void setBuyPlan(BuyPlanBean buyPlan) {
		this.buyPlan = buyPlan;
	}

	public int getPlanCountBJFinished() {
		return planCountBJFinished;
	}

	public void setPlanCountBJFinished(int planCountBJFinished) {
		this.planCountBJFinished = planCountBJFinished;
	}

	public int getPlanCountGDFinished() {
		return planCountGDFinished;
	}

	public void setPlanCountGDFinished(int planCountGDFinished) {
		this.planCountGDFinished = planCountGDFinished;
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

	public int getLastWeekSellCountGD() {
		return lastWeekSellCountGD;
	}

	public void setLastWeekSellCountGD(int lastWeekSellCountGD) {
		this.lastWeekSellCountGD = lastWeekSellCountGD;
	}

	public int getLastMonthSellCountGD() {
		return lastMonthSellCountGD;
	}

	public void setLastMonthSellCountGD(int lastMonthSellCountGD) {
		this.lastMonthSellCountGD = lastMonthSellCountGD;
	}

	public int getDealingCountGD() {
		return dealingCountGD;
	}

	public void setDealingCountGD(int dealingCountGD) {
		this.dealingCountGD = dealingCountGD;
	}

	public int getStockCountQualifiedGD() {
		return stockCountQualifiedGD;
	}

	public void setStockCountQualifiedGD(int stockCountQualifiedGD) {
		this.stockCountQualifiedGD = stockCountQualifiedGD;
	}

	public int getStockCountCheckGD() {
		return stockCountCheckGD;
	}

	public void setStockCountCheckGD(int stockCountCheckGD) {
		this.stockCountCheckGD = stockCountCheckGD;
	}

	public String getProductLineName() {
		return productLineName;
	}

	public void setProductLineName(String productLineName) {
		this.productLineName = productLineName;
	}

}
