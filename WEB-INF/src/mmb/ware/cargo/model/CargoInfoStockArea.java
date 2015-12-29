package mmb.ware.cargo.model;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class CargoInfoStockArea {
    private Integer id;

    private String code;

    private String wholeCode;

    private String name;

    private Integer stockType;

    private Integer storageId;

    private Integer areaId;

    private Integer cityId;

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
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getWholeCode() {
        return wholeCode;
    }

    public void setWholeCode(String wholeCode) {
        this.wholeCode = wholeCode == null ? null : wholeCode.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getStockType() {
        return stockType;
    }

    public void setStockType(Integer stockType) {
        this.stockType = stockType;
    }

    public Integer getStorageId() {
        return storageId;
    }

    public void setStorageId(Integer storageId) {
        this.storageId = storageId;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }
}