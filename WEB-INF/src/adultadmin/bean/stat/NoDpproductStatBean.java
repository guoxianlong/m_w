/*
 * Created on 2008-11-5
 *
 */
package adultadmin.bean.stat;

import java.util.List;

import adultadmin.action.vo.voProduct;

public class NoDpproductStatBean {
	public int productId;			//商品Id
	public String code; 			//编号
	public String name; 			//小店名称
	public String oriName;			//原名称
	public String levelOneName;  		//一级分类
	public String levelTwoName;  		//二级分类
	public String lastTime;			//最近出货完成时间
	public String lastInTime;		//最近一次入库时间
	public int historyInCount;		//历史总采购量
	public int historyOutCount;	//历史总销量
	public int stockCount; 		//xx库存数量
	public float stockAmount;		//xx库存金额
	public int soonOutCount;		//近期出货量
	public int soonReturnCount;     //近期退回量  
	public int daysOfOut;			//最近一次出货时长
	public int dayOfInStock;		//库存时间

	private String stockTypeName ;
	private int stockType ;
	/**
	 * 该产品的库存列表，所有地区的，所有库的<br/>
	 * 需要单独查询出来，放在这个voProduct实例中
	 */
	private List psList = null;
	private voProduct product = null;

	public List getPsList() {
		return psList;
	}

	public void setPsList(List psList) {
		this.psList = psList;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOriName() {
		return oriName;
	}

	public void setOriName(String oriName) {
		this.oriName = oriName;
	}

	public String getLevelOneName() {
		return levelOneName;
	}

	public void setLevelOneName(String levelOneName) {
		this.levelOneName = levelOneName;
	}

	public String getLevelTwoName() {
		return levelTwoName;
	}

	public void setLevelTwoName(String levelTwoName) {
		this.levelTwoName = levelTwoName;
	}

	public int getHistoryInCount() {
		return historyInCount;
	}

	public int getHistoryOutCount() {
		return historyOutCount;
	}

	public int getStockCount() {
		return stockCount;
	}

	public float getStockAmount() {
		return stockAmount;
	}

	public int getDaysOfOut() {
		return daysOfOut;
	}

	public void setDaysOfOut(int daysOfOut) {
		this.daysOfOut = daysOfOut;
	}

	public int getDayOfInStock() {
		return dayOfInStock;
	}

	public void setDayOfInStock(int dayOfInStock) {
		this.dayOfInStock = dayOfInStock;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getLastInTime() {
		return lastInTime;
	}

	public void setLastInTime(String lastInTime) {
		this.lastInTime = lastInTime;
	}

	public String getStockTypeName() {
		return stockTypeName;
	}

	public void setStockTypeName(String stockTypeName) {
		this.stockTypeName = stockTypeName;
	}

	public int getStockType() {
		return stockType;
	}

	public void setStockType(int stockType) {
		this.stockType = stockType;
	}

	public int getSoonOutCount() {
		return soonOutCount;
	}

	public void setSoonOutCount(int soonOutCount) {
		this.soonOutCount = soonOutCount;
	}

	public voProduct getProduct() {
		return product;
	}

	public void setProduct(voProduct product) {
		this.product = product;
	}

	public void setHistoryInCount(int historyInCount) {
		this.historyInCount = historyInCount;
	}

	public void setHistoryOutCount(int historyOutCount) {
		this.historyOutCount = historyOutCount;
	}

	public void setStockCount(int stockCount) {
		this.stockCount = stockCount;
	}

	public void setStockAmount(float stockAmount) {
		this.stockAmount = stockAmount;
	}

	public int getSoonReturnCount() {
		return soonReturnCount;
	}

	public void setSoonReturnCount(int soonReturnCount) {
		this.soonReturnCount = soonReturnCount;
	}
	
}
