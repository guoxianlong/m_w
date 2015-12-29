package adultadmin.bean.stat;
/**
 * 	作者：石远飞
 *	日期：2013-4-16
 *	说明：异常入库单实际退回商品列表
 */
public class AbnormalRealProductBean {
	public int id;
	public int productId;	//商品id
	public String productCode;	//商品编号
	public int productCount;	//商品数量
	public String productName;	//商品名字
	public String productOriname;	//商品原名
	public int abnormalId;	//异常入库单id
	
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
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public int getProductCount() {
		return productCount;
	}
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductOriname() {
		return productOriname;
	}
	public void setProductOriname(String productOriname) {
		this.productOriname = productOriname;
	}
	public int getAbnormalId() {
		return abnormalId;
	}
	public void setAbnormalId(int abnormalId) {
		this.abnormalId = abnormalId;
	}
	
}
