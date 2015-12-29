package adultadmin.bean.barcode;


/**
 *  <code>BatchBarcodePrintlogBean.java</code>
 *  <p>功能:产品批次条码打印单据日志
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-3-18 下午03:42:14	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class BatchBarcodePrintlogBean {

	/**
	 * id
	 */
	public int id ;
	
	/**
	 * 打印用户
	 */
	public String printUserName; 
	
	/**
	 * 打印单据编号
	 */
	public String printOrderCode ;
	
	/**
	 * 打印单据时间
	 */
	public String printDate;
	
	/**
	 * 打印数量
	 */
	public int printNum;
	
	/**
	 * 备注
	 */
	public String remark;
	
	/**
	 * 产品id
	 */
	public String productCode;
	
	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	/**
	 * 产品批次条码
	 */
	public String productBatchBarcode; 

	public String getProductBatchBarcode() {
		return productBatchBarcode;
	}

	public void setProductBatchBarcode(String productBatchBarcode) {
		this.productBatchBarcode = productBatchBarcode;
	}

	public String getPrintUserName() {
		return printUserName;
	}

	public void setPrintUserName(String printUserName) {
		this.printUserName = printUserName;
	}

	public String getPrintOrderCode() {
		return printOrderCode;
	}

	public void setPrintOrderCode(String printOrderCode) {
		this.printOrderCode = printOrderCode;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPrintDate() {
		return printDate;
	}

	public void setPrintDate(String printDate) {
		this.printDate = printDate;
	}

	public int getPrintNum() {
		return printNum;
	}

	public void setPrintNum(int printNum) {
		this.printNum = printNum;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
