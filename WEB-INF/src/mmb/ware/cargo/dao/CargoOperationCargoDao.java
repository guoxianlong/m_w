package mmb.ware.cargo.dao;

import java.util.List;

import mmb.ware.cargo.model.CargoOperationCargo;

public interface CargoOperationCargoDao {
	int deleteByPrimaryKey(Integer id);

    int insert(CargoOperationCargo record);

    CargoOperationCargo selectByPrimaryKey(Integer id);
    
    CargoOperationCargo selectByPrimaryKeySlave(Integer id);
    
    CargoOperationCargo selectByCondition(String condition);
    
    CargoOperationCargo selectByConditionSlave(String condition);
    
    int selectCount(String condition);
    
    int selectCountSlave(String condition);
    
    List<CargoOperationCargo> selectList(String condition, int index, int count, String orderBy);
    
    List<CargoOperationCargo> selectListSlave(String condition, int index, int count, String orderBy);
    
    int updateByCondition(String set, String condition);
}