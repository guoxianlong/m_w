package mmb.stock.aftersale;

//检测信息表
public class AfterSaleDetectLogBean {
	public int id;
	public int afterSaleDetectProductId;//售后处理单id
	public int afterSaleDetectTypeId; //检测选项id
	public String content; //一级检测内容，文本
	public String content2;//二级检测内容
	public String content3;//三级检测内容
	public int userId; //检测人id
	public String userName; //检测人姓名
	public String createDatetime; //生成时间
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAfterSaleDetectProductId() {
		return afterSaleDetectProductId;
	}
	public void setAfterSaleDetectProductId(int afterSaleDetectProductId) {
		this.afterSaleDetectProductId = afterSaleDetectProductId;
	}
	public int getAfterSaleDetectTypeId() {
		return afterSaleDetectTypeId;
	}
	public void setAfterSaleDetectTypeId(int afterSaleDetectTypeId) {
		this.afterSaleDetectTypeId = afterSaleDetectTypeId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public String getContent2() {
		return content2;
	}
	public void setContent2(String content2) {
		this.content2 = content2;
	}
	public String getContent3() {
		return content3;
	}
	public void setContent3(String content3) {
		this.content3 = content3;
	}
	
}
