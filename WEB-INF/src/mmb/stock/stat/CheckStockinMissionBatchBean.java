package mmb.stock.stat;

import adultadmin.action.vo.voProduct;
import adultadmin.bean.buy.BuyStockProductBean;

/**
 * @name 质检入库任务批次
 * @author hyb
 *
 */
public class CheckStockinMissionBatchBean {

	public int buyCount; //预计到货数量
	public int buyStockinId; //预计单id
	public int checkCount; //质检数量
	public String completeDatetime; //完成时间
	public int id; //批次任务id
	public int missionId; //任务单id
	public int productId; //产品id
	public int qualifiedCount; //合格数量
	public int status; //状态
	public int stockinCount; //实际到货数量
	public String stockinDatetime; //入库时间
	public int supplierId; //供应商id
	public String supplierName; //供应商姓名
	public voProduct product;
	public CheckStockinMissionBean checkStockinMission;
	public BuyStockProductBean buyStockProduct;
	public String unqualifiedReasons;//所有不合格原因拼接成的语句
	public int unqualifiedNumber; //不合格数量计算结果
	public String tempNum;//暂存号
	
	
	private String statusName;//状态名称
	private int differenceValue;//差异量
	private boolean showButton;//是否显示编辑按钮
	
	//以下字段为标准装箱量所使用
	public int firstCheckCount;//初录数量
	public int secondCheckCount;//复录数量
	public int firstCheckStatus;//初录状态
	public int secondCheckStatus;//复录状态
	public int leftCount;//余量
	public int currentCheckCount;//当前初录数

	public int getFirstCheckCount() {
		return firstCheckCount;
	}
	public void setFirstCheckCount(int firstCheckCount) {
		this.firstCheckCount = firstCheckCount;
	}
	public int getSecondCheckCount() {
		return secondCheckCount;
	}
	public void setSecondCheckCount(int secondCheckCount) {
		this.secondCheckCount = secondCheckCount;
	}
	public int getFirstCheckStatus() {
		return firstCheckStatus;
	}
	public void setFirstCheckStatus(int firstCheckStatus) {
		this.firstCheckStatus = firstCheckStatus;
	}
	public int getSecondCheckStatus() {
		return secondCheckStatus;
	}
	public void setSecondCheckStatus(int secondCheckStatus) {
		this.secondCheckStatus = secondCheckStatus;
	}
	public int getLeftCount() {
		return leftCount;
	}
	public void setLeftCount(int leftCount) {
		this.leftCount = leftCount;
	}
	public int getCurrentCheckCount() {
		return currentCheckCount;
	}
	public void setCurrentCheckCount(int currentCheckCount) {
		this.currentCheckCount = currentCheckCount;
	}
	

	/**
	 * 未处理
	 */
	public static int STATUS0 = 0;

	/**
	 * 已确认到货
	 */
	public static int STATUS1 = 1;
	
	/**
	 * 质检入库中
	 */
	public static int STATUS2 = 2;
	
	
	/**
	 * 已完成
	 */
	public static int STATUS3 = 3;
	
	
	/**
	 * 已删除
	 */
	public static int STATUS4 = 4;
	
	
	public String getUnqualifiedReasons() {
		return unqualifiedReasons;
	}
	public void setUnqualifiedReasons(String unqualifiedReasons) {
		this.unqualifiedReasons = unqualifiedReasons;
	}
	public int getUnqualifiedNumber() {
		return unqualifiedNumber;
	}
	public void setUnqualifiedNumber(int unqualifiedNumber) {
		this.unqualifiedNumber = unqualifiedNumber;
	}
	
	
	public BuyStockProductBean getBuyStockProduct() {
		return buyStockProduct;
	}
	public void setBuyStockProduct(BuyStockProductBean buyStockProduct) {
		this.buyStockProduct = buyStockProduct;
	}
	public CheckStockinMissionBean getCheckStockinMission() {
		return checkStockinMission;
	}
	public void setCheckStockinMission(CheckStockinMissionBean checkStockinMission) {
		this.checkStockinMission = checkStockinMission;
	}
	public voProduct getProduct() {
		return product;
	}
	public void setProduct(voProduct product) {
		this.product = product;
	}
	public int getBuyCount() {
		return buyCount;
	}
	public int getBuyStockinId() {
		return buyStockinId;
	}
	public int getCheckCount() {
		return checkCount;
	}
	public String getCompleteDatetime() {
		return completeDatetime;
	}
	public int getId() {
		return id;
	}
	public int getMissionId() {
		return missionId;
	}
	public int getProductId() {
		return productId;
	}
	public int getQualifiedCount() {
		return qualifiedCount;
	}
	public int getStatus() {
		return status;
	}
	public int getStockinCount() {
		return stockinCount;
	}
	public String getStockinDatetime() {
		return stockinDatetime;
	}
	public int getSupplierId() {
		return supplierId;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setBuyCount(int buyCount) {
		this.buyCount = buyCount;
	}
	public void setBuyStockinId(int buyStockinId) {
		this.buyStockinId = buyStockinId;
	}
	public void setCheckCount(int checkCount) {
		this.checkCount = checkCount;
	}
	public void setCompleteDatetime(String completeDatetime) {
		this.completeDatetime = completeDatetime;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setMissionId(int missionId) {
		this.missionId = missionId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public void setQualifiedCount(int qualifiedCount) {
		this.qualifiedCount = qualifiedCount;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public void setStockinCount(int stockinCount) {
		this.stockinCount = stockinCount;
	}
	public void setStockinDatetime(String stockinDatetime) {
		this.stockinDatetime = stockinDatetime;
	}
	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public int getDifferenceValue() {
		return differenceValue;
	}
	public void setDifferenceValue(int differenceValue) {
		this.differenceValue = differenceValue;
	}
	public boolean isShowButton() {
		return showButton;
	}
	public void setShowButton(boolean showButton) {
		this.showButton = showButton;
	}
	public String getTempNum() {
		return tempNum;
	}
	public void setTempNum(String tempNum) {
		this.tempNum = tempNum;
	}
	
	public static String getStatusName(int status){
		if(CheckStockinMissionBatchBean.STATUS0==status){
			return "未处理";
		}else if(CheckStockinMissionBatchBean.STATUS1==status){
			return "已确认到货";
		}else if(CheckStockinMissionBatchBean.STATUS2==status){
			return "质检入库中";
		}else if(CheckStockinMissionBatchBean.STATUS3==status){
			return "已完成";
		}else{
			return "已删除";
		}
	}
	
}
