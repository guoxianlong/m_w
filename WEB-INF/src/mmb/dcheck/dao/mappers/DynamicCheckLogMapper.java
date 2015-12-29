package mmb.dcheck.dao.mappers;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.dcheck.dao.DynamicCheckLogDao;
import mmb.dcheck.model.DynamicCheckLogBean;

/**
 * @description 
 * @create 2015-7-2 下午04:03:28
 * @author gel
 */
@Repository
public class DynamicCheckLogMapper extends AbstractDaoSupport implements DynamicCheckLogDao {

	@Override
	public DynamicCheckLogBean getDynamicCheckLog(DynamicCheckLogBean log) {
		return getSession(DynamicDataSource.SLAVE).selectOne(log);
	}

	@Override
	public int saveDynamicCheckLog(DynamicCheckLogBean bean) {
		getSession().insert(bean);
		return bean.getId();
	}

	@Override
	public Long getDynamicCheckLogCount(HashMap<String, Object> condition) {
		return getSession(DynamicDataSource.SLAVE).selectOne(condition);
	}

	@Override
	public List<HashMap<String, Object>> getDynamicCheckLogLst(HashMap<String, Object> condition) {
		return getSession(DynamicDataSource.SLAVE).selectList(condition);
	}

	@Override
	public int delDynamicCheckLog(Long id) {
		return getSession().delete(id);
	}

	@Override
	public Long getCheckDataCountByLogId(Long id) {
		return getSession(DynamicDataSource.SLAVE).selectOne(id);
	}
}
