package mmb.rec.oper.dao;

import java.util.List;
import java.util.Map;

import adultadmin.bean.order.OrderStockProductCargoBean;

public interface OrderStockProductCargoDao {
	
	public int addOrderStockProductCargo(OrderStockProductCargoBean orderStockProductCargo);
	
	public OrderStockProductCargoBean getOrderStockProductCargo(String condition);
	
	public List<OrderStockProductCargoBean> getOrderStockProductCargoList(Map<String,String> paramMap);
	
	public List<OrderStockProductCargoBean> getOrderStockProductCargoListSlave(Map<String,String> paramMap);

}
