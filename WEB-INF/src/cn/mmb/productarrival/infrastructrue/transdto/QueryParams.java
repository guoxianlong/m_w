/**  
 * @Description: 
 * @author 叶二鹏   
 * @date 2015年10月14日 上午10:46:26 
 * @version V1.0   
 */
package cn.mmb.productarrival.infrastructrue.transdto;

/** 
 * @ClassName: QueryParams 
 * @Description: TODO
 * @author: 叶二鹏
 * @date: 2015年10月14日 上午10:46:26  
 */
public class QueryParams {
	
	private int areaId;
	
	private int supplierId;
	
	private String startTime;
	
	private String endTime;
	
	private int productLineId;
	
	private String buyPlanCode;
	
	private String waybillCode;
	
	private String receiver;

	/**
	 * @return the areaId
	 */
	public int getAreaId() {
		return areaId;
	}

	/**
	 * @param areaId the areaId to set
	 */
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	/**
	 * @return the supplierId
	 */
	public int getSupplierId() {
		return supplierId;
	}

	/**
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the productLineId
	 */
	public int getProductLineId() {
		return productLineId;
	}

	/**
	 * @param productLineId the productLineId to set
	 */
	public void setProductLineId(int productLineId) {
		this.productLineId = productLineId;
	}

	/**
	 * @return the buyPlanCode
	 */
	public String getBuyPlanCode() {
		return buyPlanCode;
	}

	/**
	 * @param buyPlanCode the buyPlanCode to set
	 */
	public void setBuyPlanCode(String buyPlanCode) {
		this.buyPlanCode = buyPlanCode;
	}

	/**
	 * @return the waybillCode
	 */
	public String getWaybillCode() {
		return waybillCode;
	}

	/**
	 * @param waybillCode the waybillCode to set
	 */
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	/**
	 * @return the receiver
	 */
	public String getReceiver() {
		return receiver;
	}

	/**
	 * @param receiver the receiver to set
	 */
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	

}
