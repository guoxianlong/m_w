package mmb.rec.oper.dao;

import java.util.List;
import java.util.Map;

import adultadmin.bean.order.OrderStockBean;
public interface OrderStockDao {
	
	/**
	 * 添加OrderStock
	 * @param orderStockBean
	 * @return
	 */
	public int addOrderStock(OrderStockBean orderStockBean);
	/**
	 * 查OrderStock
	 * @param condition
	 * @return
	 */
	public OrderStockBean getOrderStock(String condition);
	
	/**
	 * 查询符合条件的OrderStock的List
	 * @param paramMap
	 * @return
	 */
	public List<OrderStockBean> getOrderStockList(Map<String,String> paramMap);
	/**
	 * 查询符合条件的OrderStock的List
	 * @param paramMap
	 * @return
	 */
	public List<OrderStockBean> getOrderStockListSlave(Map<String,String> paramMap);
	

}
