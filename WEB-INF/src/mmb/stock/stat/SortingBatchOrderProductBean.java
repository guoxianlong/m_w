package mmb.stock.stat;

public class SortingBatchOrderProductBean {
	
	public int id;
	//对应波次的id
	public int sortingBatchGroupId;
	//对应波次订单的 id
	public int sortingBatchOrderId;
	//商品id
	public int productId;
	//订单中需要这个商品的数量
	public int count;
	//已经放入订单中的数量（分播）
	public int completeCount;
	//订单中商品种类数
	public int orderSkuCount;
	//商品所属订单对应的 格子号
	public String boxCode;
	//所属订单 是否是已删除
	public int isDelete;
	//商品货位
	public int cargoId;
	//已分拣量（分拣）
	public int sortingCount;
	//分拣人id
	public int sortingUserId;
	//分拣人名称
	public String sortingUsername;
	//分拣时间
	public String sortingDatetime;
	
	
	private String cargoWholeCode;
	private String productCode;
	
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
	public int getSortingBatchOrderId() {
		return sortingBatchOrderId;
	}
	public void setSortingBatchOrderId(int sortingBatchOrderId) {
		this.sortingBatchOrderId = sortingBatchOrderId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getCompleteCount() {
		return completeCount;
	}
	public void setCompleteCount(int completeCount) {
		this.completeCount = completeCount;
	}
	public int getOrderSkuCount() {
		return orderSkuCount;
	}
	public void setOrderSkuCount(int orderSkuCount) {
		this.orderSkuCount = orderSkuCount;
	}
	public String getBoxCode() {
		return boxCode;
	}
	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}
	
	public static String getCodeByLocation( int i, int j ) {
		int y = j+1;
		String code = i == 0 ? "A" : i == 1 ? "B" : i == 2 ? "C" : i == 3 ? "D" : i == 4 ? "E" : "";
		return code + "-" + y;
	}
	public int getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}
	public int getSortingCount() {
		return sortingCount;
	}
	public void setSortingCount(int sortingCount) {
		this.sortingCount = sortingCount;
	}
	public int getCargoId() {
		return cargoId;
	}
	public void setCargoId(int cargoId) {
		this.cargoId = cargoId;
	}
	public int getSortingUserId() {
		return sortingUserId;
	}
	public void setSortingUserId(int sortingUserId) {
		this.sortingUserId = sortingUserId;
	}
	public String getSortingUsername() {
		return sortingUsername;
	}
	public void setSortingUsername(String sortingUsername) {
		this.sortingUsername = sortingUsername;
	}
	public String getSortingDatetime() {
		return sortingDatetime;
	}
	public void setSortingDatetime(String sortingDatetime) {
		this.sortingDatetime = sortingDatetime;
	}
	public String getCargoWholeCode() {
		return cargoWholeCode;
	}
	public void setCargoWholeCode(String cargoWholeCode) {
		this.cargoWholeCode = cargoWholeCode;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
}
