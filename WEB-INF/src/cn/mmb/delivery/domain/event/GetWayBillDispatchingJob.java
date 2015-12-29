/**  
 * @Description: 
 * @author 叶二鹏   
 * @date 2015年8月11日 上午10:49:32 
 * @version V1.0   
 */
package cn.mmb.delivery.domain.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.util.DateUtil;
import cn.mmb.delivery.application.WayBillApplication;
import com.mmb.framework.support.SpringHandler;

/** 
 * @ClassName: GetWayBillDispatchingJob 
 * @Description: TODO
 * @author: 叶二鹏
 * @date: 2015年8月11日 上午10:49:32  
 */
public class GetWayBillDispatchingJob implements Job {
	
	private static Log log = LogFactory.getLog("debug.Log");

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("getWayBillDispatchingJob start:" + DateUtil.getNow());
		try {
			WayBillApplication ytoWayBill = SpringHandler.getBean(WayBillApplication.class);
			ytoWayBill.getWayBillDispatching();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("定时任务更新运单信息出现异常：", e);
		}
		System.out.println("getWayBillDispatchingJob end :" + DateUtil.getNow());
	}

}
