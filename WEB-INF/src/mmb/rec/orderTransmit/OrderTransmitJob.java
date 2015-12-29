package mmb.rec.orderTransmit;

import java.sql.ResultSet;

import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class OrderTransmitJob implements Job {

	@Override
	// 出库交接
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("出库交接统计定时任务开始");
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		WareService wareService1 = new WareService();
		OrderTransmitJobService cojService = ServiceFactory.createOrderTransmitJobService(IBaseService.CONN_IN_SERVICE,
				wareService1.getDbOp());
		int orderCount = 0;
		int area = 0;
		String date = DateUtil.getBeforOneDay();
		try {
			String sqlString = "SELECT count(distinct a.id),b.stock_area FROM user_order a left join order_stock b on a.id = b.order_id left join mailing_batch_package c on b.order_id=c.order_id  where b.status<>3 and  c.create_datetime >'"
					+ date + " 00:00:00' and  c.create_datetime<'" + date + " 23:59:59' group by b.stock_area";
			ResultSet rs = wareService.getDbOp().executeQuery(sqlString);
			while (rs.next()) {
				orderCount = rs.getInt(1);
				area = rs.getInt(2);
				OrderTransmitJobBean cosjBean = new OrderTransmitJobBean();
				cosjBean.setDate(date);
				cosjBean.setArea(area);
				cosjBean.setOrderCount(orderCount);
				cojService.addOrderTransimitJobBean(cosjBean);
			}
			rs.close();
			System.out.println("出库交接统计定时任务结束");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("出库交接统计定时任务异常");
		} finally {
			wareService.releaseAll();
			wareService1.releaseAll();
		}

	}

}
