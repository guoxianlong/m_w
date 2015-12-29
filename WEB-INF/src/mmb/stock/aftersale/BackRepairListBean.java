package mmb.stock.aftersale;

/**
 * 送修清单
 * @author 李宁
 * @date 2014-2-21下午6:27:37
 */
public class BackRepairListBean {
	
	private String contract;//联系人
	private String shipDate;//发货日期
	private String supplierName;//收货厂商名称
	private String packageCode;//运输单号
	private String remark;//备注
	/** 发货地址 */
	public String deliveryAddress;
	/** 联系电话 */
	public String contractPhone;
	/** 邮政编码 */
	public String zipCode;
	
	
	
	public String getDeliveryAddress() {
		return deliveryAddress;
	}
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
	public String getContractPhone() {
		return contractPhone;
	}
	public void setContractPhone(String contractPhone) {
		this.contractPhone = contractPhone;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getContract() {
		return contract;
	}
	public void setContract(String contract) {
		this.contract = contract;
	}
	public String getShipDate() {
		return shipDate;
	}
	public void setShipDate(String shipDate) {
		this.shipDate = shipDate;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public String getPackageCode() {
		return packageCode;
	}
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
