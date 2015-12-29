package mmb.ware.cargo.dao;

import java.util.List;

import mmb.ware.cargo.model.CargoInfo;

public interface CargoInfoDao {
	int deleteByPrimaryKey(Integer id);

    int insert(CargoInfo record);

    CargoInfo selectByPrimaryKey(Integer id);
    
    CargoInfo selectByPrimaryKeySlave(Integer id);
    
    CargoInfo selectByCondition(String condition);
    
    CargoInfo selectByConditionSlave(String condition);
    
    int selectCount(String condition);
    
    int selectCountSlave(String condition);
    
    List<CargoInfo> selectList(String condition, int index, int count, String orderBy);
    
    List<CargoInfo> selectListSlave(String condition, int index, int count, String orderBy);
    
    int updateByCondition(String set, String condition);
    
    List<CargoInfo> getCargoAndProductStockList(String condition, int index, int count, String orderBy);
    
}