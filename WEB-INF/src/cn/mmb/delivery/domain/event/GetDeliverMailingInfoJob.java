package cn.mmb.delivery.domain.event;

import java.util.List;

import mmb.stock.stat.DeliverCorpInfoBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.util.DateUtil;
import cn.mmb.delivery.domain.model.WayBill;
import cn.mmb.delivery.domain.service.WayBillService;
import cn.mmb.delivery.domain.service.WayBillServiceFactory;

import com.mmb.framework.support.SpringHandler;

/**
 * 
* @Description: 获取快递公司运单号个性化信息 定时任务
* @author ahc
*
 */
public class GetDeliverMailingInfoJob implements Job {
	
	private static Log log = LogFactory.getLog("debug.Log");
	@Override
	public void execute(JobExecutionContext c) throws JobExecutionException {
	System.out.println("定时任务GetDeliverMailingInfoJob开始:" + DateUtil.getNow());
		
		/**
		 * 新的“获取快递公司面单信息”定时任务
		 */
		int delivers[] = {DeliverCorpInfoBean.DELIVER_ID_YD,DeliverCorpInfoBean.DELIVER_ID_YT_WX,DeliverCorpInfoBean.DELIVER_ID_YT_CD,DeliverCorpInfoBean.DELIVER_ID_JD_WX, DeliverCorpInfoBean.DELIVER_ID_JD_CD,DeliverCorpInfoBean.DELIVER_ID_RFD_SD};
		WayBillServiceFactory serviceFactory =SpringHandler.getBean(WayBillServiceFactory.class);
		WayBillService wayBillService =null;
		for(int deliverId :delivers){
			try {
				wayBillService = serviceFactory.create(deliverId);
				//获取待发送信息
				List<WayBill> sendData = wayBillService.getDeliverRelation(deliverId);
				
				if(!sendData.isEmpty()){
					//发送并接收报文
					List<String> resultList = wayBillService.sendWayBillInfo(sendData);
					
					//封装报文
					List<WayBill> list = wayBillService.parseToWayBill(sendData, resultList);
					
					//保存并且更新信息
					wayBillService.updateDeliverRelation(deliverId, list);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("定时任务GetDeliverMailingInfoJob异常：",e);
			}
			
		}
		System.out.println("定时任务GetDeliverMailingInfoJob结束:" + DateUtil.getNow());
	}
}
