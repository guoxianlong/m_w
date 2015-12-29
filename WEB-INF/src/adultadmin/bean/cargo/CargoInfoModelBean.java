package adultadmin.bean.cargo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adultadmin.bean.stock.ProductStockBean;


public class CargoInfoModelBean {
	private int passageId;             //巷道ID
	private String wholeCode;   //货位完整编号
	private int storageId;      //仓库ID
	private int productId;//产品id
	private int count;//退货合格产品数量
	private int stockCount;//合格货位散件区库存数量
	private int stockLockCount;//合格货物库存锁定量
	private int id;//目的货位id
	private int sourceCargoId;//源货位id
	private String sourceWholeCode;//源货位编号
	private List passageCode;//散件区巷道号，用于页面展现排序，A01,A02,A03
	private String pstrCode;//散件区巷道号，用于页面展现，A01;A02;A03
	private Map passageProduct = new HashMap();//巷道对应的商品,key为巷道号，value为List存放商品信息,returnedProductBean
	private String operationCode;//作业单编号
	private String operationStatus;//作业单状态
	private String cargoOpmaker;//制单人
	private String cargoOpDate;//制单日期
	private String auditor;//审核人
	private String auditorDate;//审核日期
	private List productBeanList = new ArrayList();//作业单中商品
	private List cargoOperList = new ArrayList();//作业单中作业操作记录
	private int cargoStockId;//源货位库存id
	private int inCargoStockId;//目标货位库存id
	private int returnProductCount;//退货库库存量
	private int returnProductLockCount;//退货库库存锁定量
	private int sourceStockCount;//源货位库存量
	private int sourceLockCount;//源货位锁定量
	private int status;//作业单状态
	private int cargoStatus;//货位状态
	private String retUpShelfCode;//退货汇总单编号
	private String cargoOperationIds;//货位操作记录id,以,号分割
	private int targetCargoType;//0整件区，1散件区
	private String cargoOperationCode;//上架单编号
	private String productCode;//产品编号
	
	private int areaId;  //地区id
	private String areaName; // 地区名称
	
	
	
	public String getWholeCode() {
		return wholeCode;
	}
	public void setWholeCode(String wholeCode) {
		this.wholeCode = wholeCode;
	}
	public int getStorageId() {
		return storageId;
	}
	public void setStorageId(int storageId) {
		this.storageId = storageId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getPassageId() {
		return passageId;
	}
	public void setPassageId(int passageId) {
		this.passageId = passageId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSourceCargoId() {
		return sourceCargoId;
	}
	public void setSourceCargoId(int sourceCargoId) {
		this.sourceCargoId = sourceCargoId;
	}
	public String getSourceWholeCode() {
		return sourceWholeCode;
	}
	public void setSourceWholeCode(String sourceWholeCode) {
		this.sourceWholeCode = sourceWholeCode;
	}
	public Map getPassageProduct() {
		return passageProduct;
	}
	public void setPassageProduct(Map passageProduct) {
		this.passageProduct = passageProduct;
	}
	public String getOperationCode() {
		return operationCode;
	}
	public void setOperationCode(String operationCode) {
		this.operationCode = operationCode;
	}
	public String getOperationStatus() {
		return operationStatus;
	}
	public void setOperationStatus(String operationStatus) {
		this.operationStatus = operationStatus;
	}
	public List getPassageCode() {
		return passageCode;
	}
	public void setPassageCode(List passageCode) {
		this.passageCode = passageCode;
	}
	public String getPstrCode() {
		return pstrCode;
	}
	public void setPstrCode(String pstrCode) {
		this.pstrCode = pstrCode;
	}
	public String getCargoOpmaker() {
		return cargoOpmaker;
	}
	public void setCargoOpmaker(String cargoOpmaker) {
		this.cargoOpmaker = cargoOpmaker;
	}
	public String getCargoOpDate() {
		return cargoOpDate;
	}
	public void setCargoOpDate(String cargoOpDate) {
		this.cargoOpDate = cargoOpDate;
	}
	public String getAuditor() {
		return auditor;
	}
	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}
	public String getAuditorDate() {
		return auditorDate;
	}
	public void setAuditorDate(String auditorDate) {
		this.auditorDate = auditorDate;
	}
	public List getProductBeanList() {
		return productBeanList;
	}
	public void setProductBeanList(List productBeanList) {
		this.productBeanList = productBeanList;
	}
	public List getCargoOperList() {
		return cargoOperList;
	}
	public void setCargoOperList(List cargoOperList) {
		this.cargoOperList = cargoOperList;
	}
	public int getCargoStockId() {
		return cargoStockId;
	}
	public void setCargoStockId(int cargoStockId) {
		this.cargoStockId = cargoStockId;
	}
	public int getInCargoStockId() {
		return inCargoStockId;
	}
	public void setInCargoStockId(int inCargoStockId) {
		this.inCargoStockId = inCargoStockId;
	}
	public int getStockCount() {
		return stockCount;
	}
	public void setStockCount(int stockCount) {
		this.stockCount = stockCount;
	}
	
	public boolean equals(Object otherObject) {
		if (this == otherObject)
			return true;
		if (otherObject == null)
			return false;
		if (getClass() != otherObject.getClass())
			return false;
		CargoInfoModelBean other = (CargoInfoModelBean) otherObject;
		return productId==other.productId;
	}

	public int hashCode() {
		int result = 17;
		int idValue = productId == 0 ? 0 : Integer.valueOf(productId).hashCode();
		result = result * 37 + idValue;
		return result;
	}
	public int getStockLockCount() {
		return stockLockCount;
	}
	public void setStockLockCount(int stockLockCount) {
		this.stockLockCount = stockLockCount;
	}
	public int getReturnProductCount() {
		return returnProductCount;
	}
	public void setReturnProductCount(int returnProductCount) {
		this.returnProductCount = returnProductCount;
	}
	public int getReturnProductLockCount() {
		return returnProductLockCount;
	}
	public void setReturnProductLockCount(int returnProductLockCount) {
		this.returnProductLockCount = returnProductLockCount;
	}
	public int getSourceStockCount() {
		return sourceStockCount;
	}
	public void setSourceStockCount(int sourceStockCount) {
		this.sourceStockCount = sourceStockCount;
	}
	public int getSourceLockCount() {
		return sourceLockCount;
	}
	public void setSourceLockCount(int sourceLockCount) {
		this.sourceLockCount = sourceLockCount;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getCargoStatus() {
		return cargoStatus;
	}
	public void setCargoStatus(int cargoStatus) {
		this.cargoStatus = cargoStatus;
	}
	public String getRetUpShelfCode() {
		return retUpShelfCode;
	}
	public void setRetUpShelfCode(String retUpShelfCode) {
		this.retUpShelfCode = retUpShelfCode;
	}
	public String getCargoOperationIds() {
		return cargoOperationIds;
	}
	public void setCargoOperationIds(String cargoOperationIds) {
		this.cargoOperationIds = cargoOperationIds;
	}
	public int getTargetCargoType() {
		return targetCargoType;
	}
	public void setTargetCargoType(int targetCargoType) {
		this.targetCargoType = targetCargoType;
	}
	public String getCargoOperationCode() {
		return cargoOperationCode;
	}
	public void setCargoOperationCode(String cargoOperationCode) {
		this.cargoOperationCode = cargoOperationCode;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
		this.areaName = ProductStockBean.getAreaName(areaId);
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
}
