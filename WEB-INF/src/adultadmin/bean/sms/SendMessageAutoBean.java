package adultadmin.bean.sms;

/**
 * 作者：赵林
 * 
 * 说明：自动发送短信设置
 *
 */
public class SendMessageAutoBean {

	public int id;     //ID
	public int startHour;  //开始时间整点
	public int endHour;    //结束时间整点
	public String content; //预设内容
	public int status;     //状态
	public int hourSet;		//时间段设置是否开启

	/**
	 * 状态：关闭
	 */
	public static int STATUS0 = 0;
	
	/**
	 * 状态：开启
	 */
	public static int STATUS1 = 1;

	/**
	 * 状态：关闭
	 */
	public static int HOURSET_STATUS0 = 0;
	
	/**
	 * 状态：开启
	 */
	public static int HOURSET_STATUS1 = 1;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStartHour() {
		return startHour;
	}
	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}
	public int getEndHour() {
		return endHour;
	}
	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getHourSet() {
		return hourSet;
	}
	public void setHourSet(int hourSet) {
		this.hourSet = hourSet;
	}

	
}
