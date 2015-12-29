package cn.mmb.delivery.domain.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mmb.framework.support.SpringHandler;

import adultadmin.util.DateUtil;
import mmb.delivery.service.DeliveryService;

/**
 * POP订单监控的定时任务
 * @author likaige
 * @create 2015年7月28日 下午4:40:34
 */
public class PopOrderMonitorJob implements Job {
	
	private static Log log = LogFactory.getLog("debug.Log");

	@Override
	public void execute(JobExecutionContext c) throws JobExecutionException {
		System.out.println("PopOrderMonitorJob start:" + DateUtil.getNow());
		try {
			DeliveryService s = SpringHandler.getBean(DeliveryService.class);
			s.popOrderMonitor();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("定时任务POP订单监控时出现异常：", e);
		}
		System.out.println("PopOrderMonitorJob end:" + DateUtil.getNow());
	}
}
