package adultadmin.bean.order;

/**
 *  <code>CityAreaBean.java</code>
 *  <p>功能:订单处理地址级联区县/街道实体类
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-2-25 下午02:28:53	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class AreaStreetBean {

	/**
	 * id
	 */
	public int id;
	
	/**
	 * 区县id
	 */
	public int areaId;
	
	/**
	 * 街道名称
	 */
	public String street;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}
}
