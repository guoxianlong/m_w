package adultadmin.bean.order;

import adultadmin.util.DateUtil;

public class OrderDealMessageBean {

	private String startDatetime;
	private String endDatetime;
	private int status;
	private String smsMessage;
	private long startTime;
	private long endTime;

	public OrderDealMessageBean(String startDatetime, String endDatetime,
			int status, String smsMessage) {
		super();
		this.startDatetime = startDatetime;
		this.endDatetime = endDatetime;
		this.status = status;
		this.smsMessage = smsMessage;
		this.startTime = DateUtil.parseDate(this.startDatetime, "yyyy-MM-dd").getTime();
		this.endTime = DateUtil.parseDate(this.endDatetime, "yyyy-MM-dd").getTime();
	}

	public String getStartDatetime() {
		return startDatetime;
	}

	public void setStartDatetime(String startDatetime) {
		this.startDatetime = startDatetime;
	}

	public String getEndDatetime() {
		return endDatetime;
	}

	public void setEndDatetime(String endDatetime) {
		this.endDatetime = endDatetime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSmsMessage() {
		return smsMessage;
	}

	public void setSmsMessage(String smsMessage) {
		this.smsMessage = smsMessage;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

}
