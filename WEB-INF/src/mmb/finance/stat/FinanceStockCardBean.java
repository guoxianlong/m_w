package mmb.finance.stat;

import java.sql.ResultSet;
import java.sql.SQLException;

import adultadmin.util.db.DbOperation;

public class FinanceStockCardBean {
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
		public static int TYPE_BUYSTOCKIN1 = 1;//采购入库
		public static int TYPE_BUYSTOCKIN15 = 15;//废弃
		public static int TYPE_BUYSTOCKIN16 = 16;//冲抵
		/**
		 * StockCard来源——销售出库
		 */
		public static int CARDTYPE_ORDERSTOCK = 2;
		public static int TYPE_ORDERSTOCK2= 2;//销售出库
		public static int TYPE_ORDERSTOCK17= 17;//废弃
		public static int TYPE_ORDERSTOCK18 = 18;//冲抵
		/**
		 * StockCard来源——调拨入库
		 */
		public static int CARDTYPE_STOCKEXCHANGEIN = 3;
		public static int TYPE_STOCKEXCHANGEIN3= 3;//调拨入库
		public static int TYPE_STOCKEXCHANGEIN17= 19;//废弃
		public static int TYPE_STOCKEXCHANGEIN18 = 20;//冲抵
		/**
		 * StockCard来源——调拨出库
		 */
		public static int CARDTYPE_STOCKEXCHANGEOUT = 4;
		public static int TYPE_STOCKEXCHANGEOUT4= 4;//调拨出库
		public static int TYPE_STOCKEXCHANGEOUT21= 21;//废弃
		public static int TYPE_STOCKEXCHANGEOUT22 = 22;//冲抵
		/**
		/**
		 * StockCard来源——销售退货入库
		 */
		public static int CARDTYPE_CANCELORDERSTOCKIN = 5;
		public static int TYPE_CANCELORDERSTOCKIN5= 5;//调拨入库
		public static int TYPE_CANCELORDERSTOCKIN23= 23;//废弃
		public static int TYPE_CANCELORDERSTOCKIN24 = 24;//冲抵
		/**
		 * StockCard来源——采购退货出库
		 */
		public static int CARDTYPE_CANCELBUYSTOCKIN = 6;
		public static int TYPE_CANCELBUYSTOCKIN6= 6;//调拨入库
		public static int TYPE_CANCELBUYSTOCKIN25= 25;//废弃
		public static int TYPE_CANCELBUYSTOCKIN26 = 26;//冲抵
		
		/**
		 * StockCard来源——报损
		 */
		public static int CARDTYPE_LOSE = 7;
		public static int TYPE_LOSE7= 7;//调拨入库
		public static int TYPE_LOSE27= 27;//废弃
		public static int TYPE_LOSE28 = 28;//冲抵
		/**
		 * StockCard来源——报溢
		 */
		public static int CARDTYPE_GET = 8;
		public static int TYPE_GET8= 8;//调拨入库
		public static int TYPE_GET29= 29;//废弃
		public static int TYPE_GET30 = 30;//冲抵
		/**
		 * StockCard来源——其他入库
		 */
		public static int CARDTYPE_OTHERSIN = 9;
		public static int TYPE_OTHERSIN9= 9;//调拨入库
		
		/**
		 * 特殊销售（样品、残次品）出库
		 */
		public static int CARDTYPE_SPECIALSALE = 15;
		
		/***
		 * StockCard来源——售后签收商品入库
		 */

		public static int CARDTYPE_AFTERSALESDETECT = 17;

		/***
		 * StockCard来源——退换货
		 */

		public static int CARDTYPE_BUYRETURNTH = 18;
		
		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public double getBalanceModeStockPrice() {
			return balanceModeStockPrice;
		}

		public void setBalanceModeStockPrice(double balanceModeStockPrice) {
			this.balanceModeStockPrice = balanceModeStockPrice;
		}

		public int getIsTicket() {
			return isTicket;
		}

		public void setIsTicket(int isTicket) {
			this.isTicket = isTicket;
		}

		public String getStockBatchCode() {
			return stockBatchCode;
		}

		public void setStockBatchCode(String stockBatchCode) {
			this.stockBatchCode = stockBatchCode;
		}

		public static int TYPE_OTHERSIN31= 31;//废弃
		public static int TYPE_OTHERSIN32 = 32;//冲抵
		/**
		 * StockCard来源——其他出库
		 */
		public static int CARDTYPE_OTHERSOUT = 10;
		public static int TYPE_OTHERSOUT10= 10;//调拨入库
		public static int TYPE_OTHERSOUT33= 33;//废弃
		public static int TYPE_OTHERSOUT34 = 34;//冲抵

		/**
		 * StockCard来源——结算退货
		 */
		public static int CARDTYPE_BALANCERETURN = 11;
		public static int TYPE_BALANCERETURN11= 11;//调拨入库
		public static int TYPE_BALANCERETURN35= 35;//废弃
		public static int TYPE_BALANCERETURN36 = 36;//冲抵
		
		/**
		 * StockCard来源——采购调价
		 */
		public static int CARDTYPE_STOCKBATCHPRICE = 12;
		public static int TYPE_STOCKBATCHPRICE12= 12;//调拨入库
		public static int TYPE_STOCKBATCHPRICE37= 37;//废弃
		public static int TYPE_STOCKBATCHPRICE38 = 38;//冲抵
		/**
		 * StockCard来源——售后退货入库
		 */
		public static int CARDTYPE_AFTERSALESCANCELSTOCKIN = 13;
		public static int TYPE_AFTERSALESCANCELSTOCKIN13= 13;//调拨入库
		public static int TYPE_AFTERSALESCANCELSTOCKIN39= 39;//废弃
		public static int TYPE_AFTERSALESCANCELSTOCKIN40 = 40;//冲抵
		
		/***
		 * StockCard来源——税点变更 
		 */
		
		public static int CARDTYPE_TAX_POINT_CHANGE = 14;
		public static int TYPE_TAX_POINT_CHANGE14= 14;//调拨入库
		public static int TAX_TAX_POINT_CHANGE41= 41;//废弃
		public static int TYPE_TAX_POINT_CHANGE42 = 42;//冲抵
		
		
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
		 * 与cardType取值想对应      例  ：售后退货入库 cardtype=13  type=1废弃，type=2冲抵
		 * 数据类型是，废弃，红冲，调整
		 */
		public int type;
		/**
		 * 来源
		 */
		public int cardType;

		/**
		 * 创建时间
		 */
		public String createDatetime;

		/**
		 * 出、入库数量
		 */
		public int stockInCount = 0;

		/**
		 * 出、入库金额
		 */
		public double stockInPriceSum = 0;

	
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
		
		/**
		 * 记录是经销，还是代销的库存均价，与isTicket一起使用
		 */
		public double balanceModeStockPrice;
		/**
		 * 判断此条记录是经销还是代销
		 */
		public int isTicket;
		/***
		 * 批次号
		 */
		public String stockBatchCode;
		
		/**
		 * 经销库存数量，代销库存数量
		 */
		public int balanceModeStockCount;
		
	/**
	 * 供应商税率
	 */
	public double tax;
	
	/**
	 * 结算模式[1:代销，2:经销]
	 */
	public int balanceMode;
	
	public int supplierId; //供应商id
	
	public double getTax() {
		return tax;
	}

	public void setTax(double tax) {
		this.tax = tax;
	}

	public int getBalanceMode() {
		return balanceMode;
	}

	public void setBalanceMode(int balanceMode) {
		this.balanceMode = balanceMode;
	}

	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

		public int getBalanceModeStockCount() {
			return balanceModeStockCount;
		}

		public void setBalanceModeStockCount(int balanceModeStockCount) {
			this.balanceModeStockCount = balanceModeStockCount;
		}

		public int productId;

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

		public String getCardTypeName(){
			String typeName = "-";
			switch(this.cardType){
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
			}
			return typeName;
		}
		
		public static int getCurrentStockCount(DbOperation db,int stockArea,int stockType,int ticket,int productId){
			int currentStockCount=0;
			String w="";
			String group="";
			if(stockArea>=0){
				w=" stock_area="+stockArea;
				group=" group by stock_area";
			}
			if(stockType>=0){
				if(w.equals("")){
					w=" stock_type="+stockType;
				}else{
					w=w+" and stock_type="+stockType;
				}
				if(group.equals("")){
					group=" group by stock_type";
				}else{
					group=group+",stock_type";
				}
			}
			group=group+",ticket,product_id";
			String sql="select sum(batch_count) count from stock_batch where "+w+" and ticket="+ticket+" and product_id="+productId+ group;
			ResultSet rs=db.executeQuery(sql);
			try {
				while(rs.next()){
					currentStockCount=rs.getInt(1);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return currentStockCount;
		}
		
		public int getStockOperationType(){
			int stockOperationType = -1;
			switch(this.cardType){
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
	

}
