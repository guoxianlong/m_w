package mmb.bi.dao;

import mmb.bi.model.BIOnGuradCountBean;

import java.util.List;
import java.util.Map;

/**
 * 在岗人力dao
 * @author mengqy
 *
 */
public interface BIOnGuradCountBeanDao {

	int insert(BIOnGuradCountBean record);

	int cancelDelete(Map<String, String> paramMap);
	
	int update(BIOnGuradCountBean record);
	
	int check(Map<String, String> paramMap);
	
	int getListCount(String condition);

	BIOnGuradCountBean selectByCondition(String condition);
	
	List<BIOnGuradCountBean> selectList(Map<String, String> paramMap);

}
