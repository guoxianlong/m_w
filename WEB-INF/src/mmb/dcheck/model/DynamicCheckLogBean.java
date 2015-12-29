package mmb.dcheck.model;

import java.io.Serializable;

/**
 * @description 
 * @create 2015-7-2 下午03:59:36
 * @author gel
 */
public class DynamicCheckLogBean implements Serializable {

	/** 
	 * @fields serialVersionUID:TODO
	 */
	private static final long serialVersionUID = 1L;
	
	
	private int id;
	private int operator;
	private int stockAreaId;
	private int cargoInfoPassageId;
	private int cargoInfoAreaId;
	private int groupId;
	private String dynamicCheckCode;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOperator() {
		return operator;
	}
	public void setOperator(int operator) {
		this.operator = operator;
	}
	public int getStockAreaId() {
		return stockAreaId;
	}
	public void setStockAreaId(int stockAreaId) {
		this.stockAreaId = stockAreaId;
	}
	public int getCargoInfoPassageId() {
		return cargoInfoPassageId;
	}
	public void setCargoInfoPassageId(int cargoInfoPassageId) {
		this.cargoInfoPassageId = cargoInfoPassageId;
	}
	public int getCargoInfoAreaId() {
		return cargoInfoAreaId;
	}
	public void setCargoInfoAreaId(int cargoInfoAreaId) {
		this.cargoInfoAreaId = cargoInfoAreaId;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public String getDynamicCheckCode() {
		return dynamicCheckCode;
	}
	public void setDynamicCheckCode(String dynamicCheckCode) {
		this.dynamicCheckCode = dynamicCheckCode;
	}
	
	
}
