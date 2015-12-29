package mmb.ware.cargo.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.ware.cargo.dao.CargoInfoStockAreaDao;
import mmb.ware.cargo.model.CargoInfoStockArea;

@Repository
public class CargoInfoStockAreaMapper extends AbstractDaoSupport implements CargoInfoStockAreaDao {

	@Override
	public int insertCargoInfoStockArea(CargoInfoStockArea record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public CargoInfoStockArea selectCargoInfoStockArea(String condition) {
		// TODO Auto-generated method stub
		return getSession().selectOne(condition);
	}

	@Override
	public CargoInfoStockArea selectCargoInfoStockAreaSlave(String condition) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectOne(condition);
	}

	@Override
	public List<CargoInfoStockArea> selectCargoInfoStockAreaList(
			Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession().selectList(map);
	}

	@Override
	public List<CargoInfoStockArea> selectCargoInfoStockAreaListSlave(
			Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}


}
