package mmb.rec.oper.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.rec.oper.dao.CargoDeptAreaDao;
import mmb.stock.cargo.CargoDeptAreaBean;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class CargoDeptAreaMapper extends AbstractDaoSupport implements CargoDeptAreaDao {

	@Override
	public int addCargoDeptArea(CargoDeptAreaBean cargoDeptAreaBean) {
		getSession().insert(cargoDeptAreaBean);
		return cargoDeptAreaBean.getId();
	}

	@Override
	public CargoDeptAreaBean getCargoDeptArea(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
	public List<CargoDeptAreaBean> getCargoDeptAreaList(
			Map<String, String> paramMap) {
		return getSession().selectList(paramMap);
	}

	@Override
	public List<CargoDeptAreaBean> getCargoDeptAreaListSlave(
			Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

}
