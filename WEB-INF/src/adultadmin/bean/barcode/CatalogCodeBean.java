package adultadmin.bean.barcode;

import adultadmin.action.vo.voCatalog;

/**
 *  <code>ProductStandardsBean.java</code>
 *  <p>功能:产品分类编码实体类
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-1-6 上午11:39:07	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
/**
 *  <code>CatalogCodeBean.java</code>
 *  <p>功能:
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-1-10 下午06:05:05	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class CatalogCodeBean {

	/**
	 * id
	 */
	public int id;
	
	/**
	 * catalog表Id
	 */
	public int catalogId ;

	/**
	 * 规格id 外键
	 */
	public int standardsId;
	
	/**
	 * 分类编号
	 */
	public String catalogCode ;
	
	/**
	 * 编号标识，0表示一级分类，1二级分类
	 */
	public int codeFlag;

	/**
	 * 分类vo
	 */
	private voCatalog vocatalog;
	
	/**
	 * 规格名称
	 */
	private String standardsName; 
	
	public int getCodeFlag() {
		return codeFlag;
	}

	public void setCodeFlag(int codeFlag) {
		this.codeFlag = codeFlag;
	}

	public int getStandardsId() {
		return standardsId;
	}

	public void setStandardsId(int standardsId) {
		this.standardsId = standardsId;
	}

	public String getStandardsName() {
		return standardsName;
	}

	public void setStandardsName(String standardsName) {
		this.standardsName = standardsName;
	}

	public voCatalog getVocatalog() {
		return vocatalog;
	}

	public void setVocatalog(voCatalog vocatalog) {
		this.vocatalog = vocatalog;
	}

	public int getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(int catalogId) {
		this.catalogId = catalogId;
	}

	public String getCatalogCode() {
		return catalogCode;
	}

	public void setCatalogCode(String catalogCode) {
		this.catalogCode = catalogCode;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
