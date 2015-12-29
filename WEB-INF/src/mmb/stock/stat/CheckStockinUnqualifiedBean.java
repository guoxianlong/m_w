package mmb.stock.stat;

import adultadmin.action.vo.voProduct;
import adultadmin.bean.stock.ProductStockBean;

/**
 * @name 不合格品接收明细
 * @author hyb
 *
 */
public class CheckStockinUnqualifiedBean {
	
	/**
	 * 未导出
	 */
	public static final int UNEXPORT = 0;
	/**
	 * 已导出
	 */
	public static final int EXPORT = 1;
	
	
	public String buyStockCode; //预计到货单号
	public String buyStorageCode; //采购入库单号
	public int count; //数量
	public String exchangeCode; //调拨单号
	public String exchangeDatetime; //调拨时间
	public int id; //不合格品接收明细id
	public String remark; //不合格原因
	public int missionBatchId; //所属批次任务的id
	public int missionId; //质检入库任务id
	public int status; //状态
	public String stockinDatetime; //到货时间
	public int productId; //产品id
	public voProduct product;
	public int area;  // 不合格品入库地区
	
	
	public voProduct getProduct() {
		return product;
	}
	public void setProduct(voProduct product) {
		this.product = product;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getBuyStockCode() {
		return buyStockCode;
	}
	public String getBuyStorageCode() {
		return buyStorageCode;
	}
	public int getCount() {
		return count;
	}
	public String getExchangeCode() {
		return exchangeCode;
	}
	public String getExchangeDatetime() {
		return exchangeDatetime;
	}
	public int getId() {
		return id;
	}
	public String getRemark() {
		return remark;
	}
	public int getMissionBatchId() {
		return missionBatchId;
	}
	public int getMissionId() {
		return missionId;
	}
	public int getStatus() {
		return status;
	}
	public String getStockinDatetime() {
		return stockinDatetime;
	}
	public void setBuyStockCode(String buyStockCode) {
		this.buyStockCode = buyStockCode;
	}
	public void setBuyStorageCode(String buyStorageCode) {
		this.buyStorageCode = buyStorageCode;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public void setExchangeCode(String exchangeCode) {
		this.exchangeCode = exchangeCode;
	}
	public void setExchangeDatetime(String exchangeDatetime) {
		this.exchangeDatetime = exchangeDatetime;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public void setMissionBatchId(int missionBatchId) {
		this.missionBatchId = missionBatchId;
	}
	public void setMissionId(int missionId) {
		this.missionId = missionId;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public void setStockinDatetime(String stockinDatetime) {
		this.stockinDatetime = stockinDatetime;
	}
	
	public String getStatusName() {
		if(status == UNEXPORT ) {
			return "未导出";
		} else if( status == EXPORT) {
			return "已导出";
		}else {
			return "";
		}
	}
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	
	public String getAreaName() {
		return ProductStockBean.getAreaName(this.area);
	}
	

}
