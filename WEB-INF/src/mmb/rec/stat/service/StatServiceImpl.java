package mmb.rec.stat.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import mmb.rec.stat.bean.SecondSortingStatBean;
import mmb.rec.stat.bean.SendOutOrderStatBean;
import mmb.rec.stat.bean.SortingStatBean;

import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class StatServiceImpl extends BaseServiceImpl {
	public StatServiceImpl(DbOperation dbOp){
		this.dbOp = dbOp;
	}

	public void sendOutOrderStat() throws SQLException {
		DbOperation dbOp_slave = new DbOperation();
		dbOp_slave.init(DbOperation.DB_SLAVE);
		String beforeDate = DateUtil.getBackFromDate(DateUtil.getNow(), 1);
		String dateTimeStart =beforeDate + " 00:00:00";
		String dateTimeEnd = beforeDate + " 23:59:59";
		String query = "SELECT plc.product_line_id,osp.stock_area,count(DISTINCT os.order_id),sum(osp.stockout_count),count(DISTINCT osp.product_id) "
				+ "from mailing_batch_package mbp join order_stock os ON mbp.order_id=os.order_id "
				+ "join order_stock_product osp on os.id=osp.order_stock_id join product p on osp.product_id=p.id "
				+ "join product_line_catalog plc on (p.parent_id1=plc.catalog_id or p.parent_id2=plc.catalog_id) "
				+ "WHERE mbp.create_datetime BETWEEN '" + dateTimeStart + "' AND '" + dateTimeEnd + "' GROUP BY osp.stock_area,plc.product_line_id";
		ResultSet rs = dbOp_slave.executeQuery(query);
		try {
			dbOp.startTransaction();
			while(rs.next()){
				SendOutOrderStatBean bean = new SendOutOrderStatBean();
				bean.setProductLineId(rs.getInt(1));
				bean.setArea(rs.getInt(2));
				bean.setDate(beforeDate);
				bean.setOrderCount(rs.getInt(3));
				bean.setProductCount(rs.getInt(4));
				bean.setSkuCount(rs.getInt(5));
				if(!this.addSendOutOrderStat(bean)){
					dbOp.rollbackTransaction();
					return;
				}
			}
			dbOp.commitTransaction();
		} catch (SQLException e) {
			dbOp.rollbackTransaction();
			e.printStackTrace();
		}finally{
			if(rs != null){
				rs.close();
			}
			dbOp_slave.release();
		}
	}
	public void sortingStat() throws SQLException {
		DbOperation dbOp_slave = new DbOperation();
		dbOp_slave.init(DbOperation.DB_SLAVE);
		String beforeDate = DateUtil.getBackFromDate(DateUtil.getNow(), 1);
		String dateTimeStart =beforeDate + " 00:00:00";
		String dateTimeEnd = beforeDate + " 23:59:59";
		String query = "SELECT storage,count(id) FROM sorting_batch_group "
				+ "WHERE receive_datetime BETWEEN '" + dateTimeStart + "' AND '" + dateTimeEnd + "' GROUP BY storage";
		ResultSet rs = dbOp_slave.executeQuery(query);
		try {
			dbOp.startTransaction();
			while(rs.next()){
				SortingStatBean bean = new SortingStatBean();
				bean.setArea(rs.getInt(1));
				bean.setDate(beforeDate);
				bean.setSortingCount(rs.getInt(2));
				if(!this.addSortingStat(bean)){
					dbOp.rollbackTransaction();
					return;
				}
			}
			dbOp.commitTransaction();
		} catch (SQLException e) {
			dbOp.rollbackTransaction();
			e.printStackTrace();
		}finally{
			if(rs != null){
				rs.close();
			}
		}
	}
	private boolean addSortingStat(SortingStatBean bean) {
		return addXXX(bean, "sorting_stat");
	}

	public void secondSortingStat() throws SQLException {
		DbOperation dbOp_slave = new DbOperation();
		dbOp_slave.init(DbOperation.DB_SLAVE);
		String beforeDate = DateUtil.getBackFromDate(DateUtil.getNow(), 1);
		String dateTimeStart =beforeDate + " 00:00:00";
		String dateTimeEnd = beforeDate + " 23:59:59";
		String query = "SELECT sbg.storage,count(DISTINCT sbo.order_id),sum(sbop.count),count(DISTINCT sbop.product_id) "
				+"FROM sorting_batch_group sbg "
				+"join sorting_batch_order sbo on sbg.id=sbo.sorting_group_id "
				+"join sorting_batch_order_product sbop on sbg.id=sbop.sorting_batch_group_id "
				+ "WHERE sbg.receive_datetime2 BETWEEN '" + dateTimeStart + "' AND '" + dateTimeEnd + "' GROUP BY sbg.storage";
		ResultSet rs = dbOp_slave.executeQuery(query);
		try {
			dbOp.startTransaction();
			while(rs.next()){
				SecondSortingStatBean bean = new SecondSortingStatBean();
				bean.setArea(rs.getInt(1));
				bean.setOrderCount(rs.getInt(2));
				bean.setProductCount(rs.getInt(3));
				bean.setDate(beforeDate);
				bean.setSkuCount(rs.getInt(4));
				if(!this.addSecondSortingStat(bean)){
					dbOp.rollbackTransaction();
					return;
				}
			}
			dbOp.commitTransaction();
		} catch (SQLException e) {
			dbOp.rollbackTransaction();
			e.printStackTrace();
		}finally{
			if(rs != null){
				rs.close();
			}
			dbOp_slave.release();
		}
	}

	private boolean addSecondSortingStat(SecondSortingStatBean bean) {
		return addXXX(bean, "second_sorting_stat");
	}

	private boolean addSendOutOrderStat(SendOutOrderStatBean bean) {
		return addXXX(bean, "send_out_order_stat");
	}
	
}
