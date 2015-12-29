/*
 * Created on 2005-11-15
 *
 */
package adultadmin.bean;

/**
 * @author bomb
 * 
 */
public class PermissionBean {
	
	int id;
	String name;		// 权限名称
	String bak;			// 详细描述
	
	int parent;			// 父结点id
	int seq;			// 显示顺序

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public int getParent() {
		return parent;
	}
	public void setParent(int parent) {
		this.parent = parent;
	}
	public String getBak() {
		return bak;
	}
	public void setBak(String bak) {
		this.bak = bak;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


}
