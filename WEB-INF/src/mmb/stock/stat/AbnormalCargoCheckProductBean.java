package mmb.stock.stat;

import java.util.HashMap;
import java.util.LinkedHashMap;

import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;

//异常货位盘点计划商品
public class AbnormalCargoCheckProductBean {
	public int id;
	public int productId;//商品id
	public String productCode;//商品编号
	public String productName;//商品名
	public String cargoWholeCode;//货位号
	public int lockCount;//锁定量
	public int firstCheckCount;//初盘数
	public int firstCheckUserId;//初盘人id
	public String firstCheckUserName;//初盘人姓名
	public int secondCheckCount;//复盘数
	public int secondCheckUserId;//复盘人id
	public String secondCheckUserName;//复盘人姓名
	public int thirdCheckCount;//终盘数
	public int thirdCheckUserId;//终盘人id
	public String thirdCheckUserName;//终盘人姓名
	public int finalCheckCount; //最终确定的盘点量的值
	public int bsbyId;  //报损报溢id
	
	public BsbyOperationnoteBean bsbyBean;  // 报损报溢的信息Bean
	
	private int eventuallyCheckCount;  // 最终盘点量 
	private CargoInfoBean cargoInfoBean;  // 货位信息
	private CargoProductStockBean cargoProductStockBean;  //货位库存信息
	private int offCount; // 盘点量， 和 货位 上所有量的差值 
	private int absCount; //盘点量， 和 货位 上所有量的差值的绝对值
	
	
	public int abnormalCargoCheckId;//计划单ID
	public int status;//是否需要盘点
	
	public int staffId;//物流员工id
	
	/**
	 * 方便easyui所添加的属性
	 */
	public int stockCount;
	public int stockLockCount;
	public int bsCount;
	public int byCount;
	public String receipts_number;
	public String statusName;
	/**
	 * 待一盘
	 */
	public static final int STATUS_WAIT_FIRST_CHECK = 0;
	/**
	 * 待二盘
	 */
	public static final int STATUS_WAIT_SECOND_CHECK = 1;
	/**
	 * 待终盘
	 */
	public static final int STATUS_WAIT_THRID_CHECK = 2;
	/**
	 * 盘点已完成
	 */
	public static final int STATUS_CHECK_FINISHED = 3;
	/**
	 * 无效盘点
	 */
	public static final int STATUS_UNAFFECTIVE = 4;
	/**
	 * 已报损
	 */
	public static final int STATUS_BS = 5;
	/**
	 * 已报溢
	 */
	public static final int STATUS_BY = 6;
	/**
	 * 正常
	 */
	public static final int STATUS_NORMAL = 7;
	
	public static HashMap statusMap = new LinkedHashMap();
	static {
		statusMap.put(Integer.valueOf(STATUS_WAIT_FIRST_CHECK), "待一盘");
		statusMap.put(Integer.valueOf(STATUS_WAIT_SECOND_CHECK), "待二盘");
		statusMap.put(Integer.valueOf(STATUS_WAIT_THRID_CHECK), "待终盘");
		statusMap.put(Integer.valueOf(STATUS_CHECK_FINISHED), "盘点已完成");
		statusMap.put(Integer.valueOf(STATUS_UNAFFECTIVE), "无效盘点");
		statusMap.put(Integer.valueOf(STATUS_BS), "已报损");
		statusMap.put(Integer.valueOf(STATUS_BY), "已报溢");
		statusMap.put(Integer.valueOf(STATUS_NORMAL), "正常");
		
	}
	
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getReceipts_number() {
		return receipts_number;
	}
	public void setReceipts_number(String receipts_number) {
		this.receipts_number = receipts_number;
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
	public int getStockCount() {
		return stockCount;
	}
	public void setStockCount(int stockCount) {
		this.stockCount = stockCount;
	}
	public int getStockLockCount() {
		return stockLockCount;
	}
	public void setStockLockCount(int stockLockCount) {
		this.stockLockCount = stockLockCount;
	}
	public int getStaffId() {
		return staffId;
	}
	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
	public int getAbnormalCargoCheckId() {
		return abnormalCargoCheckId;
	}
	public void setAbnormalCargoCheckId(int abnormalCargoCheckId) {
		this.abnormalCargoCheckId = abnormalCargoCheckId;
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
	public String getCargoWholeCode() {
		return cargoWholeCode;
	}
	public void setCargoWholeCode(String cargoWholeCode) {
		this.cargoWholeCode = cargoWholeCode;
	}
	public int getLockCount() {
		return lockCount;
	}
	public void setLockCount(int lockCount) {
		this.lockCount = lockCount;
	}
	public int getFirstCheckCount() {
		return firstCheckCount;
	}
	public void setFirstCheckCount(int firstCheckCount) {
		this.firstCheckCount = firstCheckCount;
	}
	public int getFirstCheckUserId() {
		return firstCheckUserId;
	}
	public void setFirstCheckUserId(int firstCheckUserId) {
		this.firstCheckUserId = firstCheckUserId;
	}
	public String getFirstCheckUserName() {
		return firstCheckUserName;
	}
	public void setFirstCheckUserName(String firstCheckUserName) {
		this.firstCheckUserName = firstCheckUserName;
	}
	public int getSecondCheckCount() {
		return secondCheckCount;
	}
	public void setSecondCheckCount(int secondCheckCount) {
		this.secondCheckCount = secondCheckCount;
	}
	public int getSecondCheckUserId() {
		return secondCheckUserId;
	}
	public void setSecondCheckUserId(int secondCheckUserId) {
		this.secondCheckUserId = secondCheckUserId;
	}
	public String getSecondCheckUserName() {
		return secondCheckUserName;
	}
	public void setSecondCheckUserName(String secondCheckUserName) {
		this.secondCheckUserName = secondCheckUserName;
	}
	public int getThirdCheckCount() {
		return thirdCheckCount;
	}
	public void setThirdCheckCount(int thirdCheckCount) {
		this.thirdCheckCount = thirdCheckCount;
	}
	public int getThirdCheckUserId() {
		return thirdCheckUserId;
	}
	public void setThirdCheckUserId(int thirdCheckUserId) {
		this.thirdCheckUserId = thirdCheckUserId;
	}
	public String getThirdCheckUserName() {
		return thirdCheckUserName;
	}
	public void setThirdCheckUserName(String thirdCheckUserName) {
		this.thirdCheckUserName = thirdCheckUserName;
	}
	public int getEventuallyCheckCount() {
		return eventuallyCheckCount;
	}
	public void setEventuallyCheckCount(int eventuallyCheckCount) {
		this.eventuallyCheckCount = eventuallyCheckCount;
	}
	public CargoInfoBean getCargoInfoBean() {
		return cargoInfoBean;
	}
	public void setCargoInfoBean(CargoInfoBean cargoInfoBean) {
		this.cargoInfoBean = cargoInfoBean;
	}
	public CargoProductStockBean getCargoProductStockBean() {
		return cargoProductStockBean;
	}
	public void setCargoProductStockBean(CargoProductStockBean cargoProductStockBean) {
		this.cargoProductStockBean = cargoProductStockBean;
	}
	public int getOffCount() {
		return offCount;
	}
	public void setOffCount(int offCount) {
		this.offCount = offCount;
	}
	public int getAbsCount() {
		return absCount;
	}
	public void setAbsCount(int absCount) {
		this.absCount = absCount;
	}
	public int getFinalCheckCount() {
		return finalCheckCount;
	}
	public void setFinalCheckCount(int finalCheckCount) {
		this.finalCheckCount = finalCheckCount;
	}
	
	public static String getStatusName(int status){
		String result = null;
		result = (String)statusMap.get(Integer.valueOf(status));
		if(result == null){
			result = "";
		}
		return result;
	}
	public int getBsbyId() {
		return bsbyId;
	}
	public void setBsbyId(int bsbyId) {
		this.bsbyId = bsbyId;
	}
	public BsbyOperationnoteBean getBsbyBean() {
		return bsbyBean;
	}
	public void setBsbyBean(BsbyOperationnoteBean bsbyBean) {
		this.bsbyBean = bsbyBean;
	}
}
