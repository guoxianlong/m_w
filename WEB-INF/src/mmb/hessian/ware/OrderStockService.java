package mmb.hessian.ware;

import adultadmin.action.stock.OrderStockAction;
import adultadmin.action.stock.OrderStockSaleAction;

public class OrderStockService {
	
	public int getDeliver(Integer buyMode,String address,Integer stockArea){
		
		int deliver = 0;
		OrderStockAction action = new OrderStockAction();
		deliver = action.getDeliver(buyMode, address, stockArea);
		
		return deliver;
	}
	
	public String addOrderStockAuto(Integer orderId){
		OrderStockSaleAction action = new OrderStockSaleAction();
		return action.addOrderStock1(orderId);
	}
	
}
