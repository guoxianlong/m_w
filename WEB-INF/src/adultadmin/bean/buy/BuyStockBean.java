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
 * 说明：采购进货单Bean
 */
public class BuyStockBean {

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
	 * 状态：采购已完成
	 */
	public static int STATUS6 = 6;
	
	/**
	 * 状态：已删除
	 */
	public static int STATUS8 = 8;

	public int id;                   //ID

	public int buyOrderId;           //所属采购订单ID

	public String name;              //名称
	
	public String code;              //编号

	public int status;               //状态

	public int area;                 //所属地区

	public String createDatetime;    //添加时间

	public String remark;            //备注

	public String deadline;          //最后期限

	public String confirmDatetime;   //确认时间
	
	public String productType;       //产品线
	
	public String proxyName;         //代理商名称

	public BuyOrderBean buyOrder;    //对应采购计划订单

	public int printCount;           //打印次数
	
	public int transformCount;       //转换次数

	public int buyStockinCount;      //所含入库单数量
	
	public float portage;
	
	public String expectArrivalDatetime; //预计到货时间
	
	public String expressCompany;    //快递公司
	
	public String expressCode;       //快递单号
	
	public int supplierId;			//供应商id

	public int createUserId;         //添加用户ID
	public int auditingUserId;       //审核用户ID
	public int assignUserId;         //指派用户ID
	
	public voUser creatUser;        //添加计划用户
	public voUser auditingUser;     //审核用户
	public String signDateTime;
	

	public String getSignDateTime() {
		return signDateTime;
	}

	public void setSignDateTime(String signDateTime) {
		this.signDateTime = signDateTime;
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

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

	public int getBuyOrderId() {
		return buyOrderId;
	}

	public void setBuyOrderId(int buyOrderId) {
		this.buyOrderId = buyOrderId;
	}


	public BuyOrderBean getBuyOrder() {
		return buyOrder;
	}

	public void setBuyOrder(BuyOrderBean buyOrder) {
		this.buyOrder = buyOrder;
	}

	public int getPrintCount() {
		return printCount;
	}

	public void setPrintCount(int printCount) {
		this.printCount = printCount;
	}

	public int getBuyStockinCount() {
		return buyStockinCount;
	}

	public void setBuyStockinCount(int buyStockinCount) {
		this.buyStockinCount = buyStockinCount;
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

	public int getAuditingUserId() {
		return auditingUserId;
	}

	public void setAuditingUserId(int auditingUserId) {
		this.auditingUserId = auditingUserId;
	}

	public int getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public String getStatusName() {
		String statusName = "";
		if(this.status == STATUS0){
			statusName = "未处理";
		} else if(this.status == STATUS1){
			statusName = "处理中";
		} else if(this.status == STATUS2){
			statusName = "已确认";
		} else if(this.status == STATUS3){
			statusName = "已审核";
//			if(this.buyStockinCount <= 0){
//				long deadline = DateUtil.parseDate(this.deadline).getTime();
//				long now = DateUtil.getNowDate().getTime();
//				if(deadline < now){
//					statusName = "处理入库";
//				}
//			}
		} else if(this.status == STATUS4){
			statusName = "审核未通过";
		} else if(this.status == STATUS5){
			statusName = "已指派";
		} else if(this.status == STATUS6){
			statusName = "采购已完成";
		}
		return statusName;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getProxyName() {
		return proxyName;
	}

	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
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

	public int getTransformCount() {
		return transformCount;
	}

	public void setTransformCount(int transformCount) {
		this.transformCount = transformCount;
	}

	public float getPortage() {
		return portage;
	}

	public void setPortage(float portage) {
		this.portage = portage;
	}

	public String getExpectArrivalDatetime() {
		return expectArrivalDatetime;
	}

	public void setExpectArrivalDatetime(String expectArrivalDatetime) {
		this.expectArrivalDatetime = expectArrivalDatetime;
	}

	public String getExpressCompany() {
		return expressCompany;
	}

	public void setExpressCompany(String expressCompany) {
		this.expressCompany = expressCompany;
	}

	public String getExpressCode() {
		return expressCode;
	}

	public void setExpressCode(String expressCode) {
		this.expressCode = expressCode;
	}

	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

}
