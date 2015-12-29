package adultadmin.util.timedtask;

import java.util.HashMap;
import java.util.List;

import mmb.common.dao.CommonDao;
import mmb.common.dao.mappers.CommonMapper;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

import com.mmb.framework.support.SpringHandler;

public class TrunkOrderAddJob implements Job{	
	private static byte[] lock = new byte[0];
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		synchronized (lock) {
			System.out.println("新增干线单信息start:"+DateUtil.getNow());
			CommonDao commonMapper = SpringHandler.getBean(CommonMapper.class);
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", "distinct mb.id mailingBatchId,date_format(mb.transit_datetime, '%Y-%m-%d %H:%i:%s') transitTime,mb.code,"
					+ "te.trunk_id trunkCorpId,te.stock_area_id stockArea,te.deliver_id deliver,te.time,te.deliver_admin_id opUser,"
					+ "date_format(date_format(date_add(mb.transit_datetime,INTERVAL te.time HOUR),'%Y-%m-%d %H'), '%Y-%m-%d %H:%i:%s') expectTime");
			paramMap.put("table", "mailing_batch mb join mailing_batch_package mbp on mb.code=mbp.mailing_batch_code "
					+ "join trunk_effect te on mb.deliver=te.deliver_id and mb.area=te.stock_area_id and te.status=0 "
					+ "join trunk_corp_info toi on te.trunk_id=toi.id and toi.status=0");
			paramMap.put("condition", "not exists (select * from trunk_order tro where tro.code=mb.code) and mb.status=1 "
					+ "and date_format(mb.transit_datetime, '%Y-%m-%d')>=date_format('"+DateUtil.getBeforOneDay()+"', '%Y-%m-%d') "
					+ "and date_format(mb.transit_datetime, '%Y-%m-%d')<=date_format('"+DateUtil.getNowDateStr()+"', '%Y-%m-%d')");
			List<HashMap<String, Object>> trunkOrderLs = commonMapper.getCommonInfoCount(paramMap);
			if(trunkOrderLs!=null&&trunkOrderLs.size()>0){
				DbOperation dbOp = new DbOperation();
				dbOp.init(DbOperation.DB);
				String currentDate = DateUtil.getNow();
				try{
					for(HashMap<String, Object> map:trunkOrderLs){
						dbOp.startTransaction();
						StringBuilder sb = new StringBuilder("");
						sb.append("insert into trunk_order (code, mailing_batch_id,trunk_corp_id, stock_area, deliver,status,"
								+ "receive_time, node_time, time, expect_time, upd_time, op_user,sys_op_user ) values (");
						sb.append("'"+map.get("code")+"',");
						sb.append("'"+map.get("mailingBatchId")+"',");
						sb.append("'"+map.get("trunkCorpId")+"',");
						sb.append("'"+map.get("stockArea")+"',");
						sb.append("'"+map.get("deliver")+"',");
						sb.append("'1',");
						sb.append("'"+map.get("transitTime")+"',");
						sb.append("'"+map.get("transitTime")+"',");
						sb.append("'"+map.get("time")+"',");
						sb.append("'"+map.get("expectTime")+"',");
						sb.append("'"+currentDate+"',");
						sb.append("'"+map.get("opUser")+"',");
						sb.append("'0'");
						sb.append(")");
						dbOp.executeUpdate(sb.toString());
						sb = new StringBuilder("");
						sb.append("insert into trunk_order_info (trunk_order_id, node_time,status,op_user,sys_op_user) values (");
						sb.append("'"+dbOp.getLastInsertId()+"',");
						sb.append("'"+map.get("transitTime")+"',");
						sb.append("'1',");
						sb.append("'"+map.get("opUser")+"',");
						sb.append("'0'");
						sb.append(")");
						dbOp.executeUpdate(sb.toString());
						dbOp.commitTransaction();
					}
				}catch (Exception e) {
					e.printStackTrace();
					dbOp.rollbackTransaction();
				} finally {
					dbOp.release();
				}
			}
			System.out.println("新增干线单信息end:"+DateUtil.getNow());
		}
	}
}
