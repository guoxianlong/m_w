package adultadmin.bean;

public class ProxyBean {
	public int id;
	public String name;
	public int seq;//决定在列表中显示的顺序
	public String desc;//暂时没用
	public String phone;//电话
	public String remark;//备注
	public String contactperson;//联系人
	public String createTime;//登记时间
	public String ping;
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
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getContactperson() {
		return contactperson;
	}
	public void setContactperson(String contactperson) {
		this.contactperson = contactperson;
	}
	public String getCreateDatetime() {
		return createTime;
	}
	public void setCreateDatetime(String createTime) {
		this.createTime = createTime;
	}
	public String getPing() {
		return ping;
	}
	public void setPing(String ping) {
		this.ping = ping;
	}
	
}
