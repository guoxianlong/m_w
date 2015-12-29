/**
 * 
 */
package adultadmin.bean;

import adultadmin.action.vo.voUser;

/**
 * @author Bomb
 * 
 */
public class OrderImportLogBean {
	public int id;

	public int type;

	public String createDatetime;

	public int userId;

	public String content;

	public voUser user;

	public String getContent() {
		String temp = content.replaceAll("\r\n", "<br/>");
		return temp;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public voUser getUser() {
		if(user == null){
			return new voUser();
		}
		return user;
	}

	public void setUser(voUser user) {
		this.user = user;
	}

	public String getTypeName(){
		String value = null;
		switch(this.type){
			case 0:
				value = "导入包裹单号";
				break;
			case 2:
				value = "导入包裹单号(根据姓名手机号)";
				break;
			case 1:
				value = "设置订单状态(已结算)";
				break;
			case 3:
				value = "设置订单状态(已结算)(根据姓名手机号)";
				break;
			case 4:
				value = "设置订单状态(待退回)";
				break;
			case 5:
				value = "设置订单状态(待退回)(根据姓名手机号)";
				break;
			case 6:
				value = "设置订单状态(已妥投)";
				break;
			case 7:
				value = "设置订单状态(已妥投)(根据姓名手机号)";
				break;
			case 8:
				value = "导入退回未结算（按订单号）";
				break;
			case 9:
				value = "导入退回未结算（按姓名和手机号）";
				break;
			case 10:
				value = "导入退回已结算（按订单号）";
				break;
			case 11:
				value = "导入退回已结算（按姓名和手机号）";
				break;
			case 12:
				value = "导入妥投已结算（按订单号）";
				break;
			case 13:
				value = "导入妥投已结算（按姓名和手机号）";
				break;
			case 14:
				value = "导入结算数据-妥投已结";
				break;
			case 15:
				value = "导入结算数据-退单已结";
				break;
			default:
				value = "无此操作类型";
		}
		return value;
	}
}
