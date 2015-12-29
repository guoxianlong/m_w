package mmb.aftersale;


import adultadmin.bean.afterSales.AfterSaleOrderBean;

/**
 * <code>RefundOrderBean.java</code>
 * <p>
 * 功能:订单退款管理
 * 
 * <p>
 * Copyright 商机无限 2013 All right reserved.
 * 
 * @author zhangjie@ebinf.com 时间 May 06, 2013 11:22:55 AM
 * @version 1.0 </br>最后修改人 无
 */
public class OrderRefundBean {
	  public int id;
	  public String code;  
	  public int orderId;
	  public int afterSaleOrderId;//售后单id 如果来自订单，则为0
	  public  int payType;
	  public  String payTypeDetail;
	  public String refundAccount;
	  public String refundUsername;
	  public String refundBank;
	  public int refundReasonType;
	  public String refundReasonContent;
	  public double refundPrice;
	  public String operator;
	  public String refundTime;
	  public String createDatetime;
	  public String orderCreatetime;
	  public String refundFailReason;
	  public String refundCancelReason;
	  public int status;//订单退款状态 1,等待主管审核 2,等待财务退款 3,退款成功 4,退款失败 5,退款取消 6,等待财务审核
	  public int refundType;
//	  public int fromType;//订单来源  1：来自订单  2:来自售后单 该
	  public int refundSpecies;//退款种类  1：售前退款  2：售后退款
	  public double productPrice;//商品金额
	  
	  
	  public int getAfterSaleOrderId() {
		return afterSaleOrderId;
	}
	public void setAfterSaleOrderId(int afterSaleOrderId) {
		this.afterSaleOrderId = afterSaleOrderId;
	}
	//额外字段
	  public boolean banRefundButton;//是否禁用修改订单页中申请订单退款按钮
	  public String orderRefundStatusMsg;//订单被禁用条件下订单所在状态
	  
	  public AfterSaleOrderBean afterSaleOrder;
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
		public int getOrderId() {
			return orderId;
		}
		public void setOrderId(int orderId) {
			this.orderId = orderId;
		}
		public int getPayType() {
			return payType;
		}
		public void setPayType(int payType) {
			this.payType = payType;
		}
		public String getRefundAccount() {
			return refundAccount;
		}
		public void setRefundAccount(String refundAccount) {
			this.refundAccount = refundAccount;
		}
		public String getRefundUsername() {
			return refundUsername;
		}
		public void setRefundUsername(String refundUsername) {
			this.refundUsername = refundUsername;
		}
		public String getRefundBank() {
			return refundBank;
		}
		public void setRefundBank(String refundBank) {
			this.refundBank = refundBank;
		}
		public int getRefundReasonType() {
			return refundReasonType;
		}
		public void setRefundReasonType(int refundReasonType) {
			this.refundReasonType = refundReasonType;
		}
		public String getRefundReasonContent() {
			return refundReasonContent;
		}
		public void setRefundReasonContent(String refundReasonContent) {
			this.refundReasonContent = refundReasonContent;
		}
		public double getRefundPrice() {
			return refundPrice;
		}
		public void setRefundPrice(double refundPrice) {
			this.refundPrice = refundPrice;
		}
		public String getOperator() {
			return operator;
		}
		public void setOperator(String operator) {
			this.operator = operator;
		}
		public String getRefundTime() {
			return refundTime;
		}
		public void setRefundTime(String refundTime) {
			this.refundTime = refundTime;
		}
		public String getCreateDatetime() {
			return createDatetime;
		}
		public void setCreateDatetime(String createDatetime) {
			this.createDatetime = createDatetime;
		}
		public String getRefundFailReason() {
			return refundFailReason;
		}
		public void setRefundFailReason(String refundFailReason) {
			this.refundFailReason = refundFailReason;
		}
		public String getRefundCancelReason() {
			return refundCancelReason;
		}
		public void setRefundCancelReason(String refundCancelReason) {
			this.refundCancelReason = refundCancelReason;
		}
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public int getRefundType() {
			return refundType;
		}
		public void setRefundType(int refundType) {
			this.refundType = refundType;
		}
		
		
		public AfterSaleOrderBean getAfterSaleOrder() {
			return afterSaleOrder;
		}
		public void setAfterSaleOrder(AfterSaleOrderBean afterSaleOrder) {
			this.afterSaleOrder = afterSaleOrder;
		}
		public String getPayTypeDetail() {
			return payTypeDetail;
		}
		public void setPayTypeDetail(String payTypeDetail) {
			this.payTypeDetail = payTypeDetail;
		}
		public boolean isBanRefundButton() {
			return banRefundButton;
		}
		public void setBanRefundButton(boolean banRefundButton) {
			this.banRefundButton = banRefundButton;
		}
		
		public String getOrderCreatetime() {
			return orderCreatetime;
		}
		public void setOrderCreatetime(String orderCreatetime) {
			this.orderCreatetime = orderCreatetime;
		}
		
		public String getOrderRefundStatusMsg() {
			return orderRefundStatusMsg;
		}
		public void setOrderRefundStatusMsg(String orderRefundStatusMsg) {
			this.orderRefundStatusMsg = orderRefundStatusMsg;
		}
		
//		public int getFromType() {
//			return fromType;
//		}
//		public void setFromType(int fromType) {
//			this.fromType = fromType;
//		}
		public int getRefundSpecies() {
			return refundSpecies;
		}
		public void setRefundSpecies(int refundSpecies) {
			this.refundSpecies = refundSpecies;
		}
		
		public double getProductPrice() {
			return productPrice;
		}
		public void setProductPrice(double productPrice) {
			this.productPrice = productPrice;
		}
		public static String getRefundReasonContent(int refundReasonType){
			if(refundReasonType==1){
				return "缺货";
			}else if(refundReasonType==2){
				return "库存异常";
			}else if(refundReasonType==3){
				return "已退回物流中心";
			}else if(refundReasonType==4){
				return "没有理由";
			}else if(refundReasonType==5){
				return "重新订购";
			}else if(refundReasonType==6){
				return "物流公司异常";
			}else if(refundReasonType==7){
				return "多次付款";
			}else if(refundReasonType==8){
				return "长时间没有发货";
			}else if(refundReasonType==9){
				return "其他";
			}else if(refundReasonType==10){
				return "支付异常";
			}else if(refundReasonType==20){
				return "售后全部退货";
			}else if(refundReasonType==21){
				return "售后退货";
			}else if(refundReasonType==22){
				return "售后换货";
			}
			return "";
		}
}

