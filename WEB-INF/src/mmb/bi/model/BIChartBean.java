package mmb.bi.model;

/**
 * 用于hicahrt的基本数据结构
 * @author mengqy
 *
 */
public class BIChartBean {
	
	/**
	 * 日期
	 */
	private String datetime;
	
	/**
	 * 库地区id
	 */
	private String areaId;
	
	/**
	 * 重用数据结构，在不同情况下字段值的含义不同
	 */
	private float data1;

	/**
	 * 重用数据结构，在不同情况下字段值的含义不同
	 */
	private float data2;

	/**
	 * 重用数据结构，在不同情况下字段值的含义不同
	 */
	private float data3;

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public float getData1() {
		return data1;
	}

	public void setData1(float data1) {
		this.data1 = data1;
	}

	public float getData2() {
		return data2;
	}

	public void setData2(float data2) {
		this.data2 = data2;
	}

	public float getData3() {
		return data3;
	}

	public void setData3(float data3) {
		this.data3 = data3;
	}

}
