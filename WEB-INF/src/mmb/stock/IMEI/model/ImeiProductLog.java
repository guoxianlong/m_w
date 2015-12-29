package mmb.stock.IMEI.model;

import java.sql.Timestamp;

/**
 * 商品IMEI码设置
 * @create yaoliang
 * @time  2015-10-13 09:10:25
 */
public class ImeiProductLog {
	
	private int id;
	private int productId;//产品id
	private String productCode;//产品编号
	private String storeName;//小店名称
	private String createTime;//创建时间
	private String operator;//操作人
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
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
}
