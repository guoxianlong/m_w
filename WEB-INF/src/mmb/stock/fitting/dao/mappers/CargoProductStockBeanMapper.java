package mmb.stock.fitting.dao.mappers;

import java.util.HashMap;

import mmb.stock.fitting.dao.CargoProductStockBeanDao;
import mmb.stock.fitting.model.CargoProductStockBean;
import mmb.stock.fitting.model.CargoStockCardBean;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class CargoProductStockBeanMapper extends AbstractDaoSupport implements CargoProductStockBeanDao {

	@Override
	public int insert(CargoProductStockBean record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public CargoProductStockBean selectByPrimaryKey(Integer id) {
		return getSession().selectOne(id);
	}

	@Override
	public CargoProductStockBean selectByCondition(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
	public boolean updateStockCount(int id, int count) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("count", count);
		map.put("count2", -count);		
		return getSession().update(map) > 0;
	}

	@Override
	public boolean updateStockLockCount(int id, int count) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("count", count);
		map.put("count2", -count);
		return getSession().update(map) > 0;
	}
	

	@Override
	public int insertCargoStockCardBean(CargoStockCardBean record) {
		getSession().insert(record);
		return record.getId();
	}

}
