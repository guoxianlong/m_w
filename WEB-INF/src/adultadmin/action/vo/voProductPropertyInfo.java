package adultadmin.action.vo;

/**
 * 说明：产品附加属性基本信息Bean
 *
 * 创建时间：2011-02-22
 */
public class voProductPropertyInfo {

	/**
	 *  属性类型：产品颜色
	 */
	public final static int PROPERTY_TYPE_COLOR = 0;
	
	/**
	 *  属性类型：产品品牌
	 */
	public final static int PROPERTY_TYPE_BRAND = 1;
	
	/**
	 *  属性类型：产品鞋尺码
	 */
	public final static int PROPERTY_TYPE_SHOE_SIZE = 2;
	
	/**
	 * 属性类型： 产品服装尺码
	 */
	public final static int PROPERTY_TYPE_CLOTHES_SIZE = 3;

	/**
	 * 属性类型： 物流商品邮寄类型
	 */
	public final static int PROPERTY_TYPE_MAILING_TYPE = 4;
	
	private int id;  //ID，主键，自增长
	private String name;  //产品属性描述名称
	private int value;  //产品属性对应值
	private int type;  //产品属性类别
	private int sortId;  //排序ID
	
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
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getSortId() {
		return sortId;
	}
	public void setSortId(int sortId) {
		this.sortId = sortId;
	}
	
}
