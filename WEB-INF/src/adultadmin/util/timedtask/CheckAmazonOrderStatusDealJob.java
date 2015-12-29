package adultadmin.util.timedtask;

/**
 * 查询亚马逊订单的状态是否已经成功修改的任务
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.rec.stat.service.CargoLockCountStatService;
import mmb.rec.sys.bean.TempCargoLockCountInfoBean;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.amazonservices.mws.orders._2013_09_01.samples.GetOrderSample;

import adultadmin.bean.order.AmazonOrderStatusDeal;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.db.DbOperation;

public class CheckAmazonOrderStatusDealJob  implements Job {
	public static byte[] cargoProductLockStockCheckLock = new byte[0];
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("亚马逊订单状态查询任务开始");
		synchronized(cargoProductLockStockCheckLock){
			DbOperation dbOp2 = new DbOperation();
			dbOp2.init("adult");
			WareService wareService = new WareService( dbOp2 );
			try {
				List<AmazonOrderStatusDeal> list = new ArrayList<AmazonOrderStatusDeal>();
				List<String> amazonOrderCodes = new ArrayList<String>();
				//根据亚马逊的接口要求 每次只能处理50条
				String sql = "select aom.id,aom.order_id,uop.pop_order_id from amazon_order_message aom, user_order_pop uop where aom.order_id = uop.order_id and aom.status=1 and send_count < 5 order by id asc limit 0, 50";
				ResultSet rs = dbOp2.executeQuery(sql);
				while ( rs.next() ) {
					AmazonOrderStatusDeal aosd = new AmazonOrderStatusDeal();
					aosd.setAmazonOrdreCode(rs.getString("uop.pop_order_id"));
					aosd.setId(rs.getInt("aom.id"));
					aosd.setOrderId(rs.getInt("aom.order_id"));
					list.add(aosd);
					amazonOrderCodes.add(aosd.getAmazonOrdreCode());
				}
				if( amazonOrderCodes.size() != 0 ) {
					Map<String,String> map = GetOrderSample.getOrderStatus(amazonOrderCodes);
					List<AmazonOrderStatusDeal> deleteList = new ArrayList<AmazonOrderStatusDeal>();
					List<AmazonOrderStatusDeal> undealList = new ArrayList<AmazonOrderStatusDeal>();
					List<AmazonOrderStatusDeal> noReturnList = new ArrayList<AmazonOrderStatusDeal>();
					for( AmazonOrderStatusDeal temp : list ) {
						if( map.containsKey(temp.getAmazonOrdreCode()) ) {
							String status = map.get(temp.getAmazonOrdreCode());
							if( status.equals("Shipped") || status.equals("InvoiceUnconfirmed") ) {
								//这两种状态， 表示已经成功的改成了已发货，删除订单的信息
								deleteList.add(temp);
							} else {
								//暂时其他两种状态都认为是未成功。
								temp.setOrderStatusCode(status);
								undealList.add(temp);
							}
						} else {
							noReturnList.add(temp);
						}
					}
					String deleteIds = "";
					for(int i = 0; i < deleteList.size(); i++ ) {
						AmazonOrderStatusDeal aosd = deleteList.get(i);
						if( i == 0 ) {
							deleteIds += aosd.getId();
						} else {
							deleteIds += "," + aosd.getId();
						}
					}
					wareService.getDbOp().startTransaction();
					//对于以确定的是成功的状态， 会删除记录
					if( !deleteIds.equals("") ) {
						String deleteSql = "delete from amazon_order_message where id in ("+deleteIds +")";
						wareService.getDbOp().executeUpdate(deleteSql);
					}
					//获取到了订单状态但是 还不确定的，会修改成status 0 并重新发送，并更新状态名称到数据库中
					if( undealList.size() != 0  ) {
						//String updateSql = "update amazon_order_message set status = 0, ";
						for( AmazonOrderStatusDeal temp : undealList ) {
							String updateSql = "update amazon_order_message set status = 0, order_status_code='"+temp.getOrderStatusCode() + "' where id="+temp.getId();
							wareService.getDbOp().executeUpdate(updateSql);
						}
					}
					//对于找不到的订单也做相应处理
					if( noReturnList.size() != 0 )  {
						for( AmazonOrderStatusDeal temp : noReturnList ) {
							String updateSql = "update amazon_order_message set status = 0, order_status_code='找不到订单' where id="+ temp.getId();
							wareService.getDbOp().executeUpdate(updateSql);
						}
					}
					wareService.getDbOp().commitTransaction();
				}
				System.out.println("亚马逊订单状态查询任务结束");
			}catch (Exception e) {
				e.printStackTrace();
				System.out.println("亚马逊订单状态查询任务异常");
				 boolean autoCommit = true;
				try {
					autoCommit = wareService.getDbOp().getConn().getAutoCommit();
				} catch (SQLException e1) {}
				if( !autoCommit) {
					wareService.getDbOp().rollbackTransaction();
				}
			} finally {
				wareService.releaseAll();
			}
		}
	}
	
}
