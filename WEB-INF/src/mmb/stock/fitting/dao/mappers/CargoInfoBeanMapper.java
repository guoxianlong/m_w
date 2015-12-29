package mmb.stock.fitting.dao.mappers;

import java.util.HashMap;

import mmb.stock.fitting.dao.CargoInfoBeanDao;
import mmb.stock.fitting.model.CargoInfoBean;
import mmb.stock.fitting.model.CargoOperationBean;
import mmb.stock.fitting.model.CargoOperationCargoBean;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class CargoInfoBeanMapper extends AbstractDaoSupport implements CargoInfoBeanDao {

	@Override
	public CargoInfoBean selectByPrimaryKey(Integer id) {
		return getSession().selectOne(id);
	}

	@Override
	public CargoInfoBean selectByCondition(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
	public boolean updateCargoInfoBean(String set, String condition) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("set", set);
		map.put("condition", condition);
		return getSession().update(map) > 0;
	}

	@Override
	public int insertCargoOperationCargoBean(CargoOperationCargoBean bean) {
		getSession().insert(bean);
		return bean.getId();
	}

	@Override
	public int insertCargoOperationBean(CargoOperationBean bean) {
		getSession().insert(bean);
		return bean.getId();
	}
	
	@Override
	public CargoOperationBean selectCargoOperationByCondition(String condition) {
		return getSession().selectOne(condition);
	}
	
}
