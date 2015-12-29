/*
 * Created on 2009-2-27
 *
 */
package adultadmin.bean;

/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2009-2-27
 * 
 * 说明：某地区快递计算
 */
public class Postage {

	public int id;
	/**
	 * 区域ID
	 */
	public int area;
	/**
	 * 区域名称
	 */
	public String areaName;

	/**
	 * 重量（克）
	 */
	public int weight;
	/**
	 * 起重（克）
	 */
	public int qizhong;
	/**
	 * 起重费用（元）
	 */
	public float qizhongfeiyong;
	/**
	 * 续重（克）
	 */
	public int xuzhong;
	/**
	 * 续重费用（元）
	 */
	public float xuzhongfeiyong;
	/**
	 * 妥投费用（元）
	 */
	public float tuotoufeiyong;

	/**
	 * 代收货款费用
	 */
	public float dshkfeiyong;

	/**
	 * 订单的价格
	 */
	public float price;

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public float getDshkfeiyong() {
		return dshkfeiyong;
	}

	public void setDshkfeiyong(float dshkfeiyong) {
		this.dshkfeiyong = dshkfeiyong;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getQizhong() {
		return qizhong;
	}

	public void setQizhong(int qizhong) {
		this.qizhong = qizhong;
	}

	public float getQizhongfeiyong() {
		return qizhongfeiyong;
	}

	public void setQizhongfeiyong(float qizhongfeiyong) {
		this.qizhongfeiyong = qizhongfeiyong;
	}

	public float getTuotoufeiyong() {
		return tuotoufeiyong;
	}

	public void setTuotoufeiyong(float tuotoufeiyong) {
		this.tuotoufeiyong = tuotoufeiyong;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getXuzhong() {
		return xuzhong;
	}

	public void setXuzhong(int xuzhong) {
		this.xuzhong = xuzhong;
	}

	public float getXuzhongfeiyong() {
		return xuzhongfeiyong;
	}

	public void setXuzhongfeiyong(float xuzhongfeiyong) {
		this.xuzhongfeiyong = xuzhongfeiyong;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getPostage(){
		float postage = 0;
		if(this.weight <= this.qizhong){
			postage = this.qizhongfeiyong + this.dshkfeiyong * this.price + this.tuotoufeiyong;
		} else {
			int xzCount = (this.weight - this.qizhong)/this.xuzhong;
			int xz = (this.weight - this.qizhong)%this.xuzhong;

			if(xz == 0){
				postage = this.qizhongfeiyong + xzCount * this.xuzhongfeiyong + this.dshkfeiyong * this.price + this.tuotoufeiyong;
			} else {
				postage = this.qizhongfeiyong + (xzCount + 1) * this.xuzhongfeiyong + this.dshkfeiyong * this.price + this.tuotoufeiyong;
			}
		}
		return postage;
	}

	public Object clone() {
		Postage postage = new Postage();
		postage.setArea(this.area);
		postage.setAreaName(this.areaName);
		postage.setDshkfeiyong(this.dshkfeiyong);
		postage.setId(this.id);
		postage.setPrice(this.price);
		postage.setQizhong(this.qizhong);
		postage.setQizhongfeiyong(this.qizhongfeiyong);
		postage.setTuotoufeiyong(this.getTuotoufeiyong());
		postage.setWeight(this.getWeight());
		postage.setXuzhong(this.getXuzhong());
		postage.setXuzhongfeiyong(this.getXuzhongfeiyong());
		return postage;
	}

	
}
 