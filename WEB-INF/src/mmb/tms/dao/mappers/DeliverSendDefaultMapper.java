package mmb.tms.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.tms.dao.DeliverSendDefaultDao;
import mmb.tms.model.DeliverSendDefault;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class DeliverSendDefaultMapper extends AbstractDaoSupport implements DeliverSendDefaultDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return ((Integer)getSession().delete(id)).intValue();
	}

	@Override
	public int insert(DeliverSendDefault record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(DeliverSendDefault record) {
		 return ((Integer)getSession().insert(record)).intValue();
	}

	@Override
	public DeliverSendDefault selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(DeliverSendDefault record) {
		return ((Integer)getSession().update(record)).intValue();
	}

	@Override
	public int updateByPrimaryKey(DeliverSendDefault record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<DeliverSendDefault> getDeliverSendDefaultList(Map<String, String> defaultMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(defaultMap);
	}

	@Override
	public List<Map<String, Object>> getDeliverSendDefaultMap(Map<String, String> defaultMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(defaultMap);
	}

}
