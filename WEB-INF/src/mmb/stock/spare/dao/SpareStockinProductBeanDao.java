package mmb.stock.spare.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.stock.spare.model.SpareStockinProductBean;

public interface SpareStockinProductBeanDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SpareStockinProductBean record);

    SpareStockinProductBean selectByPrimaryKey(Integer id);
    
    SpareStockinProductBean getSpareStockinProductByCondition(HashMap<String,String> conditionMap);

    int updateByPrimaryKeySelective(SpareStockinProductBean record);

    int updateByPrimaryKey(SpareStockinProductBean record);
    
    SpareStockinProductBean selectByCondition(String condition);

    int batchAddSpareStockinProducts(List<SpareStockinProductBean> list);

    int updateByCondition(String set, String condition);

	List<SpareStockinProductBean> getSpareStockinProductBeans(Map<String, String> map);
	
	int getSpareCodeStatus(Map<String,String> map);
	
	int updateSpareStockinProductByCondition(Map<String,String> map);
}