package mmb.rec.oper.dao;

import java.util.List;
import java.util.Map;

import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;

public interface OrderStockProductDao {
	
	/**
	 * 添加OrderStockProduct
	 * @param orderStockBeanProduct
	 * @return
	 */
	public int addOrderStockProduct(OrderStockProductBean orderStockProductBean);
	/**
	 * 查OrderStockProduct
	 * @param condition
	 * @return
	 */
	public OrderStockProductBean getOrderStockProduct(String condition);
	
	/**
	 * 查询符合条件的OrderStockProduct的List
	 * @param paramMap
	 * @return
	 */
	public List<OrderStockProductBean> getOrderStockProductList(Map<String,String> paramMap);
	/**
	 * 查询符合条件的OrderStockProduct的List
	 * @param paramMap
	 * @return
	 */
	public List<OrderStockProductBean> getOrderStockProductListSlave(Map<String,String> paramMap);

}
