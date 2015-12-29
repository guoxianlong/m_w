package mmb.tms.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.tms.dao.DeliverTransitRateDao;
import mmb.tms.model.DeliverTransitRate;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class DeliverTransitRateMapper extends AbstractDaoSupport implements DeliverTransitRateDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(DeliverTransitRate record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(DeliverTransitRate record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DeliverTransitRate selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(DeliverTransitRate record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(DeliverTransitRate record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<DeliverTransitRate> getDeliverTransiteRateList(Map<String, String> conditionMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(conditionMap);
	}

}
