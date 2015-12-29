package adultadmin.util.timedtask;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

import mmb.bi.model.BiOrderFinishRate;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stock.StockAreaBean;
import adultadmin.util.Arith;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Component
public class BiOrderFinishRateJob implements Job {
	public static byte[] StockExceptionJobLock = new byte[0];

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		System.out.println(DateUtil.getNow() + "订单完成率定时任务开始");
		synchronized (StockExceptionJobLock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			WareService wareService = new WareService(dbOp);
			DbOperation slave2 = new DbOperation();
			slave2.init(DbOperation.DB_SLAVE2);

			try {
				wareService.getDbOp().startTransaction();// 事务开始
				BiOrderFinishRate biOrderFinishRate = new BiOrderFinishRate();
				String today = DateUtil.getNowDateStr();
				String yesterday = DateUtil.getBackFromDate(today, 1);
				String beforeYesterday = DateUtil.getBackFromDate(today, 2);
				biOrderFinishRate.setCreateDate(yesterday);

				StringBuffer sql = new StringBuffer();
				sql.append("select os.stock_area area, count(os.id) thecount ")
						.append(" from order_stock os ")
						.append(" where os.create_datetime >= '" + yesterday
								+ " 00:00:00' and os.create_datetime <= '"
								+ yesterday + " 23:59:59' and os.status<>"
								+ OrderStockBean.STATUS4
								+ " group by os.stock_area");
				ResultSet rs = slave2.executeQuery(sql.toString());
				HashMap<String, String> intradayOutOrderMap = new HashMap<String, String>();
				while (rs.next()) {
					intradayOutOrderMap.put(rs.getInt(1) + "", rs.getInt(2)
							+ "");
				}

				sql.setLength(0);
				sql.append("select os.stock_area area, count(os.id) thecount ")
						.append("from order_stock os,mailing_batch mb, mailing_batch_package mbp ")
						.append(" where os.order_id=mbp.order_id and mbp.mailing_batch_id=mb.id "
								+ "and os.create_datetime >= '"
								+ yesterday
								+ " 00:00:00' and os.create_datetime <= '"
								+ yesterday
								+ " 23:59:59' and os.status<>"
								+ OrderStockBean.STATUS4
								+ " "
								+ "and mb.transit_datetime >= '"
								+ yesterday
								+ " 00:00:00' and mb.transit_datetime <= '"
								+ yesterday
								+ " 23:59:59' group by os.stock_area");
				rs = slave2.executeQuery(sql.toString());
				HashMap<String, String> intradayDeliverOrderMap = new HashMap<String, String>();
				while (rs.next()) {
					intradayDeliverOrderMap.put(rs.getInt(1) + "", rs.getInt(2)
							+ "");
				}

				sql.setLength(0);
				sql.append("select os.stock_area area, count(os.id) thecount ")
						.append("from  order_stock os ")
						.append("where os.create_datetime >= '" + yesterday
								+ " 00:00:00' and os.create_datetime <= '"
								+ yesterday + " 17:00:00' and os.status<>"
								+ OrderStockBean.STATUS4
								+ " group by os.stock_area");
				rs = slave2.executeQuery(sql.toString());
				HashMap<String, String> intradayCutOffOrderMap = new HashMap<String, String>();
				while (rs.next()) {
					intradayCutOffOrderMap.put(rs.getInt(1) + "", rs.getInt(2)
							+ "");
				}

				sql.setLength(0);
				sql.append("select  os.stock_area area, count(os.id) thecount ")
					.append(" from  order_stock os,mailing_batch mb, mailing_batch_package mbp ")
					.append("where  os.order_code=mbp.order_code and mbp.mailing_batch_id=mb.id "
							+ "and os.last_oper_time > '"
							+ yesterday
							+ " 00:00:00' and os.last_oper_time <= '"
							+ yesterday
							+ " 23:59:59' and os.status in (2,4,6,7,8) "
							+ "group by os.stock_area");
				rs = slave2.executeQuery(sql.toString());
				HashMap<String, String> intradayRealDeliverOrderMap = new HashMap<String, String>();
				while (rs.next()) {
					intradayRealDeliverOrderMap.put(rs.getInt(1) + "",
							rs.getInt(2) + "");
				}
				
				sql.setLength(0);
				// 分开查询各个仓，成都仓统计时间段：前一天19:30-当天19:30，无锡仓统计时间段：前一天18:30-当天18:30，其他仓不变
				HashMap<String, String> cutOffOutOrderMap = new HashMap<String, String>();
				// 成都仓
				StringBuilder cdSql = new StringBuilder();
				cdSql.append("select  os.stock_area area, count(os.id) thecount ")
				.append(" from order_stock os ")
				.append("where os.stock_area=9 and os.create_datetime > '" + beforeYesterday
						+ " 19:30:00' and os.create_datetime <= '"
						+ yesterday + " 19:30:00' and os.status<>"
						+ OrderStockBean.STATUS4
						+ " group by os.stock_area");
				rs = slave2.executeQuery(cdSql.toString());
				while (rs.next()) {
					cutOffOutOrderMap.put(rs.getInt(1) + "", rs.getInt(2) + "");
				}
				// 无锡仓
				StringBuilder wxSql = new StringBuilder();
				wxSql.append("select  os.stock_area area, count(os.id) thecount ")
				.append(" from order_stock os ")
				.append("where os.stock_area=4 and os.create_datetime > '" + beforeYesterday
						+ " 18:30:00' and os.create_datetime <= '"
						+ yesterday + " 18:30:00' and os.status<>"
						+ OrderStockBean.STATUS4
						+ " group by os.stock_area");
				rs = slave2.executeQuery(wxSql.toString());
				while (rs.next()) {
					cutOffOutOrderMap.put(rs.getInt(1) + "", rs.getInt(2) + "");
				}
				// 如果有其他仓，其他仓
				sql.append("select  os.stock_area area, count(os.id) thecount ")
						.append(" from order_stock os ")
						.append("where os.stock_area<>4 and os.stock_area<>9 and os.create_datetime > '" + beforeYesterday
								+ " 17:00:00' and os.create_datetime <= '"
								+ yesterday + " 17:00:00' and os.status<>"
								+ OrderStockBean.STATUS4
								+ " group by os.stock_area");
				rs = slave2.executeQuery(sql.toString());
				while (rs.next()) {
					cutOffOutOrderMap.put(rs.getInt(1) + "", rs.getInt(2) + "");
				}
				rs.close();

				List<StockAreaBean> stockAreaList = wareService.getStockoutAvailableAreaList();

				for (StockAreaBean stockAreaBean : stockAreaList) {
					biOrderFinishRate.setStockArea(stockAreaBean.getId());
					// 当日申请出库订单量
					biOrderFinishRate.setIntradayOutOrderCount(StringUtil
							.StringToId(intradayOutOrderMap.get(stockAreaBean
									.getId() + "")));
					// 当日订单发货量
					biOrderFinishRate.setIntradayDeliverOrderCount(StringUtil
							.StringToId(intradayDeliverOrderMap.get(stockAreaBean
									.getId() + "")));

					// 当日订单完成率
					biOrderFinishRate.setIntradayCompletePercent(Arith.div(
							biOrderFinishRate.getIntradayDeliverOrderCount(),
							biOrderFinishRate.getIntradayOutOrderCount(), 2));

					// 0:00-截单时间申请出库单量
					biOrderFinishRate.setIntradayCutOffOrderCount(StringUtil
							.StringToId(intradayCutOffOrderMap.get(stockAreaBean
									.getId() + "")));

					// 每日截单单量占比
					biOrderFinishRate.setIntradayCutOffPercent(Arith.div(
							biOrderFinishRate.getIntradayCutOffOrderCount(),
							biOrderFinishRate.getIntradayOutOrderCount(), 2));

					// 订单当日实际发货数
					biOrderFinishRate
							.setIntradayRealDeliverOrderCount(StringUtil
									.StringToId(intradayRealDeliverOrderMap
											.get(stockAreaBean.getId() + "")));
					// 截单周期申请出库订单数
					biOrderFinishRate.setCutOffOutOrderCount(StringUtil
							.StringToId(cutOffOutOrderMap.get(stockAreaBean.getId()
									+ "")));

					// 截单周期订单完成率
					biOrderFinishRate.setCutOffCompletePercent(Arith.div(
							biOrderFinishRate
									.getIntradayRealDeliverOrderCount(),
							biOrderFinishRate.getCutOffOutOrderCount(), 2));
					;

					sql.setLength(0);
					sql.append("insert into bi_order_finish_rate(`create_date`, `stock_area`, `intraday_out_order_count`, `intraday_deliver_order_count`, `intraday_complete_percent`, `cut_off_out_order_count`, `intraday_real_deliver_order_count`, `cut_off_complete_percent`, `intraday_cut_off_order_count`, `intraday_cut_off_percent`) "
							+ "values ('"
							+ biOrderFinishRate.getCreateDate()
							+ "',"
							+ biOrderFinishRate.getStockArea()
							+ ","
							+ biOrderFinishRate.getIntradayOutOrderCount()
							+ ","
							+ biOrderFinishRate.getIntradayDeliverOrderCount()
							+ ","
							+ biOrderFinishRate.getIntradayCompletePercent()
							+ ","
							+ biOrderFinishRate.getCutOffOutOrderCount()
							+ ","
							+ biOrderFinishRate
									.getIntradayRealDeliverOrderCount()
							+ ","
							+ biOrderFinishRate.getCutOffCompletePercent()
							+ ","
							+ biOrderFinishRate.getIntradayCutOffOrderCount()
							+ ","
							+ biOrderFinishRate.getIntradayCutOffPercent()
							+ ")");
					if (!wareService.getDbOp().executeUpdate(sql.toString())) {
						System.out.println(DateUtil.getNow() + "订单完成率定时任务添加失败");
						System.out.println(DateUtil.getNow() + "订单完成率定时任务结束");
						wareService.getDbOp().rollbackTransaction();
						return;
					}
				}
				wareService.getDbOp().commitTransaction();
				System.out.println(DateUtil.getNow() + "订单完成率定时任务结束");
			} catch (Exception e) {
				e.printStackTrace();
				wareService.getDbOp().rollbackTransaction();
				System.out.println(DateUtil.getNow() + "订单完成率定时任务异常");
			} finally {
				wareService.releaseAll();
				slave2.release();
			}
		}
	}

}
