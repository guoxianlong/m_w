package adultadmin.bean.sms;

/**
 * 作者：赵林
 * 
 * 说明：上行短信
 *
 */
public class ReceiveMessageBean {

	public int id;         //ID
	public String mobile;  //手机号
	public String content; //内容
	public String addtime; //添加时间
	public int status;     //状态
	public String line;
	
	
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAddtime() {
		return addtime;
	}
	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}    
	
	
}
