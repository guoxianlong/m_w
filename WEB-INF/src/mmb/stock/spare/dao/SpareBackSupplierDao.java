package mmb.stock.spare.dao;

import java.util.List;
import java.util.Map;

import mmb.stock.spare.model.SpareBackSupplier;

public interface SpareBackSupplierDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SpareBackSupplier record);

    int insertSelective(SpareBackSupplier record);

    SpareBackSupplier selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SpareBackSupplier record);

    int updateByPrimaryKey(SpareBackSupplier record);
    
    List<Map<String,String>> getSpareBackSupplierByCondition(Map<String,String> map);
    
    int getSpareBackSupplierByConditionForCount(Map<String,String> map);
    
}