package mmb.bi.model;

/**
 * 人力基础数据
 * @author mengqy
 *
 */
public class BIBaseCountBean {

	/**
	 * 自增id
	 */
	private int id;
	
	/**
	 * 库地区id
	 */
	private int areaId;
	
	/**
	 * 日期
	 */
	private String datetime;
	
	/**
	 * 在职总人数
	 */
	private int inTotal;
	/**
	 * 在职物流中心人数
	 */
	private int inWare;
	/**
	 * 在职配送部人数
	 */
	private int inDelivery;
	/**
	 * 在职职能人数
	 */
	private int inAdmin;
	
	/**
	 * 在岗总人数
	 */
	private int onTotal;
	/**
	 * 在岗物流中心人数
	 */
	private int onWare;
	/**
	 * 在岗配送部人数
	 */
	private int onDelivery;
	/**
	 * 在岗职能人数
	 */
	private int onAdmin;
	/**
	 * 临时工人数
	 */
	private int tempCount;	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public int getInTotal() {
		return inTotal;
	}
	public void setInTotal(int inTotal) {
		this.inTotal = inTotal;
	}
	public int getInWare() {
		return inWare;
	}
	public void setInWare(int inWare) {
		this.inWare = inWare;
	}
	public int getInDelivery() {
		return inDelivery;
	}
	public void setInDelivery(int inDelivery) {
		this.inDelivery = inDelivery;
	}
	public int getInAdmin() {
		return inAdmin;
	}
	public void setInAdmin(int inAdmin) {
		this.inAdmin = inAdmin;
	}
	public int getOnTotal() {
		return onTotal;
	}
	public void setOnTotal(int onTotal) {
		this.onTotal = onTotal;
	}
	public int getOnWare() {
		return onWare;
	}
	public void setOnWare(int onWare) {
		this.onWare = onWare;
	}
	public int getOnDelivery() {
		return onDelivery;
	}
	public void setOnDelivery(int onDelivery) {
		this.onDelivery = onDelivery;
	}
	public int getOnAdmin() {
		return onAdmin;
	}
	public void setOnAdmin(int onAdmin) {
		this.onAdmin = onAdmin;
	}
	public int getTempCount() {
		return tempCount;
	}
	public void setTempCount(int tempCount) {
		this.tempCount = tempCount;
	}

}
