package mmb.stock.stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adultadmin.bean.order.OrderStockProductBean;

public class ReturnPackageCheckBean {
	
	public int id;
	
	public String packageCode;  //包裹单号
	
	public String orderCode;   //订单号
	
	public String checkUserName;   //核查人用户名
	
	public int checkUserId;	//核查人id
	
	public String checkTime;  //核查时间
	
	public int checkResult;  //核查结果
	
	public int type;	//异常类型
	
	public int status;	//异常处理状态
	
	public int area;    //包裹入库地区
	
	public List<ReturnPackageCheckProductBean> returnPackageCheckProductList = new ArrayList<ReturnPackageCheckProductBean>();
	
	public List<OrderStockProductBean> orderStockProductList = new ArrayList<OrderStockProductBean>();
	
	/**
	 * 未处理
	 */
	public final static int STATUS_UNDEAL = 0;
	/**
	 * 处理中
	 */
	public final static int STATUS_DEALING = 1;
	/**
	 * 已审核
	 */
	public final static int STATUS_AUDIT_SUCCESS = 2;
	/**
	 * 审核不通过
	 */
	public final static int STATUS_AUDIT_FAIL = 3;
	/**
	 * 已完成
	 */
	public final static int STATUS_COMPLETE = 4;
	
	
	/**
	 * 正常
	 */
	public final static int TYPE_NORMAL = 0;
	/**
	 * 退货商品缺失
	 */
	public final static int TYPE_LESS = 1;
	/**
	 * 退货商品多出
	 */
	public final static int TYPE_MORE = 2;
	/**
	 * 退货商品错件
	 */
	public final static int TYPE_WRONG = 3;
	
	/**
	 * 核查结果正常
	 */
	public final static int RESULT_NORMAL = 0;
	/**
	 * 核查结果异常
	 */
	public final static int RESULT_ABNORMAL = 1;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}

	public int getCheckResult() {
		return checkResult;
	}

	public void setCheckResult(int checkResult) {
		this.checkResult = checkResult;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}
	/**
	 * 
	 * @return 状态对应中文名
	 */
	public String getStatusName() {
		String statusName = "";
		switch (this.status) {
			case ReturnPackageCheckBean.STATUS_UNDEAL :
				statusName = "未处理";
				break;
			case ReturnPackageCheckBean.STATUS_DEALING :
				statusName = "处理中";
				break;
			case ReturnPackageCheckBean.STATUS_AUDIT_SUCCESS :
				statusName = "已审核";
				break;
			case ReturnPackageCheckBean.STATUS_AUDIT_FAIL :
				statusName = "审核未通过";
				break;
			case ReturnPackageCheckBean.STATUS_COMPLETE :
				statusName = "已完成";
				break;
			default : statusName = "";
		}
		return statusName;
	}
	
	public static Map getAllStatusName() {
		Map map = new HashMap();
		map.put("" + ReturnPackageCheckBean.STATUS_UNDEAL, "未处理");
		map.put("" + ReturnPackageCheckBean.STATUS_DEALING, "处理中");
		map.put("" + ReturnPackageCheckBean.STATUS_AUDIT_SUCCESS, "已审核");
		map.put("" + ReturnPackageCheckBean.STATUS_AUDIT_FAIL, "审核未通过");
		map.put("" + ReturnPackageCheckBean.STATUS_COMPLETE, "已完成");
		return map;
	}
	/**
	 * 
	 * @return 核查结果对应中文名称
	 */
	public String getCheckResultName() {
		String result = "";
		if(this.checkResult == ReturnPackageCheckBean.RESULT_NORMAL) {
			result = "正常";
		} else if (this.checkResult == ReturnPackageCheckBean.RESULT_ABNORMAL) {
			result = "异常";
		}
		return result;
	}
	/**
	 * 
	 * @return 类型的名称
	 */
	public String getTypeName() {
		String typeName = "";
		if( this.type == ReturnPackageCheckBean.TYPE_LESS ) {
			typeName = "退回商品缺失";
		} else if (this.type == ReturnPackageCheckBean.TYPE_MORE) {
			typeName = "退回商品多出";
		} else if (this.type == ReturnPackageCheckBean.TYPE_WRONG) {
			typeName = "退回商品错件";
		}
		return typeName;
	}
	
	public static Map getAllTypeName() {
		Map map = new HashMap();
		map.put("" + ReturnPackageCheckBean.TYPE_LESS, "缺失");
		map.put("" + ReturnPackageCheckBean.TYPE_MORE, "多出");
		map.put("" + ReturnPackageCheckBean.TYPE_WRONG, "错件");
		return map;
	}

	public String getCheckUserName() {
		return checkUserName;
	}

	public void setCheckUserName(String checkUserName) {
		this.checkUserName = checkUserName;
	}

	public int getCheckUserId() {
		return checkUserId;
	}

	public void setCheckUserId(int checkUserId) {
		this.checkUserId = checkUserId;
	}

	public List<ReturnPackageCheckProductBean> getReturnPackageCheckProductList() {
		return returnPackageCheckProductList;
	}

	public void setReturnPackageCheckProductList(
			List<ReturnPackageCheckProductBean> returnPackageCheckProductList) {
		this.returnPackageCheckProductList = returnPackageCheckProductList;
	}

	public List<OrderStockProductBean> getOrderStockProductList() {
		return orderStockProductList;
	}

	public void setOrderStockProductList(
			List<OrderStockProductBean> orderStockProductList) {
		this.orderStockProductList = orderStockProductList;
	}
	
	

}
