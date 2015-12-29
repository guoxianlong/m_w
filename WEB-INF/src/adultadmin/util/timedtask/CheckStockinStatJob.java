package adultadmin.util.timedtask;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mmb.rec.stat.bean.CheckStockinStatBean;
import mmb.rec.stat.bean.CheckStockinStatService;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;
//商品入库统计
public class CheckStockinStatJob  implements Job {
	public static byte[] CheckStockinStatJobLock = new byte[0];
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("商品入库统计定时任务开始");
		synchronized(CheckStockinStatJobLock){
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			WareService wareService = new WareService(dbOp);
			CheckStockinStatService cssService = new CheckStockinStatService(IBaseService.CONN_IN_SERVICE, dbOp);
			DbOperation slave2 = new DbOperation();
			slave2.init(DbOperation.DB_SLAVE2);
			List<CheckStockinStatBean> buyStockinList = new ArrayList<CheckStockinStatBean>();
			CheckStockinStatBean checkStockinBean = null;
			List<CheckStockinStatBean>  cargoOperationList = new ArrayList<CheckStockinStatBean> ();
			try{
				wareService.getDbOp().startTransaction();//事务开始
				String nowDate = DateUtil.getNow();
				String beforeDate = DateUtil.getBackFromDate(nowDate, 1);
				String beforeBeginDate = beforeDate + " 00:00:00";
				String beforeEndDate = beforeDate + " 23:59:59";
				String buyStockinsql = "select stock_area, product_line_id, sum(stockin_count) ,count(productId) from " +
						"(select bs.stock_area stock_area, plc.product_line_id product_line_id, sum(bsp.stockin_count) stockin_count, p.id productId  " +
						"from buy_stockin bs, buy_stockin_product bsp, product p, product_line_catalog plc " +
						"where bs.id=bsp.buy_stockin_id and bsp.product_id=p.id and ((p.parent_id1=plc.catalog_id and plc.catalog_type=1) or (p.parent_id2=plc.catalog_id and plc.catalog_type=2)) " +
						"and bs.confirm_datetime between '" + beforeBeginDate + "' and '" + beforeEndDate + "' and bs.status in (4,6) " +
						"group by p.id, bs.stock_area, plc.product_line_id" +
						") checkstockinstat " +
						"group by stock_area, product_line_id";
				ResultSet buyStockinRS = slave2.executeQuery(buyStockinsql); 
				while( buyStockinRS.next() ) {
					checkStockinBean = new CheckStockinStatBean();
					checkStockinBean.setDate(beforeDate);
					checkStockinBean.setArea(buyStockinRS.getInt(1));
					checkStockinBean.setProductLineId(buyStockinRS.getInt(2));
					checkStockinBean.setCheckProductCount(buyStockinRS.getInt(3));
					checkStockinBean.setCheckSkuCount(buyStockinRS.getInt(4));
					checkStockinBean.setUpshelfProductCount(0);
					checkStockinBean.setUpshelfSkuCount(0);
					buyStockinList.add(checkStockinBean);
				}
				buyStockinRS.close();
				
				int buyStockinSize = buyStockinList.size();
				String cargoOperationSql = "select stock_area, product_line_id, sum(stockCount) ,count(productId) from " +
						"(select ci.area_id stock_area, plc.product_line_id product_line_id, sum(coc.stock_count) stockCount, p.id productId " +
						"from cargo_operation co,cargo_operation_cargo coc, product p, product_line_catalog plc,cargo_info ci " +
						"where co.id=coc.oper_id and coc.product_id=p.id and ((p.parent_id1=plc.catalog_id and plc.catalog_type=1) or (p.parent_id2=plc.catalog_id and plc.catalog_type=2)) " +
						"and ci.whole_code=coc.in_cargo_whole_code and co.type = 0 " +
						"and co.complete_datetime between '" + beforeBeginDate + "' and '" + beforeEndDate + "' " +
						"and co.status in (7,8) and coc.type = 0 and ci.stock_type = 0 " +
						"group by ci.area_id , p.id, plc.product_line_id" +
						") cargoOperation group by stock_area, product_line_id";
				ResultSet cargoOperationRS = slave2.executeQuery(cargoOperationSql); 
				while( cargoOperationRS.next() ) {
					boolean flag = false;
					int area = cargoOperationRS.getInt(1);
					int productLineId = cargoOperationRS.getInt(2);
					int upshelf_product_count = cargoOperationRS.getInt(3);
					int upshelf_sku_count = cargoOperationRS.getInt(4);
					for (int i = 0 ; i < buyStockinSize; i ++ ) {
						CheckStockinStatBean buyStockBean = buyStockinList.get(i);
						if ( area == buyStockBean.getArea() && productLineId == buyStockBean.getProductLineId() ) {
							buyStockBean.setUpshelfProductCount(upshelf_product_count);
							buyStockBean.setUpshelfSkuCount(upshelf_sku_count);
							flag = true;
						}
					}
					if (!flag) {
						checkStockinBean = new CheckStockinStatBean();
						checkStockinBean.setDate(beforeDate);
						checkStockinBean.setArea(area);
						checkStockinBean.setProductLineId(productLineId);
						checkStockinBean.setCheckProductCount(0);
						checkStockinBean.setCheckSkuCount(0);
						checkStockinBean.setUpshelfProductCount(upshelf_product_count);
						checkStockinBean.setUpshelfSkuCount(upshelf_sku_count);
						cargoOperationList.add(checkStockinBean);
					}
				}
				cargoOperationRS.close();
				
				buyStockinList.addAll(cargoOperationList);
				int listSize = buyStockinList.size();
				for (int i = 0 ; i < listSize; i ++ ) {
					CheckStockinStatBean csiBean = buyStockinList.get(i);
					if (!cssService.addCheckStockinStat(csiBean)) {
						wareService.getDbOp().rollbackTransaction();
						return;
					}
				}
				wareService.getDbOp().commitTransaction();//事务提交
				System.out.println("商品入库统计定时任务结束");
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
