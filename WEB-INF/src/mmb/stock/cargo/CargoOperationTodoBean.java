package mmb.stock.cargo;

public class CargoOperationTodoBean {
	public int id;					//ID
	public int cargoProductStockId;	//源货位商品库存id
	public int productId;			//产品id或装箱单id(待上架)
	public String productCode;		//产品编号或装箱单编号(待上架)
	public int cargoId;				//源货位id
	public String cargoCode;		//源货位号
	public int count;				//数量
	public int status;				//状态
	public int staffId;				//员工id
	public String staffName;		//员工姓名
	public int type;				//作业单类型
	
	/**
	 * 未分配
	 */
	public static int STATUS0=0;
	
	/**
	 * 已分配
	 */
	public static int STATUS1=1;
	
	/**
	 * 已生成作业单
	 */
	public static int STATUS2=2;
	
	/**
	 * 作业单已完成
	 */
	public static int STATUS3=3;
	
	/**
	 * 已作废
	 */
	public static int STATUS4=4;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCargoProductStockId() {
		return cargoProductStockId;
	}
	public void setCargoProductStockId(int cargoProductStockId) {
		this.cargoProductStockId = cargoProductStockId;
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
	public int getCargoId() {
		return cargoId;
	}
	public void setCargoId(int cargoId) {
		this.cargoId = cargoId;
	}
	public String getCargoCode() {
		return cargoCode;
	}
	public void setCargoCode(String cargoCode) {
		this.cargoCode = cargoCode;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getStaffId() {
		return staffId;
	}
	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}
	public String getStaffName() {
		return staffName;
	}
	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public static String getStatusName(int status){
		switch(status){
		case 0:return "未分配";
		case 1:return "已分配";
		case 2:return "已生成作业单";
		case 3:return "作业单已完成";
		case 4:return "已作废";
		default: return "";
		}
	}
}
