package mmb.ware.cargo.dao;

import java.util.List;

import mmb.ware.cargo.model.CargoOperationProcess;

public interface CargoOperationProcessDao {
	int deleteByPrimaryKey(Integer id);

    int insert(CargoOperationProcess record);

    CargoOperationProcess selectByPrimaryKey(Integer id);

    CargoOperationProcess selectByPrimaryKeySlave(Integer id);
    
    CargoOperationProcess selectByCondition(String condition);
    
    CargoOperationProcess selectByConditionSlave(String condition);
    
    int selectCount(String condition);
    
    int selectCountSlave(String condition);
    
    List<CargoOperationProcess> selectList(String condition, int index, int count, String orderBy);
    
    List<CargoOperationProcess> selectListSlave(String condition, int index, int count, String orderBy);
    
    int updateByCondition(String set, String condition);
}