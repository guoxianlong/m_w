package adultadmin.util.timedtask;

import mmb.rec.oper.service.OuterOrderService;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class OuterOrderAddJob implements Job {
	public static byte[] StockExceptionJobLock = new byte[0];

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		System.out.println(DateUtil.getNow() + "京东3C拽单定时任务开始");
		synchronized (StockExceptionJobLock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			WareService wareService = new WareService(dbOp);
			//String today = DateUtil.getNowDateStr();
			//String startDate = "2015-01-16" + " 00:00:00";
			//String endDate = "2015-02-03" + " 23:59:59";
			
			String endDate = DateUtil.getNowDateStr();
			String startDate =  DateUtil.getBackFromDate(endDate, 13);
			startDate+=" 00:00:00";
			endDate+=" 23:59:59";
			try {
				int pageSize = 100;
				int maxPage = 99999;
				OuterOrderService ooService = new OuterOrderService();
				ooService.getJDOrders(startDate, endDate, pageSize, maxPage, dbOp);
				System.out.println(DateUtil.getNow() + "京东3C拽单定时任务结束");
			} catch (Exception e) {
				e.printStackTrace();
				wareService.getDbOp().rollbackTransaction();
				System.out.println(DateUtil.getNow() + "京东3C拽单定时任务异常");
			} finally {
				wareService.releaseAll();
			}
		}
	}
}
