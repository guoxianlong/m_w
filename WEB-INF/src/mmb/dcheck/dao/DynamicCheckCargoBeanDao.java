package mmb.dcheck.dao;

import java.util.List;
import java.util.Map;

import mmb.dcheck.model.DynamicCheckCargoBean;

public interface DynamicCheckCargoBeanDao {
    int deleteByPrimaryKey(Integer id);

    int insert(DynamicCheckCargoBean record);

    DynamicCheckCargoBean selectByPrimaryKey(Integer id);
    
    DynamicCheckCargoBean selectByCondition(String condition);
    
    int selectCount(Map<String,String> condition);
    
    int selectCount(String condition);
    
    List<DynamicCheckCargoBean> selectList(String condition, int index, int count, String orderBy);
    
    int updateByCondition(String set, String condition);
	/**
	 * 获取动态盘点明细列表
	 * @param condition
	 * @return 动态盘点计划集合
	 */
	public List<DynamicCheckCargoBean> getDynamicCheckCargoBeans(Map<String,String> condition);
	/**
	 * 获取动态盘点明细总条数
	 * @param condition
	 * @return  总条数
	 */
	public int getDynamicCheckCargoBeanCount(Map<String, String> condition);
	
	/***
	 * 重新终盘
	 * @param id
	 */
	public int afreshDCheck(Integer  id);
}