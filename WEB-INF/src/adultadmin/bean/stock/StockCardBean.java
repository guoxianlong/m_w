/*
 * Created on 2009-7-16
 *
 */
package adultadmin.bean.stock;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import adultadmin.util.db.DbOperation;
import mmb.ware.WareService;

public strictfp class StockCardBean {

	/**
	 * 入库
	 */
	public static int STOCK_IN = 0;

	/**
	 * 出库
	 */
	public static int STOCK_OUT = 1;

	/**
	 * 其他
	 */
	public static int STOCK_OTHER = 2;

	/**
	 * 库存初始化
	 */
	public static int CARDTYPE_INIT = 0;
	/**
	 * StockCard来源——采购入库
	 */
	public static int CARDTYPE_BUYSTOCKIN = 1;
	/**
	 * StockCard来源——销售出库
	 */
	public static int CARDTYPE_ORDERSTOCK = 2;
	/**
	 * StockCard来源——调拨入库
	 */
	public static int CARDTYPE_STOCKEXCHANGEIN = 3;
	/**
	 * StockCard来源——调拨出库
	 */
	public static int CARDTYPE_STOCKEXCHANGEOUT = 4;
	/**
	 * StockCard来源——销售退货入库
	 */
	public static int CARDTYPE_CANCELORDERSTOCKIN = 5;
	/**
	 * StockCard来源——采购退货出库
	 */
	public static int CARDTYPE_CANCELBUYSTOCKIN = 6;
	/**
	 * StockCard来源——报损
	 */
	public static int CARDTYPE_LOSE = 7;
	/**
	 * StockCard来源——报溢
	 */
	public static int CARDTYPE_GET = 8;
	/**
	 * StockCard来源——其他入库
	 */
	public static int CARDTYPE_OTHERSIN = 9;
	/**
	 * StockCard来源——其他出库
	 */
	public static int CARDTYPE_OTHERSOUT = 10;

	/**
	 * StockCard来源——结算退货
	 */
	public static int CARDTYPE_BALANCERETURN = 11;

	/**
	 * StockCard来源——采购调价
	 */
	public static int CARDTYPE_STOCKBATCHPRICE = 12;
	/**
	 * StockCard来源——售后退货入库
	 */
	public static int CARDTYPE_AFTERSALESCANCELSTOCKIN = 13;

	/***
	 * StockCard来源——税点变更
	 */
	public static int CARDTYPE_TAX_POINT_CHANGE = 14;
	
	/**特殊销售（样品、残次品）出库*/
	public static final int CARDTYPE_SPECIALSALE = 15;

	/**
	 * StockCard来源——售后原品退回
	 */
	public static int CARDTYPE_AFTERSALE_OLDPRODUCT_RETURN = 36;

	/**
	 * StockCard来源——售后维修商品退回
	 */
	public static int CARDTYPE_AFTERSALE_REPAIRPRODUCT_RETURN = 16;
	/***
	 * StockCard来源——售后签收商品入库
	 */

	public static int CARDTYPE_AFTERSALESDETECT = 17;

	/**
	 * StockCard来源——原品返回
	 */
	public static int CARDTYPE_AFTERSALE_OLDPRODUCT_BACKUSER = 18;

	/**
	 * StockCard来源——维修寄回
	 */
	public static int CARDTYPE_AFTERSALE_REPAIRPRODUCT_BACKUSER = 19;
	
	/**
	 * StockCard来源——检测操作--配件入售后库
	 */
	public static int CARDTYPE_AFTERSALE_FITTING = 20;
	
	/**
	 * StockCard来源——检测操作--配件入客户库
	 */
	public static int CARDTYPE_CUSTOMER_FITTING = 21;

	/**
	 * StockCard来源——售后配件采购入库
	 */
	public static int CARDTYPE_AFTERSALE_FITTING_BUYSTOCKIN = 22;

	/**
	 * StockCard来源——配件领用售后配件入库
	 */
	public static int CARDTYPE_RECEIVE_AFTERSALE_IN_FITTING = 23;
		
	/**
	 * StockCard来源——配件领用客户配件入库
	 */
	public static int CARDTYPE_RECEIVE_CUSTOMER_IN_FITTING = 24;    
	
	/**
	 * StockCard来源——配件领用客户配件出库
	 */
	public static int CARDTYPE_RECEIVE_CUSTOMER_OUT_FITTING = 25;   

	/**
	 * StockCard来源——配件领用售后配件出库
	 */
	public static int CARDTYPE_RECEIVE_AFTERSALE_OUT_FITTING = 26;
	
	/**
	 * StockCard来源——配件寄回用户
	 */
	public static int CARDTYPE_FITTING_BACKUSER = 27;   
	
	/**
	 * StockCard来源——配件寄回用户未妥投
	 */
	public static int CARDTYPE_FITTING_BACKUSER_RETURN = 28;   
	
	/**
	 * StockCard来源——厂家维修更换商品出库
	 */
	public static int CARDTYPE_SUPPLIER_PRODUCT_REPLACE_OUT = 29;
    
	/**
	 * StockCard来源——厂家维修更换商品入库
	 */
	public static int CARDTYPE_SUPPLIER_PRODUCT_REPLACE_IN = 30;
	
	/**
	 * StockCard来源——售后配件维修返还入库
	 * @return
	 */
	public static int CARDTYPE_AFTERSALE_FITTING_REPAIR_RETURN = 31; 
	
	/**
	 * StockCard来源——备用机入库
	 */
	public static int CARDTYPE_SPARE_IN = 32;
	
	/**
	 * StockCard来源——备用机出库
	 */
	public static int CARDTYPE_SPARE_OUT = 33;
	
	/**
	 * StockCard来源——换新机寄回用户
	 */
	public static int CARDTYPE_ASFTERSALE_REPLACE_BACKUSER = 34;
	
	/**
	 * StockCard来源——售后维修换新机商品退回
	 */
	public static int CARDTYPE_AFTERSALE_REPLACE_RETURN = 35;

	public int id;

	/**
	 * 库类型
	 */
	public int stockType;

	/**
	 * 库地区
	 */
	public int stockArea;

	/**
	 * 库ID
	 */
	public int stockId;

	/**
	 * 单据号
	 */
	public String code;

	/**
	 * 来源
	 */
	public int cardType;

	/**
	 * 创建时间
	 */
	public String createDatetime;

	/**
	 * 入库数量
	 */
	public int stockInCount = 0;

	/**
	 * 入库金额
	 */
	public double stockInPriceSum = 0;

	/**
	 * 出库数量
	 */
	public int stockOutCount = 0;

	/**
	 * 出库金额
	 */
	public double stockOutPriceSum = 0;

	/**
	 * 本库类型结存
	 */
	public int stockAllType = 0;

	/**
	 * 本库地区结存
	 */
	public int stockAllArea = 0;

	/**
	 * 全库结存
	 */
	public int allStock = 0;

	/**
	 * 本库结存
	 */
	public int currentStock = 0;

	/**
	 * 库存单价
	 */
	public float stockPrice = 0;

	/**
	 * 结存总额
	 */
	public double allStockPriceSum;

	public int productId;

	private int allStockOutCount = 0;// 所有出库数量

	public String stockTypeName;
	public String stockAreaName;
	public String stockInPriceSumString;
	public String stockOutPriceSumString;
	public String allStockPriceSumString;

	/**
	 * 进销存卡片类型
	 */
	public static HashMap<Integer,String> stockCardTypeMap = new LinkedHashMap<Integer,String>();

	
	public static void setStockCardTypeMap(WareService wareService) {
		List<StockCardTypeBean> list = wareService.getStockCardTypeList();
		for( StockCardTypeBean bean : list ) {
			stockCardTypeMap.put(bean.getId(), bean.getName());
		}
	}
	
	static {
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		try{
			//加载所有的进销存卡片类型
			setStockCardTypeMap(wareService);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
	}
	
	public String getStockTypeName() {
		return stockTypeName;
	}

	public void setStockTypeName(String stockTypeName) {
		this.stockTypeName = stockTypeName;
	}

	public String getStockAreaName() {
		return stockAreaName;
	}

	public void setStockAreaName(String stockAreaName) {
		this.stockAreaName = stockAreaName;
	}

	public String getStockInPriceSumString() {
		return stockInPriceSumString;
	}

	public void setStockInPriceSumString(String stockInPriceSumString) {
		this.stockInPriceSumString = stockInPriceSumString;
	}

	public String getStockOutPriceSumString() {
		return stockOutPriceSumString;
	}

	public void setStockOutPriceSumString(String stockOutPriceSumString) {
		this.stockOutPriceSumString = stockOutPriceSumString;
	}

	public String getAllStockPriceSumString() {
		return allStockPriceSumString;
	}

	public void setAllStockPriceSumString(String allStockPriceSumString) {
		this.allStockPriceSumString = allStockPriceSumString;
	}

	public int getAllStock() {
		return allStock;
	}

	public void setAllStock(int allStock) {
		this.allStock = allStock;
	}

	public double getAllStockPriceSum() {
		return allStockPriceSum;
	}

	public void setAllStockPriceSum(double allStockPriceSum) {
		this.allStockPriceSum = allStockPriceSum;
	}

	public int getCardType() {
		return cardType;
	}

	public void setCardType(int cardType) {
		this.cardType = cardType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStockAllArea() {
		return stockAllArea;
	}

	public void setStockAllArea(int stockAllArea) {
		this.stockAllArea = stockAllArea;
	}

	public int getStockAllType() {
		return stockAllType;
	}

	public void setStockAllType(int stockAllType) {
		this.stockAllType = stockAllType;
	}

	public int getStockArea() {
		return stockArea;
	}

	public void setStockArea(int stockArea) {
		this.stockArea = stockArea;
	}

	public int getStockId() {
		return stockId;
	}

	public void setStockId(int stockId) {
		this.stockId = stockId;
	}

	public int getStockInCount() {
		return stockInCount;
	}

	public void setStockInCount(int stockInCount) {
		this.stockInCount = stockInCount;
	}

	public double getStockInPriceSum() {
		return stockInPriceSum;
	}

	public void setStockInPriceSum(double stockInPriceSum) {
		this.stockInPriceSum = stockInPriceSum;
	}

	public int getStockOutCount() {
		return stockOutCount;
	}

	public void setStockOutCount(int stockOutCount) {
		this.stockOutCount = stockOutCount;
	}

	public double getStockOutPriceSum() {
		return stockOutPriceSum;
	}

	public void setStockOutPriceSum(double stockOutPriceSum) {
		this.stockOutPriceSum = stockOutPriceSum;
	}

	public float getStockPrice() {
		return stockPrice;
	}

	public void setStockPrice(float stockPrice) {
		this.stockPrice = stockPrice;
	}

	public int getStockType() {
		return stockType;
	}

	public void setStockType(int stockType) {
		this.stockType = stockType;
	}

	public int getCurrentStock() {
		return currentStock;
	}

	public void setCurrentStock(int currentStock) {
		this.currentStock = currentStock;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getCardTypeName() {
		String typeName = StockCardBean.stockCardTypeMap.get(this.cardType);
		if( typeName == null ) {
			typeName = "-";
		}
		return typeName;
	}

	public int getStockOperationType() {
		int stockOperationType = -1;
		switch (this.cardType) {
		case 1:
			stockOperationType = STOCK_IN;
			break;
		case 2:
			stockOperationType = STOCK_OUT;
			break;
		case 3:
			stockOperationType = STOCK_IN;
			break;
		case 4:
			stockOperationType = STOCK_OUT;
			break;
		case 5:
			stockOperationType = STOCK_IN;
			break;
		case 6:
			stockOperationType = STOCK_OUT;
			break;
		case 7:
			stockOperationType = -1;
			break;
		case 8:
			stockOperationType = -1;
			break;
		case 9:
			stockOperationType = STOCK_IN;
			break;
		case 10:
			stockOperationType = STOCK_OUT;
			break;
		case 0:
			stockOperationType = -1;
			break;
		case 11:
			stockOperationType = STOCK_IN;
			break;
		}
		return stockOperationType;
	}

	public int getAllStockOutCount() {
		return allStockOutCount;
	}

	public void setAllStockOutCount(int allStockOutCount) {
		this.allStockOutCount = allStockOutCount;
	}

	@Override
	public String toString() {
		return "StockCardBean [id=" + id + ", stockType=" + stockType
				+ ", stockArea=" + stockArea + ", stockId=" + stockId
				+ ", code=" + code + ", cardType=" + cardType
				+ ", createDatetime=" + createDatetime + ", stockInCount="
				+ stockInCount + ", stockInPriceSum=" + stockInPriceSum
				+ ", stockOutCount=" + stockOutCount + ", stockOutPriceSum="
				+ stockOutPriceSum + ", stockAllType=" + stockAllType
				+ ", stockAllArea=" + stockAllArea + ", allStock=" + allStock
				+ ", currentStock=" + currentStock + ", stockPrice="
				+ stockPrice + ", allStockPriceSum=" + allStockPriceSum
				+ ", productId=" + productId + ", allStockOutCount="
				+ allStockOutCount + ", stockTypeName=" + stockTypeName
				+ ", stockAreaName=" + stockAreaName
				+ ", stockInPriceSumString=" + stockInPriceSumString
				+ ", stockOutPriceSumString=" + stockOutPriceSumString
				+ ", allStockPriceSumString=" + allStockPriceSumString + "]";
	}
}
