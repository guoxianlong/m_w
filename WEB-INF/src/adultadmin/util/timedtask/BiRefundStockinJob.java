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
 * 统计退货入库作业环节作业量
 * @author mengqy
 *
 */
@Component
public class BiRefundStockinJob implements Job {

	private static byte[] lock = new byte[0];

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		System.out.println(DateUtil.getNow() + " 定时任务统计退货入库作业环节作业量 开始执行");
		String today = DateUtil.getNowDateStr();
		String yesterday = DateUtil.getBackFromDate(today, 1);		
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB);
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);		
		TimedtaskCommonService tools = new TimedtaskCommonService();
		TimedtaskCommonService.BIOperTypeParam param = tools.new BIOperTypeParam();		
		param.setDatetime(yesterday);
		param.setOperType(EBIOperType.Type2.getIndex());
		param.setSaveCount(true);
		
		synchronized (lock) {		
			try {
				// 调拨入库量
				StringBuffer sb = new StringBuffer();
				sb.append(" SELECT e.stock_in_area AS areaId, SUM(p.stock_in_count) AS operCount ");
				sb.append(" FROM stock_exchange AS e, stock_exchange_product AS p ");
				sb.append(" WHERE e.id = p.stock_exchange_id  ");
				sb.append(" AND e.confirm_datetime BETWEEN '").append(yesterday).append(" 00:00:00' AND '").append(yesterday).append(" 23:59:59' ");
				sb.append(" AND e.stock_in_type = 4 AND e.`status` = 7  ");
				sb.append(" GROUP BY e.stock_in_area ");
				
				HashMap<Integer, Integer> map1 = tools.getCountBySql(dbSlave, sb.toString());
				param.setMap1(map1);
				sb.setLength(0);
				
				// 销售退货量
				sb.append(" SELECT area_id AS areaId, SUM(count) AS operCount ");
				sb.append(" FROM bi_sale_refund_count  ");
				sb.append(" WHERE datetime BETWEEN '").append(yesterday).append(" 00:00:00' AND '").append(yesterday).append(" 23:59:59' ");
				sb.append(" GROUP BY area_id  ");
				
				HashMap<Integer, Integer> map2 = tools.getCountBySql(dbSlave, sb.toString());
				param.setMap2(map2);
				
				db.startTransaction();
				
				tools.insertOperType(db, param);								
				
				db.commitTransaction();
				System.out.println(DateUtil.getNow() + " 定时任务统计退货入库作业环节作业量 执行结束");		
			} catch (Exception e) {
				e.printStackTrace();
				db.rollbackTransaction();
				System.out.println(DateUtil.getNow() + " 定时任务统计退货入库作业环节作业量 发生异常");
			}
			finally{
				db.release();
				dbSlave.release();
			}			
		}
	}

}
