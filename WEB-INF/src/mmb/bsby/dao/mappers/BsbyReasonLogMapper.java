package mmb.bsby.dao.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import mmb.bsby.dao.BsbyReasonLogDao;












import mmb.bsby.model.BsbyReasonLog;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class BsbyReasonLogMapper extends AbstractDaoSupport implements BsbyReasonLogDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return getSession().delete(id);
	}

	@Override
	public int insert(BsbyReasonLog bsbyReason) {
		getSession().insert(bsbyReason);
		return bsbyReason.getId();
	}

	@Override
	public BsbyReasonLog selectByPrimaryKey(Integer id) {
		return getSession(DynamicDataSource.SLAVE).selectOne(id);
	}

	@Override
	public int getBsbyReasonLogCount(String condition) {
		
		return ((Integer) getSession().selectOne(condition)).intValue();
	}

	@Override
	public List<BsbyReasonLog> getBsbyReasonLogList(Map<String,String> map) {
	    return getSession().selectList(map);
	}

	@Override
	public BsbyReasonLog queryBsbyReasonLogByCondition(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
	public int updateByCondition(String set, String condition) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("set", set);
		map.put("condition", condition);
		
		return getSession().update(map);
	}

}
