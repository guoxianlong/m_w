package mmb.dcheck.model;

import java.io.Serializable;

/**
 * @description 
 * @create 2015-7-2 下午06:10:40
 * @author gel
 */
public class DynamicCheckData implements Serializable {

	/** 
	 * @fields serialVersionUID:TODO
	 */
	private static final long serialVersionUID = 5101849589486401607L;
	
	private int id;
	private int dynamicCheckId;
	private int cargoId;
	private String cargoWholeCode;
	private int cargoInfoStockAreaId;
	private int cargoInfoPassageId;
	private int productId;
	private String productCode;
	private String productName;
	private int checkCount;
	private int checkGroup;
	private int operator;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDynamicCheckId() {
		return dynamicCheckId;
	}
	public void setDynamicCheckId(int dynamicCheckId) {
		this.dynamicCheckId = dynamicCheckId;
	}
	public int getCargoId() {
		return cargoId;
	}
	public void setCargoId(int cargoId) {
		this.cargoId = cargoId;
	}
	public String getCargoWholeCode() {
		return cargoWholeCode;
	}
	public void setCargoWholeCode(String cargoWholeCode) {
		this.cargoWholeCode = cargoWholeCode;
	}
	public int getCargoInfoStockAreaId() {
		return cargoInfoStockAreaId;
	}
	public void setCargoInfoStockAreaId(int cargoInfoStockAreaId) {
		this.cargoInfoStockAreaId = cargoInfoStockAreaId;
	}
	public int getCargoInfoPassageId() {
		return cargoInfoPassageId;
	}
	public void setCargoInfoPassageId(int cargoInfoPassageId) {
		this.cargoInfoPassageId = cargoInfoPassageId;
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
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getCheckCount() {
		return checkCount;
	}
	public void setCheckCount(int checkCount) {
		this.checkCount = checkCount;
	}
	public int getCheckGroup() {
		return checkGroup;
	}
	public void setCheckGroup(int checkGroup) {
		this.checkGroup = checkGroup;
	}
	public int getOperator() {
		return operator;
	}
	public void setOperator(int operator) {
		this.operator = operator;
	}
	
	
}
