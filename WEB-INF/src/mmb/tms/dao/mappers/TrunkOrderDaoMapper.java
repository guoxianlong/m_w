package mmb.tms.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.tms.dao.TrunkOrderDao;
import mmb.tms.model.TrunkOrder;
import mmb.tms.model.TrunkOrderInfo;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class TrunkOrderDaoMapper extends AbstractDaoSupport implements TrunkOrderDao{

	@Override
	public List<TrunkOrder> qryTrunkOrderLs(Map<String, Object> map) {
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public Long qryTrunkOrderLsTotal(Map<String, Object> map) {
		return getSession(DynamicDataSource.SLAVE).selectOne(map);
	}
	
	@Override
	public List<Map<String, Object>> qryMailingBatchPackage(Map<String,Object> map) {
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public Long qryMailingBatchPackageTotal(Map<String, Object> map) {
		return getSession(DynamicDataSource.SLAVE).selectOne(map);
	}

	@Override
	public int insertTrunkOrderInfo(TrunkOrderInfo record) {
		getSession().update(record);
		return record.getId();
	}

	@Override
	public int updateByPrimaryKeySelective(TrunkOrder record) {
		return getSession().update(record);
	}

	@Override
	public TrunkOrder qryTrunkOrderByPK(Integer trunkOrderId) {
		return getSession(DynamicDataSource.SLAVE).selectOne(trunkOrderId);
	}

	@Override
	public List<Map<String, Object>> qryTrunkOrderInfoLs(Map<String, Object> map) {
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public Long qryTrunkOrderInfoLsTotal(Map<String, Object> map) {
		return getSession(DynamicDataSource.SLAVE).selectOne(map);
	}

}
