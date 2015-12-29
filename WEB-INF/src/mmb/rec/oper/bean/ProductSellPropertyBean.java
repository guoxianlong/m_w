package mmb.rec.oper.bean;

import java.util.HashMap;
import java.util.Map;

public class ProductSellPropertyBean {
	
	public int id;
	
	public int productId;
	/**
	 * 供应商id
	 */
	public int supplierId; 
	/**
	 * 产品线id
	 */
	public int productLineId; 
	/**
	 * 为1时 是代销
	 */
	public int type; 
	/**
	 * 提示
	 */
	public String remark;
	
	public static Map<Integer, String> typeMap = new HashMap<Integer, String>();
	
	/** 代销 **/
	public static int TYPE1 = 1;
	/** 经销 **/
	public static int TYPE2 = 2;
	
	static {
		typeMap.put(TYPE1, "代销");
		typeMap.put(TYPE2, "经销");
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
	public int getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}
	public int getProductLineId() {
		return productLineId;
	}
	public void setProductLineId(int productLineId) {
		this.productLineId = productLineId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	} 
	

}
