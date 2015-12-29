package mmb.rec.stat.bean;

/**
 * 	在查询统计订单发货时效时，临时存放数据的Bean
 * 
 * @author 郝亚斌
 *
 */
public class TempOrderEffectiveInfoBean {
	
	/**
	 *  地区
	 */
	public int area;
	/**
	 * 出库单id 用来查询 出库单下的商品 确定产品线
	 */
	public int orderStockId;
	/**
	 * 申请出库时间
	 */
	public String applyDatetime;
	/**
	 * 领单时间
	 */
	public String sortingBatchDatetime;
	/**
	 * 分播时间
	 */
	public String secondSortDatetime;
	/**
	 * 复合时间
	 */
	public String auditDatetime;
	/**
	 * 订单出库时间
	 */
	public String mailingDatetime;
	/**
	 * 时间段1
	 */
	public float duration1;
	/**
	 * 时间段2
	 */
	public float duration2;
	/**
	 * 时间段3
	 */
	public float duration3;
	/**
	 * 时间段4
	 */
	public float duration4;
	/**
	 * 总耗时时间段
	 */
	public float durationTotal;
	/**
	 * 订单代表商品的parentId1
	 */
	public int parentId1;
	/**
	 * 订单代表商品的parentId2
	 */
	public int parentId2;
	/**
	 * 商品在订单中的总价
	 */
	public float perProductTotalPrice;
	
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	public int getOrderStockId() {
		return orderStockId;
	}
	public void setOrderStockId(int orderStockId) {
		this.orderStockId = orderStockId;
	}
	public String getApplyDatetime() {
		return applyDatetime;
	}
	public void setApplyDatetime(String applyDatetime) {
		this.applyDatetime = applyDatetime;
	}
	public String getSortingBatchDatetime() {
		return sortingBatchDatetime;
	}
	public void setSortingBatchDatetime(String sortingBatchDatetime) {
		this.sortingBatchDatetime = sortingBatchDatetime;
	}
	public String getSecondSortDatetime() {
		return secondSortDatetime;
	}
	public void setSecondSortDatetime(String secondSortDatetime) {
		this.secondSortDatetime = secondSortDatetime;
	}
	public String getAuditDatetime() {
		return auditDatetime;
	}
	public void setAuditDatetime(String auditDatetime) {
		this.auditDatetime = auditDatetime;
	}
	public String getMailingDatetime() {
		return mailingDatetime;
	}
	public void setMailingDatetime(String mailingDatetime) {
		this.mailingDatetime = mailingDatetime;
	}
	public float getDuration1() {
		return duration1;
	}
	public void setDuration1(float duration1) {
		this.duration1 = duration1;
	}
	public float getDuration2() {
		return duration2;
	}
	public void setDuration2(float duration2) {
		this.duration2 = duration2;
	}
	public float getDuration3() {
		return duration3;
	}
	public void setDuration3(float duration3) {
		this.duration3 = duration3;
	}
	public float getDuration4() {
		return duration4;
	}
	public void setDuration4(float duration4) {
		this.duration4 = duration4;
	}
	public float getDurationTotal() {
		return durationTotal;
	}
	public void setDurationTotal(float durationTotal) {
		this.durationTotal = durationTotal;
	}
	public int getParentId1() {
		return parentId1;
	}
	public void setParentId1(int parentId1) {
		this.parentId1 = parentId1;
	}
	public int getParentId2() {
		return parentId2;
	}
	public void setParentId2(int parentId2) {
		this.parentId2 = parentId2;
	}
	public float getPerProductTotalPrice() {
		return perProductTotalPrice;
	}
	public void setPerProductTotalPrice(float perProductTotalPrice) {
		this.perProductTotalPrice = perProductTotalPrice;
	}

}
