package mmb.stock.aftersale;import java.util.HashMap;import java.util.Map;/******************************************************************************* * 盘点商品表 * @author 2014-04-17 15:57:41 *  */	public class AfterSaleInventoryProduct implements java.io.Serializable {	/**	 * 	 */	private static final long serialVersionUID = 1L;	/**	 * 普通	 */	public static int TYPE1 = 1;	/**	 * 有库存记录缺少实物，即需要被报损的	 */	public static int TYPE2 = 2;	/**	 * 报溢(已报损)	 */	public static int TYPE3 = 3;	/**	 * 货位不一致 	 */	public static int TYPE4 = 4; 	/**	 * 已寄出	 */	public static int TYPE5 = 5;	public static Map<Integer, String> typeMap = new HashMap<Integer, String>();	static {		typeMap.put(1, "普通");		typeMap.put(2, "有库存记录缺少实物");		typeMap.put(3, "报溢(已报损)");		typeMap.put(4, "货位不一致");		typeMap.put(5, "已寄出");	}	//field	/** id **/	public int id;	/** 盘点记录id **/	public int afterSaleInventoryRecordId;	/** 售后处理单号 **/	public String afterSaleDetectProductCode;	/** 售后处理单状态 **/	public int afterSaleDetectProductStatus;	/** 售后单号 **/	public String afterSaleOrderCode;	/** 商品id **/	public int productId;	/** 记录货位号 **/	public String recordWholeCode;	/** 实际货位号 **/	public String realWholeCode;	/** 盘点人 **/	public int userId;	/**  盘点人姓名 **/	public String userName;	/** 类型，1（普通），2（bs），3（by），4（货位不一致），5（已寄出） **/	public int type;	//method	public int getId() {		return id;	}	public void setId(int id) {		this.id = id;	}	public int getAfterSaleInventoryRecordId() {		return afterSaleInventoryRecordId;	}	public void setAfterSaleInventoryRecordId(int afterSaleInventoryRecordId) {		this.afterSaleInventoryRecordId = afterSaleInventoryRecordId;	}	public String getAfterSaleDetectProductCode() {		return afterSaleDetectProductCode;	}	public void setAfterSaleDetectProductCode(String afterSaleDetectProductCode) {		this.afterSaleDetectProductCode = afterSaleDetectProductCode;	}	public int getAfterSaleDetectProductStatus() {		return afterSaleDetectProductStatus;	}	public void setAfterSaleDetectProductStatus(int afterSaleDetectProductStatus) {		this.afterSaleDetectProductStatus = afterSaleDetectProductStatus;	}	public String getAfterSaleOrderCode() {		return afterSaleOrderCode;	}	public void setAfterSaleOrderCode(String afterSaleOrderCode) {		this.afterSaleOrderCode = afterSaleOrderCode;	}	public int getProductId() {		return productId;	}	public void setProductId(int productId) {		this.productId = productId;	}	public String getRecordWholeCode() {		return recordWholeCode;	}	public void setRecordWholeCode(String recordWholeCode) {		this.recordWholeCode = recordWholeCode;	}	public String getRealWholeCode() {		return realWholeCode;	}	public void setRealWholeCode(String realWholeCode) {		this.realWholeCode = realWholeCode;	}	public int getUserId() {		return userId;	}	public void setUserId(int userId) {		this.userId = userId;	}	public String getUserName() {		return userName;	}	public void setUserName(String userName) {		this.userName = userName;	}	public int getType() {		return type;	}	public void setType(int type) {		this.type = type;	}}