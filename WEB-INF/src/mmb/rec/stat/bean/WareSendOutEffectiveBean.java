package mmb.rec.stat.bean;

/**
 * 日期: 2013-09-24
 * 
 * @author 郝亚斌
 *
 */
public class WareSendOutEffectiveBean {
	/**
	 * id
	 */
	public int id;
	/**
	 * 地区
	 */
	public int area;
	/**
	 * 日期
	 */
	public String date;
	/**
	 * 产品线id
	 */
	public int productLineId;
	/**
	 * 12小时以内的出库数量
	 */
	public int count1;
	/**
	 * 12到24小时的出库数量
	 */
	public int count2;
	/**
	 * 大于24小时的出库数量
	 */
	public int count3;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getCount1() {
		return count1;
	}

	public void setCount1(int count1) {
		this.count1 = count1;
	}

	public int getCount2() {
		return count2;
	}

	public void setCount2(int count2) {
		this.count2 = count2;
	}

	public int getCount3() {
		return count3;
	}

	public void setCount3(int count3) {
		this.count3 = count3;
	}

	public int getProductLineId() {
		return productLineId;
	}

	public void setProductLineId(int productLineId) {
		this.productLineId = productLineId;
	}

}
