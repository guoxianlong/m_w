package mmb.rec.checkOrderStat;

import java.sql.ResultSet;

import mmb.rec.orderTransmit.OrderTransmitJobService;
import mmb.stock.stat.SortingInfoService;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class CheckOrderStatJob implements Job {

	@Override
	// 复核统计
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("复核统计定时任务开始");
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		WareService wareService1 = new WareService();
		CheckOrderStatJobService cojService = ServiceFactory.createCheckOrderStatJobService(
				IBaseService.CONN_IN_SERVICE, wareService1.getDbOp());
		int orderCount = 0;
		int skuCount = 0;
		int productCount = 0;
		int area = 0;
		String date = DateUtil.getBeforOneDay();
		try {
			String sqlString = "select count(distinct a.id),count(distinct d.product_code),sum(stockout_count),c.stock_area from user_order as a "
					+ " left join order_stock as c on a.id =c.order_id left join order_stock_product as d on c.id=d.order_stock_id "
					+ " left join audit_package ap on c.order_id=ap.order_id"
					+ " where  c.status<>3 and ap.check_datetime >'"
					+ date
					+ " 00:00:00' and ap.check_datetime<'"
					+ date + " 23:59:59' group by c.stock_area";
			ResultSet rs = wareService.getDbOp().executeQuery(sqlString);
			while (rs.next()) {
				orderCount = rs.getInt(1);
				skuCount = rs.getInt(2);
				productCount = rs.getInt(3);
				area = rs.getInt(4);
				CheckOrderStatJobBean cosjBean = new CheckOrderStatJobBean();
				cosjBean.setDate(date);
				cosjBean.setArea(area);
				cosjBean.setOrderCount(orderCount);
				cosjBean.setProductCount(productCount);
				cosjBean.setSkuCount(skuCount);
				cojService.addCheckOrderStatJobBean(cosjBean);
			}
			rs.close();
			System.out.println("复核统计定时任务结束");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("复核统计定时任务异常");
		} finally {
			wareService.releaseAll();
			wareService1.releaseAll();
		}

	}

}
