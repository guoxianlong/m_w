package adultadmin.bean.cargo;

public class CargoInfoStorageBean {
	public int id;
	public String code;//代号
	public String wholeCode;//完整编号(到本级前)
	public String name;//名称
	public int areaId;//区域ID
	public int cityId;//城市ID
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
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	
	
}
