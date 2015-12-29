/**
 * CargoStaffBean.java
 * @author liuruilan
 * create on 2011-12-13
 *
 */
package adultadmin.bean.cargo;

import java.io.Serializable;
import java.util.List;

import adultadmin.util.Constants;

public class CargoStaffBean implements Serializable{

	public int id;

	/**
	 * 员工姓名
	 */
	public String name;

	/**
	 * 员工编号
	 */
	public String code;
	
	/**
	 * 创建时间
	 */
	public String createDatetime;

	/**
	 * 所属部门名称,数据库表中无此属性
	 */
	public String deptName;
	
	/**
	 * 电话
	 */
	public String phone;

	/**
	 * 后台账户id
	 */
	public int userId;

	/**
	 * 后台账户名
	 */
	public String userName;

	
	/**
	 * 中元素为字符串，格式为’库地区-库类型’
	 */
	public List cargoDeptAreaList;

	
	/**
	 *  员工照片相对路径
	 */
	public String photoUrl;
	
	/**
	 * 图片前半段地址
	 */
	public String imageHead;
	
	/**
	 * 所属部门ID
	 */
	public int deptId;
	
	/**
	 * 删除状态  - 默认值为0， 1为已删除
	 */
	public int status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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


	public List getCargoDeptAreaList() {
		return cargoDeptAreaList;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getImageHead() {
		if(imageHead==null){
			imageHead=Constants.STAFF_PHOTO_URL;
		}
		return imageHead;
	}

	public void setImageHead(String imageHead) {
		this.imageHead = imageHead;
	}



	public void setCargoDeptAreaList(List cargoDeptAreaList) {
		this.cargoDeptAreaList = cargoDeptAreaList;
	}

	public int getDeptId() {
		return deptId;
	}

	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
