package adultadmin.bean.barcode;

/**
 *  <code>ConsigPrintlogBean.java</code>
 *  <p>功能:流水线上打印信息日志
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-4-13 下午04:55:38	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class ConsigPrintlogBean {
	
	/**
	 *id 
	 */
	public int id;
	
	/**
	 * 打印用户id
	 */
	public int userId;                  
	
	/**
	 * 打印用户名
	 */
	public String printUsername; 
	
	/**
	 * 打印单子编号
	 */
	public String printCode; 
	
	/**
	 * 打印类型 0=发货单客户信息，1=打印包裹单，2=扫描包裹单号 默认=null
	 */
	public int printType ;
	
	/**
	 * 打印时间
	 */
	public String printDate ;  
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getPrintUsername() {
		return printUsername;
	}

	public void setPrintUsername(String printUsername) {
		this.printUsername = printUsername;
	}

	public String getPrintCode() {
		return printCode;
	}

	public void setPrintCode(String printCode) {
		this.printCode = printCode;
	}

	public int getPrintType() {
		return printType;
	}

	public void setPrintType(int printType) {
		this.printType = printType;
	}

	public String getPrintDate() {
		return printDate;
	}

	public void setPrintDate(String printDate) {
		this.printDate = printDate;
	}

}
