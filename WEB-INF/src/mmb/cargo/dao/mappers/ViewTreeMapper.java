package mmb.cargo.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.cargo.dao.ViewTreeDao;
import mmb.cargo.model.ViewTree;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class ViewTreeMapper extends AbstractDaoSupport implements ViewTreeDao {

	@Override
	public List<ViewTree> getViewTreeList(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public ViewTree getViewTreeForName(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectOne(map);
	}

}
