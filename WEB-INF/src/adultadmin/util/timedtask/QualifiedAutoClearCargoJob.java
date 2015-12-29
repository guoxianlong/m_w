package adultadmin.util.timedtask;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.action.vo.voProduct;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoInfoLogBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;
/**
 * 
 * 作者：朱爱林
 * 时间：2013-10-14
 * 说明：合格库自动清理货位
 */
public class QualifiedAutoClearCargoJob implements Job {
	public static byte[] cargoinfoLock = new byte[0];
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("合格库自动清理货位定时任务开始...");
		//用于查询的
		DbOperation dbOp2 = new DbOperation();
		dbOp2.init("adult_slave");
		WareService wareService2 = new WareService(dbOp2);
		ICargoService service2 = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService2.getDbOp());
		//用于清空货位,添加日志的
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StringBuilder sb = new StringBuilder();
		CargoInfoBean ciBean=null;
		try{
			//货位表与库存表联查，条件是合格库，增城、无锡，并且stock_count+stock_lock_count = 0(针对一个product_id)
			sb.append(" ci.stock_type =")
			.append(CargoInfoBean.STOCKTYPE_QUALIFIED)
			.append(" and ci.status = ").append(CargoInfoBean.STATUS0) //使用中的
			.append(" and ci.area_id in(3,4) and cps.stock_count+cps.stock_lock_count=0");
//			ResultSet rs = dbSlave.executeQuery(sb.toString());
			List list = service2.getCargoAndProductStockList(sb.toString(),-1,-1,null);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			String endTime = sdf.format(calendar.getTime())+" 23:59:59";
			calendar.add(Calendar.DAY_OF_YEAR, -30);
			String startTime  = sdf.format(calendar.getTime())+" 00:00:00";
			int cargoId = 0;
			int productId = 0;
			if(list!=null&&list.size()>0){
				for(int i=0;i<list.size();i++){
					CargoProductStockBean bean = (CargoProductStockBean) list.get(i);
					cargoId = bean.getCargoId();
					productId = bean.getProductId();
					
					service.getDbOp().startTransaction();
					//查询前30天+今天的进销存卡片总和  stockId
					int totalCount = service2.getCargoStockCardCount(" stock_id = "+bean.getId()+" and create_datetime>='"+startTime+"' and create_datetime<='"+endTime+"'");
					if(totalCount!=0){
						System.out.println("前31天的进销存卡片数量没有变化！");
						service.getDbOp().rollbackTransaction();
						continue;
					}
					voProduct product=wareService.getProduct(productId);//商品
					ciBean=service2.getCargoInfo("id="+cargoId);
					
					int count=0;
					ResultSet rs2=dbOp2.executeQuery("select count(distinct co.id) from cargo_operation co join cargo_operation_cargo coc on coc.oper_id=co.id where co.status in(1,2,3,4,5,6,10,11,12,13,14,15,19,20,21,22,23,24,28,29,30,31,32,33,37,38,39,40,41,42) and co.effect_status < 3 and coc.product_id="+product.getId()+" and (coc.in_cargo_whole_code='"+ciBean.getWholeCode()+"' or coc.out_cargo_whole_code='"+ciBean.getWholeCode()+"')");
					while(rs2.next()){
						count=rs2.getInt(1);
					}
					rs2.close();
					if(count!=0){
						System.out.println("存在与该货位相关的货位作业单，不能清空货位！");
						service.getDbOp().rollbackTransaction();
						continue;
					}
					service.deleteCargoProductStock("cargo_id="+cargoId+" and product_id="+productId);
					CargoInfoLogBean logBean=new CargoInfoLogBean();//操作记录
					logBean.setCargoId(cargoId);
					logBean.setOperDatetime(DateUtil.getNow());
				logBean.setOperAdminId(0);
				logBean.setOperAdminName("");
					String remark="";
					if(service.getCargoProductStockCount("cargo_id="+cargoId)==0){//刚刚删除了该货位的最后一个商品库存记录
						service.updateCargoInfo("status=1", "id="+cargoId);
						remark="清空货位：";
						remark+="商品";
						remark+=product==null?"null":product.getCode();
						remark+="，";
						remark+="货位状态（使用中-未使用）";
						logBean.setRemark(remark);
						service.addCargoInfoLog(logBean);
					}else{
						remark="清空货位：";
						remark+="商品";
						remark+=product==null?"null":product.getCode();
						logBean.setRemark(remark);
						service.addCargoInfoLog(logBean);
					}
					service.getDbOp().commitTransaction();
				}
			}
		}catch(Exception e){
			System.out.println("合格库自动清理货位定时任务异常...");
			service.getDbOp().rollbackTransaction();
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
			System.out.println("合格库自动清理货位定时任务结束...");
		}
	}
	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -30);
		Date dd = calendar.getTime();
		System.out.println(sdf.format(dd));
	}
}
