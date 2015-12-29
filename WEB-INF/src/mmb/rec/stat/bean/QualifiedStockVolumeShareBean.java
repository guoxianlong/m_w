package mmb.rec.stat.bean;

import java.io.Serializable;

/**
 * 
 * @author 朱爱林
 *	合格库容积率
 */
public class QualifiedStockVolumeShareBean implements Serializable{

	/**
	 * 
	 */
	public static final long serialVersionUID = 1L;
	public int id;
	public int area;//地区
	public String date;//时间
	public int productLineId;//产品线id
	public double areaStockVolume;//区域货位容积
	public double productLineProductVolume;//产品线容积
	
	public float share;//容积比例
	public String productLineName;//产品线名称
	
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
	public double getAreaStockVolume() {
		return areaStockVolume;
	}
	public void setAreaStockVolume(double areaStockVolume) {
		this.areaStockVolume = areaStockVolume;
	}
	public double getProductLineProductVolume() {
		return productLineProductVolume;
	}
	public void setProductLineProductVolume(double productLineProductVolume) {
		this.productLineProductVolume = productLineProductVolume;
	}
	public float getShare() {
		return share;
	}
	public void setShare(float share) {
		this.share = share;
	}
	public String getProductLineName() {
		return productLineName;
	}
	public void setProductLineName(String productLineName) {
		this.productLineName = productLineName;
	}
}
