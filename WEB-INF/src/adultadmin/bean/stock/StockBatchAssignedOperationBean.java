package adultadmin.bean.stock;

import adultadmin.action.vo.voUser;

/**
 * 作者：赵林
 * 
 * 创建时间：2009-08-20
 * 
 * 说明：指定批次操作Bean
 *
 */
public class StockBatchAssignedOperationBean {

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
	
	public int status;       //处理状态
	
	public int userId;       //操作人ID
	
	public voUser user;      //操作人
	
	/**
	 * 处理状态：未处理
	 */
	public static final int STATUS0 = 0;
	
	/**
	 * 处理状态：已处理
	 */
	public static final int STATUS1 = 1;
	

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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
