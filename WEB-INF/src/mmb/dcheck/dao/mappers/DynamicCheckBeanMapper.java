package mmb.dcheck.dao.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.dcheck.dao.DynamicCheckBeanDao;
import mmb.dcheck.model.DynamicCheckBean;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class DynamicCheckBeanMapper extends AbstractDaoSupport implements DynamicCheckBeanDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return getSession().delete(id);
	}

	@Override
	public int insert(DynamicCheckBean record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public DynamicCheckBean selectByPrimaryKey(Integer id) {
		return getSession(DynamicDataSource.SLAVE).selectOne(id);
	}

	

	@Override
	public int selectCount(String condition) {
		return ((Integer) getSession().selectOne(condition)).intValue();
	}

	@Override
	public List<DynamicCheckBean> selectList(String condition, int index, int count, String orderBy) {
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
	public int getDynamicCheckBeanCount(Map<String, String> map) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}

	@Override
	public List<DynamicCheckBean> getDynamicCheckBeanList(
			Map<String, String> condition) {
		return getSession(DynamicDataSource.SLAVE).selectList(condition);
	}

	@Override
	public int updateByCondition(Map<String, String> map) {
		return getSession(DynamicDataSource.SLAVE).update(map);
	}

	@Override
	public DynamicCheckBean selectByCondition(String condition) {
		
		return getSession(DynamicDataSource.SLAVE).selectOne(condition);
	}



	

	
}
