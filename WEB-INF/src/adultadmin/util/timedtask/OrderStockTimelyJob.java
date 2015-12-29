package adultadmin.util.timedtask;

/**
 * 发货及时率统计
 */
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import mmb.stock.stat.OrderStockTimelyBean;
import mmb.stock.stat.StatService;
import mmb.system.admin.AdminService;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.action.vo.voUser;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class OrderStockTimelyJob  implements Job {
	public static byte[] orderStockTimelyLock = new byte[0];
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("发货成功率统计定时任务开始");
		synchronized(orderStockTimelyLock){
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult");
			WareService wareService = new WareService(dbOp);
			IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, dbOp);
			AdminService adminService = new AdminService(IBaseService.CONN_IN_SERVICE, dbOp);
			try{
				wareService.getDbOp().startTransaction();//事务开始
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String dateStr=DateUtil.getNowDateStr();
				Calendar cal=Calendar.getInstance();//当日的第二天
				cal.setTime(sdf.parse(dateStr+" 00:00:00"));
				cal.add(Calendar.DAY_OF_YEAR, -1);
				cal.add(Calendar.HOUR, -6);
				String time1=sdf.format(cal.getTime());//昨天18点
				cal.add(Calendar.DAY_OF_YEAR, 1);
				String time2=sdf.format(cal.getTime());//今天18点
				System.out.println("发货成功率统计定时任务，开始时间"+time1);
				System.out.println("发货成功率统计定时任务，结束时间"+time2);
				cal.add(Calendar.HOUR, -10);
				String time3=sdf.format(cal.getTime());//今天8点
				cal.add(Calendar.DAY_OF_YEAR, 1);
				String time4=sdf.format(cal.getTime());//第二天8点
				
				statService.deleteOrderStockTimely("date='"+time2.substring(0,10)+"'");
				
				//1、当日订单未发货：昨日18点-今日18点申请发货的订单中，明日8点前未复核出库的订单.
				//2、当日订单已发货：昨日18点-今日18点申请发货的订单中，在昨日19点-明日8点已复核出库的订单.
				List orderStockList=stockService.getOrderStockList("create_datetime>='"+time1+"' and create_datetime<'"+time2+"' and status<>3", -1, -1, null);
				for(int i=0;i<orderStockList.size();i++){
					OrderStockBean orderStock=(OrderStockBean)orderStockList.get(i);
					
					OrderStockTimelyBean ost=new OrderStockTimelyBean();
					ost.setDate(time2.substring(0,10));
					ost.setOrderId(orderStock.getOrderId());
					ost.setOrderCode(orderStock.getOrderCode());
					
					OrderStockBean firstOrderStock=stockService.getOrderStock("order_id="+orderStock.getOrderId()+" order by create_datetime desc limit 1");
					ost.setFirstOrderStockDatetime(firstOrderStock.getCreateDatetime());
					ost.setFirstOrderStockUserId(firstOrderStock.getCreateUserId());
					
					voUser firstStockOutUser=adminService.getAdmin(firstOrderStock.getCreateUserId());
					if(firstStockOutUser!=null){
						ost.setFirstOrderStockUserName(firstStockOutUser.getUsername());
					}else{
						ost.setFirstOrderStockUserName("");
					}
					
					AuditPackageBean apBean=stockService.getAuditPackage("order_id="+orderStock.getOrderId());
					if(apBean==null||apBean.getCheckUserName().equals("")){
						ost.setStockOutDatetime(null);
						ost.setStockOutUserId(0);
						ost.setStockOutUserName("");
					}else{
						ost.setStockOutDatetime(apBean.getCheckDatetime());
						ost.setStockOutUserName(apBean.getCheckUserName());
						voUser stockOutUser=adminService.getAdmin(apBean.getCheckUserName());
						if(stockOutUser==null){
							ost.setStockOutUserId(0);
						}else{
							ost.setStockOutUserId(stockOutUser.getId());
							ost.setStockOutUserName(stockOutUser.getUsername());
						}
					}
					int orderStockCount=stockService.getOrderStockCount("order_id="+orderStock.getOrderId());
					ost.setOrderStockCount(orderStockCount);
					statService.addOrderStockTimely(ost);
				}
				
				//3：历史订单未发货：昨日18点前申请发货的订单中，明日8点未复核出库完成的订单.
				List orderStockList2=stockService.getOrderStockList("create_datetime<'"+time1+"' and status in (0,1,5)"+" and create_datetime>'2012-08-04 00:00:00'", -1, -1, null);//申请出库时间是今天以前，至今未复核出库
				for(int i=0;i<orderStockList2.size();i++){
					OrderStockBean orderStock=(OrderStockBean)orderStockList2.get(i);
					
					OrderStockTimelyBean ost=new OrderStockTimelyBean();
					ost.setDate(time2.substring(0,10));
					ost.setOrderId(orderStock.getOrderId());
					ost.setOrderCode(orderStock.getOrderCode());
					
					OrderStockBean firstOrderStock=stockService.getOrderStock("order_id="+orderStock.getOrderId()+" order by create_datetime desc limit 1");
					ost.setFirstOrderStockDatetime(firstOrderStock.getCreateDatetime());
					ost.setFirstOrderStockUserId(firstOrderStock.getCreateUserId());
					
					voUser firstStockOutUser=adminService.getAdmin(firstOrderStock.getCreateUserId());
					if(firstStockOutUser!=null){
						ost.setFirstOrderStockUserName(firstStockOutUser.getUsername());
					}else{
						ost.setFirstOrderStockUserName("");
					}
					ost.setStockOutDatetime(null);
					ost.setStockOutUserId(0);
					ost.setStockOutUserName("");
					int orderStockCount=stockService.getOrderStockCount("order_id="+orderStock.getOrderId());
					ost.setOrderStockCount(orderStockCount);
					statService.addOrderStockTimely(ost);
				}
				
				//4：当日历史订单已发货：昨日18点前申请发货的订单中，今日8点-明日8点已复核出库的订单.
				List auditPackageList=stockService.getAuditPackageList("check_datetime>='"+time3+"' and check_datetime<'"+time4+"'", -1, -1, null);
				for(int i=0;i<auditPackageList.size();i++){
					AuditPackageBean apBean=(AuditPackageBean)auditPackageList.get(i);
					OrderStockBean osBean=stockService.getOrderStock("order_id="+apBean.getOrderId()+" and create_datetime<'"+time1+"'");
					if(osBean==null){
						continue;
					}else{
						OrderStockTimelyBean ost=new OrderStockTimelyBean();
						ost.setDate(time2.substring(0,10));
						ost.setOrderId(osBean.getOrderId());
						ost.setOrderCode(osBean.getOrderCode());
						
						OrderStockBean firstOrderStock=stockService.getOrderStock("order_id="+osBean.getOrderId()+" order by create_datetime desc limit 1");
						ost.setFirstOrderStockDatetime(firstOrderStock.getCreateDatetime());
						ost.setFirstOrderStockUserId(firstOrderStock.getCreateUserId());
						
						voUser firstStockOutUser=adminService.getAdmin(firstOrderStock.getCreateUserId());
						if(firstStockOutUser!=null){
							ost.setFirstOrderStockUserName(firstStockOutUser.getUsername());
						}else{
							ost.setFirstOrderStockUserName("");
						}
						ost.setStockOutDatetime(apBean.getCheckDatetime());
						voUser stockOutUser=adminService.getAdmin(apBean.getCheckUserName());
						if(stockOutUser==null){
							ost.setStockOutUserId(0);
						}else{
							ost.setStockOutUserId(stockOutUser.getId());
							ost.setStockOutUserName(stockOutUser.getUsername());
						}
						int orderStockCount=stockService.getOrderStockCount("order_id="+osBean.getOrderId());
						ost.setOrderStockCount(orderStockCount);
						statService.addOrderStockTimely(ost);
					}
				}
				
				wareService.getDbOp().commitTransaction();//事务提交
				System.out.println("发货成功率统计定时任务结束");
			}catch (Exception e) {
				e.printStackTrace();
				wareService.getDbOp().rollbackTransaction();
			} finally {
				wareService.releaseAll();
			}
		}
	}
	
	
}
