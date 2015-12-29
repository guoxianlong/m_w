package adultadmin.util.timedtask;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.action.vo.voOrder;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;
import cn.mmb.jd.support.JdOrderInfoBean;
import cn.mmb.jd.support.JdOrderSend;

import com.jd.open.api.sdk.domain.delivery.LogisticsCompany;

public class JdAdultOrderFulfillmentJob implements Job {
	public static byte[] lock = new byte[0];
    //京东订单配送确认上传数定时任务
	String appKey = "418773602E3761FE84E443D24C9402A6";
	 String appSecret = "05cf9fd00721453eae0106ede68d4f64";
	 String accessToken = "09c4e3e1-13f5-4a31-888a-b520c3ca7e4f";
	 String url = "http://gw.api.360buy.com/routerjson";
	public void execute(JobExecutionContext context) throws JobExecutionException {
		synchronized (lock) {
			JdOrderSend jos = new JdOrderSend(appKey, appSecret, accessToken, url);
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult");
			WareService service = new WareService(dbOp);
			try {
				System.out.println("京东成人订单配送确认修改订单状态任务开始");
				Map<String,LogisticsCompany> delivers = new HashMap<String,LogisticsCompany>();
				List<LogisticsCompany> deliverCompony = jos.getDeliveryLogistics();
				for( LogisticsCompany lc : deliverCompony ) {
					String remark = lc.getLogisticsRemark();
					if( remark.indexOf(",") != -1 ) {
						String[] remarks = remark.split(",");
						for( int i = 0 ; i < remarks.length; i ++ ) {
							delivers.put(remarks[i], lc);
						}
					} else {
						delivers.put(remark, lc);
					}
				}
				System.out.println(delivers);
				String query = "SELECT uop.pop_order_id,jom.order_id,jom.order_stock_id,ap.package_code,ap.order_code, dci.phone FROM jd_adult_order_message jom  "
						+ " LEFT JOIN  audit_package ap ON jom.order_id=ap.order_id "
						+ " LEFT JOIN  user_order_pop uop ON jom.order_id=uop.order_id left join deliver_corp_info dci on dci.id = ap.deliver "
						+ " WHERE jom.status=0 and jom.send_count<5 order by jom.id desc limit 200";
				ResultSet rs = service.getDbOp().executeQuery(query);
				List<JdOrderInfoBean> joiList = new ArrayList<JdOrderInfoBean>();
				while (rs.next()) {
					voOrder orderBean = service.getOrder(rs.getInt("jom.order_id"));
					if(orderBean!=null){
						String orderId = rs.getString("jom.order_id");
						String jdOrderId = rs.getString("uop.pop_order_id");
						JdOrderInfoBean joiBean = new JdOrderInfoBean();
						joiBean.setOrderId(orderBean.getId());
						joiBean.setJdOrderId(jdOrderId);
						joiBean.setPackageCode(rs.getString("ap.package_code"));
						
						if( delivers.containsKey(orderBean.getDeliverName()) ) {
							joiBean.setLogisticsId(delivers.get(orderBean.getDeliverName()).getLogisticsId()+"");
						} else {
							joiBean.setLogisticsId("0");
						}
						joiBean.setDeliverName(orderBean.getDeliverName());
						joiList.add(joiBean);
					}
				}
				rs.close();
				
				if( joiList.size() != 0 ) {
					for( JdOrderInfoBean joiBean : joiList ) {
						if( "0".equals((joiBean.getLogisticsId())) ) {
							//没有匹配到快递公司
							String tip = "未匹配到京东设置的快递公司："+ joiBean.getDeliverName();
							String updateSql = "UPDATE jd_adult_order_message SET "
									+ "last_oper_datetime = '"+DateUtil.getNow()+ "',send_count=(send_count+1), order_status_code='" +tip+"' WHERE order_id="
									+ joiBean.getOrderId();
							service.getDbOp().executeUpdate(updateSql);
						} else {
							String result = jos.submitSendOut(joiBean.getJdOrderId(),joiBean.getLogisticsId(), joiBean.getPackageCode());
							if( "success".equals(result) ) {
								String updateSql = "UPDATE jd_adult_order_message SET "
										+ "last_oper_datetime = '"+DateUtil.getNow()+ "',status=1,send_count=(send_count+1) WHERE order_id="
										+ joiBean.getOrderId();
								service.getDbOp().executeUpdate(updateSql);
							} else {
								System.out.println(result);
								//更新状态失败，给出原因
								if (result.length() >= 22 ) {
									String updateSql = "UPDATE jd_adult_order_message SET "
											+ "last_oper_datetime = '"+DateUtil.getNow()+ "',send_count=(send_count+1), order_status_code='" +result.subSequence(0, 21)+"' WHERE order_id="
											+ joiBean.getOrderId();
									service.getDbOp().executeUpdate(updateSql);
								} else {
									String updateSql = "UPDATE jd_adult_order_message SET "
											+ "last_oper_datetime = '"+DateUtil.getNow()+ "',send_count=(send_count+1), order_status_code='" +result+"' WHERE order_id="
											+ joiBean.getOrderId();
									service.getDbOp().executeUpdate(updateSql);
								}
							}
						}
						
						
					}
				}
				System.out.println("京东成人订单配送确认修改订单状态任务结束");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}
	}
}
