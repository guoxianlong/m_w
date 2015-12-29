package adultadmin.bean.order;

/**
 * 
 * <code>UserOrderCommonPropertiesBean.java</code>
 * <p>
 * 功能:
 * 
 * <p>
 * Copyright 商机无限 2012 All right reserved.
 * 
 * @author 李双 lishuang@ebinf.com 时间 Oct 9, 2012 4:30:49 PM
 * @version 1.0 </br>最后修改人 无
 */
public class UserOrderCommonPropertiesBean {
	/** 订单id */
	public int orderId;
	/** 订单状态 */
	public int orderStatus;
	/** 订单发货状态 */
	public int stockoutStatus;
	/** 用户自己取消 */
	public int userCansel;
	/** 发货备注记录 */
	public String stockoutRemark;

	/**
	 * 订单状态  名称
	 */
	private String orderContent;
	
	/**
	 * 发货订单  名称
	 */
	private String stockoutContent;
	
	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(int orderStatus) {
		this.orderStatus = orderStatus;
	}

	public int getStockoutStatus() {
		return stockoutStatus;
	}

	public void setStockoutStatus(int stockoutStatus) {
		this.stockoutStatus = stockoutStatus;
	}

	public int getUserCansel() {
		return userCansel;
	}

	public void setUserCansel(int userCansel) {
		this.userCansel = userCansel;
	}

	public String getStockoutRemark() {
		return stockoutRemark;
	}

	public void setStockoutRemark(String stockoutRemark) {
		this.stockoutRemark = stockoutRemark;
	}

	public String getOrderContent() {
		return orderContent;
	}

	public void setOrderContent(String orderContent) {
		this.orderContent = orderContent;
	}

	public String getStockoutContent() {
		return stockoutContent;
	}

	public void setStockoutContent(String stockoutContent) {
		this.stockoutContent = stockoutContent;
	}

 

}
