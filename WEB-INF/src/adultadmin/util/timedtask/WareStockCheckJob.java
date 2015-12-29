package adultadmin.util.timedtask;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class WareStockCheckJob  implements Job {
	public static byte[] StockExceptionJobLock = new byte[0];
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println(DateUtil.getNow() + "物流库存校验定时任务开始");
		synchronized(StockExceptionJobLock){
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			WareService wareService = new WareService(dbOp);
			DbOperation slave2 = new DbOperation();
			slave2.init(DbOperation.DB_SLAVE2);
			WareService slave2Service = new WareService(slave2);
			
			try{
				wareService.getDbOp().startTransaction();//事务开始
				
				for (int stockType : ProductStockBean.stockTypeMap.keySet()) {
					if (stockType != ProductStockBean.STOCKTYPE_QUALITYTESTING && stockType != ProductStockBean.STOCKTYPE_NIFFER && stockType != ProductStockBean.STOCKTYPE_CUSTOMER) {
						for(int stockArea = 0;stockArea < 10; stockArea++){
							//增城
							if (!searchStockInfo(slave2Service, wareService, stockType, stockArea)) {
								wareService.getDbOp().rollbackTransaction();//事务回滚
								System.out.println(DateUtil.getNow() + "物流库存校验定时任务异常");
								return;
							}

							//无锡
//							if (!searchStockInfo(slave2Service, wareService, stockType, ProductStockBean.AREA_WX)) {
//								wareService.getDbOp().rollbackTransaction();//事务回滚
//								System.out.println(DateUtil.getNow() + "物流库存校验定时任务异常");
//								return;
//							}
//
//							//深圳
//							if (!searchStockInfo(slave2Service, wareService, stockType, ProductStockBean.AREA_SZ)) {
//								wareService.getDbOp().rollbackTransaction();//事务回滚
//								System.out.println(DateUtil.getNow() + "物流库存校验定时任务异常");
//								return;
//							}
//
//							//西安
//							if (!searchStockInfo(slave2Service, wareService, stockType, ProductStockBean.AREA_XA)) {
//								wareService.getDbOp().rollbackTransaction();//事务回滚
//								System.out.println(DateUtil.getNow() + "物流库存校验定时任务异常");
//								return;
//							}
//
//							//北京
//							if (!searchStockInfo(slave2Service, wareService, stockType, ProductStockBean.AREA_BJ)) {
//								wareService.getDbOp().rollbackTransaction();//事务回滚
//								System.out.println(DateUtil.getNow() + "物流库存校验定时任务异常");
//								return;
//							}
						}
					}
				}
				
				wareService.getDbOp().commitTransaction();//事务提交
				System.out.println(DateUtil.getNow() + "物流库存校验定时任务结束");
			}catch (Exception e) {
				e.printStackTrace();
				wareService.getDbOp().rollbackTransaction();
				System.out.println(DateUtil.getNow() + "物流库存校验定时任务异常");
			} finally {
				wareService.releaseAll();
				slave2.release();
			}
		}
	}
	
	public boolean searchStockInfo(WareService slave2Service, WareService wareService, int stockType, int stockArea) {
		HashMap<Integer, HashMap<String, Integer>> map = new HashMap<Integer, HashMap<String,Integer>>();
		HashMap<String, Integer> tempMap = null;
		String nowDate = DateUtil.getNow().substring(0, 10);
		
		try{
			
			//库存商品数量大于0的
			String stockProductSql = "select * from " +
					" (select sum(ps.stock + ps.lock_count) stockcount, ps.product_id productid from product_stock ps where type= " + stockType + " and area = "+ stockArea + " group by ps.product_id) stockps " +
					" where stockcount > 0 ";
			ResultSet stockProductRS = slave2Service.getDbOp().executeQuery(stockProductSql); 
			while( stockProductRS.next() ) {
				tempMap = new HashMap<String, Integer>();
				tempMap.put("count", stockProductRS.getInt(1));
				tempMap.put("cargoCount", 0);
				tempMap.put("batchCount", 0);
				map.put(stockProductRS.getInt(2), tempMap);
			}
			stockProductRS.close();
			
			//货位库存商品数量大于0的
			String cargoStockProductSql = "select * from " +
					" (select sum(cps.stock_count + cps.stock_lock_count) cargostockcount, cps.product_id productid from cargo_product_stock cps, cargo_info ci where cps.cargo_id=ci.id and ci.stock_type= " + stockType + " and ci.area_id = "+ stockArea + " group by cps.product_id) cargops " +
					" where cargostockcount > 0";
			ResultSet cargoStockProductRS = slave2Service.getDbOp().executeQuery(cargoStockProductSql); 
			while( cargoStockProductRS.next() ) {
				tempMap = map.get(cargoStockProductRS.getInt(2));
				if(tempMap != null){
					tempMap.put("cargoCount", tempMap.get("cargoCount") + cargoStockProductRS.getInt(1));
				} else {
					tempMap = new HashMap<String, Integer>();
					tempMap.put("count", 0);
					tempMap.put("cargoCount", cargoStockProductRS.getInt(1));
					tempMap.put("batchCount", 0);
					map.put(cargoStockProductRS.getInt(2), tempMap);
				}
			}
			cargoStockProductRS.close();
			
			//库存批次商品数量大于0的
			String StockBatchSql = "select * from " +
					" (select sum(sb.batch_count) batchcount, sb.product_id productid from stock_batch sb where sb.stock_type =  " + stockType + " and sb.stock_area = " + stockArea + " group by sb.product_id) batchs " +
					" where batchcount > 0";
			ResultSet StockBatchRS = slave2Service.getDbOp().executeQuery(StockBatchSql); 
			while( StockBatchRS.next() ) {
				tempMap = map.get(StockBatchRS.getInt(2));
				if(tempMap != null){
					tempMap.put("batchCount", tempMap.get("batchCount") + StockBatchRS.getInt(1));
				} else {
					tempMap = new HashMap<String, Integer>();
					tempMap.put("count", 0);
					tempMap.put("cargoCount", 0);
					tempMap.put("batchCount", StockBatchRS.getInt(1));
					map.put(StockBatchRS.getInt(2), tempMap);
				}
			}
			StockBatchRS.close();
			
			if(!addWareStockCheck(map, wareService, nowDate, stockType, stockArea)){
				return false;
			}
			
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean addWareStockCheck(HashMap<Integer, HashMap<String, Integer>> map, 
			WareService wareService, String nowDate, int stockType, int stockArea) {
		boolean result = true;
		Iterator<Entry<Integer, HashMap<String, Integer>>> iter = map.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<Integer, HashMap<String, Integer>> entry = (Map.Entry<Integer, HashMap<String,Integer>>)iter.next();
			int productId = (Integer)entry.getKey();
			HashMap<String, Integer> stockMap = (HashMap<String, Integer>)entry.getValue();
			int count = stockMap.get("count");
			int cargoCount = stockMap.get("cargoCount");
			int batchCount = stockMap.get("batchCount");
			//有一个数量不为0，且3个值数量不相等时添加
			if ((count != 0 || cargoCount != 0 || batchCount != 0) && !(count == cargoCount && cargoCount == batchCount)) {
				if (!wareService.getDbOp().executeUpdate("insert into ware_stock_check(create_date,stock_area,stock_type,product_id,stock_count,cargo_stock_count,stock_batch_count) values ('"+nowDate+"',"+stockArea+","+stockType+","+productId+","+count+","+cargoCount+","+batchCount+")")) {
					result = false;
					break;
				}
			}
		}
		
		return result;
	}
	
}

