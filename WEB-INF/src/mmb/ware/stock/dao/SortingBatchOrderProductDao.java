package mmb.ware.stock.dao;

import java.util.List;

import mmb.ware.stock.model.SortingBatchOrderProduct;

public interface SortingBatchOrderProductDao {
	int deleteByPrimaryKey(Integer id);

    int insert(SortingBatchOrderProduct record);

    SortingBatchOrderProduct selectByPrimaryKey(Integer id);

    SortingBatchOrderProduct selectByPrimaryKeySlave(Integer id);
    
    SortingBatchOrderProduct selectByCondition(String condition);
    
    SortingBatchOrderProduct selectByConditionSlave(String condition);
    
    int selectCount(String condition);
    
    int selectCountSlave(String condition);
    
    List<SortingBatchOrderProduct> selectList(String condition, int index, int count, String orderBy);
    
    List<SortingBatchOrderProduct> selectListSlave(String condition, int index, int count, String orderBy);
    
    int updateByCondition(String set, String condition);
}