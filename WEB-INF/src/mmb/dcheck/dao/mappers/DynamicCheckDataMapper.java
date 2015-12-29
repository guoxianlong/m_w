package mmb.dcheck.dao.mappers;

import java.util.List;

import mmb.dcheck.dao.DynamicCheckDataDao;
import mmb.dcheck.model.DynamicCheckData;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

/**
 * @description 
 * @create 2015-7-2 下午04:03:28
 * @author gel
 */
@Repository
public class DynamicCheckDataMapper extends AbstractDaoSupport implements DynamicCheckDataDao {

	@Override
	public DynamicCheckData getDynamicCheckData(DynamicCheckData data) {
		List<DynamicCheckData> list = getSession(DynamicDataSource.SLAVE).selectList(data);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public int saveDynamicCheckData(DynamicCheckData bean) {
		getSession().insert(bean);
		return bean.getId();
	}

	@Override
	public int deleteDynamicCheckData(int id) {
		getSession().delete(id);
		return id;
	}

}
