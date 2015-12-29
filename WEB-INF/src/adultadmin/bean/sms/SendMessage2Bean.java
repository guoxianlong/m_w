package adultadmin.bean.sms;

/**
 * 作者：赵林
 * 说明：群发下行短信
 *
 */
public class SendMessage2Bean {

	public int id;    //ID
	public String mobile;  //手机号
	public String sendDatetime;   //下发时间
	public int sendUserId;    //下发管理员ID
	public String sendUsername;   //下发管理员
	public String content;     //下发内容
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getSendDatetime() {
		return sendDatetime;
	}
	public void setSendDatetime(String sendDatetime) {
		this.sendDatetime = sendDatetime;
	}
	public int getSendUserId() {
		return sendUserId;
	}
	public void setSendUserId(int sendUserId) {
		this.sendUserId = sendUserId;
	}
	public String getSendUsername() {
		return sendUsername;
	}
	public void setSendUsername(String sendUsername) {
		this.sendUsername = sendUsername;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
}
