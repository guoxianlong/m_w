package adultadmin.bean.stock;
/**
 * 
 * 作者：朱爱林
 * 时间：2013-10-25
 * 说明：库区域表
 */
public class StockAreaBean {
	
	public int id;
	public String name;//库区域名称
	public int type; //是否是发货仓库  1 是， 0 不是
	public int attribute;//地区属性 0 我司仓 1 非我司仓 
	
	/**
	 * 我司仓
	 */
	public static int OUR_WARE = 0;
	/**
	 * 非我司仓
	 */
	public static int NO_OUR_WARE = 1;
	
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getAttribute() {
		return attribute;
	}
	public void setAttribute(int attribute) {
		this.attribute = attribute;
	}
	
}
