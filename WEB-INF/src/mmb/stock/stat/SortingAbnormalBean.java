package mmb.stock.stat;

import java.util.ArrayList;
import java.util.List;

//异常单
public class SortingAbnormalBean {
	public int id;
	public String code;//异常单编号
	public String operCode;//作业单号
	public int operType;//作业单类型
	public int abnormalType;//异常类型
	public int status;//异常处理状态
	
	public int wareArea; //库地区 
	public String createDatetime;//创建时间
	public String createUserName; //创建人用户名
	public int createUserId;  //创建人用户id
	
	public String statusName;
	
	public List<SortingAbnormalProductBean> sortingAbnormalProductList = new ArrayList<SortingAbnormalProductBean>();
	
	/**
	 * 异常处理状态：未处理
	 */
	public static final int STATUS0 = 0;
	
	/**
	 * 异常处理状态：处理中
	 */
	public static final int STATUS1 = 1;
	
	/**
	 * 异常处理状态：无异常
	 */
	public static final int STATUS2 = 2;
	
	/**
	 * 异常处理状态：待盘点
	 */
	public static final int STATUS3 = 3;
	
	/**
	 * 异常处理状态：盘点中
	 */
	public static final int STATUS4 = 4;
	
	/**
	 * 异常处理状态：已盘点
	 */
	public static final int STATUS5 = 5;
	
	/**
	 * 异常类型：撤单
	 */
	public static final int ABNORMALTYPE0 = 0;
	
	/**
	 * 异常类型：分拣货位异常
	 */
	public static final int ABNORMALTYPE1 = 1;
	
	/**
	 * 异常类型：分拣SKU错误
	 */
	public static final int ABNORMALTYPE2 = 2;
	
	/**
	 * 作业单类型：出库单
	 */
	public static final int OPERTYPE0 = 0;
	
	/**
	 * 作业单类型：分拣波次
	 */
	public static final int OPERTYPE1 = 1;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getOperCode() {
		return operCode;
	}
	public void setOperCode(String operCode) {
		this.operCode = operCode;
	}
	public int getOperType() {
		return operType;
	}
	public void setOperType(int operType) {
		this.operType = operType;
	}
	public int getAbnormalType() {
		return abnormalType;
	}
	public void setAbnormalType(int abnormalType) {
		this.abnormalType = abnormalType;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public List<SortingAbnormalProductBean> getSortingAbnormalProductList() {
		return sortingAbnormalProductList;
	}
	public void setSortingAbnormalProductList(
			List<SortingAbnormalProductBean> sortingAbnormalProductList) {
		this.sortingAbnormalProductList = sortingAbnormalProductList;
	}
	public int getWareArea() {
		return wareArea;
	}
	public void setWareArea(int wareArea) {
		this.wareArea = wareArea;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public int getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	/**
	 * 返回对应状态的中文名
	 * @return
	 */
	public String getStatusName() {
		String result = "";
		switch (this.status) {
			case SortingAbnormalBean.STATUS0 : 
				result = "未处理";
				break;
			case SortingAbnormalBean.STATUS1 : 
				result = "处理中";
				break;
			case SortingAbnormalBean.STATUS2 : 
				result = "无异常";
				break;
			case SortingAbnormalBean.STATUS3 : 
				result = "待盘点";
				break;
			case SortingAbnormalBean.STATUS4 : 
				result = "盘点中";
				break;
			case SortingAbnormalBean.STATUS5 : 
				result = "已盘点";
				break;
			default : result = "";
		}
		return result;
	}
	/**
	 * 获得分拣异常类型名称
	 * @return
	 */
	public String getAbnormalTypeName() {
		String result = "";
		switch (this.abnormalType) {
			case SortingAbnormalBean.ABNORMALTYPE0 : 
				result = "撤单";
				break;
			case SortingAbnormalBean.ABNORMALTYPE1 : 
				result = "分拣货位异常";
				break;
			case SortingAbnormalBean.ABNORMALTYPE2 : 
				result = "分拣SKU异常";
				break;
			default : result = "";
		}
		return result;
	}
	/**
	 * 获得作业单类型名称
	 * @return
	 */
	public String getOperTypeName() {
		String result = "";
		switch (this.operType) {
		case SortingAbnormalBean.OPERTYPE0 : 
			result = "出库单";
			break;
		case SortingAbnormalBean.OPERTYPE1 : 
			result = "分拣波次";
			break;
		default : result = "";
		}
		return result;
	}
	
}
