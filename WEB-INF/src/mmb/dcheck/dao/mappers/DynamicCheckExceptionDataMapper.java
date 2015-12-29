package mmb.dcheck.dao.mappers;

import java.util.HashMap;
import java.util.List;

import mmb.dcheck.dao.DynamicCheckExceptionDataDao;
import mmb.dcheck.model.DynamicCheckExceptionData;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class DynamicCheckExceptionDataMapper extends AbstractDaoSupport implements DynamicCheckExceptionDataDao{
	@Override
	public Long getExceptionDataCount(HashMap<String, Object> condition) {
		return getSession(DynamicDataSource.SLAVE).selectOne(condition);
	}

	@Override
	public List<HashMap<String, Object>> getExceptionDataLst(HashMap<String, Object> condition) {
		return getSession(DynamicDataSource.SLAVE).selectList(condition);
	}

	@Override
	public int saveDynamicCheckExceptionData(DynamicCheckExceptionData bean) {
		getSession().insert(bean);
		return bean.getId();
	}

	@Override
	public int deleteDynamciCheckExceptionData(DynamicCheckExceptionData bean) {
		getSession().delete(bean);
		return bean.getId();
	}

	@Override
	public Integer existExceptionCargo(String wholeCode) {
		return getSession(DynamicDataSource.SLAVE).selectOne(wholeCode);
	}

	@Override
	public List<String> getExceptionCargoCode(int checkId) {
		return getSession().selectList(checkId);
	}

	
}
