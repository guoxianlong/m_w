package mmb.rec.oper.dao;

import java.util.List;
import java.util.Map;

import adultadmin.bean.cargo.CargoDeptBean;

public interface CargoDeptDao {

	public int addCargoDept(CargoDeptBean cargoDeptBean);

	public CargoDeptBean getCargoDept(String condition);

	public List<CargoDeptBean> getCargoDeptList(Map<String, String> paramMap);

	public List<CargoDeptBean> getCargoDeptListSlave(
			Map<String, String> paramMap);

}
