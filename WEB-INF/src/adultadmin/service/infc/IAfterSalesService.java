/*
 * Created on 2007-1-24
 *
 */
package adultadmin.service.infc;

import java.util.ArrayList;
import java.util.List;

import adultadmin.bean.afterSales.AfterSaleCostListBean;
import adultadmin.bean.afterSales.AfterSaleOperatingLogBean;
import adultadmin.bean.afterSales.AfterSaleOrderBean;
import adultadmin.bean.afterSales.AfterSaleOrderProduct;
import adultadmin.bean.afterSales.AfterSaleRefundOrderBean;

/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2010-1-7
 * 
 * 说明：售后相关数据库操作
 */
public interface IAfterSalesService extends IBaseService {
	public boolean addAfterSaleOrder(AfterSaleOrderBean afterSaleOrder);

	public boolean deleteAfterSaleOrder(String condition);

	public AfterSaleOrderBean getAfterSaleOrder(String condition);

	public int getAfterSaleOrderCount(String condition);

	public List getAfterSaleOrderList(String condition, int index, int count,
			String orderBy);

	public boolean updateAfterSaleOrder(String set, String condition);

	//售后退货单
	public boolean addAfterSaleRefundOrder(AfterSaleRefundOrderBean asrob);

	public boolean deleteAfterSaleRefundOrder(String condition);

	public AfterSaleRefundOrderBean getAfterSaleRefundOrder(String condition);

	public int getAfterSaleRefundOrderCount(String condition);

	public List getAfterSaleRefundOrderList(String condition, int index,
			int count, String orderBy);

	public boolean updateAfterSaleRefundOrder(String set, String condition);

	public boolean addAfterSaleOperatingLog(AfterSaleOperatingLogBean bean);
	

	/**
	 * 售后退换货费用单
	 */
	public AfterSaleCostListBean getAfterSaleCostList(String condition);
	
	public boolean deleteAfterSaleOrderProduct(String condition);

	public AfterSaleOrderProduct getAfterSaleOrderProduct(String condition);

	public int getAfterSaleOrderProductCount(String condition);

	public List getAfterSaleOrderProductList(String condition, int index,
			int count, String orderBy);

	public boolean updateAfterSaleOrderProduct(String set, String condition);
	
	public boolean addAfterSaleOrderProduct(AfterSaleOrderProduct bean);
	

}
