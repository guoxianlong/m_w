package mmb.contract.dto;

import org.apache.ibatis.type.Alias;

/**
 * 合同返利表
 * @description:
 * @create:2014年7月29日 下午1:53:52
 * @author:mawanjun
 */
@Alias("supplierContractMoney")
public class SupplierContractMoney {
	private int id;	//int(10)	ID		
	private int supplierId;//	int(10)	供应商ID		
	private int contractId;//	int(10)	合同ID		
	private double orderMoney;//	double(15)	订单总金额		
	private double contractMoney;//	double(15)	返利金额		
	private double restMoney;//	double(15)	消费金额		
	private int returnType;//	tinyint	返利类型类型（1不阶梯  2，重复阶梯  3，非重复阶梯）		
	public static Integer TYPE_UNSTEP=1;//1不阶梯
	public static Integer TYPE_REPEATSTEP=2;//重复阶梯
	public static Integer TYPE_STEP=3;//非重复阶梯
	private int dataType;//	tinyint	数据类型：1添加订单时添加的合同返利信息；2：返利金额消费信息	
	private int applicationId;//	int(10)	请款单ID	
	private String applicationCode;//	varchar(100)	请款单编号（data_type1时，记录描述信息）	
	private double shareMoney;//	double(15)	合同分摊的请款金额	

	private String contractCode;//合同编号，冗余字段，表里没有
	
	public String getContractCode() {
		return contractCode;
	}
	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}
	public int getContractId() {
		return contractId;
	}
	public void setContractId(int contractId) {
		this.contractId = contractId;
	}
	public double getOrderMoney() {
		return orderMoney;
	}
	public void setOrderMoney(double orderMoney) {
		this.orderMoney = orderMoney;
	}
	public double getContractMoney() {
		return contractMoney;
	}
	public void setContractMoney(double contractMoney) {
		this.contractMoney = contractMoney;
	}
	public double getRestMoney() {
		return restMoney;
	}
	public void setRestMoney(double restMoney) {
		this.restMoney = restMoney;
	}
	public int getReturnType() {
		return returnType;
	}
	public void setReturnType(int returnType) {
		this.returnType = returnType;
	}
	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	public int getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(int applicationId) {
		this.applicationId = applicationId;
	}
	public String getApplicationCode() {
		return applicationCode;
	}
	public void setApplicationCode(String applicationCode) {
		this.applicationCode = applicationCode;
	}
	public double getShareMoney() {
		return shareMoney;
	}
	public void setShareMoney(double shareMoney) {
		this.shareMoney = shareMoney;
	}
	
}
