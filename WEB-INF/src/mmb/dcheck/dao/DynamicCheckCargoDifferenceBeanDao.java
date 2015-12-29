package mmb.dcheck.dao;

import java.util.List;

import mmb.dcheck.model.DynamicCheckCargoDifferenceBean;

public interface DynamicCheckCargoDifferenceBeanDao {
    int deleteByPrimaryKey(Integer id);

    int insert(DynamicCheckCargoDifferenceBean record);

    DynamicCheckCargoDifferenceBean selectByPrimaryKey(Integer id);
    
    DynamicCheckCargoDifferenceBean selectByCondition(String condition);    
    

    int selectCount(String condition);
    
    List<DynamicCheckCargoDifferenceBean> selectList(String condition, int index, int count, String orderBy);
    
    int updateByCondition(String set, String condition);
}