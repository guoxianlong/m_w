
    /**  
     * 文件名：AreaStockExchange.java  
     *  
     * 版本信息：  
     * 日期：2013-2-20  
     * Copyright 买卖宝 Corporation 2013   
     * 版权所有  
     *  
     */  
    
package mmb.stock.stat;

import java.util.ArrayList;
import java.util.List;

import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.stock.StockExchangeBean;


/**  
 * 此类描述的是：地区间调拨  
 * @author: liubo 
 * @version: 2013-2-20 下午02:24:00   
 */

public class AreaStockExchangeBean {

	public int id;
	public int productId;//产品id
	public float saleCount;//日均发货量
	public int outArea;//源调拨地区
	public float outSaleCount;//源调拨地区日均发货量
	public int area;//目的调拨地区
	
	private String productCode;//产品编号
	private String productOriName;//原名称
	private int inAreaStockCount;//目的库结存
	private int outAreaStockCount;//源库结存
	private int stockinCount;//7日内预计到货数量
	private int exchangeInCount;//调入数量
	private int exchangeOutCount;//调出数量
	private int needExchangeCount;//需调拨量
//	private List<String> buyStockinCodeList = new ArrayList<String>();//7日内预计到货单
	private String buyStockCode;//7日内预计到货单号列表
	
	private List<CargoOperationBean> stockExchangeBeanList = new ArrayList<CargoOperationBean>();//调拨单信息
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOutArea() {
		return outArea;
	}
	public void setOutArea(int outArea) {
		this.outArea = outArea;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getProductOriName() {
		return productOriName;
	}
	public void setProductOriName(String productOriName) {
		this.productOriName = productOriName;
	}
	public int getInAreaStockCount() {
		return inAreaStockCount;
	}
	public void setInAreaStockCount(int inAreaStockCount) {
		this.inAreaStockCount = inAreaStockCount;
	}
	public int getOutAreaStockCount() {
		return outAreaStockCount;
	}
	public void setOutAreaStockCount(int outAreaStockCount) {
		this.outAreaStockCount = outAreaStockCount;
	}
	public int getStockinCount() {
		return stockinCount;
	}
	public void setStockinCount(int stockinCount) {
		this.stockinCount = stockinCount;
	}
	public int getExchangeInCount() {
		return exchangeInCount;
	}
	public void setExchangeInCount(int exchangeInCount) {
		this.exchangeInCount = exchangeInCount;
	}
	public int getExchangeOutCount() {
		return exchangeOutCount;
	}
	public void setExchangeOutCount(int exchangeOutCount) {
		this.exchangeOutCount = exchangeOutCount;
	}
	public int getNeedExchangeCount() {
		return needExchangeCount;
	}
	public void setNeedExchangeCount(int needExchangeCount) {
		this.needExchangeCount = needExchangeCount;
	}
//	public List<String> getBuyStockinCodeList() {
//		return buyStockinCodeList;
//	}
//	public void setBuyStockinCodeList(List<String> buyStockinCodeList) {
//		this.buyStockinCodeList = buyStockinCodeList;
//	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public float getSaleCount() {
		return saleCount;
	}
	public void setSaleCount(float saleCount) {
		this.saleCount = saleCount;
	}
	public float getOutSaleCount() {
		return outSaleCount;
	}
	public void setOutSaleCount(float outSaleCount) {
		this.outSaleCount = outSaleCount;
	}
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	public String getBuyStockCode() {
		return buyStockCode;
	}
	public void setBuyStockCode(String buyStockCode) {
		this.buyStockCode = buyStockCode;
	}
	
	public boolean equals(Object otherObject) {
		if (this == otherObject)
			return true;
		if (otherObject == null)
			return false;
		if (getClass() != otherObject.getClass())
			return false;
		AreaStockExchangeBean other = (AreaStockExchangeBean) otherObject;
		return id==other.id;
	}

	public int hashCode() {
		int result = 17;
		int idValue = this.getId() == 0 ? 0 : Integer.valueOf(this.getId()).hashCode();
		result = result * 37 + idValue;
		return result;
	}
	public List<CargoOperationBean> getStockExchangeBeanList() {
		return stockExchangeBeanList;
	}
	public void setStockExchangeBeanList(
			List<CargoOperationBean> stockExchangeBeanList) {
		this.stockExchangeBeanList = stockExchangeBeanList;
	}
}
