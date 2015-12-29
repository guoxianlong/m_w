package mmb.stock.afStock.dao;

import mmb.stock.afStock.model.AfterSaleDetectProductBean;

public interface AfterSaleDetectProductBeanDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AfterSaleDetectProductBean record);

    int insertSelective(AfterSaleDetectProductBean record);

    AfterSaleDetectProductBean selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AfterSaleDetectProductBean record);

    int updateByPrimaryKey(AfterSaleDetectProductBean record);
    
    AfterSaleDetectProductBean selectByCondition(String condition);
    
    int updateByCondition(String set, String condition);
}