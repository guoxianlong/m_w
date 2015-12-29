package mmb.stock.stat;

import java.util.LinkedHashMap;
import java.util.Map;

//异常货位盘点计划
public class AbnormalCargoCheckBean {
	public int id;
	public String code;//计划单编号
	public String createDatetime;//生成时间
	public int createUserId;//生成人id
	public String createUserName;//生成人姓名
	public int operCreateUserId;//报损报溢单生成人id
	public String operCreateUserName;//报损报溢单生成人姓名
	public int area;//库地区
	public int status;//状态
	public String areaName;
	
	/**
	 * 盘点状态：未开始
	 */
	public static int STATUS0 = 0;
	
	/**
	 * 盘点状态：一盘中
	 */
	public static int STATUS1 = 1;
	
	/**
	 * 盘点状态：二盘中
	 */
	public static int STATUS2 = 2;
	
	/**
	 * 盘点状态：终盘中
	 */
	public static int STATUS3 = 3;
	public static Map<Integer,String> statusMap = new LinkedHashMap<Integer,String>();
	
	
	public String getStatusName(){
		return statusMap.get(status);
	}
	/**
	 * 盘点状态：盘点已完成
	 */
	public static int STATUS4 = 4;
	
	/**
	 * 盘点状态：已完成
	 */
	public static int STATUS5 = 5;
	static{
		statusMap.put(0, "未开始");
		statusMap.put(1, "一盘中");
		statusMap.put(2, "二盘中");
		statusMap.put(3, "终盘中");
		statusMap.put(4, "盘点已完成");
		statusMap.put(5, "已完成");
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public int getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public int getOperCreateUserId() {
		return operCreateUserId;
	}
	public void setOperCreateUserId(int operCreateUserId) {
		this.operCreateUserId = operCreateUserId;
	}
	public String getOperCreateUserName() {
		return operCreateUserName;
	}
	public void setOperCreateUserName(String operCreateUserName) {
		this.operCreateUserName = operCreateUserName;
	}
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
