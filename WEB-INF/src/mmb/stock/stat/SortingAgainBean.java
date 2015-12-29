package mmb.stock.stat;
/**
 * 作者：石远飞
 * 
 * 日期：2013-6-7
 *	
 * 说明：再次分拣功能bean
 */
public class SortingAgainBean {
	
	public int sortingBatchOrderProductId;//分拣波次订单商品表ID
	
	public String proudctCode;//商品编号
	
	public String proudctName;//商品名称
	
	public String wholeCode;//货位编号
	
	public int wholeId;//货位ID
	
	public int count;//未处理数
	
	public int total;//订购数
	
	public String recommendWhole;//推荐货位
	
	
	public int getSortingBatchOrderProductId() {
		return sortingBatchOrderProductId;
	}
	public void setSortingBatchOrderProductId(int sortingBatchOrderProductId) {
		this.sortingBatchOrderProductId = sortingBatchOrderProductId;
	}
	public String getProudctCode() {
		return proudctCode;
	}
	public void setProudctCode(String proudctCode) {
		this.proudctCode = proudctCode;
	}
	public String getWholeCode() {
		return wholeCode;
	}
	public void setWholeCode(String wholeCode) {
		this.wholeCode = wholeCode;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public String getRecommendWhole() {
		return recommendWhole;
	}
	public void setRecommendWhole(String recommendWhole) {
		this.recommendWhole = recommendWhole;
	}
	public String getProudctName() {
		return proudctName;
	}
	public void setProudctName(String proudctName) {
		this.proudctName = proudctName;
	}
	public int getWholeId() {
		return wholeId;
	}
	public void setWholeId(int wholeId) {
		this.wholeId = wholeId;
	}
	
}
