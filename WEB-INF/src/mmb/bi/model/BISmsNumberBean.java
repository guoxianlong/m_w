package mmb.bi.model;

import mmb.bi.model.BIOnGuradCountBean.EDepartment;

/**
 * 定时短信手机号
 * 
 * @author mengqy
 * 
 */
public class BISmsNumberBean {
	/**
	 * 自增id
	 */
	private int id;
	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 手机号
	 */
	private String number;
	/**
	 * 部门
	 */
	private EDepartment department;
	/**
	 * 职称
	 */
	private ETitle title;

	/**
	 * 创建人id
	 */
	private int createUserId;
	/**
	 * 创建人用户名
	 */
	private String createUsername;
	/**
	 * 创建时间
	 */
	private String createTime;

	/**
	 * 修改人id
	 */
	private int updateUserId;
	/**
	 * 修改人用户名
	 */
	private String updateUsername;
	/**
	 * 更新时间
	 */
	private String updateTime;

	/**
	 * 审核人id
	 */
	private int checkUserId;
	/**
	 * 审核人用户名
	 */
	private String checkUsername;
	/**
	 * 审核时间
	 */
	private String checkTime;

	/**
	 * 状态 0 未审核 1 审核通过 2审核未通过
	 */
	private EStatus status;

	/**
	 * 状态枚举
	 */
	public enum EStatus {
		/**
		 * 0 待审核
		 */
		Status0("待审核", 0),
		/**
		 * 1 审核通过
		 */
		Status1("审核通过", 1),
		/**
		 * 2 审核未通过
		 */
		Status2("审核未通过", 2);

		// 成员变量
		private String name;
		private Integer index;

		private EStatus(String name, Integer index) {
			this.name = name;
			this.index = index;
		}

		public static EStatus getEnum(Integer index) {
			for (EStatus c : EStatus.values()) {
				if (c.getIndex() == index) {
					return c;
				}
			}
			return null;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getIndex() {
			return index;
		}

		public void setIndex(Integer index) {
			this.index = index;
		}
	}

	/**
	 * 职称
	 * 
	 * @author mengqy
	 * 
	 */
	public enum ETitle {
		/**
		 * 0 总裁
		 */
		Title0("总裁", 0),
		/**
		 * 1 总经理
		 */
		Title1("总经理", 1),
		/**
		 * 2 总监
		 */
		Title2("总监", 2),
		/**
		 * 3 高级经理
		 */
		Title3("高级经理", 3),
		/**
		 * 4 经理
		 */
		Title4("经理", 4),
		/**
		 * 5 主管
		 */
		Title5("主管", 5);

		// 成员变量
		private String name;
		private Integer index;

		private ETitle(String name, Integer index) {
			this.name = name;
			this.index = index;
		}

		public static ETitle getEnum(Integer index) {
			for (ETitle c : ETitle.values()) {
				if (c.getIndex() == index) {
					return c;
				}
			}
			return null;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getIndex() {
			return index;
		}

		public void setIndex(Integer index) {
			this.index = index;
		}
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

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String udpateTime) {
		this.updateTime = udpateTime;
	}

	public int getStatus() {
		if (this.status == null) {
			return -1;
		}
		return status.getIndex();
	}

	public void setStatus(int status) {
		this.status = EStatus.getEnum(status);
	}

	public void setStatusByEnum(EStatus status) {
		this.status = status;
	}

	public String getStatusName() {
		if (this.status == null)
			return "";
		return this.status.getName();
	}

	public int getDepartment() {
		if(this.department == null)
			return -1;
		return department.getIndex();
	}
	
	public String getDepartmentName() {
		if(this.department == null)
			return null;
		return department.getName();
	}

	public void setDepartment(int department) {
		this.department = EDepartment.getEnum(department);
	}
	
	public void setDepartmentByEnum(EDepartment department)	{
		this.department = department;
	}

	public int getTitle() {
		if(this.title == null)
			return -1;
		return title.getIndex();
	}

	public void setTitle(int title) {
		this.title = ETitle.getEnum(title);
	}
	
	public void setTitleByEnum(ETitle title) {
		this.title = title;
	}
	
	public String getTitleName() {
		if(this.title == null)
			return null;
		return this.title.getName();
	}

	public int getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateUsername() {
		return createUsername;
	}

	public void setCreateUsername(String createUsername) {
		this.createUsername = createUsername;
	}

	public int getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(int updateUserId) {
		this.updateUserId = updateUserId;
	}

	public String getUpdateUsername() {
		return updateUsername;
	}

	public void setUpdateUsername(String updateUsername) {
		this.updateUsername = updateUsername;
	}

	public int getCheckUserId() {
		return checkUserId;
	}

	public void setCheckUserId(int checkUserId) {
		this.checkUserId = checkUserId;
	}

	public String getCheckUsername() {
		return checkUsername;
	}

	public void setCheckUsername(String checkUsername) {
		this.checkUsername = checkUsername;
	}

	public String getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}
	
}
