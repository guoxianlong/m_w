package mmb.stock.spare.dao;

import java.util.List;
import java.util.Map;

import java.util.HashMap;
import java.util.List;

import mmb.stock.spare.model.SpareCodeBean;

public interface SpareCodeBeanDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SpareCodeBean record);

    int insertSelective(SpareCodeBean record);

    SpareCodeBean selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SpareCodeBean record);

    int updateByPrimaryKey(SpareCodeBean record);
    
	List<SpareCodeBean> getSpareCodeBean(Map<String, String> map);
    
    SpareCodeBean getSpareCodeByCondition(HashMap<String,String> conditionMap);
    
    int updateSpareCodeBeanByCondition(Map<String,String> map);
    
    /**
     * 批量跟新备用机号状态
     * @param list
     * @return
     */
    int batchUpdateSpareCodeStatus(List<SpareCodeBean> list);
    
    int updateStatusByCode(SpareCodeBean record);

}