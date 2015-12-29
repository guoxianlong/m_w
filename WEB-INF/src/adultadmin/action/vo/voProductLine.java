package adultadmin.action.vo;

/**
 * 作者：李宁
 * 
 * 创建日期：2011-02-15
 * 
 * 说明：产品线详细信息
 * 
 */
public class voProductLine {
	/**
	 * 产品线id
	 * */
	private int id;
	
	/**
	 * 产品线名称，不能重复
	 * */
	private String name;
	
	/**
	 * 产品线备注
	 * */
	private String remark;
	
	/**
	 * 产品线状态(预留)
	 * */
	private int status;
	
	private int permissionId;
	
	/**
	 * 是否有关联分类
	 */
	private boolean isexist;

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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public boolean isIsexist() {
		return isexist;
	}

	public void setIsexist(boolean isexist) {
		this.isexist = isexist;
	}

	public int getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(int permissionId) {
		this.permissionId = permissionId;
	}
	
}
