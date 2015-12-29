package mmb.tms.dao.mappers;

import mmb.tms.dao.DeliverBalanceTypeDao;
import mmb.tms.model.DeliverBalanceType;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class DeliverBalanceTypeMapper extends AbstractDaoSupport implements DeliverBalanceTypeDao{

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(DeliverBalanceType record) {
		return getSession().insert(record);
	}

	@Override
	public int insertSelective(DeliverBalanceType record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DeliverBalanceType selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(DeliverBalanceType record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(DeliverBalanceType record) {
		return getSession().update(record);
	}

	@Override
	public DeliverBalanceType getDeliverBalanceTypeByDeliverId(Integer deliverId) {
		return getSession(DynamicDataSource.SLAVE).selectOne(deliverId);
	}

}
