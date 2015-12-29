package mmb.tms.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.tms.dao.TrunkLineDao;
import mmb.tms.model.TrunkCorpInfo;
import mmb.tms.model.TrunkEffectForExcel;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import adultadmin.action.vo.voUser;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class TrunkLineMapper extends AbstractDaoSupport implements TrunkLineDao {

	@Override
	public int addTrunk(TrunkCorpInfo t) {
		// TODO Auto-generated method stub
		return ((Integer)getSession().insert(t)).intValue();
	}

	@Override
	public List<TrunkCorpInfo> getTrunkCorpInfo(Map<String, String> map) {
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public int upDateTrunkCorpInfo(Map<String, String> map) {
		// TODO Auto-generated method stub
		return ((Integer)getSession().update(map)).intValue();
	}

	@Override
	public int getTrunkCorpInfoCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}

}
