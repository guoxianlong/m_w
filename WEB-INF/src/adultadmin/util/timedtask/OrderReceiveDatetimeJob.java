package adultadmin.util.timedtask;

import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class OrderReceiveDatetimeJob implements Job {
	public static byte[] OrderReceiveDatetimeJobLock = new byte[0];

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		System.out.println(DateUtil.getNow() + "记录订单签收时间任务开始");
		synchronized (OrderReceiveDatetimeJobLock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			WareService wareService = new WareService(dbOp);

			String yesterday = DateUtil.getBackFromDate(
					DateUtil.getNowDateStr(), 1);
			try {
				wareService.getDbOp().startTransaction();// 事务开始

				wareService
						.executeUpdate(
								"update audit_package ap,deliver_order do "
										+ "set ap.receive_datetime=do.receive_time "
										+ "where ap.order_id=do.order_id and do.receive_time>='"
										+ yesterday
										+ " 00:00:00' and do.receive_time <='"
										+ yesterday + " 23:59:59' and deliver_state=7") ;

				wareService.getDbOp().commitTransaction();// 事务提交
				System.out.println(DateUtil.getNow() + "记录订单签收时间任务结束");
			} catch (Exception e) {
				e.printStackTrace();
				wareService.getDbOp().rollbackTransaction();
				System.out.println(DateUtil.getNow() + "记录订单签收时间任务异常");
			} finally {
				wareService.releaseAll();
			}
		}
	}
}
