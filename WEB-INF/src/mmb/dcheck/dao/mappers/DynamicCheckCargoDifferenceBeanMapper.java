package mmb.dcheck.dao.mappers;

import java.util.HashMap;
import java.util.List;

import mmb.dcheck.dao.DynamicCheckCargoDifferenceBeanDao;
import mmb.dcheck.model.DynamicCheckCargoDifferenceBean;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class DynamicCheckCargoDifferenceBeanMapper extends AbstractDaoSupport implements DynamicCheckCargoDifferenceBeanDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return getSession().delete(id);
	}

	@Override
	public int insert(DynamicCheckCargoDifferenceBean record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public DynamicCheckCargoDifferenceBean selectByPrimaryKey(Integer id) {
		return getSession().selectOne(id);
	}

	@Override
	public DynamicCheckCargoDifferenceBean selectByCondition(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
	public int selectCount(String condition) {
		return ((Integer) getSession().selectOne(condition)).intValue();
	}

	@Override
	public List<DynamicCheckCargoDifferenceBean> selectList(String condition, int index, int count, String orderBy) {
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
	
}
