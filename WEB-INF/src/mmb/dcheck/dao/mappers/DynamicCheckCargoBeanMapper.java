package mmb.dcheck.dao.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.dcheck.dao.DynamicCheckCargoBeanDao;
import mmb.dcheck.model.DynamicCheckCargoBean;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class DynamicCheckCargoBeanMapper extends AbstractDaoSupport implements DynamicCheckCargoBeanDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return getSession().delete(id);
	}

	@Override
	public int insert(DynamicCheckCargoBean record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public int selectCount(String condition) {
		return ((Integer) getSession().selectOne(condition)).intValue();
	}

	@Override
	public DynamicCheckCargoBean selectByPrimaryKey(Integer id) {
		Map<String, Integer> map=new HashMap<String, Integer>();
		map.put("id", id);
		return getSession().selectOne(id);
	}

	@Override
	public DynamicCheckCargoBean selectByCondition(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
    public int selectCount(Map<String,String> condition) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(condition.get("condition"))).intValue();
	}
	

	@Override
	public List<DynamicCheckCargoBean> selectList(String condition, int index, int count, String orderBy) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("condition", condition);
		map.put("index", index);
		map.put("count", count);
		map.put("orderBy", orderBy);
		
		return getSession().selectList(map);
	}
	
	@Override
	public int updateByCondition(String set, String condition) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("set", set);
		map.put("condition", condition);
		
		return getSession().update(map);
	}

	@Override
	public List<DynamicCheckCargoBean> getDynamicCheckCargoBeans(
			Map<String, String> condition) {
		return getSession(DynamicDataSource.SLAVE).selectList(condition);
	}

	@Override
	public int getDynamicCheckCargoBeanCount(Map<String, String> condition) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(condition)).intValue();
	}

	@Override
	public int afreshDCheck(Integer id) {
		Map<String, Integer> map=new HashMap<String, Integer>();
		map.put("id", id);
		return getSession().update(map);
	}
}
