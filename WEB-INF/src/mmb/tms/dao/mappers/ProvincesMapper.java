package mmb.tms.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.tms.dao.ProvincesDao;
import mmb.tms.model.Provinces;

@Repository
public class ProvincesMapper extends AbstractDaoSupport implements ProvincesDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(Provinces record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(Provinces record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Provinces selectByPrimaryKey(Integer id) {
		return getSession(DynamicDataSource.SLAVE).selectOne(id);
	}

	@Override
	public int updateByPrimaryKeySelective(Provinces record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(Provinces record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Provinces> getProvincesList(Map<String,String> map) {
		//return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(condition)).intValue();
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

}
