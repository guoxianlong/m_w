package adultadmin.util.timedtask;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class OrderDeliverElspsedTimeJob implements Job {
	public static byte[] OrderReceiveDatetimeJobLock = new byte[0];

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		System.out.println(DateUtil.getNow() + "记录订单签收时间任务开始");
		synchronized (OrderReceiveDatetimeJobLock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			WareService wareService = new WareService(dbOp);
			DbOperation slave2 = new DbOperation();
			slave2.init(DbOperation.DB_SLAVE2);
			WareService slave2Service = new WareService(slave2);

			String now = DateUtil.getNowDateStr();

			String yesterday = DateUtil.getBackFromDate(now, 1);

			String beginDate = DateUtil.getBackFromDate(now, 61);

			String endDate = DateUtil.getBackFromDate(now, 2);
			try {
				wareService.getDbOp().startTransaction();// 事务开始

				StringBuffer querySql = new StringBuffer();
				querySql.append("SELECT ");
				querySql.append("	ap.areano, ");
				querySql.append("	uoei.add_id1, ");
				querySql.append("	uoei.add_id2, ");
				querySql.append("	uoei.add_id3, ");
				querySql.append("	round( ");
				querySql.append("		sum( ");
				querySql.append("			TIMESTAMPDIFF( ");
				querySql.append("				MINUTE, ");
				querySql.append("				mb.transit_datetime, ");
				querySql.append("				ap.receive_datetime ");
				querySql.append("			) ");
				querySql.append("		) / 60, ");
				querySql.append("		2 ");
				querySql.append("	) amount,count(ap.id) thecount ");
				querySql.append("FROM ");
				querySql.append("	audit_package ap ");
				querySql.append("JOIN mailing_batch_package mbp ON ap.order_code=mbp.order_code ");
				querySql.append("JOIN mailing_batch mb ON mbp.mailing_batch_code = mb.code ");
				querySql.append("JOIN user_order_extend_info uoei ON ap.order_code = uoei.order_code ");
				querySql.append("WHERE ");
				querySql.append("	ap.receive_datetime >='");
				querySql.append(beginDate)
						.append(" 00:00:00' and ap.receive_datetime <='")
						.append(endDate).append(" 23:59:59' ");
				querySql.append("GROUP BY ");
				querySql.append("	ap.areano, ");
				querySql.append("	uoei.add_id1, ");
				querySql.append("	uoei.add_id2, ");
				querySql.append("	uoei.add_id3 ");

				ResultSet rs = slave2Service.getDbOp().executeQuery(
						querySql.toString());

				List<HashMap> list = new ArrayList<HashMap>();
				HashMap tempMap = null;

				while (rs.next()) {
					tempMap = new HashMap();
					tempMap.put("area", rs.getInt(1));
					tempMap.put("add_id1", rs.getInt(2));
					tempMap.put("add_id2", rs.getInt(3));
					tempMap.put("add_id3", rs.getInt(4));
					tempMap.put("amount", rs.getFloat(5));
					tempMap.put("orderCount", rs.getInt(6));
					list.add(tempMap);
				}
				rs.close();

				int size = list.size();

				for (int i = 0; i < size; i++) {
					tempMap = list.get(i);
					if (!wareService
							.getDbOp()
							.executeUpdate(
									"insert into order_deliver_elspsed_time(create_date,add_id1,add_id2,add_id3,stock_area,deliver_elspsed_time,order_count) values ("
											+ "'" + yesterday + "' ,"+ tempMap.get("add_id1") + "," + tempMap.get("add_id2") + "," + tempMap.get("add_id3") + "," + tempMap.get("area") + "," + tempMap.get("amount") + "," + tempMap.get("orderCount") + ")")) {
						wareService.getDbOp().rollbackTransaction();
						System.out.println(DateUtil.getNow() + "记录订单签收时间任务异常");
						return;
					}
				}

				wareService.getDbOp().commitTransaction();// 事务提交
				System.out.println(DateUtil.getNow() + "记录订单签收时间任务结束");
			} catch (Exception e) {
				e.printStackTrace();
				wareService.getDbOp().rollbackTransaction();
				System.out.println(DateUtil.getNow() + "记录订单签收时间任务异常");
			} finally {
				wareService.releaseAll();
				slave2Service.releaseAll();
			}
		}
	}
}
