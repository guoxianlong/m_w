package mmb.stock.aftersale.dao;

import java.util.HashMap;

import mmb.stock.aftersale.AfterSaleDetectProductBean;


public interface AfterSaleDetectProductDao {
	
	int getAfterSaleBackSupplierProductCount(HashMap<String,String> map);
	
	AfterSaleDetectProductBean getDetectProduct(HashMap<String,String> map);
	
	AfterSaleDetectProductBean getDetectProductById(Integer id);
	
	int updateDetectProduct(AfterSaleDetectProductBean record);
}
