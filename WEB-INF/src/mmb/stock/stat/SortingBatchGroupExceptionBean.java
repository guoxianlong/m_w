package mmb.stock.stat;


public class SortingBatchGroupExceptionBean {
	
	public int id;
	//对应波次的id
	public int sortingBatchGroupId;
	//对应货位的 id
	public int cargoId;
	//商品id
	public int productId;
	//是否异常，0为否，1为是
	public int isException;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSortingBatchGroupId() {
		return sortingBatchGroupId;
	}
	public void setSortingBatchGroupId(int sortingBatchGroupId) {
		this.sortingBatchGroupId = sortingBatchGroupId;
	}
	public int getCargoId() {
		return cargoId;
	}
	public void setCargoId(int cargoId) {
		this.cargoId = cargoId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getIsException() {
		return isException;
	}
	public void setIsException(int isException) {
		this.isException = isException;
	}
	
	
}
