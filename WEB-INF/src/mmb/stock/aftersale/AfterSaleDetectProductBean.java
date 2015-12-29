package mmb.stock.aftersale;

import java.util.HashMap;
import java.util.Map;

//签收包裹商品（处理单）
public class AfterSaleDetectProductBean {
	
	public static Map<String, Integer> handleMap = new HashMap<String, Integer>();
	//处理意见
	/**检测异常**/
	public static String HANDLE1 = "检测异常";
	/**直接退货**/
	public static String HANDLE2 = "直接退货";
	/**扣费退货**/
	public static String HANDLE3 = "扣费退货";
	/**换货**/
	public static String HANDLE4 = "换货";
	/**错发换货**/
	public static String HANDLE5 = "错发换货";
	/**付费维修**/
	public static String HANDLE6 = "付费维修";
	/**保修**/
	public static String HANDLE7 = "保修";
	/**原品返回**/
	public static String HANDLE8 = "原品返回";
	/**换新机*/
	public static String HANDLE9 = "换新机";
	
	public static Map<Integer, String> inBuyOrderMap = new HashMap<Integer, String>();
	/** 否 **/
	public static int INBUYORDER0 = 0;
	/** 是 **/
	public static int INBUYORDER1 = 1;
	
	public static Map<Integer, String> inUserOrderMap = new HashMap<Integer, String>();
	/** 否 **/
	public static int INUSERORDER0 = 0;
	/** 是 **/
	public static int INUSERORDER1 = 1;
	
	public static Map<Integer, String> statusMap = new HashMap<Integer, String>();
	/** 无**/
	public static int STATUS0 = 0;
	/** 检测中**/
	public static int STATUS1 = 1;
	/** 等待客户确认 **/
	public static int STATUS2 = 2;
	/** 退货 **/
	public static int STATUS3 = 3;
	/** 保修 **/
	public static int STATUS4 = 4;
	/** 换货 **/
	public static int STATUS5 = 5;
	/** 错发换货 **/
	public static int STATUS6 = 6;
	/** 付费维修 **/
	public static int STATUS7 = 7;
	/** 付费维修已完成 **/
	public static int STATUS8 = 8;
	/** 保修已完成 **/
	public static int STATUS9 = 9;
	/** 原品返回 **/
	public static int STATUS10 = 10;
	/** 原品已返回 **/
	public static int STATUS11 = 11;
	/** 待封箱 **/
	public static int STATUS12 = 12;
	/** 封箱已完成 **/
	public static int STATUS13 = 13;
	/** 退货已完成 **/
	public static int STATUS14 = 14;
	/** 维修已完成 **/
	public static int STATUS15 = 15;
	/** 维修商品已退回 **/
	public static int STATUS16 = 16;
	/** 原品已退回 **/
	public static int STATUS17 = 17;
	/** 换货已发货 **/
	public static int STATUS18 = 18;
	/** 换货已退回 **/
	public static int STATUS19 = 19;
	/**
	 * 报溢（售后库）
	 */
	public static int STATUS23 = 23;
	/**
	 * 报溢已完成（售后库）
	 */
	public static int STATUS24 = 24;
	/** 维修换新机中 **/
	public static int STATUS20 = 20;
	/** 维修换新机已完成 **/
	public static int STATUS21 = 21;
	/** 维修换新机已退回 **/
	public static int STATUS22 = 22;
	
	public static Map<Integer, String> mainProductStatusMap = new HashMap<Integer, String>();
	//主商品状态
	/**完好**/
	public static int MAIN_PRODUCT_STATUS1 = 1;
	/**残次**/
	public static int MAIN_PRODUCT_STATUS2 = 2;
	
	public static Map<Integer, String> bsStatusMap = new HashMap<Integer, String>();
	/**未报损**/
	public static int BS_STATUS1 = 1;
	/**已报损**/
	public static int BS_STATUS2 = 2;
	
	
	public static Map<Integer, String> lockStatusMap = new HashMap<Integer, String>();	
	/**未锁定**/
	public static int LOCK_STATUS0 = 0;
	/**已锁定**/
	public static int LOCK_STATUS1 = 1;
	/**报损完成**/
	public static int LOCK_STATUS2 = 2;	
	
	public static Map<Integer, String> debitNoteMap = new HashMap<Integer, String>();
	
	/**发票已寄回**/
	public static int DEBIT_NOTE0 = 0;
	/**发票未寄回**/
	public static int DEBIT_NOTE1 = 1;
	/**未开发票**/
	public static int DEBIT_NOTE2 = 2;	
	
	static {
		inBuyOrderMap.put(INBUYORDER0, "否");
		inBuyOrderMap.put(INBUYORDER1, "是");
		
		inUserOrderMap.put(INUSERORDER0, "否");
		inUserOrderMap.put(INUSERORDER1, "是");
		
		statusMap.put(STATUS0, "无");
		statusMap.put(STATUS1, "检测中");
		statusMap.put(STATUS2, "等待客户确认");
		statusMap.put(STATUS3, "退货");
		statusMap.put(STATUS4, "保修");
		statusMap.put(STATUS5, "换货");
		statusMap.put(STATUS6, "错发换货");
		statusMap.put(STATUS7, "付费维修");
		statusMap.put(STATUS8, "付费维修已完成");
		statusMap.put(STATUS9, "保修已完成");
		statusMap.put(STATUS10, "原品返回");
		statusMap.put(STATUS11, "原品已返回");
		statusMap.put(STATUS12, "待封箱");
		statusMap.put(STATUS13, "封箱已完成");
		statusMap.put(STATUS14, "退货已完成");
		statusMap.put(STATUS15, "维修已完成");
		statusMap.put(STATUS16, "维修商品已退回");
		statusMap.put(STATUS17, "原品已退回");
		statusMap.put(STATUS18, "换货已发货");
		statusMap.put(STATUS19, "换货已退回");
		statusMap.put(STATUS23, "报溢");
		statusMap.put(STATUS24, "报溢已完成");
		statusMap.put(STATUS20, "维修换新机中");
		statusMap.put(STATUS21, "维修换新机已完成");
		statusMap.put(STATUS22, "维修换新机已退回");
		
		handleMap.put(HANDLE1, 0);
		handleMap.put(HANDLE2, 1);
		handleMap.put(HANDLE3, 2);
		handleMap.put(HANDLE4, 3);
		handleMap.put(HANDLE5, 4);
		handleMap.put(HANDLE6, 5);
		handleMap.put(HANDLE7, 6);
		handleMap.put(HANDLE8, 7);
		handleMap.put(HANDLE9, 15);
		
		mainProductStatusMap.put(MAIN_PRODUCT_STATUS1, "完好");
		mainProductStatusMap.put(MAIN_PRODUCT_STATUS2, "残次");
		
		bsStatusMap.put(BS_STATUS1, "未报损");
		bsStatusMap.put(BS_STATUS2, "已报损");
		
		lockStatusMap.put(LOCK_STATUS0, "未锁定");
		lockStatusMap.put(LOCK_STATUS1, "已锁定");
		lockStatusMap.put(LOCK_STATUS2, "报损完成");
		
		debitNoteMap.put(DEBIT_NOTE0, "发票已寄回");
		debitNoteMap.put(DEBIT_NOTE1, "发票未寄回");
		debitNoteMap.put(DEBIT_NOTE2, "未开发票");
	}
	
	
	public int 	id;
	public int afterSaleDetectPackageId; //签收包裹id
	public int productId; //商品id
	private String productName;
	private String productCode;
	public int stockType;//库类型
	public String stockTypeName;//库类型名称
	public int afterSaleOrderId; //售后单id
	public String afterSaleOrderCode;//售后单号
	public int inBuyOrder; //是否是售后单商品
	private String inBuyOrderName;
	public int inUserOrder; //是否是订单商品
	private String inUserOrderName;
	public String remark; //备注
	public String code; //处理单号
	public String IMEI;  //IMEI码
	public int status; //处理单状态
	public String statusName; //处理单状态名称
	public String cargoWholeCode;//处理单相关货位
	public String createDatetime;//入库时间
	public int createUserId;//创建人id
	public String createUserName;//创建人userName
	public int bsStatus;//是否报损
	public int lockStatus;// 0：未锁定 1：已锁定 2：报损完成
	public int areaId;//售后地区
	
	private String packageCode;
	
	private int parentId1;
	private String parentId1Name;
	private float price5;
	private String flatName;//订单来源
	//销售属性
	private String sellTypeName;
	private String productOriname;
	private int backSupplierProductId;//最后一条返厂商品记录的id
	
	
	public int getBackSupplierProductId() {
		return backSupplierProductId;
	}
	public void setBackSupplierProductId(int backSupplierProductId) {
		this.backSupplierProductId = backSupplierProductId;
	}
	public String getProductOriname() {
		return productOriname;
	}
	public void setProductOriname(String productOriname) {
		this.productOriname = productOriname;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAfterSaleDetectPackageId() {
		return afterSaleDetectPackageId;
	}
	public void setAfterSaleDetectPackageId(int afterSaleDetectPackageId) {
		this.afterSaleDetectPackageId = afterSaleDetectPackageId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public int getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public int getAfterSaleOrderId() {
		return afterSaleOrderId;
	}
	public void setAfterSaleOrderId(int afterSaleOrderId) {
		this.afterSaleOrderId = afterSaleOrderId;
	}
	public int getInBuyOrder() {
		return inBuyOrder;
	}
	public void setInBuyOrder(int inBuyOrder) {
		this.inBuyOrder = inBuyOrder;
	}
	public int getInUserOrder() {
		return inUserOrder;
	}
	public void setInUserOrder(int inUserOrder) {
		this.inUserOrder = inUserOrder;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getIMEI() {
		return IMEI;
	}
	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCargoWholeCode() {
		return cargoWholeCode;
	}
	public void setCargoWholeCode(String cargoWholeCode) {
		this.cargoWholeCode = cargoWholeCode;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getInBuyOrderName() {
		return inBuyOrderName;
	}
	public void setInBuyOrderName(String inBuyOrderName) {
		this.inBuyOrderName = inBuyOrderName;
	}
	public String getInUserOrderName() {
		return inUserOrderName;
	}
	public void setInUserOrderName(String inUserOrderName) {
		this.inUserOrderName = inUserOrderName;
	}
	public String getAfterSaleOrderCode() {
		return afterSaleOrderCode;
	}
	public void setAfterSaleOrderCode(String afterSaleOrderCode) {
		this.afterSaleOrderCode = afterSaleOrderCode;
	}
	public int getParentId1() {
		return parentId1;
	}
	public void setParentId1(int parentId1) {
		this.parentId1 = parentId1;
	}
	public String getParentId1Name() {
		return parentId1Name;
	}
	public void setParentId1Name(String parentId1Name) {
		this.parentId1Name = parentId1Name;
	}
	public float getPrice5() {
		return price5;
	}
	public void setPrice5(float price5) {
		this.price5 = price5;
	}
	public String getPackageCode() {
		return packageCode;
	}
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}
	public int getStockType() {
		return stockType;
	}
	public void setStockType(int stockType) {
		this.stockType = stockType;
	}
	public String getStockTypeName() {
		return stockTypeName;
	}
	public void setStockTypeName(String stockTypeName) {
		this.stockTypeName = stockTypeName;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getSellTypeName() {
		return sellTypeName;
	}
	public void setSellTypeName(String sellTypeName) {
		this.sellTypeName = sellTypeName;
	}
	public int getBsStatus() {
		return bsStatus;
	}
	public void setBsStatus(int bsStatus) {
		this.bsStatus = bsStatus;
	}
	public int getLockStatus() {
		return lockStatus;
	}
	public void setLockStatus(int lockStatus) {
		this.lockStatus = lockStatus;
	}
	public String getFlatName() {
		return flatName;
	}
	public void setFlatName(String flatName) {
		this.flatName = flatName;
	}

}
