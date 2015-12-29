package adultadmin.bean.cargo;

import java.util.List;

import adultadmin.action.vo.voProduct;

public class CargoOperationCargoBean {
	
	public static int COC_WITH_INCARGOINFO_TYPE=0;//有目的货位的类型
	public static int COC_UNWITH_INCARGOINFO_TYPE=1;//有目的货位的状态
	public static int COC_WITH_INCARGOINFO_STATUS=1;//没有目的货位的类型
	public static int COC_UNWITH_INCARGOINFO_STATUS=0;//没有目的货位的状态
	public int id;
	public int operId;
	public int productId;
	public int inCargoProductStockId;
	public String inCargoWholeCode;
	public int outCargoProductStockId;
	public String outCargoWholeCode;
	public int stockCount;
	public int type; //type=0是目的货位1是原货位
	public int useStatus;
	public int completeCount;//退货上架单，上架完成商品数量
	public CargoProductStockBean cargoProductStock;  //货位库存信息
	public voProduct product;          //产品信息
	public CargoInfoBean cargoInfo;    //货位信息
	public List cocList;
	public List cartonningList;//装箱单信息
	public CargoOperationBean cargoOperation;//作业单
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOperId() {
		return operId;
	}
	public void setOperId(int operId) {
		this.operId = operId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getInCargoProductStockId() {
		return inCargoProductStockId;
	}
	public void setInCargoProductStockId(int inCargoProductStockId) {
		this.inCargoProductStockId = inCargoProductStockId;
	}
	public int getOutCargoProductStockId() {
		return outCargoProductStockId;
	}
	public void setOutCargoProductStockId(int outCargoProductStockId) {
		this.outCargoProductStockId = outCargoProductStockId;
	}
	public int getStockCount() {
		return stockCount;
	}
	public void setStockCount(int stockCount) {
		this.stockCount = stockCount;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public CargoProductStockBean getCargoProductStock() {
		return cargoProductStock;
	}
	public void setCargoProductStock(CargoProductStockBean cargoProductStock) {
		this.cargoProductStock = cargoProductStock;
	}
	public int getUseStatus() {
		return useStatus;
	}
	public void setUseStatus(int useStatus) {
		this.useStatus = useStatus;
	}
	public String getInCargoWholeCode() {
		return inCargoWholeCode;
	}
	public void setInCargoWholeCode(String inCargoWholeCode) {
		this.inCargoWholeCode = inCargoWholeCode;
	}
	public String getOutCargoWholeCode() {
		return outCargoWholeCode;
	}
	public void setOutCargoWholeCode(String outCargoWholeCode) {
		this.outCargoWholeCode = outCargoWholeCode;
	}
	public List getCocList() {
		return cocList;
	}
	public void setCocList(List cocList) {
		this.cocList = cocList;
	}
	public voProduct getProduct() {
		return product;
	}
	public void setProduct(voProduct product) {
		this.product = product;
	}
	public CargoInfoBean getCargoInfo() {
		return cargoInfo;
	}
	public void setCargoInfo(CargoInfoBean cargoInfo) {
		this.cargoInfo = cargoInfo;
	}
	public List getCartonningList() {
		return cartonningList;
	}
	public void setCartonningList(List cartonningList) {
		this.cartonningList = cartonningList;
	}
	public int getCompleteCount() {
		return completeCount;
	}
	public void setCompleteCount(int completeCount) {
		this.completeCount = completeCount;
	}
	public CargoOperationBean getCargoOperation() {
		return cargoOperation;
	}
	public void setCargoOperation(CargoOperationBean cargoOperation) {
		this.cargoOperation = cargoOperation;
	}
	
	
}
