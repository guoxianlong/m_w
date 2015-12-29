package mmb.bi.dao;

import mmb.bi.model.BIInServiceCountBean;

import java.util.List;
import java.util.Map;

/**
 * 在职人力dao
 * @author mengqy
 *
 */
public interface BIInServiceCountBeanDao {
	
	int insert(BIInServiceCountBean record);
	
	int cancelDelete(Map<String, String> paramMap);

	int update(BIInServiceCountBean record);
	
	int check(Map<String, String> paramMap);

	int getListCount(String condition);
	
	BIInServiceCountBean selectByCondition(String condition);
	
	List<BIInServiceCountBean> selectList(Map<String, String> paramMap);
	
}
