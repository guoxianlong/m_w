package mmb.dcheck.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import adultadmin.util.StringUtil;
import mmb.dcheck.dao.DynamicCheckLogDao;
import mmb.dcheck.model.DynamicCheckLogBean;

/**
 * @description 
 * @create 2015-7-2 下午04:23:40
 * @author gel
 */
@Service
public class DynamicCheckLogServiceImpl implements DCheckLogService {
	
	@Resource
	DynamicCheckLogDao dao;

	@Override
	public DynamicCheckLogBean getDynamicCheckLog(
			Map<String, Object> map) {
		String dynamicCheckCode = "" + map.get("code");
		int passageId = StringUtil.toInt("" + map.get("passage"));
		int stockArea = StringUtil.toInt("" + map.get("stockArea"));
		int group = StringUtil.toInt("" + map.get("group"));
		int operator = StringUtil.toInt("" + map.get("operator"));
		
		DynamicCheckLogBean param = new DynamicCheckLogBean();
		param.setCargoInfoAreaId(stockArea);
		param.setCargoInfoPassageId(passageId);
		param.setOperator(operator);
		param.setGroupId(group);
		param.setDynamicCheckCode(dynamicCheckCode);
		
		return dao.getDynamicCheckLog(param);
	}

	@Override
	public int saveDynamicCheckLog(Map<String, Object> map) {
		String dynamicCheckCode = "" + map.get("code");
		int passageId = StringUtil.toInt("" + map.get("passage"));
		int areaId = StringUtil.toInt("" + map.get("areaId"));
		int group = StringUtil.toInt("" + map.get("group"));
		int operator = StringUtil.toInt("" + map.get("operator"));
		int stockArea = StringUtil.toInt("" + map.get("stockArea"));
		
		DynamicCheckLogBean param = new DynamicCheckLogBean();
		param.setCargoInfoAreaId(stockArea);
		param.setCargoInfoPassageId(passageId);
		param.setOperator(operator);
		param.setGroupId(group);
		param.setStockAreaId(areaId);
		param.setDynamicCheckCode(dynamicCheckCode);
		
		return dao.saveDynamicCheckLog(param);
	}

	@Override
	public int delDynamicCheckLog(Long id) {		
		return dao.delDynamicCheckLog(id);
	}

}
