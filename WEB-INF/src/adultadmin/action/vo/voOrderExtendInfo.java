package adultadmin.action.vo;

/**
 * 说明：订单扩展信息
 * 作者：赵林
 * 创建时间：2012-09-24
 *
 */
public class voOrderExtendInfo {
	
	private int id;    //订单ID
	private String orderCode;   //订单
	private float orderPrice;   //订单价格(订单实际价格，区别于应付金额)
	private int payMode;        //付款方式(主要用于在线支付不同支付渠道划分)
	private int payStatus;      //付款状态(主要用于在线支付)
	private int addId1 = -1;         //订单地址一级省级id
	private int addId2 = -1;         //订单地址二级城市id
	private int addId3 = -1;         //订单地址三级区县id
	private int addId4 = -1;         //订单地址四级街道id
	private String add5 = "";        //订单地址详细地址(第五级手动输入地址部分)
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public float getOrderPrice() {
		return orderPrice;
	}
	public void setOrderPrice(float orderPrice) {
		this.orderPrice = orderPrice;
	}
	public int getPayMode() {
		return payMode;
	}
	public void setPayMode(int payMode) {
		this.payMode = payMode;
	}
	public int getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(int payStatus) {
		this.payStatus = payStatus;
	}
	public int getAddId1() {
		return addId1;
	}
	public void setAddId1(int addId1) {
		this.addId1 = addId1;
	}
	public int getAddId2() {
		return addId2;
	}
	public void setAddId2(int addId2) {
		this.addId2 = addId2;
	}
	public int getAddId3() {
		return addId3;
	}
	public void setAddId3(int addId3) {
		this.addId3 = addId3;
	}
	public int getAddId4() {
		return addId4;
	}
	public void setAddId4(int addId4) {
		this.addId4 = addId4;
	}
	public String getAdd5() {
		return add5;
	}
	public void setAdd5(String add5) {
		this.add5 = add5;
	}

	
}
