package mmb.rec.oper.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.common.dao.CommonDao;
import mmb.common.service.CommonService;
import mmb.rec.oper.dao.CargoDeptAreaDao;
import mmb.stock.cargo.CargoDeptAreaBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.StringUtil;

@Service
public class CargoDeptAreaService {
	@Autowired
	public CargoDeptAreaDao cargoDeptAreaMapper;
	@Autowired
	public CommonDao commonMapper;

	public int addCargoDeptArea(CargoDeptAreaBean cargoDeptAreaBean) {
		cargoDeptAreaMapper.addCargoDeptArea(cargoDeptAreaBean);
		return cargoDeptAreaBean.getId();
	}

	public CargoDeptAreaBean getCargoDeptArea(String condition) {
		return cargoDeptAreaMapper.getCargoDeptArea(condition);
	}

	public List<CargoDeptAreaBean> getCargoDeptAreaList(String condition,int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return cargoDeptAreaMapper.getCargoDeptAreaList(paramMap);
	}
	public List<CargoDeptAreaBean> getCargoDeptAreaListSlave(String condition,int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return cargoDeptAreaMapper.getCargoDeptAreaListSlave(paramMap);
	}
	
	public int deleteCargoDeptArea(String condition) {
		Map<String,String> paramMap = CommonService.constructDeleteMap("cargo_dept_area", condition);
		return commonMapper.deleteCommon(paramMap);
	}
	
	
	public int updateCargoDeptArea(String set, String condition) {
		Map<String,String> paramMap = CommonService.constructUpdateMap("cargo_dept_area", set, condition);
		return commonMapper.updateCommon(paramMap);
	}
	
	public int getCargoDeptAreaCount(String condition ) {
		Map<String,String> paramMap = CommonService.constructCountMap("cargo_dept_area", condition);
		return commonMapper.getCommonCount(paramMap);
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class) 
	public void deleteAndAddCargoDeptArea(HttpServletRequest request, String deptId) {
		int returnValue = 0;
		if(deptId!=null && deptId.length()>0){
			deleteCargoDeptArea("dept_id="+deptId);
		}
		for (int key : ProductStockBean.areaMap.keySet()) {
			String checkBox[]=(String[])request.getParameterValues("checkBox" + key);
			if (checkBox != null) {
				for (int i = 0; i < checkBox.length; i++) {
					CargoDeptAreaBean bean = new CargoDeptAreaBean();
					bean.setArea(key);
					bean.setDeptId(StringUtil.toInt(deptId));
					bean.setStockType(StringUtil.toInt(checkBox[i]));
					CargoDeptAreaBean cadBean = getCargoDeptArea("area=" + key + " and dept_id="+deptId+" and stock_type="+checkBox[i]);
					if(cadBean==null){
						returnValue = addCargoDeptArea(bean);
						if (returnValue <= 0) {
							throw new RuntimeException("添加失败!");
						}
					}
				}
			}
		}
	}

}
