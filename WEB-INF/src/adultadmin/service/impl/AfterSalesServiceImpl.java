/*
 * Created on 2007-1-24
 *
 */
package adultadmin.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import adultadmin.bean.afterSales.AfterSaleCostListBean;
import adultadmin.bean.afterSales.AfterSaleNifferRecordBean;
import adultadmin.bean.afterSales.AfterSaleOperatingLogBean;
import adultadmin.bean.afterSales.AfterSaleOrderBean;
import adultadmin.bean.afterSales.AfterSaleOrderProduct;
import adultadmin.bean.afterSales.AfterSaleRefundOrderBean;
import adultadmin.service.infc.IAfterSalesService;
import adultadmin.util.db.DbOperation;

/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2010-1-7
 * 
 * 说明：售后数据库操作
 */
public class AfterSalesServiceImpl extends BaseServiceImpl implements IAfterSalesService {
	private final static String FITTINGS_PRODUCTS = "fittings_product";
	private final static String AFTER_SALE_FITTINGS_COST = "after_sale_fittings_cost";
	private final static String FITTINGS_BEAN_URL = "adultadmin.bean.afterSales.FittingsBean";
	private final static String AFTER_SALE_COST = "after_sale_cost";
	private final static String AFTER_SALE_COST_BEAN_URL = "adultadmin.bean.afterSales.AfterSaleCostBean";
	public AfterSalesServiceImpl(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

//	public AfterSalesServiceImpl() {
//		this.useConnType = CONN_IN_SERVICE;
//	}

	private static String afterSaleOrderTableName = "after_sale_order";
	private static String customerOpinionRecordTableName = "customer_opinion_record";
	private static String problemAndSolverRecordTableName = "problem_and_solver_record";
	private static String complaintTypeRecordTableName = "complaint_type_record";
	private static String afterSaleRefundOrderTableName = "after_sale_refund_order";
	private static String afterSaleRefundOrderProductTableName = "after_sale_refund_order_product";
	private static String afterSaleOperatingLogTableName = "after_sale_operating_log";
	private static String nifferRecordTableName = "after_sale_niffer_record";

	public boolean addAfterSaleOrder(AfterSaleOrderBean afterSaleOrder) {
		return addXXX(afterSaleOrder, afterSaleOrderTableName);
	}

	public boolean deleteAfterSaleOrder(String condition) {
		return deleteXXX(condition, afterSaleOrderTableName);
	}

	public AfterSaleOrderBean getAfterSaleOrder(String condition) {
		return (AfterSaleOrderBean) getXXX(condition, afterSaleOrderTableName,
				"adultadmin.bean.afterSales.AfterSaleOrderBean");
	}

	public int getAfterSaleOrderCount(String condition) {
		return getXXXCount(condition, afterSaleOrderTableName, "id");
	}

	public List getAfterSaleOrderList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, afterSaleOrderTableName,
				"adultadmin.bean.afterSales.AfterSaleOrderBean");
	}

	public int getAfterSaleOrderCountByProduct(String condition) {
		DbOperation dbOp = this.getDbOp();
		ResultSet rs =null;
		int count=0;
		try{
			rs = dbOp.executeQuery(condition);
			if(rs.next()){
				count=rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.release(dbOp);
		}
		return count;
	}

	public List getAfterSaleOrderListByProduct(String condition, int index, int count, String orderBy) {
		DbOperation dbOp = this.getDbOp();
		if (orderBy != null) {
			condition += " order by " + orderBy;
        }
		condition = DbOperation.getPagingQuery(condition, index, count);
        
		ResultSet rs =null;
		List list = new ArrayList();
		try{
			rs = dbOp.executeQuery(condition);
			while(rs.next()){
				AfterSaleOrderBean bean = new AfterSaleOrderBean();
				bean.setId(rs.getInt("id"));
				bean.setAfterSaleOrderCode(rs.getString("after_sale_order_code"));
				bean.setOrderId(rs.getInt("order_id"));
				bean.setOrderCode(rs.getString("order_code"));
				bean.setProductCode(rs.getString("productcodes"));
				bean.setProductNames(rs.getString("productnames"));
				bean.setCustomerName(rs.getString("customer_name"));
				bean.setCustomerPhone(rs.getString("customer_phone"));
				bean.setCreatorName(rs.getString("creator_name"));
				bean.setCreateTime(rs.getString("create_time"));
				bean.setLastOperateTime(rs.getString("last_operate_time"));
				bean.setLastOperatorName(rs.getString("last_operator_name"));
				bean.setProductReceiveTime(rs.getString("product_receive_time"));
				bean.setRemittanceTime(rs.getString("remittance_time"));
				bean.setStatus(rs.getInt("status"));
				bean.setProductLineNames(rs.getString("linename"));
				bean.setProblemDescription(rs.getString("problem_description"));
				bean.setComplaintTypeId(rs.getInt("complaint_type_id"));
				bean.setCustomerAddress(rs.getString("customer_address"));
				bean.setProductCode(bean.getProductCode()!=null?bean.getProductCode().replaceAll(",", "<br />"):"");
				bean.setProductNames(bean.getProductCode()!=null?bean.getProductNames().replaceAll(",", "<br />"):"");
				bean.setProductLineNames(bean.getProductLineNames()!=null?bean.getProductLineNames().replaceAll(",", "<br />"):"");
				 
				list.add(bean);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			this.release(dbOp);
		}
		
		return list;
	}
	
	public boolean updateAfterSaleOrder(String set, String condition) {
		return updateXXX(set, condition, afterSaleOrderTableName);
	}
	
	//售后退货单
	public boolean addAfterSaleRefundOrder(AfterSaleRefundOrderBean asrob) {
		return addXXX(asrob, afterSaleRefundOrderTableName);
	}

	public boolean deleteAfterSaleRefundOrder(String condition) {
		return deleteXXX(condition, afterSaleRefundOrderTableName);
	}

	public AfterSaleRefundOrderBean getAfterSaleRefundOrder(String condition) {
		return (AfterSaleRefundOrderBean) getXXX(condition, afterSaleRefundOrderTableName,
				"adultadmin.bean.afterSales.AfterSaleRefundOrderBean");
	}

	public int getAfterSaleRefundOrderCount(String condition) {
		return getXXXCount(condition, afterSaleRefundOrderTableName, "id");
	}

	public List getAfterSaleRefundOrderList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, afterSaleRefundOrderTableName,
				"adultadmin.bean.afterSales.AfterSaleRefundOrderBean");
	}

	public boolean updateAfterSaleRefundOrder(String set, String condition) {
		return updateXXX(set, condition, afterSaleRefundOrderTableName);
	}
	public boolean addAfterSaleOperatingLog(AfterSaleOperatingLogBean bean) {
		return addXXX(bean, afterSaleOperatingLogTableName);
	}

	public boolean deleteAfterSaleOperatingLog(String condition) {
		return deleteXXX(condition, afterSaleOperatingLogTableName);
	}

	public AfterSaleOperatingLogBean getAfterSaleOperatingLog(String condition) {
		return (AfterSaleOperatingLogBean) getXXX(condition, afterSaleOperatingLogTableName,
				"adultadmin.bean.afterSales.AfterSaleOperatingLogBean");
	}

	public int getAfterSaleOperatingLogCount(String condition) {
		return getXXXCount(condition, afterSaleOperatingLogTableName, "id");
	}

	public List getAfterSaleOperatingLogList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, afterSaleOperatingLogTableName,
				"adultadmin.bean.afterSales.AfterSaleOperatingLogBean");
	}

	public boolean updateAfterSaleOperatingLog(String set, String condition) {
		return updateXXX(set, condition, afterSaleOperatingLogTableName);
	}

	public boolean addAfterSaleNifferRecord(AfterSaleNifferRecordBean asnrb) {
		return addXXX(asnrb, nifferRecordTableName);
	}

	public boolean deleteAfterSaleNifferRecord(String condition) {
		return deleteXXX(condition, nifferRecordTableName);
	}

	public AfterSaleNifferRecordBean getAfterSaleNifferRecord(String condition) {
		return (AfterSaleNifferRecordBean) getXXX(condition, nifferRecordTableName,
				"adultadmin.bean.afterSales.AfterSaleNifferRecordBean");
	}

	public int getAfterSaleNifferRecordCount(String condition) {
		return getXXXCount(condition, nifferRecordTableName, "id");
	}

	public List getAfterSaleNifferRecordList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, nifferRecordTableName,
				"adultadmin.bean.afterSales.AfterSaleNifferRecordBean");
	}

	public boolean updateAfterSaleNifferRecord(String set, String condition) {
		return updateXXX(set, condition, nifferRecordTableName);
	}
	
	/**
	 * 售后订单产品操作
	 */
	public boolean deleteAfterSaleOrderProduct(String condition) {
	 
		return deleteXXX(condition, "after_sale_order_product");
	}

	public AfterSaleOrderProduct getAfterSaleOrderProduct(String condition) {
		 
		return (AfterSaleOrderProduct) getXXX(condition, "after_sale_order_product","adultadmin.bean.afterSales.AfterSaleOrderProduct");
	}

	public int getAfterSaleOrderProductCount(String condition) {
		 
		return getXXXCount(condition, "after_sale_order_product", "id");
	}

	public List getAfterSaleOrderProductList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "after_sale_order_product",
				"adultadmin.bean.afterSales.AfterSaleOrderProduct");
	}

	public boolean updateAfterSaleOrderProduct(String set, String condition) {
		
		return  updateXXX(set, condition, "after_sale_order_product");
		
	}

	public boolean addAfterSaleOrderProduct(AfterSaleOrderProduct bean) {
		return addXXX(bean,"after_sale_order_product");
	}

	/**
	 * 添加售后单费用日志 2010-03-12 李青
	 * 
	 * @param asnrb
	 * @return
	 */
	private final static String AFTER_SALE_COST_OPERATION_RECORD = "after_sale_cost_operation_record";
	private final static String AFTER_ORDER_SALE_COST_OPERATION_BEAN_URL = "adultadmin.bean.afterSales.AfterSaleCostOperationRecordBean";


	public AfterSaleCostListBean getAfterSaleCostList(String condition) {
		return (AfterSaleCostListBean) getXXX(condition, "after_sale_cost_list","adultadmin.bean.afterSales.AfterSaleCostListBean");
	}
}
