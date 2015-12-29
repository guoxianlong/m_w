package adultadmin.util.timedtask;

import java.sql.ResultSet;
import java.util.HashMap;

import mmb.rec.oper.service.OrderStockService;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class OrderSendOutCountJob implements Job {
	public static byte[] StockExceptionJobLock = new byte[0];

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		System.out.println(DateUtil.getNow() + "应发货量定时任务开始");
		synchronized (StockExceptionJobLock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			WareService wareService = new WareService(dbOp);
			DbOperation slave2 = new DbOperation();
			slave2.init(DbOperation.DB_SLAVE2);
			
			String[] yesterdays = new String[]{"2014-11-18"};

			try {
				String today = DateUtil.getNowDateStr();
				String yesterday = DateUtil.getBackFromDate(today, 1);
				
				HashMap<String, Integer> mdcDeliverySkuMap = new HashMap<String, Integer>();
				wareService.getDbOp().executeUpdate("delete from order_send_out_count where type = 1");
				HashMap<Integer, String> mdcAreaMap = ProductStockBean.mdcAreaMap;
				HashMap<Integer, String> stockoutAvailableAreaMap = ProductStockBean.stockoutAvailableAreaMap;
				HashMap<Integer, String> unMdcAreaMap = new HashMap<Integer, String>();
				for (int key : stockoutAvailableAreaMap.keySet()) {
					if (mdcAreaMap.get(key) == null ) {
						unMdcAreaMap.put(key, stockoutAvailableAreaMap.get(key));
					}
				}
				String mdcAreas = "100";
				for (int area : mdcAreaMap.keySet()) {
					mdcAreas = mdcAreas + "," + area;
				}
				OrderStockService orderStockService = new OrderStockService();
				int beforecount = 0;
				ResultSet rs = slave2.executeQuery("select count(*) from order_send_out_count where create_date = '2014-11-18'");
				if (rs.next()) {
					beforecount = rs.getInt(1);
				}
				rs.close();
				StringBuffer sb = new StringBuffer();
				for(int i = 0; i < yesterdays.length; i++){
					sb.append(yesterdays[i]);
				}
				if (beforecount <= 0) {
					wareService.getDbOp().executeUpdate("delete from order_send_out_count where id >=112610808  and id <= 112789748");
					for (String beforeday : yesterdays) {
						orderStockService.orderSendOutCount(beforeday, mdcAreas, mdcDeliverySkuMap, unMdcAreaMap, slave2, wareService, sb.toString());
					}
				}
				
				orderStockService.orderSendOutCount(yesterday, mdcAreas, mdcDeliverySkuMap, unMdcAreaMap, slave2, wareService, sb.toString());

				System.out.println(DateUtil.getNow() + "应发货量定时任务结束");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(DateUtil.getNow() + "应发货量定时任务异常");
			} finally {
				wareService.releaseAll();
				slave2.release();
			}
		}
	}
}
