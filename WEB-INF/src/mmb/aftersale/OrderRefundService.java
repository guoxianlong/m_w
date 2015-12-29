package mmb.aftersale;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.chinasms.sms.Sender;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voUser;
import adultadmin.bean.afterSales.AfterSaleCostListBean;
import adultadmin.bean.afterSales.AfterSaleOperatingLogBean;
import adultadmin.bean.afterSales.AfterSaleOrderBean;
import adultadmin.bean.afterSales.AfterSaleRefundOrderBean;
import adultadmin.service.IAdminService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IAfterSalesService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IUserOrderService;
import adultadmin.service.infc.IUserService;
import adultadmin.util.DateUtil;
import adultadmin.util.SmsSender;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class OrderRefundService extends BaseServiceImpl  {
	public OrderRefundService() {
		this.useConnType = CONN_IN_SERVICE;
	}

	public OrderRefundService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}
	 /*
     * 请查看父类或接口对应的注释。
     */
	public boolean addOrderRefundBean(OrderRefundBean orderRefund) {
        return addXXX(orderRefund, "order_refund");
	}
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteOrderRefund(String condition) {
		return deleteXXX(condition, "order_refund");
	}
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateOrderRefund(String set, String condition) {
		return updateXXX(set, condition, "order_refund");
	}
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getOrderRefundCount(String condition) {
		return getXXXCount(condition, "order_refund", "id");
	}
	public int getOrderRefundCountByCondition(String condition) {

        int count = 0;

        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return count;
        }
        ResultSet rs = null;

        //构建查询语句
        String query = " select count(order_refund.id) c_id" +
		 				" from order_refund  "+
		 				" join user_order uo "+
		 				" on  order_refund.order_id = uo.id ";
        if (condition != null) {
            query += " where " + condition;
        }
        
        //执行查询
        rs = dbOp.executeQuery(query);

        if (rs == null) {
            release(dbOp);
            return count;
        }
        try {
            //把结果集封装
            if (rs.next()) {
                count = rs.getInt("c_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //释放数据库连接
        release(dbOp);

        return count;
	}
	 /*
     * 请查看父类或接口对应的注释。
     */
    public OrderRefundBean getOrderRefund(String condition) {
        return (OrderRefundBean) getXXX(condition, "order_refund", "mmb.aftersale.OrderRefundBean");
    }

	 /*
     * 请查看父类或接口对应的注释。
     */
	public ArrayList getOrderRefundList(String condition, int index, int count, String orderBy) {
        return getXXXList(condition, index, count, orderBy, "order_refund",
        "mmb.aftersale.OrderRefundBean");
	}
	
	public ArrayList getOrderRefundListByCondition(String condition, int index, int count, String orderBy) {
		ArrayList orderRefundList = new ArrayList();
		DbOperation dbOp = getDbOp();
		String query = " select order_refund.*,uo.code uo_code,uo.status uo_status " +
					 " from order_refund  "+
					" join user_order uo "+
					" on  order_refund.order_id = uo.id ";
		if (condition != null) {
            query += " where " + condition;
        }
        if (orderBy != null) {
            query += " order by " + orderBy;
        }
        query = DbOperation.getPagingQuery(query, index, count);

        ResultSet rs = dbOp.executeQuery(query);
		try{
			while(rs.next()){
				OrderRefundBean orb = new OrderRefundBean();
				String code = rs.getString("code");  
				int id = rs.getInt("id");
				int orderId = rs.getInt("order_id");
				int payType = rs.getInt("pay_type");
				String payTypeDetail = rs.getString("pay_type_detail");
				String refundAccount = rs.getString("refund_account");
				String refundUsername = rs.getString("refund_username");
				String refundBank = rs.getString("refund_bank");
				int refundReasonType = rs.getInt("refund_reason_type");;
				String refundReasonContent = rs.getString("refund_reason_content");
				double refundPrice = rs.getDouble("refund_price");
				String operator = rs.getString("operator");
				String refundTime = rs.getString("refund_time");
				String createDatetime = rs.getString("create_datetime");
				String refundFailReason = rs.getString("refund_fail_reason");
				String refundCancelReason = rs.getString("refund_cancel_reason");
				int status = rs.getInt("status");;
				int refundType = rs.getInt("refund_type");;
				
				String orderCode =  rs.getString("uo_code");  
				int orderStatus =  rs.getInt("uo_status");
				
				orb.setId(id);
				orb.setCode(code);
				orb.setOrderId(orderId);
				orb.setPayType(payType);
				orb.setPayTypeDetail( ("null".equals(payTypeDetail)||(payTypeDetail==null))?"":payTypeDetail );
				orb.setRefundAccount(("null".equals(refundAccount)||(refundAccount==null))?"":refundAccount );
				orb.setRefundBank(("null".equals(refundBank)||(refundBank==null))?"":refundBank );
				orb.setRefundUsername(("null".equals(refundUsername)||(refundUsername==null))?"":refundUsername );
				orb.setRefundPrice(refundPrice);
				orb.setOperator(("null".equals(operator)||(operator==null))?"":operator );
				orb.setRefundTime(("null".equals(refundTime)||(refundTime==null))?"":refundTime );
				orb.setRefundFailReason(("null".equals(refundFailReason)||(refundFailReason==null))?"":refundFailReason );
				orb.setRefundCancelReason(("null".equals(refundCancelReason)||(refundCancelReason==null))?"":refundCancelReason );
				orb.setRefundReasonType(refundReasonType);
				orb.setRefundReasonContent(refundReasonContent);
				orb.setRefundType(refundType);
				
				orderRefundList.add(orb);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			this.release(dbOp);
		}
		return orderRefundList;
	}
	
	 /**
	 * 获取售后单商品金额
	 *@author zhangjie
	 *@date 2013-6-28 下午3:22:51
	 * @param AfterSaleOrderBean asob 
	 */
	 public static float getRealProductAfterSalePrice(AfterSaleOrderBean asob ){
		 //1.从after_sale_refund_order_product中根据after_sale_order_id查找到product_id 及 count
		 //2.取得after_sale_order中的order_id
		 //3.去user_order_product_split_history表中查找对应的order_id，及第一步中查找到的product_id去查单价 dprice/count，然后再用单价去乘以退货商品数量
		 float productSumPrice=0;
		 DbOperation db = new DbOperation();
			db.init(DbOperation.DB_SLAVE);
			IAdminService service = ServiceFactory.createAdminService(db);
			IUserOrderService userOrderSer =  ServiceFactory.createUserOrderService(IBaseService.CONN_IN_SERVICE, service.getDbOperation());
			try{
				//计算售后单中对应订单的产品单价
				int orderId = asob.getOrderId();
				StringBuilder sb = new StringBuilder("order_id=").append(orderId);
				ArrayList uopshList =userOrderSer.getUserOrderProductSplitHistoryList(sb.toString(), -1, -1, null);
				Map productUnitPriceMap = new HashMap();
				if(uopshList!=null && uopshList.size()>0){
					for(int i=0; i<uopshList.size();i++){
						UserOrderProductSplitHistoryBean uopsh = (UserOrderProductSplitHistoryBean)uopshList.get(i);
						int productId = uopsh.getProductId();
						float dprice = uopsh.getDprice();//获取该商品总价
						int count = uopsh.getCount();
						float productUnitPrice = dprice;//计算商品实际销售价
						productUnitPriceMap.put(productId,productUnitPrice );
					}
				}
				
				//获取售后单中的退货、换货商品,并计算总价(注意::::::::::换货还没试呢，不知道是不是一样????????????????????????????)
				//还有有赠品的情况??????????????????????????????????
				ResultSet rs = service.getDbOperation().executeQuery("select product_id,count  from after_sale_refund_order_product where type = 0 and after_sale_order_id = "+asob.getId());
				while(rs!=null&&rs.next()){
					int product_id = rs.getInt("product_id");
					int count = rs.getInt("count");
					if(productUnitPriceMap.get(product_id)!=null){
						float productUnitPrice = (Float)productUnitPriceMap.get(product_id);
						productSumPrice +=productUnitPrice*count;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				db.release();
			}
		 return productSumPrice;
	 }
	 /**
	 * 换货S订单发货后修改售后单状态，如果有退回钱包同时退回钱包
	 *@author zhangjie
	 *@date 2013-6-28 下午3:22:51
	 * @param   DbOperation dbop_DB 由外部传进来的数据库连接，内部不管关闭
	 */
	 public static boolean modifyAfterSaleOrderStatus(voOrder order,voUser admin,DbOperation dbop_outSide ){
		 	
		 	//如果是 售后换货订单，则在 订单发货完成以后，设置相应的售后单状态为“售后已完成”
//		 IAfterSalesService afService;
//		 	if(dbop_outSide!=null){
//		 		afService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, dbop_outSide);
//		 	}else{
//		 		afService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, null);
//		 	}
//		 	DbOperation dbOpSlave = new  DbOperation();
//		 	dbOpSlave.init(DbOperation.DB_SLAVE);
//			AfterSaleRefundOrderBean asro = afService.getAfterSaleRefundOrder("new_order_code='" + order.getCode() + "'");
//			try{
//				if(dbop_outSide==null){
//					afService.getDbOp().startTransaction();
//				}
//				if(asro != null){
//					//需要判断费用单  退回类型。 如果是：有余额需要退回给用户 则变更成售后单状态变成 换货余额财务确认 ls2012-6-20
//					AfterSaleCostListBean ascl = afService.getAfterSaleCostList("after_sale_order_id="+asro.getAfterSaleOrderId());
//					AfterSaleOrderBean asob = afService.getAfterSaleOrder("id=" + +asro.getAfterSaleOrderId());
//					AfterSaleOperatingLogBean afLog = new AfterSaleOperatingLogBean();
//					if(ascl.getReturnProductPrice()<=0){//如果换货余额是0元，则直接到售后换货已完成状态,并记录日志
//						if(!afService.updateAfterSaleOrder("status=" + AfterSaleOrderBean.STATUS_换货已完成, "id=" + asro.getAfterSaleOrderId())){
//							if(dbop_outSide==null){
//								afService.getDbOp().rollbackTransaction();
//							}
//							return false;
//						}else{
//							afLog.setContent("将售后单："+ asro.getAfterSaleOrderCode()+ " 从“"+ AfterSaleOrderBean.STATUS_MAP.get(Integer.valueOf(AfterSaleOrderBean.STATUS_换货发货中)) + "”状态改为“"+ AfterSaleOrderBean.STATUS_MAP.get(Integer.valueOf(AfterSaleOrderBean.STATUS_换货已完成))+ "”状态");
//							afLog.setPrevStatus(AfterSaleOrderBean.STATUS_换货发货中);
//							afLog.setStatus(AfterSaleOrderBean.STATUS_换货已完成);
//						}
//					}else{
//						if(ascl.getBackType()==1 || ascl.getBackType()==2){
//							afLog.setContent("将售后单："+ asro.getAfterSaleOrderCode()+ " 从“"+ AfterSaleOrderBean.STATUS_MAP.get(Integer.valueOf(AfterSaleOrderBean.STATUS_换货发货中)) + "”状态改为“"+AfterSaleOrderBean.STATUS_MAP.get(Integer.valueOf(AfterSaleOrderBean.STATUS_等待财务退款))+"”状态");
//							afLog.setPrevStatus(AfterSaleOrderBean.STATUS_换货发货中);
//							afLog.setStatus(AfterSaleOrderBean.STATUS_等待财务退款);
//						
//							//新增信息start
//							if(ascl.getBackType()==1){//如果是钱包支付
//								//退回用户钱包
//								int error = new WalletUtil().afterSaleRefund(ascl.getBackUserName(),asob.getOrderId(),ascl.getReturnProductPrice(),dbop_outSide);
//								if(error ==-1){
//									if(dbop_outSide==null){
//										afService.getDbOp().rollbackTransaction();
//									}
//									return false;
//								}else if(error==-11){
//									if(dbop_outSide==null){
//										afService.getDbOp().rollbackTransaction();
//									}
//									return false;
//								}else if(error!=100){
//									if(dbop_outSide==null){
//										afService.getDbOp().rollbackTransaction();
//									}
//									return false;
//								}else{
//								//下发退款短信
//									
////									IUserService userService = ServiceFactory.createUserService(IBaseService.CONN_IN_SERVICE, dbOpSlave);
////									voUser vouser = userService.getUser("username ='"+ascl.getBackUserName()+"'");
////									String smsMessage = "尊敬的"+(vouser.getName()==null?vouser.getUsername():vouser.getName())+"，您的换货余额"+ascl.getReturnProductPrice()+"元已成功退入买卖宝钱包，用户名："+vouser.getUsername()+"，请登陆www.mmb.cn查收，如有疑问请致电4008843211";
////									
////									if(!SmsSender.sendSMS(vouser.getPhone(),smsMessage)){
////										if(dbop_outSide==null){
////											afService.getDbOp().rollbackTransaction();
////										}
////										return false;
////									}
//								}
//								//修改状态为换货已完成
//								if(!afService.updateAfterSaleOrder("status=" + AfterSaleOrderBean.STATUS_换货已完成, "id=" + asro.getAfterSaleOrderId())){
//									if(dbop_outSide==null){
//										afService.getDbOp().rollbackTransaction();
//									}
//									return false;
//								}
//								
//								//记录日志 换货发货中->换货已完成 余额等待财务退款
//								afLog.setContent("将售后单："+ asro.getAfterSaleOrderCode()+ " 从“"+ AfterSaleOrderBean.STATUS_MAP.get(Integer.valueOf(AfterSaleOrderBean.STATUS_换货发货中)) + "”状态改为“"+ AfterSaleOrderBean.STATUS_MAP.get(Integer.valueOf(AfterSaleOrderBean.STATUS_换货已完成))+ "”状态");
//								afLog.setPrevStatus(AfterSaleOrderBean.STATUS_换货发货中);
//								afLog.setStatus(AfterSaleOrderBean.STATUS_换货已完成);
//							}else if(ascl.getBackType()==2){
//								OrderRefundService refundService = new OrderRefundService(IBaseService.CONN_IN_SERVICE, dbOpSlave);
//								//组织orderRefundBean
//								OrderRefundBean orb = new OrderRefundBean();
//								orb.setAfterSaleOrderId(asob.getId());
//								orb.setOperator(admin==null?"":admin.getUsername());//售后单操作人员
//								//根据选择的payType,决定如何组织有用数据
//								//售后退款单编号规则:(HTK+日期+四位数)
//								SimpleDateFormat sdfc = new SimpleDateFormat("yyyyMMdd");
//								Calendar cal = Calendar.getInstance();
//								String code = "HTK"+sdfc.format(cal.getTime());
//								ArrayList refundList = refundService.getOrderRefundList(" code like '"+code+"%' ", 0, 1, " id desc");
//								if(refundList==null||refundList.size()==0){
//									code+="0001";
//								}else{
//									String _code = ((OrderRefundBean)(refundList.get(0))).getCode();
//									int number = Integer.parseInt(_code.substring(_code.length()-4));
//									number++;
//									code += String.format("%04d",new Object[]{new Integer(number)});
//								}
//								//根据选择的payType,决定如何组织有用数据
//								orb.setCode(code);
//								orb.setOrderId(asob.getOrderId());
//								//获取银行等账号信息
//								int text_res_id = StringUtil.toInt(asob.getCustomerBankName());	
//								ResultSet rs_temp = dbOpSlave.executeQuery(" select content from text_res where id = " + text_res_id );
//								String customerBankName = null;
//								while(rs_temp!=null&&rs_temp.next()){
//									customerBankName = rs_temp.getString("content");
//								}
//								String customerAccount = asob.getCustomerAccount();
//								String customerAccountOwnerName = asob.getCustomerAccountOwnerName();
//								
//								orb.setRefundBank(customerBankName);
//								orb.setRefundAccount(customerAccount);
//								orb.setRefundUsername(customerAccountOwnerName);
//								orb.setRefundSpecies(2);
//								orb.setStatus(2);
//								orb.setRefundReasonType(22);//售后换货
//								orb.setRefundReasonContent(OrderRefundBean.getRefundReasonContent(22));
//								orb.setProductPrice(OrderRefundService.getRealProductAfterSalePrice(asob));
//								orb.setRefundPrice(ascl.getReturnProductPrice());//设置换货余额
//								
//								orb.setCreateDatetime(DateUtil.getNow());
//								rs_temp = dbOpSlave.executeQuery(" select create_datetime from user_order where id = " + asob.getOrderId() );
//								String orderCreateDatetime = null;
//								while(rs_temp!=null&&rs_temp.next()){
//									orderCreateDatetime = rs_temp.getString("create_datetime");
//								}
//								orb.setOrderCreatetime(orderCreateDatetime);
//								orb.setRefundType(1);
//	//							orb.setFromType(2);
//								//新增售后退款单
//								//更新状态为换货余额财务确认 同时add orderRefundBean
//								//记录日志 换货发货中->余额等待财务退款
//								rs_temp.close();
//								boolean flag = true;
//								refundService = new OrderRefundService(IBaseService.CONN_IN_SERVICE,afService.getDbOp());
//								if(refundService.getOrderRefund(" after_sale_order_id = " + asob.getId())!=null){
//									flag = refundService.deleteOrderRefund(" after_sale_order_id = " + asob.getId());
//								}
//								if(flag){
//									flag = refundService.addOrderRefundBean(orb);
//								}
//								if(flag){
//									flag = afService.updateAfterSaleOrder("status=" + AfterSaleOrderBean.STATUS_换货余额财务确认, "id=" + asro.getAfterSaleOrderId());
//								}
//								if(!flag){
//									if(dbop_outSide==null){
//										afService.getDbOp().rollbackTransaction();
//									}
//									return false;
//								}
//							}
//						}else{
//							if(!afService.updateAfterSaleOrder("status=" + AfterSaleOrderBean.STATUS_换货已完成, "id=" + asro.getAfterSaleOrderId())){
//								if(dbop_outSide==null){
//									afService.getDbOp().rollbackTransaction();
//								}
//								return false;
//							}
//							afLog.setContent("将售后单："+ asro.getAfterSaleOrderCode()+ " 从“"+ AfterSaleOrderBean.STATUS_MAP.get(Integer.valueOf(AfterSaleOrderBean.STATUS_换货发货中)) + "”状态改为“"+ AfterSaleOrderBean.STATUS_MAP.get(Integer.valueOf(AfterSaleOrderBean.STATUS_换货已完成))+ "”状态");
//							afLog.setPrevStatus(AfterSaleOrderBean.STATUS_换货发货中);
//							afLog.setStatus(AfterSaleOrderBean.STATUS_换货已完成);
//						}
//					}
//					afLog.setOperatorName(admin.getUsername());
//					afLog.setOperateTime(DateUtil.getNow());
//					afLog.setRecordId(asro.getAfterSaleOrderId());
//					afLog.setRecordCode(asro.getAfterSaleOrderCode());
//					if(!afService.addAfterSaleOperatingLog(afLog)){
//						if(dbop_outSide==null){
//							afService.getDbOp().rollbackTransaction();
//						}
//						return false;
//					}
//				}
//				if(dbop_outSide==null)
//					afService.getDbOp().commitTransaction();
//			 }catch(Exception e){
//				 e.printStackTrace();
//				 if(dbop_outSide==null)
//					 afService.getDbOp().rollbackTransaction();
//				 return false;
//			 }finally{
//				 dbOpSlave.release();
//				 if(dbop_outSide==null){//如果不是外部传过来的，需要内部关闭
//					 afService.releaseAll();
//				 }
//			 }
			 return true;
	 	}
}
