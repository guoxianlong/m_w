package mmb.ware.cargo.dao;

import java.util.List;

import mmb.ware.cargo.model.CargoOperLog;

public interface CargoOperLogDao {
	int deleteByPrimaryKey(Integer id);

    int insert(CargoOperLog record);

    CargoOperLog selectByPrimaryKey(Integer id);

    CargoOperLog selectByPrimaryKeySlave(Integer id);
    
    CargoOperLog selectByCondition(String condition);
    
    CargoOperLog selectByConditionSlave(String condition);
    
    int selectCount(String condition);
    
    int selectCountSlave(String condition);
    
    List<CargoOperLog> selectList(String condition, int index, int count, String orderBy);
    
    List<CargoOperLog> selectListSlave(String condition, int index, int count, String orderBy);
    
    int updateByCondition(String set, String condition);
}