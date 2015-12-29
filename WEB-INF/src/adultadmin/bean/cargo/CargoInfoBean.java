package adultadmin.bean.cargo;


import java.util.List;


import java.util.HashMap;
import java.util.LinkedHashMap;

import adultadmin.action.vo.voProductLine;

public class CargoInfoBean {
	public int id;             //ID
	public String code;        //编号
	public String wholeCode;   //货位完整编号
	public int storeType;      //存放类型
	public int maxStockCount;  //货位最大容量
	public int warnStockCount; //货位警戒线
	public int spaceLockCount; //货位空间锁定量
	public int productLineId;  //产品线ID
	public int type;           //货位类型
	public int length;         //长度
	public int width;          //宽度
	public int high;           //高度
	public int status;         //状态
	public int shelfId;        //货架ID
	public int passageId;      //巷道ID
	public int stockAreaId;    //仓库区域ID
	public int storageId;      //仓库ID
	public int areaId;        //地区ID
	public int cityId;         //城市ID
	public String remark;      //备注
	public int floorNum;       //层号
	public int stockType;     //库类别
	public voProductLine productLine;  //产品线
	public CargoInfoAreaBean cargoArea; //地区
	
	public long cargoVolume;  // 货位的体积 cm 3次方
	public long leftVolume; //货位的剩余体积 cm 3次方
	
	private int skucount;//临时属性 不对应数据库中表的字段  用于保存成都仓中所属区域存储sku数
	
	public int getSkucount() {
		return skucount;
	}

	public void setSkucount(int skucount) {
		this.skucount = skucount;
	}

	/**
	 * 存放类型：散件区
	 */
	public static final int STORE_TYPE0 = 0;
	
	/**
	 * 存放类型：整件区
	 */
	public static final int STORE_TYPE1 = 1;
	
	/**
	 * 存放类型：缓存区
	 */
	public static final int STORE_TYPE2 = 2;
	
	/**
	 * 存放类型：散件区或缓存区(特殊，仅用于上架单)
	 */
	public static final int STORE_TYPE3 = 3;
	
	/**
	 * 存放类型：混合区
	 */
	public static final int STORE_TYPE4 = 4;

	/**
	 * 存放类型：作业区
	 */
	public static final int STORE_TYPE5 = 5;

	/**
	 * 存放类型：包裹区
	 */
	public static final int STORE_TYPE6 = 6;
	
	/**
	 * 存放类型：下架区
	 */
	public static final int STORE_TYPE7 = 7;
	
	/**
	 * 类型：普通 
	 */
	public static final int TYPE0 = 0;
	
	/**
	 * 类型：热销 
	 */
	public static final int TYPE1 = 1;
	
	/**
	 * 类型：滞销 
	 */
	public static final int TYPE2 = 2;
	/**
	 * 类型 完好
	 */
	public static final int TYPE3 = 3;
	/**
	 * 类型 保修机残次
	 */
	public static final int TYPE4 = 4;
	
	/**
	 * 非保修机残次
	 */
	public static final int TYPE5 = 5;
	
	
	/**
	 * 状态：使用中 
	 */
	public static final int STATUS0 = 0;
	
	/**
	 * 状态：未使用
	 */
	public static final int STATUS1 = 1;
	
	/**
	 * 状态：未开通 
	 */
	public static final int STATUS2 = 2;
	
	/**
	 * 状态：已删除 
	 */
	public static final int STATUS3 = 3;
	
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
	public static HashMap typeMap = new LinkedHashMap();
	public static HashMap statusMap = new LinkedHashMap();
	public static HashMap storeTypeMap = new LinkedHashMap();
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
		
		typeMap.put(Integer.valueOf(TYPE0), "普通");
		typeMap.put(Integer.valueOf(TYPE1), "热销");
		typeMap.put(Integer.valueOf(TYPE2), "滞销");
		typeMap.put(Integer.valueOf(TYPE3), "完好");
		typeMap.put(Integer.valueOf(TYPE4), "保修机残次");
		typeMap.put(Integer.valueOf(TYPE5), "非保修机残次");
		
		statusMap.put(Integer.valueOf(STATUS0), "使用中");
		statusMap.put(Integer.valueOf(STATUS1), "未使用");
		statusMap.put(Integer.valueOf(STATUS2), "未开通");
		statusMap.put(Integer.valueOf(STATUS3), "已删除");
		
		storeTypeMap.put(Integer.valueOf(STORE_TYPE0), "散件区");
		storeTypeMap.put(Integer.valueOf(STORE_TYPE1), "整件区");
		storeTypeMap.put(Integer.valueOf(STORE_TYPE2), "缓存区");
		storeTypeMap.put(Integer.valueOf(STORE_TYPE3), "散件区或整件区");
		storeTypeMap.put(Integer.valueOf(STORE_TYPE4), "混合区");
		storeTypeMap.put(Integer.valueOf(STORE_TYPE5), "作业区");
		storeTypeMap.put(Integer.valueOf(STORE_TYPE6), "包裹区");
		storeTypeMap.put(Integer.valueOf(STORE_TYPE7), "下架区");
	}
	
	public String getStockTypeName(){
		String result = null;
		result = (String)stockTypeMap.get(Integer.valueOf(this.stockType));
		if(result == null){
			result = "";
		}
		return result;
	}
	
	public String getTypeName(){
		String result = null;
		result = (String)typeMap.get(Integer.valueOf(this.type));
		if(result == null){
			result = "";
		}
		return result;
	}
	
	public String getStatusName(){
		String result = null;
		result = (String)statusMap.get(Integer.valueOf(this.status));
		if(result == null){
			result = "";
		}
		return result;
	}
	
	public String getStoreTypeName(){
		String result = null;
		result = (String)storeTypeMap.get(Integer.valueOf(this.storeType));
		if(result == null){
			result = "";
		}
		return result;
	}
	
	public List cargoPSList;  //货位产品库存信息
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
	public int getStoreType() {
		return storeType;
	}
	public void setStoreType(int storeType) {
		this.storeType = storeType;
	}
	public int getMaxStockCount() {
		return maxStockCount;
	}
	public void setMaxStockCount(int maxStockCount) {
		this.maxStockCount = maxStockCount;
	}
	public int getWarnStockCount() {
		return warnStockCount;
	}
	public void setWarnStockCount(int warnStockCount) {
		this.warnStockCount = warnStockCount;
	}
	public int getSpaceLockCount() {
		return spaceLockCount;
	}
	public void setSpaceLockCount(int spaceLockCount) {
		this.spaceLockCount = spaceLockCount;
	}
	public int getProductLineId() {
		return productLineId;
	}
	public void setProductLineId(int productLineId) {
		this.productLineId = productLineId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHigh() {
		return high;
	}
	public void setHigh(int high) {
		this.high = high;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getShelfId() {
		return shelfId;
	}
	public void setShelfId(int shelfId) {
		this.shelfId = shelfId;
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getFloorNum() {
		return floorNum;
	}
	public void setFloorNum(int floorNum) {
		this.floorNum = floorNum;
	}
	public int getStockType() {
		return stockType;
	}
	public void setStockType(int stockType) {
		this.stockType = stockType;
	}

	public List getCargoPSList() {
		return cargoPSList;
	}
	public void setCargoPSList(List cargoPSList) {
		this.cargoPSList = cargoPSList;
	}

	public voProductLine getProductLine() {
		return productLine;
	}
	public void setProductLine(voProductLine productLine) {
		this.productLine = productLine;
	}

	public CargoInfoAreaBean getCargoArea() {
		return cargoArea;
	}

	public void setCargoArea(CargoInfoAreaBean cargoArea) {
		this.cargoArea = cargoArea;
	}

	public int getPassageId() {
		return passageId;
	}

	public void setPassageId(int passageId) {
		this.passageId = passageId;
	}

	public long getCargoVolume() {
		return cargoVolume;
	}

	public void setCargoVolume(long cargoVolume) {
		this.cargoVolume = cargoVolume;
	}

	public long getLeftVolume() {
		return leftVolume;
	}

	public void setLeftVolume(long leftVolume) {
		this.leftVolume = leftVolume;
	}
	
	public long calculateVolume() {
		return this.length * this.width * this.high;
	}

	
}
