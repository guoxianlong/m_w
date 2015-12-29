package mmb.ware.cargo.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.ware.cargo.dao.CargoInfoCityDao;
import mmb.ware.cargo.model.CargoInfoCity;
@Repository
public class CargoInfoCityMapper extends AbstractDaoSupport implements CargoInfoCityDao {

	@Override
	public int insertCargoInfoCity(CargoInfoCity record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public CargoInfoCity selectCargoInfoCity(String condition) {
		// TODO Auto-generated method stub
		return getSession().selectOne(condition);
	}

	@Override
	public List<CargoInfoCity> selectCargoInfoCityList(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession().selectList(map);
	}

	@Override
	public CargoInfoCity selectCargoInfoCitySlave(String condition) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectOne(condition);
	}

	@Override
	public List<CargoInfoCity> selectCargoInfoCityListSlave(
			Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

}
