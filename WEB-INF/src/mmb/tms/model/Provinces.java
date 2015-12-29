package mmb.tms.model;

import java.util.List;

public class Provinces {

	/**
	 * 作用：与provinces表中id关联,id大于100000的省份属于京东的;id小于100000的省份属于买卖宝的
	 */
	public static final int PROVINCE_FLAG = 100000;
    private Integer id;

    private String name;
    
    private List<ProvinceCity> citys;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

	public List<ProvinceCity> getCitys() {
		return citys;
	}

	public void setCitys(List<ProvinceCity> citys) {
		this.citys = citys;
	}
    
}