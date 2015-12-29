package mmb.stock.fitting.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.rec.pda.bean.JsonModel;
import mmb.rec.pda.util.JsonModelUtil;
import mmb.rec.pda.util.PDAUtil;
import mmb.rec.sys.easyui.Json;
import mmb.stock.fitting.model.FittingOutBean;
import mmb.stock.fitting.service.FittingPDAService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.util.StringUtil;

@RequestMapping("fittingPDAController")
@Controller
public class FittingPDAController {

	private static byte[] lock = new byte[0];
	
	@Autowired
	private FittingPDAService pdaService;

	/**
	 * 配件更换货位
	 * 
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/changeCargo")
	@ResponseBody
	public JsonModel changeCargo(HttpServletRequest request) {
		PDAUtil tools = new PDAUtil();
		String outWholeCode;
		String inWholeCode;
		String code;
		int count;
		int type;

		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			outWholeCode = JsonModelUtil.getString(json, "outWholeCode");
			inWholeCode = JsonModelUtil.getString(json, "inWholeCode");
			code = JsonModelUtil.getString(json, "code");
			count = JsonModelUtil.getInt(json, "count");
			type = JsonModelUtil.getInt(json, "type");
			
			// 0客户配件库 1售后配件库
			int groupFlag = -1;
			if (type == 0) {
				groupFlag = 2108;
			} else {
				groupFlag = 2107;
			}
			
			if (!tools.checkGroupFlag(groupFlag)) {
				return JsonModelUtil.error("您没有相应的操作权限,权限id:" + groupFlag);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return JsonModelUtil.error("参数异常!");
		}
		synchronized (lock) {			
			try {
				pdaService.changeCargo(outWholeCode, inWholeCode, code, count, type, tools.getUser());
				return JsonModelUtil.success();
			} catch (Exception e) {
				e.printStackTrace();
				return JsonModelUtil.error(e.getMessage());
			}			
		}
	}
 

	/**
	 * 配件出库(领单)
	 * 
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/getFittingOutList")
	@ResponseBody
	public JsonModel getFittingOutList(HttpServletRequest request) {
		PDAUtil tools = new PDAUtil();
		String code;
		try {
			JsonModel json = tools.getModelAndCheck(request, 2109);
			if (json.getFlag() == 0)
				return json;
			code = JsonModelUtil.getString(json, "code");
		} catch (Exception e) {
			e.printStackTrace();
			return JsonModelUtil.error("参数异常!");
		}
		
		try {
			List<FittingOutBean> list = this.pdaService.getFittingOutList(code);
			
			if (list == null || list.size() == 0) {
				return JsonModelUtil.error("没有查询到可以出库的配件");
			}
			
			return JsonModelUtil.success("list", list);
		} catch (Exception e) {
			e.printStackTrace();
			return JsonModelUtil.error(e.getMessage());
		}
	}
	
	/**
	 * 配件出库
	 * 
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/fittingOut")
	@ResponseBody
	public JsonModel fittingOut(HttpServletRequest request) {
		PDAUtil tools = new PDAUtil();
		String code = "1";
		List<HashMap<String, String>> mapList = null;
		try {
			JsonModel json = tools.getModelAndCheck(request, 2109);
			if (json.getFlag() == 0)
				return json;
			code = JsonModelUtil.getString(json, "code");
			mapList = JsonModelUtil.getListMap(json, "list", new String[] { "wholeCode", "code", "count" });
			if (mapList == null || mapList.size() == 0) {
				return JsonModelUtil.error("请至少出库一个配件");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return JsonModelUtil.error("参数异常!");
		}
		
		synchronized (lock) {		
			try {
				List<FittingOutBean> beanList = this.pdaService.getFittingOutList(code);			
				if (beanList == null || beanList.size() == 0) {
					return JsonModelUtil.error("没有查询到可以出库的配件");
				}			
				HashMap<String, Integer> tempMap = UnionBeanCount(beanList);
				HashMap<String, Integer> outMap = UnionMapCount(mapList);
				if (!compareCount(tempMap, outMap)) {
					return JsonModelUtil.error("请确认配件出库数量是否正确");
				}
				
				this.pdaService.fittingOut(code, mapList, beanList, tools.getUser());
				return JsonModelUtil.success();
			} catch (Exception e) {
				e.printStackTrace();
				return JsonModelUtil.error(e.getMessage());
			}	
		}		
	}


	private HashMap<String, Integer> UnionMapCount(List<HashMap<String, String>> tempList) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (HashMap<String, String> hashMap : tempList) {			
			Integer n = Integer.valueOf(hashMap.get("count"));
			if (!map.containsKey(hashMap.get("code"))) {
				map.put(hashMap.get("code"), n);
			} else {			
				map.put(hashMap.get("code"), (map.get(hashMap.get("code")) + n));
			}
		}
		return map;
	}
	
	private HashMap<String, Integer> UnionBeanCount(List<FittingOutBean> tempList) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (FittingOutBean bean : tempList) {
			Integer n = Integer.valueOf(bean.getCount());
			if (!map.containsKey(bean.getCode())) {
				map.put(bean.getCode(), n);
			} else {			
				map.put(bean.getCode(), (map.get(bean.getCode()) + n));
			}
		}
		return map;
	}
	
	// 比较数量是否相等
	private boolean compareCount(HashMap<String, Integer> map1, HashMap<String, Integer> map2) {
		if (map1.size() != map2.size()) {
			return false;
		}
		for (Map.Entry<String, Integer> m : map1.entrySet()) {
			if(!map2.containsKey(m.getKey()) || map2.get(m.getKey()) != m.getValue()){
				return false;
			}
		}
		return true;
	}
}
