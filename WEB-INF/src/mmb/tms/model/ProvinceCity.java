package mmb.tms.model;

import java.util.List;

public class ProvinceCity {
    private Integer id;

    private Integer provinceId;

    private String city;

    private String code;

    private Boolean isFar;

    private Provinces provinces;
    
    private List<DeliverSendSpecial> specials;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Boolean getIsFar() {
        return isFar;
    }

    public void setIsFar(Boolean isFar) {
        this.isFar = isFar;
    }

	public Provinces getProvinces() {
		return provinces;
	}

	public void setProvinces(Provinces provinces) {
		this.provinces = provinces;
	}

	public List<DeliverSendSpecial> getSpecials() {
		return specials;
	}

	public void setSpecials(List<DeliverSendSpecial> specials) {
		this.specials = specials;
	}
}