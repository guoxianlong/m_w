package mmb.common.dao;

import java.util.List;

import adultadmin.action.vo.voOrderProduct;

public interface OrderProductDao {
	
	public List<voOrderProduct> getOrderProductsSplit(int id);
	
	public List<voOrderProduct> getOrderPresentsSplit(int id);

}
