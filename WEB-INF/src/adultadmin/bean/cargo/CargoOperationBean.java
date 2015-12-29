package adultadmin.bean.cargo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import adultadmin.util.StringUtil;


public class CargoOperationBean {
	public int id;
	public int status=0;
	public String createDatetime;
	public String remark="";
	public String confirmDatetime;
	public int createUserId=0;
	public String auditingDatetime;
	public int auditingUserId=0;
	public String code="";
	public String source="";
	public String storageCode="";
	public int stockInType=0;//目的货位的存放类型
	public int stockOutType=0;//源货位的存放类型
	public String createUserName="";
	public String auditingUserName="";
	public String confirmUserName="";
	public String completeDatetime;
	public int completeUserId=0;
	public String completeUsername="";
	public int type;
	public int printCount;  //打印次数
	public String lastOperateDatetime;//最近更新时间
	public String statusName;//当前状态名称
	public String effectTimeName;// 时效状态名称
	public int effectStatus;   //时效状态
	public int stockOutArea;   //源库地区
	public int stockInArea;   //目的库地区
	
	
	private ArrayList cargoInfoList = new ArrayList();//下架单列表显示用的
	
	private int exChnageId;
	
	private String productCode;//产品编号
	private String productOriName;//原名称
	private int inAreaStockCount;//目的库结存
	private int outAreaStockCount;//源库结存
	
	private int exchangeCount;//调拨量
//	/**
//	 * 状态：未处理
//	 */
//	public static final int STATUS0 = 0;
//	
//	/**
//	 * 状态：处理中
//	 */
//	public static final int STATUS1 = 1;
//	
//	/**
//	 * 状态：已确认 
//	 */
//	public static final int STATUS2 = 2;
//	
//	/**
//	 * 状态：审核通过
//	 */
//	public static final int STATUS3 = 3;
//
//	/**
//	 * 状态：无用
//	 */
//	public static final int STATUS4 = 4;
//	
//	/**
//	 * 状态：审核未通过
//	 */
//	public static final int STATUS5 = 5;
//	
//	/**
//	 * 状态：上架成功
//	 */
//	public static final int STATUS6 = 6;
//	
//	/**
//	 * 状态：上架失败
//	 */
//	public static final int STATUS7 = 7;
	
	/**
	 * 类型：上架单 
	 */
	public static final int TYPE0 = 0;
	
	/**
	 * 类型：下架单
	 */
	public static final int TYPE1 = 1;
	
	/**
	 * 类型：补货单
	 */
	public static final int TYPE2 = 2;
	
	/**
	 * 类型：货位间调拨单
	 */
	public static final int TYPE3 = 3;
	
	/**
	 * 类型：退货上架单
	 */
	public static final int TYPE4 = 4;
	
	
	/**
	 * 时效状态：进行中
	 */
	public static final int EFFECT_STATUS0 = 0;
	
	/**
	 * 时效状态：超出时效
	 */
	public static final int EFFECT_STATUS1 = 1;
	
	/**
	 * 时效状态：复核中
	 */
	public static final int EFFECT_STATUS2 = 2;
	
	/**
	 * 时效状态：作业成功
	 */
	public static final int EFFECT_STATUS3 = 3;
	
	/**
	 * 时效状态：作业失败
	 */
	public static final int EFFECT_STATUS4 = 4;
	
	public static HashMap typeMap = new LinkedHashMap();
	public static HashMap statusMap = new LinkedHashMap();
	public static HashMap effectStatusMap = new LinkedHashMap();
	static {
		typeMap.put(Integer.valueOf(TYPE0), "上架单");
		typeMap.put(Integer.valueOf(TYPE1), "下架单");
		typeMap.put(Integer.valueOf(TYPE2), "补货单");
		typeMap.put(Integer.valueOf(TYPE3), "货位间调拨单");
		typeMap.put(Integer.valueOf(TYPE4), "退货上架单");
		
		effectStatusMap.put(Integer.valueOf(EFFECT_STATUS0), "进行中");
		effectStatusMap.put(Integer.valueOf(EFFECT_STATUS1), "超出时效");
		effectStatusMap.put(Integer.valueOf(EFFECT_STATUS2), "待复核");
		effectStatusMap.put(Integer.valueOf(EFFECT_STATUS3), "作业成功");
		effectStatusMap.put(Integer.valueOf(EFFECT_STATUS4), "作业失败");
		
//		statusMap.put(Integer.valueOf(STATUS0), "未处理");
//		statusMap.put(Integer.valueOf(STATUS1), "处理中");
//		statusMap.put(Integer.valueOf(STATUS2), "已确认");
//		statusMap.put(Integer.valueOf(STATUS3), "审核通过");
//		statusMap.put(Integer.valueOf(STATUS5), "审核未通过");
//		statusMap.put(Integer.valueOf(STATUS6), "已完成");
//		statusMap.put(Integer.valueOf(STATUS7), "上架失败");
	}
	
	

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getLastOperateDatetime() {
		return lastOperateDatetime;
	}

	public void setLastOperateDatetime(String lastOperateDatetime) {
		this.lastOperateDatetime = lastOperateDatetime;
	}

	public String getTypeName(){
		String result = null;
		result = (String)typeMap.get(Integer.valueOf(this.type));
		if(result == null){
			result = "";
		}
		return result;
	}
	
//	public String getStatusName(){
//		String result = null;
//		result = (String)statusMap.get(Integer.valueOf(this.status));
//		if(result == null){
//			result = "";
//		}
//		return result;
//	}
	
	
	public int getId() {
		return id;
	}
	public String getStatusName() {
		return statusName;
	}

	public String getEffectTimeName() {
		effectTimeName = StringUtil.convertNull((String)effectStatusMap.get(Integer.valueOf(this.effectStatus)));
		return effectTimeName;
	}

	public void setEffectTimeName(String effectTimeName) {
		this.effectTimeName = effectTimeName;
	}

	public void setId(int id) {
		this.id = id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getConfirmDatetime() {
		return confirmDatetime;
	}
	public void setConfirmDatetime(String confirmDatetime) {
		this.confirmDatetime = confirmDatetime;
	}
	public int getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}
	public String getAuditingDatetime() {
		return auditingDatetime;
	}
	public void setAuditingDatetime(String auditingDatetime) {
		this.auditingDatetime = auditingDatetime;
	}
	public int getAuditingUserId() {
		return auditingUserId;
	}
	public void setAuditingUserId(int auditingUserId) {
		this.auditingUserId = auditingUserId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getStorageCode() {
		return storageCode;
	}
	public void setStorageCode(String storageCode) {
		this.storageCode = storageCode;
	}
	public int getStockInType() {
		return stockInType;
	}
	public void setStockInType(int stockInType) {
		this.stockInType = stockInType;
	}
	public int getStockOutType() {
		return stockOutType;
	}
	public void setStockOutType(int stockOutType) {
		this.stockOutType = stockOutType;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public String getAuditingUserName() {
		return auditingUserName;
	}
	public void setAuditingUserName(String auditingUserName) {
		this.auditingUserName = auditingUserName;
	}
	public String getConfirmUserName() {
		return confirmUserName;
	}
	public void setConfirmUserName(String confirmUserName) {
		this.confirmUserName = confirmUserName;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public ArrayList getCargoInfoList() {
		return cargoInfoList;
	}
	public void setCargoInfoList(ArrayList cargoInfoList) {
		this.cargoInfoList = cargoInfoList;
	}

	public String getCompleteDatetime() {
		return completeDatetime;
	}

	public void setCompleteDatetime(String completeDatetime) {
		this.completeDatetime = completeDatetime;
	}

	public int getCompleteUserId() {
		return completeUserId;
	}

	public void setCompleteUserId(int completeUserId) {
		this.completeUserId = completeUserId;
	}

	public String getCompleteUsername() {
		return completeUsername;
	}

	public void setCompleteUsername(String completeUsername) {
		this.completeUsername = completeUsername;
	}

	public int getExChnageId() {
		return exChnageId;
	}

	public void setExChnageId(int exChnageId) {
		this.exChnageId = exChnageId;
	}

	public int getPrintCount() {
		return printCount;
	}

	public void setPrintCount(int printCount) {
		this.printCount = printCount;
	}

	public int getEffectStatus() {
		return effectStatus;
	}

	public void setEffectStatus(int effectStatus) {
		this.effectStatus = effectStatus;
	}

	public int getStockOutArea() {
		return stockOutArea;
	}

	public void setStockOutArea(int stockOutArea) {
		this.stockOutArea = stockOutArea;
	}

	public int getStockInArea() {
		return stockInArea;
	}

	public void setStockInArea(int stockInArea) {
		this.stockInArea = stockInArea;
	}

	
	public boolean equals(Object otherObject) {
		if (this == otherObject)
			return true;
		if (otherObject == null)
			return false;
		if (getClass() != otherObject.getClass())
			return false;
		CargoOperationBean other = (CargoOperationBean) otherObject;
		return id==other.id;
	}

	public int hashCode() {
		int result = 17;
		int idValue = this.getId() == 0 ? 0 : Integer.valueOf(this.getId()).hashCode();
		result = result * 37 + idValue;
		return result;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductOriName() {
		return productOriName;
	}

	public void setProductOriName(String productOriName) {
		this.productOriName = productOriName;
	}

	public int getInAreaStockCount() {
		return inAreaStockCount;
	}

	public void setInAreaStockCount(int inAreaStockCount) {
		this.inAreaStockCount = inAreaStockCount;
	}

	public int getOutAreaStockCount() {
		return outAreaStockCount;
	}

	public void setOutAreaStockCount(int outAreaStockCount) {
		this.outAreaStockCount = outAreaStockCount;
	}

	public int getExchangeCount() {
		return exchangeCount;
	}

	public void setExchangeCount(int exchangeCount) {
		this.exchangeCount = exchangeCount;
	}

}
