package adultadmin.util.timedtask;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.mmb.dangdang.support.DangDangOrderInfo;
import cn.mmb.dangdang.support.DangDangOrderItem;
import cn.mmb.dangdang.support.DangDangOrderSend;
import cn.mmb.dangdang.support.OrderInfoModel;
import adultadmin.action.vo.voOrder;
import adultadmin.service.impl.StockServiceImpl;
import adultadmin.util.Constants;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class DangDangOrderFulfillmentJob implements Job {
	public static byte[] lock = new byte[0];
	String filePath = Constants.WARE_UPLOAD+"ware/sendGoods.xml";
	String appSecret = "3C4DFEECF2E27ABEEAE2B05B9D0253BE97C804C809544C62079D5733B2F4CB56";
	String redirectUrlPrefix = "http://oauth.dangdang.com/default.jsp";
	String appKey = "2100002502";
	String secret="65A6ED6FFA778EC2EAC9D959CC29A4AB";
	String baseUrl="http://api.open.dangdang.com/openapi/rest";
    //亚马逊订单配送确认上传数定时任务
	public void execute(JobExecutionContext context) throws JobExecutionException {
		synchronized (lock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult");
			WareService service = new WareService(dbOp);
			try {
				System.out.println("当当订单配送确认修改订单状态任务开始");
				String query = "SELECT uop.pop_order_id,dom.order_id,dom.order_stock_id,ap.package_code,ap.order_code, dci.phone FROM dangdang_order_message dom  "
						+ " LEFT JOIN  audit_package ap ON dom.order_id=ap.order_id "
						+ " LEFT JOIN  user_order_pop uop ON dom.order_id=uop.order_id left join deliver_corp_info dci on dci.id = ap.deliver "
						+ " WHERE dom.status=0 and send_count<5 order by dom.id desc limit 200";
				ResultSet rs = service.getDbOp().executeQuery(query);
				List<DangDangOrderInfo> ddoiList = new ArrayList<DangDangOrderInfo>();
				Map<String,String> map = new HashMap<String,String>();
				while (rs.next()) {
					voOrder orderBean = service.getOrder(rs.getInt("dom.order_id"));
					if(orderBean!=null){
						String orderId = rs.getString("dom.order_id");
						String dangdangOrderId = rs.getString("uop.pop_order_id");
						DangDangOrderInfo ddoi = new DangDangOrderInfo();
						ddoi.setLogisticsName(orderBean.getDeliverName());
						ddoi.setLogisticsOrderID(rs.getString("ap.package_code"));
						ddoi.setLogisticsTel(rs.getString("dci.phone"));
						ddoi.setOrderID(dangdangOrderId);
						ddoi.setLocalOrderId(orderId);
						ddoiList.add(ddoi);
						map.put(dangdangOrderId, orderId);
					}
				}
				rs.close();
				if( ddoiList.size() != 0 ) {
					String dateString = DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
					DangDangOrderSend ddos = new DangDangOrderSend(appSecret, redirectUrlPrefix, appKey, secret, baseUrl);
					String xml = "<?xml version=\"1.0\" encoding=\"GBK\"?>";
					xml+="<request>";
					xml+="<functionID>dangdang.order.goods.send</functionID>";
					xml+="<time>"+dateString+"</time>";
					xml+="<OrdersList>";
					for( DangDangOrderInfo ddoi : ddoiList ) {
						xml+="<OrderInfo>"; 
						xml+="<orderID>"+ddoi.getOrderID()+"</orderID>";
						xml+="<logisticsName>"+ddoi.getLogisticsName()+"</logisticsName>"; 
						xml+="<logisticsTel>"+ddoi.getLogisticsTel()+"</logisticsTel>"; 
						xml+="<logisticsOrderID>"+ddoi.getLogisticsOrderID()+"</logisticsOrderID>";
						xml+="<SendGoodsList>";
						List<DangDangOrderItem> items = ddos.getOrderItems(ddoi.getOrderID());
						for(DangDangOrderItem item : items  ) {
							xml+="<ItemInfo>";
							xml+="<itemID>"+item.getItemID()+"</itemID>";
							xml+="<sendGoodsCount>"+item.getSendGoodsCount()+"</sendGoodsCount>";
							xml+="<productItemId>"+item.getProductItemId()+"</productItemId>";
							xml+="</ItemInfo>";
						}
						xml +="</SendGoodsList>";
						xml +="</OrderInfo>";
					}
					xml+="</OrdersList>";
					xml+="</request>";
					List<OrderInfoModel> list = ddos.submitOrderSend(filePath, xml);
					for( OrderInfoModel oim : list ) {
						String orderId = map.get(oim.getOrderID());
						if( oim.getOrderOperCode().equals("0") ) {
							String updateSql = "UPDATE dangdang_order_message SET "
									+ "last_oper_datetime = '"+DateUtil.getNow()+ "',status=1,send_count=(send_count+1) WHERE order_id="
									+ orderId;
							service.getDbOp().executeUpdate(updateSql);
						} else {
							if ( oim.getOrderOperation().length() >= 22 ) {
								System.out.println(oim.getOrderOperation());
							} else {
								System.out.println(oim.getOrderOperation());
								String updateSql = "UPDATE dangdang_order_message SET "
										+ "last_oper_datetime = '"+DateUtil.getNow()+ "',send_count=(send_count+1), order_status_code='" +oim.getOrderOperation()+"' WHERE order_id="
										+ orderId;
								service.getDbOp().executeUpdate(updateSql);
							}
						}
					}
				}
				
				System.out.println("当当订单配送确认修改订单状态任务结束");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}
	}
}
