package cn.mmb.delivery.domain.model;

import java.util.List;

import cn.mmb.delivery.domain.model.vo.TraceInfo;

public abstract class WayBillTrace {
	
	/**
	 * 结果成功或失败
	 */
	private boolean sucess;
	
	/**
	 * 反馈信息
	 */
	private String resultInfo;
	
	/**
	 * deliver_order表id
	 */
	private int deliverOrderId;
	
	/**
	 * 订单id
	 */
	private int orderId;
	
	/**
	 * 物流状态
	 */
	private int status;
	
	/**
	 * 运单号
	 */
	private String deliverNo;
	
	/**
	 * 面单信息
	 */
	private List<WayBill> wayBill;

	/**
	 * 接口获取到的运单信息
	 */
	private List<TraceInfo> traceInfo;

	/**
	 * @return the sucess
	 */
	public boolean isSucess() {
		return sucess;
	}

	/**
	 * @param sucess the sucess to set
	 */
	public void setSucess(boolean sucess) {
		this.sucess = sucess;
	}

	/**
	 * @return the resultInfo
	 */
	public String getResultInfo() {
		return resultInfo;
	}

	/**
	 * @param resultInfo the resultInfo to set
	 */
	public void setResultInfo(String resultInfo) {
		this.resultInfo = resultInfo;
	}

	/**
	 * @return the traceInfo
	 */
	public List<TraceInfo> getTraceInfo() {
		return traceInfo;
	}

	/**
	 * @param traceInfo the traceInfo to set
	 */
	public void setTraceInfo(List<TraceInfo> traceInfo) {
		this.traceInfo = traceInfo;
	}

	/**
	 * @return the deliverNo
	 */
	public String getDeliverNo() {
		return deliverNo;
	}

	/**
	 * @param deliverNo the deliverNo to set
	 */
	public void setDeliverNo(String deliverNo) {
		this.deliverNo = deliverNo;
	}

	/**
	 * @return the wayBill
	 */
	public List<WayBill> getWayBill() {
		return wayBill;
	}

	/**
	 * @param wayBill the wayBill to set
	 */
	public void setWayBill(List<WayBill> wayBill) {
		this.wayBill = wayBill;
	}

	/**
	 * @return the deliverOrderId
	 */
	public int getDeliverOrderId() {
		return deliverOrderId;
	}

	/**
	 * @param deliverOrderId the deliverOrderId to set
	 */
	public void setDeliverOrderId(int deliverOrderId) {
		this.deliverOrderId = deliverOrderId;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the orderId
	 */
	public int getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

}
