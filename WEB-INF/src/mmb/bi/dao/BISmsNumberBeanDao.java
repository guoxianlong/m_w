package mmb.bi.dao;

import mmb.bi.model.BISmsNumberBean;

import java.util.HashMap;
import java.util.List;

/**
 * 定时短信手机号dao
 * @author mengqy
 *
 */
public interface BISmsNumberBeanDao {
	
	/**
	 * 添加
	 * @param record
	 * @return
	 */
	int insert(BISmsNumberBean record);

	/**
	 * 全部删除	
	 * @param condition
	 * @return
	 */
	boolean cancelDeleteAll(String condition);
	
	/**
	 * 修改
	 * @param record
	 * @return
	 */
	int update(BISmsNumberBean record);
	
	/**
	 * 全部审核(审核通过和审核不通过 )
	 * @param paramMap
	 * @return
	 */
	boolean checkAll(HashMap<String, String> paramMap);
	 
	/**
	 * 查询数量
	 * @param condition
	 * @return
	 */
	int getListCount(String condition);
	
	/**
	 * 查询一个
	 * @param condition
	 * @return
	 */
	BISmsNumberBean selectByCondition(String condition);
	
	/**
	 * 查询列表
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	List<BISmsNumberBean> selectList(String condition, int index, int count, String orderBy);	
}
