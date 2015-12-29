package adultadmin.util.timedtask;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.bi.model.BiOrderInStockAging;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stock.StockAreaBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Component
public class BiOrderInStockAgingJob implements Job {
	public static byte[] StockExceptionJobLock = new byte[0];

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		System.out.println(DateUtil.getNow() + "在库时效订单定时任务开始");
		synchronized (StockExceptionJobLock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			WareService wareService = new WareService(dbOp);
			DbOperation slave2 = new DbOperation();
			slave2.init(DbOperation.DB_SLAVE2);

			try {
				wareService.getDbOp().startTransaction();// 事务开始
				BiOrderInStockAging biOrderInStockAging = new BiOrderInStockAging();
				String today = DateUtil.getNowDateStr();
				String yesterday = DateUtil.getBackFromDate(today, 1);
				biOrderInStockAging.setCreateDate(yesterday);

				HashMap<Integer, HashMap<String, String>> map = BiOrderInStockAging.inStockAgingTypeMap;

				List<StockAreaBean> stockAreaList = wareService
						.getStockoutAvailableAreaList();
				for (int key : map.keySet()) {
					StringBuffer sql = new StringBuffer();
					sql.append("select ");
					sql.append(" os.stock_area area, count(os.id) thecount ");
					sql.append(" from order_stock os,mailing_batch mb, mailing_batch_package mbp,sorting_batch_order sbo, sorting_batch sb ");
					sql.append(" where os.order_id=mbp.order_id and mb.code=mbp.mailing_batch_code and os.order_id=sbo.order_id and sb.id=sbo.sorting_batch_id and sbo.delete_status=0 ");
					sql.append(" and mb.transit_datetime >= '"
							+ yesterday
							+ " 00:00:00' and mb.transit_datetime <= '"
							+ yesterday
							+ " 23:59:59'  and os.status <> " + OrderStockBean.STATUS4 + " and timestampdiff(hour,sb.create_datetime, mb.transit_datetime) >= "
							+ map.get(key).get("start")
							+ " and timestampdiff(hour,sb.create_datetime, mb.transit_datetime) < "
							+ map.get(key).get("end"));
					sql.append(" group by os.stock_area ");
					ResultSet rs = slave2.executeQuery(sql.toString());
					Map<String, String> addMap = new HashMap<String, String>();
					while (rs.next()) {
						addMap.put(rs.getInt(1) + "" , rs.getInt(2) + "");
					}
					rs.close();
					for (StockAreaBean stockAreaBean : stockAreaList) {
						biOrderInStockAging.setInStockAgingType(key);
						biOrderInStockAging.setStockArea(stockAreaBean.getId());
						biOrderInStockAging.setOrderCount(StringUtil.StringToId(addMap.get(stockAreaBean.getId() + "")));
						String insertSql = "INSERT INTO `bi_order_in_stock_aging` ( `create_date`, `stock_area`, `in_stock_aging_type`, `order_count`) VALUES (" +
								"'" + biOrderInStockAging.getCreateDate() + "'," + biOrderInStockAging.getStockArea() + "," + biOrderInStockAging.getInStockAgingType() + ","  + biOrderInStockAging.getOrderCount() + ")";
						if (!wareService.getDbOp().executeUpdate(insertSql)) {
							wareService.getDbOp().rollbackTransaction();
							System.out.println(DateUtil.getNow() + "添加信息出错");
							System.out.println(DateUtil.getNow() + "在库时效订单定时任务结束");
							return ;
						}
					}
				}
				wareService.getDbOp().commitTransaction();
				System.out.println(DateUtil.getNow() + "在库时效订单定时任务结束");
			} catch (Exception e) {
				e.printStackTrace();
				wareService.getDbOp().rollbackTransaction();
				System.out.println(DateUtil.getNow() + "在库时效订单定时任务异常");
			} finally {
				wareService.releaseAll();
				slave2.release();
			}
		}
	}
}
