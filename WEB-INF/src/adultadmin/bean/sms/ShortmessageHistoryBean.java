package adultadmin.bean.sms;

/**
 * 作者:赵林
 * 
 * 说明：下行短信历史记录
 *
 */
public class ShortmessageHistoryBean {

	public int id;         //ID
	public String mobile;  //手机号
	public int type;       //短信类型
	public String content; //内容
	public String url;     //URL
	public String addtime; //添加时间
	public int status;     //状态
	
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
}
