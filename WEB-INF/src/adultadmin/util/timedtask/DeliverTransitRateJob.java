package adultadmin.util.timedtask;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mmb.stock.stat.DeliverCorpInfoBean;
import mmb.stock.stat.DeliverService;
import mmb.tms.model.DeliverTransitRate;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.service.infc.IBaseService;
import adultadmin.util.Arith;
import adultadmin.util.db.DbOperation;

/**
 * 统计交接及时率
 * @author 李宁
 * @date 2014-4-1
 */
public class DeliverTransitRateJob implements Job{
	public static byte[] deliverTransitRateLock = new byte[0];
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("交接及时率统计定时任务开始");
		synchronized(deliverTransitRateLock){
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult");
			PreparedStatement pst = null;
			WareService wareService = new WareService(dbOp);
			DeliverService deliverService = new DeliverService(IBaseService.CONN_IN_SERVICE,dbOp);
			try{
				//统计定时任务前一天的数据
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal=Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.DAY_OF_YEAR, -1);
				String statDate = sdf.format(cal.getTime());//统计日期
				String startTime = statDate + " 00:00:00";
				String endTime = statDate + " 23:59:59";
				//获取所有的启用中的快递公司列表
				List<DeliverCorpInfoBean> deliverList  = deliverService.getDeliverCorpInfoList("status=1",-1,-1,null);
				if(deliverList!=null && deliverList.size()>0){
					wareService.getDbOp().startTransaction();//事务开始
					for(int i=0;i<deliverList.size();i++){
						DeliverCorpInfoBean deliver = deliverList.get(i);
						//统计复核量
						StringBuilder checkCountSql = new StringBuilder();
						List<DeliverTransitRate> list = new ArrayList<DeliverTransitRate>();
						checkCountSql.append("select deliver,areano,count(DISTINCT(order_id)) amount from audit_package ")
									.append(" where check_datetime between '").append(startTime).append("' and '").append(endTime)
									.append("' and deliver=").append(deliver.getId()).append(" group by areano");
						ResultSet rs = dbOp.executeQuery(checkCountSql.toString());
						while(rs.next()){
							DeliverTransitRate bean = new DeliverTransitRate();
							bean.setDeliverId(rs.getInt("deliver"));
							bean.setAreaId(rs.getShort("areano"));
							bean.setCheckCount(rs.getInt("amount"));
							list.add(bean);
						}
						rs.close();
						if(list!=null && list.size()>0){
							for(int j=0;j<list.size();j++){
								DeliverTransitRate transitRate = list.get(j); 
								//统计当日交接订单量
								StringBuilder transitCountSql = new StringBuilder();
								transitCountSql.append("select ap.deliver deliver,count(DISTINCT(mbp.order_id)) amount from mailing_batch_package mbp")
								.append(" join mailing_batch mb on mbp.mailing_batch_code = mb.code join audit_package ap on mbp.order_id = ap.order_id")
								.append(" where mb.`status`=1 and ap.check_datetime between '").append(startTime).append("' and '").append(endTime)
								.append("' and ap.deliver=").append(deliver.getId()).append(" and mb.transit_datetime between'")
								.append(startTime).append("' and '").append(endTime).append("' and ap.areano=").append(transitRate.getAreaId());
								rs = dbOp.executeQuery(transitCountSql.toString());
								while(rs.next()){
									transitRate.setTransitCount(rs.getInt("amount"));
								}
								rs.close();
								//查询最晚交接时间
								String lastTransitTime = "";
								rs = dbOp.executeQuery("select lastest_transit_time from deliver_kpi where deliver_id=" + deliver.getId() + " and area_id=" + transitRate.getAreaId());
								if(rs.next()){
									lastTransitTime = rs.getString("lastest_transit_time");
								}
								if(lastTransitTime.length()>0){
									//统计当日按时交接订单量
									StringBuilder inTimeTransitCountSql = new StringBuilder();
									inTimeTransitCountSql.append("select ap.deliver deliver,count(DISTINCT(mbp.order_id)) amount from mailing_batch_package mbp")
									.append(" join mailing_batch mb on mbp.mailing_batch_code = mb.code join audit_package ap on mbp.order_id = ap.order_id")
									.append(" where mb.`status`=1 and ap.check_datetime between '").append(startTime).append("' and '").append(endTime)
									.append("' and ap.deliver=").append(deliver.getId()).append(" and mb.transit_datetime between'")
									.append(startTime).append("' and '").append(statDate).append(" ").append(lastTransitTime).append(":59' and ap.areano=")
									.append(transitRate.getAreaId());
									rs = dbOp.executeQuery(inTimeTransitCountSql.toString());
									while(rs.next()){
										transitRate.setIntimeTransitCount(rs.getInt("amount"));
									}
								}
							}
						}
						
						if(list.size()>0){
							StringBuilder insertSql = new StringBuilder();
							insertSql.append(" insert into deliver_transit_rate ");
							insertSql.append(" (deliver_id,area_id,date,check_count,transit_count,intime_transit_count,transit_rate,intime_transit_rate) ");
							insertSql.append(" values (?,?,?,?,?,?,?,?) ");
							pst = dbOp.getConn().prepareStatement(insertSql.toString());
							for(int j=0;j<list.size();j++){
								DeliverTransitRate transitRate = list.get(j); 
								pst.setInt(1, transitRate.getDeliverId());
								pst.setInt(2, transitRate.getAreaId());
								pst.setDate(3, new java.sql.Date(cal.getTime().getTime()));
								pst.setInt(4, transitRate.getCheckCount());
								pst.setInt(5, transitRate.getTransitCount());
								pst.setInt(6, transitRate.getIntimeTransitCount());
								if(transitRate.getCheckCount()!=0){
									pst.setFloat(7, Arith.div(transitRate.getTransitCount(), transitRate.getCheckCount(),4));
								}else{
									pst.setFloat(7, 0);
								}
								if(transitRate.getCheckCount()!=0){
									pst.setFloat(8, Arith.div(transitRate.getIntimeTransitCount(), transitRate.getCheckCount(),4));
								}else{
									pst.setFloat(8, 0);
								}
								pst.addBatch();
							}
							pst.executeBatch();
						}
					}
					wareService.getDbOp().commitTransaction();//提交事务
				}
				System.out.println("交接及时率统计定时任务结束");
			}catch (Exception e) {
				e.printStackTrace();
				wareService.getDbOp().rollbackTransaction();
			}finally{
				wareService.releaseAll();
			}
		}
	}

}
