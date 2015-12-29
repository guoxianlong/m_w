package mmb.rec.oper.dao;

import java.util.List;
import java.util.Map;

import mmb.stock.cargo.CargoDeptAreaBean;

public interface CargoDeptAreaDao {

	public int addCargoDeptArea(CargoDeptAreaBean cargoDeptAreaBean);

	public CargoDeptAreaBean getCargoDeptArea(String condition);

	public List<CargoDeptAreaBean> getCargoDeptAreaList(Map<String, String> paramMap);

	public List<CargoDeptAreaBean> getCargoDeptAreaListSlave(
			Map<String, String> paramMap);

}
