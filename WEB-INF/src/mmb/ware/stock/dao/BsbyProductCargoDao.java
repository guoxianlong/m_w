package mmb.ware.stock.dao;

import java.util.List;

import mmb.ware.stock.model.BsbyProductCargo;

public interface BsbyProductCargoDao {
	int deleteByPrimaryKey(Integer id);

    int insert(BsbyProductCargo record);

    BsbyProductCargo selectByPrimaryKey(Integer id);

    BsbyProductCargo selectByPrimaryKeySlave(Integer id);
    
    BsbyProductCargo selectByCondition(String condition);
    
    BsbyProductCargo selectByConditionSlave(String condition);
    
    int selectCount(String condition);
    
    int selectCountSlave(String condition);
    
    List<BsbyProductCargo> selectList(String condition, int index, int count, String orderBy);
    
    List<BsbyProductCargo> selectListSlave(String condition, int index, int count, String orderBy);
    
    int updateByCondition(String set, String condition);
}