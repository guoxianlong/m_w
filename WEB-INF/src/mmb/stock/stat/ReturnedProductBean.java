package mmb.stock.stat;


public class ReturnedProductBean {

	public final static int UNAPPRAISAL = 0;
	public final static int APPRAISAL_QUALIFY = 1;
	public final static int APPRAISAL_UNQUALIFY = 2;
	public int id;
	public int productId;//商品id
	public String productCode;//商品编号
	public String productName;//商品名称
	public int count;//数量
	public int type;//种类，0未质检，1质检合格，2质检不合格
	private String inCargoWholeCode;//目的货位编码
	private int completeCount;//完成量
	private int cargoOperationCargoId;//货位操作记录id
	private String cargoOprationCode;//上架单编号
	private int availableStockCount;//库存可用量
	
	public int lockCount;//锁定量
			
	
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
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getLockCount() {
		return lockCount;
	}
	public void setLockCount(int lockCount) {
		this.lockCount = lockCount;
	}
	public String getInCargoWholeCode() {
		return inCargoWholeCode;
	}
	public void setInCargoWholeCode(String inCargoWholeCode) {
		this.inCargoWholeCode = inCargoWholeCode;
	}
	
	public String getTypeName() {
		return type == this.UNAPPRAISAL ? "未质检" : type == this.APPRAISAL_QUALIFY ? "质检合格" : "质检不合格";
	}
	public int getCompleteCount() {
		return completeCount;
	}
	public void setCompleteCount(int completeCount) {
		this.completeCount = completeCount;
	}
	public int getCargoOperationCargoId() {
		return cargoOperationCargoId;
	}
	public void setCargoOperationCargoId(int cargoOperationCargoId) {
		this.cargoOperationCargoId = cargoOperationCargoId;
	}
	public String getCargoOprationCode() {
		return cargoOprationCode;
	}
	public void setCargoOprationCode(String cargoOprationCode) {
		this.cargoOprationCode = cargoOprationCode;
	}
	public int getAvailableStockCount() {
		return availableStockCount;
	}
	public void setAvailableStockCount(int availableStockCount) {
		this.availableStockCount = availableStockCount;
	}
	
}
