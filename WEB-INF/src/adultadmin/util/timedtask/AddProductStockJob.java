package adultadmin.util.timedtask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mmb.msg.TemplateMarker;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.action.vo.voOrder;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

import com.chinasms.sms.SenderSMS3;

// 添加商品库存定时任务
public class AddProductStockJob implements Job {
	public static byte[] messageLock = new byte[0];

	public void execute(JobExecutionContext context) throws JobExecutionException {
		synchronized (messageLock) {
			DbOperation dbOpSlave = new DbOperation();
			DbOperation dbOp = new DbOperation();
			dbOpSlave.init(DbOperation.DB_SLAVE);
			dbOp.init(DbOperation.DB);
			WareService service = new WareService(dbOpSlave);
			WareService service2 = new WareService(dbOp);
			try {
				System.out.println("AddProductStockJob  开始   "+DateUtil.getNow());
				service.addProductStockNoAreaList(dbOp);//获取product_stock集合
				System.out.println("AddProductStockJob  结束"+DateUtil.getNow());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service2.releaseAll();
				service.releaseAll();
			}
		}
	}
}
