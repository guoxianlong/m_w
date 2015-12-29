package cn.mmb.delivery.domain.event;

import mmb.delivery.service.DeliveryService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.util.DateUtil;

import com.mmb.framework.support.SpringHandler;

/**
 * 修补POP订单信息的定时任务
 * @author likaige
 * @create 2015年6月6日 下午1:04:44
 */
public class RepairPopOrderInfoJob implements Job {
	
	private static Log log = LogFactory.getLog("debug.Log");

	@Override
	public void execute(JobExecutionContext c) throws JobExecutionException {
		System.out.println("RepairPopOrderInfoJob start:" + DateUtil.getNow());
		try {
			DeliveryService s = SpringHandler.getBean(DeliveryService.class);
			s.repairPopOrderInfo();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("定时任务修补POP订单信息时出现异常：", e);
		}
		System.out.println("RepairPopOrderInfoJob end :" + DateUtil.getNow());
	}
}
