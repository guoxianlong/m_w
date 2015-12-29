package mmb.ware.cargo.dao;

import java.util.List;

import mmb.ware.cargo.model.CargoOperationLog;

public interface CargoOperationLogDao {
	int deleteByPrimaryKey(Integer id);

    int insert(CargoOperationLog record);

    CargoOperationLog selectByPrimaryKey(Integer id);
    
    CargoOperationLog selectByPrimaryKeySlave(Integer id);
    
    CargoOperationLog selectByCondition(String condition);
    
    CargoOperationLog selectByConditionSlave(String condition);
    
    int selectCount(String condition);
    
    int selectCountSlave(String condition);
    
    List<CargoOperationLog> selectList(String condition, int index, int count, String orderBy);
    
    List<CargoOperationLog> selectListSlave(String condition, int index, int count, String orderBy);
    
    int updateByCondition(String set, String condition);
}