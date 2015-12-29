package adultadmin.util.timedtask;

import java.util.HashMap;

import mmb.bi.model.EBIOperType;
import mmb.bi.service.TimedtaskCommonService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

/**
 * 统计分播作业环节作业量
 * @author mengqy
 *
 */
@Component
public class BiSorting2Job implements Job {

	private static byte[] lock = new byte[0];

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		System.out.println(DateUtil.getNow() + " 定时任务统计分播作业环节作业量 开始执行");
		String today = DateUtil.getNowDateStr();
		String yesterday = DateUtil.getBackFromDate(today, 1);		
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB);
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);		
		TimedtaskCommonService tools = new TimedtaskCommonService();
		TimedtaskCommonService.BIOperTypeParam param = tools.new BIOperTypeParam();		
		param.setDatetime(yesterday);
		param.setOperType(EBIOperType.Type4.getIndex());		
		param.setSaveCount(false);
		
		synchronized (lock) {		
			try {
				
				// 分播结批量
				StringBuffer sb = new StringBuffer();
				sb.append(" SELECT sbg.`storage` AS areaId, SUM(sbop.complete_count) as operCount ");
				sb.append(" FROM sorting_batch_group AS sbg, sorting_batch_order_product AS sbop ");
				sb.append(" WHERE sbg.id = sbop.sorting_batch_group_id ");
				sb.append(" AND sbg.complete_datetime2 BETWEEN '").append(yesterday).append(" 00:00:00' AND '").append(yesterday).append(" 23:59:59' ");				
				sb.append(" AND sbg.status2 = 2 ");
				sb.append(" GROUP BY sbg.storage ");
								
				HashMap<Integer, Integer> map1 = tools.getCountBySql(dbSlave, sb.toString());
				param.setMap1(map1);				 
				
				db.startTransaction();				
				tools.insertOperType(db, param);												
				db.commitTransaction();
				
				System.out.println(DateUtil.getNow() + " 定时任务统计分播作业环节作业量 执行结束");		
			} catch (Exception e) {
				e.printStackTrace();
				db.rollbackTransaction();
				System.out.println(DateUtil.getNow() + " 定时任务统计分播作业环节作业量 发生异常");
			}
			finally{
				db.release();
				dbSlave.release();
			}			
		}
	}

}
