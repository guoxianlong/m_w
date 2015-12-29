package mmb.stock.stat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.db.DbOperation;

public class SortingInfoService extends BaseServiceImpl{
	public SortingInfoService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public SortingInfoService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	// 分拣批次
	public boolean addSortingBatchInfo(SortingBatchBean bean) {
		return addXXX(bean, "sorting_batch");
	}

	public ArrayList getSortingBatchList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "sorting_batch", "mmb.stock.stat.SortingBatchBean");
	}

	public int getSortingBatchCount(String condition) {
		return getXXXCount(condition, "sorting_batch", "id");
	}

	public SortingBatchBean getSortingBatchInfo(String condition) {
		return (SortingBatchBean) getXXX(condition, "sorting_batch",
		"mmb.stock.stat.SortingBatchBean");
	}

	public boolean updateSortingBatchInfo(String set, String condition) {
		return updateXXX(set, condition, "sorting_batch");
	}

	public boolean deleteSortingBatchInfo(String condition) {
		return deleteXXX(condition, "sorting_batch");
	}
	// 分拣波次
	public boolean addSortingBatchGroupInfo(SortingBatchGroupBean bean) {
		return addXXX(bean, "sorting_batch_group");
	}

	public ArrayList getSortingBatchGroupList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "sorting_batch_group", "mmb.stock.stat.SortingBatchGroupBean");
	}

	public int getSortingBatchGroupCount(String condition) {
		return getXXXCount(condition, "sorting_batch_group", "id");
	}

	public SortingBatchGroupBean getSortingBatchGroupInfo(String condition) {
		return (SortingBatchGroupBean) getXXX(condition, "sorting_batch_group",
		"mmb.stock.stat.SortingBatchGroupBean");
	}

	public boolean updateSortingBatchGroupInfo(String set, String condition) {
		return updateXXX(set, condition, "sorting_batch_group");
	}

	public boolean deleteSortingBatchGroupInfo(String condition) {
		return deleteXXX(condition, "sorting_batch_group");
	}
	// 分拣批次中的订单
	public boolean addSortingBatchOrderInfo(SortingBatchOrderBean bean) {
		return addXXX(bean, "sorting_batch_order");
	}

	public ArrayList getSortingBatchOrderList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "sorting_batch_order", "mmb.stock.stat.SortingBatchOrderBean");
	}

	public int getSortingBatchOrderCount(String condition) {
		return getXXXCount(condition, "sorting_batch_order", "id");
	}

	public SortingBatchOrderBean getSortingBatchOrderInfo(String condition) {
		return (SortingBatchOrderBean) getXXX(condition, "sorting_batch_order",
		"mmb.stock.stat.SortingBatchOrderBean");
	}

	public boolean updateSortingBatchOrderInfo(String set, String condition) {
		return updateXXX(set, condition, "sorting_batch_order");
	}

	public boolean deleteSortingBatchOrderInfo(String condition) {
		return deleteXXX(condition, "sorting_batch_order");
	}
	
	// 分拣中的货位商品异常
	public boolean addSortingBatchGroupExceptionInfo(SortingBatchGroupExceptionBean bean) {
		return addXXX(bean, "sorting_batch_group_exception");
	}

	public ArrayList getSortingBatchGroupExceptionList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "sorting_batch_group_exception", "mmb.stock.stat.SortingBatchGroupExceptionBean");
	}

	public int geSortingBatchGroupExceptionCount(String condition) {
		return getXXXCount(condition, "sorting_batch_group_exception", "id");
	}

	public SortingBatchOrderBean getSortingBatchGroupExceptionInfo(String condition) {
		return (SortingBatchOrderBean) getXXX(condition, "sorting_batch_group_exception",
		"mmb.stock.stat.SortingBatchGroupExceptionBean");
	}

	public boolean updateSortingBatchGroupExceptionInfo(String set, String condition) {
		return updateXXX(set, condition, "sorting_batch_group_exception");
	}

	public boolean deleteSortingBatchGroupExceptionInfo(String condition) {
		return deleteXXX(condition, "sorting_batch_group_exception");
	}
	
	public ArrayList getSortingBatchOrderListByAddress(String condition, int index, int count, String orderBy) {
		//return getXXXList(condition, index, count, orderBy, "sorting_batch_order", "mmb.stock.stat.SortingBatchOrderBean");
		ArrayList list = new ArrayList();
		DbOperation dbOp = getDbOp();
		 //构建查询语句
		StringBuilder sql = new StringBuilder(40);
		sql.append("select sbo.*,u.address from sorting_batch_order sbo join user_order u on sbo.order_id =u.id where ");
		if(condition==null || condition.length()==0){
			condition=" 1=1 ";
		}
        if (orderBy != null) {
        	condition += " order by " + orderBy;
        }
        condition = DbOperation.getPagingQuery(condition, index, count);
        sql.append(condition);
        ResultSet rs = dbOp.executeQuery(sql.toString());
		try{
			if(rs.next()){
				 
				
				
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			this.release(dbOp);
		}
		
		return list;
	}
	
	public boolean getOrderStatus(String orderCode){
		boolean flag = false;
		DbOperation dbOp = getDbOp();
		String sql = "select uo.status=6 from user_order uo where uo.code='"+orderCode+"'";
		ResultSet rs = dbOp.executeQuery(sql);
		try{
			if(rs.next()){		 
				if(rs.getBoolean(1)){
					flag = true;
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			this.release(dbOp);
		}
		return flag;
	}
	public SortingBatchOrderProductBean getSortingBatchOrderProducInfo(String condition) {
		return (SortingBatchOrderProductBean) getXXX(condition, "sorting_batch_order_product",
		"mmb.stock.stat.SortingBatchOrderProductBean");
	}
	public ArrayList getSortingBatchOrderProductList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "sorting_batch_order_product", "mmb.stock.stat.SortingBatchOrderProductBean");
	}
	public boolean deleteSortBatchOrderProduct(String condition){
		return deleteXXX(condition,"sorting_batch_order_product");
	}
	
	/**
	 * 查询Amazon订单编号 用的方法
	 * @param dbOp
	 * @param orderCode
	 * @return
	 */
	public String getThirdCode(int orderId) {
		String result = "";
		DbOperation dbOp = getDbOp();
		String amazonSql = "select pop_order_id from user_order_pop where order_id='"+orderId+"'";
		ResultSet amazonRs = dbOp.executeQuery(amazonSql);
		try {
			if (amazonRs.next()) {
				result = amazonRs.getString(1);
			}
		} catch(SQLException e){
			e.printStackTrace();
		}finally{
			this.release(dbOp);
		}
		return result;
	}
}
