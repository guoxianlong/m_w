package adultadmin.util.timedtask;

/**
 * 发货及时率统计
 */
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import mmb.rec.stat.bean.TempOrderEffectiveInfoBean;
import mmb.rec.stat.service.OrderSendOutEffectiveService;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class OrderSendOutEffectiveJob  implements Job {
	public static byte[] orderSendOutEffectiveLock = new byte[0];
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("订单发货时效统计任务开始");
		synchronized(orderSendOutEffectiveLock){
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult");
			WareService wareService = new WareService(dbOp);
			OrderSendOutEffectiveService orderSendOutEffectiveService = new OrderSendOutEffectiveService(IBaseService.CONN_IN_SERVICE, dbOp);
			try {
				// 首先，在当前得到前一天的时间
				Date dateNow = new Date();
				Date dateYesterday = null;
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(dateNow);
				calendar.add(calendar.DATE, -1);// 把日期往后增加一天.整数往后推,负数往前移动
				dateYesterday = calendar.getTime(); // 这个时间就是日期往后推一天的结果
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				String yesterdayString = formatter.format(dateYesterday);
				String startTime = yesterdayString + " 00:00:00";
				String endTime = yesterdayString + " 23:59:59";
				// 然后查出 之前一天的所有时间信息，和地区信息
				String condition = null;
				condition = " mbp.create_datetime between '"+startTime+"' and '"+endTime+"'";
				List<TempOrderEffectiveInfoBean> orderEffectiveInfoListAll = orderSendOutEffectiveService.getOrderEffectiveInfo(condition);
				//遍历所得到的列表，得到每个订单一个的，拥有最高商品总价的商品条目
				List<TempOrderEffectiveInfoBean> orderEffectiveInfoList = orderSendOutEffectiveService.getRepresentOrderProductEffectiveInfo(orderEffectiveInfoListAll);
				wareService.getDbOp().startTransaction();// 事务开始
				orderSendOutEffectiveService.calculateOrderSendOutEffectiveInfo(orderEffectiveInfoList,yesterdayString);
				wareService.getDbOp().commitTransaction();//事务提交
				System.out.println("订单发货时效统计任务结束");
			}catch (Exception e) {
				e.printStackTrace();
				System.out.println("订单发货时效统计任务异常");
				try {
					if( !wareService.getDbOp().getConn().getAutoCommit() ) {
						wareService.getDbOp().rollbackTransaction();
					}
				} catch (SQLException e1) {}
			} finally {
				wareService.releaseAll();
			}
		}
	}
	
}
