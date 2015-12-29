package mmb.stock.area.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.easyui.Json;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.EasyuiPageBean;
import mmb.stock.area.model.StockArea;
import mmb.stock.area.model.StockAreaSubTemp;
import mmb.stock.area.service.StockAreaSercive;
import mmb.stock.area.service.StockAreaTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.stock.ProductStockBean;
@RequestMapping("/areaController")
@Controller
public class AreaController {
	@Autowired
	private StockAreaSercive stockAreaSercive;
	
	@Autowired
	private StockAreaTypeService stockAreaTypeService;
	/**
	 * 获取仓库列表
	 * @throws IOException 
	 * @throws ServletException 
	 * @auth aohaichen
	 */
	@RequestMapping("/getStockArea")
	@ResponseBody
	public EasyuiDataGridJson getStockAreaList(HttpServletRequest request,HttpServletResponse response,EasyuiPageBean page) throws ServletException, IOException{
		Map<String,String> map = new HashMap<String,String>();
		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(2186)){
			request.setAttribute("msg", "你没有查询权限!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			easyuiDataGridJson.setTip("123123123123");
			return null;
		}
		int total =stockAreaSercive.getStockAreaCount(map);
		easyuiDataGridJson.setTotal((long)total);
		map.put("start", (page.getPage()-1) * page.getRows() + "");
		map.put("count", page.getRows() + "");
		if(page.getSort()!=null){
			map.put("order", page.getSort() + "");
			map.put("sort", page.getOrder() + "");	
		}
		List<StockArea> list =stockAreaSercive.getStockAreaList(map);
		easyuiDataGridJson.setRows(list);
		return easyuiDataGridJson;
		
	}
	/**
	 * 获取下拉列表仓库类型
	 * @auth aohaichen
	 */
	@RequestMapping("/getStockAreaSubTempList")
	@ResponseBody
	public EasyuiDataGridJson getStockAreaSubTempList(HttpServletRequest request,HttpServletResponse response,int id){
		Map<String,String> map = new HashMap<String,String>();
		List<StockAreaSubTemp> list = new ArrayList<StockAreaSubTemp>();
		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		map.put("condition", "sa.id = "+id);
		List<Map<String,Object>> list2 =stockAreaSercive.getStockAreaSubTempList(map);
		for(int i = 0 ; i < list2.size() ; i++){
			Map<String,Object> map2=list2.get(i);
			StockAreaSubTemp stockAreaSubTemp = new StockAreaSubTemp();
			if(map2!=null){
				if(map2.get("name") != null ){
					stockAreaSubTemp.setName(map2.get("name").toString());
				}
				if(map2.get("status")!=null){
					stockAreaSubTemp.setStatus(((Long)map2.get("status")).intValue());
				}
			}
			list.add(stockAreaSubTemp);
		}
		
		easyuiDataGridJson.setRows(list);
		return easyuiDataGridJson;
		
	}
	
	/**
	 * 新增和编辑一个库地区
	 * @auth aohaichen
	 */
	@RequestMapping("/addOrEditStockArea")
	@ResponseBody
	public Json addOrEditStockArea(HttpServletRequest request,HttpServletResponse response,String id,String name,String type,String attribute,String method){
		Json j = new Json();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录，操作失败");
			return j;
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(2186);
		if(!viewAll){ 
			j.setMsg("你没有使用权限！");
			return j;
		} 
		try {
			StockArea stockArea = new StockArea();
			stockArea.setId(Integer.parseInt(id));
			stockArea.setName(name);
			stockArea.setType(Integer.parseInt(type));
			stockArea.setAttribute(Integer.parseInt(attribute));
			if(method==null){//新增
				int result =stockAreaSercive.addStockArea(stockArea);
				if(result>0){
					j.setMsg("添加成功！");
					j.setSuccess(true);
				}else{
					j.setMsg("添加失败！");
					j.setSuccess(false);
				}
			}else{//编辑
				int result =stockAreaSercive.editStockArea(stockArea);
				if(result>0){
					j.setMsg("修改成功！");
					j.setSuccess(true);
				}else{
					j.setMsg("修改失败！");
					j.setSuccess(false);
				}
			}
		}catch(NumberFormatException e){
			j.setMsg("请输入正确ID！");
			return j;
		}catch (Exception e) {
			e.printStackTrace();
			j.setMsg("系统异常！");
			return j;
		}
		ProductStockBean.initStockAreaTypeCacheAll();
		return j;
	}
	
	/**
	 * 获取仓库类型名称
	 * @auth aohaichen
	 */
	@RequestMapping("/getStockTypeNameList")
	@ResponseBody
	public Json getStockTypeNameList(HttpServletRequest request,HttpServletResponse response,String id){
		Json j = new Json();	
		Map<String,String> map = new HashMap<String, String>();
		map.put("condition", "sa.id= "+id);
		List<Map<String,Object>> list =stockAreaSercive.getStockTypeNameList(map);
		j.setObj(list);
		return j;
	}
	
	/**
	 * 获取仓库类型
	 * @auth aohaichen
	 */
	@RequestMapping("/getStockType")
	@ResponseBody
	public Json getStockType(HttpServletRequest request,HttpServletResponse response){
		Json j = new Json();	
		Map<String,String> map = new HashMap<String, String>();
		List<Map<String,Object>> list =stockAreaSercive.getStockTypeByCount(map);
		j.setObj(list);
		j.setSuccess(true);
		return j;
	}
	
	/**
	 * 修改仓库类型
	 * @auth aohaichen
	 */
	@RequestMapping("/editStockAreaType")
	@ResponseBody
	public Json editStockAreaType(HttpServletRequest request,HttpServletResponse response,String[] typeName,String id){
		Json j = new Json();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录，操作失败");
			return j;
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(2186);
		if(!viewAll){ 
			j.setMsg("你没有使用权限！");
			return j;
		}
		try {
			stockAreaTypeService.editStockAreaType(id, typeName);
			j.setMsg("修改成功！");
			j.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg("系统异常！");
			return j;
		}
		ProductStockBean.initStockAreaTypeCacheAll();
		return j;
	}
}
