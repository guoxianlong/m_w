package mmb.tms.dao.mappers;

import java.util.List;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.tms.dao.DeliverLogDao;
import mmb.tms.model.DeliverLog;

@Repository
public class DeliverLogMapper extends AbstractDaoSupport implements DeliverLogDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(DeliverLog record) {
		return getSession().insert(record);
	}

	@Override
	public int insertSelective(DeliverLog record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DeliverLog selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(DeliverLog record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(DeliverLog record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<DeliverLog> getDeliverLogList(String condition) {
		return getSession(DynamicDataSource.SLAVE).selectList(condition);
	}

	

}
