package mmb.ware.stock.dao;

import java.util.List;

import mmb.ware.stock.model.BsbyProduct;

public interface BsbyProductDao {
	int deleteByPrimaryKey(Integer id);

    int insert(BsbyProduct record);

    BsbyProduct selectByPrimaryKey(Integer id);

    BsbyProduct selectByPrimaryKeySlave(Integer id);
    
    BsbyProduct selectByCondition(String condition);
    
    BsbyProduct selectByConditionSlave(String condition);
    
    int selectCount(String condition);
    
    int selectCountSlave(String condition);
    
    List<BsbyProduct> selectList(String condition, int index, int count, String orderBy);
    
    List<BsbyProduct> selectListSlave(String condition, int index, int count, String orderBy);
    
    int updateByCondition(String set, String condition);
}