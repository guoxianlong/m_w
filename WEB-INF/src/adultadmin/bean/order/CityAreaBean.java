package adultadmin.bean.order;

/**
 *  <code>CityAreaBean.java</code>
 *  <p>功能:订单处理地址级联城市/区县实体类
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-2-25 下午02:28:53	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class CityAreaBean {

	/**
	 * id
	 */
	public int id;
	
	/**
	 * 城市id
	 */
	public int cityId;
	
	/**
	 * 区县名称
	 */
	public String area;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

}
