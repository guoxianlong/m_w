/**  
 * @Description: 
 * @author 叶二鹏   
 * @date 2015年10月14日 上午10:37:14 
 * @version V1.0   
 */
package cn.mmb.productarrival.domain.model;

/** 
 * @ClassName: ArrivalMessageModel 
 * @Description: 商品到货信息model
 * @author: 叶二鹏
 * @date: 2015年10月14日 上午10:37:14  
 */
public class ArrivalMessageModel {
	
	private int id;
	
	private int areaId;
	
	private String areaName;
	
	private String arrivalTime;
	
	private int codeFlag;
	
	private String waybillCode;
	
	private int deliverCorpId;
	
	private String deliverCorpName;
	
	private String buyPlanCode;
	
	private int supplierId;
	
	private String supplierName;
	
	private int arrivalCount;
	
	private String temporaryCargo;
	
	private int productLineId;
	
	private String productLineName;
	
	private String businessUnit;
	
	private String receiver;
	
	private String addUser;
	
	private String addTime;
	
	private int isPrintBill;
	
	private String arrivalException;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

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
	 * @return the arrivalTime
	 */
	public String getArrivalTime() {
		if(arrivalTime.length() >16) {
			return arrivalTime.substring(0, 16);
		}
		return arrivalTime;
	}

	/**
	 * @param arrivalTime the arrivalTime to set
	 */
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	/**
	 * @return the codeFlag
	 */
	public int getCodeFlag() {
		return codeFlag;
	}
	
	public String getCodeFlagName() {
		if (this.codeFlag == 1) {
			return "有";
		}
		if (this.codeFlag == 2) {
			return "无";
		}
		return "";
	}

	/**
	 * @param codeFlag the codeFlag to set
	 */
	public void setCodeFlag(int codeFlag) {
		this.codeFlag = codeFlag;
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
	 * @return the deliverCorpId
	 */
	public int getDeliverCorpId() {
		return deliverCorpId;
	}

	/**
	 * @param deliverCorpId the deliverCorpId to set
	 */
	public void setDeliverCorpId(int deliverCorpId) {
		this.deliverCorpId = deliverCorpId;
	}

	/**
	 * @return the deliverCorpName
	 */
	public String getDeliverCorpName() {
		return deliverCorpName;
	}

	/**
	 * @param deliverCorpName the deliverCorpName to set
	 */
	public void setDeliverCorpName(String deliverCorpName) {
		this.deliverCorpName = deliverCorpName;
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
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * @return the arrivalCount
	 */
	public int getArrivalCount() {
		return arrivalCount;
	}

	/**
	 * @param arrivalCount the arrivalCount to set
	 */
	public void setArrivalCount(int arrivalCount) {
		this.arrivalCount = arrivalCount;
	}

	/**
	 * @return the temporaryCargo
	 */
	public String getTemporaryCargo() {
		return temporaryCargo;
	}

	/**
	 * @param temporaryCargo the temporaryCargo to set
	 */
	public void setTemporaryCargo(String temporaryCargo) {
		this.temporaryCargo = temporaryCargo;
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
	 * @return the productLineName
	 */
	public String getProductLineName() {
		return productLineName;
	}

	/**
	 * @param productLineName the productLineName to set
	 */
	public void setProductLineName(String productLineName) {
		this.productLineName = productLineName;
	}

	/**
	 * @return the businessUnit
	 */
	public String getBusinessUnit() {
		return businessUnit;
	}

	/**
	 * @param businessUnit the businessUnit to set
	 */
	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
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

	/**
	 * @return the addUser
	 */
	public String getAddUser() {
		return addUser;
	}

	/**
	 * @param addUser the addUser to set
	 */
	public void setAddUser(String addUser) {
		this.addUser = addUser;
	}

	/**
	 * @return the addTime
	 */
	public String getAddTime() {
		if(addTime.length() >19) {
			return addTime.substring(0, 19);
		}
		return addTime;
	}

	/**
	 * @param addTime the addTime to set
	 */
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	/**
	 * @return the isPrintBill
	 */
	public int getIsPrintBill() {
		return isPrintBill;
	}

	/**
	 * @param isPrintBill the isPrintBill to set
	 */
	public void setIsPrintBill(int isPrintBill) {
		this.isPrintBill = isPrintBill;
	}
	
	public String getIsPrintBillName(){
		if (this.isPrintBill == 1) {
			return "是";
		}
		if (this.isPrintBill == 2) {
			return "否";
		}
		return "";
	}

	/**
	 * @return the areaName
	 */
	public String getAreaName() {
		return areaName;
	}

	/**
	 * @param areaName the areaName to set
	 */
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	/**
	 * @return the arrivalException
	 */
	public String getArrivalException() {
		return arrivalException;
	}

	/**
	 * @param arrivalException the arrivalException to set
	 */
	public void setArrivalException(String arrivalException) {
		this.arrivalException = arrivalException;
	}

}
