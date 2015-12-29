/*
 * Created on 2009-5-8
 *
 */
package adultadmin.bean.order;

import java.io.Serializable;
import java.util.List;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voProduct;

/**
 * 出库单商品信息
 */
public class OrderStockProductBean implements Serializable{

	/**
	 * 未处理
	 */
	public static int UNDEAL = 0;
	/**
	 * 已处理
	 */
	public static int DEALED = 1;
	/**
	 * 已删除
	 */
	public static int DELETED = 2;

	public int id;

	public int orderStockId;//出库单id

	public int stockoutId;//库存记录的id，product_stock表的

	public int stockoutCount;//出库量

	public int productId;//商品id

	public String productCode;//商品编码

	public int status;//状态

	public String createDatetime;//创建时间

	public String dealDatetime;//处理时间

	public String remark;//备注

	public int stockArea;//库存地区
	public int stockType;//库存类型

	public voProduct product;
	
	public List ospcList;   //订单出货货位信息列表
	public String cargoCode;
	/**
	 * 产品条码VO
	 */
	private ProductBarcodeVO productBarcodeVO;

	public ProductBarcodeVO getProductBarcodeVO() {
		return productBarcodeVO;
	}

	public void setProductBarcodeVO(ProductBarcodeVO productBarcodeVO) {
		this.productBarcodeVO = productBarcodeVO;
	}

	public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public String getDealDatetime() {
		return dealDatetime;
	}

	public void setDealDatetime(String dealDatetime) {
		this.dealDatetime = dealDatetime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOrderStockId() {
		return orderStockId;
	}

	public void setOrderStockId(int orderStockId) {
		this.orderStockId = orderStockId;
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

	public int getStockoutCount() {
		return stockoutCount;
	}

	public void setStockoutCount(int stockoutCount) {
		this.stockoutCount = stockoutCount;
	}

	public int getStockoutId() {
		return stockoutId;
	}

	public void setStockoutId(int stockoutId) {
		this.stockoutId = stockoutId;
	}

	public voProduct getProduct() {
		return product;
	}

	public void setProduct(voProduct product) {
		this.product = product;
	}

	public int getStockArea() {
		return stockArea;
	}

	public void setStockArea(int stockArea) {
		this.stockArea = stockArea;
	}

	public int getStockType() {
		return stockType;
	}

	public void setStockType(int stockType) {
		this.stockType = stockType;
	}

	public List getOspcList() {
		return ospcList;
	}

	public void setOspcList(List ospcList) {
		this.ospcList = ospcList;
	}

	public String getCargoCode() {
		return cargoCode;
	}

	public void setCargoCode(String cargoCode) {
		this.cargoCode = cargoCode;
	}

}
