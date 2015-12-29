package adultadmin.action.vo;

/**
 *  <code>ProductBarcodevVO.java</code>
 *  <p>功能:产品条形码VO
 *  
 *  <p>Copyright 商机无限 2010 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2010-12-28 下午03:34:15	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class ProductBarcodeVO {

	/**
	 * 条形码id
	 */
	public int id;
	
	/**
	 * 产品id
	 */
	public int productId;
	
	/**
	 * 条形码
	 */
	public String barcode;
	
	/**
	 * 条形码来源 1 产品自带，2系统生成，默认0 无来源
	 */
	public int barcodeSource;
	
	/**
	 * 系统生成的后4位值0001-9999
	 */
	public String sysValue;
	
	/**
	 * 条码状态 0 有效，1无效 默认0
	 */
	public int barcodeStatus;
	

	public int getBarcodeStatus() {
		return barcodeStatus;
	}

	public void setBarcodeStatus(int barcodeStatus) {
		this.barcodeStatus = barcodeStatus;
	}

	public String getSysValue() {
		return sysValue;
	}

	public void setSysValue(String sysValue) {
		this.sysValue = sysValue;
	}

	/**
	 * 条形码来源字符串形式
	 */
	private String barSource;

	public String getBarSource() {
		if(barcodeSource==0)
			barSource="";
		else if(barcodeSource==1)
			barSource=" 产品自带";
		else if(barcodeSource==2)
			barSource="系统生成";
			
		return barSource;
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

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public int getBarcodeSource() {
		return barcodeSource;
	}

	public void setBarcodeSource(int barcodeSource) {
		this.barcodeSource = barcodeSource;
	}
	
}
