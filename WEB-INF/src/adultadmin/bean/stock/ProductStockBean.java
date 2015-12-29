/*
 * Created on 2007-11-14
 *
 */
package adultadmin.bean.stock;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mmb.ware.WareService;
import adultadmin.action.vo.voProduct;
import adultadmin.util.db.DbOperation;

/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2009-4-28
 * 
 * 说明：产品库存、分地区、分库存种类
 */
public class ProductStockBean {

	/**
	 * 北库
	 */
	public static int AREA_BJ = 0;
	/**
	 * 芳村
	 */
	public static int AREA_GF = 1;
	/**
	 * 广速
	 */
	public static int AREA_GS = 2;
	/**
	 * 增城
	 */
	public static int AREA_ZC = 3;
	/**
	 * 无锡
	 */
	public static int AREA_WX = 4;
	/**
	 * 京东
	 */
	public static int AREA_JD = 5;
	/**
	 * 深圳
	 */
	public static int AREA_SZ = 7;
	/**
	 * 西安
	 */
	public static int AREA_XA = 8;
	
	/**
	 * 成都
	 */
	public static int AREA_CD = 9;
	
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
	 *备用机库(虚拟库)
	 */
	public static int STOCKTYPE_SPARE = 13;
	
	/**
	 * 库存状态：正常状态
	 */
	public static int STOCKSTATUS_NORMAL = 0;

	/**
	 * 库存状态：隐藏状态
	 */
	public static int STOCKSTATUS_HIDE = 1;


	public int id;

	public int productId;

	public int stock;

	public int lockCount;

	public int area;

	public int type;

	public int status;
	
	public int uncheck;
	
	public int qulifyCount;
	
	public int unqulifyCount;
	
	public String areaName ; //仓库名称

	
	private int allStock;//总结存数量
	
	public String whole_code;
	
	public String getWhole_code() {
		return whole_code;
	}

	public void setWhole_code(String whole_code) {
		this.whole_code = whole_code;
	}

	public int getUncheck() {
		return uncheck;
	}

	public void setUncheck(int uncheck) {
		this.uncheck = uncheck;
	}

	public int getQulifyCount() {
		return qulifyCount;
	}

	public void setQulifyCount(int qulifyCount) {
		this.qulifyCount = qulifyCount;
	}

	public int getUnqulifyCount() {
		return unqulifyCount;
	}

	public void setUnqulifyCount(int unqulifyCount) {
		this.unqulifyCount = unqulifyCount;
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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
	public voProduct product;

	public voProduct getProduct() {
		return product;
	}

	public void setProduct(voProduct product) {
		this.product = product;
	}

	public int getAllStock() {
		return allStock;
	}

	public void setAllStock(int allStock) {
		this.allStock = allStock;
	}
	
	/**
	 * 库地区列表
	 */
	public static HashMap<Integer,String> areaMap = new LinkedHashMap<Integer,String>();
	/**
	 * 可发货库地区列表
	 */
	public static HashMap<Integer,String> stockoutAvailableAreaMap = new LinkedHashMap<Integer,String>();
	
	public static Map<Integer,String> getAreaMap() {
		return areaMap;
	}
	public static void setAreaMap(WareService wareService) {
		List<StockAreaBean> list = wareService.getStockAreaList();
		for(StockAreaBean bean :list){
			areaMap.put(bean.getId(), bean.getName());
		}
	}
	public static void setStockoutAvailableAreaMap(WareService wareService) {
		List<StockAreaBean> list = wareService.getStockoutAvailableAreaList();
		for( StockAreaBean bean : list ) {
			stockoutAvailableAreaMap.put(bean.getId(), bean.getName());
		}
	}
	public static String getAreaName(int area){
		String result = null;
		result = (String)areaMap.get(Integer.valueOf(area));
		if(result == null){
			result = "";
		}
		return result;
	}
	
	/**
	 * mdc库地区列表
	 */
	public static HashMap<Integer,String> mdcAreaMap = new LinkedHashMap<Integer,String>();
	
	public static Map<Integer,String> getMdcAreaMap() {
		return mdcAreaMap;
	}
	public static void setMdcAreaMap(WareService wareService) {
		List<StockAreaBean> list = wareService.getMdcStockAreaList();
		for(StockAreaBean bean :list){
			mdcAreaMap.put(bean.getId(), bean.getName());
		}
	}
	
	/**
	 * 库类型类表
	 */
	public static HashMap<Integer,String> stockTypeMap = new LinkedHashMap<Integer,String>();
	public static Map<Integer,String> getStockTypeMap() {
		return stockTypeMap;
	}
	public static void setStockTypeMap(WareService wareService) {
		List<StockTypeBean> list = wareService.getStockTypeList();
		for(StockTypeBean bean :list){
			stockTypeMap.put(bean.getId(), bean.getName());
		}
	}
	public static String getStockTypeName(int type){
		String result = null;
		result = (String)stockTypeMap.get(Integer.valueOf(type));
		if(result == null){
			result = "";
		}
		return result;
	}

	
	/**
	 * 库类型--所有的地区
	 */
	public static Map<Integer,List<StockAreaBean>> typeToAreaMap = new HashMap<Integer, List<StockAreaBean>>();
	public static Map<Integer,List<StockAreaBean>> getTypeToAreaMap() {
		return typeToAreaMap;
	}
	public static void setTypeToAreaMap(WareService wareService) {
		typeToAreaMap = wareService.getTypeToAreaMap();
	}
	/**
	 * 获取所属的库地区列表
	 * @param typeId
	 * @return
	 */
	public static List<StockAreaBean> getStockAreaByType(int typeId){
		return typeToAreaMap.get(typeId);
	}
	
	/**
	 * 库地区--所有的类型
	 */
	public static Map<Integer,List<StockTypeBean>> areaToTypeMap = new HashMap<Integer, List<StockTypeBean>>();
	public static Map<Integer,List<StockTypeBean>> getAreaToTypeMap() {
		return areaToTypeMap;
	}
	public static void setAreaToTypeMap(WareService wareService) {
		areaToTypeMap = wareService.getAreaToTypeMap();
	}
	/**
	 *	获取所有的库类型 列表
	 */
	public static List<StockTypeBean> getStockTypeByArea(int areaId){
		return areaToTypeMap.get(areaId);
	}
	
	public static void  initStockAreaTypeCacheAll() {
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_WARE);
		WareService wareService = new WareService(db);
		try{
			//刷新库地区
			setAreaMap(wareService);
			//刷新库类型
			setStockTypeMap(wareService);
			//刷新库类型对应所有的库地区
			setTypeToAreaMap(wareService);
			//刷新库地区对应所有的库类型
			setAreaToTypeMap(wareService);
			//加载发货地区的Map
			setStockoutAvailableAreaMap(wareService);
			//加载mdc地区
			setMdcAreaMap(wareService);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
	}
	
	static {
		ProductStockBean.initStockAreaTypeCacheAll();
	}
	
	/**
	 * 合格库下的仓库
	 */
	public static Integer[] AREA_QUALIFIED = new Integer[]{AREA_BJ,AREA_GF,AREA_GS,AREA_ZC,AREA_WX};
	
	/**
	 * 待检库下的仓库
	 */
	public static Integer[] AREA_CHECK = new Integer[]{AREA_BJ,AREA_GF,AREA_ZC,AREA_WX};
	
	/**
	 * 维修库下的仓库
	 */
	public static Integer[] AREA_REPAIR = new Integer[]{AREA_BJ,AREA_GF,AREA_ZC};
	
	/**
	 * 返厂库下的仓库
	 */
	public static Integer[] AREA_BACK = new Integer[]{AREA_BJ,AREA_GF,AREA_ZC,AREA_WX};
	
	/**
	 * 退货库下的仓库
	 */
	public static Integer[] AREA_RETURN = new Integer[]{AREA_BJ,AREA_GF,AREA_GS,AREA_ZC,AREA_WX};
	
	/**
	 * 残次品库下的仓库
	 */
	public static Integer[] AREA_DEFECTIVE = new Integer[]{AREA_BJ,AREA_GF,AREA_GS,AREA_ZC};
	
	/**
	 * 样品库下的仓库
	 */
	public static Integer[] AREA_SAMPLE = new Integer[]{AREA_BJ,AREA_GF,AREA_ZC,AREA_WX};
	
	/**
	 * 售后库下的仓库
	 */
	public static Integer[] AREA_AFTER_SALE = new Integer[]{AREA_GF};
	
	public static HashMap stockStatusMap = new HashMap();
	static {
		stockStatusMap.put(Integer.valueOf(STOCKSTATUS_NORMAL), "正常");
		stockStatusMap.put(Integer.valueOf(STOCKSTATUS_HIDE), "隐藏");
	}
	
	public static String getStockStatusName(int status){
		String result = null;
		result = (String)stockStatusMap.get(Integer.valueOf(status));
		if(result == null){
			result = "";
		}
		return result;
	}
	
	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
}


