package cn.mmb.delivery.domain.model.vo;
/**
 * 监控系统查询参数
 * @author lml
 *
 */
public class MonitorQueryParamBean {
	
	/** 买卖宝-成都 */
	public static final int POP9 = 9;
	/** 买卖宝-无锡 */
	public static final int POP4 = 4;
	/** 京东 */
	public static final int POP5 = 5;
	
	private int codeType;
	private String code;
	private String startTime;
	private String endTime;
	private int sendStatus;
	private int failedReason;
	
	private int pop;//pop商家
	private int delivery;//承运商
	private int stockArea;//库区
	
	public int getStockArea() {
		return stockArea;
	}
	public void setStockArea(int stockArea) {
		this.stockArea = stockArea;
	}
	public int getDelivery() {
		return delivery;
	}
	public int getPop() {
		return pop;
	}
	public void setPop(int pop) {
		this.pop = pop;
	}
	public void setDelivery(int delivery) {
		this.delivery = delivery;
	}
	public int getCodeType() {
		return codeType;
	}
	public void setCodeType(int codeType) {
		this.codeType = codeType;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public int getSendStatus() {
		return sendStatus;
	}
	public void setSendStatus(int sendStatus) {
		this.sendStatus = sendStatus;
	}
	public int getFailedReason() {
		return failedReason;
	}
	public void setFailedReason(int failedReason) {
		this.failedReason = failedReason;
	}

}
