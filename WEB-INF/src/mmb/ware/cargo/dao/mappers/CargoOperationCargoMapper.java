package mmb.ware.cargo.dao.mappers;

import java.util.HashMap;
import java.util.List;

import mmb.ware.cargo.dao.CargoOperationCargoDao;
import mmb.ware.cargo.model.CargoOperationCargo;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class CargoOperationCargoMapper extends AbstractDaoSupport implements CargoOperationCargoDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return getSession().delete(id);
	}

	@Override
	public int insert(CargoOperationCargo record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public CargoOperationCargo selectByPrimaryKey(Integer id) {
		return getSession().selectOne(id);
	}
	@Override
	public CargoOperationCargo selectByPrimaryKeySlave(Integer id) {
		return getSession(DynamicDataSource.SLAVE).selectOne(id);
	}

	@Override
	public CargoOperationCargo selectByCondition(String condition) {
		return getSession().selectOne(condition);
	}
	
	@Override
	public CargoOperationCargo selectByConditionSlave(String condition) {
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
	public List<CargoOperationCargo> selectList(String condition, int index, int count, String orderBy) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("condition", condition);
		map.put("index", index);
		map.put("count", count);
		map.put("orderBy", orderBy);
		
		return getSession().selectList(map);
	}
	@Override
	public List<CargoOperationCargo> selectListSlave(String condition, int index, int count, String orderBy) {
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
