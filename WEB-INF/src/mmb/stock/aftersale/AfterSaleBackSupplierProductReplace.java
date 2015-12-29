package mmb.stock.aftersale;

import java.util.HashMap;
import java.util.Map;

public class AfterSaleBackSupplierProductReplace {
	/**
	 * 待审核
	 */
	public static final int AUDIT_STATUS1 = 1;
	/**
	 * 审核通过
	 */
	public static final int AUDIT_STATUS2 = 2;
	/**
	 * 审核不通过
	 */
	public static final int AUDIT_STATUS3 = 3;
	
	public static Map<Integer,String> auditStatusMap = new HashMap<Integer, String>();
	
	static {
		auditStatusMap.put(AUDIT_STATUS1, "待审核");
		auditStatusMap.put(AUDIT_STATUS2, "审核通过");
		auditStatusMap.put(AUDIT_STATUS3, "审核不通过");
	}
	
	public int id;
	public String code;
	public String newImei;
	public String oldImei;
	public int newProductId;
	public int oldProductId;
	public String createDatetime;
	public int auditStatus;
	public int supplierId;
	public String supplierName;
	public int createUserId;
	public String createUserName;
	public int auditUserId;
	public String auditUserName;
	public float oldPrice;
	public float newPrice;
	public int detectId;
	public String auditDatetime;
	public int backSupplierProductId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getNewImei() {
		return newImei;
	}
	public void setNewImei(String newImei) {
		this.newImei = newImei;
	}
	public String getOldImei() {
		return oldImei;
	}
	public void setOldImei(String oldImei) {
		this.oldImei = oldImei;
	}
	public int getNewProductId() {
		return newProductId;
	}
	public void setNewProductId(int newProductId) {
		this.newProductId = newProductId;
	}
	public int getOldProductId() {
		return oldProductId;
	}
	public void setOldProductId(int oldProductId) {
		this.oldProductId = oldProductId;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public int getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(int auditStatus) {
		this.auditStatus = auditStatus;
	}
	public int getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public int getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public int getAuditUserId() {
		return auditUserId;
	}
	public void setAuditUserId(int auditUserId) {
		this.auditUserId = auditUserId;
	}
	public String getAuditUserName() {
		return auditUserName;
	}
	public void setAuditUserName(String auditUserName) {
		this.auditUserName = auditUserName;
	}
	public float getOldPrice() {
		return oldPrice;
	}
	public void setOldPrice(float oldPrice) {
		this.oldPrice = oldPrice;
	}
	public float getNewPrice() {
		return newPrice;
	}
	public void setNewPrice(float newPrice) {
		this.newPrice = newPrice;
	}
	public int getDetectId() {
		return detectId;
	}
	public void setDetectId(int detectId) {
		this.detectId = detectId;
	}
	public String getAuditDatetime() {
		return auditDatetime;
	}
	public void setAuditDatetime(String auditDatetime) {
		this.auditDatetime = auditDatetime;
	}
	public int getBackSupplierProductId() {
		return backSupplierProductId;
	}
	public void setBackSupplierProductId(int backSupplierProductId) {
		this.backSupplierProductId = backSupplierProductId;
	}
}
