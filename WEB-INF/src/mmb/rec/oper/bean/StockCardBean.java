package mmb.rec.oper.bean;

public class StockCardBean {
    private int id;

    private int stockType;

    private int stockArea;

    private int stockId;

    private String code;

    private int cardType;

    private String createDatetime;

    private int stockInCount;

    private int stockOutCount;

    private double stockInPriceSum;

    private double stockOutPriceSum;

    private int stockAllType;

    private int stockAllArea;

    private int currentStock;

    private int allStock;

    private float stockPrice;

    private double allStockPriceSum;

    private int productId;

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

	/**
	 * StockCard来源——售后原品退回
	 */
	public static int CARDTYPE_AFTERSALE_OLDPRODUCT_RETURN = 15;

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
	 * StockCard来源——备用机入库
	 */
	public static int CARDTYPE_SPARE_IN = 32;
	
	/**
	 * StockCard来源——备用机出库
	 */
	public static int CARDTYPE_SPARE_OUT = 33;
	
	/**
	 * StockCard来源——换新机寄回
	 */
	public static int CARDTYPE_ASFTERSALE_REPLACE_BACKUSER = 34;
	/**
	 * StockCard来源——售后维修换新机商品退回
	 */
	public static int CARDTYPE_AFTERSALE_REPLACE_RETURN = 35;
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStockType() {
        return stockType;
    }

    public void setStockType(int stockType) {
        this.stockType = stockType;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
    }

    public int getStockInCount() {
        return stockInCount;
    }

    public void setStockInCount(int stockInCount) {
        this.stockInCount = stockInCount;
    }

    public int getStockOutCount() {
        return stockOutCount;
    }

    public void setStockOutCount(int stockOutCount) {
        this.stockOutCount = stockOutCount;
    }
    
    public double getStockInPriceSum() {
        return stockInPriceSum;
    }

    public void setStockInPriceSum(double stockInPriceSum) {
        this.stockInPriceSum = stockInPriceSum;
    }

    public double getStockOutPriceSum() {
        return stockOutPriceSum;
    }

    public void setStockOutPriceSum(double stockOutPriceSum) {
        this.stockOutPriceSum = stockOutPriceSum;
    }

    public int getStockAllType() {
        return stockAllType;
    }

    public void setStockAllType(int stockAllType) {
        this.stockAllType = stockAllType;
    }

    public int getStockAllArea() {
        return stockAllArea;
    }

    public void setStockAllArea(int stockAllArea) {
        this.stockAllArea = stockAllArea;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getAllStock() {
        return allStock;
    }

    public void setAllStock(int allStock) {
        this.allStock = allStock;
    }

    public float getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(float stockPrice) {
        this.stockPrice = stockPrice;
    }

    public double getAllStockPriceSum() {
        return allStockPriceSum;
    }

    public void setAllStockPriceSum(double allStockPriceSum) {
        this.allStockPriceSum = allStockPriceSum;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public String getCardTypeName() {
		String typeName = "-";
		switch (this.cardType) {
		case 1:
			typeName = "采购入库";
			break;
		case 2:
			typeName = "销售出库";
			break;
		case 3:
			typeName = "调拨入库";
			break;
		case 4:
			typeName = "调拨出库";
			break;
		case 5:
			typeName = "销售退货入库";
			break;
		case 6:
			typeName = "采购退货出库";
			break;
		case 7:
			typeName = "报损";
			break;
		case 8:
			typeName = "报溢";
			break;
		case 9:
			typeName = "其它入库";
			break;
		case 10:
			typeName = "其它出库";
			break;
		case 0:
			typeName = "库存初始化";
			break;
		case 11:
			typeName = "结算退货";
			break;
		case 12:
			typeName = "采购调价";
			break;
		case 13:
			typeName = "售后退货入库";
			break;
		case 14:
			typeName = "税点变更";
			break;
		case 15:
			typeName = "售后原品退回";
			break;
		case 16:
			typeName = "售后维修商品退回";
			break;
		case 17:
			typeName = "售后签收商品入库";
			break;
		case 18:
			typeName = "原品返回";
			break;
		case 19:
			typeName = "维修寄回";
			break;
		case 20:
			typeName = "检测操作--配件入售后库";
			break;
		case 21:
			typeName = "检测操作--配件入客户库";
			break;		
		case 22:
			typeName = "售后配件采购入库";
			break;
		case 23:
			typeName = "配件领用售后配件入库";
			break;
		case 24:
			typeName = "配件领用客户配件入库";
			break;
		case 25:
			typeName = "配件领用客户配件出库";
			break;
		case 26:
			typeName = "配件领用售后配件出库";
			break;
		case 27:
			typeName = "配件寄回用户";
			break;
		case 28:
			typeName = "配件寄回用户未妥投";
			break;
		}
		return typeName;
	}
}