package adultadmin.bean.cargo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class CargoDeptBean {
	public int id;
	public String name;//该级部门名称
	public String code;//该级部门代号
	public int parentId1;//一级部门id
	public int parentId2;//二级部门id
	public int parentId3;//三级部门id
	public int parentId0;//零级部门id
	
	public List juniorDeptList;//下级部门列表
	
	public int getParentId0() {
		return parentId0;
	}
	public void setParentId0(int parentId0) {
		this.parentId0 = parentId0;
	}
	public List getJuniorDeptList() {
		return juniorDeptList;
	}
	public void setJuniorDeptList(List juniorDeptList) {
		this.juniorDeptList = juniorDeptList;
	}
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
	public int getParentId1() {
		return parentId1;
	}
	public void setParentId1(int parentId1) {
		this.parentId1 = parentId1;
	}
	public int getParentId2() {
		return parentId2;
	}
	public void setParentId2(int parentId2) {
		this.parentId2 = parentId2;
	}
	public int getParentId3() {
		return parentId3;
	}
	public void setParentId3(int parentId3) {
		this.parentId3 = parentId3;
	}
	
	public static HashMap storeMap=new LinkedHashMap();
	static{
		storeMap.put("01", "GZZ");
		storeMap.put("02", "JSW");
	}
}
