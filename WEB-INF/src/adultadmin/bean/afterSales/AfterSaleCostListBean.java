package adultadmin.bean.afterSales;

import adultadmin.bean.system.TextResBean;
import adultadmin.util.Arith;

public class AfterSaleCostListBean {

	public int id;
	public int afterSaleOrderId;
	public int costType;//是应收1 还是应付 2 (退换货时)    原品返回中 text_res 10 的值
	public int typeRes; //text_res 中值6  退货原因 text_res 8 的值 原品返回中 text_res 9 的值
	public float franking; // 运费
	public float shipping; //应收 发货费
	public float fray; // 磨损费
	public int backType; // 有余额打款类型 ：1向用户钱包汇款 2 银行打款 有差额： 4货到付款 5银行汇款  6钱包支付  
	public float payShipping;//应付  发货费
	public String backUserName; // 钱包账号
	public float returnProductPrice ;//退回商品总价
	
	public float realPrice; //换货商品 销售价格
	public float realDprice; // 换货商品 折扣后价格
	
	public float payFranking;//应付 运费
	public float payFray;//应付 磨损费
	
	private String typeResText;
	private String costTypeName; // 原品返回中 text_res 10 的值
	
	private TextResBean trBean ;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAfterSaleOrderId() {
		return afterSaleOrderId;
	}

	public void setAfterSaleOrderId(int afterSaleOrderId) {
		this.afterSaleOrderId = afterSaleOrderId;
	}

	public int getTypeRes() {
		return typeRes;
	}

	public void setTypeRes(int typeRes) {
		this.typeRes = typeRes;
	}

	public float getFranking() {
		return franking;
	}

	public void setFranking(float franking) {
		this.franking = franking;
	}

	public float getShipping() {
		return shipping;
	}

	public void setShipping(float shipping) {
		this.shipping = shipping;
	}

	public float getFray() {
		return fray;
	}

	public void setFray(float fray) {
		this.fray = fray;
	}

	public int getBackType() {
		return backType;
	}

	public void setBackType(int backType) {
		this.backType = backType;
	}

	public String getBackUserName() {
		return backUserName;
	}

	public void setBackUserName(String backUserName) {
		this.backUserName = backUserName;
	}

	public int getCostType() {
		return costType;
	}

	public void setCostType(int costType) {
		this.costType = costType;
	}

	public String getTypeResText() {
		return typeResText;
	}

	public void setTypeResText(String typeResText) {
		this.typeResText = typeResText;
	}

	public float getReturnProductPrice() {
		return returnProductPrice;
	}

	public void setReturnProductPrice(float returnProductPrice) {
		this.returnProductPrice = returnProductPrice;
	}

	/**
	 * 
	 * 功能:计算退货余额 
	 * <p>作者 李双 Apr 18, 2012 2:01:09 PM
	 * @return
	 */
	public float getBalance(){
		if(costType==1){//应收
			return Arith.sub(Arith.sub(Arith.sub(Arith.add(returnProductPrice,payShipping),franking),shipping),fray);
		}else{
			return Arith.sub(Arith.sub(Arith.add(Arith.add(returnProductPrice,franking),payShipping),shipping),fray);
		}
	}
	/**
	 * 
	 * 功能:计算原品返回价格计算
	 * 应收-应付
	 * <p>作者 李双 Sep 20, 2012 4:10:58 PM
	 * @return
	 */
	public float getOrignalBalance(){
		return (franking+fray+shipping)-payFranking-payFray-payShipping;
	}
	
	public float getPayShipping() {
		return payShipping;
	}

	public void setPayShipping(float payShipping) {
		this.payShipping = payShipping;
	}

	public float getRealPrice() {
		return realPrice;
	}

	public void setRealPrice(float realPrice) {
		this.realPrice = realPrice;
	}

	public float getRealDprice() {
		return realDprice;
	}

	public void setRealDprice(float realDprice) {
		this.realDprice = realDprice;
	}

	public float getPayFranking() {
		return payFranking;
	}

	public void setPayFranking(float payFranking) {
		this.payFranking = payFranking;
	}

	public float getPayFray() {
		return payFray;
	}

	public void setPayFray(float payFray) {
		this.payFray = payFray;
	}

	public String getCostTypeName() {
		return costTypeName;
	}

	public void setCostTypeName(String costTypeName) {
		this.costTypeName = costTypeName;
	}

	public TextResBean getTrBean() {
		return trBean;
	}

	public void setTrBean(TextResBean trBean) {
		this.trBean = trBean;
	}

}
