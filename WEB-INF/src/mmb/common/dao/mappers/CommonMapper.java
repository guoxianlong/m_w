package mmb.common.dao.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.common.dao.CommonDao;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class CommonMapper extends AbstractDaoSupport implements CommonDao {

	@Override
	public int deleteCommon(Map<String, String> paramMap) {
		return ((Integer)getSession().delete(paramMap)).intValue();
	}

	@Override
	public int updateCommon(Map<String, String> paramMap) {
		return ((Integer)getSession().update(paramMap)).intValue();
	}
	
	@Override
	public int insertCommon(Map<String, String> paramMap) {
		return ((Integer)getSession().insert(paramMap)).intValue();
	}

	@Override
	public int getCommonCount(Map<String, String> paramMap) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(paramMap)).intValue();
	}

	@Override
	public List<HashMap<String, String>> getCommonInfo(
			Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	@Override
	public List<HashMap<String, Object>> getCommonInfoCount(
			Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	@Override
	public List<HashMap<String, String>> getPOPCommonInfo(HashMap<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}
}
