package mmb.ware.stock.dao;

import java.util.List;

import mmb.ware.stock.model.BsbyOperationRecord;

public interface BsbyOperationRecordDao {
	int deleteByPrimaryKey(Integer id);

    int insert(BsbyOperationRecord record);

    BsbyOperationRecord selectByPrimaryKey(Integer id);

    BsbyOperationRecord selectByPrimaryKeySlave(Integer id);
    
    BsbyOperationRecord selectByCondition(String condition);
    
    BsbyOperationRecord selectByConditionSlave(String condition);
    
    int selectCount(String condition);
    
    int selectCountSlave(String condition);
    
    List<BsbyOperationRecord> selectList(String condition, int index, int count, String orderBy);
    
    List<BsbyOperationRecord> selectListSlave(String condition, int index, int count, String orderBy);
    
    int updateByCondition(String set, String condition);
}