package mmb.tms.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.tms.dao.ProvinceCityDao;
import mmb.tms.model.ProvinceCity;

@Repository
public class ProvinceCityMapper extends AbstractDaoSupport implements ProvinceCityDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(ProvinceCity record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(ProvinceCity record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ProvinceCity selectByPrimaryKey(Integer id) {
		return getSession(DynamicDataSource.SLAVE).selectOne(id);
	}

	@Override
	public int updateByPrimaryKeySelective(ProvinceCity record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(ProvinceCity record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<ProvinceCity> getProvinceCityList(Map<String, String> cityMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(cityMap);
	}

}
