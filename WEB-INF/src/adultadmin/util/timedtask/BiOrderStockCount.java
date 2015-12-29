package adultadmin.util.timedtask;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

import mmb.bi.service.TimedtaskCommonService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

/**
 * 统计整体效能(订单量)
 * @author mengqy
 *
 */
@Component
public class BiOrderStockCount implements Job {

	private static byte[] lock = new byte[0];

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		System.out.println(DateUtil.getNow() + " 定时任务统计整体效能(订单量) 开始执行");
		String today = DateUtil.getNowDateStr();
		String yesterday = DateUtil.getBackFromDate(today, 1);		
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB);
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);		
		TimedtaskCommonService tools = new TimedtaskCommonService();
		 
		
		synchronized (lock) {		
			try {
				
				// 当日实际发货量
				StringBuffer sb = new StringBuffer();
				sb.append(" SELECT mb.area AS areaId, COUNT(mbp.order_id) AS operCount ");
				sb.append(" FROM mailing_batch AS mb, mailing_batch_package AS mbp ");
				sb.append(" WHERE mb.id = mbp.mailing_batch_id ");
				sb.append(" AND mb.transit_datetime BETWEEN '").append(yesterday).append(" 00:00:00' AND '").append(yesterday).append(" 23:59:59' ");				
				sb.append(" AND mb.`status` = 1 ");
				sb.append(" GROUP BY mb.area ");
				
				HashMap<Integer, Integer> map = tools.getCountBySql(dbSlave, sb.toString());
						 
				
				if (map != null && map.size() > 0) {
					db.startTransaction();
					
					sb.setLength(0);
					sb.append(" INSERT INTO bi_order_count  ");
					sb.append(" (area_id, datetime, create_time, order_count) ");
					sb.append(" VALUES ( ?, ?, ?, ? ) ");				

					for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
						
						db.prepareStatement(sb.toString());	
						PreparedStatement ps = db.getPStmt();
						ps.setInt(1, entry.getKey());
						ps.setString(2, yesterday);
						ps.setString(3, DateUtil.getNow());
						ps.setInt(4, entry.getValue());
						
						ps.executeUpdate();
					}

					db.commitTransaction();
				}						
				
				System.out.println(DateUtil.getNow() + " 定时任务统计整体效能(订单量) 执行结束");		
			} catch (Exception e) {
				e.printStackTrace();
				db.rollbackTransaction();
				System.out.println(DateUtil.getNow() + " 定时任务统计整体效能(订单量) 发生异常");
			}
			finally{
				db.release();
				dbSlave.release();
			}			
		}
	}

}
