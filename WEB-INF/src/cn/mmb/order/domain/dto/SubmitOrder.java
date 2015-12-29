package cn.mmb.order.domain.dto;

import java.util.List;

import cn.mmb.order.domain.entity.PopStockArea;

public class SubmitOrder {
	
	private static final int ADDRESSCONSTANT = 100000;

	private String orderCode;
	
	private int addr1;
	private int addr2;
	private int addr3;
	private int addr4;
	
	private List<SubmitOrderProduct> productList;
	
	/**京东发货仓*/
	private PopStockArea stockArea;
	/**POP订单号*/
	private String popOrderCode;
	
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public int getAddr1() {
		if(this.addr1 > ADDRESSCONSTANT){
			this.addr1 -= ADDRESSCONSTANT;
		}
		return addr1;
	}
	public void setAddr1(int addr1) {
		this.addr1 = addr1;
	}
	public int getAddr2() {
		if(this.addr2 > ADDRESSCONSTANT){
			this.addr2 -= ADDRESSCONSTANT;
		}
		return addr2;
	}
	public void setAddr2(int addr2) {
		this.addr2 = addr2;
	}
	public int getAddr3() {
		if(this.addr3 > ADDRESSCONSTANT){
			this.addr3 -= ADDRESSCONSTANT;
		}
		return addr3;
	}
	public void setAddr3(int addr3) {
		this.addr3 = addr3;
	}
	public int getAddr4() {
		if(this.addr4 > ADDRESSCONSTANT){
			this.addr4 -= ADDRESSCONSTANT;
		}
		return addr4;
	}
	public void setAddr4(int addr4) {
		this.addr4 = addr4;
	}
	public List<SubmitOrderProduct> getProductList() {
		return productList;
	}
	public void setProductList(List<SubmitOrderProduct> productList) {
		this.productList = productList;
	}
	public PopStockArea getStockArea() {
		return stockArea;
	}
	public void setStockArea(PopStockArea stockArea) {
		this.stockArea = stockArea;
	}
	public String getPopOrderCode() {
		return popOrderCode;
	}
	public void setPopOrderCode(String popOrderCode) {
		this.popOrderCode = popOrderCode;
	}
	
}
