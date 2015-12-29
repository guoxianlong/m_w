package mmb.tms.dao.mappers;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.tms.dao.DeliverKpiDao;
import mmb.tms.model.DeliverKpi;
@Repository
public class DeliverKpiMapper extends AbstractDaoSupport implements DeliverKpiDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(DeliverKpi record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public int insertSelective(DeliverKpi record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DeliverKpi selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(DeliverKpi record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(DeliverKpi record) {
		return getSession().update(record);
	}

	@Override
	public List<DeliverKpi> getDeliverKpiList(HashMap map) {
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

}
