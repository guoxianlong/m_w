package adultadmin.action.vo;

/**
 * 作者：李宁
 * 
 * 创建日期：2011-02-15
 * 
 * 说明：产品线关联分类信息
 * 
 */
public class voProductLineCatalog {
	/**
	 * id
	 * */
	private int id;
	
	/**
	 * 所属产品线id
	 * */
	private int product_line_id;
	
	/**
	 * 所属产品线名称
	 */
	private String product_line_name;

	/**
	 * 产品分类id
	 * */
	private int catalog_id;
	
	/**
	 * 产品分类类型(0:一级分类;1:二级分类)
	 * */
	private int catalog_type;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getProduct_line_name() {
		return product_line_name;
	}

	public void setProduct_line_name(String product_line_name) {
		this.product_line_name = product_line_name;
	}
	
	public int getProduct_line_id() {
		return product_line_id;
	}

	public void setProduct_line_id(int product_line_id) {
		this.product_line_id = product_line_id;
	}

	public int getCatalog_id() {
		return catalog_id;
	}

	public void setCatalog_id(int catalog_id) {
		this.catalog_id = catalog_id;
	}

	public int getCatalog_type() {
		return catalog_type;
	}

	public void setCatalog_type(int catalog_type) {
		this.catalog_type = catalog_type;
	}
}
