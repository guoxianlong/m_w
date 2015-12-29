package adultadmin.bean.cargo;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class CargoInfoShelfBean {
	public int id;
	public String code;
	public String wholeCode;
	public int stockType;
	public int floorCount;
	public int stockAreaId;
	public int storageId;
	public int areaId;
	public int cityId;
	public int passageId;//巷道Id
	/**
	 * 合格库
	 */
	public static int STOCKTYPE_QUALIFIED = 0;
	/**
	 * 待检库
	 */
	public static int STOCKTYPE_CHECK = 1;
	/**
	 * 维修库
	 */
	public static int STOCKTYPE_REPAIR = 2;
	/**
	 * 返厂库
	 */
	public static int STOCKTYPE_BACK = 3;
	/**
	 * 退货库
	 */
	public static int STOCKTYPE_RETURN = 4;
	/**
	 * 残次品库
	 */
	public static int STOCKTYPE_DEFECTIVE = 5;
	/**
	 * 样品库
	 */
	public static int STOCKTYPE_SAMPLE = 6;
	/**
	 * 质检库(虚拟库)
	 */
	public static int STOCKTYPE_QUALITYTESTING = 7;
	/**
	 * 换货库(虚拟库)
	 */
	public static int STOCKTYPE_NIFFER = 8;
	/**
	 * 售后库
	 */
	public static int STOCKTYPE_AFTER_SALE = 9;
	/**
	 * 客户库
	 */
	public static int STOCKTYPE_CUSTOMER = 10;
	
	
	public static HashMap stockTypeMap = new LinkedHashMap();
	static {
		stockTypeMap.put(Integer.valueOf(STOCKTYPE_QUALIFIED), "合格库");
		stockTypeMap.put(Integer.valueOf(STOCKTYPE_CHECK), "待验库");
		stockTypeMap.put(Integer.valueOf(STOCKTYPE_REPAIR), "维修库");
		stockTypeMap.put(Integer.valueOf(STOCKTYPE_BACK), "返厂库");
		stockTypeMap.put(Integer.valueOf(STOCKTYPE_RETURN), "退货库");
		stockTypeMap.put(Integer.valueOf(STOCKTYPE_DEFECTIVE), "残次品库");
		stockTypeMap.put(Integer.valueOf(STOCKTYPE_SAMPLE), "样品库");
		stockTypeMap.put(Integer.valueOf(STOCKTYPE_QUALITYTESTING), "质检库");
		stockTypeMap.put(Integer.valueOf(STOCKTYPE_NIFFER), "换货库");
		stockTypeMap.put(Integer.valueOf(STOCKTYPE_AFTER_SALE), "售后库");
		stockTypeMap.put(Integer.valueOf(STOCKTYPE_CUSTOMER), "客户库");
	}
	public String getStockTypeName(){
		String result = null;
		result = (String)stockTypeMap.get(Integer.valueOf(this.stockType));
		if(result == null){
			result = "";
		}
		return result;
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
	public String getWholeCode() {
		return wholeCode;
	}
	public void setWholeCode(String wholeCode) {
		this.wholeCode = wholeCode;
	}
	public int getStockType() {
		return stockType;
	}
	public void setStockType(int stockType) {
		this.stockType = stockType;
	}
	public int getFloorCount() {
		return floorCount;
	}
	public void setFloorCount(int floorCount) {
		this.floorCount = floorCount;
	}
	public int getStockAreaId() {
		return stockAreaId;
	}
	public void setStockAreaId(int stockAreaId) {
		this.stockAreaId = stockAreaId;
	}
	public int getStorageId() {
		return storageId;
	}
	public void setStorageId(int storageId) {
		this.storageId = storageId;
	}
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	public int getPassageId() {
		return passageId;
	}
	public void setPassageId(int passageId) {
		this.passageId = passageId;
	}
	
}
