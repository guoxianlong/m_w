package mmb.stock.stat;

import adultadmin.bean.stock.ProductStockBean;

/**
 * 
 * @author liubo
 * 暂存号
 *
 */
public class TemporaryNumberBean {

	
	public int id;
	public String name;
	public int area;
	private String areaName;//地区名称
	
	
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
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	public String getAreaName() {
		areaName = (String) ProductStockBean.areaMap.get(this.getArea());
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
}
