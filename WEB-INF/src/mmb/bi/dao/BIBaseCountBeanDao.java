package mmb.bi.dao;

import java.util.List;
import java.util.Map;

import mmb.bi.model.BIBaseCountBean;;

/**
 * 人力基础数据 dao
 * @author mengqy
 *
 */
public interface BIBaseCountBeanDao {

    int insert(BIBaseCountBean record);

    int deleteByPrimaryKey(Integer id);
    
	int updateByPrimaryKey(BIBaseCountBean record);
	
	int getListCount(String condition);	
	
    BIBaseCountBean selectByCondition(String condition);

	List<BIBaseCountBean> selectList(Map<String, String> paramMap);

	BIBaseCountBean searchOne(Map<String, String> paramMap);
}