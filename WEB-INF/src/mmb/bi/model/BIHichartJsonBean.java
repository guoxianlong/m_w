package mmb.bi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * BI hichart 图表bean，用于向页面传递Json数据
 * 
 * @author mengqy
 * 
 */
public class BIHichartJsonBean {
	
	/**
	 * categories 属性
	 */
	private List<String> catList;
	
	/**
	 * 重用数据结构，在不同情况下字段值的含义不同
	 */
	private List<Float> data1;

	/**
	 * 重用数据结构，在不同情况下字段值的含义不同
	 */
	private List<Float> data2;

	/**
	 * 重用数据结构，在不同情况下字段值的含义不同
	 */
	private List<Float> data3;

	/**
	 * 0:按天查询
	 * 1:按月查询
	 * 2:按年查询
	 */
	private int selectType;
	
	/**
	 * 图表标题
	 */
	private String title;
	
	

	public BIHichartJsonBean() {
		this.catList = new ArrayList<String>();
		this.data1 = new ArrayList<Float>();
		this.data2 = new ArrayList<Float>();
		this.data3 = new ArrayList<Float>();
	}

	public List<String> getCatList() {
		return catList;
	}

	public void setCatList(List<String> catList) {
		this.catList = catList;
	}

	public List<Float> getData1() {
		return data1;
	}

	public void setData1(List<Float> data1) {
		this.data1 = data1;
	}

	public List<Float> getData2() {
		return data2;
	}

	public void setData2(List<Float> data2) {
		this.data2 = data2;
	}

	public List<Float> getData3() {
		return data3;
	}

	public void setData3(List<Float> data3) {
		this.data3 = data3;
	}

	public int getSelectType() {
		return selectType;
	}

	public void setSelectType(int selectType) {
		this.selectType = selectType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
