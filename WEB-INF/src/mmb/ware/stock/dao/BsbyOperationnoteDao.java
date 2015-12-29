package mmb.ware.stock.dao;

import java.util.List;

import mmb.ware.stock.model.BsbyOperationnote;

public interface BsbyOperationnoteDao {
	int deleteByPrimaryKey(Integer id);

    int insert(BsbyOperationnote record);

    BsbyOperationnote selectByPrimaryKey(Integer id);

    BsbyOperationnote selectByPrimaryKeySlave(Integer id);
    
    BsbyOperationnote selectByCondition(String condition);
    
    BsbyOperationnote selectByConditionSlave(String condition);
    
    int selectCount(String condition);
    
    int selectCountSlave(String condition);
    
    List<BsbyOperationnote> selectList(String condition, int index, int count, String orderBy);
    
    List<BsbyOperationnote> selectListSlave(String condition, int index, int count, String orderBy);
    
    int updateByCondition(String set, String condition);

	int selectMaxCount(String condition);
}