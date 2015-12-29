package mmb.stock.spare.dao;

import java.util.List;
import java.util.Map;

import mmb.stock.spare.model.SpareBackSupplierProduct;

public interface SpareBackSupplierProductDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SpareBackSupplierProduct record);

    int insertSelective(SpareBackSupplierProduct record);

    SpareBackSupplierProduct selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SpareBackSupplierProduct record);

    int updateByPrimaryKey(SpareBackSupplierProduct record);
    
    List<Map<String,String>> getSpareBackSupplierproductByCondition(Map<String,String> map);
    
    List<SpareBackSupplierProduct> getSpareBackSupplierproductJoinProduct(Map<String,String> map);
}