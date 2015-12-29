package cn.mmb.order.domain.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * POP采购计划单
 * @author likaige
 * @create 2015年9月16日 上午9:26:26
 */
public class PopBuyPlan {
	/**未确认：1*/
	public static final int STATUS1 = 1;
	/**已确认：2*/
	public static final int STATUS2 = 2;
	/**已取消：3*/
	public static final int STATUS3 = 3;
	
	private int id;
	private String orderCode;//MMB订单号
	private String popOrderCode;//JD订单号
	private double orderPrice;//订单价格(含税)
	private double orderNakedPrice;//订单价格(不含税)
	private double freight;//运费
	private int status;//状态[1:未确认; 2:已确认; 3:已取消]
	private String createTime;//创建时间
	private int type;//甲方类型，默认为MMB
	private int popType;//乙方类型,现为jd
	private int stockAreaPopecId; //POP发货仓id
	
	/**计划单商品列表*/
	private List<PopBuyPlanProduct> productList = new ArrayList<PopBuyPlanProduct>();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getPopOrderCode() {
		return popOrderCode;
	}
	public void setPopOrderCode(String popOrderCode) {
		this.popOrderCode = popOrderCode;
	}
	public double getOrderPrice() {
		return orderPrice;
	}
	public void setOrderPrice(double orderPrice) {
		this.orderPrice = orderPrice;
	}
	public double getOrderNakedPrice() {
		return orderNakedPrice;
	}
	public void setOrderNakedPrice(double orderNakedPrice) {
		this.orderNakedPrice = orderNakedPrice;
	}
	public double getFreight() {
		return freight;
	}
	public void setFreight(double freight) {
		this.freight = freight;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getPopType() {
		return popType;
	}
	public void setPopType(int popType) {
		this.popType = popType;
	}
	public int getStockAreaPopecId() {
		return stockAreaPopecId;
	}
	public void setStockAreaPopecId(int stockAreaPopecId) {
		this.stockAreaPopecId = stockAreaPopecId;
	}
	public List<PopBuyPlanProduct> getProductList() {
		return productList;
	}
	public void setProductList(List<PopBuyPlanProduct> productList) {
		this.productList = productList;
	}
	
}
