/**  
 * @Description: 
 * @author 叶二鹏   
 * @date 2015年10月14日 上午10:36:47 
 * @version V1.0   
 */
package cn.mmb.productarrival.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mmb.rec.sys.easyui.EasyuiDataGridJson;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voUser;
import adultadmin.util.DateUtil;
import cache.ProductLinePermissionCache;
import cn.mmb.productarrival.application.ArrivalApplication;
import cn.mmb.productarrival.domain.model.ArrivalMessageModel;
import cn.mmb.productarrival.domain.service.ArrivalServiceImpl;
import cn.mmb.productarrival.infrastructrue.transdto.EasyuiPage;
import cn.mmb.productarrival.infrastructrue.transdto.QueryParams;

/** 
 * @ClassName: ProductArrivalController 
 * @Description: 商品到货信息controller
 * @author: 叶二鹏
 * @date: 2015年10月14日 上午10:36:47  
 */
@Controller
@RequestMapping("/productArrivalController")
public class ProductArrivalController {
	
	@Resource
	private ArrivalApplication arrivalApplication;
	
	@Resource
	private ArrivalServiceImpl arrivalServiceImpl;
	
	/** 
	 * @Description: 跳转到商品到货信息列表
	 * @return ModelAndView 返回类型 
	 * @author 叶二鹏
	 * @date 2015年10月14日 下午2:18:41 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/toArrivalList")
	public ModelAndView toArrivalList(HttpServletRequest request){
		
		Map<String, Object> model = new HashMap<String, Object>();
		//获取产品线信息
		List<voProductLine> productLineList = ProductLinePermissionCache.getAllProductLineList();
		model.put("productLineList", productLineList);
		
		//供应商列表
		List<Map<String, Object>> supplierList = arrivalServiceImpl.getSupplier();
		Map<String, Object> none = new HashMap<String, Object>();
		none.put("id", -10);
		none.put("name", "无");
		none.put("name_abbreviation", "w");
		supplierList.add(0, none);
		model.put("supplierList", supplierList);

		return new ModelAndView("admin/product/arrivalMessage/arrivalMessageList",model);
	}
	
	/** 
	 * @Description: 分页获取到货信息列表
	 * @return EasyuiPage<ArrivalMessageModel> 返回类型 
	 * @author 叶二鹏
	 * @date 2015年10月14日 下午3:06:17 
	 */
	@RequestMapping("/getArrivalPage")
	@ResponseBody
	public EasyuiDataGridJson getArrivalPage(QueryParams params, EasyuiPage<ArrivalMessageModel> page){
		page = arrivalServiceImpl.getArrivalPage(params, page);
		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		easyuiDataGridJson.setRows(page.getResult());
		easyuiDataGridJson.setTotal((long)page.getTotal());
		return easyuiDataGridJson;
	}
	
	/** 
	 * @Description: 跳转到添加页
	 * @return ModelAndView 返回类型 
	 * @author 叶二鹏
	 * @date 2015年10月15日 下午3:44:04 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/toAddArrivalMessage")
	public ModelAndView toAddArrivalMessage(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		//获取产品线信息
		List<voProductLine> productLineList = ProductLinePermissionCache.getAllProductLineList();
		model.put("productLineList", productLineList);
		
		//供应商列表
		List<Map<String, Object>> supplierList = arrivalServiceImpl.getSupplier();
		Map<String, Object> none = new HashMap<String, Object>();
		none.put("id", -10);
		none.put("name", "无");
		none.put("name_abbreviation", "w");
		supplierList.add(0, none);
		model.put("supplierList", supplierList);

		return new ModelAndView("admin/product/arrivalMessage/addArrivalMessage",model);
	}
	
	/** 
	 * @Description: 添加
	 * @return Map<String,Object> 返回类型 
	 * @author 叶二鹏
	 * @date 2015年10月15日 下午3:43:52 
	 */
	@RequestMapping("/addArrivalMessage")
	@ResponseBody
	public String addArrivalMessage(ArrivalMessageModel model, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		voUser curuser = (voUser)session.getAttribute("userView");
		model.setAddUser(curuser.getUsername());
		model.setAddTime(DateUtil.getNow());
		String result = "success";
		try {
			int count = arrivalServiceImpl.getCountByWayBillCode(model.getWaybillCode());
			if (count > 0) {
				result = "codeIsEx";
			} else {
				arrivalApplication.addArrivalMessage(model);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "fail";
		}
		return result;
	}
	
	/** 
	 * @Description: 删除信息
	 * @return Map<String,Object> 返回类型 
	 * @author 叶二鹏
	 * @date 2015年10月16日 上午10:42:54 
	 */
	@RequestMapping("/delArrivalMessage")
	@ResponseBody
	public Map<String, Object> delArrivalMessage(int id, HttpServletRequest request){
		HttpSession session = request.getSession(false);
		voUser curuser = (voUser)session.getAttribute("userView");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			arrivalServiceImpl.delArrivalMessage(id, curuser.getUsername());
			map.put("result", "success");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", "fail");
		}
		return map;
	}
	
	@RequestMapping("/exportList")
	public void exportList(QueryParams params, HttpServletRequest request, HttpServletResponse response) {
		arrivalServiceImpl.exportList( params,  request,  response);
	}

}
