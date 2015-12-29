package mmb.tms.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.tms.dao.DeliverAdminUserDao;
import mmb.tms.model.DeliverAdminUser;

@Repository
public class DeliverAdminUserMapper extends AbstractDaoSupport implements DeliverAdminUserDao{

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(DeliverAdminUser record) {
		// TODO Auto-generated method stub
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public int insertSelective(DeliverAdminUser record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DeliverAdminUser selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(DeliverAdminUser record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(DeliverAdminUser record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Map<String,String>> getDeliverAdminUser(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public int getDeliverAdminUserCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}

	@Override
	public int updateDeliverAdminUser(Map<String, String> map) {
		// TODO Auto-generated method stub
		return ((Integer)getSession().update(map)).intValue();
	}

}
