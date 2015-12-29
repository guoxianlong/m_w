/*
 * Created on 2009-6-29
 *
 */
package adultadmin.bean.stock;

public class ProductStockTuneLogBean {

	public int id;

	public int productId;

	public int stockArea;

	public int stockType;

	public int productStockId;

	public int userId;
	public String username;

	public int srcStockCount;

	public int tarStockCount;

	public String createDatetime;

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

	public int getProductStockId() {
		return productStockId;
	}

	public void setProductStockId(int productStockId) {
		this.productStockId = productStockId;
	}

	public int getSrcStockCount() {
		return srcStockCount;
	}

	public void setSrcStockCount(int srcStockCount) {
		this.srcStockCount = srcStockCount;
	}

	public int getStockArea() {
		return stockArea;
	}

	public void setStockArea(int stockArea) {
		this.stockArea = stockArea;
	}

	public int getStockType() {
		return stockType;
	}

	public void setStockType(int stockType) {
		this.stockType = stockType;
	}

	public int getTarStockCount() {
		return tarStockCount;
	}

	public void setTarStockCount(int tarStockCount) {
		this.tarStockCount = tarStockCount;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
