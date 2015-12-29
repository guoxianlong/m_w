package adultadmin.action.stock;

import java.io.BufferedReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.finance.balance.BalanceService;
import mmb.finance.stat.FinanceReportFormsService;
import mmb.stock.stat.SortingBatchBean;
import mmb.stock.stat.SortingBatchOrderBean;
import mmb.stock.stat.SortingInfoService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voUser;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stock.StockAdminHistoryBean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBalanceService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

/**
 *  <code>BatchUpdateOrderDeliverAction.java</code>
 *  <p>功能:批量修改订单的快递公司Action (根据上传的excel文件)
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-6-15 下午05:35:11	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class BatchUpdateOrderDeliverAction extends BaseAction {
	
	public static byte[] lock = new byte[0];
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
    	voUser loginUser = (voUser)request.getSession().getAttribute("userView");
		if (loginUser == null) {
			request.setAttribute("tip", "当前没有登录，添加失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}
		//根据订单号，批量修改快递公司
		synchronized (lock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			DbOperation dbOpSlave = new DbOperation();
			dbOpSlave.init(DbOperation.DB_SLAVE);
			
			WareService wareService = new WareService(dbOp);
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, dbOp);
			IBalanceService balanceService=ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE, dbOp);
			BalanceService balanceService2=new BalanceService(IBaseService.CONN_IN_SERVICE, dbOp);
			FinanceReportFormsService fService=new FinanceReportFormsService(IBaseService.CONN_IN_SERVICE, dbOp);
			
			SortingInfoService siService2 = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, dbOpSlave);
			
			int result=0,failure = 0;
			Statement st = null;
			try{
				StringBuffer update = new StringBuffer();
				StringBuffer error = new StringBuffer();
				String data = request.getParameter("ordersDeliver");
				if(data==null || data.length()==0){
					request.setAttribute("tip", "单号和快递信息不能为空！");
				}else{
					Connection conn = dbOp.getConn();
					st = conn.createStatement();
					BufferedReader read = new BufferedReader(new StringReader(data));
					String tmp=null;
					int i=1;
					while((tmp = read.readLine())!=null){
						dbOp.startTransaction();
						String[] str = tmp.trim().split("\\s");
						if(str.length<2){
							str =tmp.trim().split("\\t");
						}
						if(str.length>=2){
							String orderCode = str[0];
							String deliverName = str[1];
							if(orderCode!=null && deliverName!=null){
								int deliver=0;
								//获取快递公司编号
								if (voOrder.deliverChangeMap.containsValue(deliverName)) {
									Set set = voOrder.deliverChangeMap.entrySet();
									Iterator it = set.iterator();
									while (it.hasNext()) {
										Map.Entry entry = (Map.Entry) it.next();
										if (entry.getValue().equals(deliverName)) {
											deliver = Integer.parseInt(entry.getKey().toString());
										}
									}
								}
								orderCode = "".equals(orderCode)?"订单号为空":orderCode; 
								deliverName = "".equals(deliverName)?"快递公司为空":deliverName; 
								voOrder userOrder = wareService.getOrder(" code='"+orderCode+"'");
								
								if(userOrder==null){
									error.append("<font color=\"red\">").append(orderCode).append("</font> ").append(deliverName).append("<br/>");
									failure++;
									continue;
								}else if(deliver==0){
									error.append(orderCode).append(" <font color=\"red\">").append(deliverName).append("</font><br/>");
									failure++;
									continue;
								}else{
									SortingBatchOrderBean sboBean = siService2.getSortingBatchOrderInfo("order_code='" + orderCode + "' and delete_status<>1 ");
									if(sboBean!=null){
										if (sboBean.getDeleteStatus() == 1) {
											error.append(orderCode).append(" <font color=\"red\">").append(deliverName).append("</font><br/>");
											dbOp.rollbackTransaction();
											failure++;
											continue;
										}
										else{
											// 该订单所处的批次的状态是分拣中并且，并且该批次中存在分拣中之前的订单，则该订单不能被修改快递公司
											SortingBatchBean batchBean = siService.getSortingBatchInfo("id=" + sboBean.getSortingBatchId());
											//if(batchBean.getType1()==1){
												int orderCount = siService.getSortingBatchOrderCount("delete_status<>1 and status in(0,1) and sorting_batch_id="+sboBean.getSortingBatchId());//该批次下未打单的订单数
												int orderCount2 = siService.getSortingBatchOrderCount("delete_status<>1 and status in(2,3) and sorting_batch_id="+sboBean.getSortingBatchId());//该批次下已打单的订单数
												if (orderCount!=0&&orderCount2!=0) {//该批次下有未打单的也有已打单的，则不能修改快递公司
													error.append(orderCode).append(" <font color=\"red\">").append(deliverName).append("</font><br/>");
													dbOp.rollbackTransaction();
													failure++;
													continue;
												} 
											//}
											siService.updateSortingBatchOrderInfo("deliver=" + deliver, "order_id=" + sboBean.getOrderId() + " and delete_status<>1");
											SortingBatchBean sbBean = siService.getSortingBatchInfo("id=" + sboBean.getSortingBatchId());
											if (sbBean != null) {
												if (sbBean.getStatus() == SortingBatchBean.STATUS0) {
													if (!siService.updateSortingBatchInfo("status=1", "id=" + sboBean.getSortingBatchId())) {
														request.setAttribute("tip", "操作失败!");
														request.setAttribute("result", "failure");
														dbOp.rollbackTransaction();
														return mapping.findForward(IConstants.FAILURE_KEY);
													}
												}
											}
										}
									}
//									else{
//										adminService.rollbackTransaction();
//										failure++;
//										continue;
//									}
									update.append(" update user_order set deliver=");
									update.append(deliver);
									update.append(" where code='").append(orderCode).append("'");
									st.addBatch(update.toString());
									update.delete(0, update.length());
									
									update.append(" update order_stock set deliver=");
									update.append(deliver);
									update.append(" where order_code='").append(orderCode).append("'");
									st.addBatch(update.toString());
									update.delete(0, update.length());
									
									//修改核对包裹记录
									update.append(" update audit_package set deliver=");
									update.append(deliver);
									update.append(" where order_code='").append(orderCode).append("'");
									st.addBatch(update.toString());
									update.delete(0, update.length());
									
									voOrder order=wareService.getOrder("code='"+orderCode+"'");
									//若订单已出库，需要修改财务表快递公司数据
									
									if(order!=null&&(order.getStatus()==6||order.getStatus()==11||order.getStatus()==13||order.getStatus()==14)){
										String balanceType="";
										if(voOrder.deliverToBalanceTypeMap.get(deliver+"")!=null){
											balanceType=voOrder.deliverToBalanceTypeMap.get(deliver+"").toString();
										}else{
											dbOp.rollbackTransaction();
											failure++;
											continue;
										}
										
										String firstDate=DateUtil.getFirstDayOfMonth();
										int lastDate=DateUtil.getLastDateByMonth(new Date());
										if(balanceService.getMailingBalance("stockout_datetime <='" + lastDate+" 23:59:59" + "' AND stockout_datetime >='" + firstDate+" 00:00:00" + "' AND mailing_balance_auditing_id = 0 and order_code='"+orderCode+"'") != null){
											//报错：非本月数据 or 已经提交结算！
											//return;
											balanceService.updateMailingBalance("balance_type =" + balanceType, " mailing_balance_auditing_id = 0 AND order_code = '" + orderCode+"'");
											
											if(balanceService2.getFinanceMailingBalanceCount("order_code = '" + orderCode+"'") == 1){
												balanceService2.updateFinanceMailingBalanceBean("balance_type =" + balanceType, "mailing_balance_auditing_id = 0 AND data_type = 0 AND order_code = '" + orderCode+"'");
											}

											fService.updateFinanceSellBean("deliver_type = " + balanceType,  "data_type = 0 AND order_id = "+order.getId());
										}
									}
									
									i++;
					                //添加快递公司修改日志
									OrderStockBean orderstockBean = service.getOrderStock("order_code = '" +orderCode+"'");
									String orderStockCode="" ;
									if(orderstockBean==null){
										orderStockCode="出库单已删除!";
									}else{
										orderStockCode=orderstockBean.getCode();
									}
									
									String firstDeilver = "";
									if(userOrder.getDeliver()==0){
										firstDeilver="空";
									}else{
										 firstDeilver = ""+userOrder.getDeliverMapAll().get(String.valueOf(userOrder.getDeliver()));
									}
									StockAdminHistoryBean log = new StockAdminHistoryBean();
									log.setAdminId(loginUser.getId());
				                    log.setAdminName(loginUser.getUsername());
				                    log.setLogId(orderstockBean.getId());
				                    log.setLogType(StockAdminHistoryBean.ORDER_DELIVER);
				                    log.setOperDatetime(DateUtil.getNow());
				                    log.setRemark("订单号(" +userOrder.getCode()+")出库单编号 :("+orderStockCode+")快递公司("+firstDeilver+"-"+voOrder.deliverMapAll.get(String.valueOf(deliver))+")");
				                    log.setType(StockAdminHistoryBean.CHANGE);
				                    service.addStockAdminHistory(log);
								}
								dbOp.commitTransaction();
								if(i%100==0){
									st.executeBatch();
									result += i-1;
									i=1; 
								}
							}
						}else if(str.length==1 && str[0].length()>0){
							error.append(" <font color=\"red\">").append(str[0]).append("</font><br/>");
							failure++;
							continue;
						}else{
							error.append(" <font color=\"red\">").append(tmp+"  格式错误</font><br/>");
							failure++;
							continue;
						}
					}
					if(i!=1){
						st.executeBatch();
						result+=i-1;
					}
					String str="<font color='red'>快递公司修改成功"+result+"个，失败"+failure+"个！</font><br/>";
					error.insert(0, str);
					request.setAttribute("result", error);
				}
			}catch(Exception e){
				e.printStackTrace();
				dbOp.rollbackTransaction();
				request.setAttribute("result", e.getMessage());
			}finally{
				if(st!=null)st.close();
				dbOp.release();
				dbOpSlave.release();
			}
			return mapping.findForward("success");
		}
	}
}

