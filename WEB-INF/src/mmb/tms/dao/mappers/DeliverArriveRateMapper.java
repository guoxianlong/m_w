package mmb.tms.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.tms.dao.DeliverArriveRateDao;
import mmb.tms.model.DeliverArriveRate;

@Repository
public class DeliverArriveRateMapper extends AbstractDaoSupport implements DeliverArriveRateDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(DeliverArriveRate record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(DeliverArriveRate record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DeliverArriveRate selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(DeliverArriveRate record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(DeliverArriveRate record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<DeliverArriveRate> getDeliverArriveRateList(Map<String, String> conditionMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(conditionMap);
	}

}
