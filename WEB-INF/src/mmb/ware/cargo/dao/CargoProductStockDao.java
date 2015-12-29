package mmb.ware.cargo.dao;

import java.util.List;

import mmb.ware.cargo.model.CargoProductStock;

public interface CargoProductStockDao {
	int deleteByPrimaryKey(Integer id);

    int insert(CargoProductStock record);

    CargoProductStock selectByPrimaryKey(Integer id);
    
    CargoProductStock selectByPrimaryKeySlave(Integer id);
    
    CargoProductStock selectByCondition(String condition);
    
    CargoProductStock selectByConditionSlave(String condition);
    
    int selectCount(String condition);
    
    int selectCountSlave(String condition);
    
    List<CargoProductStock> selectList(String condition, int index, int count, String orderBy);
    
    List<CargoProductStock> selectListSlave(String condition, int index, int count, String orderBy);
    
    int updateByCondition(String set, String condition);
    
    boolean updateCargoProductStockCount(int id, int count);
    
    boolean updateCargoProductStockLockCount(int id,int count);
}