package mmb.rec.oper.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.rec.oper.dao.CargoDeptDao;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import adultadmin.bean.cargo.CargoDeptBean;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class CargoDeptMapper extends AbstractDaoSupport implements CargoDeptDao {

	@Override
	public int addCargoDept(CargoDeptBean cargoDeptBean) {
		getSession().insert(cargoDeptBean);
		return cargoDeptBean.getId();
	}

	@Override
	public CargoDeptBean getCargoDept(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
	public List<CargoDeptBean> getCargoDeptList(
			Map<String, String> paramMap) {
		return getSession().selectList(paramMap);
	}

	@Override
	public List<CargoDeptBean> getCargoDeptListSlave(
			Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

}
