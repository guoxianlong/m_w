package mmb.ware.cargo.dao;

import java.util.List;

import mmb.ware.cargo.model.CargoOperation;

public interface CargoOperationDao {
	int deleteByPrimaryKey(Integer id);

    int insert(CargoOperation record);

    CargoOperation selectByPrimaryKey(Integer id);
    
    CargoOperation selectByPrimaryKeySlave(Integer id);
    
    CargoOperation selectByCondition(String condition);
    
    CargoOperation selectByConditionSlave(String condition);
    
    int selectCount(String condition);
    
    int selectCountSlave(String condition);
    
    List<CargoOperation> selectList(String condition, int index, int count, String orderBy);
    
    List<CargoOperation> selectListSlave(String condition, int index, int count, String orderBy);
    
    int updateByCondition(String set, String condition);
}