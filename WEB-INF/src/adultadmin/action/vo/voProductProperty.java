package adultadmin.action.vo;

/**
 * 说明：产品附加属性Bean
 *
 * 创建日期：2011-01-24
 */
public class voProductProperty {
	
	/**
	 * 产品ID
	 */
	private int id;
	private int productId;
	private int productParentId1;  //产品一级分类ID
	private int productParentId2;  //产品二级分类ID	
	private int drove;  //适用人群
	private int size;   //尺寸号码
	private String model;   //产品型号
	private int color;
	private int mailingType;//是否有电池
	
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getProductParentId1() {
		return productParentId1;
	}
	public void setProductParentId1(int productParentId1) {
		this.productParentId1 = productParentId1;
	}
	public int getProductParentId2() {
		return productParentId2;
	}
	public void setProductParentId2(int productParentId2) {
		this.productParentId2 = productParentId2;
	}
	public int getDrove() {
		return drove;
	}
	public void setDrove(int drove) {
		this.drove = drove;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getMailingType() {
		return mailingType;
	}
	public void setMailingType(int mailingType) {
		this.mailingType = mailingType;
	}
	
}
