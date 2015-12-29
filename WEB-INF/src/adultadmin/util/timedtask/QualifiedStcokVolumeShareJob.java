package adultadmin.util.timedtask;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.rec.stat.bean.QualifiedStockVolumeShareBean;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.db.DbOperation;

public class QualifiedStcokVolumeShareJob implements Job{
	public static byte[] stockShareLock = new byte[0];
	//合格库容积率
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		synchronized (stockShareLock) {
			WareService wareService = new WareService();
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp(DbOperation.DB_SLAVE));
			IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp(DbOperation.DB));
			StringBuilder sb = new StringBuilder();
			try{
				System.out.println("合格库容积率JOB开始.........");
				//获取合格库混合区使用中货位的体积
				sb.append("select ci.area_id, sum(ci.length*ci.width*ci.high) from cargo_info ci")
					.append(" where ci.area_id in(3,4) AND ci.stock_type=").append(CargoInfoBean.STOCKTYPE_QUALIFIED)
					.append(" and ci.store_type=").append(CargoInfoBean.STORE_TYPE4)
					.append(" and ci.status=").append(CargoInfoBean.STATUS0)
					.append(" group by ci.area_id");
				ResultSet rs = cargoService.getDbOp().executeQuery(sb.toString());
				Map<Integer,Double> map = new HashMap<Integer,Double>();
				while(rs.next()){
					Integer area_id = rs.getInt(1);
					double stock_volume = rs.getBigDecimal(2).doubleValue();//所有货位体积
					map.put(area_id,stock_volume);
				}
				
				sb.delete(0, sb.length());
				//获取合格库混合区使用中货位下的商品体积
				sb.append("select ci.area_id,plc.product_line_id,pwp.length*pwp.width*pwp.height*(sum(cps.stock_count+cps.stock_lock_count)) ")
					.append(" from product_ware_property pwp ,cargo_product_stock cps ,cargo_info ci,product p,product_line_catalog plc ")
					.append(" where pwp.product_id = cps.product_id and ci.id = cps.cargo_id and ci.area_id in(3,4) ")
					.append(" AND ci.stock_type=").append(CargoInfoBean.STOCKTYPE_QUALIFIED)
					.append(" and ci.store_type=").append(CargoInfoBean.STORE_TYPE4)
					.append(" and ci.status=").append(CargoInfoBean.STATUS0)
					.append(" and pwp.product_id = p.id and (p.parent_id1 = plc.catalog_id or p.parent_id2 = plc.catalog_id) ")
					.append(" GROUP BY ci.area_id,plc.product_line_id");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Calendar c = Calendar.getInstance();
					c.add(Calendar.DAY_OF_YEAR, -1);
					String time = sdf.format(c.getTime());
					ResultSet rs2 = cargoService.getDbOp().executeQuery(sb.toString());
					List<QualifiedStockVolumeShareBean> list = new ArrayList<QualifiedStockVolumeShareBean>();
					while(rs2.next()){
						QualifiedStockVolumeShareBean bean = new QualifiedStockVolumeShareBean();
						Integer area = rs2.getInt(1);//区域id
						bean.setArea(area);
						Integer productlineId = rs2.getInt(2);//产品线id
						bean.setProductLineId(productlineId);
						
						Double productVolume = (Double)rs2.getObject(3);//产品线下商品体积
						Double stockVolume = map.get(area);//货位体积
//						float share = (pp.divide(ss, 4, BigDecimal.ROUND_HALF_DOWN).floatValue())*100;//容积比
						bean.setProductLineProductVolume(productVolume);
						bean.setAreaStockVolume(stockVolume);
						bean.setDate(time);//时间
						list.add(bean);
					}
					service.getDbOp().startTransaction();
					for (QualifiedStockVolumeShareBean bean : list) {
						if(!service.addQualifiedStockVolumeShareBean(bean)){
							service.getDbOp().rollbackTransaction();
							return;
						}
					}
					service.getDbOp().commitTransaction();
					System.out.println("合格库容积率JOB结束.........");
			}catch(Exception e){
				service.getDbOp().rollbackTransaction();
				System.out.println("合格库容积率JOB异常.........");
				e.printStackTrace();
			}finally{
				wareService.releaseAll();
			}
		}
	}
}
