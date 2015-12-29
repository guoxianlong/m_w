package adultadmin.util.timedtask;

import java.text.SimpleDateFormat;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.bean.cargo.CargoOperLogBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;
import cache.CargoOperationProcessCache;

// 货位作业单时效修改任务
public class CargoOperationEffectTimeJob  implements Job {
	
	public static byte[] effectTimeLock = new byte[0];
	public List processList=CargoOperationProcessCache.processList;//缓存中的货位作业单流程列表
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		synchronized(effectTimeLock){
			DbOperation dbOpSlave = new DbOperation();
			dbOpSlave.init("adult_slave");
			ICargoService serviceSlave = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOpSlave);
			DbOperation dbOp = new DbOperation();
			dbOp.init();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
			try {
				service.getDbOp().startTransaction();
				String startTime = DateUtil.getBackFromDate(DateUtil.getNowDateStr(), 3);
				//需要时效设置的作业单，包括生成阶段，交接阶段，作业结束的待复核
				List cargoOperationList=serviceSlave.getCargoOperationList("effect_status = 0 and status > 0 and create_datetime >= '"+startTime+"'", -1, -1, null);
				
				for(int i=0;i<cargoOperationList.size();i++){
					CargoOperationBean cargoOperation=(CargoOperationBean)cargoOperationList.get(i);
					
					String lastOperationDatetime=cargoOperation.getLastOperateDatetime();//最后操作时间
					String now=DateUtil.getNow();
					
					//缓存中的相应流程
					CargoOperationProcessBean process=(CargoOperationProcessBean)processList.get(cargoOperation.getStatus()-1);
					int effectTime=process.getEffectTime();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					if(lastOperationDatetime==null){
						continue;
					}
					long date1=sdf.parse(lastOperationDatetime).getTime();
					long date2=sdf.parse(now).getTime();
					long date3=effectTime*60*1000;
					if(date1+date3<date2){//已超时
						service.updateCargoOperation("effect_status = 1", "id = "+cargoOperation.getId());
						
						//该作业单日志的最后一条记录
						CargoOperLogBean cargoOperLog=service.getCargoOperLog("oper_id="+cargoOperation.getId()+" order by id desc limit 1");
						if(cargoOperLog!=null){
							service.updateCargoOperLog("effect_time=1", "id="+cargoOperLog.getId());
						}
					}
				}
				service.getDbOp().commitTransaction();
			}catch (Exception e) {
				e.printStackTrace();
				service.getDbOp().rollbackTransaction();
			} finally {
				service.releaseAll();
				serviceSlave.releaseAll();
			}
		}
	}
}
