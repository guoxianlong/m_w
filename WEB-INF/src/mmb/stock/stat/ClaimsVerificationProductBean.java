package mmb.stock.stat;

import java.util.HashMap;
import java.util.Map;

import adultadmin.action.vo.voProduct;

public class ClaimsVerificationProductBean {
	
	public int id;
	public int claimsVerificationId;
	
	public String productCode;
	public int count;
	public int exist;
	public voProduct product;
	public String productLineName;
	public int claimsType;
	public String claimsTypeName;
	
	public static final int EXIST = 1;
	public static final int NOT_EXIST = 0;
	/**
	 * 整单理赔
	 */
	public static final int CLAIMS_TYPE0 = 0;
	/**
	 * 按sku理赔
	 */
	public static final int CLAIMS_TYPE1 = 1;
	/**
	 * 三倍邮费理赔
	 */
	public static final int CLAIMS_TYPE2 = 2;
	/**
	 * 包装理赔
	 */
	public static final int CLAIMS_TYPE3 = 3;
	
	public static Map<Integer,String> claimsTypeMap = new HashMap<Integer,String>();
	
	static {
		claimsTypeMap.put(ClaimsVerificationProductBean.CLAIMS_TYPE0, "整单理赔");
		claimsTypeMap.put(ClaimsVerificationProductBean.CLAIMS_TYPE1, "按sku理赔");
		claimsTypeMap.put(ClaimsVerificationProductBean.CLAIMS_TYPE2, "三倍邮费理赔");
		claimsTypeMap.put(ClaimsVerificationProductBean.CLAIMS_TYPE3, "包装理赔");
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getClaimsVerificationId() {
		return claimsVerificationId;
	}
	public void setClaimsVerificationId(int claimsVerificationId) {
		this.claimsVerificationId = claimsVerificationId;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getExist() {
		return exist;
	}
	public void setExist(int exist) {
		this.exist = exist;
	}
	public voProduct getProduct() {
		return product;
	}
	public void setProduct(voProduct product) {
		this.product = product;
	}
	public String getProductLineName() {
		return productLineName;
	}
	public void setProductLineName(String productLineName) {
		this.productLineName = productLineName;
	}
	public int getClaimsType() {
		return claimsType;
	}
	public void setClaimsType(int claimsType) {
		this.claimsType = claimsType;
	}
	
	public String getClaimsTypeName() {
		return claimsTypeMap.get(this.claimsType);
	}
}
