package mmb.stock.aftersale;

import java.util.HashMap;
import java.util.Map;

//签收包裹列表
public class AfterSaleDetectPackageBean {
	
	public int id;
	public int deliverId; //快递公司id，来自sys_dict表
	public int returnType; //寄回方式
	public float freight; //运费
	public String packageCode; //包裹单号
	public String createDatetime; //签收时间
	public int createUserId; //签收人账号id
	public String createUserName; //签收人姓名
	public int status; //状态：未匹配，已匹配，匹配失败，已检测
	public String statusName;
	public String orderCode;//订单号
	public String phone;//电话号
	public String senderName;//寄件人姓名
	public String senderAddress;//寄件人地址
	public String remark;//匹配失败备注
	public int areaId; //签收地
	
	public String afterSaleOrderCodes; 
	public String afterSaleOrderIds;
	public String orderCodes;
	
    private String code;//售后处理单号
    private String productOrinames;//商品原名称
    
    
    public String getProductOrinames() {
		return productOrinames;
	}
	public void setProductOrinames(String productOrinames) {
		this.productOrinames = productOrinames;
	}
	public String getAfterSaleOrderIds() {
		return afterSaleOrderIds;
	}
	public void setAfterSaleOrderIds(String afterSaleOrderIds) {
		this.afterSaleOrderIds = afterSaleOrderIds;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAfterSaleOrderCode() {
		return afterSaleOrderCode;
	}
	public void setAfterSaleOrderCode(String afterSaleOrderCode) {
		this.afterSaleOrderCode = afterSaleOrderCode;
	}
	private String afterSaleOrderCode;//售后单号
	public static Map<Integer,String> statusMap = new HashMap<Integer, String>();
	/**未匹配**/
	public static final int STATUS0 = 0;
	/**已匹配**/
	public static final int STATUS1 = 1;
	/**匹配失败**/
	public static final int STATUS2 = 2;
	/**已检测**/
	public static final int STATUS3 = 3;
	
	public static Map<Integer,String> typeMap = new HashMap<Integer, String>();
	/**寄付**/
	public static final int TYPE0 = 0;
	/**到付**/
	public static final int TYPE1 = 1;
	
	static{
		statusMap.put(STATUS0, "未匹配");
		statusMap.put(STATUS1, "已匹配");
		statusMap.put(STATUS2, "匹配失败");
		statusMap.put(STATUS3, "已检测");
	
		typeMap.put(TYPE0, "寄付");
		typeMap.put(TYPE1, "到付");
	}
	
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDeliverId() {
		return deliverId;
	}
	public void setDeliverId(int deliverId) {
		this.deliverId = deliverId;
	}
	public int getReturnType() {
		return returnType;
	}
	public void setReturnType(int returnType) {
		this.returnType = returnType;
	}
	public float getFreight() {
		return freight;
	}
	public void setFreight(float freight) {
		this.freight = freight;
	}
	public String getPackageCode() {
		return packageCode;
	}
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
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
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getSenderAddress() {
		return senderAddress;
	}
	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getAfterSaleOrderCodes() {
		return afterSaleOrderCodes;
	}
	public void setAfterSaleOrderCodes(String afterSaleOrderCodes) {
		this.afterSaleOrderCodes = afterSaleOrderCodes;
	}
	public String getOrderCodes() {
		return orderCodes;
	}
	public void setOrderCodes(String orderCodes) {
		this.orderCodes = orderCodes;
	}
	public String getParentId1() {
		return parentId1;
	}
	public void setParentId1(String parentId1) {
		this.parentId1 = parentId1;
	}
	public String getParentId2() {
		return parentId2;
	}
	public void setParentId2(String parentId2) {
		this.parentId2 = parentId2;
	}
	public String getParentId3() {
		return parentId3;
	}
	public void setParentId3(String parentId3) {
		this.parentId3 = parentId3;
	}
	private String parentId1;
	private String parentId2;
	private String parentId3;

}
