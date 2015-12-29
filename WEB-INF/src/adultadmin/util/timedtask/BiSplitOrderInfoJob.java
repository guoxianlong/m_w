package adultadmin.util.timedtask;

import java.sql.ResultSet;
import java.util.HashMap;

import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;
//添加拆单量信息 定时任务
public class BiSplitOrderInfoJob implements Job {
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
				System.out.println("拆单、越仓发货定时任务开始");
				String today = DateUtil.getNowDateStr();
				String yesterday = DateUtil.getBackFromDate(today, 1);
				
				HashMap<Integer,Integer> splitOrderMap = new HashMap<Integer,Integer>();//记录拆单量（母单）
				HashMap<Integer,Integer> baseOrderMap = new HashMap<Integer,Integer>();//记录应发单量（母单）
				HashMap<Integer,Integer> spanOrderMap = new HashMap<Integer,Integer>();//记录越仓发货数量
				HashMap<Integer,Integer> subOrderMap = new HashMap<Integer,Integer>();//应发单量（子单）
				String query = " SELECT substr(adp.priority,1,1) area, count(distinct c.parent_id) thecount FROM audit_package d"
						+ " join user_order_extend_info uoei on d.order_code=uoei.order_code "
						+ " join provinces po on po.id=uoei.add_id1 " 
						+ " join area_delivery_priority adp on po.name like concat(adp.province,'%') "
					    + " JOIN user_order_sub_list c ON d.order_id=c.child_id "
						+ " where d.check_datetime >='"+yesterday+" 00:00:00' and d.check_datetime <='" + yesterday + " 23:59:59'"
						+ " group by substr(adp.priority,1,1) ";
				ResultSet rs = slaveDbOp.executeQuery(query);
				while (rs.next()) {
					splitOrderMap.put(rs.getInt("area"), rs.getInt("thecount"));
				}
				query = " select area, count(distinct parentId) thecount from (SELECT case when c.parent_id is not null then c.parent_id else d.order_id end as parentId,substr(adp.priority,1,1) area FROM audit_package d"
						+ " join user_order_extend_info uoei on d.order_code=uoei.order_code "
						+ " join provinces po on po.id=uoei.add_id1 " 
						+ " join area_delivery_priority adp on po.name like concat(adp.province,'%') "
					    + " left JOIN user_order_sub_list c ON d.order_id=c.child_id "
						+ " where d.check_datetime >='"+yesterday+" 00:00:00' and d.check_datetime <='" + yesterday + " 23:59:59' ) c"
						+ " group by area ";
				rs = slaveDbOp.executeQuery(query);
				while (rs.next()) {
					baseOrderMap.put(rs.getInt("area"), rs.getInt("thecount"));
				}
				query = " SELECT substr(adp.priority,1,1) area, count(d.order_id) thecount FROM audit_package d"
						+ " join user_order_extend_info uoei on d.order_code=uoei.order_code "
						+ " join provinces po on po.id=uoei.add_id1 " 
						+ " join area_delivery_priority adp on po.name like concat(adp.province,'%') "
						+ " where d.check_datetime >='"+yesterday+" 00:00:00' and d.check_datetime <='" + yesterday + " 23:59:59' and d.areano!=substr(adp.priority,1,1) "
						+ " group by substr(adp.priority,1,1) ";
				rs = slaveDbOp.executeQuery(query);
				while (rs.next()) {
					spanOrderMap.put(rs.getInt("area"), rs.getInt("thecount"));
				}
				query = " SELECT substr(adp.priority,1,1) area, count(d.order_id) thecount FROM audit_package d"
						+ " join user_order_extend_info uoei on d.order_code=uoei.order_code "
						+ " join provinces po on po.id=uoei.add_id1 " 
						+ " join area_delivery_priority adp on po.name like concat(adp.province,'%') "
						+ " where d.check_datetime >='"+yesterday+" 00:00:00' and d.check_datetime <='" + yesterday + " 23:59:59' "
						+ " group by substr(adp.priority,1,1) ";
				rs = slaveDbOp.executeQuery(query);
				while (rs.next()) {
					subOrderMap.put(rs.getInt("area"), rs.getInt("thecount"));
				}
				rs.close();
			    HashMap<Integer, String> stockAreaMap = ProductStockBean.stockoutAvailableAreaMap;
				
				for (int stockAreaId : stockAreaMap.keySet()) {//遍历发货仓
					int splitOrderCount = 0;//拆单量（母单）
					int baseOrderCount = 0;//拆单量（母单）
					int spanOrderCount = 0;//拆单量（母单）
					int subOrderCount = 0;//拆单量（母单）
					if(splitOrderMap.get(stockAreaId)!=null){
						 splitOrderCount =splitOrderMap.get(stockAreaId);
					}
					if(baseOrderMap.get(stockAreaId)!=null){
						 baseOrderCount = baseOrderMap.get(stockAreaId);//应发单量（母单）
					}
					if(spanOrderMap.get(stockAreaId)!=null){
						 spanOrderCount = spanOrderMap.get(stockAreaId);//越仓发货量（子单）
					}
					if(subOrderMap.get(stockAreaId)!=null){
						 subOrderCount = subOrderMap.get(stockAreaId);//应发单量（子单）
					}
					String insertSQL = " INSERT INTO bi_split_order_info (create_date,stock_area,split_order_count,base_order_count,span_order_count,sub_order_count)"
							+ " VALUES('"+yesterday+"',"+stockAreaId +","+splitOrderCount+","+baseOrderCount+","+spanOrderCount+","+subOrderCount+")";
					if (!dbOp.executeUpdate(insertSQL)) {
						wareService.getDbOp().rollbackTransaction();
						System.out.println(DateUtil.getNow() + "拆单、越仓发货定时任务出错");
						System.out.println(DateUtil.getNow() + "拆单、越仓发货定时任务结束");
						return ;
					}
				}
				wareService.getDbOp().commitTransaction();
				System.out.println("拆单、越仓发货定时任务结束");
			} catch (Exception e) {
				e.printStackTrace();
				wareService.getDbOp().rollbackTransaction();
				System.out.println(DateUtil.getNow() + "拆单、越仓发货定时任务异常");
			} finally {
				wareService.releaseAll();
				slaveDbOp.release();
			}
		}
	}
}
