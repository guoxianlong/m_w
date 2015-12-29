package mmb.stock.aftersale;

import java.util.HashMap;
import java.util.Map;

public class AfterSaleOperationListBean {
	
	/**漏发补货**/
	public static final int TYPE1 = 1;
	/**退货**/
	public static final int TYPE2 = 2;
	/**换货**/
	public static final int TYPE3 = 3;
	
	public static Map<Integer,String> typeMap = new HashMap<Integer,String>();
	
	static{
		typeMap.put(TYPE1, "漏发补货");
		typeMap.put(TYPE2, "退货");
		typeMap.put(TYPE3, "换货");
	}
	
	public int id;
	/**单据编号**/
	public String CODE;
	/**单据类型：1 漏发补货  2退货  3换货**/
	public int TYPE;
	/**如果是漏发补货，则存储补货新生成订单id(S单id),如果是退货则存储退货商品对应的订单id,如果是换货单，则存储对应新生成的订单id(s单id)**/
	public int orderId;
	/**对应订单code**/
	public String orderCode;
	/**售后单id**/
	public int afterSaleOrderId;
	/**售后单编号**/
	public String afterSaleOrderCode;
	/**单据状态**/
	public int state;
	/**备注**/
	public String remarks;
	/**创建者id**/
	public int creatorId;
	/**创建者username**/
	public String creatorName;
	/**创建时间**/
	public String createTime;
	/**客户姓名**/
	public String customerName;
	/**客户手机号**/
	public String customerPhone;
	/**客户地址**/
	public String customerAddress;
	/**客户邮政编码**/
	public String customerPostCode;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCODE() {
		return CODE;
	}
	public void setCODE(String cODE) {
		CODE = cODE;
	}
	public int getTYPE() {
		return TYPE;
	}
	public void setTYPE(int tYPE) {
		TYPE = tYPE;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public int getAfterSaleOrderId() {
		return afterSaleOrderId;
	}
	public void setAfterSaleOrderId(int afterSaleOrderId) {
		this.afterSaleOrderId = afterSaleOrderId;
	}
	public String getAfterSaleOrderCode() {
		return afterSaleOrderCode;
	}
	public void setAfterSaleOrderCode(String afterSaleOrderCode) {
		this.afterSaleOrderCode = afterSaleOrderCode;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public int getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerPhone() {
		return customerPhone;
	}
	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}
	public String getCustomerAddress() {
		return customerAddress;
	}
	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}
	public String getCustomerPostCode() {
		return customerPostCode;
	}
	public void setCustomerPostCode(String customerPostCode) {
		this.customerPostCode = customerPostCode;
	}
}
