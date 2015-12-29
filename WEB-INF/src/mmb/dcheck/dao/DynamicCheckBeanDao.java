package mmb.dcheck.dao;

import java.util.List;
import java.util.Map;

import mmb.dcheck.model.DynamicCheckBean;

public interface DynamicCheckBeanDao {
    int deleteByPrimaryKey(Integer id);

    int insert(DynamicCheckBean record);
 
    DynamicCheckBean selectByPrimaryKey(Integer id);
    
   
    
    int selectCount(String condition);
    
    List<DynamicCheckBean> selectList(String condition, int index, int count, String orderBy);
    List<DynamicCheckBean>  getDynamicCheckBeanList(Map<String,String> condition);
    
    int updateByCondition(String set, String condition);
    
    int getDynamicCheckBeanCount(Map<String, String> map);
    int updateByCondition(Map<String, String> map);
	/**
	 * 获取动态盘点计划
	 * @param condition
	 * @return 动态盘点计划
	 */
	public  DynamicCheckBean selectByCondition(String condition);
}