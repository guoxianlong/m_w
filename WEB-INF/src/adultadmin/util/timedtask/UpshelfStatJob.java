package adultadmin.util.timedtask;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mmb.rec.stat.bean.UpshelfStatBean;
import mmb.rec.stat.bean.UpshelfStatService;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;
//上架效率
public class UpshelfStatJob  implements Job {
	public static byte[] UpshelfStatJobLock = new byte[0];
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("上架效率定时任务开始");
		synchronized(UpshelfStatJobLock){
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			WareService wareService = new WareService(dbOp);
			UpshelfStatService upshelfStatService = new UpshelfStatService(IBaseService.CONN_IN_SERVICE, dbOp);
			DbOperation slave2 = new DbOperation();
			slave2.init(DbOperation.DB_SLAVE2);
			List<UpshelfStatBean> upshelfStatList = new ArrayList<UpshelfStatBean>();
			UpshelfStatBean upshelfStatBean = null;
			try{
				wareService.getDbOp().startTransaction();//事务开始
				String nowDate = DateUtil.getNow();
				String beforeDate = DateUtil.getBackFromDate(nowDate, 1);
				String beforeBeginDate = beforeDate + " 00:00:00";
				String beforeEndDate = beforeDate + " 23:59:59";
				String sql = "select csc.stock_area, plc.product_line_id, count(distinct csc.code), sum(csc.stock_in_count), count(distinct p.id) " +
						"from cargo_stock_card csc,product p, product_line_catalog plc " +
						"where csc.product_id=p.id and ((p.parent_id1=plc.catalog_id and plc.catalog_type=1) or (p.parent_id2=plc.catalog_id and plc.catalog_type=2)) and csc.stock_type=0 " +
						"and csc.card_type in (3,14) and csc.create_datetime between '" + beforeBeginDate + "' and '" + beforeEndDate + "' " +
						"group by csc.stock_area, plc.product_line_id";
				
				ResultSet UpshelfStatRS = slave2.executeQuery(sql); 
				while( UpshelfStatRS.next() ) {
					upshelfStatBean = new UpshelfStatBean();
					upshelfStatBean.setDate(beforeDate);
					upshelfStatBean.setArea(UpshelfStatRS.getInt(1));
					upshelfStatBean.setProductLineId(UpshelfStatRS.getInt(2));
					upshelfStatBean.setOperCount(UpshelfStatRS.getInt(3));
					upshelfStatBean.setProductCount(UpshelfStatRS.getInt(4));
					upshelfStatBean.setSkuCount(UpshelfStatRS.getInt(5));
					upshelfStatList.add(upshelfStatBean);
				}
				UpshelfStatRS.close();
				int listSize = upshelfStatList.size();
				for (int i = 0 ; i < listSize; i ++ ) {
					UpshelfStatBean usBean = upshelfStatList.get(i);
					if (!upshelfStatService.addUpshelfStat(usBean)) {
						wareService.getDbOp().rollbackTransaction();
						return;
					}
				}
				wareService.getDbOp().commitTransaction();//事务提交
				System.out.println("上架效率定时任务结束");
			}catch (Exception e) {
				e.printStackTrace();
				wareService.getDbOp().rollbackTransaction();
			} finally {
				wareService.releaseAll();
				slave2.release();
			}
		}
	}
	
}

