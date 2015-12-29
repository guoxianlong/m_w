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
 * 发送快递面单的定时任务
 * @author likaige
 * @create 2015年5月4日 上午10:07:18
 */
public class SendWaybillJob implements Job {
	
	private static Log log = LogFactory.getLog("debug.Log");

	@Override
	public void execute(JobExecutionContext c) throws JobExecutionException {
		System.out.println("SendWaybillJob start:" + DateUtil.getNow());
		DeliveryService s = SpringHandler.getBean(DeliveryService.class);
		try {
			//s.sendWaybill(); //发送MMB面单信息
		} catch (Exception e) {
			e.printStackTrace();
			log.error("定时任务发送MMB快递面单时出现异常：", e);
		}
		try {
			s.sendPopWaybill(); //发送POP面单信息
		} catch (Exception e) {
			e.printStackTrace();
			log.error("定时任务发送JD快递面单时出现异常：", e);
		}
		
		try {
			s.updateOrderStatusToFail();//当前时间减去订单创建时间>=1小时时将订单状态改为-1即发送失败
			s.markNoBuyOrderPopInfo();//标记发送失败没有采销表数据的pop_order_info状态为-3
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("SendWaybillJob end :" + DateUtil.getNow());
	}
}
