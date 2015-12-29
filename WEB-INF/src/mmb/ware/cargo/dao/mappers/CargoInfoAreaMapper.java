package mmb.ware.cargo.dao.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.ware.cargo.dao.CargoInfoAreaDao;
import mmb.ware.cargo.model.CargoInfoArea;

@Repository
public class CargoInfoAreaMapper extends AbstractDaoSupport implements CargoInfoAreaDao {

	@Override
	public int insertCargoInfoArea(CargoInfoArea record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public CargoInfoArea selectCargoInfoArea(String condition) {
		// TODO Auto-generated method stub
		return getSession().selectOne(condition);
	}
	@Override
	public CargoInfoArea selectByPrimaryKey(Integer id) {
		Map<String, Integer> map=new HashMap<String, Integer>();
		map.put("id", id);
		return getSession().selectOne(map);
	}
	@Override
	public CargoInfoArea selectCargoInfoAreaSlave(String condition) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectOne(condition);
	}

	@Override
	public List<CargoInfoArea> selectCargoInfoAreaList(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession().selectList(map);
	}

	@Override
	public List<CargoInfoArea> selectCargoInfoAreaListSlave(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

}
