package mmb.tms.dao.mappers;

import java.util.List;

import java.util.Map;

import mmb.tms.dao.AuditOrderStatDao;
import mmb.tms.model.AuditOrderStat;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class AuditOrderStatMapper extends AbstractDaoSupport implements
		AuditOrderStatDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(AuditOrderStat record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(AuditOrderStat record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AuditOrderStat selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(AuditOrderStat record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(AuditOrderStat record) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public List<AuditOrderStat> getAuditOrderStatList(Map<String, String> conditionMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(conditionMap);
	}

}
