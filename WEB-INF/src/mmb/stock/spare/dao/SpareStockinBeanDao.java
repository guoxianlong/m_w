package mmb.stock.spare.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.stock.spare.model.SpareStockinBean;

public interface SpareStockinBeanDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SpareStockinBean record);

    int insertSelective(SpareStockinBean record);

    SpareStockinBean selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SpareStockinBean record);

    int updateByPrimaryKey(SpareStockinBean record);
    
    List<SpareStockinBean> getSpareStockinList(HashMap<String,String> conditionMap);
    
    int updateByCondition(String set, String condition);

    /**
     * 入库单列表
     * @param conditionMap
     * @return
     * 2014-10-23
     * lining
     */
    List<SpareStockinBean> getSpareStockinListJoinProduct(HashMap<String,String> conditionMap);
    
    int getSpareStockInCount(HashMap<String,String> conditionMap);
    
    Map<String,String> getSupplierId(Map<String,String> map);
    
    Map<String,String> getSpareStockinAreaId(Map<String,String> map);
}