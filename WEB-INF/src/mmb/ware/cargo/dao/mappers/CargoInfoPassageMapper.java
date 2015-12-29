package mmb.ware.cargo.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.ware.cargo.dao.CargoInfoPassageDao;
import mmb.ware.cargo.model.CargoInfoPassage;

@Repository
public class CargoInfoPassageMapper extends AbstractDaoSupport  implements CargoInfoPassageDao {

	@Override
	public int insertCargoInfoPassage(CargoInfoPassage record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public CargoInfoPassage selectCargoInfoPassage(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
	public CargoInfoPassage selectCargoInfoPassageSlave(String condition) {
		return getSession(DynamicDataSource.SLAVE).selectOne(condition);
	}

	@Override
	public List<CargoInfoPassage> selectCargoInfoPassageList(
			Map<String, String> map) {
		return getSession().selectList(map);
	}

	@Override
	public List<CargoInfoPassage> selectCargoInfoPassageListSlave(
			Map<String, String> map) {
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

}
