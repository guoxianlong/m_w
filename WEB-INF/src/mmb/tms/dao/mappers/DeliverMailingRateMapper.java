package mmb.tms.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.tms.dao.DeliverMailingRateDao;
import mmb.tms.model.AuditOrderStat;
import mmb.tms.model.DeliverMailingRate;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class DeliverMailingRateMapper extends AbstractDaoSupport implements DeliverMailingRateDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(DeliverMailingRate record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(DeliverMailingRate record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DeliverMailingRate selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(DeliverMailingRate record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(DeliverMailingRate record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<DeliverMailingRate> getDeliverMailingRateList(Map<String, String> conditionMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(conditionMap);
	}

}
