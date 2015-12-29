package mmb.rec.oper.service;

import java.util.List;
import java.util.Map;

import mmb.common.dao.CommonDao;
import mmb.common.service.CommonService;
import mmb.rec.oper.dao.CargoDeptDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.bean.cargo.CargoDeptBean;

@Service
public class CargoDeptService {
	@Autowired
	public CargoDeptDao cargoDeptMapper;
	@Autowired
	public CommonDao commonMapper;

	public int addCargoDept(CargoDeptBean cargoDeptBean) {
		cargoDeptMapper.addCargoDept(cargoDeptBean);
		return cargoDeptBean.getId();
	}

	public CargoDeptBean getCargoDept(String condition) {
		return cargoDeptMapper.getCargoDept(condition);
	}

	public List<CargoDeptBean> getCargoDeptList(String condition,int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return cargoDeptMapper.getCargoDeptList(paramMap);
	}
	public List<CargoDeptBean> getCargoDeptListSlave(String condition,int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return cargoDeptMapper.getCargoDeptListSlave(paramMap);
	}
	
	public int deleteCargoDept(String condition) {
		Map<String,String> paramMap = CommonService.constructDeleteMap("cargo_dept", condition);
		return commonMapper.deleteCommon(paramMap);
	}
	
	public int updateCargoDept(String set, String condition) {
		Map<String,String> paramMap = CommonService.constructUpdateMap("cargo_dept", set, condition);
		return commonMapper.updateCommon(paramMap);
	}
	
	public int getCargoDeptCount(String condition ) {
		Map<String,String> paramMap = CommonService.constructCountMap("cargo_dept", condition);
		return commonMapper.getCommonCount(paramMap);
	}

}
