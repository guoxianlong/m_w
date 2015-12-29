package adultadmin.bean.cargo;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class CargoInfoStockAreaBean {
	public int id;
	public String code;
	public String wholeCode;
	public String name;
	public int stockType;
	public int storageId;
	public int areaId;
	public int cityId;
	
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
	
	/**
	 * 配件售后库
	 */
	public static int STOCKTYPE_AFTER_SALE_FIITING = 11;
	/**
	 * 配件客户库
	 */
	public static int STOCKTYPE_CUSTOMER_FITTING = 12;
	
	/**
	 * 备用机库
	 */
	public static int STOCKTYPE_SPARE = 13;
	
	
	
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
		stockTypeMap.put(Integer.valueOf(STOCKTYPE_AFTER_SALE_FIITING), "配件售后库");
		stockTypeMap.put(Integer.valueOf(STOCKTYPE_CUSTOMER_FITTING), "配件客户库");	
		stockTypeMap.put(Integer.valueOf(STOCKTYPE_SPARE), "备用机库");
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getStockType() {
		return stockType;
	}
	public void setStockType(int stockType) {
		this.stockType = stockType;
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
	
}
