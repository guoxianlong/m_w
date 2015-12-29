package adultadmin.bean.stock;

import adultadmin.action.vo.voUser;

/**
 * 作者：赵林
 * 
 * 创建时间：2009-08-20
 * 
 * 说明：批次操作记录Bean
 *
 */
public class StockBatchLogBean {

	public int id;           //id
	
	public String code;      //单据号
	
	public int stockType;   //库类别
	
	public int stockArea;   //库地区
	
	public String batchCode;//批次号
	
	public int batchCount;  //批次变化量
	
	public float batchPrice;//批次价格
	
	public int productId;   //对应产品ID
	
	public String remark;    //来源（备注）
	
	public String createDatetime; //操作时间
	
	public int userId;       //操作人ID
	
	public voUser user;      //操作人

	
	public String orderCode;  //订单号
	public String confirm_datetime;//入库时间;
	
	public int supplierId; //供应商id
	
	public float tax; //供应商税率
	
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getConfirm_datetime() {
		return confirm_datetime;
	}
	public void setConfirm_datetime(String confirm_datetime) {
		this.confirm_datetime = confirm_datetime;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public int getStockType() {
		return stockType;
	}

	public void setStockType(int stockType) {
		this.stockType = stockType;
	}

	public int getStockArea() {
		return stockArea;
	}

	public void setStockArea(int stockArea) {
		this.stockArea = stockArea;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public int getBatchCount() {
		return batchCount;
	}

	public void setBatchCount(int batchCount) {
		this.batchCount = batchCount;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public voUser getUser() {
		return user;
	}

	public void setUser(voUser user) {
		this.user = user;
	}

	public float getBatchPrice() {
		return batchPrice;
	}

	public void setBatchPrice(float batchPrice) {
		this.batchPrice = batchPrice;
	}
	public int getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}
	public float getTax() {
		return tax;
	}
	public void setTax(float tax) {
		this.tax = tax;
	}
}
