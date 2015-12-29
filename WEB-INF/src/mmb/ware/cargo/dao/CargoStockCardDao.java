package mmb.ware.cargo.dao;

import java.util.List;

import mmb.ware.cargo.model.CargoStockCard;

public interface CargoStockCardDao {
	int deleteByPrimaryKey(Integer id);

    int insert(CargoStockCard record);

    CargoStockCard selectByPrimaryKey(Integer id);

    CargoStockCard selectByPrimaryKeySlave(Integer id);
    
    CargoStockCard selectByCondition(String condition);
    
    CargoStockCard selectByConditionSlave(String condition);
    
    int selectCount(String condition);
    
    int selectCountSlave(String condition);
    
    List<CargoStockCard> selectList(String condition, int index, int count, String orderBy);
    
    List<CargoStockCard> selectListSlave(String condition, int index, int count, String orderBy);
    
    int updateByCondition(String set, String condition);
}