package mmb.tp.bean;

import java.io.Serializable;

public class DeliverInfoSearchLog implements Serializable {

	private int id;//用户ID
	private String username;//用户名
	private String password;//密码
	private String createDatetime;//创建时间
	private String lastModifyDatetime;//最后一次修改时间
	private int flag;//0非管理员，1管理员
	private String name;//名字
	private int deliverId;//快递公司id
	private String deliverName;//快递公司名称
	private String phone;//电话
	private int pvLimit;//日访问量限制
	private int currentSearchCount;//当天访问量
	private int allSearchCount;//总访问量
	private int lastSearchtime;//最后访问时间
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public String getLastModifyDatetime() {
		return lastModifyDatetime;
	}
	public void setLastModifyDatetime(String lastModifyDatetime) {
		this.lastModifyDatetime = lastModifyDatetime;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDeliverId() {
		return deliverId;
	}
	public void setDeliverId(int deliverId) {
		this.deliverId = deliverId;
	}
	public String getDeliverName() {
		return deliverName;
	}
	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getPvLimit() {
		return pvLimit;
	}
	public void setPvLimit(int pvLimit) {
		this.pvLimit = pvLimit;
	}
	public int getCurrentSearchCount() {
		return currentSearchCount;
	}
	public void setCurrentSearchCount(int currentSearchCount) {
		this.currentSearchCount = currentSearchCount;
	}
	public int getAllSearchCount() {
		return allSearchCount;
	}
	public void setAllSearchCount(int allSearchCount) {
		this.allSearchCount = allSearchCount;
	}
	public int getLastSearchtime() {
		return lastSearchtime;
	}
	public void setLastSearchtime(int lastSearchtime) {
		this.lastSearchtime = lastSearchtime;
	}
	
	
}
