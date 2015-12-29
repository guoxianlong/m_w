package adultadmin.util.timedtask;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mmb.bi.model.BiProductLineDeliverInfo;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.bean.order.OrderStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;
//添加产品线发货信息定时任务
public class BiAddProductLineDeliverInfoJob implements Job {
	public static byte[] lock = new byte[0];
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		synchronized (lock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			DbOperation slaveDbOp = new DbOperation();
			slaveDbOp.init(DbOperation.DB_SLAVE2);
			WareService wareService = new WareService(dbOp);
			try {
				wareService.getDbOp().startTransaction();// 事务开始
				System.out.println("添加产品线发货信息定时任务开始");
				String today = DateUtil.getNowDateStr();
				String yesterday = DateUtil.getBackFromDate(today, 1);
				String query = " SELECT os.id,os.stock_area,uoei.add_id1,plc.product_line_id,sum(osp.stockout_count) FROM order_stock os"
					   +" LEFT JOIN user_order_extend_info uoei ON os.order_code=uoei.order_code"
					   +" LEFT JOIN order_stock_product osp ON os.id=osp.order_stock_id"
					   +" LEFT JOIN product p ON osp.product_id=p.id"
					   +" LEFT JOIN product_line_catalog plc ON (p.parent_id1=plc.catalog_id or p.parent_id2=plc.catalog_id)"
					   +" LEFT JOIN audit_package ap ON os.order_code=ap.order_code"
					   +" WHERE os.status<>"+OrderStockBean.STATUS4 +" and ap.check_datetime >= '"+yesterday+" 00:00:00' and ap.check_datetime <= '"+yesterday+" 23:59:59'"
					   +" GROUP BY os.stock_area,plc.product_line_id,uoei.add_id1 ORDER BY stock_area";
				ResultSet rs = slaveDbOp.executeQuery(query);
				List<BiProductLineDeliverInfo> list = new ArrayList<BiProductLineDeliverInfo>();
				while (rs.next()) {
					BiProductLineDeliverInfo bean = new BiProductLineDeliverInfo();
					bean.setCreateDate(yesterday);
					bean.setProductCount(rs.getInt("sum(osp.stockout_count)"));
					bean.setProductLineId(rs.getInt("plc.product_line_id"));
					bean.setProvincesId(rs.getInt("uoei.add_id1"));
					bean.setStockArea(rs.getInt("os.stock_area"));
					list.add(bean);
				}
				rs.close();
				if(list!=null && list.size()>0){
					for(int i=0;i<list.size();i++){
						BiProductLineDeliverInfo bean = (BiProductLineDeliverInfo)list.get(i);
						String insertSQL = " INSERT INTO bi_product_line_deliver_info (create_date,stock_area,provinces_id,product_line_id,product_count)"
								+ " VALUES('"+bean.getCreateDate()+"',"+bean.getStockArea() +","+bean.getProvincesId()+","+bean.getProductLineId()+","+bean.getProductCount()+")";
						
						if (!dbOp.executeUpdate(insertSQL)) {
							wareService.getDbOp().rollbackTransaction();
							System.out.println(DateUtil.getNow() + "添加产品线发货信息出错");
							System.out.println(DateUtil.getNow() + "添加产品线发货信息定时任务结束");
							return ;
						}
					}
				}
				wareService.getDbOp().commitTransaction();
				System.out.println("添加产品线发货信息定时任务结束");
			} catch (Exception e) {
				e.printStackTrace();
				wareService.getDbOp().rollbackTransaction();
				System.out.println(DateUtil.getNow() + "添加产品线发货信息定时任务异常");
			} finally {
				wareService.releaseAll();
				slaveDbOp.release();
			}
		}
	}

}
