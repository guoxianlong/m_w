package adultadmin.bean.stock;
/**
 * 
 * 作者：朱爱林
 * 时间：2013-10-25
 * 说明：库类型表
 */
public class StockTypeBean {

	//id,name
	public int id;
	public String name;//库类型的名称
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
	
}
