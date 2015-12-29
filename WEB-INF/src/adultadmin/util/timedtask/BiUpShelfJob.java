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
 * 统计上架作业环节作业量
 * @author mengqy
 *
 */
@Component
public class BiUpShelfJob implements Job {

	private static byte[] lock = new byte[0];

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		System.out.println(DateUtil.getNow() + " 定时任务统计上架作业环节作业量 开始执行");
		String today = DateUtil.getNowDateStr();
		String yesterday = DateUtil.getBackFromDate(today, 1);		
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB);
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);		
		TimedtaskCommonService tools = new TimedtaskCommonService();
		TimedtaskCommonService.BIOperTypeParam param = tools.new BIOperTypeParam();		
		param.setDatetime(yesterday);
		param.setOperType(EBIOperType.Type1.getIndex());
		param.setSaveCount(true);
		
		synchronized (lock) {		
			try {
				// 退货上架量
				StringBuffer sb = new StringBuffer();
				sb.append(" SELECT ci0.area_id AS areaId, SUM(c0.stock_count) AS operCount ");
				sb.append(" FROM cargo_operation_cargo AS c0, cargo_info AS ci0, cargo_operation_cargo AS c1, cargo_info AS ci1,cargo_operation cp");
				sb.append(" WHERE c0.in_cargo_whole_code = ci0.whole_code  ");
				sb.append(" AND c0.oper_id = c1.oper_id  ");
				sb.append(" AND c1.out_cargo_whole_code = ci1.whole_code  ");
				sb.append(" and c0.oper_id=cp.id");
				sb.append(" and cp.`status` = 8  ");
				sb.append(" and cp.complete_datetime BETWEEN '").append(yesterday).append(" 00:00:00' AND '").append(yesterday).append(" 23:59:59' ");
				sb.append(" AND c0.type = 0 AND ci0.stock_type = 0 "); // 合格库
				sb.append(" AND c1.type = 1 AND ci1.stock_type = 4 "); // 退货库 
				sb.append(" GROUP BY ci0.area_id  ");
				
				HashMap<Integer, Integer> map1 = tools.getCountBySql(dbSlave, sb.toString());
				param.setMap1(map1);
				sb.setLength(0);
				
				// 采购上架量
				sb.append(" SELECT ci0.area_id AS areaId, SUM(c0.stock_count) AS operCount ");
				sb.append(" FROM cargo_operation_cargo AS c0, cargo_info AS ci0, cargo_operation_cargo AS c1, cargo_info AS ci1 ");
				sb.append(" WHERE c0.in_cargo_whole_code = ci0.whole_code  ");
				sb.append(" AND c0.oper_id = c1.oper_id  ");
				sb.append(" AND c1.out_cargo_whole_code = ci1.whole_code  ");
				sb.append(" AND c0.oper_id IN ( ");
				sb.append("  SELECT id FROM cargo_operation  ");
				sb.append("  WHERE `status` = 8  ");
				sb.append("  AND complete_datetime BETWEEN '").append(yesterday).append(" 00:00:00' AND '").append(yesterday).append(" 23:59:59' ");
				sb.append(" ) ");
				sb.append(" AND c0.type = 0 AND ci0.stock_type = 0 "); // 合格库
				sb.append(" AND c1.type = 1 AND ci1.stock_type = 1 "); // 待验库 
				sb.append(" GROUP BY ci0.area_id  ");
				
				HashMap<Integer, Integer> map2 = tools.getCountBySql(dbSlave, sb.toString());
				param.setMap2(map2);
				
				db.startTransaction();				
				tools.insertOperType(db, param);												
				db.commitTransaction();
				
				System.out.println(DateUtil.getNow() + " 定时任务统计上架作业环节作业量 执行结束");		
			} catch (Exception e) {
				e.printStackTrace();
				db.rollbackTransaction();
				System.out.println(DateUtil.getNow() + " 定时任务统计上架作业环节作业量 发生异常");
			}
			finally{
				db.release();
				dbSlave.release();
			}			
		}
	}

}
