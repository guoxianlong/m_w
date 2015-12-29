package adultadmin.bean.cargo;

import java.util.HashMap;
import java.util.LinkedHashMap;


public class CargoStaffPerformanceBean {
	public int id; //id
	public int type;//作业类型
	public int operCount; //作业量
	public int productCount;//作业商品数量
	public String date; //统计时间
	public int staffId; //这里存的是userId CArgoStaff表里的user_id与之对应
	/*
	 * 分拣
	 */
	public static final int TYPE0 = 0;
	/*
	 * 复核
	 */
	public static final int TYPE1 = 1;
	/*
	 * 质检装箱
	 */
	public static final int TYPE2 = 2;
	/*
	 * 上架
	 */
	public static final int TYPE3 = 3;
	/*
	 * 退货上架
	 */
	public static final int TYPE4 = 4;
	/*
	 * 完成上架
	 */
	public static final int TYPE5 = 5;
	/*
	 * 退货上架汇总
	 */
	public static final int TYPE6 = 6;
	/*
	 * 退货上架汇总
	 */
	public static final int TYPE7 = 7;
	
	public static HashMap<Integer, String> typeMap = new LinkedHashMap<Integer, String>();
	static{
		typeMap.put(TYPE0, "单SKU分拣");
		typeMap.put(TYPE1, "复核");
		typeMap.put(TYPE2, "质检装箱");
		typeMap.put(TYPE3, "上架");
		typeMap.put(TYPE4, "退货上架");
		typeMap.put(TYPE5, "完成上架");
		typeMap.put(TYPE6, "退货上架汇总");
		typeMap.put(TYPE7, "多SKU分拣");
	}
	public String getTypeName(){
		String name = null;
		name = typeMap.get(this.type);
		if(name == null){
			return "";
		}
		return name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getOperCount() {
		return operCount;
	}
	public void setOperCount(int operCount) {
		this.operCount = operCount;
	}
	public int getProductCount() {
		return productCount;
	}
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getStaffId() {
		return staffId;
	}
	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}
	
	
}
