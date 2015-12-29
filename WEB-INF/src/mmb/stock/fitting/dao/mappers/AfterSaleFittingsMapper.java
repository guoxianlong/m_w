package mmb.stock.fitting.dao.mappers;

import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.stock.fitting.dao.AfterSaleFittingsDao;
import mmb.stock.fitting.model.AfterSaleFittings;

@Repository
public class AfterSaleFittingsMapper extends AbstractDaoSupport implements AfterSaleFittingsDao {

	@Override
	public int deleteByPrimaryKey(Long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(AfterSaleFittings record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(AfterSaleFittings record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AfterSaleFittings selectByPrimaryKey(Long id) {
		return getSession(DynamicDataSource.SLAVE).selectOne(id);
	}

	@Override
	public int updateByPrimaryKeySelective(AfterSaleFittings record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(AfterSaleFittings record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, Object> getFittingName(Map<String, Object> condition) {
		return getSession(DynamicDataSource.SLAVE).selectOne(condition);
	}
}
