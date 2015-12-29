package mmb.hessian.ware;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * @author zhaolin
 * 说明：订单物流包裹投递信息
 */
public class DeliverOrderInfoBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private int orderId;  //订单ID
	private int deliverId; //快递公司id
	private String deliverName;  //快递公司前台
	private String deliverPhone;  //快递热线电话
	private String deliverNo;   //快递单号
	private String deliverInfo;  //包裹投递信息
	private int deliverState = -1; //配送状态
	private List<String> deliverInfoList; //完整物流信息
	
	/**已出库*/
	public static final int DELIVER_STATE0 = 0;
	/**已揽收*/
	public static final int DELIVER_STATE1 = 1;
	/**在途*/
	public static final int DELIVER_STATE2 = 2;
	/**到达当地*/
	public static final int DELIVER_STATE4 = 4;
	/**投递中*/
	public static final int DELIVER_STATE5 = 5;
	/**未妥投已退回*/
	public static final int DELIVER_STATE6 = 6;
	/**已签收*/
	public static final int DELIVER_STATE7 = 7;
	/**未妥投开始退回*/
	public static final int DELIVER_STATE8 = 8;
	/**疑难*/
	public static final int DELIVER_STATE9 = 9;
	/**退回中*/
	public static final int DELIVER_STATE10 = 10;
	/**丢失*/
	public static final int DELIVER_STATE11 = 11;
	/**破损*/
	public static final int DELIVER_STATE12 = 12;
	public static HashMap<Integer, String> deliverStateMap = new HashMap<Integer, String>();
	static {
		deliverStateMap.put(0, "已出库");
		deliverStateMap.put(1, "已揽收");
		deliverStateMap.put(2, "在途");
		deliverStateMap.put(4, "到达当地");
		deliverStateMap.put(5, "投递中");
		deliverStateMap.put(6, "未妥投已退回");
		deliverStateMap.put(7, "已签收");
		deliverStateMap.put(8, "未妥投开始退回");
		deliverStateMap.put(9, "疑难");
		deliverStateMap.put(10, "退回中");
		deliverStateMap.put(11, "丢失");
		deliverStateMap.put(12, "破损");
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
	public int getDeliverId() {
		return deliverId;
	}
	public void setDeliverId(int deliverId) {
		this.deliverId = deliverId;
	}
	public String getDeliverName() {
		return deliverName;
	}
	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
	}
	public String getDeliverPhone() {
		return deliverPhone;
	}
	public void setDeliverPhone(String deliverPhone) {
		this.deliverPhone = deliverPhone;
	}
	public String getDeliverNo() {
		return deliverNo;
	}
	public void setDeliverNo(String deliverNo) {
		this.deliverNo = deliverNo;
	}
	public String getDeliverInfo() {
		return deliverInfo;
	}
	public void setDeliverInfo(String deliverInfo) {
		this.deliverInfo = deliverInfo;
	}
	public int getDeliverState() {
		return deliverState;
	}
	public void setDeliverState(int deliverState) {
		this.deliverState = deliverState;
	}
	public List<String> getDeliverInfoList() {
		return deliverInfoList;
	}
	public void setDeliverInfoList(List<String> deliverInfoList) {
		this.deliverInfoList = deliverInfoList;
	}
}
