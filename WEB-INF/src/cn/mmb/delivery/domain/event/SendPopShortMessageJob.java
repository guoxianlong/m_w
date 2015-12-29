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
 * 发送大客户订单出库短信的定时任务
 * @author likaige
 * @create 2015年6月3日 上午10:27:53
 */
public class SendPopShortMessageJob implements Job {
	
	private static Log log = LogFactory.getLog("debug.Log");

	@Override
	public void execute(JobExecutionContext c) throws JobExecutionException {
		System.out.println("SendPopShortMessageJob start:" + DateUtil.getNow());
		try {
			DeliveryService s = SpringHandler.getBean(DeliveryService.class);
			s.sendPopShortMessage();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("定时任务发送大客户订单出库短信时出现异常：", e);
		}
		System.out.println("SendPopShortMessageJob end :" + DateUtil.getNow());
	}
}
