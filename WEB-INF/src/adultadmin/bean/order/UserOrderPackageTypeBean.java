package adultadmin.bean.order;

/**
 *  <code>UserOrderPackageTypeBean.java</code>
 *  <p>功能:用户包裹单产品分类Bean
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-5-27 上午11:15:36	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class UserOrderPackageTypeBean {
	
	/**
	 * id
	 */
	public int id;

	/**
	 * 订单的产品分类
	 */
	public String name;
	
	/**
	 * 分类值
	 */
	public int typeId;
	
	/**
	 * 产品分类Id
	 */
	public int productCatalog;
	
	public String productIds;

	public int  checkOrder;
	
	/**
	 * 一级分类名称
	 */
	public String catalogName;

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

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public int getProductCatalog() {
		return productCatalog;
	}

	public void setProductCatalog(int productCatalog) {
		this.productCatalog = productCatalog;
	}

	public String getProductIds() {
		return productIds;
	}

	public void setProductIds(String productIds) {
		this.productIds = productIds;
	}

	public int getCheckOrder() {
		return checkOrder;
	}

	public void setCheckOrder(int checkOrder) {
		this.checkOrder = checkOrder;
	}

	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}
	
	
}
