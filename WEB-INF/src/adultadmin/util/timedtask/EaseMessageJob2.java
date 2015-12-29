package adultadmin.util.timedtask;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.msg.TemplateMarker;
import mmb.stock.stat.DeliverCorpInfoBean;
import mmb.stock.stat.DeliverService;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.bean.sms.EaseMessage2Bean;
import adultadmin.bean.sms.SendMessage3Bean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ISMSService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

import com.chinasms.sms.SenderSMS3;

// 针对价格高的手机用户货到付款，需要及时提示用户备好钱来收货。主要针对手机线。是继原来针对手机发送“安心短信”之后在发的一条短信。
public class EaseMessageJob2 implements Job {
	public static byte[] messageLock = new byte[0];
	public void execute(JobExecutionContext context) throws JobExecutionException {
		synchronized(messageLock){
			DbOperation dbOpSlave = new DbOperation();
			dbOpSlave.init(DbOperation.DB_SLAVE);
			DbOperation dbOp = new DbOperation();
			WareService service = new WareService(dbOpSlave);
			DeliverService deliverService = new DeliverService(IBaseService.CONN_IN_SERVICE,dbOpSlave);
			dbOp.init(DbOperation.DB_SMS);
			ISMSService smsService = ServiceFactory.createSMSService(IBaseService.CONN_IN_SERVICE, dbOp);
			try {
				ResultSet rs = null;
				rs = service.getDbOp().executeQuery("select b.catalog_id from product_line a join product_line_catalog  b on a.id=b.product_line_id where a.name like '手机%'");
				List<String> catalogList = new ArrayList<String>();
				while (rs.next()) {
					catalogList.add(rs.getInt("b.catalog_id")+"");
				}
				rs.close();
				SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar cal1=Calendar.getInstance();
				Calendar cal2=Calendar.getInstance();
				cal1.add(Calendar.HOUR, -96);
				cal2.add(Calendar.HOUR, -72);
				String date1=df.format(cal1.getTime());//后退96小时的时间
				String date2=df.format(cal2.getTime());//后退72小时的时间
				//导入包裹单时的信息发送列表
				List<?> sendMessageList=smsService.getSendMessage3List("send_datetime >= '"+date1+"' and send_datetime <= '"+date2+"'", -1, -1, null);
				if(sendMessageList!=null){
					for(int i=0;i<sendMessageList.size();i++){
						SendMessage3Bean bean=(SendMessage3Bean)sendMessageList.get(i);
						int orderId=bean.getOrderId();//订单id
						voOrder order=service.getOrder(orderId);//订单
						if(order==null){
							System.out.println("安心短信定时任务，未找到订单，id:"+orderId);
							continue;
						}
						boolean canSend=false;//是否可以发送短信
						String productName = new String();
						String packageNum=order.getPackageNum();//包裹单号
						List<?> orderProductList = service.getOrderProducts(orderId);
						if ((order.getStatus() == 6 || order.getStatus() == 14)&& (!order.getAddress().startsWith("广东省") || order.getDeliver() == 13)) {// 状态为已发货或已妥投
							if (orderProductList != null) {
								for (int j = 0; j < orderProductList.size(); j++) {
									voOrderProduct vop = (voOrderProduct) orderProductList.get(j);
									if ((catalogList.contains(vop.getParentId1()+"")|| catalogList.contains(vop.getParentId2()+""))&&vop.getPrice()>=1500) {
										productName = vop.getName();
										canSend = true;
										break;
									}
								}
							}
						}
						if( order.isDaqOrder() || order.isAmazonOrder() || order.isTaobaoOrder() || order.isDangdangOrder() || order.isJdOrder() || order.isLTOrder() || order.isJdAdultOrder()) {
							canSend = false;
						}
						if(canSend){
							//安心短信列表
							List<?> easeMessageList=smsService.getEaseMessageList2("order_id="+orderId, -1, -1, null);
							if(easeMessageList==null||easeMessageList.size()==0){//安心短信列表无记录，应发送安心短信
								DeliverCorpInfoBean dciBean = deliverService.getDeliverCorpInfo("id="+order.getDeliver());
								Map<String,Object> paramMap = new HashMap<String, Object>();
								paramMap.put("deliverPhone", dciBean.getPhone());
								paramMap.put("productName", productName);
								paramMap.put("orderProductCount", orderProductList.size()>1?"等":"");
								paramMap.put("packageNum", packageNum);
								String templateName = TemplateMarker.EASE_MESSAGE_NAME2;
								TemplateMarker tm = TemplateMarker.getMarker();
								String content2 = tm.getOutString(templateName, paramMap);
								
								SenderSMS3.send(0, order.getPhone(), content2);//无user，userId用0代替
	
								EaseMessage2Bean emBean=new EaseMessage2Bean();
								emBean.setOrderId(orderId);
								emBean.setOrderCode(order.getCode());
								emBean.setPackageNum(packageNum);
								emBean.setMobile(order.getPhone());
								emBean.setSendDatetime(DateUtil.getNow());
								emBean.setContent(content2);
								smsService.addEaseMessage2(emBean);
							}
						}
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			} finally {
				smsService.releaseAll();
				service.releaseAll();
			}
		}
	}
}
