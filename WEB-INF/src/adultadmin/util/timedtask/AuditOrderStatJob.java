package adultadmin.util.timedtask;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.util.db.DbOperation;

/**
 * 复合单量统计
 * @author 李宁
 * @date 2014-4-4
 */
public class AuditOrderStatJob implements Job{
	public static byte[] auditOrderStatLock = new byte[0];
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("复合单量统计定时任务开始");
		synchronized (auditOrderStatLock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult");
			WareService wareService = new WareService(dbOp);
			PreparedStatement pst = null;
			try{
				//统计定时任务前一天的数据
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal=Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.DAY_OF_YEAR, -1);
				String statDate = sdf.format(cal.getTime());//统计日期
				String startTime = statDate + " 00:00:00";
				String endTime = statDate + " 23:59:59";
				StringBuilder statSql = new StringBuilder();
				statSql.append("select deliver,areano,count(DISTINCT(order_id)) amount from audit_package ")
				.append(" where check_datetime between '").append(startTime).append("' and '").append(endTime)
				.append("' group by deliver,areano");
				
				wareService.getDbOp().startTransaction();
				ResultSet rs = dbOp.executeQuery(statSql.toString());
				StringBuilder insertSql = new StringBuilder();
				insertSql.append(" insert into audit_order_stat ");
				insertSql.append(" (deliver_id,area_id,date,audit_count) ");
				insertSql.append(" values (?,?,?,?) ");
				pst = dbOp.getConn().prepareStatement(insertSql.toString());
				int count = 0;
				while(rs!=null && rs.next()){
					pst.setInt(1, rs.getInt("deliver"));
					pst.setInt(2, rs.getInt("areano"));
					pst.setDate(3, new java.sql.Date(cal.getTime().getTime()));
					pst.setInt(4, rs.getInt("amount"));
					pst.addBatch();
					count++;
					if(count%1000 == 0 || rs.isLast()){
						pst.executeBatch();
					}
				}
				wareService.getDbOp().commitTransaction();
			}catch (Exception e) {
				e.printStackTrace();
				wareService.getDbOp().rollbackTransaction();
			}finally{
				wareService.releaseAll();
			}
			System.out.println("复合单量统计定时任务结束");
		}
	}

}
