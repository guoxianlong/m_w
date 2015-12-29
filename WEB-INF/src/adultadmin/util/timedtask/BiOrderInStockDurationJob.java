package adultadmin.util.timedtask;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

import mmb.bi.model.BiOrderInStockDuration;
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
public class BiOrderInStockDurationJob implements Job {
	public static byte[] StockExceptionJobLock = new byte[0];

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		System.out.println(DateUtil.getNow() + "订单在库时长定时任务开始");
		synchronized (StockExceptionJobLock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			WareService wareService = new WareService(dbOp);
			DbOperation slave2 = new DbOperation();
			slave2.init(DbOperation.DB_SLAVE2);

			try {
				wareService.getDbOp().startTransaction();// 事务开始
				BiOrderInStockDuration biOrderInStockDuration = new BiOrderInStockDuration();
				String today = DateUtil.getNowDateStr();
				String yesterday = DateUtil.getBackFromDate(today, 1);
				biOrderInStockDuration.setCreateDate(yesterday);

				List<StockAreaBean> stockAreaList = wareService
						.getStockoutAvailableAreaList();
				HashMap<Integer, HashMap<String, String>> map = BiOrderInStockDuration.timeMap;
				for (int key : map.keySet()) {
					String startTime = yesterday + " "
							+ map.get(key).get("start") + ":00";
					String endTime = yesterday
							+ " "
							+ (map.get(key).get("end").equals("23:59") ? map
									.get(key).get("end") + ":59" : map.get(key)
									.get("end") + ":00");
					StringBuffer sql = new StringBuffer();
					sql.append(
							"select os.stock_area area, count(os.id) thecount ")
							.append(" from order_stock os,mailing_batch mb, mailing_batch_package mbp ")
							.append(" where os.order_id=mbp.order_id and mbp.mailing_batch_code=mb.code and mb.transit_datetime >= '"
									+ startTime
									+ "' and mb.transit_datetime < '"
									+ endTime
									+ "' " + " group by os.stock_area");
					ResultSet rs = slave2.executeQuery(sql.toString());
					HashMap<String, String> intradayRealDeliverOrderMap = new HashMap<String, String>();
					while (rs.next()) {
						intradayRealDeliverOrderMap.put(rs.getInt(1) + "",
								rs.getInt(2) + "");
					}

					sql.setLength(0);
					sql.append(
							"select os.stock_area area, count(os.id) thecount ")
							.append(" from order_stock os,mailing_batch mb, mailing_batch_package mbp ")
							.append(" where os.order_id=mbp.order_id and mbp.mailing_batch_code=mb.code and os.create_datetime >= '"
									+ yesterday
									+ " 00:00:00' and os.create_datetime <='"
									+ yesterday
									+ " 23:59:59' and os.status <> "
									+ OrderStockBean.STATUS4
									+ " and mb.transit_datetime >= '"
									+ startTime
									+ "' and mb.transit_datetime < '"
									+ endTime
									+ "' " + " group by os.stock_area");
					rs = slave2.executeQuery(sql.toString());
					HashMap<String, String> intradayDeliverOrderMap = new HashMap<String, String>();
					while (rs.next()) {
						intradayDeliverOrderMap.put(rs.getInt(1) + "",
								rs.getInt(2) + "");
					}

					sql.setLength(0);
					sql.append("select os.stock_area area, round(sum(TIMESTAMPDIFF(second, sb.create_datetime, mb.transit_datetime))/3600, 2) amount  ");
					sql.append(
							" from order_stock os,mailing_batch mb, mailing_batch_package mbp,sorting_batch_order sbo,sorting_batch sb  ")
							.append(" where os.order_id = sbo.order_id AND sbo.sorting_batch_id = sb.id and sbo.delete_status=0 and os.order_id=mbp.order_id and mbp.mailing_batch_code=mb.code and mb.transit_datetime >= '"
									+ startTime
									+ "' and mb.transit_datetime < '"
									+ endTime
									+ "' " + " group by os.stock_area");
					rs = slave2.executeQuery(sql.toString());
					HashMap<String, String> inStockDurationMap = new HashMap<String, String>();
					while (rs.next()) {
						inStockDurationMap.put(rs.getInt(1) + "",
								rs.getFloat(2) + "");
					}

					// 异常订单处理时间
					sql.setLength(0);
					sql.append("SELECT                                                 ");
					sql.append("	os.stock_area area,                                ");
					sql.append("	round(                                             ");
					sql.append("		sum(                                           ");
					sql.append("			TIMESTAMPDIFF(                             ");
					sql.append("				second,                                ");
					sql.append("				ap.first_complete_order_stock_datetime,");
					sql.append("				ap.last_complete_order_stock_datetime  ");
					sql.append("			)                                          ");
					sql.append("		) / 3600,                                        ");
					sql.append("		2                                              ");
					sql.append("	) amount                                           ");
					sql.append("FROM                                                   ");
					sql.append("	order_stock os,                                    ");
					sql.append("	audit_package ap,                                  ");
					sql.append("	mailing_batch mb,                                  ");
					sql.append("	mailing_batch_package mbp                          ");
					sql.append("WHERE                                                  ");
					sql.append("	os.order_id = ap.order_id                          ");
					sql.append("AND os.order_id = mbp.order_id                         ");
					sql.append("AND mbp.mailing_batch_code= mb.code and os.create_datetime >= '"
							+ yesterday
							+ " 00:00:00' and os.create_datetime <='"
							+ yesterday
							+ " 23:59:59' and os.status <> "
							+ OrderStockBean.STATUS4
							+ " and mb.transit_datetime >= '"
							+ startTime
							+ "' and mb.transit_datetime < '"
							+ endTime
							+ "' "
							+ " group by os.stock_area");
					rs = slave2.executeQuery(sql.toString());
					HashMap<String, String> exceptionDurationMap = new HashMap<String, String>();
					while (rs.next()) {
						exceptionDurationMap.put(rs.getInt(1) + "",
								rs.getFloat(2) + "");
					}
					// 分拣时长
					// 分拣结束/分播开始时间/第一次复核扫描ck单号时间-批次生成，
					sql.setLength(0);
					sql.append("SELECT                                                                ");
					sql.append("	ap.areano area,                                               ");
					sql.append("	round(                                                            ");
					sql.append("		sum(                                                          ");
					sql.append("			TIMESTAMPDIFF(                                            ");
					sql.append("				second,                                               ");
					sql.append("				sb1.create_datetime,                                   ");
					sql.append("				(                                                     ");
					sql.append("					CASE                                              ");
					sql.append("					WHEN sbg1.sorting_complete_datetime IS NULL THEN   ");
					sql.append("						(                                             ");
					sql.append("							CASE                                      ");
					sql.append("							WHEN sbg1.receive_datetime2 IS NULL THEN   ");
					sql.append("								ap.first_complete_order_stock_datetime");
					sql.append("							ELSE                                      ");
					sql.append("								sbg1.receive_datetime2                 ");
					sql.append("							END                                       ");
					sql.append("						)                                             ");
					sql.append("					ELSE                                              ");
					sql.append("						sbg1.sorting_complete_datetime                 ");
					sql.append("					END                                               ");
					sql.append("				)                                                     ");
					sql.append("			) / thecount                                                        ");
					sql.append("		) / 3600 ,                                                       ");
					sql.append("		2                                                             ");
					sql.append("	) amount                                                          ");
					sql.append("FROM        order_stock os1,                                                          ");
					sql.append("	sorting_batch_group sbg1,                                          ");
					sql.append("	sorting_batch sb1,                                                 ");
					sql.append("	sorting_batch_order sbo1,                                          ");
					sql.append("	audit_package ap,mailing_batch mb1, mailing_batch_package mbp1,     ");
					sql.append("  (SELECT sbgId, count(sbot.id) thecount FROM(select sbg.id sbgId  from ");
					sql.append("	order_stock os,sorting_batch_group sbg,                                          ");
					sql.append("	sorting_batch_order sbo,                                          ");
					sql.append("	mailing_batch mb, mailing_batch_package mbp      ");
					sql.append("WHERE                                                                 ");
					sql.append("	os.order_id = sbo.order_id                                        ");
					sql.append("AND sbg.id = sbo.sorting_group_id and sbo.delete_status=0                                    ");
					sql.append(" and os.order_id=mbp.order_id and mbp.mailing_batch_code=mb.code ");
					sql.append("and os.create_datetime >= '" + yesterday
							+ " 00:00:00' and os.create_datetime <='"
							+ yesterday + " 23:59:59' and os.status <> "
							+ OrderStockBean.STATUS4
							+ " and mb.transit_datetime >= '" + startTime
							+ "' and mb.transit_datetime < '" + endTime + "' group by sbg.id"
							+ ") orderGroup1 JOIN sorting_batch_order sbot ON orderGroup1.sbgId = sbot.sorting_group_id ");
					sql.append(" AND sbot.delete_status = 0 group by orderGroup1.sbgId ");
					sql.append(" ) orderGroup ");
					sql.append(" where	    os1.order_id = sbo1.order_id and     sbg1.id=orderGroup.sbgId                               ");
					sql.append("AND sbg1.id = sbo1.sorting_group_id and sbo1.delete_status=0                                    ");
					sql.append("and sbo1.sorting_batch_id = sb1.id                                      ");
					sql.append(" and sbo1.order_id=ap.order_id and os1.order_id=mbp1.order_id and mbp1.mailing_batch_code=mb1.code ");
					sql.append("and os1.create_datetime >= '" + yesterday
							+ " 00:00:00' and os1.create_datetime <='"
							+ yesterday + " 23:59:59' and os1.status <> "
							+ OrderStockBean.STATUS4
							+ " and mb1.transit_datetime >= '" + startTime
							+ "' and mb1.transit_datetime < '" + endTime + "' "
							+ " group by ap.areano ");
					rs = slave2.executeQuery(sql.toString());
					HashMap<String, String> sortingDurationMap = new HashMap<String, String>();
					while (rs.next()) {
						sortingDurationMap.put(rs.getInt(1) + "",
								rs.getFloat(2) + "");
					}

					// 分播时长
					// 分播结束时间/第一次复核扫描ck单号时间-分播开始时间/分拣结束时间/第一次复核扫描ck单号时间
					sql.setLength(0);
					sql.append("SELECT                                                                     ");
					sql.append("	ap.areano area,                                                    ");
					sql.append("	round(                                                                 ");
					sql.append("		sum(                                                               ");
					sql.append("			TIMESTAMPDIFF(                                                 ");
					sql.append("				second,                                                    ");
					sql.append("				(                                                          ");
					sql.append("					CASE                                                   ");
					sql.append("					WHEN sbg1.receive_datetime2 IS NULL THEN                ");
					sql.append("						(                                                  ");
					sql.append("							CASE                                           ");
					sql.append("							WHEN sbg1.sorting_complete_datetime IS NULL THEN");
					sql.append("								ap.first_complete_order_stock_datetime     ");
					sql.append("							ELSE                                           ");
					sql.append("								sbg1.sorting_complete_datetime              ");
					sql.append("							END                                            ");
					sql.append("						)                                                  ");
					sql.append("					ELSE                                                   ");
					sql.append("						sbg1.receive_datetime2                              ");
					sql.append("					END                                                    ");
					sql.append("				),                                                         ");
					sql.append("				(                                                          ");
					sql.append("					CASE                                                   ");
					sql.append("					WHEN sbg1.complete_datetime2 IS NULL THEN               ");
					sql.append("						ap.first_complete_order_stock_datetime             ");
					sql.append("					ELSE                                                   ");
					sql.append("						sbg1.complete_datetime2                             ");
					sql.append("					END                                                    ");
					sql.append("				)                                                          ");
					sql.append("			) /thecount                                                             ");
					sql.append("		) / 3600,                                                            ");
					sql.append("		2                                                                  ");
					sql.append("	) amount                                                               ");
					sql.append("FROM        order_stock os1,                                                          ");
					sql.append("	sorting_batch_group sbg1,                                          ");
					sql.append("	sorting_batch_order sbo1,                                          ");
					sql.append("	audit_package ap,mailing_batch mb1, mailing_batch_package mbp1,     ");
					sql.append("  (SELECT sbgId, count(sbot.id) thecount FROM(select sbg.id sbgId  from ");
					sql.append("	order_stock os,sorting_batch_group sbg,                                          ");
					sql.append("	sorting_batch_order sbo,                                          ");
					sql.append("	mailing_batch mb, mailing_batch_package mbp      ");
					sql.append("WHERE                                                                 ");
					sql.append("	os.order_id = sbo.order_id                                        ");
					sql.append("AND sbg.id = sbo.sorting_group_id and sbo.delete_status=0                                    ");
					sql.append(" and os.order_id=mbp.order_id and mbp.mailing_batch_code=mb.code ");
					sql.append("and os.create_datetime >= '" + yesterday
							+ " 00:00:00' and os.create_datetime <='"
							+ yesterday + " 23:59:59' and os.status <> "
							+ OrderStockBean.STATUS4
							+ " and mb.transit_datetime >= '" + startTime
							+ "' and mb.transit_datetime < '" + endTime + "' group by sbg.id"
							+ ") orderGroup1 JOIN sorting_batch_order sbot ON orderGroup1.sbgId = sbot.sorting_group_id ");
					sql.append(" AND sbot.delete_status = 0 group by orderGroup1.sbgId ");
					sql.append(" ) orderGroup ");
					sql.append(" where	    os1.order_id = sbo1.order_id and     sbg1.id=orderGroup.sbgId                               ");
					sql.append("AND sbg1.id = sbo1.sorting_group_id and sbo1.delete_status=0                                    ");
					sql.append(" and sbo1.order_id=ap.order_id and os1.order_id=mbp1.order_id and mbp1.mailing_batch_code=mb1.code ");
					sql.append("and os1.create_datetime >= '" + yesterday
							+ " 00:00:00' and os1.create_datetime <='"
							+ yesterday + " 23:59:59' and os1.status <> "
							+ OrderStockBean.STATUS4
							+ " and mb1.transit_datetime >= '" + startTime
							+ "' and mb1.transit_datetime < '" + endTime + "' "
							+ " group by ap.areano ");
					rs = slave2.executeQuery(sql.toString());
					HashMap<String, String> allocateDurationMap = new HashMap<String, String>();
					while (rs.next()) {
						allocateDurationMap.put(rs.getInt(1) + "",
								rs.getFloat(2) + "");
					}

					// 复核时长
					// 第一次打单时间 -分播结批时间/第一次复核ck单号时间
					sql.setLength(0);
					sql.append("SELECT                                                        ");
					sql.append("	os.stock_area area,                                       ");
					sql.append("	round(                                                    ");
					sql.append("		sum(                                                  ");
					sql.append("			TIMESTAMPDIFF(                                    ");
					sql.append("				second,                                       ");
					sql.append("				(                                             ");
					sql.append("					CASE                                      ");
					sql.append("					WHEN sbg.complete_datetime2 IS NULL THEN  ");
					sql.append("						ap.first_complete_order_stock_datetime");
					sql.append("					ELSE                                      ");
					sql.append("						sbg.complete_datetime2                ");
					sql.append("					END                                       ");
					sql.append("				),                                            ");
					sql.append("				ap.first_print_package_datetime               ");
					sql.append("			)                                                 ");
					sql.append("		) / 3600,                                               ");
					sql.append("		2                                                     ");
					sql.append("	) amount                                                  ");
					sql.append("FROM                                                          ");
					sql.append("	order_stock os,                                           ");
					sql.append("	sorting_batch_group sbg,                                  ");
					sql.append("	sorting_batch_order sbo,                                  ");
					sql.append("	audit_package ap,                                         ");
					sql.append("	mailing_batch mb,                                         ");
					sql.append("	mailing_batch_package mbp                                 ");
					sql.append("WHERE                                                         ");
					sql.append("	os.order_id = sbo.order_id        and sbo.delete_status=0                         ");
					sql.append("AND sbo.sorting_group_id = sbg.id                             ");
					sql.append("AND os.order_id = ap.order_id                                 ");
					sql.append("AND os.order_id = mbp.order_id                                ");
					sql.append("AND mbp.mailing_batch_code= mb.code and os.create_datetime >= '"
							+ yesterday
							+ " 00:00:00' and os.create_datetime <='"
							+ yesterday
							+ " 23:59:59' and os.status <> "
							+ OrderStockBean.STATUS4
							+ " and mb.transit_datetime >= '"
							+ startTime
							+ "' and mb.transit_datetime < '"
							+ endTime
							+ "' "
							+ " group by os.stock_area");
					rs = slave2.executeQuery(sql.toString());
					HashMap<String, String> reviewDurationMap = new HashMap<String, String>();
					while (rs.next()) {
						reviewDurationMap.put(rs.getInt(1) + "", rs.getFloat(2)
								+ "");
					}

					// 交接时长
					sql.setLength(0);
					sql.append("SELECT                                          ");
					sql.append("	os.stock_area area,                         ");
					sql.append("	round(                                      ");
					sql.append("		sum(                                    ");
					sql.append("			TIMESTAMPDIFF(                      ");
					sql.append("				second,                         ");
					sql.append("				ap.first_print_package_datetime,");
					sql.append("				mb.transit_datetime             ");
					sql.append("			)                                   ");
					sql.append("		) / 3600,                                 ");
					sql.append("		2                                       ");
					sql.append("	) amount                                    ");
					sql.append("FROM                                            ");
					sql.append("	order_stock os,                             ");
					sql.append("	audit_package ap,                           ");
					sql.append("	mailing_batch mb,                           ");
					sql.append("	mailing_batch_package mbp                   ");
					sql.append("WHERE                                           ");
					sql.append(" os.order_id = ap.order_id                   ");
					sql.append("AND os.order_id = mbp.order_id                  ");
					sql.append("AND mbp.mailing_batch_code= mb.code and os.create_datetime >= '"
							+ yesterday
							+ " 00:00:00' and os.create_datetime <='"
							+ yesterday
							+ " 23:59:59' and os.status <> "
							+ OrderStockBean.STATUS4
							+ " and mb.transit_datetime >= '"
							+ startTime
							+ "' and mb.transit_datetime < '"
							+ endTime
							+ "' "
							+ " group by os.stock_area");
					rs = slave2.executeQuery(sql.toString());
					HashMap<String, String> associateDurationMap = new HashMap<String, String>();
					while (rs.next()) {
						associateDurationMap.put(rs.getInt(1) + "",
								rs.getFloat(2) + "");
					}
					for (StockAreaBean stockAreaBean : stockAreaList) {
						biOrderInStockDuration.setStockArea(stockAreaBean
								.getId());
						biOrderInStockDuration.setBeginDateTime(startTime);
						biOrderInStockDuration.setEndDateTime(endTime);

						biOrderInStockDuration
								.setIntradayRealDeliverOrderCount(StringUtil.StringToId(intradayRealDeliverOrderMap
										.get(stockAreaBean.getId() + "")));
						biOrderInStockDuration
								.setIntradayDeliverOrderCount(StringUtil.StringToId(intradayDeliverOrderMap
										.get(stockAreaBean.getId() + "")));
						biOrderInStockDuration.setInStockDuration(StringUtil
								.toFloat(inStockDurationMap.get(stockAreaBean
										.getId() + "")));
						biOrderInStockDuration.setSortingDuration(Arith.add(
								StringUtil.toFloat(sortingDurationMap
										.get(stockAreaBean.getId() + "")),
								StringUtil.toFloat(exceptionDurationMap
										.get(stockAreaBean.getId() + ""))));
						biOrderInStockDuration.setAllocateDuration(StringUtil
								.toFloat(allocateDurationMap.get(stockAreaBean
										.getId() + "")));
						biOrderInStockDuration.setReviewDuration(Arith.sub(
								StringUtil.toFloat(reviewDurationMap
										.get(stockAreaBean.getId() + "")),
								StringUtil.toFloat(exceptionDurationMap
										.get(stockAreaBean.getId() + ""))));
						biOrderInStockDuration.setAssociateDuration(StringUtil
								.toFloat(associateDurationMap.get(stockAreaBean
										.getId() + "")));
						String insertSql = "insert into `bi_order_in_stock_duration` ( `create_date`, `begin_date_time`, `end_date_time`, `stock_area`, `intraday_real_deliver_order_count`, `intraday_deliver_order_count`, `in_stock_duration`, `sorting_duration`, `allocate_duration`, `review_duration`, `associate_duration`) VALUES ("
								+ "'"
								+ biOrderInStockDuration.getCreateDate()
								+ "','"
								+ biOrderInStockDuration.getBeginDateTime()
								+ "','"
								+ biOrderInStockDuration.getEndDateTime()
								+ "',"
								+ biOrderInStockDuration.getStockArea()
								+ ","
								+ biOrderInStockDuration
										.getIntradayRealDeliverOrderCount()
								+ ","
								+ biOrderInStockDuration
										.getIntradayDeliverOrderCount()
								+ ","
								+ biOrderInStockDuration.getInStockDuration()
								+ ","
								+ biOrderInStockDuration.getSortingDuration()
								+ ","
								+ biOrderInStockDuration.getAllocateDuration()
								+ ","
								+ biOrderInStockDuration.getReviewDuration()
								+ ","
								+ biOrderInStockDuration.getAssociateDuration()
								+ ")";
						if (!wareService.getDbOp().executeUpdate(insertSql)) {
							System.out.println(DateUtil.getNow()
									+ "订单在库时长定时任务添加失败");
							System.out.println(DateUtil.getNow()
									+ "订单在库时长定时任务结束");
							wareService.getDbOp().rollbackTransaction();
							return;
						}
					}
				}
				wareService.getDbOp().commitTransaction();
				System.out.println(DateUtil.getNow() + "订单在库时长定时任务结束");
			} catch (Exception e) {
				e.printStackTrace();
				wareService.getDbOp().rollbackTransaction();
				System.out.println(DateUtil.getNow() + "订单在库时长定时任务异常");
			} finally {
				wareService.releaseAll();
				slave2.release();
			}
		}
	}
}
