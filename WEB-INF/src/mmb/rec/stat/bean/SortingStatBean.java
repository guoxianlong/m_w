package mmb.rec.stat.bean;

public class SortingStatBean {
	
	public int id; //ID
	public int area; //地区
	public String date; //日期
	public int sortingCount; //分拣行数
	
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
	public int getSortingCount() {
		return sortingCount;
	}
	public void setSortingCount(int sortingCount) {
		this.sortingCount = sortingCount;
	}
}
