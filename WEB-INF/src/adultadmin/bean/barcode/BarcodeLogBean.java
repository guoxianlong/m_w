package adultadmin.bean.barcode;

/**
 *  <code>BarcodeLogBean.java</code>
 *  <p>功能:条码日志实体类
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-1-18 下午03:13:13	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class BarcodeLogBean {

	/**
	 * id
	 */
	public int id; 
	
	/**
	 * 产品id
	 */
	public int productId ;
	
	/**
	 * 产品原名称
	 */
	public String oriname;
	
	/**
	 * 小店名称
	 */
	public String pname;
	
	/**
	 * 修改前条码
	 */
	public String oldBarcode;
	
	/**
	 * 修改后的条码
	 */
	public String barcode; 
	
	/**
	 * 操作人名
	 */
	public String adminName;
	
	/**
	 * 操作时间
	 */
	public String operDatetime;

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

	public String getOriname() {
		return oriname;
	}

	public void setOriname(String oriname) {
		this.oriname = oriname;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getOldBarcode() {
		return oldBarcode;
	}

	public void setOldBarcode(String oldBarcode) {
		this.oldBarcode = oldBarcode;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getOperDatetime() {
		return operDatetime;
	}

	public void setOperDatetime(String operDatetime) {
		this.operDatetime = operDatetime;
	}
}
