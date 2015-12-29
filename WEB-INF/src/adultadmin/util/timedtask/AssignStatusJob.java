package adultadmin.util.timedtask;

import java.util.List;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import adultadmin.bean.stock.MailingBatchPackageBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

// 包裹所处于未分配状态超过48小时，则其状态被修改为投递超时
public class AssignStatusJob implements Job {

	public static byte[] messageLock = new byte[0];

	public void execute(JobExecutionContext context) throws JobExecutionException {
		synchronized (messageLock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult");
			DbOperation dbOp1 = new DbOperation();
			dbOp1.init("adult_slave");
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			IStockService service1 = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp1);
			try {
				List list = service1.getMailingBatchPackageList("mailing_status in (1,2)", -1, -1, "id");
				service.getDbOp().startTransaction();
				for (int i = 0; i < list.size(); i++) {
					MailingBatchPackageBean bean = (MailingBatchPackageBean) list.get(i);
					// 判断入库时间超过48小时但是状态仍未未分配的包裹状态被改成投递超时
					if (DateUtil.parseDate(DateUtil.getNow(), "yyyy-mm-dd HH:mm:ss").getTime() / (1000*60*60) - DateUtil.parseDate(bean.getStockInDatetime(), "yyyy-mm-dd HH:mm:ss").getTime() / (1000*60*60) > 48) {
						service.updateMailingBatchPackage("mailing_status=4", "id=" + bean.getId());
					}
				}
				service.getDbOp().commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				service.getDbOp().rollbackTransaction();
			} finally {
				service.releaseAll();
				service1.releaseAll();
			}
		}
	}
}
