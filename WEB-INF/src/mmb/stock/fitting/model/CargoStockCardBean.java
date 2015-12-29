package mmb.stock.fitting.model;


public class CargoStockCardBean {
    private int id;

    private int stockType;

    private int stockArea;

    private int stockId;

    private String code;

    private int cardType;

    private String createDatetime;

    private int stockInCount;

    private double stockInPriceSum;

    private int stockOutCount;

    private double stockOutPriceSum;

    private String cargoWholeCode;

    private int cargoStoreType;

    private int currentStock;

    private int allStock;

    private float stockPrice;

    private double allStockPriceSum;

    private int productId;

    private int currentCargoStock;

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

	/**
	 * StockCard来源——上架入库
	 */
	public static int CARDTYPE_UPSHELFSTOCKIN = 14;

	/**
	 * StockCard来源——上架出库
	 */
	public static int CARDTYPE_UPSHELFSTOCKOUT = 15;

	/**
	 * StockCard来源——货位间调拨入库
	 */
	public static int CARDTYPE_CARGOEXCHAGESTOCKIN = 16;

	/**
	 * StockCard来源——货位间调拨出库
	 */
	public static int CARDTYPE_CARGOEXCHAGESTOCKOUT = 17;

	/**
	 * StockCard来源——下架入库
	 */
	public static int CARDTYPE_DOWNSHELFSTOCKIN = 18;

	/**
	 * StockCard来源——下架出库
	 */
	public static int CARDTYPE_DOWNSHELFSTOCKOUT = 19;

	/**
	 * StockCard来源——补货入库
	 */
	public static int CARDTYPE_REFILLSTOCKIN = 20;

	/**
	 * StockCard来源——补货出库
	 */
	public static int CARDTYPE_REFILLSTOCKOUT = 21;
	/**
	 * StockCard来源——分拣出库
	 */
	public static int CARDTYPE_PDASORTINGOUT = 26;

	/**
	 * StockCard来源——分拣入库
	 */
	public static int CARDTYPE_PDASORTINGIN = 27;
	/**
	 * StockCard来源——异常商品入库
	 */
	public static int CARDTYPE_EXECINPRODUCT = 28;

	/**
	 * StockCard来源——异常商品出库
	 */
	public static int CARDTYPE_EXECOUTPRODUCT = 29;

	/**
	 * StockCard来源——售后原品返回
	 */
	public static int CARDTYPE_AFTERSALE_OLDPRODUCT_RETURN = 30;

	/**
	 * StockCard来源——售后维修商品退回
	 */
	public static int CARDTYPE_AFTERSALE_REPAIRPRODUCT_RETURN = 31;

	/**
	 * StockCard来源——售后签收商品入库
	 */
	public static int CARDTYPE_AFTERSALESDETECT = 32;

	/**
	 * StockCard来源——售后库商品上架
	 */
	public static int CARDTYPE_UPSHELF_FOR_AFTERSALE = 33;

	/**
	 * StockCard来源——用户库商品上架
	 */
	public static int CARDTYPE_UPSHELF_FOR_USER = 34;

	/**
	 * StockCard来源——原品返回
	 */
	public static int CARDTYPE_AFTERSALE_OLDPRODUCT_BACKUSER = 35;

	/**
	 * StockCard来源——维修寄回
	 */
	public static int CARDTYPE_AFTERSALE_REPAIRPRODUCT_BACKUSER = 36;
	
	/**
	 * StockCard来源——售后商品出库
	 */
	public static int CARDTYPE_AFTERSALESDETECT_OUT = 37;

	/**
	 * StockCard来源——检测操作--配件入售后库
	 */
	public static int CARDTYPE_AFTERSALE_FITTING = 38;
	
	/**
	 * StockCard来源——检测操作--配件入客户库
	 */
	public static int CARDTYPE_CUSTOMER_FITTING = 39;

	/**
	 *  StockCard来源——售后配件采购入库
	 */
	public static int CARDTYPE_AFTERSALE_FITTING_BUY_STOCKIN = 40;
	
	/**
	 * StockCard来源——配件领用售后配件出库
	 */
	public static int CARDTYPE_RECEIVE_AFTERSALE_OUT_FITTING = 41;
	
	/**
	 * StockCard来源——配件领用售后配件入库
	 */
	public static int CARDTYPE_RECEIVE_AFTERSALE_IN_FITTING = 42;
		
	/**
	 * StockCard来源——配件领用客户配件入库
	 */
	public static int CARDTYPE_RECEIVE_CUSTOMER_IN_FITTING = 43;    
	
	/**
	 * StockCard来源——配件领用客户配件出库
	 */
	public static int CARDTYPE_RECEIVE_CUSTOMER_OUT_FITTING = 44;    

	/**
	 * StockCard来源——客户配件库货位更换
	 */
	public static int CARDTYPE_AFTERSALE_FITTING_CHANGE_CARGO = 45;
	
	/**
	 * StockCard来源——售后配件库货位更换
	 */
	public static int CARDTYPE_CUSTOM_FITTING_CHANGE_CARGO = 46;
	
	/**
	 * StockCard来源——配件寄回用户
	 */
	public static int CARDTYPE_FITTING_BACKUSER = 47;   
	
	/**
	 * StockCard来源——配件寄回用户未妥投
	 */
	public static int CARDTYPE_FITTING_BACKUSER_RETURN = 48;  
	
	/**
	 * StockCard来源——备用机出库
	 */
	public static int CARDTYPE_SPARE_OUT = 54;
	

	/**
	 * StockCard来源——备用库商品上架
	 */
	public static int CARDTYPE_UPSHELF_FOR_SPARE = 55;
	
	/**
	 * StockCard来源——换新机寄回用户
	 */
	public static int CARDTYPE_ASFTERSALE_REPLACE_BACKUSER = 56;
	
	/**
	 * StockCard来源——备用机入库
	 */
	public static int CARDTYPE_SPARE_IN = 58;
	/**
	 * StockCard来源——售后维修换新机商品退回
	 */
	public static int CARDTYPE_AFTERSALE_REPLACE_RETURN = 59;
	
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

    public String getCargoWholeCode() {
        return cargoWholeCode;
    }

    public void setCargoWholeCode(String cargoWholeCode) {
        this.cargoWholeCode = cargoWholeCode == null ? null : cargoWholeCode.trim();
    }

    public int getCargoStoreType() {
        return cargoStoreType;
    }

    public void setCargoStoreType(int cargoStoreType) {
        this.cargoStoreType = cargoStoreType;
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

    public int getCurrentCargoStock() {
        return currentCargoStock;
    }

    public void setCurrentCargoStock(int currentCargoStock) {
        this.currentCargoStock = currentCargoStock;
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
			typeName = "上架入库";
			break;
		case 15:
			typeName = "上架出库";
			break;
		case 16:
			typeName = "货位间调拨入库";
			break;
		case 17:
			typeName = "货位间调拨出库";
			break;
		case 18:
			typeName = "下架入库";
			break;
		case 19:
			typeName = "下架出库";
			break;
		case 20:
			typeName = "补货入库";
			break;
		case 21:
			typeName = "补货出库";
			break;
		case 26:
			typeName = "分拣出库";
			break;
		case 27:
			typeName = "分拣入库";
			break;
		case 28:
			typeName = "异常商品入库";
			break;
		case 29:
			typeName = "异常商品出库";
			break;
		case 30:
			typeName = "售后原品退回";
			break;
		case 31:
			typeName = "售后维修商品退回";
			break;
		case 32:
			typeName = "售后签收商品入库";
			break;
		case 33:
			typeName = "售后库商品上架";
			break;
		case 34:
			typeName = "用户库商品上架";
			break;
		case 35:
			typeName = "原品返回";
			break;
		case 36:
			typeName = "维修寄回";
			break;
		case 37:
			typeName = "售后商品出库";
			break;
		case 38:
			typeName = "检测操作--配件入售后库";
			break;
		case 39:
			typeName = "检测操作--配件入客户库";
			break;
		case 40:
			typeName = "售后配件采购入库";
			break;
		case 41:
			typeName = "配件领用售后配件出库";
			break;
		case 42:
			typeName = "配件领用售后配件入库";
			break;
		case 43:
			typeName = "配件领用客户配件入库";
			break;
		case 44:
			typeName = "配件领用客户配件出库";
			break;	
		case 45:
			typeName = "客户配件库货位更换";
			break;
		case 46:
			typeName = "售后配件库货位更换";
			break;
		case 47:
			typeName = "配件寄回用户";
			break;
		case 48:
			typeName = "配件寄回用户未妥投";
			break;
		case 49:
			typeName = "售后库商品下架";
			break;
		case 50:
			typeName = "用户库商品下架";
			break;
		case 51:
			typeName = "厂家维修更换商品出库";
			break;
		case 52:
			typeName = "厂家维修更换商品入库";
			break;
		case 53:
			typeName = "售后配件维修返还入库";
			break;
		case 54:
			typeName = "备用机出库";
			break;
		case 55:
			typeName = "备用库商品上架";
			break;			
		case 56:
			typeName = "换新机寄回用户";
			break;	
		case 58:
			typeName = "备用机入库";
			break;
		case 59:
			typeName = "售后维修换新机商品退回";
			break;
		}
		return typeName;
	}

}