package adultadmin.bean.sms;

/**
 * 作者：赵林
 * 
 * 说明：系统自动发送短信历史记录
 *
 */
public class SendMessageAutoHistoryBean {

	public int id;           //ID
	public String phone;     //手机号
	public String content;   //内容
	public String createDatetime; //创建时间
	public int type;         //类型
	
	public static int TYPE_订单上行短信自动回复 = 0;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
}
