package adultadmin.util.timedtask;

import java.util.Map;

import mmb.msg.TemplateMarker;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.action.vo.voOrder;
import adultadmin.util.db.DbOperation;

import com.chinasms.sms.SenderSMS3;

// 包裹单预警短信任务
public class DeliverWarningJob implements Job {
	public static byte[] messageLock = new byte[0];

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		synchronized (messageLock) {
			DbOperation dbOpSlave = new DbOperation();
			dbOpSlave.init(DbOperation.DB_SLAVE);
			WareService service = new WareService(dbOpSlave);

			try {
				Map<String,Object> paramMap =voOrder.deliverMapAll;
				int count =service.getDeliverPackageCodeCount(9);//获取广东省包裹单剩余量
				int count2 =service.getDeliverPackageCodeCount(44);//获取辽宁省包裹单剩余量
				int count3 =service.getDeliverPackageCodeCount(10);//获取宅急送包裹单剩余量
				int count4 =service.getDeliverPackageCodeCount(34);//获取陕西邮政包裹单剩余量
				int count5 =service.getDeliverPackageCodeCount(37);//获取无锡邮政包裹单剩余量
				int count6 =service.getDeliverPackageCodeCount(31);//获取湖南邮政包裹单剩余量
				int count7 =service.getDeliverPackageCodeCount(25);//获取广西邮政包裹单剩余量
				int count8 =service.getDeliverPackageCodeCount(32);//获取湖北邮政包裹单剩余量
				int count9 =service.getDeliverPackageCodeCount(35);//获取贵州邮政包裹单剩余量
				if (count <= 5000) {
					String templateName = TemplateMarker.DELIVER_WARNING_NAME;				
					paramMap.put("corpName",paramMap.get("9"));
					paramMap.put("count","5000");
					TemplateMarker tm = TemplateMarker.getMarker();
					String content = tm.getOutString(templateName, paramMap);
					if(!content.equals("")){
						sendMessage(content);//发送短信
					}
				}				
				if (count2 <= 400) {
					String templateName = TemplateMarker.DELIVER_WARNING_NAME;
					paramMap.put("corpName",paramMap.get("44"));
					paramMap.put("count","400");
					TemplateMarker tm = TemplateMarker.getMarker();
					String content = tm.getOutString(templateName, paramMap);
					if(!content.equals("")){
						sendMessage(content);//发送短信
					}
				}
				if (count3 <= 500) {
					String templateName = TemplateMarker.DELIVER_WARNING_NAME;
					paramMap.put("corpName",paramMap.get("10"));
					paramMap.put("count","500");
					TemplateMarker tm = TemplateMarker.getMarker();
					String content = tm.getOutString(templateName, paramMap);
					if(!content.equals("")){
						sendMessage(content);//发送短信
					}
					
					
				}
				if (count4 <= 3000) {
					String templateName = TemplateMarker.DELIVER_WARNING_NAME;
					paramMap.put("corpName",paramMap.get("34"));
					paramMap.put("count","3000");
					TemplateMarker tm = TemplateMarker.getMarker();
					String content = tm.getOutString(templateName, paramMap);
					if(!content.equals("")){
						sendMessage(content);//发送短信
					}
					
				}
				if (count5 <= 3000) {
					String templateName = TemplateMarker.DELIVER_WARNING_NAME;
					paramMap.put("corpName",paramMap.get("37"));
					paramMap.put("count","3000");
					TemplateMarker tm = TemplateMarker.getMarker();
					String content = tm.getOutString(templateName, paramMap);
					if(!content.equals("")){
						sendMessage(content);//发送短信
					}
					
				}
				if (count6 <= 3000) {
					String templateName = TemplateMarker.DELIVER_WARNING_NAME;
					paramMap.put("corpName",paramMap.get("31"));
					paramMap.put("count","3000");
					TemplateMarker tm = TemplateMarker.getMarker();
					String content = tm.getOutString(templateName, paramMap);
					if(!content.equals("")){
						sendMessage(content);//发送短信
					}
					
				}
				if (count7 <= 3000) {
					String templateName = TemplateMarker.DELIVER_WARNING_NAME;
					paramMap.put("corpName",paramMap.get("25"));
					paramMap.put("count","3000");
					TemplateMarker tm = TemplateMarker.getMarker();
					String content = tm.getOutString(templateName, paramMap);
					if(!content.equals("")){
						sendMessage(content);//发送短信
					}
					
				}
				if (count8 <= 3000) {
					String templateName = TemplateMarker.DELIVER_WARNING_NAME;
					paramMap.put("corpName",paramMap.get("32"));
					paramMap.put("count","3000");
					TemplateMarker tm = TemplateMarker.getMarker();
					String content = tm.getOutString(templateName, paramMap);
					if(!content.equals("")){
						sendMessage(content);//发送短信
					}
					
				}
				if (count9 <= 3000) {
					String templateName = TemplateMarker.DELIVER_WARNING_NAME;
					paramMap.put("corpName",paramMap.get("35"));
					paramMap.put("count","3000");
					TemplateMarker tm = TemplateMarker.getMarker();
					String content = tm.getOutString(templateName, paramMap);
					if(!content.equals("")){
						sendMessage(content);//发送短信
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}
	}
	
	/**
	 * 发送短信
	 * @param content
	 */
	public void sendMessage(String content){
		//String phone1 = "15210862564";//wxj 未收到过短信，撤销发送
		String phone2 = "13488778745";//zl
		String phone3 = "15201060982";//lk
		String phone4 = "18551038928";//朱宗良
		String phone5 = "18115759769";//黎昌东
		String phone6 = "18620931942";//王明磊
		String phone7 = "18102278486";//张瀚宇
		//SenderSMS3.sendDeliverWarning(0, phone1, content);// 无user，userId用0代替
		SenderSMS3.sendDeliverWarning(0, phone2, content);// 无user，userId用0代替
		SenderSMS3.sendDeliverWarning(0, phone3, content);// 无user，userId用0代替
		SenderSMS3.sendDeliverWarning(0, phone4, content);// 无user，userId用0代替
		SenderSMS3.sendDeliverWarning(0, phone5, content);// 无user，userId用0代替
		SenderSMS3.sendDeliverWarning(0, phone6, content);// 无user，userId用0代替
		SenderSMS3.sendDeliverWarning(0, phone7, content);// 无user，userId用0代替
	}
}
