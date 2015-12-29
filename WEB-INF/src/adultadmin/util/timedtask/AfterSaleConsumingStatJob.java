package adultadmin.util.timedtask;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mmb.stock.aftersale.AfStockService;
import mmb.stock.aftersale.AfterSaleCycleStatBean;
import mmb.stock.aftersale.AfterSaleCycleStatDataBean;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * 售后耗时统计
 * @author 李宁
 * @date 2014-4-4
 */
public class AfterSaleConsumingStatJob implements Job{
	public static byte[] auditOrderStatLock = new byte[0];
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("售后耗时统计定时任务开始------------------");
		synchronized (auditOrderStatLock) {
			DbOperation db = new DbOperation();
			db.init(DbOperation.DB);
			AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,db);
			
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB_SLAVE);
			PreparedStatement pst = null;
			ResultSet rs = null;
			try{
				//统计定时任务前一天的数据
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal=Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.DAY_OF_YEAR, -1);
				String statDate = sdf.format(cal.getTime());//统计日期
				String startTime = statDate + " 00:00:00";
				String endTime = statDate + " 23:59:59";
				StringBuilder statSql = new StringBuilder();
				
				List<AfterSaleCycleStatDataBean> records = new ArrayList<AfterSaleCycleStatDataBean>();
				statSql.append("select aswpr.id,aswpr.code,aswpr.after_sale_order_id,aswpr.type,aso.create_time,aso.order_id,aso.order_code,aso.after_sale_order_code ")
						.append("from after_sale_warehource_product_records aswpr join after_sale_order aso on aswpr.after_sale_order_id=aso.id ")
						.append(" where aso.last_operate_time between '").append(startTime).append("' and '").append(endTime).append("' ")
						.append(" and aso.`status`=79 and aswpr.type!=10 order by aswpr.id");
				
				//有处理单的售后单
				rs = dbOp.executeQuery(statSql.toString());
				while(rs!=null && rs.next()){
					AfterSaleCycleStatDataBean bean = new AfterSaleCycleStatDataBean();
					bean.setAfterSaleDetectProductId(rs.getInt(1));
					bean.setAfterSaleDetectProductCode(rs.getString(2));
					bean.setAfterSaleOrderId(rs.getInt(3));
					bean.setAfterSaleDetectProductType(rs.getInt(4));
					bean.setAfterSaleCreateDatetime(rs.getString(5));
					bean.setOrderId(rs.getInt(6));
					bean.setOrderCode(rs.getString(7));
					bean.setAfterSaleOrderCode(rs.getString(8));
					records.add(bean);
				}
				rs.close();
				statSql.delete(0, statSql.length());
				//补偿退款和漏发补货的售后单
				statSql.append("select id,after_sale_order_code,type,create_time,order_id,order_code from after_sale_order where type in (1,2) and last_operate_time ")
							.append(" between '").append(startTime).append("' and '").append(endTime).append("' ");
				rs = dbOp.executeQuery(statSql.toString());
				while(rs!=null && rs.next()){
					AfterSaleCycleStatDataBean bean = new AfterSaleCycleStatDataBean();
					bean.setAfterSaleOrderId(rs.getInt(1));
					bean.setAfterSaleOrderCode(rs.getString(2));
					bean.setAfterSaleType(rs.getInt(3));
					bean.setAfterSaleCreateDatetime(rs.getString(4));
					bean.setOrderId(rs.getInt(5));
					bean.setOrderCode(rs.getString(6));
					records.add(bean);
				}
				rs.close();
				statSql.delete(0, statSql.length());
				//已取消的售后单
				statSql.append("select id,after_sale_order_code,type,create_time,order_id,order_code from after_sale_order where is_canceled=1 and last_operate_time ")
							.append(" between '").append(startTime).append("' and '").append(endTime).append("' ");
				rs = dbOp.executeQuery(statSql.toString());
				while(rs!=null && rs.next()){
					AfterSaleCycleStatDataBean bean = new AfterSaleCycleStatDataBean();
					bean.setAfterSaleOrderId(rs.getInt(1));
					bean.setAfterSaleOrderCode(rs.getString(2));
					bean.setAfterSaleType(rs.getInt(3));
					bean.setAfterSaleCreateDatetime(rs.getString(4));
					bean.setOrderId(rs.getInt(5));
					bean.setOrderCode(rs.getString(6));
					records.add(bean);
				}
				
				List<AfterSaleCycleStatBean> cycleList = new ArrayList<AfterSaleCycleStatBean>();
				//开始统计耗时
				if(records!=null && records.size()>0){
					for(int i=0;i<records.size();i++){
						AfterSaleCycleStatDataBean bean = records.get(i);
						AfterSaleCycleStatBean cycleBean = new AfterSaleCycleStatBean();
						cycleBean.setAfterSaleOrderId(bean.getAfterSaleOrderId());
						cycleBean.setAfterSaleOrderCode(bean.getAfterSaleOrderCode());
						cycleBean.setAfterSaleDetectProductId(bean.getAfterSaleDetectProductId());
						cycleBean.setAfterSaleDetectProductCode(bean.getAfterSaleDetectProductCode());
						cycleBean.setAfterSaleOrderCreateDatetime(bean.getAfterSaleCreateDatetime());
						
						int afterSaleType = bean.getAfterSaleType();
						int afterSaleDetectProductType = bean.getAfterSaleDetectProductType();
						//设置售后周期里的售后类型
						if(afterSaleType==0 && afterSaleDetectProductType==0){
							cycleBean.setAfterSaleType(AfterSaleCycleStatBean.AFTER_SALE_TYPE_CANCLE);
						}else{
							if(afterSaleType > 0){
								if(afterSaleType==1){
									cycleBean.setAfterSaleType(AfterSaleCycleStatBean.AFTER_SALE_TYPE_COMPENSATION_REFUND);
								}else if(afterSaleType == 2){
									cycleBean.setAfterSaleType(AfterSaleCycleStatBean.AFTER_SALE_TYPE_REISSUE);
								}
							}
							if(afterSaleDetectProductType>0){
								if(afterSaleDetectProductType==1 || afterSaleDetectProductType == 2 || afterSaleDetectProductType == 8){
									cycleBean.setAfterSaleType(AfterSaleCycleStatBean.AFTER_SALE_TYPE_RETURN);//退货
								}else if(afterSaleDetectProductType==3 || afterSaleDetectProductType==4 || afterSaleDetectProductType==9){
									cycleBean.setAfterSaleType(AfterSaleCycleStatBean.AFTER_SALE_TYPE_REPLACE);//换货
								}else if(afterSaleDetectProductType==5 || afterSaleDetectProductType==6){
									cycleBean.setAfterSaleType(AfterSaleCycleStatBean.AFTER_SALE_TYPE_REPAIR);//维修
								}else if(afterSaleDetectProductType==7 || afterSaleDetectProductType==8){
									cycleBean.setAfterSaleType(AfterSaleCycleStatBean.AFTER_SALE_TYPE_ORIGINAL);//原品返回
								}
							}
							if(cycleBean.getAfterSaleType()==0){
								cycleBean.setAfterSaleType(-1);
							}
						}
						
						if(cycleBean.getAfterSaleType()>=0){
							long num1 = getAfterSaleConsuming(1,1,2,dbOp,bean.getAfterSaleOrderId());
							long num2 = getAfterSaleConsuming(1,1,9,dbOp,bean.getAfterSaleOrderId());
							long num3 = getAfterSaleConsuming(1,1,0,dbOp,bean.getAfterSaleOrderId());
							long num4 = getAfterSaleConsuming(3,1,2,dbOp,bean.getAfterSaleOrderId());
							cycleBean.setPreAfterSaleConsuming(num1+num2+num3);
							
							num1 = getAfterSaleConsuming(2,2,3,dbOp,bean.getAfterSaleDetectProductId());
							num2 = getAfterSaleConsuming(2,2,7,dbOp,bean.getAfterSaleDetectProductId());
							num3 = getAfterSaleConsuming(2,2,4,dbOp,bean.getAfterSaleDetectProductId());
							num4 =  getAfterSaleConsuming(2,2,5,dbOp,bean.getAfterSaleDetectProductId());
							long num5 =  getAfterSaleConsuming(2,2,6,dbOp,bean.getAfterSaleDetectProductId());
							long num6 =  getAfterSaleConsuming(2,2,1,dbOp,bean.getAfterSaleDetectProductId());
							long num7 = getAfterSaleCostsConsuming(dbOp,bean.getAfterSaleOrderId(),bean.getAfterSaleDetectProductCode());
							cycleBean.setCustomerConfirmConsuming(num1+num2+num3+num4+num5+num6+num7);
							
							num1 = getAfterSaleConsuming(4,2,4,dbOp,bean.getAfterSaleOrderId());
							cycleBean.setsApplyDeliveryConsuming(num1);
							
							cycleBean.setQualitySupportConsuming(getAfterSaleConsuming(1,0,1,dbOp,bean.getAfterSaleOrderId()));
							cycleBean.setConfirmCostsConsuming(getAfterSaleConsuming(3,6, 4, dbOp, bean.getAfterSaleOrderId()));
							
							num1 = getFinaceConsuming(dbOp,4,6,1,bean.getAfterSaleOrderId());
							num2 = getFinaceConsuming(dbOp,4,5,1,bean.getAfterSaleOrderId());
							cycleBean.setFinancialRefundConsuming(num1+num2);
							
							num1 = getFinaceConsuming(dbOp,4,6,2,bean.getAfterSaleOrderId());
							num2 = getFinaceConsuming(dbOp,4,5,2,bean.getAfterSaleOrderId());
							cycleBean.setCustomerPayMoneyConsuming(num1+num2);
							cycleBean.setCustomerReturnConsuming(getCustomerReturnConsuming(bean.getAfterSaleOrderId(),bean.getAfterSaleDetectProductCode(),dbOp));
							
							num1 = getConsuming(dbOp,1,18,bean.getAfterSaleDetectProductCode());
							num2 = getConsuming(dbOp,1,13,bean.getAfterSaleDetectProductCode());
							num3 = getConsuming(dbOp,18,13,bean.getAfterSaleDetectProductCode());
							cycleBean.setMatchPackageConsuming(num1+num2+num3);
							cycleBean.setDetectConsuming(getConsuming(dbOp,13,4,bean.getAfterSaleDetectProductCode()));
							cycleBean.setEnterAfterSaleStockConsuming(getConsuming(dbOp,4,10,bean.getAfterSaleDetectProductCode()));
							
							num1 = getConsuming(dbOp,12,8,bean.getAfterSaleDetectProductCode());
							num2 = getConsuming(dbOp,4,8,bean.getAfterSaleDetectProductCode());
							if(cycleBean.getAfterSaleType()==AfterSaleCycleStatBean.AFTER_SALE_TYPE_ORIGINAL){
								cycleBean.setAfterSaleShippingConsuming(num2);
							}else if(cycleBean.getAfterSaleType()==AfterSaleCycleStatBean.AFTER_SALE_TYPE_REPAIR){
								cycleBean.setAfterSaleShippingConsuming(num1);
							}else{
								cycleBean.setAfterSaleShippingConsuming(-1);
							}
							
							num1 = getConsuming(dbOp,4,12,bean.getAfterSaleDetectProductCode());
							num2 = getConsuming(dbOp,2,19,bean.getAfterSaleDetectProductCode());
							num3 = getConsuming(dbOp,2,20,bean.getAfterSaleDetectProductCode());
							cycleBean.setBackSupplierConsuming(num1+num2+num3);
							
							num1 =  getConsuming(dbOp,12,21,bean.getAfterSaleDetectProductCode());
							num2 =  getConsuming(dbOp,21,2,bean.getAfterSaleDetectProductCode());
							cycleBean.setRepairsConsuming(num1+num2);
							cycleBean.setsShippingConsuming(getSShippingConsuming(dbOp,4,4,bean.getAfterSaleOrderId()));
							
							cycleBean.setCreateDatetime(DateUtil.getNow());
							long totalConsuming = cycleBean.getCustomerReturnConsuming() + cycleBean.getPreAfterSaleConsuming() + cycleBean.getCustomerConfirmConsuming()
									+ cycleBean.getConfirmCostsConsuming() + cycleBean.getsApplyDeliveryConsuming() + cycleBean.getQualitySupportConsuming()
									+ cycleBean.getMatchPackageConsuming() + cycleBean.getDetectConsuming() + cycleBean.getEnterAfterSaleStockConsuming()
									+ cycleBean.getAfterSaleShippingConsuming() + cycleBean.getBackSupplierConsuming() + cycleBean.getFinancialRefundConsuming()
									+ cycleBean.getRepairsConsuming() + cycleBean.getCustomerPayMoneyConsuming() + cycleBean.getCustomerPayMoneyConsuming()
									+ cycleBean.getsShippingConsuming();
							cycleBean.setTotalConsuming(totalConsuming);
							cycleList.add(cycleBean);
						}
					}
				}
				
				afStockService.getDbOp().startTransaction();
				StringBuilder insertSql = new StringBuilder();
				insertSql.append(" insert into after_sale_cycle_stat ");
				insertSql.append(" (after_sale_order_id,after_sale_order_code,after_sale_detect_product_id,after_sale_detect_product_code,after_sale_type,after_sale_create_datetime,create_datetime,")
							.append("customer_return_consuming,pre_after_sale_consuming,customer_confirm_consuming,confirm_costs_consuming,s_apply_delivery_consuming,")
							.append("quality_support_consuming,match_package_consuming,detect_consuming,enter_aftersale_stock_consuming,after_sale_shipping_consuming,")
							.append("back_supplier_consuming,financial_refund_consuming,repairs_consuming,customer_pay_money_consuming,s_shipping_cousuming,")
							.append("total_consuming) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				pst = db.getConn().prepareStatement(insertSql.toString());
				int count = 0;
				if(cycleList!=null && cycleList.size()>0){
					for(int i=0;i<cycleList.size();i++){
						AfterSaleCycleStatBean cycleBean = cycleList.get(i);
						pst.setInt(1, cycleBean.getAfterSaleOrderId());
						pst.setString(2, cycleBean.getAfterSaleOrderCode());
						pst.setInt(3, cycleBean.getAfterSaleDetectProductId());
						pst.setString(4,cycleBean.getAfterSaleDetectProductCode());
						pst.setInt(5, cycleBean.getAfterSaleType());
						pst.setString(6,cycleBean.getAfterSaleOrderCreateDatetime());
						pst.setString(7, cycleBean.getCreateDatetime());
						pst.setLong(8, cycleBean.getCustomerReturnConsuming());
						pst.setLong(9, cycleBean.getPreAfterSaleConsuming());
						pst.setLong(10, cycleBean.getCustomerConfirmConsuming());
						pst.setLong(11, cycleBean.getConfirmCostsConsuming());
						pst.setLong(12, cycleBean.getsApplyDeliveryConsuming());
						pst.setLong(13, cycleBean.getQualitySupportConsuming());
						pst.setLong(14, cycleBean.getMatchPackageConsuming());
						pst.setLong(15, cycleBean.getDetectConsuming());
						pst.setLong(16, cycleBean.getEnterAfterSaleStockConsuming());
						pst.setLong(17, cycleBean.getAfterSaleShippingConsuming());
						pst.setLong(18, cycleBean.getBackSupplierConsuming());
						pst.setLong(19, cycleBean.getFinancialRefundConsuming());
						pst.setLong(20, cycleBean.getRepairsConsuming());
						pst.setLong(21, cycleBean.getCustomerPayMoneyConsuming());
						pst.setLong(22, cycleBean.getsShippingConsuming());
						pst.setLong(23, cycleBean.getTotalConsuming());
						pst.addBatch();
						count++;
						if(count%1000 == 0 || i==cycleList.size()-1){
							pst.executeBatch();
						}
					}
				}
				afStockService.getDbOp().commitTransaction();
			}catch (Exception e) {
				e.printStackTrace();
				afStockService.getDbOp().rollbackTransaction();
			}finally{
				afStockService.releaseAll();
				dbOp.release();
			}
			System.out.println("售后耗时统计定时任务结束--------------------");
		}
	}
	
	/**
	 * 获取销售那边相关日志的耗时
	 * @param type
	 * @param preStatus
	 * @param nowStatus
	 * @param dbOp
	 * @param afterSaleOrderId
	 * @return 
	 */
	private long getAfterSaleConsuming(int type,int preStatus,int nowStatus,DbOperation dbOp,int id){
		long total = 0;
		List<Integer> list = new ArrayList<Integer>();
		ResultSet rs = null;
		try {
			if(id>0){
				if(type==3){//售后费用明细单据
					//需要查询相应费用单的id
					rs = dbOp.executeQuery("select id from after_sale_charge_list where after_sale_order_id=" + id);
					while(rs!=null && rs.next()){
						int recordId = rs.getInt(1);
						list.add(recordId);
					}
					rs.close();
				}else if(type==4){//售后操作单
					//需要查询相应费用单的id
					rs = dbOp.executeQuery("select id from after_sale_operation_list where after_sale_order_id=" + id);
					while(rs!=null && rs.next()){
						int recordId = rs.getInt(1);
						list.add(recordId);
					}
					rs.close();
				}else{
					list.add(id);
				}
				if(list!=null && list.size()>0){
					for(int i=0;i<list.size();i++){
						StringBuilder query = new StringBuilder();
						query.append("select TIMESTAMPDIFF(SECOND,pre_operate_time,now_operate_time) from after_sale_order_service_state_log ")
								.append(" where type=").append(type)
								.append(" and pre_status=").append(preStatus).append(" and now_status=").append(nowStatus).append(" and record_id=").append(list.get(i));
						rs = dbOp.executeQuery(query.toString());
						if(rs != null){
							while(rs.next()){
								total += rs.getLong(1);
							}
						}else{
							total = -1;
						}
					}
				}else{
					total = -1;
				}
			}else{
				total = -1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return total;
	}
	
	/**
	 * 获取财务相关的耗时
	 * @param preStatus
	 * @param nowStatus
	 * @param dbOp
	 * @param afterSaleOrderId
	 * @return
	 */
	private long getFinaceConsuming(DbOperation dbOp,int preStatus,int nowStatus,int payTo,int afterSaleOrderId){
		long total = 0;
		StringBuilder query = new StringBuilder();
		query.append("select sum(TIMESTAMPDIFF(SECOND,asofsl.pre_operate_time,asofsl.now_operate_time)) difftime from after_sale_order_finace_state_log asofsl ")
				.append("join after_sale_charge_list ascl on asofsl.record_id=ascl.id where asofsl.type=1 and asofsl.pre_status=").append(preStatus)
				.append(" and asofsl.now_status=").append(nowStatus).append(" and ascl.pay_to=").append(payTo).append(" and ascl.after_sale_order_id=").append(afterSaleOrderId);
		ResultSet rs = null;
		try {
			rs = dbOp.executeQuery(query.toString());
			if(rs != null && rs.next()){
				total = rs.getLong(1);
			}else{
				total = -1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return total;
	}
	
	/**
	 * 获取用户寄回耗时
	 * @param afterSaleOrderId
	 * @param afterSaleDetectProductCode
	 * @param dbOp
	 * @return
	 */
	private long getCustomerReturnConsuming(int afterSaleOrderId,String afterSaleDetectProductCode,DbOperation dbOp){
		int total = 0;
		ResultSet rs = null;
		try{
			if(afterSaleDetectProductCode!=null && afterSaleDetectProductCode.trim().length()>0){
				String startTime = null;
				String endTime = null;
				String query = "select now_operate_time from after_sale_order_service_state_log where type=1 and now_status=2 and record_id=" + afterSaleOrderId;
				rs = dbOp.executeQuery(query);
				if(rs!=null && rs.next()){
					startTime = rs.getString(1);
				}
				rs.close();
				query = "select create_datetime from after_sale_log where type=1 and oper_code='" + afterSaleDetectProductCode + "'";
				rs = dbOp.executeQuery(query);
				if(rs!=null && rs.next()){
					endTime = rs.getString(1);
				}
				if(startTime!=null && !(StringUtil.checkNull(startTime).equals("")) && endTime!=null && !(StringUtil.checkNull(endTime).equals(""))){
					total = DateUtil.getMinuteSub(startTime,endTime);
					if(total!=-1){
						total = total * 60;
					}
				}else{
					total = -1;
				}
			}else{
				total = -1;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return total;
	}
	
	
	private long getConsuming(DbOperation dbOp,int preType,int nowType,String operCode){
		long total = 0;
		ResultSet rs = null;
		try{
			if(operCode!= null && operCode.trim().length()>0){
				List<String> startTimeList = new ArrayList<String>();
				List<String> endTimeList = new ArrayList<String>();
				String query = "select create_datetime from after_sale_log where type="+ preType +" and oper_code='" + operCode + "'";
				rs = dbOp.executeQuery(query);
				while(rs!=null && rs.next()){
					if(!(StringUtil.checkNull(rs.getString(1)).equals(""))){
						startTimeList.add(rs.getString(1).trim());
					}
				}
				rs.close();
				
				query = "select create_datetime from after_sale_log where type=" + nowType + " and oper_code='" + operCode + "'";
				rs = dbOp.executeQuery(query);
				while(rs!=null && rs.next()){
					if(!(StringUtil.checkNull(rs.getString(1)).equals(""))){
						endTimeList.add(rs.getString(1).trim());
					}
				}
				rs.close();
				if(startTimeList!=null && startTimeList.size()>0 && endTimeList!=null && endTimeList.size()>0){
					for(int i=0;i<startTimeList.size();i++){
						String startTime = startTimeList.get(i);
						String endTime = endTimeList.get(i);
						if(startTime.length()>0 && endTime.length()>0){
							total = DateUtil.getMinuteSub(startTime,endTime);
							if(total!=-1){
								total += total * 60;
							}
						}
					}
				}else{
					total = -1;
				}
			}else{
				total = -1;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return total;
	}
	
	/**
	 * 获取s单发货耗时
	 * @param dbOp
	 * @param type
	 * @param orderId
	 * @param operCode
	 * @return
	 */
	private long getSShippingConsuming(DbOperation dbOp,int type,int nowStatus,int afterSaleOrderId){
		long total = 0;
		ResultSet rs = null;
		try{
			//需要查询相应费用单的id
			List<Integer> recordIds = new ArrayList<Integer>();
			List<Integer> orderIds = new ArrayList<Integer>();
			rs = dbOp.executeQuery("select id,order_id from after_sale_operation_list where type!=2 and  after_sale_order_id=" + afterSaleOrderId);
			while(rs!=null && rs.next()){
				recordIds.add(rs.getInt(1));
				orderIds.add(rs.getInt(2));
			}
			rs.close();
			if(recordIds!=null && recordIds.size()>0){
				for(int i=0;i<recordIds.size();i++){
					int recordId = recordIds.get(i);
					int orderId = orderIds.get(i);
					String startTime = null;
					String endTime = null;
					String query = "select now_operate_time from after_sale_order_service_state_log where type="+ type +" and record_id=" + recordId + " and now_status=" + nowStatus ;
					rs = dbOp.executeQuery(query);
					if(rs!=null && rs.next()){
						if(!(StringUtil.checkNull(rs.getString(1)).equals(""))){
							startTime = rs.getString(1).trim();
						}
					}
					rs.close();
					query = "select check_datetime from audit_package where order_id=" + orderId;
					rs = dbOp.executeQuery(query);
					if(rs!=null && rs.next()){
						if(!(StringUtil.checkNull(rs.getString(1)).equals(""))){
							endTime = rs.getString(1).trim();
						}
					}
					if(startTime!=null && startTime.length()>0 && endTime!=null && endTime.length()>0){
						total = DateUtil.getMinuteSub(startTime,endTime);
						if(total!=-1){
							total += total * 60;
						}
					}else{
						total = -1;
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return total;
	}
	
	/**
	 * 获取维修费用确认耗时
	 * @param dbOp
	 * @param afterSaleOrderId
	 * @param operCode
	 * @return
	 */
	private long getAfterSaleCostsConsuming(DbOperation dbOp,int afterSaleOrderId,String operCode){
		long total = 0;
		ResultSet rs = null;
		try{
			if(operCode!=null && operCode.trim().length()>0){
				String startTime = null;
				String endTime = null;
				String query = "select now_operate_time from after_sale_order_service_state_log where type=3 and now_status=4 and record_id=" + afterSaleOrderId;
				rs = dbOp.executeQuery(query);
				if(rs!=null && rs.next()){
					if(!(StringUtil.checkNull(rs.getString(1)).equals(""))){
						startTime = rs.getString(1).trim();
					}
				}
				rs.close();
				query = "select create_datetime from after_sale_log where type=21 and oper_code='" + operCode + "'";
				rs = dbOp.executeQuery(query);
				if(rs!=null && rs.next()){
					if(!(StringUtil.checkNull(rs.getString(1)).equals(""))){
						endTime = rs.getString(1).trim();
					}
				}
				if(startTime!=null && startTime.length()>0 && endTime!=null && endTime.length()>0){
					total = DateUtil.getMinuteSub(startTime,endTime);
					if(total!=-1){
						total += total * 60;
					}
				}else{
					total = -1;
				}
			}else{
				total = -1;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return total;
	}
}
