package adultadmin.bean.cargo;

import java.util.HashMap;
import java.util.Map;

public class CargoDownShelfBean {
 
	private int id;
	private int cargoId;
	private String cargoCode; //仓库编号
	private int productId; //产品表 产品id
	private String productCode;
	private String productName;// 产品表 产品名称
	private int stockCount;//当前库存数
	private int stockCountLock;//当前库存数 冻结数量
	private int MaxStockCount;//最大容量
	private int warStockCount;//警戒线
	private int cargoType; //货位类型
	private int volume;//体积
	private String cargoMark;//货位表 -- 备注
	private String cargoTypeName;
	
	public static Map cargoInfoType = new HashMap();
	static{
		cargoInfoType.put(Integer.valueOf(0), "普通");
		cargoInfoType.put(Integer.valueOf(1), "热销");
		cargoInfoType.put(Integer.valueOf(2), "滞销");
	}
	
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getStockCount() {
		return stockCount;
	}
	public void setStockCount(int stockCount) {
		this.stockCount = stockCount;
	}
	public int getStockCountLock() {
		return stockCountLock;
	}
	public void setStockCountLock(int stockCountLock) {
		this.stockCountLock = stockCountLock;
	}
	public int getMaxStockCount() {
		return MaxStockCount;
	}
	public void setMaxStockCount(int maxStockCount) {
		MaxStockCount = maxStockCount;
	}
	public int getWarStockCount() {
		return warStockCount;
	}
	public void setWarStockCount(int warStockCount) {
		this.warStockCount = warStockCount;
	}
	public int getCargoType() {
		return cargoType;
	}
	public void setCargoType(int cargoType) {
		this.cargoType = cargoType;
	}
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	public String getCargoMark() {
		return cargoMark;
	}
	public void setCargoMark(String cargoMark) {
		this.cargoMark = cargoMark;
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getCargoTypeName(int key) {
		String result = (String)cargoInfoType.get(Integer.valueOf(key));
		if(result==null) result="";
		return result;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	 
	
}
