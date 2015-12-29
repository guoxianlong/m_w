package mmb.bi.model;

/**
 * 查询图表 页面提交数据
 * @author mengqy
 *
 */
public class BIHichartPostBean {
	private int layer;
	private int areaId;
	private int operType;
	
	private int beginYear;
	private int beginMonth;
	private int beginDay;	
	private int endYear;
	private int endMonth;
	private int endDay;
	
	
	public int getLayer() {
		return layer;
	}
	public void setLayer(int layer) {
		this.layer = layer;
	}
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public int getOperType() {
		return operType;
	}
	public void setOperType(int operType) {
		this.operType = operType;
	}
	
	
	public int getBeginYear() {
		return beginYear;
	}
	public void setBeginYear(int beginYear) {
		this.beginYear = beginYear;
	}
	public int getBeginMonth() {
		return beginMonth;
	}
	public void setBeginMonth(int beginMonth) {
		this.beginMonth = beginMonth;
	}
	public int getBeginDay() {
		return beginDay;
	}
	public void setBeginDay(int beginDay) {
		this.beginDay = beginDay;
	}
	public int getEndYear() {
		return endYear;
	}
	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}
	public int getEndMonth() {
		return endMonth;
	}
	public void setEndMonth(int endMonth) {
		this.endMonth = endMonth;
	}
	public int getEndDay() {
		return endDay;
	}
	public void setEndDay(int endDay) {
		this.endDay = endDay;
	}
	
	
}
