package mmb.util.excel;

/**
 * 
 *  <code>ExcelReportBean.java</code>
 *  <p>功能:
 *  
 *  <p>Copyright 商机无限 2012 All right reserved.
 *  @author 李双 lishuang@ebinf.com 时间 Nov 2, 2012 2:20:05 PM	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class ExcelReportBean {
	
	/**表头名字**/
	private String fielNames;

	/**对应表头名字 bean中字段**/
	private String fielNamesEN;

	/**bean class**/
	private Class classType;

	private int type;//2003 1 2007 2;

	/**导出excle名字**/
	private String excleName;

	private String[] fielNamesArray =null; 
	public String[] getFielNames() {
		if(fielNamesArray==null) fielNamesArray= fielNames.split(",");
		return fielNamesArray;
	}

	public void setFielNames(String fielNames) {
		this.fielNames = fielNames;
	}
	
	private String[] fielNamesENArray =null; 
	public String[] getFielNamesEN() {
		if(fielNamesENArray==null) fielNamesENArray =fielNamesEN.split(","); 
		return fielNamesENArray;
	}

	public void setFielNamesEN(String fielNamesEN) {
		this.fielNamesEN = fielNamesEN;
	}

	public Class getClassType() {
		return classType;
	}

	public void setClassType(Class classType) {
		this.classType = classType;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getExcleName() {
		return excleName;
	}

	public void setExcleName(String excleName) {
		this.excleName = excleName;
	}

}

