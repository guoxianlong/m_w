package adultadmin.util.timedtask;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mmb.stock.stat.DeliverCorpInfoBean;
import mmb.stock.stat.DeliverService;
import mmb.tms.model.DeliverMailingRate;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.service.infc.IBaseService;
import adultadmin.util.Arith;
import adultadmin.util.db.DbOperation;

/**
 * 投递及时率统计定时任务
 * @author 李宁
 * @date 2014-4-3
 */
public class DeliverMailingRateJob implements Job{
	public static byte[] deliverMailingRateLock = new byte[0];
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("投递及时率统计定时任务开始");
		synchronized (deliverMailingRateLock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult");
			WareService wareService = new WareService(dbOp);
			DeliverService deliverService = new DeliverService(IBaseService.CONN_IN_SERVICE,dbOp);
			PreparedStatement pst = null;
			try{
				//获取所有的启用中的快递公司列表
				List<DeliverCorpInfoBean> deliverList  = deliverService.getDeliverCorpInfoList("status=1",-1,-1,null);
				if(deliverList!=null && deliverList.size()>0){
					wareService.getDbOp().startTransaction();//事务开始
					for(int i=0;i<deliverList.size();i++){
						DeliverCorpInfoBean deliver = deliverList.get(i);
						//发货仓id为3,4
						List<DeliverMailingRate> list = new ArrayList<DeliverMailingRate>();
						DeliverMailingRate bean = getDeliverMailingRate(dbOp, deliver,3);
						if(bean!=null){
							list.add(bean);
						}
						bean = getDeliverMailingRate(dbOp, deliver,4);
						if(bean!=null){
							list.add(bean);
						}
						if(list!=null && list.size()>0){
							StringBuilder insertSql = new StringBuilder();
							insertSql.append(" insert into deliver_mailing_rate ");
							insertSql.append(" (deliver_id,area_id,date,transit_count,intime_mailing_count,intime_mailing_rate) ");
							insertSql.append(" values (?,?,?,?,?,?) ");
							pst = dbOp.getConn().prepareStatement(insertSql.toString());
							for(int j=0;j<list.size();j++){
								DeliverMailingRate mailingRate = list.get(j); 
								pst.setInt(1, mailingRate.getDeliverId());
								pst.setInt(2, mailingRate.getAreaId());
								pst.setDate(3, new java.sql.Date(mailingRate.getDate().getTime()));
								pst.setInt(4, mailingRate.getTransitCount());
								pst.setInt(5, mailingRate.getIntimeMailingCount());
								if(mailingRate.getTransitCount()!=0){
									pst.setFloat(6, Arith.div(mailingRate.getIntimeMailingCount(), mailingRate.getTransitCount(), 4));
								}else{
									pst.setFloat(6, 0);
								}
								pst.addBatch();
							}
							pst.executeBatch();
						}
					}
					wareService.getDbOp().commitTransaction();//提交事务
				}
			}catch (Exception e) {
				e.printStackTrace();
				wareService.getDbOp().rollbackTransaction();
			}finally{
				wareService.releaseAll();
			}
		}
		System.out.println("投递及时率统计定时任务结束");
	}

	private DeliverMailingRate getDeliverMailingRate(DbOperation dbOp, DeliverCorpInfoBean deliver,int area) throws SQLException {
		//查询投递时效
		String mailingTime = "";
		ResultSet rs = dbOp.executeQuery("select mailing_time from deliver_kpi where deliver_id=" + deliver.getId() + " and area_id=" + area);
		if(rs.next()){
			mailingTime = rs.getString("mailing_time");
		}
		rs.close();
		if(mailingTime.length()>0){
			//获取统计日期 =（当前时间-揽收时效）的前一天
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal=Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.HOUR, -Integer.valueOf(mailingTime).intValue());
			cal.add(Calendar.DAY_OF_YEAR, -1);
			String statDate = sdf.format(cal.getTime());//统计日期
			String startTime = statDate + " 00:00:00";
			String endTime = statDate + " 23:59:59";
			
			DeliverMailingRate bean = new DeliverMailingRate();
			bean.setDeliverId(deliver.getId());
			bean.setAreaId((short)area);
			bean.setDate(cal.getTime());
			//统计当日交接订单总量
			StringBuilder transitCountSql = new StringBuilder();
			transitCountSql.append("select mb.area area,count(DISTINCT(mbp.order_id)) amount from mailing_batch_package mbp ")
			.append("join mailing_batch mb on mbp.mailing_batch_id = mb.id where mb.`status`=1 and mb.deliver=").append(deliver.getId())
			.append(" and mb.transit_datetime between '").append(startTime).append("' and '").append(endTime).append("' and mb.area=").append(area);
			rs = dbOp.executeQuery(transitCountSql.toString());
			if(rs.next()){
				bean.setTransitCount(rs.getInt("amount"));
			}
			rs.close();
			//统计揽收即时的订单总量
			StringBuilder collectCountSql = new StringBuilder();
			collectCountSql.append("select count(DISTINCT(ap.order_id)) amount from audit_package ap join mailing_batch_package mbp on ap.order_id=mbp.order_id ")
			.append("join mailing_batch mb on mb.code=mbp.mailing_batch_code and mb.transit_datetime BETWEEN '").append(startTime).append("' and '")
			.append(endTime).append("' join deliver_order d on d.order_id=ap.order_id join deliver_order_info doi on doi.deliver_id=d.id ")
			.append("and doi.deliver_state in(5,6,7) and FROM_UNIXTIME(doi.add_time/1000,'%Y-%m-%d %H:%i:%s') < DATE_ADD(mb.transit_datetime,INTERVAL ")
			.append(Integer.valueOf(mailingTime).intValue()).append(" HOUR) where ap.areano=").append(area).append(" and ap.deliver=")
			.append(deliver.getId());
			rs = dbOp.executeQuery(collectCountSql.toString());
			if(rs.next()){
				bean.setIntimeMailingCount(rs.getInt("amount"));
			}
			return bean;
		}else{
			return null;
		}
	}

}
