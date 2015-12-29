package adultadmin.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import mmb.ware.WareService;
import adultadmin.util.DateUtil;
import adultadmin.util.FileUtil;
import adultadmin.util.db.DbOperation;

public class WareStockLockTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(DateUtil.getNow() + "物流库存锁定量校验定时任务开始");

		Connection conn = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/shop?user=root&password=123456");

			DbOperation slave2 = new DbOperation();
			slave2.init(conn);
			WareService slave2Service = new WareService(slave2);

			//增城合格库
			if (!searchStockLockInfo(slave2Service, null, 0, 3)) {
				System.out.println(DateUtil.getNow() + "物流库存锁定量校验定时任务异常");
				return;
			}
			System.out.println(DateUtil.getNow() + "物流库存锁定量校验定时任务结束");
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println(DateUtil.getNow() + "物流库存锁定量校验定时任务异常");
		} finally {
			try{
				if(conn != null){
					conn.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		System.out.println(DateUtil.getNow() + "物流库存锁定量校验定时任务结束");
	}

	public static boolean searchStockLockInfo(WareService slave2Service, WareService wareService, int stockType, int stockArea) {

		String nowDate = DateUtil.getNow().substring(0, 10);

		HashMap<Integer, HashMap<String, Integer>> map = new HashMap<Integer, HashMap<String,Integer>>();
		HashMap<String, Integer> tempMap;
		StringBuffer s = new StringBuffer();
		try{

			//库存商品数量大于0的
			String stockProductSql = "select ps.lock_count stockcount, ps.product_id productid from product_stock ps where type=" + stockType + " and area = "+ stockArea;
			ResultSet stockProductRS = slave2Service.getDbOp().executeQuery(stockProductSql); 
			while( stockProductRS.next() ) {
				tempMap = new HashMap<String, Integer>();
				tempMap.put("lockcount", stockProductRS.getInt(1));
				tempMap.put("shouldLockcount", 0);
				map.put(stockProductRS.getInt(2), tempMap);
				s.append(stockProductRS.getInt(2)).append(":").append("lockCount  ").append(stockProductRS.getInt(1)).append("\r\n");
			}
			stockProductRS.close();

			/****************调拨冻结**************************/
			String stockExchangeSql = "select sum(sep.stock_out_count) scount,sep.product_id productid "
					+" from stock_exchange_product sep,stock_exchange se "
					+" where sep.stock_exchange_id=se.id"
					+" and (se.status in(2,3,5,6,8) or (se.status=1 and sep.status=1) )" +
					" and se.stock_out_type =" + stockType + " and se.stock_out_area = "+ stockArea+" group by sep.product_id";
			ResultSet stockExchangeRS = slave2Service.getDbOp().executeQuery(stockExchangeSql); 
			while( stockExchangeRS.next() ) {
				tempMap = map.get(stockExchangeRS.getInt(2));
				if(tempMap != null){
					tempMap.put("shouldLockcount", tempMap.get("shouldLockcount")+stockExchangeRS.getInt(1));
					s.append(stockExchangeRS.getInt(2)).append(":").append("exchangeLock  ").append(stockExchangeRS.getInt(1)).append("\r\n");
				}
			}
			stockExchangeRS.close();

			/****************销售出库冻结**************************/
			if(stockType == 0){
				String stockOutSql = "select sum(osp.stockout_count) ocount,osp.product_id productid "+
						"from order_stock_product osp,order_stock os "+
						"where osp.order_stock_id=os.id and os.status in(1,5) "+
						" and os.stock_area = "+ stockArea+" group by osp.product_id";
				ResultSet stockOutRS = slave2Service.getDbOp().executeQuery(stockOutSql); 
				while( stockOutRS.next() ) {
					tempMap = map.get(stockOutRS.getInt(2));
					if(tempMap != null){
						tempMap.put("shouldLockcount", tempMap.get("shouldLockcount")+stockOutRS.getInt(1));
						s.append(stockOutRS.getInt(2)).append(":").append("stockOutLock  ").append(stockOutRS.getInt(1)).append("\r\n");
					}
				}
				stockOutRS.close();
			}

			/****************报损单冻结**************************/
			String bsbyLockSql = "select sum(bsby_count),p.product_id productid from bsby_operationnote b ,bsby_product p" +
					" where b.id=p.operation_id  and (current_type = 1 or current_type=6) and type =0  " +
					" and b.warehouse_area = "+ stockArea+" and b.warehouse_type=" + stockType + " group by p.product_id";
			ResultSet bsbyLockRS = slave2Service.getDbOp().executeQuery(bsbyLockSql); 
			while( bsbyLockRS.next() ) {
				tempMap = map.get(bsbyLockRS.getInt(2));
				if(tempMap != null){
					tempMap.put("shouldLockcount", tempMap.get("shouldLockcount")+bsbyLockRS.getInt(1));
					s.append(bsbyLockRS.getInt(2)).append(":").append("bsbyLock  ").append(bsbyLockRS.getInt(1)).append("\r\n");
				}
			}
			bsbyLockRS.close();

			/****************仓内作业单冻结**************************/
			String cocLockSql = "select sum(coc.stock_count),p.id productid from cargo_operation co join cargo_operation_cargo coc on coc.oper_id=co.id " +
					"join product p on p.id=coc.product_id join cargo_info c1 on coc.in_cargo_whole_code = c1.whole_code " +
					"join cargo_info c2 on  coc.out_cargo_whole_code = c2.whole_code " +
					"where (c1.area_id<>c2.area_id or c1.stock_type <> c2.stock_type) and co.status in(2,3,11,12,20,21,29,30) " +
					"and co.effect_status in (0,1) and c2.area_id = "+ stockArea+" and c2.stock_type=" + stockType + 
					" group by p.id";
			ResultSet cocLockRS = slave2Service.getDbOp().executeQuery(cocLockSql); 
			while( cocLockRS.next() ) {
				tempMap = map.get(cocLockRS.getInt(2));
				if(tempMap != null){
					tempMap.put("shouldLockcount", tempMap.get("shouldLockcount")+cocLockRS.getInt(1));
					s.append(cocLockRS.getInt(2)).append(":").append("cargoOperationLock  ").append(cocLockRS.getInt(1)).append("\r\n");
				}
			}
			cocLockRS.close();


			/****************货位异常单冻结**************************/
			String abnormalLockSql = "select sum(sap.lock_count),sap.product_id productid from sorting_abnormal_product sap "+
					" join cargo_info ci on ci.whole_code=sap.cargo_whole_code and ci.area_id = "+ stockArea+" and ci.stock_type=" + stockType +
					" where sap.lock_count>0 and sap.status in (0,1,2,3)" +
					" group by sap.product_id";
			ResultSet abnormalLockRS = slave2Service.getDbOp().executeQuery(abnormalLockSql); 
			while( abnormalLockRS.next() ) {
				tempMap = map.get(abnormalLockRS.getInt(2));
				if(tempMap != null){
					tempMap.put("shouldLockcount", tempMap.get("shouldLockcount")+abnormalLockRS.getInt(1));
					s.append(abnormalLockRS.getInt(2)).append(":").append("abnormalLock  ").append(abnormalLockRS.getInt(1)).append("\r\n");
				}
			}
			abnormalLockRS.close();
			FileUtil.writeFile("D:", "lock_detail.txt", s.toString());
			//无锡
			if(!addWareStockLockCheck(map, wareService, nowDate, stockType,stockArea)){
				return false;
			}
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static boolean addWareStockLockCheck(HashMap<Integer, HashMap<String, Integer>> map, 
			WareService wareService, String nowDate, int stockType, int stockArea) {
		boolean result = true;
		Iterator<Entry<Integer, HashMap<String, Integer>>> iter = map.entrySet().iterator();
		StringBuffer s = new StringBuffer();
		while(iter.hasNext()){
			Map.Entry<Integer, HashMap<String, Integer>> entry = (Map.Entry<Integer, HashMap<String,Integer>>)iter.next();
			int productId = (Integer)entry.getKey();
			HashMap<String, Integer> stockMap = (HashMap<String, Integer>)entry.getValue();
			int stockLockCount = stockMap.get("lockcount");
			int shouldLockCount = stockMap.get("shouldLockcount");
			if(stockLockCount != shouldLockCount){
				System.out.println("insert into ware_stock_lock_check(create_date,stock_area,stock_type,product_id,stock_lock_count,should_lock_count) values ('"+nowDate+"',"+stockArea+","+stockType+","+productId+","+stockLockCount+","+shouldLockCount+")");
				s.append("insert into ware_stock_lock_check(create_date,stock_area,stock_type,product_id,stock_lock_count,should_lock_count) values ('"+nowDate+"',"+stockArea+","+stockType+","+productId+","+stockLockCount+","+shouldLockCount+")\r\n");
			}
		}
		FileUtil.writeFile("D:", "lock_exception.txt", s.toString());
		return result;
	}
}
