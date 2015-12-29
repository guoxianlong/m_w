package mmb.cargo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.cargo.model.ReturnedProductDirectRequestBean;
import mmb.cargo.model.ReturnedProductVirtualRequestBean;
import mmb.cargo.service.IReturnedProductDirectService;
import mmb.easyui.Json;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.stock.cargo.CargoDeptAreaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voCatalog;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoInfoStorageBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import cache.CatalogCache;

/**
 * 
 * @descripion 退货上架指向管理
 * @author 刘仁华
 * @time 2015年1月30日
 */
@Controller
@RequestMapping("/returnedProductDirect")
public class ReturnedProductDirectController {
	@Resource
	private IReturnedProductDirectService returnedProductDirectService;

	/*
	 * 登入页面
	 */
	@RequestMapping("/init")
	public String init(ModelMap map, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		voUser adminUser = (voUser) request.getSession().getAttribute("userView");
		if (adminUser == null) {
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		this.initBaseData(map, request, adminUser);
		return "admin/cargo/returnedProductDirect";
	}

	/*
	 * 创建退货上架指向
	 */
	@RequestMapping(value = "/create", method = { RequestMethod.POST })
	@ResponseBody
	public Json create(ModelMap map, HttpServletRequest request, HttpServletResponse response,
			ReturnedProductDirectRequestBean requestBean){
		voUser adminUser = (voUser) request.getSession().getAttribute("userView");
		Json json = new Json();
		if (adminUser == null) {
			json.setSuccess(false);
			json.setMsg("当前没有登录,操作失败！");
			return json;
		}
		try {
			boolean flag = returnedProductDirectService.createDirect(requestBean, adminUser);
			json.setSuccess(flag);
			if(flag){
				json.setMsg("新增成功！");
			}else{
				json.setMsg("新增失败！");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("新增失败！");
			e.printStackTrace();
		}
		return json;
	}

	/*
	 * 修改退货上架指向
	 */
	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	@ResponseBody
	public Json update(ModelMap map, HttpServletRequest request, HttpServletResponse response,
			ReturnedProductDirectRequestBean requestBean){
		voUser adminUser = (voUser) request.getSession().getAttribute("userView");
		Json json = new Json();
		if (adminUser == null) {
			json.setSuccess(false);
			json.setMsg("当前没有登录,操作失败！");
			return json;
		}
		try {
			boolean flag = returnedProductDirectService.updateDirect(requestBean, adminUser);
			json.setSuccess(flag);
			if(flag){
				json.setMsg("修改成功！");
			}else{
				json.setMsg("修改失败！");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("修改失败！");
			e.printStackTrace();
		}
		return json;
	}
	
	/*
	 * 查询列表
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/query", method = { RequestMethod.GET,RequestMethod.POST })
	@ResponseBody
	public EasyuiDataGridJson query(ModelMap map, HttpServletRequest request, HttpServletResponse response,
			ReturnedProductDirectRequestBean requestBean){
		voUser adminUser = (voUser) request.getSession().getAttribute("userView");
		if (adminUser == null) {
			return null;
		}
		// 仓库列表
		List<String> storageList = new ArrayList<String>();
		List<CargoInfoStorageBean> storageBeanList = (List<CargoInfoStorageBean>)this.getStorageList(request);
		for(CargoInfoStorageBean bean:storageBeanList){
			storageList.add(""+bean.getId());
		}
		requestBean.setLimitStorage(storageList);
		return returnedProductDirectService.getDirectData(requestBean);
	}
	
	/*
	 * 作废
	 */
	@RequestMapping("/cancel")
	@ResponseBody
	public Json cancel(HttpServletRequest request,Integer directId){
		voUser adminUser = (voUser) request.getSession().getAttribute("userView");
		Json json = new Json();
		if (adminUser == null) {
			json.setSuccess(false);
			json.setMsg("当前没有登录,操作失败！");
			return json;
		}
		try {
			boolean flag = returnedProductDirectService.cancelDirect(directId,adminUser);
			json.setSuccess(flag);
			if(flag){
				json.setMsg("修改成功！");
			}else{
				json.setMsg("修改失败！");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("修改失败！");
			e.printStackTrace();
		}
		return json;
	}
	
	/*
	 * 查询巷道列表
	 */
	@RequestMapping(value = "/showPassageDetail", method = { RequestMethod.GET,RequestMethod.POST })
	@ResponseBody
	public EasyuiDataGridJson showPassageDetail(ModelMap map, HttpServletRequest request, HttpServletResponse response,
			ReturnedProductDirectRequestBean requestBean) throws ServletException, IOException{
		voUser adminUser = (voUser) request.getSession().getAttribute("userView");
		if (adminUser == null) {
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		return returnedProductDirectService.getPassageDetailLs(requestBean);
	}
	
	/*
	 * 查询操作日志
	 */
	@RequestMapping(value = "/showDirectLog", method = { RequestMethod.GET,RequestMethod.POST })
	@ResponseBody
	public EasyuiDataGridJson showDirectLog(ModelMap map, HttpServletRequest request, HttpServletResponse response,
			ReturnedProductDirectRequestBean requestBean) throws ServletException, IOException{
		voUser adminUser = (voUser) request.getSession().getAttribute("userView");
		if (adminUser == null) {
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = adminUser.getGroup();
		if (!group.isFlag(3099)) {
			return null;
		}
		return returnedProductDirectService.getDirectLogLs(requestBean);
	}
	
	/*
	 * 仓库区域获取
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/getStockAreaList")
	@ResponseBody
	public List getStockAreaList(String storageId) {
		// 区域获取
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try {
			return service.getCargoInfoStockAreaList("storage_id =" + storageId + " and stock_type="
					+ CargoInfoBean.STOCKTYPE_QUALIFIED, -1, -1, "whole_code asc");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return null;
	}

	/*
	 * 巷道获取
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/getPassageList")
	@ResponseBody
	public List getPassageList(String stockAreaId) {
		// 巷道获取
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try {
			return service.getCargoInfoPassageList("stock_area_id=" + stockAreaId, -1, -1, "whole_code asc");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return null;
	}

	/*
	 * 货架层数获取
	 */
	@RequestMapping("/getMaxFloorNum")
	@ResponseBody
	public String getMaxFloorNum(String passageId) {
		return returnedProductDirectService.getMaxFloorNum(passageId);
	}

	/*
	 * 登入临时表管理页面
	 */
	@RequestMapping("/initvirtual")
	public String initVirtual(ModelMap map, HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		voUser adminUser = (voUser) request.getSession().getAttribute("userView");
		if (adminUser == null) {
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		// 仓库列表
		map.put("storageList", this.getStorageList(request));
		return "admin/cargo/returnedProductVirtual";
	}
	
	/*
	 * 查询临时表数据
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/queryVirtual", method = { RequestMethod.GET,RequestMethod.POST })
	@ResponseBody
	public EasyuiDataGridJson queryVirtual(ModelMap map, HttpServletRequest request, 
			HttpServletResponse response,ReturnedProductVirtualRequestBean requestBean) throws ServletException,
			IOException {
		voUser adminUser = (voUser) request.getSession().getAttribute("userView");
		if (adminUser == null) {
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		// 仓库列表
		List<String> storageList = new ArrayList<String>();
		List<CargoInfoStorageBean> storageBeanList = (List<CargoInfoStorageBean>)this.getStorageList(request);
		for(CargoInfoStorageBean bean:storageBeanList){
			storageList.add(""+bean.getId());
		}
		requestBean.setLimitStorage(storageList);
		return returnedProductDirectService.getVirtualData(requestBean);
	}
	
	/*
	 * 删除临时表数据
	 */
	@RequestMapping("/deleteVirtual")
	@ResponseBody
	public Json deleteVirtual(HttpServletRequest request,String virtualId){
		voUser adminUser = (voUser) request.getSession().getAttribute("userView");
		Json json = new Json();
		if (adminUser == null) {
			json.setSuccess(false);
			json.setMsg("当前没有登录,操作失败！");
			return json;
		}
		try {
			if(virtualId!=null&&!"".equals(virtualId)){
				boolean flag = returnedProductDirectService.deleteVirtualBatch(virtualId);
				json.setSuccess(flag);
				if(flag){
					json.setMsg("删除成功！");
				}else{
					json.setMsg("删除失败！");
				}
			}else{
				json.setSuccess(false);
				json.setMsg("请选择要删除的对象！");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("删除失败！");
			e.printStackTrace();
		}
		return json;
	}
	
	/*
	 * 初始化基础数据
	 */
	@SuppressWarnings("rawtypes")
	private void initBaseData(ModelMap map, HttpServletRequest request, voUser adminUser) {
		//权限控制
		UserGroupBean group = adminUser.getGroup();
		if (group.isFlag(3099)) {
			map.put("viewLog", "1");
		}
		
		// 仓库列表
		List storageList = this.getStorageList(request);
		map.put("storageList", storageList);
		// 一级分类列表
		List<voCatalog> firstCatalogList = this.getFirstCatalogList(adminUser);
		map.put("firstCatalogList", firstCatalogList);
	}

	/*
	 * 获取员工所属仓库
	 */
	@SuppressWarnings("rawtypes")
	private List getStorageList(HttpServletRequest request) {
		List storageList = null;
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try {
			// 库地区列表
			// 根据归属部门判断其所在仓库
			List<String> areaList = CargoDeptAreaService.getCargoDeptAreaList(request,CargoInfoBean.STOCKTYPE_QUALIFIED);
			if (areaList.size() > 0) {
				StringBuilder sb = new StringBuilder("");
				for (String area : areaList) {
					sb.append("," + area);
				}
				storageList = service.getCargoInfoStorageList("area_id in(" + sb.substring(1) + ")", -1, -1,
						"whole_code asc");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return storageList;
	}

	/*
	 * 获取一级分类列表
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<voCatalog> getFirstCatalogList(voUser adminUser) {
		return (List<voCatalog>)((HashMap)CatalogCache.catalogLevelList.get(0)).get(Integer.valueOf(0));
	}
}
