
    /**  
     * 文件名：AreaExchangeJob.java  
     *  
     * 版本信息：  
     * 日期：2013-2-21  
     * Copyright 买卖宝 Corporation 2013   
     * 版权所有  
     *  
     */  
    
package adultadmin.util.timedtask;

import mmb.rec.stat.service.StatServiceImpl;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.util.db.DbOperation;

/**  
 * 说明：分拣统计
 * @author syuf
 */

public class SortingStatJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("分拣统计定时任务开始...");
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		try {
			StatServiceImpl statService = new StatServiceImpl(dbOp);
			statService.sortingStat();
			System.out.println("分拣统计定时任务结束...");
		} catch (Exception e) {
			System.out.println("分拣统计定时任务异常...");
			e.printStackTrace();
		} finally{
			dbOp.release();
		}
	}

}
