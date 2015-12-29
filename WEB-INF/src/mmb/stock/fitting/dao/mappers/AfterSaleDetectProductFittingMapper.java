package mmb.stock.fitting.dao.mappers;

import java.util.Map;

import mmb.stock.fitting.dao.AfterSaleDetectProductFittingDao;
import mmb.stock.fitting.model.AfterSaleDetectProductFitting;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class AfterSaleDetectProductFittingMapper extends AbstractDaoSupport implements AfterSaleDetectProductFittingDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(AfterSaleDetectProductFitting record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(AfterSaleDetectProductFitting record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AfterSaleDetectProductFitting selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(AfterSaleDetectProductFitting record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(AfterSaleDetectProductFitting record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, Object> getAfterSaleDetectProductFitting(Map<String, Object> condition) {
		return getSession(DynamicDataSource.SLAVE).selectOne(condition);
	}

	@Override
	public Map<String, Object> getAfterSalebackUserDetect(Map<String, Object> condition) {
		return getSession(DynamicDataSource.SLAVE).selectOne(condition);
	}

}
