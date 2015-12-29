package mmb.bi.dao;

import java.util.List;
import java.util.Map;

import mmb.bi.model.BIStandardCapacityBean;

/**
 * 标准产能 dao
 * @author mengqy
 *
 */
public interface BIStandardCapacityBeanDao {
	
	int insert(BIStandardCapacityBean record);
	
    int deleteByPrimaryKey(Integer id);

    int updateStopTime(Map<String, String> paramMap);
	
	int getListCount(String condition);

    BIStandardCapacityBean selectByCondition(String condition);

	List<BIStandardCapacityBean> selectList(Map<String, String> paramMap);    
	
}