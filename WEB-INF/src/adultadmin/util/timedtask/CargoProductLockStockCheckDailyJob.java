package adultadmin.util.timedtask;

/**
 * 发货及时率统计
 */
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.stat.bean.TempOrderEffectiveInfoBean;
import mmb.rec.stat.service.CargoLockCountStatService;
import mmb.rec.stat.service.OrderSendOutEffectiveService;
import mmb.rec.sys.bean.TempCargoLockCountInfoBean;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class CargoProductLockStockCheckDailyJob  implements Job {
	public static byte[] cargoProductLockStockCheckLock = new byte[0];
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("货位锁定库存检查定时任务开始");
		synchronized(cargoProductLockStockCheckLock){
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult_slave");
			DbOperation dbOp2 = new DbOperation();
			dbOp2.init("adult");
			WareService wareService = new WareService(dbOp);
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService stockService=ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			CargoLockCountStatService cargoLockCountStatService = new CargoLockCountStatService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
			CargoLockCountStatService cargoLockCountStatService2 = new CargoLockCountStatService(IBaseService.CONN_IN_SERVICE,dbOp2);
			try {
				Map<String,Integer> mayLockCargoStockMap = new HashMap<String,Integer>();
				//首先，查出所有可能锁定库存的所有的锁定量
				//1 得到调拨单的锁定List
				List<TempCargoLockCountInfoBean> list1 = cargoLockCountStatService.getStockExchangeLockList();
				mayLockCargoStockMap = cargoLockCountStatService.getSumLockCargoStockCountMap(mayLockCargoStockMap, list1);
				//2 得到订单发货可能锁定List
				List<TempCargoLockCountInfoBean> list2 = cargoLockCountStatService.getOrderStockOutLockList();
				mayLockCargoStockMap = cargoLockCountStatService.getSumLockCargoStockCountMap(mayLockCargoStockMap, list2);
				//3 得到报损单可能锁定List
				List<TempCargoLockCountInfoBean> list3 = cargoLockCountStatService.getBsOperationLockList();
				mayLockCargoStockMap = cargoLockCountStatService.getSumLockCargoStockCountMap(mayLockCargoStockMap, list3);
				//4 得到仓内作业可能锁定List
				List<TempCargoLockCountInfoBean> list4 = cargoLockCountStatService.getCargoOperationLockList();
				mayLockCargoStockMap = cargoLockCountStatService.getSumLockCargoStockCountMap(mayLockCargoStockMap, list4);
				//5 得到异常单可能的锁定List
				List<TempCargoLockCountInfoBean> list5 = cargoLockCountStatService.getAbnormalCargoStockLockList();
				mayLockCargoStockMap = cargoLockCountStatService.getSumLockCargoStockCountMap(mayLockCargoStockMap, list5);
				
				//得到待盘查的货位库存锁定量的相关信息。
				List<TempCargoLockCountInfoBean> list = cargoLockCountStatService.getCargoProductStockLockCountInfo();
				//寻找锁定量异常的货位
				cargoLockCountStatService2.getDbOp().startTransaction();
				boolean result = cargoLockCountStatService2.dealProblemCargoProductStockLockInfo(list, mayLockCargoStockMap);
				if( result  ) {
					cargoLockCountStatService2.getDbOp().commitTransaction();
				} else {
					cargoLockCountStatService2.getDbOp().rollbackTransaction();
				}
				System.out.println("货位锁定库存检查定时任务结束");
			}catch (Exception e) {
				e.printStackTrace();
				System.out.println("货位锁定库存检查定时任务异常");
				 boolean autoCommit = true;
				try {
					autoCommit = cargoLockCountStatService2.getDbOp().getConn().getAutoCommit();
				} catch (SQLException e1) {}
				if( !autoCommit) {
					cargoLockCountStatService2.getDbOp().rollbackTransaction();
				}
			} finally {
				wareService.releaseAll();
				cargoLockCountStatService2.releaseAll();
			}
		}
	}
	
}
