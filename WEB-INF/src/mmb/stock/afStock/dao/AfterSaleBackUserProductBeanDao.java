package mmb.stock.afStock.dao;

import mmb.stock.afStock.model.AfterSaleBackUserProductBean;

public interface AfterSaleBackUserProductBeanDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AfterSaleBackUserProductBean record);

    int insertSelective(AfterSaleBackUserProductBean record);

    AfterSaleBackUserProductBean selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AfterSaleBackUserProductBean record);

    int updateByPrimaryKey(AfterSaleBackUserProductBean record);
}