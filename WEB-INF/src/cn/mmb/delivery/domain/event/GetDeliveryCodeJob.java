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
 * 获取运单号的定时任务
 * @author likaige
 * @create 2015年4月28日 下午1:41:10
 */
public class GetDeliveryCodeJob implements Job {
	
	private static Log log = LogFactory.getLog("debug.Log");

	@Override
	public void execute(JobExecutionContext c) throws JobExecutionException {
		System.out.println("GetDeliveryCodeJob start:" + DateUtil.getNow());
		try {
			DeliveryService s = SpringHandler.getBean(DeliveryService.class);
			s.getDeliveryCode();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("定时任务获取运单号时出现异常：", e);
		}
		System.out.println("GetDeliveryCodeJob end :" + DateUtil.getNow());
	}
}
