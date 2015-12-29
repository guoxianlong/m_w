package mmb.stock.afStock.dao;

import mmb.stock.afStock.model.AfterSaleBackSupplierProductBean;

public interface AfterSaleBackSupplierProductBeanDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AfterSaleBackSupplierProductBean record);

    int insertSelective(AfterSaleBackSupplierProductBean record);

    AfterSaleBackSupplierProductBean selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AfterSaleBackSupplierProductBean record);

    int updateByPrimaryKey(AfterSaleBackSupplierProductBean record);
    
    AfterSaleBackSupplierProductBean selectByCondition(String condition);
}