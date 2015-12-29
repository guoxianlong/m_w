package mmb.ware.cargo.dao.mappers;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.ware.cargo.dao.CargoOperationLogDao;
import mmb.ware.cargo.model.CargoOperationLog;

@Repository
public class CargoOperationLogMapper extends AbstractDaoSupport implements CargoOperationLogDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return getSession().delete(id);
	}

	@Override
	public int insert(CargoOperationLog record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public CargoOperationLog selectByPrimaryKey(Integer id) {
		return getSession().selectOne(id);
	}
	@Override
	public CargoOperationLog selectByPrimaryKeySlave(Integer id) {
		return getSession(DynamicDataSource.SLAVE).selectOne(id);
	}

	@Override
	public CargoOperationLog selectByCondition(String condition) {
		return getSession().selectOne(condition);
	}
	
	@Override
	public CargoOperationLog selectByConditionSlave(String condition) {
		return getSession(DynamicDataSource.SLAVE).selectOne(condition);
	}

	@Override
	public int selectCount(String condition) {
		return ((Integer) getSession().selectOne(condition)).intValue();
	}
	@Override
	public int selectCountSlave(String condition) {
		return ((Integer) getSession(DynamicDataSource.SLAVE).selectOne(condition)).intValue();
	}

	@Override
	public List<CargoOperationLog> selectList(String condition, int index, int count, String orderBy) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("condition", condition);
		map.put("index", index);
		map.put("count", count);
		map.put("orderBy", orderBy);
		
		return getSession().selectList(map);
	}
	@Override
	public List<CargoOperationLog> selectListSlave(String condition, int index, int count, String orderBy) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("condition", condition);
		map.put("index", index);
		map.put("count", count);
		map.put("orderBy", orderBy);
		
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public int updateByCondition(String set, String condition) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("set", set);
		map.put("condition", condition);
		return getSession().update(map);
	}
}
