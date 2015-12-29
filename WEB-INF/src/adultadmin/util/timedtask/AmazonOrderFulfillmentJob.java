package adultadmin.util.timedtask;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.action.vo.voOrder;
import adultadmin.service.impl.StockServiceImpl;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class AmazonOrderFulfillmentJob implements Job {
	public static byte[] lock = new byte[0];
    //亚马逊订单配送确认上传数定时任务
	public void execute(JobExecutionContext context) throws JobExecutionException {
		synchronized (lock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult");
			WareService service = new WareService(dbOp);
			try {
				System.out.println("亚马逊订单配送确认上传数定时任务开始");
				String query = "SELECT uop.pop_order_id,aom.order_id,aom.order_stock_id,ap.package_code,ap.order_code FROM amazon_order_message aom  "
						+ " LEFT JOIN  audit_package ap ON aom.order_id=ap.order_id "
						+ " LEFT JOIN  user_order_pop uop ON aom.order_id=uop.order_id "
						+ " WHERE aom.status=0 and send_count<5 order by aom.id desc limit 200";
				ResultSet rs = service.getDbOp().executeQuery(query);
				List<Integer> orderIdList = new ArrayList<Integer>();
				String xml = "";
				int num=1;
				while (rs.next()) {
					voOrder orderBean = service.getOrder(rs.getInt("aom.order_id"));
					Calendar c = Calendar.getInstance();
					int zoneOffset = c.get(java.util.Calendar.ZONE_OFFSET);  
				    int dstOffset = c.get(java.util.Calendar.DST_OFFSET);  
				    c.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
					Date d = c.getTime();
					//转换为格林尼治时间格式
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					String dateString = formatter.format(d);
					if(orderBean!=null){
						xml += "<Message>";
						xml += "<MessageID>"+num+"</MessageID>";
						xml += "<OrderFulfillment>";
						xml += "<AmazonOrderID>"+rs.getString("uop.pop_order_id")+"</AmazonOrderID>";
						xml += "<MerchantFulfillmentID>"+rs.getInt("aom.order_stock_id")+"</MerchantFulfillmentID>";
						xml += "<FulfillmentDate>"+dateString +"+00:00"+"</FulfillmentDate>";
						xml += "<FulfillmentData>";
						xml += "<CarrierName>"+orderBean.getDeliverName()+"</CarrierName>";
						xml += "<ShipperTrackingNumber>"+rs.getString("ap.package_code")+"</ShipperTrackingNumber>";
						xml += "</FulfillmentData>";
						xml += "</OrderFulfillment>";
						xml += "</Message>";
						orderIdList.add(rs.getInt("aom.order_id"));
						num++;
					}
				}
				rs.close();
				if(!"".equals(xml)){
					if (StockServiceImpl.amazonSubmitFeed(xml)) {
						if (orderIdList != null && orderIdList.size() > 0) {
							for (int i = 0; i < orderIdList.size(); i++) {
								String updateSql = "UPDATE amazon_order_message SET "
										+ "last_oper_datetime = '"+DateUtil.getNow()+ "',status=1,send_count=(send_count+1) WHERE order_id="
										+ orderIdList.get(i);
								service.getDbOp().executeUpdate(updateSql);
							}
						}
					}
				}
				System.out.println("亚马逊订单配送确认上传数定时任务结束");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}
	}
}
