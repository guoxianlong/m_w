package mmb.stock.stat;

import adultadmin.action.vo.voProduct;

//异常单商品
public class SortingAbnormalProductBean {
	public int id;
	public int sortingAbnormalId;//分拣异常单id
	public int productId;//商品id
	public String productCode;//商品编号
	public String cargoWholeCode;//货位号
	public int count;//商品总数
	public int lockCount;//锁定量
	public int status;//状态 
	public String lastOperDatetime; //最后操作时间
	
	public voProduct product; //productBean
	
	public int cargo_id;//货位ID
	/**
	 * 未处理 
	 */
	public static final int STATUS_UNDEAL = 0;
	/**
	 * 处理 中
	 */
	public static final int STATUS_DEALING = 1;
	/**
	 * 待盘点 
	 */
	public static final int STATUS_WAIT_FOR_CHECK = 2;
	/**
	 * 盘点中 
	 */
	public static final int STATUS_CHECKING = 3;
	/**
	 * 已盘点
	 */
	public static final int STATUS_CHECKED = 4;
	/**
	 * 无异常
	 */
	public static final int STATUS_NORMAL = 5;
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSortingAbnormalId() {
		return sortingAbnormalId;
	}
	public void setSortingAbnormalId(int sortingAbnormalId) {
		this.sortingAbnormalId = sortingAbnormalId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getCargoWholeCode() {
		return cargoWholeCode;
	}
	public void setCargoWholeCode(String cargoWholeCode) {
		this.cargoWholeCode = cargoWholeCode;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getLockCount() {
		return lockCount;
	}
	public void setLockCount(int lockCount) {
		this.lockCount = lockCount;
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
	
	public String getCargoCodePartly() {
		String result = "";
		if( this.cargoWholeCode == null || this.cargoWholeCode.equals("")) {
			
		} else {
			int point = this.cargoWholeCode.indexOf("-");
			result =this.cargoWholeCode.substring(point+1, this.cargoWholeCode.length());
		}
		return result;
	}
	public String getLastOperDatetime() {
		return lastOperDatetime;
	}
	public void setLastOperDatetime(String lastOperDatetime) {
		this.lastOperDatetime = lastOperDatetime;
	}
	public int getCargo_id() {
		return cargo_id;
	}
	public void setCargo_id(int cargo_id) {
		this.cargo_id = cargo_id;
	}
	
	public String getStatusName() {
		String result = "";
		switch(this.status) {
			case SortingAbnormalProductBean.STATUS_UNDEAL :
				result ="未处理";
				break;
			case SortingAbnormalProductBean.STATUS_DEALING :
				result ="处理中";
				break;
			case SortingAbnormalProductBean.STATUS_WAIT_FOR_CHECK :
				result ="待盘点";
				break;
			case SortingAbnormalProductBean.STATUS_CHECKING :
				result ="盘点中";
				break;
			case SortingAbnormalProductBean.STATUS_CHECKED :
				result ="已盘点";
				break;
			case SortingAbnormalProductBean.STATUS_NORMAL :
				result ="无异常";
				break;
			default : result = "";
		}
		return result;
	}
	
}
