package adultadmin.util.timedtask;

import java.util.Iterator;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.bean.sms.OrderMessage3Bean;
import adultadmin.bean.sms.OrderMessage2Bean;
import adultadmin.bean.sms.SendMessage3Bean;
import adultadmin.bean.sms.ReceiveMessageBean;
import adultadmin.bean.sms.ReceiveMessageHistoryBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ISMSService;
import adultadmin.util.db.DbOperation;

//启动短信自动回复定时任务
public class SMSReplyJob implements Job  {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		DbOperation dbOp = new DbOperation();
		dbOp.init("sms");
		ISMSService smsService = ServiceFactory.createSMSService(IBaseService.CONN_IN_SERVICE, dbOp);
		try{

			//发货短信回复(sms3)
			List rmList = smsService.getReceiveMessage3List(null, -1, -1, "id asc");
			if(rmList != null && rmList.size() > 0){
				Iterator iter = rmList.listIterator();
				while(iter.hasNext()){
					ReceiveMessageBean receiveMessage = (ReceiveMessageBean)iter.next();
					SendMessage3Bean sendMessage3 = smsService.getSendMessage3("mobile = '"+receiveMessage.getMobile()+"' order by id desc limit 1");
					if(sendMessage3 != null){

						OrderMessage3Bean orderMessage3 = new OrderMessage3Bean();
						orderMessage3.setOrderId(sendMessage3.getOrderId());
						orderMessage3.setOrderCode(sendMessage3.getOrderCode());
						orderMessage3.setPackageNum(sendMessage3.getPackageNum());
						orderMessage3.setMobile(sendMessage3.getMobile());
						orderMessage3.setSendDatetime(sendMessage3.getSendDatetime());
						orderMessage3.setSendUserId(sendMessage3.getSendUserId());
						orderMessage3.setSendUsername(sendMessage3.getSendUsername());
						orderMessage3.setSendContent(sendMessage3.getContent());
						orderMessage3.setReceiveDatetime(receiveMessage.getAddtime());
						orderMessage3.setReceiveContent(receiveMessage.getContent());
						orderMessage3.setStatus(OrderMessage3Bean.STATUS0);

						smsService.addOrderMessage3(orderMessage3);
					}

					ReceiveMessageHistoryBean receiveMessageHistory = new ReceiveMessageHistoryBean();
					receiveMessageHistory.setAddtime(receiveMessage.getAddtime());
					receiveMessageHistory.setContent(receiveMessage.getContent());
					receiveMessageHistory.setLine(receiveMessage.getLine());
					receiveMessageHistory.setMobile(receiveMessage.getMobile());
					receiveMessageHistory.setStatus(receiveMessage.getStatus());

					smsService.addReceiveMessageHistory3(receiveMessageHistory);
					smsService.deleteReceiveMessage3("id = "+receiveMessage.getId());
				}
			}

			//群发短信回复(sms2)
			rmList = smsService.getReceiveMessage2List(null, -1, -1, "id asc");
			if(rmList != null && rmList.size() > 0){
				Iterator iter = rmList.listIterator();
				while(iter.hasNext()){
					ReceiveMessageBean receiveMessage = (ReceiveMessageBean)iter.next();

					OrderMessage2Bean orderMessage2 = new OrderMessage2Bean();
					orderMessage2.setMobile(receiveMessage.getMobile());
					orderMessage2.setReceiveDatetime(receiveMessage.getAddtime());
					orderMessage2.setReceiveContent(receiveMessage.getContent());
					orderMessage2.setStatus(OrderMessage2Bean.STATUS0);

					smsService.addOrderMessage2(orderMessage2);

					ReceiveMessageHistoryBean receiveMessageHistory = new ReceiveMessageHistoryBean();
					receiveMessageHistory.setAddtime(receiveMessage.getAddtime());
					receiveMessageHistory.setContent(receiveMessage.getContent());
					receiveMessageHistory.setLine(receiveMessage.getLine());
					receiveMessageHistory.setMobile(receiveMessage.getMobile());
					receiveMessageHistory.setStatus(receiveMessage.getStatus());

					smsService.addReceiveMessageHistory2(receiveMessageHistory);
					smsService.deleteReceiveMessage2("id = "+receiveMessage.getId());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			smsService.releaseAll();
		}

	}
}
