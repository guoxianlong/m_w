/*
 * Created on 2005-11-15
 *
 */
package adultadmin.bean;

import mmb.util.BinaryFlag;

/**
 * @author bomb
 * 
 */
public class UserPermissionBean {
	public int id;

	public int userId;

	public int securityLevel;

	public int permission;

	public int groupId;
	public int groupId2;
	public int groupId3;

	public int[] groups;

	public BinaryFlag flag;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPermission() {
		return permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	public int getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(int securityLevel) {
		this.securityLevel = securityLevel;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getGroupId2() {
		return groupId2;
	}

	public void setGroupId2(int groupId2) {
		this.groupId2 = groupId2;
	}

	public int getGroupId3() {
		return groupId3;
	}

	public void setGroupId3(int groupId3) {
		this.groupId3 = groupId3;
	}

	public int[] getGroups() {
		return groups;
	}

	public void setGroups(int[] groups) {
		this.groups = groups;
	}

	public BinaryFlag getFlag() {
		return flag;
	}

	public void setFlag(BinaryFlag flag) {
		this.flag = flag;
	}

	public boolean isInFlag(int id) {
		return this.flag.get(id);
	}

	public boolean isInGroups(int id) {
		if (groups == null) {
			return false;
		}
		for (int i = 0; i < groups.length; i++) {
			if (groups[i] == id) {
				return true;
			}
		}
		return false;
	}

}
