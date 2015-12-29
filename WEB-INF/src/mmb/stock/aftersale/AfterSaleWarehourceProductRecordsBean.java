package mmb.stock.aftersale;

import java.sql.Timestamp;


public class AfterSaleWarehourceProductRecordsBean {
	
	public int id;
	/**处理单号**/
	public String code;
	/**售后对应包裹id**/
	public int afterSaleWarehourcePackageId;
	/**售后单id**/
	public int afterSaleOrderId;
	/**产品id**/
	public int productId;
	/**是否是售后单商品**/
	public int inBuyOrder;
	/**是否是订单商品**/
	public int inUserOrder;
	/**IMEI码**/
	public String IMEI;
	/**备注**/
	public String remark;
	/**客户确认前表示类型：0 检测异常 1直接退货 2 扣费退货 3 可以换货 4错发换货 5 付费维修 6保修 7建议原品返回  8无法维修建议退货 9无法维修建议换货 
	 * 客户确认后表示类型：1直接退货 2 扣费退货 3正常换货  4错发换货 5 付费维修 6 保修  7 原品退回**/
	public int type;
	/**处理单状态:1检测中 2等待客户确认 3退货 4换货 5维修 6 原品返回 7处理已完成 8处理未妥投**/
	public int status;
	/**是否装箱：0 未装箱  1 待装箱 2 已装箱  3 待拆箱**/
	public int isVanning;
	/**创建人id**/
	public int createUserId;
	/**创建人姓名**/
	public String createUserName;
	/**创建时间**/
	public Timestamp createDatetime;
	/**修改人id**/
	public int modifyUserId;
	/**修改人姓名**/
	public String modifyUserName;
	/**修改时间**/
	public Timestamp modifyDatetime;
	
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
	public int getAfterSaleWarehourcePackageId() {
		return afterSaleWarehourcePackageId;
	}
	public void setAfterSaleWarehourcePackageId(int afterSaleWarehourcePackageId) {
		this.afterSaleWarehourcePackageId = afterSaleWarehourcePackageId;
	}
	public int getAfterSaleOrderId() {
		return afterSaleOrderId;
	}
	public void setAfterSaleOrderId(int afterSaleOrderId) {
		this.afterSaleOrderId = afterSaleOrderId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
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
	public String getIMEI() {
		return IMEI;
	}
	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getIsVanning() {
		return isVanning;
	}
	public void setIsVanning(int isVanning) {
		this.isVanning = isVanning;
	}
	public int getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public Timestamp getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(Timestamp createDatetime) {
		this.createDatetime = createDatetime;
	}
	public int getModifyUserId() {
		return modifyUserId;
	}
	public void setModifyUserId(int modifyUserId) {
		this.modifyUserId = modifyUserId;
	}
	public String getModifyUserName() {
		return modifyUserName;
	}
	public void setModifyUserName(String modifyUserName) {
		this.modifyUserName = modifyUserName;
	}
	public Timestamp getModifyDatetime() {
		return modifyDatetime;
	}
	public void setModifyDatetime(Timestamp modifyDatetime) {
		this.modifyDatetime = modifyDatetime;
	}
	
}
