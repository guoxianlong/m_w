/*
 * Created on 2007-6-28
 *
 */
package adultadmin.bean.buy;

import adultadmin.action.vo.voUser;
import adultadmin.util.DateUtil;
/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2009-2-19
 * 
 * 说明：采购计划Bean
 */
public class BuyPlanBean {

	/**
	 * 状态：未处理
	 */
	public static int STATUS0 = 0;

	/**
	 * 状态：处理中
	 */
	public static int STATUS1 = 1;

	/**
	 * 状态：已确认
	 */
	public static int STATUS2 = 2;

	/**
	 * 状态：已审核
	 */
	public static int STATUS3 = 3;


	/**
	 * 状态：审核未通过
	 */
	public static int STATUS4 = 4;

	/**
	 * 状态：已指派
	 */
	public static int STATUS5 = 5;
	
	/**
	 * 状态：已完成
	 */
	public static int STATUS6 = 6;
	
	/**
	 * 状态：已删除
	 */
	public static int STATUS8 = 8;
	

	public int id;          //ID

	public String name;     //名称
	
	public String code;     //编号

	public int status;      //状态

	public String createDatetime;   //添加时间

	public String remark;           //备注

	public String deadline;         //最后时间期限

	public String confirmDatetime;  //确认时间

	public int printCount;          //打印次数
	
	public int transformCount;      //转换次数
	
	public String productType;      //产品线

	public int buyOrderCount;       //该计划包含的采购订单数量

	public int createUserId;        //添加计划用户ID
	public int auditingUserId;      //审核用户ID
	public int assignUserId;        //指派用户ID
	
	public voUser creatUser;        //添加计划用户
	public voUser auditingUser;     //审核用户
	
	public String getConfirmDatetime() {
		return confirmDatetime;
	}

	public void setConfirmDatetime(String confirmDatetime) {
		this.confirmDatetime = confirmDatetime;
	}

	public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPrintCount() {
		return printCount;
	}

	public void setPrintCount(int printCount) {
		this.printCount = printCount;
	}

	public int getTransformCount() {
		return transformCount;
	}

	public void setTransformCount(int transformCount) {
		this.transformCount = transformCount;
	}

	public int getBuyOrderCount() {
		return buyOrderCount;
	}

	public void setBuyOrderCount(int buyOrderCount) {
		this.buyOrderCount = buyOrderCount;
	}

	public boolean isOvertime(){
		long deadline = DateUtil.parseDate(this.deadline).getTime();
		long now = DateUtil.getNowDate().getTime();
		return (deadline < now);
	}

	public int getAssignUserId() {
		return assignUserId;
	}

	public void setAssignUserId(int assignUserId) {
		this.assignUserId = assignUserId;
	}

	public int getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public int getAuditingUserId() {
		return auditingUserId;
	}

	public void setAuditingUserId(int auditingUserId) {
		this.auditingUserId = auditingUserId;
	}

	public String getStatusName() {
		String statusName = "";
		if(this.status == STATUS0){
			statusName = "未处理";
		} else if(this.status == STATUS1){
			statusName = "处理中";
		} else if(this.status == STATUS2){
//			statusName = "已计划未审核";
			statusName = "已确认";
		} else if(this.status == STATUS3){
			statusName = "已审核";
//			if(this.buyOrderCount <= 0){
//				long deadline = DateUtil.parseDate(this.deadline).getTime();
//				long now = DateUtil.getNowDate().getTime();
//				if(deadline < now){
//					statusName = "处理订购";
//				}
//			}
		} else if(this.status == STATUS4){
			statusName = "审核未通过";
		} else if(this.status == STATUS5){
			statusName = "已指派";
		} else if(this.status == STATUS6){
			statusName = "已完成";
		}
		return statusName;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public voUser getCreatUser() {
		return creatUser;
	}

	public void setCreatUser(voUser creatUser) {
		this.creatUser = creatUser;
	}

	public voUser getAuditingUser() {
		return auditingUser;
	}

	public void setAuditingUser(voUser auditingUser) {
		this.auditingUser = auditingUser;
	}
	
}
