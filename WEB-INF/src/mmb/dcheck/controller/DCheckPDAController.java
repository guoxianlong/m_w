package mmb.dcheck.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.dcheck.model.DynamicCheckBean;
import mmb.dcheck.model.DynamicCheckLogBean;
import mmb.dcheck.service.DCheckLogService;
import mmb.dcheck.service.DCheckPDAService;
import mmb.dcheck.service.DCheckService;
import mmb.rec.pda.bean.JsonModel;
import mmb.rec.pda.util.JsonModelUtil;
import mmb.rec.pda.util.PDAParamCallBack;
import mmb.rec.pda.util.PDAUtil;
import mmb.rec.sys.easyui.Json;
import mmb.ware.cargo.model.CargoInfo;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.util.StringUtil;


@Controller
@RequestMapping("/dCheckPDAController")
public class DCheckPDAController {

	private static byte[] lock = new byte[0];

	@Autowired
	private DCheckPDAService dCheckPDAService;
	
	@Autowired
	private DCheckService dCheckService;

	@Autowired
	private DCheckLogService logService;
	/**
	 * 领取动态盘点单
	 * 
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/getDCheckCode")
	@ResponseBody
	public JsonModel getDCheckCode(HttpServletRequest request) {
		PDAUtil tools = new PDAUtil();
		int flag;
		int type;
		int areaId;
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			flag = JsonModelUtil.getInt(json, "flag");
			type = JsonModelUtil.getInt(json, "type");
			areaId = Integer.valueOf(json.getArea());
			// // 0客户配件库 1售后配件库
			// int groupFlag = -1;
			// if (type == 0) {
			// groupFlag = 2108;
			// } else {
			// groupFlag = 2107;
			// }
			//
			// if (!tools.checkGroupFlag(groupFlag)) {
			// return JsonModelUtil.error("您没有相应的操作权限,权限id:" + groupFlag);
			// }
		} catch (Exception e) {
			e.printStackTrace();
			return JsonModelUtil.error("参数异常!");
		}
		try {
			Json j = this.dCheckPDAService.getDCheckCode(type, areaId);
			if (j.isSuccess()) {
				return JsonModelUtil.success("code", j.getObj());
			}
			return JsonModelUtil.error(j.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
			return JsonModelUtil.error(e.getMessage());
		}
	}

	/**
	 * 获取区和巷道列表
	 * 
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/getAreaAndPassageList")
	@ResponseBody
	public JsonModel getAreaAndPassageList(HttpServletRequest request) {
		PDAUtil tools = new PDAUtil();
		int areaId;
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			areaId = Integer.valueOf(json.getArea());
		} catch (Exception e) {
			e.printStackTrace();
			return JsonModelUtil.error("参数异常!");
		}
		try {
			Map<String, Object> map = this.dCheckPDAService.getAreaAndPassage(areaId);
			if (map == null) {
				return JsonModelUtil.error("没有查询到区和巷道列表");
			}

			JsonModel model = JsonModelUtil.success("areaMap", map.get("areaMap"));
			model.getData().put("passageMap", map.get("passageMap"));
			model.getData().put("map", map.get("map"));
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			return JsonModelUtil.error(e.getMessage());
		}
	}

	/**
	 * 领取需要盘单的货位
	 * 
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/getCheckCargoList")
	@ResponseBody
	public JsonModel getCheckCargoList(HttpServletRequest request) {
		PDAUtil tools = new PDAUtil();
		HashMap<String, Object> map = new HashMap<String, Object>();

		try {
			JsonModel json = tools.getParamMap(request, -1, map, new PDAParamCallBack() {
				@Override
				public boolean getParam(JsonModel json, HashMap<String, Object> map) {
					map.put("code", JsonModelUtil.getString(json, "code"));
					map.put("flag", JsonModelUtil.getString(json, "flag"));
					map.put("type", JsonModelUtil.getString(json, "type"));
					map.put("stockArea", JsonModelUtil.getString(json, "stockArea"));
					map.put("passage", JsonModelUtil.getString(json, "passage"));
					return true;
				}
			});
			if (json.getFlag() == 0)
				return json;

		} catch (Exception e) {
			e.printStackTrace();
			return JsonModelUtil.error("参数异常!");
		}
		try {
			Json j = this.dCheckPDAService.getCheckCargoList(map);
			if (j.isSuccess()) {
				return JsonModelUtil.success("list", j.getObj());
			}
			return JsonModelUtil.error(j.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
			return JsonModelUtil.error(e.getMessage());
		}
	}


	/**
	 * 领取盘点的商品列表
	 * 
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/getCheckProductList")
	@ResponseBody
	public JsonModel getCheckProductList(HttpServletRequest request) {
		PDAUtil tools = new PDAUtil();
		HashMap<String, Object> map = new HashMap<String, Object>();

		try {
			JsonModel json = tools.getParamMap(request, -1, map, new PDAParamCallBack() {
				@Override
				public boolean getParam(JsonModel json, HashMap<String, Object> map) {
					map.put("code", JsonModelUtil.getString(json, "code"));
					map.put("flag", JsonModelUtil.getString(json, "flag"));
					map.put("type", JsonModelUtil.getString(json, "type"));
					map.put("cargo", JsonModelUtil.getString(json, "cargo"));
					return true;
				}
			});
			if (json.getFlag() == 0)
				return json;

		} catch (Exception e) {
			e.printStackTrace();
			return JsonModelUtil.error("参数异常!");
		}
		try {
			Json j = this.dCheckPDAService.getCheckProductList(map);
			if (j.isSuccess()) {
				return JsonModelUtil.success("list", j.getObj());
			}
			return JsonModelUtil.error(j.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
			return JsonModelUtil.error(e.getMessage());
		}
	}

	
	
	/**
	 * 验证商品编号，并获取商品名称
	 * 
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/getProductName")
	@ResponseBody
	public JsonModel getProductName(HttpServletRequest request) {
		PDAUtil tools = new PDAUtil();
		String code;
		try {
			JsonModel json = tools.getModelAndCheck(request, -1);
			if (json.getFlag() == 0)
				return json;
			code = JsonModelUtil.getString(json, "code");
		} catch (Exception e) {
			e.printStackTrace();
			return JsonModelUtil.error("参数异常!");
		}
		try {
			Json j = this.dCheckPDAService.getProductName(code);
			if (j.isSuccess()) {
				return JsonModelUtil.success("productName", j.getObj());
			}
			return JsonModelUtil.error(j.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
			return JsonModelUtil.error(e.getMessage());
		}
	}

	/**
	 * 完成盘点
	 * 
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/finishDynamicCheck")
	@ResponseBody
	public JsonModel finishDynamicCheck(HttpServletRequest request) {
		PDAUtil tools = new PDAUtil();
		HashMap<String, Object> map = new HashMap<String, Object>();

		try {
			JsonModel json = tools.getParamMap(request, -1, map, new PDAParamCallBack() {
				@Override
				public boolean getParam(JsonModel json, HashMap<String, Object> map) {
					map.put("code", JsonModelUtil.getString(json, "code"));
					map.put("flag", JsonModelUtil.getString(json, "flag"));
					map.put("type", JsonModelUtil.getString(json, "type"));
					map.put("cargo", JsonModelUtil.getString(json, "cargo"));
					map.put("products", JsonModelUtil.getListMap(json, "products", new String[] { "code", "count" }));
					return true;
				}
			});
			if (json.getFlag() == 0)
				return json;

		} catch (Exception e) {
			e.printStackTrace();
			return JsonModelUtil.error("参数异常!");
		}

		synchronized (lock) {
			try {
				this.dCheckPDAService.finishDynamicCheck(map, tools.getUser());
				return JsonModelUtil.success();
			} catch (Exception e) {
				e.printStackTrace();
				return JsonModelUtil.error(e.getMessage());
			}
		}
	}
	
	/** 
	 * @Description: 查询商品
	 * @return JsonModel 返回类型 
	 * @author 叶二鹏
	 * @date 2015年7月4日 上午10:10:11 
	 */
	@RequestMapping("/getProductNameByCode")
	@ResponseBody
	public JsonModel getProductNameByCode(HttpServletRequest request) {
		String code = request.getParameter("code");
		String cargo = request.getParameter("cargo");
		try {
			Json j = this.dCheckPDAService.getProductNameAndCargo(code, cargo);
			if (j.isSuccess()) {
				return JsonModelUtil.success("productName", j.getObj());
			}
			return JsonModelUtil.error(j.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
			return JsonModelUtil.error(e.getMessage());
		}
	}
	
	/** 
	 * @Description: 初始化盘点商品列表
	 * @return String 返回类型 
	 * @author 叶二鹏
	 * @date 2015年7月4日 上午10:09:41 
	 */
	@RequestMapping("/toCargoProductList")
	public String toCargoProductList(HttpServletRequest request){
		String cargo = request.getParameter("cargo");
		List<Map<String, String>> cargoProduct = this.dCheckPDAService.getCargoProduct(cargo);
		request.setAttribute("cargoProduct", cargoProduct);
		request.setAttribute("code", request.getParameter("code"));
		request.setAttribute("stockArea", request.getParameter("stockArea"));
		request.setAttribute("passage", request.getParameter("passage"));
		request.setAttribute("area", request.getParameter("area"));
		request.setAttribute("areaId", request.getParameter("areaId"));
		request.setAttribute("group", request.getParameter("group"));
		request.setAttribute("cargo", cargo);
		request.setAttribute("dynamicCheckId", request.getParameter("dynamicCheckId"));
		return "admin/dcheck/dcheckProduct";
	}
	
	/** 
	 * @description 跳转到选择盘点货位页面
	 * @param request
	 * @return
	 * @returnType String
	 * @create 2015-7-1 下午04:49:45
	 * @author gel
	 */
	@RequestMapping("/toSelectCheckCargoPage")
	public String toSelectCheckCargoPage(HttpServletRequest request) {
		int areaId;
		int type;
		try {
			// 初始化数据：大盘
			areaId = StringUtil.toInt(request.getParameter("areaId"));
			type = 2;// 大盘
			Json j = this.dCheckPDAService.getDCheck(type, areaId);
			DynamicCheckBean bean = null;
			if (j != null) {
				bean = (DynamicCheckBean) j.getObj();
			}
			
			Map<String, Object> map = this.dCheckPDAService.getAreaAndPassage(areaId);
			if (bean != null) {
				request.setAttribute("pdCode", bean.getCode());
				request.setAttribute("dynamicCheckId", bean.getId());
			}
			request.setAttribute("stockArea", StringUtil.toInt(request.getParameter("stockArea")));
			request.setAttribute("areaMap", map.get("areaMap"));
			request.setAttribute("passageMap", map.get("passageMap"));
			request.setAttribute("map", map.get("map"));
			request.setAttribute("areaId", areaId);
			request.setAttribute("passage", request.getParameter("passage"));
			request.setAttribute("area", request.getParameter("area"));
			String isBack = "";
			if (StringUtils.isNotBlank(request.getParameter("isBack"))) {
				isBack = request.getParameter("isBack");
			}
			request.setAttribute("isBack", isBack);
			request.setAttribute("group", request.getParameter("group"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "admin/dcheck/selectCheckCargo";
	}
	
	/**
	 * @description:确定分组
	 * @param request
	 * @return
	 * @returnType: String
	 * @create:2015年7月5日 下午1:13:13
	 */
	@RequestMapping("/divideIntoGroup")
	@ResponseBody
	public synchronized String divideIntoGroup(HttpServletRequest request){
		String result = "success";
		try{
			String code = request.getParameter("code");
			String flag = "1"; // 一盘
			String type = "2"; // 大盘
			String passage = request.getParameter("passage"); // 巷道
			String areaId = request.getParameter("areaId"); 
			String group = request.getParameter("group");
			String area = request.getParameter("area");
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if (user == null) {
				return "你好，请先登录！";
			}
			// 查当前选定的分组
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("code", code);
			map.put("flag", flag);
			map.put("type", type);
			map.put("areaId", areaId);
			map.put("stockArea", area);
			map.put("passage", passage);
			map.put("group", group);
			map.put("operator", user.getId());
			// 查询分组是否已经被选择，如果有别人已经选择该组，则不能选择该组
			DynamicCheckLogBean log = logService.getDynamicCheckLog(map);
			if (log == null) {
			} else {
				if (log.getOperator() != user.getId()) {
					return "分组已被他人使用盘点过！";
				}
			}
			
			if("0".equals(group)){
				map.put("group", "1");
			}else{
				map.put("group", "0");
			}
			DynamicCheckLogBean log2 = logService.getDynamicCheckLog(map);
			if (log2 == null) {
			} else {
				if (log2.getOperator() == user.getId()) {
					return "已使用另一分组盘点过！";
				} 
			}
			map.put("group", group);
			if (log == null) {
				logService.saveDynamicCheckLog(map);
			}
			return result;
		}catch(Exception e){
			e.printStackTrace();
			return "系统异常:"+e.getMessage();
		}
	}
	
	
	/** 
	 * @description 领取需要盘单的货位
	 * @param request
	 * @return
	 * @returnType JsonModel
	 * @create 2015-7-1 下午08:07:42
	 * @author gel
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getCheckCargoList2")
	@ResponseBody
	public List<String> getCheckCargoList2(HttpServletRequest request) {
		List<String> result = new ArrayList<String>();
		String code = request.getParameter("code");
		String flag = "1"; // 一盘
		String type = "2"; // 大盘
		String passage = request.getParameter("passage"); // 巷道
		String areaId = request.getParameter("areaId"); 
		String group = request.getParameter("group");
		String area = request.getParameter("area");
		try {
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if (user == null) {
				result.add("你好，请先登录！");
				return result;
			}
			
			// 查当前选定的分组
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("code", code);
			map.put("flag", flag);
			map.put("type", type);
			map.put("areaId", areaId);
			map.put("stockArea", area);
			map.put("passage", passage);
			map.put("group", group);
			map.put("operator", user.getId());
//			
//			// 查询分组是否已经被选择，如果有别人已经选择该组，则不能选择该组
//			DynamicCheckLogBean log = logService.getDynamicCheckLog(map);
//			if (log == null) {
//				
//			} else {
//				if (log.getOperator() == user.getId()) {
//					 
//				} else {
//					result.add("分组已被他人使用盘点过！");
//					return result;
//				}
//			}
//			
//			// 查另一个分组
//			HashMap<String, Object> map2 = new HashMap<String, Object>();
//			map2.put("code", code);
//			map2.put("flag", flag);
//			map2.put("type", type);
//			map2.put("areaId", areaId);
//			map2.put("stockArea", area);
//			map2.put("passage", passage);
//			map2.put("group", "0".equals(group) ? 1: 0);
//			map2.put("operator", user.getId());
//			
//			DynamicCheckLogBean log2 = logService.getDynamicCheckLog(map2);
//			if (log2 == null) {
//				
//			} else {
//				if (log2.getOperator() == user.getId()) {
//					result.add("已使用另一分组盘点过！");
//					return result;
//				} 
//			}
//			
//			if (log == null) {
//				logService.saveDynamicCheckLog(map);
//			}
			
			Json j = this.dCheckPDAService.getCheckCargoList(map);
			if (j.isSuccess()) {
				result = (List<String>)j.getObj();
			} else {
				result = Collections.EMPTY_LIST;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (result != null && result.size() == 0) {
			result = new ArrayList<String>();
			result.add("没有查到货位！");
		}
		
		return result;
	}
	
	/** 
	 * @description 
	 * @param request
	 * @return
	 * @returnType JsonModel
	 * @create 2015-7-2 下午06:08:15
	 * @author gel
	 */
	@RequestMapping("/finishDynamicCheck2")
	@ResponseBody
	public JsonModel finishDynamicCheck2(HttpServletRequest request) {
		String flag = "1"; // 一盘
		String type = "2"; // 大盘
		String code = request.getParameter("code");
		String stockArea = request.getParameter("stockArea"); //发货地
		String passage = request.getParameter("passage"); // 巷道
		String areaId = request.getParameter("area"); // 区域
		String group = request.getParameter("group");
		String cargo = request.getParameter("cargo");
		int dynamicCheckId = StringUtil.toInt(request.getParameter("dynamicCheckId"));
		String codes = request.getParameter("codes");
		String counts = request.getParameter("counts");
		List<HashMap<String, String>> products = this.constructProducts(codes, counts);
		
		try {
			voUser user = (voUser) request.getSession().getAttribute("userView");
			
			if (user == null) {
				return JsonModelUtil.error("你好，请先登录！");
			}
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("code", code);
			map.put("flag", flag);
			map.put("type", type);
			map.put("areaId", stockArea);
			map.put("stockArea", areaId);
			map.put("passage", passage);
			map.put("group", group);
			map.put("operator", user.getId());
			map.put("cargo", cargo);
			map.put("dynamicCheckId", dynamicCheckId);
			map.put("products", products);
			
			synchronized (lock) {
				try {
					this.dCheckPDAService.finishDynamicCheck2(map, user);
					return JsonModelUtil.success();
				} catch (Exception e) {
					e.printStackTrace();
					return JsonModelUtil.error(e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	private List<HashMap<String, String>> constructProducts(String codes, String counts) {
		if (codes == null || counts == null) {
			throw new RuntimeException("商品条码或者数量为空！");
		}
		List<HashMap<String, String>> products = new ArrayList<HashMap<String, String>>();
		
		String[] barCodes = {};
		String[] productCounts = {};
		if (StringUtils.isNotBlank(codes)) {
			barCodes = codes.split(",");
		}
		if (StringUtils.isNotBlank(counts)) {
			productCounts = counts.split(",");
		}
		
		if (barCodes.length != productCounts.length) {
			throw new RuntimeException("商品条码个数和对应的数量个数不对应！");
		}
		
		for (int i=0; i<barCodes.length; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("code", barCodes[i]);
			map.put("count", productCounts[i]);
			products.add(map);
		}
		
		return products;
	}
	
	@RequestMapping("/checkCargoExist")
	@ResponseBody
	public String checkCargoExist(HttpServletRequest req) {
		String cargo = req.getParameter("cargo"); // 货位编号
		String areaId = req.getParameter("areaId"); // 库区
		String passageId = req.getParameter("passageId"); // 货位巷道
		String stockArea = req.getParameter("area"); // 货位区域
		
		StringBuilder sb = new StringBuilder();
		sb.append(" whole_code = '").append(StringUtil.dealParam(cargo)).append("' ");
		sb.append(" AND store_type <> ").append(CargoInfoBean.STORE_TYPE2);
		sb.append(" AND stock_type = ").append(CargoInfoBean.STOCKTYPE_QUALIFIED);
		sb.append(" AND area_id = ").append(areaId);
		sb.append(" AND passage_id = ").append(passageId);
		sb.append(" AND stock_area_id = ").append(stockArea);
		CargoInfo cargoInfo = dCheckService.getCargoByCondition(sb.toString());
		return cargoInfo == null ? "此区域没有查到此货位！" : "";
	}
}
