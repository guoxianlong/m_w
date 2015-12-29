package mmb.ware.cargo.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.ware.cargo.dao.CargoInfoStorageDao;
import mmb.ware.cargo.model.CargoInfoStorage;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class CargoInfoStorageMapper extends AbstractDaoSupport implements CargoInfoStorageDao {

	@Override
	public int insertCargoInfoStorage(CargoInfoStorage record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public CargoInfoStorage selectCargoInfoStorage(String condition) {
		// TODO Auto-generated method stub
		return getSession().selectOne(condition);
	}

	@Override
	public CargoInfoStorage selectCargoInfoStorageSlave(String condition) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectOne(condition);
	}

	@Override
	public List<CargoInfoStorage> selectCargoInfoStorageList(
			Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession().selectList(map);
	}

	@Override
	public List<CargoInfoStorage> selectCargoInfoStorageListSlave(
			Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

}
