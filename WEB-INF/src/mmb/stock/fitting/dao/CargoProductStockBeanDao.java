package mmb.stock.fitting.dao;


import mmb.stock.fitting.model.CargoProductStockBean;
import mmb.stock.fitting.model.CargoStockCardBean;

public interface CargoProductStockBeanDao {
	
    int insert(CargoProductStockBean record);
    
	CargoProductStockBean selectByPrimaryKey(Integer id);

	CargoProductStockBean selectByCondition(String condition);

	boolean updateStockCount(int id, int count);

	boolean updateStockLockCount(int id, int count);
	
	
	int insertCargoStockCardBean(CargoStockCardBean record);
}