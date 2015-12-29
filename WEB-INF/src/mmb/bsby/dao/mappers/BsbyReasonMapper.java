package mmb.bsby.dao.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.bsby.dao.BsbyReasonDao;
import mmb.bsby.model.BsbyReason;










import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class BsbyReasonMapper extends AbstractDaoSupport implements BsbyReasonDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return getSession().delete(id);
	}

	@Override
	public int insert(BsbyReason bsbyReason) {
		getSession().insert(bsbyReason);
		return bsbyReason.getId();
	}

	@Override
	public BsbyReason selectByPrimaryKey(Integer id) {
		return getSession(DynamicDataSource.SLAVE).selectOne(id);
	}

	@Override
	public int selectCount(String condition) {
		return ((Integer) getSession().selectOne(condition)).intValue();
	}

	@Override
	public List<BsbyReason> getBsbyReasonList(Map<String,String> map) {
	    return getSession().selectList(map);
	}

	@Override
	public BsbyReason queryBsbyReasonByCondition(String condition) {
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
