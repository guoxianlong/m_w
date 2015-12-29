package mmb.stock.stat;

public class AbnormalOrderSearchLogBean {
	
	public int id;
	public int searchUserId;
	public String searchUserName;
	public String searchDatetime;
	public String orderCode;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSearchUserId() {
		return searchUserId;
	}
	public void setSearchUserId(int searchUserId) {
		this.searchUserId = searchUserId;
	}
	public String getSearchUserName() {
		return searchUserName;
	}
	public void setSearchUserName(String searchUserName) {
		this.searchUserName = searchUserName;
	}

	public String getSearchDatetime() {
		return searchDatetime;
	}
	public void setSearchDatetime(String searchDatetime) {
		this.searchDatetime = searchDatetime;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
		
}
