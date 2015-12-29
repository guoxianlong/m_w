package mmb.tms.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.tms.dao.TrunkEffectDao;
import mmb.tms.model.TrunkEffect;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class TrunkEffectMapper extends AbstractDaoSupport implements TrunkEffectDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(TrunkEffect record) {
		// TODO Auto-generated method stub
		return ((Integer)getSession().insert(record)).intValue();
	}

	@Override
	public int insertSelective(TrunkEffect record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TrunkEffect selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(TrunkEffect record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(TrunkEffect record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Map<String, String>> getTrunkEffectForLineList(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public int getTrunkEffectForLineCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}
	
	@Override
	public List<Map<String,String>> getTrunkEffect(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}
	
	@Override
	public List<Map<String, String>> getTrunkEffectByAreaAndDeliver(
			Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public List<Map<String, String>> getTrunkEffectByTrunkAndDeliverAdmin(
			Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public int updateTrunkEffect(Map<String, String> map) {
		// TODO Auto-generated method stub
		return ((Integer)getSession().update(map)).intValue();
	}

	@Override
	public List<Map<String, String>> getTrunkEffectList(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public int getTrunkEffectCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}


}
