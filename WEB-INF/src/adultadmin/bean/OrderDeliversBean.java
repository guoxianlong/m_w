package adultadmin.bean;

import java.util.ArrayList;
import java.util.List;

public class OrderDeliversBean {

	/**
	 * 
	 */
	private String dateTime; //时间
	private String str_code; // 发货单集合
	private double sumMoney =0; //总金额
	private double backMoney=0; //退回钱
	private int sumNum=0;  // 总数
	private int backNum =0; //退回数量
	private int deliver ; //快递公司
	private List subBeanList = new ArrayList(); //子集
	private boolean flag=true;
	private float sumWeight; //包裹总重量
	public OrderDeliversBean(){
		
	}
	
	public OrderDeliversBean(int deliver){
		this.deliver = deliver;
	}
	
	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getStr_code() {
		return str_code;
	}

	public void setStr_code(String str_code) {
		this.str_code = str_code;
	}

	public double getSumMoney() {
		return sumMoney;
	}

	public void setSumMoney(double sumMoney) {
		this.sumMoney = sumMoney;
	}

	public double getBackMoney() {
		return backMoney;
	}

	public void setBackMoney(double backMoney) {
		this.backMoney = backMoney;
	}

	public int getSumNum() {
		return sumNum;
	}

	public void setSumNum(int sumNum) {
		this.sumNum = sumNum;
	}

	public int getBackNum() {
		return backNum;
	}

	public void setBackNum(int backNum) {
		this.backNum = backNum;
	}

	public List getSubBeanList() {
		return subBeanList;
	}

	public void setSubBeanList(List subBeanList) {
		this.subBeanList = subBeanList;
	}

	public int getDeliver() {
		return deliver;
	}

	public void setDeliver(int deliver) {
		this.deliver = deliver;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public float getSumWeight() {
		return sumWeight;
	}

	public void setSumWeight(float sumWeight) {
		this.sumWeight = sumWeight;
	}
	
	
}
