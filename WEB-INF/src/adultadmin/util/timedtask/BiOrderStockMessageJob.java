package adultadmin.util.timedtask;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.chinasms.sms.SenderSMS3;

import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;
import mmb.bi.model.EBIArea;
import mmb.msg.TemplateMarker;

/**
 * 发单量定时短信
 * @author mengqy
 *
 */
@Component
public class BiOrderStockMessageJob implements Job {

	private static byte[] lock = new byte[0];

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		System.out.println(DateUtil.getNow() + " 定时任务发单量定时短信 开始执行");
		String today = DateUtil.getNowDateStr();
		String yesterday = DateUtil.getBackFromDate(today, 1);		
 
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		
		List<MessageData> list = new ArrayList<BiOrderStockMessageJob.MessageData>();//不同地区的基础数据信息
		
		String emsDeliverIdCondition = null;
		
		for (Integer key : ProductStockBean.stockoutAvailableAreaMap.keySet()) {			
			// 配置EMS
			switch (key.intValue()) {
			// 3 增城 
			case 3:
				emsDeliverIdCondition = " AND mb.deliver IN ( 9, 11 ) "; // 广东省速递局 广速省外
				break;
		    // 4 无锡				
			case 4:
				emsDeliverIdCondition = " AND mb.deliver IN ( 29, 37 ) "; // 无锡邮政 上海无疆
				break;

		    // 9 成都				
			case 9:
				emsDeliverIdCondition = " AND mb.deliver = 52 "; // 四川邮政
				break;

			default:
				emsDeliverIdCondition = null;
				break;
			}
						
			MessageData data = new MessageData();
			data.setAreaId(key.intValue());
			data.setAreaName(ProductStockBean.stockoutAvailableAreaMap.get(key));
			data.setDate("");
			data.setEmsDeliverIdCondition(emsDeliverIdCondition);
			data.setTime(",截单时间20:59");			
		
			list.add(data);
		}
		
		synchronized (lock) {		
			try {
				
				StringBuffer sb = new StringBuffer();
				int total = 0;
				int totalEMS = 0;				
				for (MessageData messageData : list) {					
					// 地区总发单量
					sb.setLength(0);
					sb.append("select count(os.id) thecount ")
					.append(" from  order_stock os,mailing_batch mb, mailing_batch_package mbp ")
					.append("where  os.order_code=mbp.order_code and mbp.mailing_batch_id=mb.id "
							+ "and os.last_oper_time > '"
							+ today
							+ " 00:00:00' and os.last_oper_time <= '"
							+ today
							+ " 23:59:59' and os.status in (2,4,6,7,8) ")
					.append(" AND mb.area = ").append(messageData.getAreaId());
					
//					sb.append(" SELECT COUNT(mbp.order_id) ");
//					sb.append(" FROM mailing_batch AS mb, mailing_batch_package AS mbp ");
//					sb.append(" WHERE mb.id = mbp.mailing_batch_id ");
//					sb.append(" AND mb.transit_datetime BETWEEN '").append(yesterday).append(" 21:00:00' AND '").append(today).append(" 20:59:59' ");				
//					sb.append(" AND mb.area = ").append(messageData.getAreaId());
					
					int totalTemp = dbSlave.getInt(sb.toString());
					messageData.setTotal(totalTemp);
					total += totalTemp;
					if (messageData.getEmsDeliverIdCondition() == null){
						messageData.setTotalEMS(0);
						continue;
					}
					
					// EMS发单量
					sb.setLength(0);
//					sb.append(" SELECT COUNT(mbp.order_id) ");
//					sb.append(" FROM mailing_batch AS mb, mailing_batch_package AS mbp ");
//					sb.append(" WHERE mb.id = mbp.mailing_batch_id ");
//					sb.append(" AND mb.transit_datetime BETWEEN '").append(yesterday).append(" 21:00:00' AND '").append(today).append(" 20:59:59' ");				
//					sb.append(" AND mb.area = ").append(messageData.getAreaId());
//					sb.append(messageData.getEmsDeliverIdCondition());
					
					sb.append("select count(os.id) thecount ")
					.append(" from  order_stock os,mailing_batch mb, mailing_batch_package mbp ")
					.append("where  os.order_code=mbp.order_code and mbp.mailing_batch_id=mb.id "
							+ "and os.last_oper_time > '"
							+ today
							+ " 00:00:00' and os.last_oper_time <= '"
							+ today
							+ " 23:59:59' and os.status in (2,4,6,7,8) ")
					.append(" AND mb.area = ").append(messageData.getAreaId())
					.append(messageData.getEmsDeliverIdCondition());
					
					int totalEMSTemp = dbSlave.getInt(sb.toString());
					messageData.setTotalEMS(totalEMSTemp);
					totalEMS += totalEMSTemp;
				}
				
				MessageData totalData = new MessageData();
				totalData.setAreaName(EBIArea.AreaAll.getName());
				totalData.setDate(DateUtil.formatDate(DateUtil.getNowDate(), "MM月dd日,"));
				totalData.setTime("");
				totalData.setTotal(total);
				totalData.setTotalEMS(totalEMS);
				list.add(0, totalData);
				
				TemplateMarker tm = TemplateMarker.getMarker();
				
//				List<String> contentList = new ArrayList<String>();
				/*for (MessageData messageData2 : list) {
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("date", messageData2.getDate());
					paramMap.put("time", messageData2.getTime());
					paramMap.put("area", messageData2.getAreaName());				
					paramMap.put("total", messageData2.getTotal());
					paramMap.put("totalEMS", messageData2.getTotalEMS());
					paramMap.put("totalEMSPer", getPer(messageData2.getTotalEMS(), messageData2.getTotal()));				
					paramMap.put("totalNotEMS", (messageData2.getTotal() - messageData2.getTotalEMS()));
					paramMap.put("totalNotEMSPer", getPer((messageData2.getTotal() - messageData2.getTotalEMS()), messageData2.getTotal()));
					
					String content = tm.getOutString(TemplateMarker.ORDER_STOCK_COUNT_MESSAGE_NAME, paramMap);
					contentList.add(content);
				}*/
				
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("date", today);
				paramMap.put("total", total);
				
				for (MessageData messageData2 : list) {
					if (messageData2.getAreaId() == 4) {
						paramMap.put("wxArea", "无锡");
						paramMap.put("wxTotal", messageData2.getTotal());
					}
					if (messageData2.getAreaId() == 9) {
						paramMap.put("cdArea", "成都");
						paramMap.put("cdTotal", messageData2.getTotal());
					}
					
				}
				String content = tm.getOutString(TemplateMarker.ORDER_STOCK_COUNT_MESSAGE_NAME, paramMap);
//				contentList.add(content);
				
				String telNumber = null;
				String query = " SELECT GROUP_CONCAT(number) AS numbers FROM bi_sms_number WHERE `status` = 1 ";
				ResultSet rs = dbSlave.executeQuery(query);
				if (rs != null) {
					if (rs.next()) {
						telNumber = rs.getString("numbers");
					}					
					rs.close();
				}
				
				if (telNumber == null) {
					throw new RuntimeException("没有查询到需要发送短信的手机号");
				}
							
//				for (String string : contentList) {
//					if (!SenderSMS3.sendMore(0, telNumber, string)) {
//						throw new RuntimeException("发送定时短信失败");
//					}	
//				}
				
				if (!SenderSMS3.sendMore(0, telNumber, content)) {
					throw new RuntimeException("发送定时短信失败");
				}
				
				System.out.println(DateUtil.getNow() + " 定时任务发单量定时短信 执行结束");		
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(DateUtil.getNow() + " 定时任务发单量定时短信 发生异常");
			}
			finally{
				dbSlave.release();
			}			
		}
	}
	
	/**
	 * 计算float 保留两位小数
	 * 
	 * @param f
	 * @return
	 */
	private float getKeepTwoDecimal(float f) {
		java.math.BigDecimal b = new java.math.BigDecimal(f);
		return (float) b.setScale(2, java.math.BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 计算比分比
	 * @param count
	 * @param total
	 * @return
	 */
	private String getPer(int count, int total) {
		if (total <= 0)
			return "0%";
		return getKeepTwoDecimal((float) ((count * 100.0) / (total * 1.0))) + "%";
	}

	private class MessageData {
		private int areaId;
		private String areaName;
		private String date;
		private String time;
		private String emsDeliverIdCondition;
		
		private int total;
		private int totalEMS;
		
		
		public int getAreaId() {
			return areaId;
		}
		public void setAreaId(int areaId) {
			this.areaId = areaId;
		}
		public String getAreaName() {
			return areaName;
		}
		public void setAreaName(String areaName) {
			this.areaName = areaName;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public String getEmsDeliverIdCondition() {
			return emsDeliverIdCondition;
		}
		public void setEmsDeliverIdCondition(String emsDeliverIdCondition) {
			this.emsDeliverIdCondition = emsDeliverIdCondition;
		}
		public int getTotal() {
			return total;
		}
		public void setTotal(int total) {
			this.total = total;
		}
		public int getTotalEMS() {
			return totalEMS;
		}
		public void setTotalEMS(int totalEMS) {
			this.totalEMS = totalEMS;
		}
	}
}
