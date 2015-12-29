package adultadmin.util.timedtask;

import java.text.SimpleDateFormat;
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
import adultadmin.bean.sms.EaseMessageBean;
import adultadmin.bean.sms.SendMessage3Bean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ISMSService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

import com.chinasms.sms.SenderSMS3;

// 安心短信任务
public class EaseMessageJob implements Job {
	public static byte[] messageLock = new byte[0];
	public void execute(JobExecutionContext context) throws JobExecutionException {
		synchronized(messageLock){
			DbOperation dbOpSlave = new DbOperation();
			dbOpSlave.init(DbOperation.DB_SLAVE);
			WareService service = new WareService(dbOpSlave);
			DeliverService deliverService = new DeliverService(IBaseService.CONN_IN_SERVICE,dbOpSlave);
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB_SMS);
			ISMSService smsService = ServiceFactory.createSMSService(IBaseService.CONN_IN_SERVICE, dbOp);

			try {
				SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar cal1=Calendar.getInstance();
				Calendar cal2=Calendar.getInstance();
				cal1.add(Calendar.HOUR, -72);
				cal2.add(Calendar.HOUR, -48);
				String date1=df.format(cal1.getTime());//后退72小时的时间
				String date2=df.format(cal2.getTime());//后退48小时的时间(2011-11-21变更，原为24小时)
				//导入包裹单时的信息发送列表
				List sendMessageList=smsService.getSendMessage3List("send_datetime >= '"+date1+"' and send_datetime <= '"+date2+"'", -1, -1, null);
				for(int i=0;i<sendMessageList.size();i++){
					SendMessage3Bean bean=(SendMessage3Bean)sendMessageList.get(i);
					int orderId=bean.getOrderId();//订单id
					voOrder order=service.getOrder(orderId);//订单
					if(order==null){
						System.out.println("安心短信定时任务，未找到订单，id:"+orderId);
						continue;
					}
					boolean canSend=false;//是否可以发送短信
					String packageNum=order.getPackageNum();//包裹单号
					if((order.getStatus()==6||order.getStatus()==14)&&(!order.getAddress().startsWith("广东省")||order.getDeliver()==13)){//状态为已发货或已妥投
						canSend=true;
					}
					if ( order.isTaobaoOrder() || order.isLTOrder() ){
						canSend= false;
					}
					if(canSend){
						//安心短信列表
						List easeMessageList=smsService.getEaseMessageList("order_id="+orderId, -1, -1, null);
						if(easeMessageList!=null&&easeMessageList.size()==0){//安心短信列表无记录，应发送安心短信
							List orderProductList=service.getOrderProducts(orderId);//订单产品表
							String mostExpensiveProduct="";//最贵商品名
							float mostExpensivePrice=0;//最贵商品单价
							int mostExpensiveProductCatalog=0;//最贵商品分类
							
							for(int j=0;j<orderProductList.size();j++){
								voOrderProduct product=(voOrderProduct)orderProductList.get(j);
								float price=product.getPrice();
								if(price>mostExpensivePrice){//比当前记录的最贵商品单价要高
									mostExpensivePrice=price;
									mostExpensiveProduct=product.getName();
									mostExpensiveProductCatalog=product.getParentId1();
									
								}
							}
							if(mostExpensiveProduct.length() > 13){
								mostExpensiveProduct = mostExpensiveProduct.substring(0, 13);
							}

							String templateName=TemplateMarker.EASE_MESSAGE_NAME;
							Map<String,Object> paramMap = new HashMap<String, Object>();
							//判断如果是大q订单 直接发送 72小时的短信 按照原有逻辑发送
							String content2 = "";
							if( order.isDaqOrder() ) {
								DeliverCorpInfoBean dciBean = deliverService.getDeliverCorpInfo("id="+order.getDeliver());
								templateName=TemplateMarker.EASE_MESSAGE_DAQ_NAME;
								paramMap.put("deliverPhone", dciBean.getPhone());
								paramMap.put("productName",  mostExpensiveProduct);
								paramMap.put("orderProductCount", orderProductList.size()>1?"等":"");
								paramMap.put("packageNum", packageNum);
								TemplateMarker tm =TemplateMarker.getMarker();
								content2=tm.getOutString(templateName, paramMap);
								//大Q手机 要使用 65通道发送
								SenderSMS3.send(0, order.getPhone(), content2, 65);//无user，userId用0代替
							} else {
								if(mostExpensiveProductCatalog==690||mostExpensiveProductCatalog==94){
									templateName=TemplateMarker.EASE_MESSAGE_ADULT_NAME;
								} 
								paramMap.put("orderProductCount", orderProductList.size()>1?"等":"");
								paramMap.put("mostExpensiveProduct", mostExpensiveProduct);
								paramMap.put("orderCode", order.getCode());
								TemplateMarker tm =TemplateMarker.getMarker();
								content2=tm.getOutString(templateName, paramMap);
								SenderSMS3.send(0, order.getPhone(), content2);//无user，userId用0代替
							}
							//如果不是大q订单 才会发送 评价短信
							if( !order.isDaqOrder() && !order.isAmazonOrder() && !order.isTaobaoOrder() && !order.isDangdangOrder() && !order.isJdOrder() && !order.isLTOrder()  && !order.isJdAdultOrder()) {
								/**
								 * 发送有奖短信
								 */
								String templateName3=TemplateMarker.PRIZE_MESSAGE_NAME;
								Map<String,Object> paramMap3 = new HashMap<String,Object>();
								TemplateMarker tm3 =TemplateMarker.getMarker();
								String content3=tm3.getOutString(templateName3, paramMap3);
								SenderSMS3.send(0, order.getPhone(), content3);//无user，userId用0代替
							} 

							EaseMessageBean emBean=new EaseMessageBean();
							emBean.setOrderId(orderId);
							emBean.setOrderCode(order.getCode());
							emBean.setPackageNum(packageNum);
							emBean.setMobile(order.getPhone());
							emBean.setSendDatetime(DateUtil.getNow());
							emBean.setContent(content2);
							smsService.addEaseMessage(emBean);
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
