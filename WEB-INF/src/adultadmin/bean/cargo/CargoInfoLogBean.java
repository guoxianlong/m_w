package adultadmin.bean.cargo;

//货位操作记录
public class CargoInfoLogBean {
	public int id;
	public int cargoId;
	public String operDatetime;
	public int operAdminId;
	public String operAdminName;
	public String remark;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCargoId() {
		return cargoId;
	}
	public void setCargoId(int cargoId) {
		this.cargoId = cargoId;
	}
	public String getOperDatetime() {
		return operDatetime;
	}
	public void setOperDatetime(String operDatetime) {
		this.operDatetime = operDatetime;
	}
	public int getOperAdminId() {
		return operAdminId;
	}
	public void setOperAdminId(int operAdminId) {
		this.operAdminId = operAdminId;
	}
	public String getOperAdminName() {
		return operAdminName;
	}
	public void setOperAdminName(String operAdminName) {
		this.operAdminName = operAdminName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
