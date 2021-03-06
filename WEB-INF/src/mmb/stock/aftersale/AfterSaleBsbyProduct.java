package mmb.stock.aftersale;/******************************************************************************* * 售后报损报溢商品 *  * @author 2014-04-17 15:57:41 *  */public class AfterSaleBsbyProduct implements java.io.Serializable {	/**	 * 	 */	public static final long serialVersionUID = 1L;	// field	/** id **/	public int id;	/** 报损报溢单id **/	public int bsbyOperationnoteId;	/** 状态 **/	public int status;	/** 售后处理单号 **/	public String afterSaleDetectProductCode;	/** 售后处理单状态 **/	public int afterSaleDetectProductStatus;	/** 售后单号 **/	public String afterSaleOrderCode;	/** 商品id **/	public int productId;	/** IMEI码 **/	public String imei;	/** 货位号 **/	public String wholeCode;	/** 未删除 **/	public static int STATUS0 = 0;	/** 已删除 **/	public static int STATUS1 = 1;	/** 已完成 **/	public static int STATUS2 = 2;	/** 库类型 **/	private int stockType;		private String productName;	private String productCode;	private String afterSaleDetectProductStatusName;	private int count;	private String productOriname;	private String price;//报损单价含税（不含税）	public String getPrice() {		return price;	}	public void setPrice(String price) {		this.price = price;	}	public String getProductOriname() {		return productOriname;	}	public void setProductOriname(String productOriname) {		this.productOriname = productOriname;	}	public int getCount() {		return count;	}	public void setCount(int count) {		this.count = count;	}	// method	public int getId() {		return id;	}	public void setId(int id) {		this.id = id;	}	public int getBsbyOperationnoteId() {		return bsbyOperationnoteId;	}	public void setBsbyOperationnoteId(int bsbyOperationnoteId) {		this.bsbyOperationnoteId = bsbyOperationnoteId;	}	public int getStatus() {		return status;	}	public void setStatus(int status) {		this.status = status;	}	public String getAfterSaleDetectProductCode() {		return afterSaleDetectProductCode;	}	public void setAfterSaleDetectProductCode(String afterSaleDetectProductCode) {		this.afterSaleDetectProductCode = afterSaleDetectProductCode;	}	public int getAfterSaleDetectProductStatus() {		return afterSaleDetectProductStatus;	}	public void setAfterSaleDetectProductStatus(int afterSaleDetectProductStatus) {		this.afterSaleDetectProductStatus = afterSaleDetectProductStatus;	}	public String getAfterSaleOrderCode() {		return afterSaleOrderCode;	}	public void setAfterSaleOrderCode(String afterSaleOrderCode) {		this.afterSaleOrderCode = afterSaleOrderCode;	}	public int getProductId() {		return productId;	}	public void setProductId(int productId) {		this.productId = productId;	}	public String getImei() {		return imei;	}	public void setImei(String imei) {		this.imei = imei;	}	public String getWholeCode() {		return wholeCode;	}	public void setWholeCode(String wholeCode) {		this.wholeCode = wholeCode;	}	public String getProductName() {		return productName;	}	public void setProductName(String productName) {		this.productName = productName;	}	public String getProductCode() {		return productCode;	}	public void setProductCode(String productCode) {		this.productCode = productCode;	}	public String getAfterSaleDetectProductStatusName() {		return afterSaleDetectProductStatusName;	}	public void setAfterSaleDetectProductStatusName(			String afterSaleDetectProductStatusName) {		this.afterSaleDetectProductStatusName = afterSaleDetectProductStatusName;	}	public int getStockType() {		return stockType;	}	public void setStockType(int stockType) {		this.stockType = stockType;	}}