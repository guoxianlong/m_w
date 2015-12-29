package mmb.stock.stat;

/**
 * @name 员工排班
 * @author HYB
 *
 */
public class CheckStaffBean {

	public String date;	//排班日期
	public int dayCount; //日在编人数
	public int monthCount; //月在编人数
	public String nikeName;//别名
	public int areaId;//库地区Id
	
	public int id;
	
	
	public String getNikeName() {
		return nikeName;
	}
	public void setNikeName(String nikeName) {
		this.nikeName = nikeName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getDayCount() {
		return dayCount;
	}
	public void setDayCount(int dayCount) {
		this.dayCount = dayCount;
	}
	public int getMonthCount() {
		return monthCount;
	}
	public void setMonthCount(int monthCount) {
		this.monthCount = monthCount;
	}
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	
}
