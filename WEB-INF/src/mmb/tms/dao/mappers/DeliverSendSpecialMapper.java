package mmb.tms.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.tms.dao.DeliverSendSpecialDao;
import mmb.tms.model.DeliverSendSpecial;

@Repository
public class DeliverSendSpecialMapper extends AbstractDaoSupport implements DeliverSendSpecialDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return ((Integer)getSession().delete(id)).intValue();
	}

	@Override
	public int insert(DeliverSendSpecial record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(DeliverSendSpecial record) {
		return  ((Integer)getSession(DynamicDataSource.MASTER).insert(record)).intValue();
	}

	@Override
	public DeliverSendSpecial selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(DeliverSendSpecial record) {
		return ((Integer)getSession().update(record)).intValue();
	}

	@Override
	public int updateByPrimaryKey(DeliverSendSpecial record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<DeliverSendSpecial> getDeliverSendSpecialList(Map<String, String> deliverMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(deliverMap);
	}

	@Override
	public List<Map<String, Object>> getDeliverSendSpecialMap(Map<String, String> deliverMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(deliverMap);
	}

	@Override
	public String getDeliverAreaProvinces(String condition) {
		return getSession(DynamicDataSource.SLAVE).selectOne(condition);
	}

	@Override
	public String getDeliverAreaCities(String condition) {
		return getSession(DynamicDataSource.SLAVE).selectOne(condition);
	}


}
