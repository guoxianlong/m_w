package adultadmin.bean.bybs;
/**
 * 批量审批报损报益
 * @author hepeng
 * 2014-03-12
 */
public class CheckBsbyInfo {
	private int id;
	private String bsbyCode;//单据号
	private String productCode;//商品编号
	private int bsCount;//报损数量
	private int byCount;//报溢数量
	private String cargoCode;//货位号
	private String bsbyStatus;//是否 符合    0 不符合     1 标示符合数据
	private String remark;//备注信息
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getBsbyStatus() {
		return bsbyStatus;
	}
	public void setBsbyStatus(String bsbyStatus) {
		this.bsbyStatus = bsbyStatus;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBsbyCode() {
		return bsbyCode;
	}
	public void setBsbyCode(String bsbyCode) {
		this.bsbyCode = bsbyCode;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
	public int getBsCount() {
		return bsCount;
	}
	public void setBsCount(int bsCount) {
		this.bsCount = bsCount;
	}
	public int getByCount() {
		return byCount;
	}
	public void setByCount(int byCount) {
		this.byCount = byCount;
	}
	public String getCargoCode() {
		return cargoCode;
	}
	public void setCargoCode(String cargoCode) {
		this.cargoCode = cargoCode;
	}


}
