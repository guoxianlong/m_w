
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

import mmb.stock.stat.AreaStockExchangeService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**  
 * 此类描述的是：  自动获取跨区调拨列表
 * @author: liubo 
 * @version: 2013-2-21 上午10:27:52   
 */

public class AreaExchangeJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		AreaStockExchangeService areaService = new AreaStockExchangeService();
		areaService.createExchangeProductList();

	}

}
