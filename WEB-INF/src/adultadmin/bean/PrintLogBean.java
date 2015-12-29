/**
 * 
 */
package adultadmin.bean;

import adultadmin.action.vo.voUser;

/**
 * @author Bomb
 * 
 */
public class PrintLogBean {
	public static int PRINT_LOG_TYPE_BUYSTOCKIN = 0;
	public static int PRINT_LOG_TYPE_BUYPLAN = 1;
	public static int PRINT_LOG_TYPE_BUYSTOCK = 2;
	public static int PRINT_LOG_TYPE_BUYORDER = 3;
	public static int PRINT_LOG_TYPE_BUYRETURN = 4;
	public static int PRINT_LOG_TYPE_BATCHPRICE = 5;

	public int id;

	public int operId;

	public int userId;

	public String createDatetime;

	public voUser user;

	public int type;

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

	public int getOperId() {
		return operId;
	}

	public void setOperId(int operId) {
		this.operId = operId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public voUser getUser() {
		return user;
	}

	public void setUser(voUser user) {
		this.user = user;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
