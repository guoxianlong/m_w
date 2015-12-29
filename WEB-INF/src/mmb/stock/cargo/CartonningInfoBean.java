package mmb.stock.cargo;

public class CartonningInfoBean {
	public int id;
	public String code;
	public String createTime;
	public int cause;
	public int status;
	public String name;
	public CartonningProductInfoBean productBean;
	public int cargoId;
	public String cargoWholeCode;
	public int operId;//作业单ID
	public int buyStockInId; //入库单Id
	/**
	 * 装箱原因：入库作业
	 */
	public static int CAUSE0 = 0;
	/**
	 * 原因：仓内作业
	 */
	public static int CAUSE1 = 1;
	/**
	 * 原因：盘点抽检
	 */
	public static int CAUSE2 = 2;
	/**
	 * 原因：日常理货
	 */
	public static int CAUSE3 = 3;
	/**
	 * 原因：其他原因.
	 */
	public static int CAUSE4 = 4;

	
	/**
	 * 状态：未打印
	 */
	public static int STATUS0 = 0;
	/**
	 * 状态：已生效
	 */
	public static int STATUS1 = 1;
	/**
	 * 状态：已作废
	 */
	public static int STATUS2 = 2;
	
	public CartonningProductInfoBean getProductBean() {
		return productBean;
	}
	public void setProductBean(CartonningProductInfoBean productBean) {
		this.productBean = productBean;
	}
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
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public int getCause() {
		return cause;
	}
	public void setCause(int cause) {
		this.cause = cause;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getCauseName() {
		String causeName = "";
		if(this.cause == CAUSE0){
			causeName = "入库作业";
		} else if(this.cause == CAUSE1){
			causeName = "仓内作业";
		} else if(this.cause == CAUSE2){
			causeName = "盘点抽检";
		}else if(this.cause == CAUSE3){
			causeName = "日常理货";
		}else if(this.cause == CAUSE4){
			causeName = "其他原因";
		}
		return causeName;
	}
	public String getStatusName() {
		String statusName = "";
		if(this.status == STATUS0){
			statusName = "未打印";
		} else if(this.status == STATUS2){
			statusName = "已作废";
		} else if(this.status == STATUS1){
			statusName = "已生效";
		}
		return statusName;
	}
	public int getOperId() {
		return operId;
	}
	public void setOperId(int operId) {
		this.operId = operId;
	}
	public int getBuyStockInId() {
		return buyStockInId;
	}
	public void setBuyStockInId(int buyStockInId) {
		this.buyStockInId = buyStockInId;
	}
}
