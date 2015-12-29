package adultadmin.util.timedtask;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mmb.rec.stat.bean.StockShareBean;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.db.DbOperation;

public class StockShareJob implements Job {
	public static byte[] stockShareLock = new byte[0];

	// 库存占比
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		System.out.println("库存占比JOB开始.....");
		synchronized (stockShareLock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB_SLAVE2);
			IProductStockService service = ServiceFactory
					.createProductStockService(IBaseService.CONN_IN_SERVICE,null);
			StringBuilder sb = new StringBuilder();
			
			sb.append(
					"select ps.area,plc.product_line_id , round(sum((ps.stock+ps.lock_count)*p.price5),2),count( DISTINCT p.id),sum(ps.stock+ps.lock_count) ")
					.append(" from product p,product_line_catalog plc,product_stock ps ")
					.append(" where p.id = ps.product_id and (p.parent_id1 = plc.catalog_id or p.parent_id2 = plc.catalog_id) and ps.area in(3,4) ")
					.append(" group by ps.area,plc.product_line_id");
			try {
				ResultSet rs = dbOp.executeQuery(
						sb.toString());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DAY_OF_YEAR, -1);
				String time = sdf.format(c.getTime());
				List<StockShareBean> list = new ArrayList<StockShareBean>();
				while (rs.next()) {
					StockShareBean bean = new StockShareBean();
					int area = rs.getInt(1);// 区域id
					bean.setArea(area);
					int productlineId = rs.getInt(2);// 产品线id
					bean.setProductLineId(productlineId);
					float price = rs.getFloat(3);// 总金额 精确到2位（可用量+锁定量）
					bean.setPrice(price);
					int skuCount = rs.getInt(4);// sku数量
					bean.setSkuCount(skuCount);
					int productCount = rs.getInt(5);// 商品数量
					bean.setProductCount(productCount);

					bean.setDate(time);// 时间
					list.add(bean);
				}
				service.getDbOp().startTransaction();
				for(StockShareBean bean:list){
					if(!service.addStockShare(bean)){
						service.getDbOp().rollbackTransaction();
						return;
					}
				}
				service.getDbOp().commitTransaction();
				System.out.println("库存占比JOB结束.....");
			} catch (Exception e) {
				service.getDbOp().rollbackTransaction();
				System.out.println("库存占比JOB异常.....");
				e.printStackTrace();
			} finally {
				dbOp.release();
				service.releaseAll();
			}
		}
	}
	public static void main(String[] args) {
		float a = 0.0f;
		System.out.println(a<=0);
		BigDecimal bd = new BigDecimal(0.00f);
		System.out.println(bd.floatValue());
	}
}
