package mmb.tms.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.tms.dao.DeliverAdminUserLogDao;
import mmb.tms.model.DeliverAdminUserLog;

@Repository
public class DeliverAdminUserLogMapper extends AbstractDaoSupport implements DeliverAdminUserLogDao {

	@Override
	public int addDeliverAdminUserLog(DeliverAdminUserLog deliverAdminUserLog) {
		// TODO Auto-generated method stub
		return ((Integer)getSession().insert(deliverAdminUserLog)).intValue();
	}

	@Override
	public List<Map<String, String>> getDeliverAdminUserLog(
			Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public int getDeliverAdminUserCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}

}
