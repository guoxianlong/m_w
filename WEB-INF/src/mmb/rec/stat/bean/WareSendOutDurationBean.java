package mmb.rec.stat.bean;

/**
 * 2013-9-24
 * @author 郝亚斌
 *
 */
public class WareSendOutDurationBean {
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
	 * 申请出库到领单的平均时间
	 */
	public float duration1;
	/**
	 * 领单到分播的平均时间
	 */
	public float duration2;
	/**
	 * 分播到复核的平均时间
	 */
	public float duration3;
	/**
	 * 复核到出库的平均时间
	 */
	public float duration4;
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
	public int getProductLineId() {
		return productLineId;
	}
	public void setProductLineId(int productLineId) {
		this.productLineId = productLineId;
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

}
