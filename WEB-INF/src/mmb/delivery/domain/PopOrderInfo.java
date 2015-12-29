package mmb.delivery.domain;

import java.util.HashMap;
import java.util.Map;

public class PopOrderInfo {
	
	/** 发送失败(20分钟仍未发送出去的视为发送失败) */
	public static final int SEND_STATUS_FAIL = -1;
	/** 未发送 */
	public static final int SEND_STATUS0 = 0;
	/** 已发送 */
	public static final int SEND_STATUS1 = 1;
	/** 发送成功(已成功发送到POP) */
	public static final int SEND_STATUS2 = 2;

	private int id;
	
	/** MMB订单id */
	private int orderId;
	
	/** MMB订单号 */
	private String orderCode;
	
	/** POP订单号 */
	private String popOrderCode;
	
	/** 运单号 */
	private String deliverCode;
	
	/** 配送状态[0:已出库; 7:已妥投; 8:拒收] */
	private Integer status;
	
	/** 发送状态[0:未发送; 1:已发送; 2:发送成功(已成功发送到POP)] */
	private int sendStatus;
	
	/** 出库时间*/
	private String outstockTime;
	
	/** 订单创建时间 */
	private String orderCreateTime;
	
	/** 快递公司名 */
	private String deliveryName;
	
	/** 库区 :默认为京东*/
	private String stockArea = "京东";

	public static Map<Integer, String> map = new HashMap<Integer, String>();
	static{
		map.put(4, "买卖宝-无锡");
		map.put(5, "京东");
		map.put(9, "买卖宝-成都");
	}
	
	public static String getStockArea(int stockArea) {
		return map.get(stockArea);
	}
	
	public String getStockArea() {
		return stockArea;
	}
	
	public String getDeliveryName() {
		return deliveryName;
	}

	public void setDeliveryName(String deliveryName) {
		this.deliveryName = deliveryName;
	}

	public void setStockArea(String stockArea) {
		this.stockArea = stockArea;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getPopOrderCode() {
		return popOrderCode;
	}

	public void setPopOrderCode(String popOrderCode) {
		this.popOrderCode = popOrderCode;
	}

	public String getDeliverCode() {
		return deliverCode;
	}

	public void setDeliverCode(String deliverCode) {
		this.deliverCode = deliverCode;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public int getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(int sendStatus) {
		this.sendStatus = sendStatus;
	}

	public String getOutstockTime() {
		return outstockTime;
	}

	public void setOutstockTime(String outstockTime) {
		this.outstockTime = outstockTime;
	}

	public String getOrderCreateTime() {
		return orderCreateTime;
	}

	public void setOrderCreateTime(String orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}

}
