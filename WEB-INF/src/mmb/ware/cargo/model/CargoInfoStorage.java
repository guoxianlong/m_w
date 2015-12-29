package mmb.ware.cargo.model;

public class CargoInfoStorage {
    private Integer id;

    private String code;

    private String wholeCode;

    private String name;

    private Integer areaId;

    private Integer cityId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getWholeCode() {
        return wholeCode;
    }

    public void setWholeCode(String wholeCode) {
        this.wholeCode = wholeCode == null ? null : wholeCode.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }
}