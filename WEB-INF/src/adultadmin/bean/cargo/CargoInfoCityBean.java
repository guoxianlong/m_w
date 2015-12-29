package adultadmin.bean.cargo;

public class CargoInfoCityBean {
	public int id;
	public String code;//代号
	public String wholeCode;//完整编号(到本级前)
	public String name;//名称
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getWholeCode() {
		return wholeCode;
	}
	public void setWholeCode(String wholeCode) {
		this.wholeCode = wholeCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
