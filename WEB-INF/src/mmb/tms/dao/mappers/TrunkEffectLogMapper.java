package mmb.tms.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.tms.dao.TrunkEffectLogDao;
import mmb.tms.model.TrunkEffectLog;
import mmb.tms.service.ITrunkEffectLogService;

@Repository
public class TrunkEffectLogMapper extends AbstractDaoSupport implements TrunkEffectLogDao {

	@Override
	public int addTrunkEffectLog(TrunkEffectLog trunkEffectLog) {
		// TODO Auto-generated method stub
		return ((Integer)getSession().insert(trunkEffectLog)).intValue();
	}

	@Override
	public List<Map<String, String>> getTrunkEffectLog(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public int getTrunkEffectLogCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}

}
