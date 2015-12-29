package mmb.stock.stat;

public class SortingBatchGroupStatBean {
	
	/**
	 *  波次id
	 */
	private int sortingBatchGroupId;
	/**
	 * 操作人id
	 */
	private int staffId;
	/**
	 * 操作订单id
	 */
	private int sortingBatchOrderId;
	/**
	 * 商品数量  (已修改为 分播了的数量 而不是需要分播的数量)
	 */
	private int productCount;
	
	/**
	 * 分播完成的时间
	 */
	private String sortingBatchCompleteTime;
	
	/**
	 *  操作人用户名
	 */
	private String staffUserName;
	
	
	public int getSortingBatchGroupId() {
		return sortingBatchGroupId;
	}

	public void setSortingBatchGroupId(int sortingBatchGroupId) {
		this.sortingBatchGroupId = sortingBatchGroupId;
	}

	public int getStaffId() {
		return staffId;
	}

	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}

	public int getSortingBatchOrderId() {
		return sortingBatchOrderId;
	}

	public void setSortingBatchOrderId(int sortingBatchOrderId) {
		this.sortingBatchOrderId = sortingBatchOrderId;
	}

	public int getProductCount() {
		return productCount;
	}

	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

	public String getStaffUserName() {
		return staffUserName;
	}

	public void setStaffUserName(String staffUserName) {
		this.staffUserName = staffUserName;
	}

	public String getSortingBatchCompleteTime() {
		return sortingBatchCompleteTime;
	}

	public void setSortingBatchCompleteTime(String sortingBatchCompleteTime) {
		this.sortingBatchCompleteTime = sortingBatchCompleteTime;
	}

}
