package mmb.rec.sys.controller;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.oper.service.CargoDeptService;
import mmb.stock.cargo.CargoDeptAreaBean;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.cargo.CartonningInfoBean;
import mmb.stock.cargo.CartonningInfoService;
import mmb.stock.cargo.CartonningProductInfoBean;
import mmb.system.admin.AdminService;
import mmb.ware.WareService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.cargo.CargoOperationAction;
import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoDeptBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoInfoCityBean;
import adultadmin.bean.cargo.CargoInfoLogBean;
import adultadmin.bean.cargo.CargoInfoPassageBean;
import adultadmin.bean.cargo.CargoInfoShelfBean;
import adultadmin.bean.cargo.CargoInfoStockAreaBean;
import adultadmin.bean.cargo.CargoInfoStorageBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAreaBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.stock.StockCardComparator;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.Constants;
import adultadmin.util.DateUtil;
import adultadmin.util.Encoder;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/CargoController")
public class CargoAttributeManageController {
	public static byte[] cargoLock = new byte[0];
	
	@Autowired 
	public CargoDeptService mCargoDeptService;
	
	@Autowired 
	public mmb.rec.oper.service.CargoDeptAreaService mCargoDeptAreaService;
	/**
	 * 查询地区列表
	 * 2013-8-13
	 * 朱爱林	
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/cargoInfoCityList")
	@ResponseBody
	public Object cargoInfoCityList(HttpServletRequest request,
			HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		try{
			List cityList=service.getCargoInfoCityList(null, -1, -1, "code asc");
//			List areaCountList=new ArrayList();
			for(int i=0;i<cityList.size();i++){
				map = new HashMap<String, String>();
				CargoInfoCityBean bean=(CargoInfoCityBean)cityList.get(i);
				int cityId=bean.getId();
				int areaCount=service.getCargoInfoAreaCount("city_id="+cityId);
//				areaCountList.add(Integer.valueOf(areaCount));
				map.put("city_id", cityId+"");
				map.put("city_areaCount", areaCount+"");
				map.put("city_code", bean.getCode());
				map.put("city_name", bean.getName());
				map.put("city_count", Integer.valueOf(areaCount)+"");
				result.add(map);
			}
//			request.setAttribute("areaCountList", areaCountList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
//		return mapping.findForward("city");
		return result;
	}
	/**
	 * 地区列表下的删除城市
	 * 2013-8-13
	 * 朱爱林	
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/deleteCity")
	public void deleteCity(HttpServletRequest request,HttpServletResponse response) throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		synchronized(cargoLock){
			ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
			try{
				service.getDbOp().startTransaction();
				int id=Integer.valueOf(request.getParameter("cityId")).intValue();
				if(service.getCargoInfoAreaCount("city_id="+id)>0){
	//				request.setAttribute("tip", "该城市下已添加地区，不能删除！");
	//				request.setAttribute("result", "failure");
	//				service.getDbOp().rollbackTransaction();
	//				return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'该城市下已添加地区，不能删除！'}");
					response.getWriter().write(result.toString());
					return;
				}
				service.deleteCargoInfoCity("id="+id);
				result.append("{result:'success',tip:'删除成功！'}");
				response.getWriter().write(result.toString());
				service.getDbOp().commitTransaction();
				return;
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				service.releaseAll();
			}
		}
	}
	/**
	 * 地区列表下的增加城市
	 * 2013-8-13
	 * 朱爱林	
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/addCity")
	public void addCity(HttpServletRequest request,HttpServletResponse response) throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		synchronized(cargoLock){
			ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
			try{
				service.getDbOp().startTransaction();
				String code=StringUtil.convertNull(request.getParameter("code")).trim();
				String name=StringUtil.convertNull(request.getParameter("name")).trim();
				char[] ccode=code.toCharArray();
				if(code.equals("")){
	//				request.setAttribute("tip", "请填写城市代号！");
	//				request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
	//				return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'请填写城市代号！'}");
					response.getWriter().write(result.toString());
					return;
				}
				if(ccode.length>2||ccode[0]<65||ccode[0]>90||ccode[1]<65||ccode[1]>90){
	//				request.setAttribute("tip", "请填写有效的城市代号！");
	//				request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
	//				return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'请填写有效的城市代号！'}");
					response.getWriter().write(result.toString());
					return;
				}
				int codeCount=service.getCargoInfoCityCount("code='"+code+"'");
				if(codeCount!=0){
	//				request.setAttribute("tip", "城市代号与其他重复，请重新填写！");
	//				request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
	//				return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'城市代号与其他重复，请重新填写！'}");
					response.getWriter().write(result.toString());
					return;
				}
				if(name.equals("")){
	//				request.setAttribute("tip", "请填写城市名称！");
	//				request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
	//				return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'请填写城市名称！'}");
					response.getWriter().write(result.toString());
					return;
					
				}
				if(name.length()>10){
	//				request.setAttribute("tip", "城市名称最大支持10个字符！");
	//				request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
	//				return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'城市名称最大支持10个字符！'}");
					response.getWriter().write(result.toString());
					return;
				}
				int nameCount=service.getCargoInfoCityCount("name='"+name+"'");
				if(nameCount!=0){
	//				request.setAttribute("tip", "城市名称与其他重复，请重新填写！");
	//				request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
	//				return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'城市名称与其他重复，请重新填写！'}");
					response.getWriter().write(result.toString());
					return;
				}
				CargoInfoCityBean bean=new CargoInfoCityBean();
				bean.setCode(code);
				bean.setName(name);
				bean.setWholeCode(code);
				service.addCargoInfoCity(bean);
				service.getDbOp().commitTransaction();
				result.append("{result:'success',tip:'添加成功！'}");
				response.getWriter().write(result.toString());
				service.getDbOp().commitTransaction();
				return;
			}catch(Exception e){
				e.printStackTrace();
				service.getDbOp().rollbackTransaction();
			}finally{
				service.releaseAll();
			}
		}
	}
	/**
	 * easyui combobox获取所属城市列表
	 * 2013-8-13
	 * 朱爱林	
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/querySelectCitys")
	@ResponseBody
	public Object querySelectCitys(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
//		StringBuilder result = new StringBuilder();
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String flag = StringUtil.convertNull(request.getParameter("flag"));
			String id = StringUtil.convertNull(request.getParameter("id"));
			List wholeCityList=service.getCargoInfoCityList(null, -1, -1, "id desc");//所有的城市列表
//			result.append("[");
			List<Map<String,String>> list = new ArrayList<Map<String,String>>();
			Map<String,String> map = new HashMap<String, String>();
			if("all".equals(flag)){
				map.put("id", "");
				map.put("text", "全部");
				map.put("selected", "true");
			}else if("choice".equals(flag)){
				map.put("id", "");
				map.put("text", "-请选择-");
				if("".equals(id)){
					map.put("selected", "true");
				}
			}
			list.add(map);
			for(int i=0;i<wholeCityList.size();i++){
				map = new HashMap<String, String>();
				CargoInfoCityBean cargoInfoCityBean = (CargoInfoCityBean) wholeCityList.get(i);
				map.put("id", cargoInfoCityBean.getId()+"");
				map.put("text", cargoInfoCityBean.getCode()+"--"+cargoInfoCityBean.getName());
				if(id.equals(map.get("id"))){
					map.put("selected", "true");
				}
				list.add(map);
//				result.append("{'id':"+cargoInfoCityBean.getId()+",")
//					.append("'text':'"+cargoInfoCityBean.getCode()+"--"+cargoInfoCityBean.getName()+"'}");
//				if(i!=wholeCityList.size()-1){
//					result.append(",");
//				}else{
//					result.append("]");
//				}
				
			}
			return list;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return null;
	}
	/**
	 * 获取地区列表
	 * 2013-8-13
	 * 朱爱林	
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/cargoInfoAreaList")
	@ResponseBody
	public Object cargoInfoAreaList(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		try{
//			List wholeCityList=service.getCargoInfoCityList(null, -1, -1, "id desc");//所有的城市列表
			List areaList=new ArrayList();//区域列表
			if(request.getParameter("cityId")==null||request.getParameter("cityId").equals("")){
				areaList=service.getCargoInfoAreaList(null, -1, -1, "code asc");
			}else{
				String cityId=request.getParameter("cityId");//城市Id,查询条件
				areaList=service.getCargoInfoAreaList("city_id="+cityId, -1, -1, "code asc");
			}
//			List storageCountList=new ArrayList();//区域内地区数列表
//			List cityNameList=new ArrayList();//城市名称列表
			for(int i=0;i<areaList.size();i++){
				map = new HashMap<String, String>();
				CargoInfoAreaBean bean=(CargoInfoAreaBean)areaList.get(i);
				int areaId=bean.getId();//区域Id
				int storageCount=service.getCargoInfoStorageCount("area_id="+areaId);					
//				storageCountList.add(Integer.valueOf(storageCount));
				int cityId2=bean.getCityId();
				String cityName=service.getCargoInfoCity("id="+cityId2).getName();
//				cityNameList.add(cityName);
				map.put("area_id", areaId+"");
				map.put("area_code", bean.getCode());//地区代号
				map.put("area_name", bean.getName());//地区名称
				map.put("area_belong_city", cityName);//所属城市
				map.put("area_count", Integer.valueOf(storageCount)+"");//下属仓库个数
			
				list.add(map);
			}
//			request.setAttribute("areaList", areaList);
//			request.setAttribute("storageCountList", storageCountList);
//			request.setAttribute("cityNameList", cityNameList);
//			request.setAttribute("wholeCityList", wholeCityList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return list;
	}
	/**
	 * 删除地区
	 * 2013-8-13
	 * 朱爱林
	 */
	@RequestMapping("/deleteArea")
	public void deleteArea(HttpServletRequest request,HttpServletResponse response) throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		synchronized(cargoLock){
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			service.getDbOp().startTransaction();
			int areaId=Integer.parseInt(request.getParameter("areaId"));
			if(service.getCargoInfoStorageCount("area_id="+areaId)>0){
//				request.setAttribute("tip", "该地区下已添加仓库，不能删除！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'该地区下已添加仓库，不能删除！'}");
				response.getWriter().write(result.toString());
				return;
			}
			service.deleteCargoInfoArea("id="+areaId);
//			request.setAttribute("deleted", Integer.valueOf(1));
			service.getDbOp().commitTransaction();
			result.append("{result:'success',tip:'删除成功！'}");
			response.getWriter().write(result.toString());
			return;
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
		return;
	}
	/**
	 * 增加区域
	 * 2013-8-14
	 * 朱爱林
	 */
	@RequestMapping("/addArea")
	public void addArea(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		synchronized(cargoLock){
			ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
			try{
				service.getDbOp().startTransaction();
				String areaCode=StringUtil.convertNull(request.getParameter("areaCode")).trim();//区域代号
				String areaName=StringUtil.convertNull(request.getParameter("areaName")).trim();//区域名称
				String cityId=StringUtil.convertNull(request.getParameter("cityId"));//城市Id
				String areaId=StringUtil.convertNull(request.getParameter("areaId"));//地区Id
				if(cityId.equals("")){
	//				request.setAttribute("tip", "请选择一个城市！");
	//				request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
	//				return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'请选择一个城市！'}");
					response.getWriter().write(result.toString());
					return;
				}
				if(areaCode.equals("")){
	//				request.setAttribute("tip", "请填写地区代号！");
	//				request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
	//				return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'请填写地区代号！'}");
					response.getWriter().write(result.toString());
					return;
				}
				String cityCode=service.getCargoInfoCity("id="+cityId).getCode();//城市代号
				String wholeCode=cityCode+areaCode;//完整编号
				char[] ccode=areaCode.toCharArray();
				
				if(ccode.length>1||ccode[0]<65||ccode[0]>90){
	//				request.setAttribute("tip", "请填写有效的地区代号");
	//				request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
	//				return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'请填写有效的地区代号！'}");
					response.getWriter().write(result.toString());
					return;
				}
				int codeCount=service.getCargoInfoAreaCount("code='"+areaCode+"' and city_id="+cityId);
				if(codeCount!=0){
	//				request.setAttribute("tip", "地区代号与其他重复，请重新填写！");
	//				request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
	//				return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'地区代号与其他重复，请重新填写！'}");
					response.getWriter().write(result.toString());
					return;
				}
				if(areaName.equals("")){
	//				request.setAttribute("tip", "请填写地区名称！");
	//				request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
	//				return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'请填写地区名称！'}");
					response.getWriter().write(result.toString());
					return;
				}
				if(areaName.length()>10){
	//				request.setAttribute("tip", "地区名称最大支持10个字符！");
	//				request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
	//				return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'地区名称最大支持10个字符！'}");
					response.getWriter().write(result.toString());
					return;
				}
				int nameCount=service.getCargoInfoAreaCount("name='"+areaName+"' and city_id="+cityId);
				if(nameCount!=0){
	//				request.setAttribute("tip", "地区名称与其他重复，请重新填写！");
	//				request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
	//				return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'地区名称与其他重复，请重新填写！'}");
					response.getWriter().write(result.toString());
					return;
				}
				CargoInfoAreaBean bean=new CargoInfoAreaBean();
				bean.setCode(areaCode);
				bean.setWholeCode(wholeCode);
				bean.setName(areaName);
				bean.setCityId(Integer.parseInt(cityId));
				if(!areaId.equals("")){
					int areaIdCount=service.getCargoInfoAreaCount("id="+areaId);
					if(areaIdCount!=0){
	//					request.setAttribute("tip", "地区Id与其他重复，请重新填写！");
	//					request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
	//					return mapping.findForward(IConstants.FAILURE_KEY);
						result.append("{result:'failure',tip:'地区Id与其他重复，请重新填写！'}");
						response.getWriter().write(result.toString());
						return;
					}
					bean.setId(Integer.parseInt(areaId));
					bean.setOldId(Integer.parseInt(areaId));
				}
				service.addCargoInfoArea(bean);
				service.getDbOp().commitTransaction();
				result.append("{result:'success',tip:'添加成功！'}");
				response.getWriter().write(result.toString());
				return;
			}catch(Exception e){
				e.printStackTrace();
				service.getDbOp().rollbackTransaction();
			}finally{
				service.releaseAll();
			}
		}
		return;
	}
	/**
	 * 获取二级下拉选：所属地区
	 * 2013-8-14
	 * 朱爱林	
	 * @return
	 */
	@RequestMapping("/querySelectAreas")
	@ResponseBody
	public Object querySelectAreas(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
//		StringBuilder result = new StringBuilder();
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String cityId=request.getParameter("cityId");
			List cargoInfoAreaList=null;
			if(cityId.equals("")){
				cargoInfoAreaList=new ArrayList();
			}else{
				cargoInfoAreaList=service.getCargoInfoAreaList("city_id="+cityId, -1, -1, "id desc");
			}
			List<Map<String,String>> list = new ArrayList<Map<String,String>>();
			Map<String,String> map = new HashMap<String, String>();
			map.put("id", "");
			map.put("text", "-请选择-");
			map.put("selected", "true");
			list.add(map);
			for(int i=0;i<cargoInfoAreaList.size();i++){
				map = new HashMap<String, String>();
				CargoInfoAreaBean cargoInfoAreaBean = (CargoInfoAreaBean) cargoInfoAreaList.get(i);
				map.put("id", cargoInfoAreaBean.getId()+"");
				map.put("text", cargoInfoAreaBean.getCode()+"--"+cargoInfoAreaBean.getName());
				list.add(map);
			}
			return list;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return null;
	}
	/**
	 * 仓库列表查询
	 * 2013-8-14
	 * 朱爱林	
	 * @return
	 */
	@RequestMapping("/cargoInfoStorageList")
	@ResponseBody
	public Object cargoInfoStorageList(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		try{
			
			List wholeCityList=service.getCargoInfoCityList(null, -1, -1, "id desc");//所有的城市列表
			List storageList=new ArrayList();//仓库列表
			String condition="";
			if(request.getParameter("cityId")!=null&&(!request.getParameter("cityId").equals(""))){
				condition+="city_id=";
				condition+=request.getParameter("cityId");
			}
			if(request.getParameter("areaId")!=null&&(!request.getParameter("areaId").equals(""))){
				if(condition.equals("")){
					condition+="area_id=";
				}else{
					condition+=" and area_id=";
				}
				condition+=request.getParameter("areaId");
			}
			if(condition.equals("")){
				condition=null;
			}
			storageList=service.getCargoInfoStorageList(condition, -1, -1, "code asc");
//			List stockAreaCountList=new ArrayList();//区域内地区数列表
//			List cityList=new ArrayList();//城市列表(bean)
//			List areaList=new ArrayList();//地区列表(bean)
			for(int i=0;i<storageList.size();i++){
				map = new HashMap<String, String>();
				CargoInfoStorageBean bean=(CargoInfoStorageBean)storageList.get(i);//仓库bean
				int storageId=bean.getId();//区域Id
				int stockAreaCount=service.getCargoInfoStockAreaCount("storage_id="+storageId);//区域内地区数
				map.put("storage_count", Integer.valueOf(stockAreaCount)+"");//仓库内区域数量
//				stockAreaCountList.add(Integer.valueOf(stockAreaCount));
				int cityId=bean.getCityId();
				CargoInfoCityBean cicb=service.getCargoInfoCity("id="+cityId);
				map.put("storage_belong_city", cicb==null?"":cicb.getCode()+"--"+cicb.getName());//所属城市
//				cityList.add(cicb);
				int areaId=bean.getAreaId();
				CargoInfoAreaBean ciab=service.getCargoInfoArea("id="+areaId);
				//加个null判断是测试数据库存在仓库没有对应的所属城市或地区
				map.put("storage_belong_area", ciab==null?"":ciab.getCode()+"--"+ciab.getName());//所属地区
//				areaList.add(ciab);
				
				map.put("storage_id",storageId+"");
				map.put("storage_code", bean.getCode());//仓库代号
				map.put("storage_name", bean.getName());//仓库名称
				
				list.add(map);
			}
//			request.setAttribute("storageList", storageList);
//			request.setAttribute("stockAreaCountList", stockAreaCountList);
//			request.setAttribute("cityList", cityList);
//			request.setAttribute("areaList", areaList);
//			request.setAttribute("wholeCityList", wholeCityList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return list;
	}
	/**
	 * 添加仓库
	 * 2013-8-14
	 * 朱爱林
	 */
	@RequestMapping("/addStorage")
	public void addStorage(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		synchronized(cargoLock){
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			service.getDbOp().startTransaction();
			String storageCode=StringUtil.convertNull(request.getParameter("storageCode")).trim();//仓库代号
			String storageName=StringUtil.convertNull(request.getParameter("storageName")).trim();//仓库名称
			String cityId=StringUtil.convertNull(request.getParameter("cityId"));//城市Id
			String areaId=StringUtil.convertNull(request.getParameter("areaId"));//地区Id
			if(cityId.equals("")){
//				request.setAttribute("tip", "请选择一个城市！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'请选择一个城市！'}");
				response.getWriter().write(result.toString());
				return;
			}
			if(areaId.equals("")){
//				request.setAttribute("tip", "请选择一个地区！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'请选择一个地区！'}");
				response.getWriter().write(result.toString());
				return;
			}
			if(storageCode.equals("")){
//				request.setAttribute("tip", "请填写仓库代号！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'请填写仓库代号！'}");
				response.getWriter().write(result.toString());
				return;
			}
			char[] ccode=storageCode.toCharArray();
			if(ccode.length>2||ccode[0]<48||ccode[0]>57||ccode[1]<48||ccode[1]>57){
//				request.setAttribute("tip", "请填写有效的仓库代号！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'请填写有效的仓库代号！'}");
				response.getWriter().write(result.toString());
				return;
			}
			int storageCount=service.getCargoInfoStorageCount("code='"+storageCode+"' and area_id="+areaId);
			if(storageCount!=0){
//				request.setAttribute("tip","仓库代号与其他重复，请重新填写！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'仓库代号与其他重复，请重新填写！'}");
				response.getWriter().write(result.toString());
				return;
			}
			if(storageName.equals("")){
//				request.setAttribute("tip", "请填写仓库名称！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'请填写仓库名称！'}");
				response.getWriter().write(result.toString());
				return;
			}
			int storageNameCount=service.getCargoInfoStorageCount("name='"+storageName+"' and area_id="+areaId);
			if(storageNameCount!=0){
//				request.setAttribute("tip","仓库名称与其他重复，请重新填写！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'仓库名称与其他重复，请重新填写！'}");
				response.getWriter().write(result.toString());
				return;
			}
			
			String cityCode=service.getCargoInfoCity("id="+cityId).getCode();//城市代号
			String areaCode=service.getCargoInfoArea("id="+areaId).getCode();//地区代号
			String wholeCode=cityCode+areaCode+storageCode;//完整编号
			
			CargoInfoStorageBean bean=new CargoInfoStorageBean();
			bean.setCode(storageCode);
			bean.setWholeCode(wholeCode);
			bean.setName(storageName);
			bean.setAreaId(Integer.parseInt(areaId));
			bean.setCityId(Integer.parseInt(cityId));
			service.addCargoInfoStorage(bean);
			service.getDbOp().commitTransaction();
			result.append("{result:'success',tip:'添加仓库成功！'}");
			response.getWriter().write(result.toString());
			return;
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
		return;
	}
	/**
	 * 删除仓库
	 * 2013-8-14
	 * 朱爱林
	 */
	@RequestMapping("/deleteStorage")
	public void deleteStorage(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		synchronized(cargoLock){
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			service.getDbOp().startTransaction();
			int storageId=Integer.parseInt(request.getParameter("storageId"));
			if(service.getCargoInfoStockAreaCount("storage_id="+storageId)>0){
//				request.setAttribute("tip", "该仓库下已添加仓库区域，不能删除！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'该仓库下已添加仓库区域，不能删除！'}");
				response.getWriter().write(result.toString());
				return;
			}
			service.deleteCargoInfoStorage("id="+storageId);
//			request.setAttribute("deleted", Integer.valueOf(1));
			service.getDbOp().commitTransaction();
			result.append("{result:'success',tip:'仓库添加成功！'}");
			response.getWriter().write(result.toString());
			return;
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
		return;
	}
	/**
	 * 获取二级下拉选：所属仓库
	 * 2013-8-14
	 * 朱爱林	
	 * @return
	 */
	@RequestMapping("/querySelectStorages")
	@ResponseBody
	public Object querySelectStorages(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
//		StringBuilder result = new StringBuilder();
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String areaId=request.getParameter("areaId");
			String choice=request.getParameter("choice");
			List cargoInfoStorageList=null;
			if("all".equals(choice)){
				cargoInfoStorageList=cargoInfoStorageList=service.getCargoInfoStorageList(null, -1, -1, "id desc");
			}else{
				if(areaId.equals("")){
					cargoInfoStorageList=service.getCargoInfoStorageList("1=2", -1, -1, "id desc");
				}else{
					cargoInfoStorageList=service.getCargoInfoStorageList("area_id="+areaId, -1, -1, "id desc");
				}
			}
			List<Map<String,String>> list = new ArrayList<Map<String,String>>();
			Map<String,String> map = new HashMap<String, String>();
			map.put("id", "");
			map.put("text", "-请选择-");
			map.put("code", "-请选择-");
			map.put("selected", "true");
			list.add(map);
			for(int i=0;i<cargoInfoStorageList.size();i++){
				map = new HashMap<String, String>();
				CargoInfoStorageBean cargoInfoStorageBean = (CargoInfoStorageBean) cargoInfoStorageList.get(i);
				map.put("id", cargoInfoStorageBean.getId()+"");
				map.put("text", cargoInfoStorageBean.getCode()+"--"+cargoInfoStorageBean.getName());
				map.put("code", cargoInfoStorageBean.getWholeCode());
				list.add(map);
			}
			return list;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return null;
	}
	/**
	 * 仓库区域列表查询
	 * 2013-8-15
	 * 朱爱林	
	 * @return
	 */
	@RequestMapping("/cargoInfoStockAreaList")
	@ResponseBody
	public Object cargoInfoStockAreaList(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		Map<String,Object> resultMap = new HashMap<String, Object>();
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> map = new HashMap<String, String>();
		resultMap.put("total","0");
		resultMap.put("rows",list);
		try{
			
			List wholeCityList=service.getCargoInfoCityList(null, -1, -1, "id desc");//所有的城市列表
			List wholeStorageList=service.getCargoInfoStorageList(null, -1, -1, "id desc");//所有仓库列表
			List stockAreaList=new ArrayList();//仓库区域列表
			String cityId=StringUtil.convertNull(request.getParameter("cityId"));
			String areaId=StringUtil.convertNull(request.getParameter("areaId"));
			String storageId=StringUtil.convertNull(request.getParameter("storageId"));
			String stockAreaCode=StringUtil.convertNull(request.getParameter("stockAreaCode")).trim();
			if(request.getParameter("storageId")!=null&&request.getParameter("cityId")==null){
				CargoInfoStorageBean bean=service.getCargoInfoStorage("id="+storageId);
				cityId=bean.getCityId()+"";
				areaId=bean.getAreaId()+"";
			}
			/*
			 * page=1&rows=20
			 * 获取easyui传来的page,rows参数
			 */
			String page1 = request.getParameter("page");
			String rows1 = request.getParameter("rows");
			int page = Integer.parseInt((page1==null||page1=="0")?"1":page1);
			int rows = Integer.parseInt((rows1==null||rows1=="0")?"20":rows1);
			int start = (page-1)*rows;//每页的开始
			
			
			int countPerPage=20;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			}
//            PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);
//            String para="";
//            para=(cityId.equals("")?"":"&cityId="+cityId)
//    			+(areaId.equals("")?"":"&areaId="+areaId)
//    			+(storageId.equals("")?"":"&storageId="+storageId)
//    			+(stockAreaCode.equals("")?"":"&stockAreaCode="+stockAreaCode)
//    			;
//            paging.setCurrentPageIndex(pageIndex);
//            paging.setPrefixUrl("cargoInfo.do?method=cargoInfoStockAreaList"+para);
            
			String condition="";
			if(!cityId.equals("")){
				condition+="city_id=";
				condition+=cityId;
//				map.put("city_id", cityId);//所属城市
			}
			if(!areaId.equals("")){
				if(condition.equals("")){
					condition+="area_id=";
				}else{
					condition+=" and area_id=";
				}
				condition+=areaId;
//				map.put("area_id", areaId);//所属区域
			}
			if(!storageId.equals("")){
				if(condition.equals("")){
					condition+="storage_id=";
				}else{
					condition+=" and storage_id=";
				}
				condition+=storageId;
//				map.put("storage_id", storageId);//所属仓库
			}
			if(!stockAreaCode.equals("")){
				if(condition.equals("")){
					condition+="code='";
					condition+=stockAreaCode;
					condition+="'";
				}else{
					condition+=" and code='";
					condition+=stockAreaCode;
					condition+="'";
				}
//				map.put("stock_area_code", stockAreaCode);//区域代号
			}
//			list.add(map);//为了进行回显
			if(condition.toString().equals("")){
				condition=null;
			}
//			stockAreaList=service.getCargoInfoStockAreaList(condition,paging.getCurrentPageIndex() * countPerPage,countPerPage,"whole_code asc");
			stockAreaList=service.getCargoInfoStockAreaList(condition,start,rows,"whole_code asc");
			if(stockAreaList.size()==0&&page!=1){
				stockAreaList=service.getCargoInfoStockAreaList(condition,start,rows,"whole_code asc");
//				stockAreaList=service.getCargoInfoStockAreaList(condition,(paging.getCurrentPageIndex()-1) * countPerPage,countPerPage,"whole_code asc");
			}
			int totalCount = service.getCargoInfoStockAreaCount(condition); //根据条件得到 总数量
			resultMap.put("total", totalCount);
//			paging.setTotalCount(totalCount);
//			paging.setTotalPageCount(totalCount%countPerPage==0?totalCount/countPerPage:totalCount/countPerPage+1);
			
//			List storageCodeList=new ArrayList();//所属仓库代号列表
//			List passageCountList=new ArrayList();//巷道数列表
			for(int i=0;i<stockAreaList.size();i++){
				map = new HashMap<String, String>();
				CargoInfoStockAreaBean bean=(CargoInfoStockAreaBean)stockAreaList.get(i);
				int storageId2=bean.getStorageId();
				String storageCode=service.getCargoInfoStorage("id="+storageId2).getWholeCode();
//				storageCodeList.add(storageCode);
				map.put("storage_code", storageCode);//所属仓库代号
				int passageCount=service.getCargoInfoPassageCount("stock_area_id="+bean.getId());
//				passageCountList.add(Integer.valueOf(passageCount));
				map.put("passage_count", Integer.valueOf(passageCount)+"");//巷道数
				map.put("area_code", bean.getCode());//区域代号
				map.put("area_name", bean.getName());//区域名称
				map.put("stock_type", bean.getStockTypeName());//库存类型
				map.put("stock_area_id", bean.getId()+"");//仓库id
				list.add(map);
			}
			resultMap.put("rows", list);
//			request.setAttribute("wholeStorageList", wholeStorageList);
//			request.setAttribute("stockAreaList", stockAreaList);
//			request.setAttribute("storageCodeList", storageCodeList);
//			request.setAttribute("passageCountList", passageCountList);
//			request.setAttribute("wholeCityList", wholeCityList);
//			request.setAttribute("paging", paging);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return resultMap;
	}
	/**
	 * 添加仓库区域
	 * 2013-8-15
	 * 朱爱林
	 */
	@RequestMapping("/addStockArea")
	public void addStockArea(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		synchronized(cargoLock){
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			service.getDbOp().startTransaction();
			String storageId=StringUtil.convertNull(request.getParameter("storageId"));//所属仓库Id
			int stockType=Integer.parseInt(StringUtil.convertNull(request.getParameter("stockType")));//库存类型
			String stockAreaCode=StringUtil.convertNull(request.getParameter("newStockAreaCode")).trim();//区域代号
			String stockAreaName=StringUtil.convertNull(request.getParameter("stockAreaName")).trim();//区域名称
			String passageCount=request.getParameter("passageCount").trim();//巷道数量
			if(storageId.equals("")){
//				request.setAttribute("tip", "请选择所属仓库！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'请选择所属仓库！'}");
				response.getWriter().write(result.toString());
				return;
			}
			if(stockAreaCode.equals("")){
//				request.setAttribute("tip", "请填写区域代号！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'请填写区域代号！'}");
				response.getWriter().write(result.toString());
				return;
			}
			char[] ccode=stockAreaCode.toCharArray();
			if(ccode.length>1||ccode[0]<65||ccode[0]>90){
//				request.setAttribute("tip", "请填写有效的区域代号！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'请填写有效的区域代号！'}");
				response.getWriter().write(result.toString());
				return;
			}
			int stockAreaCount=service.getCargoInfoStockAreaCount("code='"+stockAreaCode+"' and storage_id="+storageId);
			if(stockAreaCount!=0){
//				request.setAttribute("tip","区域代号与其他重复，请重新填写！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'区域代号与其他重复，请重新填写！'}");
				response.getWriter().write(result.toString());
				return;
			}
			if(stockAreaName.equals("")){
//				request.setAttribute("tip", "请填写区域名称！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'请填写区域名称！'}");
				response.getWriter().write(result.toString());
				return;
			}
			int stockAreaNameCount=service.getCargoInfoStockAreaCount("name='"+stockAreaName+"' and storage_id="+storageId);
			if(stockAreaNameCount!=0){
//				request.setAttribute("tip","区域名称与其他重复，请重新填写！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'区域名称与其他重复，请重新填写！'}");
				response.getWriter().write(result.toString());
				return;
			}
			if(passageCount.equals("")){
//				request.setAttribute("tip","请填写巷道个数！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'请填写巷道个数！'}");
				response.getWriter().write(result.toString());
				return;
			}
			if(!(passageCount.matches("^[0-9]{1}$")||(passageCount.matches("^[0-9]{2}$")))){
//				request.setAttribute("tip","巷道个数只允许输入两位纯数字！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'巷道个数只允许输入两位纯数字！'}");
				response.getWriter().write(result.toString());
				return;
			}
			String storageCode=service.getCargoInfoStorage("id="+storageId).getWholeCode();//所属仓库完整编号
			String wholeCode=storageCode+"-"+stockAreaCode;//仓库区域完整编号
			int areaId=service.getCargoInfoArea("code='"+storageCode.subSequence(2, 3)+"'").getId();//区域Id
			int cityId=service.getCargoInfoCity("code='"+storageCode.substring(0, 2)+"'").getId();//城市Id
			CargoInfoStockAreaBean bean=new CargoInfoStockAreaBean();
			bean.setCode(stockAreaCode);
			bean.setWholeCode(wholeCode);
			bean.setName(stockAreaName);
			bean.setStockType(stockType);
			bean.setStorageId(Integer.parseInt(storageId));
			bean.setAreaId(areaId);
			bean.setCityId(cityId);
			service.addCargoInfoStockArea(bean);
//			request.setAttribute("cityId", ""+cityId);
//			request.setAttribute("areaId", ""+areaId);
//			request.setAttribute("storageId", storageId);
			
			CargoInfoStockAreaBean newStockArea=service.getCargoInfoStockArea("whole_code='"+wholeCode+"'");//刚添加的仓库区域（含Id）
			for(int i=0;i<Integer.parseInt(passageCount);i++){
				List passageList=service.getCargoInfoPassageList("stock_area_id="+newStockArea.getId(), -1, -1, "id desc");
				String maxPassageCode="00";//该仓库区域下最大巷道编号
				if(passageList.size()>0){
					maxPassageCode=((CargoInfoPassageBean)passageList.get(0)).getCode();
				}
				int passageCode=Integer.parseInt(maxPassageCode)+1;
				
				CargoInfoPassageBean passageBean=new CargoInfoPassageBean();//将要添加的巷道Bean
				passageBean.setCode(passageCode<10?"0"+passageCode:""+passageCode);
				passageBean.setWholeCode(wholeCode+passageBean.getCode());
				passageBean.setStockType(stockType);
				passageBean.setCityId(cityId);
				passageBean.setAreaId(areaId);
				passageBean.setStorageId(Integer.parseInt(storageId));
				passageBean.setStockAreaId(newStockArea.getId());
				service.addCargoInfoPassage(passageBean);
			}
			service.getDbOp().commitTransaction();
			result.append("{result:'success',tip:'仓库区域添加成功！'}");
			response.getWriter().write(result.toString());
			return;
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
	}
	/**
	 * 删除仓库区域
	 * 2013-8-15
	 * 朱爱林
	 */
	@RequestMapping("/deleteStockArea")
	public void deleteStockArea(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		synchronized(cargoLock){
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			service.getDbOp().startTransaction();
			int stockAreaId=Integer.parseInt(request.getParameter("stockAreaId"));
			if(service.getCargoInfoShelfCount("stock_area_id="+stockAreaId)>0){
//				request.setAttribute("tip", "该仓库区域下已添加货架，不能删除！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'该仓库区域下已添加货架，不能删除！'}");
				response.getWriter().write(result.toString());
				return;
			}
//			CargoInfoStockAreaBean bean=service.getCargoInfoStockArea("id="+stockAreaId);
//			request.setAttribute("storageId", bean.getStorageId()+"");
			service.deleteCargoInfoStockArea("id="+stockAreaId);
			request.setAttribute("deleted", Integer.valueOf(1));
			service.getDbOp().commitTransaction();
			result.append("{result:'success',tip:'仓库区域删除成功！'}");
			response.getWriter().write(result.toString());
			return;
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
	}
	/**
	 * 获取下拉选：所属区域
	 * 2013-8-15
	 * 朱爱林	
	 * @return
	 */
	@RequestMapping("/querySelectstockAreas")
	@ResponseBody
	public Object querySelectstockAreas(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
//		StringBuilder result = new StringBuilder();
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			
			String storageId=request.getParameter("storageId");
			String choice = StringUtil.convertNull(request.getParameter("choice"));
			List cargoInfoStockAreaList=new ArrayList();
			if(choice.equals("all")){
				cargoInfoStockAreaList=service.getCargoInfoStockAreaList(null, -1, -1, "id desc");
			}else{
				if(storageId.equals("")){
				}else{
					cargoInfoStockAreaList=service.getCargoInfoStockAreaList("storage_id="+storageId, -1, -1, "id desc");
				}
			}
			
			List<Map<String,String>> list = new ArrayList<Map<String,String>>();
			Map<String,String> map = new HashMap<String, String>();
			map.put("id", "");
			map.put("text", "-请选择-");
			map.put("code", "-请选择-");
			map.put("selected", "true");
			list.add(map);
			for(int i=0;i<cargoInfoStockAreaList.size();i++){
				map = new HashMap<String, String>();
				CargoInfoStockAreaBean cargoInfoStockAreaBean = (CargoInfoStockAreaBean) cargoInfoStockAreaList.get(i);
				map.put("id", cargoInfoStockAreaBean.getId()+"");
				map.put("text", cargoInfoStockAreaBean.getCode()+"--"+cargoInfoStockAreaBean.getName());
				list.add(map);
			}
			return list;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return null;
	}
	/**
	 * 巷道列表查询
	 * 2013-8-15
	 * 朱爱林	
	 * @return
	 */
	@RequestMapping("/cargoInfoPassageList")
	@ResponseBody
	public Object cargoInfoPassageList(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService cargoService=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		String cityId=StringUtil.convertNull(request.getParameter("cityId"));
		String areaId=StringUtil.convertNull(request.getParameter("areaId"));
		String storageId=StringUtil.convertNull(request.getParameter("storageId"));
		String stockAreaId=StringUtil.convertNull(request.getParameter("stockAreaId"));
		String stockAreaCode=StringUtil.convertNull(request.getParameter("stockAreaCode")).trim();
		String passageCode=StringUtil.convertNull(request.getParameter("passageCode")).trim();
		
		Map<String,Object> resultMap = new HashMap<String, Object>();
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		resultMap.put("total","0");
		resultMap.put("rows",list);
		try{
//			int countPerPage=20;
//			int pageIndex=0;
//			if(request.getParameter("pageIndex")!=null){
//				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
//			}
			String page1 = StringUtil.convertNull(request.getParameter("page"));
			String rows1 = StringUtil.convertNull(request.getParameter("rows"));
			int page = page1.equals("")?1:Integer.parseInt(page1);
			int rows = rows1.equals("")?20:Integer.parseInt(rows1);
			int start = (page-1)*rows;//开始下标
			
            String condition="1=1";//查询条件
			if(!cityId.equals("")){
				condition+=" and city_id=";
				condition+=cityId;
			}
			if(!areaId.equals("")){
				condition+=" and area_id=";
				condition+=areaId;
			}
			if(!storageId.equals("")){
				condition+=" and storage_id=";
				condition+=storageId;
			}
			if(!stockAreaId.equals("")){
				condition+=" and stock_area_id=";
				condition+=stockAreaId;
			}
			if(!stockAreaCode.equals("")){
				CargoInfoStockAreaBean bean=cargoService.getCargoInfoStockArea("whole_code='"+stockAreaCode+"'");
				condition+=" and stock_area_id=";
				condition+=(bean==null?"0":""+bean.getId());
			}
			if(!passageCode.equals("")){
				condition+=" and whole_code='";
				condition+=passageCode;
				condition+="'";
			}
			if(condition.toString().equals("")){
				condition=null;
			}
            
			int totalCount=cargoService.getCargoInfoPassageCount(condition);
			resultMap.put("total", totalCount+"");
//			PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
//            String para="";
//            para=(cityId.equals("")?"":"&cityId="+cityId)
//    			+(areaId.equals("")?"":"&areaId="+areaId)
//    			+(storageId.equals("")?"":"&storageId="+storageId)
//    			+(stockAreaId.equals("")?"":"&stockAreaId="+stockAreaId)
//    			+(stockAreaCode.equals("")?"":"&stockAreaCode="+stockAreaCode)
//    			+(passageCode.equals("")?"":"&passageCode="+passageCode)
//    			;
//            paging.setCurrentPageIndex(pageIndex);
//            paging.setPrefixUrl("cargoInfo.do?method=cargoInfoPassageList"+para);
			
			List passageList=cargoService.getCargoInfoPassageList(condition, start, rows, null);
			for(int i=0;i<passageList.size();i++){
				CargoInfoPassageBean passageBean=(CargoInfoPassageBean)passageList.get(i);
				int shelfCount=cargoService.getCargoInfoShelfCount("passage_id="+passageBean.getId());
				passageBean.setShelfCount(shelfCount);
				map = new HashMap<String, String>();
				map.put("passage_id", passageBean.getId()+"");
				map.put("passage_code", passageBean.getCode());//巷道号
				map.put("belong_area", passageBean.getWholeCode().substring(0,7));//所属区域
				map.put("stock_type", passageBean.getStockTypeName());//库存类型
				map.put("shelf_count", shelfCount+"");//货架数
				list.add(map);
			}
//			List wholeCityList=cargoService.getCargoInfoCityList(null, -1, -1, null);//所有城市列表
//			List wholeStorageList=cargoService.getCargoInfoStorageList(null, -1, -1, "id desc");//所有仓库列表
//			request.setAttribute("list", passageList);
//			request.setAttribute("wholeCityList", wholeCityList);
//			request.setAttribute("wholeStorageList", wholeStorageList);
//			request.setAttribute("paging", paging);
			resultMap.put("rows", list);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			cargoService.releaseAll();
		}
		return resultMap;
	}
	/**
	 * 添加新巷道
	 * 2013-8-15
	 * 朱爱林
	 */
	@RequestMapping("/addPassage")
	public void addPassage(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		synchronized(cargoLock){
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			service.getDbOp().startTransaction();
			String storageId=StringUtil.convertNull(request.getParameter("storageId"));//所属仓库Id
			String stockAreaId=StringUtil.convertNull(request.getParameter("stockAreaId"));//所属区域Id
			String passageCode=StringUtil.convertNull(request.getParameter("newPassageCode")).trim();//巷道号
			int shelfCount=StringUtil.toInt(request.getParameter("shelfCount"));//添加货架数
						
			if(storageId.equals("")){
//				request.setAttribute("tip", "请选择所属仓库！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'请选择所属仓库！'}");
				response.getWriter().write(result.toString());
				return;
			}
			if(stockAreaId.equals("")){
//				request.setAttribute("tip", "请选择所属区域！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'请选择所属区域！'}");
				response.getWriter().write(result.toString());
				return;
			}
			char[] ccode=passageCode.toCharArray();
			if(ccode.length==1||(ccode.length==2&&(ccode[0]<48||ccode[0]>57||ccode[1]<48||ccode[1]>57))||ccode.length>2||passageCode.equals("00")){
//				request.setAttribute("tip", "请填写有效的巷道号！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'请填写有效的巷道号！'}");
				response.getWriter().write(result.toString());
				return;
			}
			int passageCodeCount=service.getCargoInfoPassageCount("code='"+passageCode+"' and stock_area_id="+stockAreaId);
			if(passageCodeCount!=0){
//				request.setAttribute("tip","该巷道号已存在，请重新填写！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'该巷道号已存在，请重新填写！'}");
				response.getWriter().write(result.toString());
				return;
			}
			int currentCount=service.getCargoInfoPassageCount("stock_area_id="+stockAreaId);
			if(currentCount>=99){
//				request.setAttribute("tip", "该仓库区域已有99个巷道，不能再添加！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'该仓库区域已有99个巷道，不能再添加！'}");
				response.getWriter().write(result.toString());
				return;
			}
			if(shelfCount>99){
//				request.setAttribute("tip", "最多添加99个货架！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'最多添加99个货架！'}");
				response.getWriter().write(result.toString());
				return;
			}
			CargoInfoStockAreaBean stockAreaBean=service.getCargoInfoStockArea("storage_id="+storageId+" and id="+stockAreaId);
			if(stockAreaBean==null){
//				request.setAttribute("tip", "没有找到该仓库区域！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'没有找到该仓库区域！'}");
				response.getWriter().write(result.toString());
				return;
			}
			CargoInfoPassageBean passageBean=new CargoInfoPassageBean();
			passageBean.setCityId(stockAreaBean.getCityId());
			passageBean.setAreaId(stockAreaBean.getAreaId());
			passageBean.setStorageId(stockAreaBean.getStorageId());
			passageBean.setStockAreaId(stockAreaBean.getId());
			passageBean.setStockType(stockAreaBean.getStockType());
			String code="";
			if(!passageCode.equals("")){//输入了巷道号
				int passageCount=service.getCargoInfoPassageCount("stock_area_id="+stockAreaId+" and code='"+passageCode+"'");
				if(passageCount>0){
//					request.setAttribute("tip", "巷道号重复！");
//					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
//					return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'巷道号重复！'}");
					response.getWriter().write(result.toString());
					return;
				}
				code=passageCode;
			}else{
				List passageList=service.getCargoInfoPassageList("stock_area_id="+stockAreaId, -1, -1, "id desc");
				if(passageList.size()>0){
					CargoInfoPassageBean passage=(CargoInfoPassageBean)passageList.get(0);
					int maxCode=Integer.parseInt(passage.getCode())+1;
					code=maxCode<10?("0"+maxCode):(""+maxCode);
				}else{
					code="01";
				}
			}
			passageBean.setCode(code);
			passageBean.setWholeCode(stockAreaBean.getWholeCode()+code);
			service.addCargoInfoPassage(passageBean);
			
			CargoInfoPassageBean newPassageBean=service.getCargoInfoPassage("whole_code='"+passageBean.getWholeCode()+"'");
			for(int i=0;i<shelfCount;i++){
				CargoInfoShelfBean shelfBean=new CargoInfoShelfBean();
				shelfBean.setCityId(newPassageBean.getCityId());
				shelfBean.setAreaId(newPassageBean.getAreaId());
				shelfBean.setStorageId(newPassageBean.getStorageId());
				shelfBean.setStockAreaId(newPassageBean.getStockAreaId());
				shelfBean.setPassageId(newPassageBean.getId());
				shelfBean.setStockType(newPassageBean.getStockType());
				shelfBean.setFloorCount(5);
				String shelfCode=i+1<10?"0"+(i+1):""+(i+1);
				shelfBean.setCode(shelfCode);
				shelfBean.setWholeCode(newPassageBean.getWholeCode()+shelfCode);
				service.addCargoInfoShelf(shelfBean);
			}
			
			List passageList=service.getCargoInfoPassageList("1=1", -1, -1, null);
			for(int i=0;i<passageList.size();i++){
				CargoInfoPassageBean bean=(CargoInfoPassageBean)passageList.get(i);
				int count=service.getCargoInfoShelfCount("passage_id="+bean.getId());
				bean.setShelfCount(count);
			}
			
//			request.setAttribute("list", passageList);
//			List wholeCityList=service.getCargoInfoCityList(null, -1, -1, null);//所有城市列表
//			List wholeStorageList=service.getCargoInfoStorageList(null, -1, -1, "id desc");//所有仓库列表
//			request.setAttribute("wholeCityList", wholeCityList);
//			request.setAttribute("wholeStorageList", wholeStorageList);
			service.getDbOp().commitTransaction();
			result.append("{result:'success',tip:'新巷道添加成功！'}");
			response.getWriter().write(result.toString());
			return;
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
	}
	/**
	 * 删除巷道
	 * 2013-8-15
	 * 朱爱林
	 */
	@RequestMapping("/deletePassage")
	public void deletePassage(HttpServletRequest request ,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		WareService wareService = new WareService();
		ICargoService cargoService=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		String passageId=request.getParameter("passageId");
		synchronized (cargoLock) {
		try{
			cargoService.getDbOp().startTransaction();
			cargoService.deleteCargoInfoPassage("id="+passageId);
			cargoService.deleteCargoInfoShelf("passage_id="+passageId);
			cargoService.deleteCargoInfo("passage_id="+passageId);
			List passageList=cargoService.getCargoInfoPassageList("1=1", -1, -1, null);
			for(int i=0;i<passageList.size();i++){
				CargoInfoPassageBean passageBean=(CargoInfoPassageBean)passageList.get(i);
				int shelfCount=cargoService.getCargoInfoShelfCount("passage_id="+passageBean.getId());
				passageBean.setShelfCount(shelfCount);
			}
			
//			request.setAttribute("list", passageList);
			List wholeCityList=cargoService.getCargoInfoCityList(null, -1, -1, null);//所有城市列表
			List wholeStorageList=cargoService.getCargoInfoStorageList(null, -1, -1, "id desc");//所有仓库列表
//			request.setAttribute("wholeCityList", wholeCityList);
//			request.setAttribute("wholeStorageList", wholeStorageList);
			cargoService.getDbOp().commitTransaction();
			result.append("{result:'success',tip:'巷道删除成功！'}");
			response.getWriter().write(result.toString());
			return;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			cargoService.releaseAll();
		}
		}
	}
	/**
	 * 查询下拉菜单 巷道
	 * 2013-8-16
	 * 朱爱林	List productLineList=wareService.getProductLineList("1=1");
	 */
	@RequestMapping("/querySelectPassage")
	@ResponseBody
	public Object querySelectPassage(HttpServletRequest request,HttpServletResponse response){
		String stockAreaId=request.getParameter("stockAreaId");
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> map = new HashMap<String, String>();
		try{
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if(user==null){
				request.setAttribute("tip", "当前没有登录，操作失败！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			List cargoInfoPassageList=null;
			if(stockAreaId.equals("")){
				cargoInfoPassageList=new ArrayList();
			}else{
				cargoInfoPassageList=service.getCargoInfoPassageList("stock_area_id="+stockAreaId, -1, -1, "id desc");
			}
			map.put("id", "");
			map.put("text", "--请选择--");
			map.put("selected", "true");
			list.add(map);
			for(int i=0;i<cargoInfoPassageList.size();i++){
				CargoInfoPassageBean bean = (CargoInfoPassageBean) cargoInfoPassageList.get(i);
				map = new HashMap<String, String>();
				map.put("id", bean.getId()+"");
				map.put("text", bean.getCode());
				list.add(map);
			}
			return list;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return null;
	}
	/**
	 * 货架列表查询
	 * 2013-8-16
	 * 朱爱林	
	 */
	@RequestMapping("/cargoInfoShelfList")
	@ResponseBody
	public Object cargoInfoShelfList(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		Map<String,Object> resultMap = new HashMap<String, Object>();
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		resultMap.put("total","0");
		resultMap.put("rows",list);
		try{
			List wholeCityList=service.getCargoInfoCityList(null, -1, -1, "id desc");//所有城市列表
			List wholeStorageList=service.getCargoInfoStorageList(null, -1, -1, "id desc");//所有地区列表
			String cityId=StringUtil.convertNull(request.getParameter("cityId"));
			String areaId=StringUtil.convertNull(request.getParameter("areaId"));
			String storageId=StringUtil.convertNull(request.getParameter("storageId"));
			String stockAreaId=StringUtil.convertNull(request.getParameter("stockAreaId"));
			String passageId=StringUtil.convertNull(request.getParameter("passageId"));//巷道Id
			String stockAreaCode=StringUtil.convertNull(request.getParameter("stockAreaCode")).trim();
			String shelfCode=StringUtil.convertNull(request.getParameter("shelfCode")).trim();
			if(request.getParameter("storageId")!=null&&request.getParameter("stockAreaId")!=null&&request.getParameter("cityId")==null){
				CargoInfoStockAreaBean bean=service.getCargoInfoStockArea("id="+stockAreaId+" and storage_id="+storageId);
				request.setAttribute("cityId", bean.getCityId()+"");
				request.setAttribute("areaId", bean.getAreaId()+"");
			}
//			Map<String,Object> resultMap = new HashMap<String, Object>();
//			List<Map<String,String>> list = new ArrayList<Map<String,String>>();
//			Map<String,String> map = null;
			
			String page1 = StringUtil.convertNull(request.getParameter("page"));
			String rows1 = StringUtil.convertNull(request.getParameter("rows"));
			int page = page1.equals("")?1:Integer.parseInt(page1);
			int rows = rows1.equals("")?20:Integer.parseInt(rows1);
			int start = (page-1)*rows;//开始下标
			
//			int countPerPage=20;
//			int pageIndex=0;
//			if(request.getParameter("pageIndex")!=null){
//				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
//			}
//            PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);
//            String para="";
//            para=(cityId.equals("")?"":"&cityId="+cityId)
//    			+(areaId.equals("")?"":"&areaId="+areaId)
//    			+(storageId.equals("")?"":"&storageId="+storageId)
//    			+(stockAreaId.equals("")?"":"&stockAreaId="+stockAreaId)
//    			+(passageId.equals("")?"":"&passageId="+passageId)
//    			+(stockAreaCode.equals("")?"":"&stockAreaCode="+stockAreaCode)
//    			+(shelfCode.equals("")?"":"&shelfCode="+shelfCode)
//    			;
//            paging.setCurrentPageIndex(pageIndex);
//            paging.setPrefixUrl("cargoInfo.do?method=cargoInfoShelfList"+para);
			
			List shelfList=new ArrayList();//货架列表
			String condition="1=1";//查询条件
			if(!cityId.equals("")){
				condition+=" and city_id=";
				condition+=cityId;
			}
			if(!areaId.equals("")){
				condition+=" and area_id=";
				condition+=areaId;
			}
			if(!storageId.equals("")){
				condition+=" and storage_id=";
				condition+=storageId;
			}
			if(!stockAreaId.equals("")){
				condition+=" and stock_area_id=";
				condition+=stockAreaId;
			}
			if(!passageId.equals("")){
				condition+=" and passage_id=";
				condition+=passageId;
			}
			if(!stockAreaCode.equals("")){
				CargoInfoStockAreaBean bean=service.getCargoInfoStockArea("whole_code='"+stockAreaCode+"'");
				condition+=" and stock_area_id=";
				condition+=(bean==null?"0":""+bean.getId());
			}
			if(!shelfCode.equals("")){
				condition+=" and whole_code='";
				condition+=shelfCode;
				condition+="'";
			}
			if(condition.toString().equals("")){
				condition=null;
			}
			shelfList=service.getCargoInfoShelfList(condition,start,rows,"whole_code asc");
			int totalCount = service.getCargoInfoShelfCount(condition); //根据条件得到 总数量
			resultMap.put("total", totalCount+"");
//			paging.setTotalCount(totalCount);
//			paging.setTotalPageCount(totalCount%countPerPage==0?totalCount/countPerPage:totalCount/countPerPage+1);
//			List stockAreaCodeList=new ArrayList();//所属区域代号列表
//			List cargoCountList=new ArrayList();//货位数列表
			boolean boo = true;
			for(int i=0;i<shelfList.size();i++){
				
				map = new HashMap<String, String>();
				CargoInfoShelfBean bean=(CargoInfoShelfBean)shelfList.get(i);//一个货架
				
				int stockAreaId2=bean.getStockAreaId();
				String stockAreaCode2=service.getCargoInfoStockArea("id="+stockAreaId2).getWholeCode();
//				stockAreaCodeList.add(stockAreaCode2);
				
				int floorCount=bean.getFloorCount();//该货架的层数
//				List cargoSheldCountList=new ArrayList();//每层的货位数列表
				StringBuilder sheld = new StringBuilder();
				for(int j=1;j<=floorCount;j++){
					int cargoCount=service.getCargoInfoCount("shelf_id="+bean.getId()+" and floor_num="+j+" and status<>3");
//					cargoSheldCountList.add(Integer.valueOf(cargoCount));
					sheld.append(Integer.valueOf(cargoCount)).append("/").append(j+"  ");
					if(Integer.valueOf(cargoCount)!=0){
						boo = false;
					}
				}
//				cargoCountList.add(cargoSheldCountList);
				map.put("shelf_id", bean.getId()+"");
				map.put("del_flag", boo+"");//标识是否可以进行删除操作
				map.put("shelf_code", bean.getCode());//货架号
				map.put("stock_area_code2", stockAreaCode2);//所属区域
				map.put("shelf_whole_code", bean.getWholeCode().length()==9?"":bean.getWholeCode().substring(7,9));//巷道号
				map.put("shelf_stock_type_name", bean.getStockTypeName());//库存类型
				map.put("shelf_floor_count", bean.getFloorCount()+"层");//货架层数
				map.put("shelf_shelf_floor", sheld.toString());//货位数/层
				list.add(map);
			}
			resultMap.put("rows", list);
//			request.setAttribute("shelfList", shelfList);
//			request.setAttribute("wholeStorageList", wholeStorageList);
//			request.setAttribute("wholeCityList", wholeCityList);
//			request.setAttribute("stockAreaCodeList", stockAreaCodeList);
//			request.setAttribute("cargoCountList", cargoCountList);
//			request.setAttribute("paging", paging);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return resultMap;
	}
	/**
	 * 增加新货架
	 * 2013-8-16
	 * 朱爱林
	 */
	@RequestMapping("/addShelf")
	public void addShelf(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		synchronized(cargoLock){
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			service.getDbOp().startTransaction();
			String storageId=StringUtil.convertNull(request.getParameter("storageId"));//所属仓库Id
			String stockAreaId=StringUtil.convertNull(request.getParameter("stockAreaId"));//所属区域Id
			String passageId=StringUtil.convertNull(request.getParameter("passageId"));//巷道Id，可以为空
			String shelfCode=StringUtil.convertNull(request.getParameter("newShelfCode")).trim();//货架号
			String floorCount=StringUtil.convertNull(request.getParameter("floorCount")).trim();//层数
			String shelfCount=StringUtil.convertNull(request.getParameter("shelfCount")).trim();//添加货架数
						
			if(storageId.equals("")){
//				request.setAttribute("tip", "请选择所属仓库！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'请选择所属仓库！'}");
				response.getWriter().write(result.toString());
				return;
			}
			if(stockAreaId.equals("")){
//				request.setAttribute("tip", "请选择所属区域！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'请选择所属区域！'}");
				response.getWriter().write(result.toString());
				return;
			}
//			if(passageId.equals("")){
//				request.setAttribute("tip", "请选择巷道！");
//				request.setAttribute("result", "failure");
//				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
//			}
			char[] ccode=shelfCode.toCharArray();
			if(ccode.length==1||(ccode.length==2&&(ccode[0]<48||ccode[0]>57||ccode[1]<48||ccode[1]>57))||ccode.length>2||shelfCode.equals("00")){
//				request.setAttribute("tip", "请填写有效的货架号！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'请填写有效的货架号！'}");
				response.getWriter().write(result.toString());
				return;
			}
			int shelfCodeCount=service.getCargoInfoShelfCount("code='"+shelfCode+"' and stock_area_id="+stockAreaId+(passageId.equals("")?"":" and passage_id="+passageId));
			if(shelfCodeCount!=0){
//				request.setAttribute("tip","该货架号已存在，请重新填写！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'该货架号已存在，请重新填写！'}");
				response.getWriter().write(result.toString());
				return;
			}
			if(floorCount.equals("")){
//				request.setAttribute("tip", "请填写货架层数！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'请填写货架层数！'}");
				response.getWriter().write(result.toString());
				return;
			}
			char[] cfloor=floorCount.toCharArray();
			if(cfloor.length>1||cfloor[0]<48||cfloor[0]>57){
//				request.setAttribute("tip", "货架层数必须是1位的数字，请重新填写！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'货架层数必须是1位的数字，请重新填写！'}");
				response.getWriter().write(result.toString());
				return;
			}
			if(shelfCount.equals("")){
//				request.setAttribute("tip", "请填写货架个数");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'请填写货架个数！'}");
				response.getWriter().write(result.toString());
				return;
			}
			char[] ccount=shelfCount.toCharArray();
			if(ccount.length==1){
				if(ccount[0]<48||ccount[0]>57){
//					request.setAttribute("tip", "货架个数必须是1位或2位的数字，请重新填写！");
//					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
//					return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'货架个数必须是1位或2位的数字，请重新填写！'}");
					response.getWriter().write(result.toString());
					return;
				}
			}else if(ccount.length==2){
				if(ccount[0]<48||ccount[0]>57||ccount[1]<48||ccount[1]>57){
//					request.setAttribute("tip", "货架个数必须是1位或2位的数字，请重新填写！");
//					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
//					return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'货架个数必须是1位或2位的数字，请重新填写！'}");
					response.getWriter().write(result.toString());
					return;
				}
			}else if(ccount.length>2){
//				request.setAttribute("tip", "货架个数必须是1位或2位的数字，请重新填写！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'货架个数必须是1位或2位的数字，请重新填写！'}");
				response.getWriter().write(result.toString());
				return;
			}
			List passageList=service.getCargoInfoPassageList("stock_area_id="+stockAreaId+(passageId.equals("")?"":" and id="+passageId), -1, -1, null);
			for(int i=0;i<passageList.size();i++){
				CargoInfoPassageBean passage=(CargoInfoPassageBean)passageList.get(i);
				int currentCount=service.getCargoInfoShelfCount("passage_id="+passage.getId());
				int totalCount=Integer.parseInt(shelfCount)+currentCount;
				if(totalCount>99){
//					request.setAttribute("tip", "仓库每个巷道内的货架个数不得超过99个。"+"\\r"+passage.getWholeCode()+"巷道内当前货架个数为"+currentCount+"个，" +
//							"本次要添加"+Integer.parseInt(shelfCount)+"个。"+"\\r已超出巷道内货架个数限定范围，请重新填写！");
//					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
//					return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'仓库每个巷道内的货架个数不得超过99个。"+"\\r"+passage.getWholeCode()+"巷道内当前货架个数为"+currentCount+"个，" +
							"本次要添加"+Integer.parseInt(shelfCount)+"个。"+"\\r已超出巷道内货架个数限定范围，请重新填写！'}");
					response.getWriter().write(result.toString());
					return;
				}
			}
			
			
			if(!shelfCode.equals("")){//输入了货架号
				int i=0;//比输入的货架号大的货位个数
				for(int k=0;k<passageList.size();k++){
					CargoInfoPassageBean passage=(CargoInfoPassageBean)passageList.get(k);
					List cargoInfoShelfList=service.getCargoInfoShelfList("passage_id="+passage.getId(), -1, -1, "code desc");
					for(int j=0;j<cargoInfoShelfList.size();j++){
						CargoInfoShelfBean bean=(CargoInfoShelfBean)cargoInfoShelfList.get(j);//该区域内所有货架
						if(Integer.parseInt(bean.getCode())>Integer.parseInt(shelfCode)){
							i++;
						}
					}
					if(i+Integer.parseInt(shelfCode)+Integer.parseInt(shelfCount)>100){
//						request.setAttribute("tip", "编号超过上限，无法添加货架！");
//						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
//						return mapping.findForward(IConstants.FAILURE_KEY);
						result.append("{result:'failure',tip:'编号超过上限，无法添加货架！'}");
						response.getWriter().write(result.toString());
						return;
					}
				}
			}else{//没输入货架号
				for(int k=0;k<passageList.size();k++){
					CargoInfoPassageBean passage=(CargoInfoPassageBean)passageList.get(k);
					List cargoInfoShelfList=service.getCargoInfoShelfList("passage_id="+passage.getId(), -1, -1, "code desc");
					if(cargoInfoShelfList.size()>0){
						CargoInfoShelfBean bean=(CargoInfoShelfBean)cargoInfoShelfList.get(0);
						if(Integer.parseInt(bean.getCode())+Integer.parseInt(shelfCount)>99){
//							request.setAttribute("tip", "编号超过上限，无法添加货架！");
//							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
//							return mapping.findForward(IConstants.FAILURE_KEY);
							result.append("{result:'failure',tip:'编号超过上限，无法添加货架！'}");
							response.getWriter().write(result.toString());
							return;
						}
					}
				}
			}
			for(int k=0;k<passageList.size();k++){
				CargoInfoPassageBean passage=(CargoInfoPassageBean)passageList.get(k);
				for(int i=0;i<Integer.parseInt(shelfCount);i++){
					CargoInfoShelfBean bean=new CargoInfoShelfBean();
					String passageCode=service.getCargoInfoPassage("id="+passage.getId()).getWholeCode();//所属巷道的whole_code
					int stockType=service.getCargoInfoStockArea("id="+stockAreaId).getStockType();//所属仓库区域的库类别
					bean.setStockType(stockType);
					bean.setFloorCount(Integer.parseInt(floorCount));
					bean.setPassageId(passage.getId());
					bean.setStockAreaId(Integer.parseInt(stockAreaId));
					bean.setStorageId(Integer.parseInt(storageId));
					int areaId=service.getCargoInfoArea("code='"+passageCode.substring(2,3)+"'").getId();
					bean.setAreaId(areaId);
					int cityId=service.getCargoInfoCity("code='"+passageCode.substring(0, 2)+"'").getId();
					bean.setCityId(cityId);
					
					if(!shelfCode.equals("")){
						int j=i;//自增的量，如果自增后代号重复可以j++
						int code=Integer.parseInt(shelfCode);
						String sCode="";
						do{
							if(code+j<10){
								sCode="0"+(code+j);
							}else{
								sCode=""+(code+j);
							}
							j++;
						}while(service.getCargoInfoShelfCount("code='"+sCode+"' and passage_id="+passage.getId())!=0);
						bean.setCode(sCode);
						bean.setWholeCode(passageCode+sCode);
					}else{
						List cargoInfoShelfList=service.getCargoInfoShelfList("passage_id="+passage.getId(), -1, -1, "code desc");
						String code="";
						if(cargoInfoShelfList.size()==0){
							code=passageCode+"00";
						}else{
							CargoInfoShelfBean cbean=(CargoInfoShelfBean)cargoInfoShelfList.get(0);
							code=cbean.getWholeCode();
						}
						String code1=code.substring(0, 9);
						String code2=code.substring(9, 11);
						int codeInt=Integer.parseInt(code2)+1;
						if(codeInt<10){
							bean.setCode("0"+codeInt);
							bean.setWholeCode(code1+"0"+codeInt);
						}else{
							bean.setCode(String.valueOf(codeInt));
							bean.setWholeCode(code1+codeInt);
						}
					}
					service.addCargoInfoShelf(bean);
				}
			}
			service.getDbOp().commitTransaction();
			result.append("{result:'success',tip:'货架添加成功！'}");
			response.getWriter().write(result.toString());
			return;
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
	}
	/**
	 * 删除货架
	 * 2013-8-16
	 * 朱爱林
	 */
	@RequestMapping("/deleteShelf")
	public void deleteShelf(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		synchronized(cargoLock){
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			service.getDbOp().startTransaction();
			int shelfId=Integer.parseInt(request.getParameter("shelfId"));
			if(service.getCargoInfoCount("shelf_id="+shelfId+" and status<>3")>0){
//				request.setAttribute("tip", "该货架下已添加货位，不能删除！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'该货架下已添加货位，不能删除！'}");
				response.getWriter().write(result.toString());
				return;
			}
			service.deleteCargoInfoShelf("id="+shelfId);
			request.setAttribute("deleted", Integer.valueOf(1));
			service.getDbOp().commitTransaction();
			result.append("{result:'success',tip:'货架删除成功！'}");
			response.getWriter().write(result.toString());
			return;
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
	}
	/**
	 * 查询下拉菜单 产品线
	 * 2013-8-19
	 * 朱爱林
	 */
	@RequestMapping("/querySelectProductLine")
	@ResponseBody
	public Object querySelectProductLine(HttpServletRequest request,HttpServletResponse response){
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
//		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> map = new HashMap<String, String>();
		try{
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if(user==null){
				request.setAttribute("tip", "当前没有登录，操作失败！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			List productLineList=wareService.getProductLineList("1=1");
			map.put("id", "");
			map.put("text", "--请选择--");
			map.put("selected", "true");
			list.add(map);
//			<option value="<%=productLine.getId() %>" 
//					<%if(productLineId!=null&&(!productLineId.equals(""))&&Integer.parseInt(productLineId)==productLine.getId()){ 
//						%>selected=selected<%} %>><%=productLine.getName() %></option>
			for(int i=0;i<productLineList.size();i++){
				voProductLine bean = (voProductLine) productLineList.get(i);
				map = new HashMap<String, String>();
				map.put("id", bean.getId()+"");
				map.put("text", bean.getName());
				list.add(map);
			}
			return list;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return null;
	}
	/**
	 * 批量开通货位列表
	 * 2013-8-19
	 * 朱爱林	
	 */
	@RequestMapping("/openCargoList")
	@ResponseBody
	public Object openCargoList(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		Map<String,Object> resultMap = new HashMap<String, Object>();
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		resultMap.put("total","0");
		resultMap.put("rows",list);
		try{
			String wholeCode=StringUtil.convertNull(request.getParameter("wholeCode")).trim();//货位号
			String storageId=StringUtil.convertNull(request.getParameter("storageId"));//仓库Id
			String stockAreaId=StringUtil.convertNull(request.getParameter("stockAreaId"));//仓库区域Id
			String passageId=StringUtil.convertNull(request.getParameter("passageId"));//巷道Id
			String shelfCode=StringUtil.convertNull(request.getParameter("shelfCode")).trim();//货架代号
			String floorNum=StringUtil.convertNull(request.getParameter("floorNum")).trim();//第几层
			String stockType=StringUtil.convertNull(request.getParameter("stockType"));//库存类型
			String storeType=StringUtil.convertNull(request.getParameter("storeType"));//存放类型
			String productLineId=StringUtil.convertNull(request.getParameter("productLineId"));//货位产品线
			String type=StringUtil.convertNull(request.getParameter("type"));//货位类型
			
			StringBuilder buf=new StringBuilder("status=2");//查询条件
//			StringBuilder para=new StringBuilder();//分页条件
			boolean isQuery=false;
			if(!wholeCode.equals("")){
				buf.append(" and whole_code like'");
				buf.append(wholeCode);
				buf.append("%'");
//				para.append("&wholeCode=");
//				para.append(wholeCode);
				isQuery=true;
			}
			if(!storageId.equals("")){
				buf.append(" and storage_id=");
				buf.append(storageId);
//				para.append("&storageId=");
//				para.append(storageId);
				isQuery=true;
			}
			if(!stockAreaId.equals("")){
				buf.append(" and stock_area_id=");
				buf.append(stockAreaId);
//				para.append("&stockAreaId=");
//				para.append(stockAreaId);
				isQuery=true;
			}
			if(!passageId.equals("")){
				buf.append(" and passage_id=");
				buf.append(passageId);
//				para.append("&passageId=");
//				para.append(passageId);
				isQuery=true;
			}
			if(!shelfCode.equals("")){
//				CargoInfoShelfBean cargoInfoShelfBean = service.getCargoInfoShelf(" whole_code = '"+shelfCode+"'");
//				buf.append(" and shelf_id=");
//				buf.append(cargoInfoShelfBean.getId());
//				ci.whole_code regexp '"+"[A-Z0-9\\-]{7}"+shelfCode+"[0-9]{3}"
				buf.append(" and whole_code regexp");
				buf.append(" '[A-Z0-9\\-]{7}");
				buf.append(shelfCode);
				buf.append("[0-9]{3}'");
				isQuery=true;
			}
			if(!floorNum.equals("")){
				buf.append(" and floor_num=");
				buf.append(floorNum);
//				para.append("&floorNum=");
//				para.append(floorNum);
				isQuery=true;
			}
			if(!stockType.equals("")){
				buf.append(" and stock_type=");
				buf.append(stockType);
//				para.append("&stockType=");
//				para.append(stockType);
				isQuery=true;
			}
			if(!storeType.equals("")){
				buf.append(" and store_type=");
				buf.append(storeType);
//				para.append("&storeType=");
//				para.append(storeType);
				isQuery=true;
			}
			if(!productLineId.equals("")){
				buf.append(" and product_line_id=");
				buf.append(productLineId);
//				para.append("&productLineId=");
//				para.append(productLineId);
				isQuery=true;
			}
			if(!type.equals("")){
				buf.append(" and type=");
				buf.append(type);
//				para.append("&type=");
//				para.append(type);
				isQuery=true;
			}
//			Map<String,Object> resultMap = new HashMap<String, Object>();
//			List<Map<String,String>> list = new ArrayList<Map<String,String>>();
//			Map<String,String> map = null;
			
			String page1 = StringUtil.convertNull(request.getParameter("page"));
			String rows1 = StringUtil.convertNull(request.getParameter("rows"));
			int page = page1.equals("")?1:Integer.parseInt(page1);
			int rows = rows1.equals("")?20:Integer.parseInt(rows1);
			int start = (page-1)*rows;//开始下标
//			
//			int countPerPage=20;
//			int pageIndex=0;
//			if(request.getParameter("pageIndex")!=null){
//				pageIndex=Integer.parseInt(request.getParameter("pageIndex"));
//			}
//			PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);
//			paging.setCurrentPageIndex(pageIndex);
//            paging.setPrefixUrl("cargoInfo.do?method=openCargoList"+para.toString());
            int total=0;
            if(isQuery){
            	total=service.getCargoInfoCount(buf.toString());
            }
            resultMap.put("total", total+"");
//            paging.setTotalCount(total);
//            paging.setTotalPageCount(total%countPerPage==0?total/countPerPage:total/countPerPage+1);
            List cargoInfoList=new ArrayList();
            if(isQuery){
            	cargoInfoList=service.getCargoInfoList(buf.toString(), start, rows, "whole_code asc");
//    			if(cargoInfoList!=null&&cargoInfoList.size()==0&&paging.getCurrentPageIndex()!=0){
//    				paging.setCurrentPageIndex(paging.getCurrentPageIndex()-1);
//    				cargoInfoList=service.getCargoInfoList(buf.toString(), paging.getCountPerPage()*paging.getCurrentPageIndex(), paging.getCountPerPage(), "whole_code asc");
//    			}
            }
			List productLineNameList=new ArrayList();
			if(cargoInfoList!=null){
				for(int i=0;i<cargoInfoList.size();i++){
					map = new HashMap<String, String>();
					CargoInfoBean ciBean=(CargoInfoBean)cargoInfoList.get(i);
					String productLineName="";
					map.put("cargo_info_cargoId", ciBean.getId()+"");//货位id
					map.put("cargo_info_wholecode", ciBean.getWholeCode());//货位号
					map.put("cargo_info_warnstock", ciBean.getWarnStockCount()+"");//货位警戒线
					map.put("cargo_info_maxstock", ciBean.getMaxStockCount()+"");//货位最大容量
					map.put("cargo_info_storetype", ciBean.getStoreTypeName());//存放类型
					map.put("cargo_info_stocktype", ciBean.getStockTypeName());//库存类型 	
					map.put("cargo_info_type", ciBean.getTypeName());//货位类型 	
					map.put("cargo_info_remark", ciBean.getRemark().length()>3?ciBean.getRemark().substring(0,3)+"...":ciBean.getRemark());//备注
					
					if(ciBean.getProductLineId()>0){
						voProductLine productLine=wareService.getProductLine("product_line.id="+ciBean.getProductLineId());
						if(productLine!=null){
							productLineName=productLine.getName();
							map.put("cargo_info_productline", productLineName);//货位产品线
						}
					}
//					productLineNameList.add(productLineName);
					list.add(map);
				}
			}
			resultMap.put("rows", list);
//			List productLineList=wareService.getProductLineList("1=1");
//			List wholeStorageList=service.getCargoInfoStorageList("1=1", -1, -1, "whole_code asc");
//			request.setAttribute("wholeStorageList", wholeStorageList);
//			request.setAttribute("productLineList", productLineList);
//			request.setAttribute("productLineNameList", productLineNameList);
//			request.setAttribute("list", cargoInfoList);
//			request.setAttribute("paging", paging);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return resultMap;
	}
	/**
	 * 批量开通货位
	 * 2013-8-19
	 * 朱爱林
	 */
	@RequestMapping("/openCargo")
	public void openCargo(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		synchronized(cargoLock){
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			service.getDbOp().startTransaction();
			String cargoIds = StringUtil.convertNull(request.getParameter("cargoId"));
			String[] cargoIdList=cargoIds.split(",");
			StringBuilder sbd1=new StringBuilder();//记录已删除的货位号
			StringBuilder sbd2=new StringBuilder();//记录已开通的货位号
			
			for(int i=0;i<cargoIdList.length;i++){
				String cargoId=cargoIdList[i];
				CargoInfoBean ciBean=service.getCargoInfo("id="+cargoId);
				if(ciBean.getStatus()==3){
					if(sbd1.length()>0){
						sbd1.append("、");
					}
					sbd1.append(ciBean.getWholeCode());
					continue;
				}
				if(ciBean.getStatus()==0||ciBean.getStatus()==1){
					if(sbd2.length()>0){
						sbd2.append("、");
					}
					sbd2.append(ciBean.getWholeCode());
					continue;
				}
				service.updateCargoInfo("status=1", "id="+ciBean.getId());
				CargoInfoLogBean logBean=new CargoInfoLogBean();
				logBean.setCargoId(ciBean.getId());
				logBean.setOperDatetime(DateUtil.getNow());
				logBean.setOperAdminId(user.getId());
				logBean.setOperAdminName(user.getUsername());
				logBean.setRemark("开通货位");
				service.addCargoInfoLog(logBean);
			}
			StringBuilder tip=new StringBuilder();
			if(sbd1.length()>0){
				tip.append("货位号"+sbd1.toString()+"不存在！无法开通！");
			}
			if(tip.length()>0){
				tip.append("\\r");
			}
			if(sbd2.length()>0){
				tip.append("货位号"+sbd2.toString()+"已开通！不需要再开通！");
			}
			service.getDbOp().commitTransaction();
			if(tip.length()>0){
//				request.setAttribute("tip", tip.toString());
				result.append("{result:'failure',tip:'"+tip+"'}");
				response.getWriter().write(result.toString());
				return;
			}else{
				result.append("{result:'success',tip:'批量开通货位成功！'}");
				response.getWriter().write(result.toString());
				return;
			}
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
//		return mapping.findForward("toOpenCargoList");
	}
	/**
	 * 批量删除货位
	 * 2013-8-19
	 * 朱爱林
	 */
	@RequestMapping("/deleteCargo")
	public void deleteCargo(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		synchronized(cargoLock){
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			service.getDbOp().startTransaction();
			String cargoIds = StringUtil.convertNull(request.getParameter("cargoId"));
			String[] cargoIdList=cargoIds.split(",");
			StringBuilder sbd1=new StringBuilder();//记录已删除的货位号
			StringBuilder sbd2=new StringBuilder();//记录已开通的货位号
			
			for(int i=0;i<cargoIdList.length;i++){
				String cargoId=cargoIdList[i];
				CargoInfoBean ciBean=service.getCargoInfo("id="+cargoId);
				if(ciBean.getStatus()==3){
					if(sbd1.length()>0){
						sbd1.append("、");
					}
					sbd1.append(ciBean.getWholeCode());
					continue;
				}
				if(ciBean.getStatus()==0||ciBean.getStatus()==1){
					if(sbd2.length()>0){
						sbd2.append("、");
					}
					sbd2.append(ciBean.getWholeCode());
					continue;
				}
				service.updateCargoInfo("status=3", "id="+ciBean.getId());
				CargoInfoLogBean logBean=new CargoInfoLogBean();
				logBean.setCargoId(Integer.parseInt(cargoId));
				logBean.setOperDatetime(DateUtil.getNow());
				logBean.setOperAdminId(user.getId());
				logBean.setOperAdminName(user.getUsername());
				logBean.setRemark("删除货位");
				service.addCargoInfoLog(logBean);
			}
			StringBuilder tip=new StringBuilder();
			if(sbd1.length()>0){
				tip.append("货位号"+sbd1.toString()+"已被删除，操作失败！");
			}
			if(tip.length()>0){
				tip.append("\\r");
			}
			if(sbd2.length()>0){
				tip.append("货位"+sbd2.toString()+"已开通，不能删除！");
			}
			service.getDbOp().commitTransaction();
			if(tip.length()>0){
//				request.setAttribute("tip", tip.toString());
				result.append("{result:'failure',tip:'"+tip+"'}");
				response.getWriter().write(result.toString());
				return;
			}else{
				result.append("{result:'success',tip:'批量货位删除成功！'}");
				response.getWriter().write(result.toString());
				return;
			}
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
	}

	/**
	 * 货位绑定产品中的核实
	 * 2013-8-20
	 * 朱爱林
	 */
	@RequestMapping("/checkCargoProduct")
	public String checkCargoProduct(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
//			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
//			response.getWriter().write(result.toString());
//			return;
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String productCode = null;
		String cargoCode = null;
		try{
			productCode=StringUtil.convertNull(request.getParameter("productCode")).trim();//产品编号
			cargoCode=StringUtil.convertNull(request.getParameter("cargoCode")).trim();//货位号
			if(productCode.equals("")){
				request.setAttribute("tip", "请输入产品编号！");
				request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/cargoProduct.jsp?productCode="+productCode+"&cargoCode="+cargoCode);
				return "/admin/tip";
//				return "./admin/rec/sys/checkCargoProduct.jsp";
			}
			if(cargoCode.equals("")){
				request.setAttribute("tip", "请输入货位号！");
				request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/cargoProduct.jsp?productCode="+productCode+"&cargoCode="+cargoCode);
				return "/admin/tip";
			}
			voProduct product=wareService.getProduct(productCode);//产品
			if(product==null){
				request.setAttribute("tip", "请输入有效的产品编号！");
				request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/cargoProduct.jsp?productCode="+productCode+"&cargoCode="+cargoCode);
				return "/admin/tip";
//				result.append("{result:'failure',tip:'请输入有效的产品编号！'}");
//				response.getWriter().write(result.toString());
//				return;
			}
			//设置产品的产品线名称
			if(wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId1())!=null){
				String productLineName=wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId1()).getName();
				product.setProductLineName(productLineName);
			}else if(wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId2())!=null){
				String productLineName=wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId2()).getName();
				product.setProductLineName(productLineName);
			}else if(wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId3())!=null){
				String productLineName=wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId3()).getName();
				product.setProductLineName(productLineName);
			}else{
				product.setProductLineName("");
			}
			CargoInfoBean ciBean=service.getCargoInfo("whole_code='"+cargoCode+"'");//货位
			if(ciBean==null){
				request.setAttribute("tip", "请输入有效的货位号！");
				request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/cargoProduct.jsp?productCode="+productCode+"&cargoCode="+cargoCode);
				return "/admin/tip";
//				result.append("{result:'failure',tip:'请输入有效的货位号！'}");
//				response.getWriter().write(result.toString());
//				return "/admin/error";
			}
			if(ciBean.getStatus()==3){
				request.setAttribute("tip", "货位号已被删除，请重新填写！");
				request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/cargoProduct.jsp?productCode="+productCode+"&cargoCode="+cargoCode);
				return "/admin/tip";
			}
			if(ciBean.getStatus()==2){
				request.setAttribute("tip", "该货位未开通，请重新填写货位！");
				request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/cargoProduct.jsp?productCode="+productCode+"&cargoCode="+cargoCode);
				return "/admin/tip";
			}
			if(service.getCargoProductStock("cargo_id="+ciBean.getId()+" and product_id="+product.getId())!=null){
				request.setAttribute("tip", "该货位已绑定该产品，请重新确认货位！");
				request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/cargoProduct.jsp?productCode="+productCode+"&cargoCode="+cargoCode);
				return "/admin/tip";
			}
			if(ciBean.getStoreType()==0){
				CargoProductStockBean cps=service.getCargoProductStock("cargo_id="+ciBean.getId());
				if(cps!=null&&cps.getProductId()!=product.getId()){
					request.setAttribute("tip", "该货位已使用中，不能继续绑定其他产品，请重新确认货位！");
					request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/cargoProduct.jsp?productCode="+productCode+"&cargoCode="+cargoCode);
					return "/admin/tip";
				}
			}
			List cpsList=service.getCargoProductStockList("product_id="+product.getId(), -1, -1, "id asc");
			List cargoList=new ArrayList();//该商品绑定的货位列表
			List stockCountList=new ArrayList();//货位库存列表
			for(int i=0;i<cpsList.size();i++){
				CargoProductStockBean cps=(CargoProductStockBean)cpsList.get(i);
				int cargoId=cps.getCargoId();
				CargoInfoBean ci=service.getCargoInfo("id="+cargoId);
				if(ci!=null){
					cargoList.add(ci);
					int stockCount=cps.getStockCount()+cps.getStockLockCount();
					stockCountList.add(""+stockCount);
				}
			}
			StringBuilder address=new StringBuilder();
			
			String cityName=service.getCargoInfoCity("id="+ciBean.getCityId()).getName();
			address.append(cityName);
			String areaName=service.getCargoInfoArea("id="+ciBean.getAreaId()).getName();
			address.append(areaName);
			String storageName=service.getCargoInfoStorage("id="+ciBean.getStorageId()).getName();
			address.append(storageName);
			String stockAreaCode=service.getCargoInfoStockArea("id="+ciBean.getStockAreaId()).getCode();
			address.append(stockAreaCode);
			address.append("区");
			int passageCode=Integer.parseInt(service.getCargoInfoPassage("id="+ciBean.getPassageId()).getCode());
			if(passageCode!=0){
				address.append(passageCode);
				address.append("巷道第");
			}
			int shelfCode=Integer.parseInt(service.getCargoInfoShelf("id="+ciBean.getShelfId()).getCode());
			address.append(shelfCode);
			address.append("排货架第");
			int floorNum=service.getCargoInfo("id="+ciBean.getId()).getFloorNum();
			address.append(floorNum);
			address.append("层第");
			int cargoNum=Integer.parseInt(service.getCargoInfo("id="+ciBean.getId()).getCode());
			address.append(""+cargoNum);
			address.append("货位");
			String productLineName="";
			if(wareService.getProductLine("product_line.id="+ciBean.getProductLineId())!=null){
				productLineName=wareService.getProductLine("product_line.id="+ciBean.getProductLineId()).getName();
			}
			request.setAttribute("productLineName", productLineName);
			request.setAttribute("address", address.toString());
			request.setAttribute("cargoInfoBean", ciBean);
			request.setAttribute("product", product);
			request.setAttribute("cargoList", cargoList);
			request.setAttribute("stockCountList", stockCountList);
			return "forward:/admin/rec/sys/checkCargoProduct.jsp";
		}catch(Exception e){
			e.printStackTrace();
			request.setAttribute("tip", "后台异常！");
			request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/cargoProduct.jsp?productCode="+productCode+"&cargoCode="+cargoCode);
			return "/admin/tip";
		}finally{
			service.releaseAll();
		}
	}
	/**
	 * 货位绑定产品中的核实确认提交
	 * 2013-8-20
	 * 朱爱林
	 */
	@RequestMapping("/cargoProduct")
	public String cargoProduct(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		synchronized(CargoOperationAction.cargoAssignLock){
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			service.getDbOp().startTransaction();
			String productCode=StringUtil.convertNull(request.getParameter("productCode")).trim();//产品编号
			String cargoCode=StringUtil.convertNull(request.getParameter("cargoCode")).trim();//货位号
			voProduct product=wareService.getProduct(productCode);//产品
			if(product==null){
				request.setAttribute("tip", "请输入有效的产品编号！");
				request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/cargoProduct.jsp?productCode="+productCode+"&cargoCode="+cargoCode);
				return "/admin/tip";
			}
			CargoInfoBean ciBean=service.getCargoInfo("whole_code='"+cargoCode+"'");//货位
			if(ciBean==null){
				request.setAttribute("tip", "请输入有效的货位号！");
				request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/cargoProduct.jsp?productCode="+productCode+"&cargoCode="+cargoCode);
				return "/admin/tip";
			}
			if(ciBean.getStatus()==3){
				request.setAttribute("tip", "货位号已被删除，请重新填写！");
				request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/cargoProduct.jsp?productCode="+productCode+"&cargoCode="+cargoCode);
				return "/admin/tip";
			}
			if(ciBean.getStatus()==2){
				request.setAttribute("tip", "该货位未开通，请重新填写货位！");
				request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/cargoProduct.jsp?productCode="+productCode+"&cargoCode="+cargoCode);
				return "/admin/tip";
			}
			if(service.getCargoProductStock("cargo_id="+ciBean.getId()+" and product_id="+product.getId())!=null){
				request.setAttribute("tip", "该货位已绑定该产品，请重新确认货位！");
				request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/cargoProduct.jsp?productCode="+productCode+"&cargoCode="+cargoCode);
				return "/admin/tip";
			}
			if(ciBean.getStoreType()==0){
				CargoProductStockBean cps=service.getCargoProductStock("cargo_id="+ciBean.getId());
				if(cps!=null&&cps.getProductId()!=product.getId()){
					request.setAttribute("tip", "该货位已使用中，不能继续绑定其他产品，请重新确认货位！");
					request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/cargoProduct.jsp?productCode="+productCode+"&cargoCode="+cargoCode);
					return "/admin/tip";
				}
			}
			CargoProductStockBean cps=new CargoProductStockBean();
			cps.setCargoId(ciBean.getId());
			cps.setProductId(product.getId());
			cps.setStockCount(0);
			cps.setStockLockCount(0);
			service.addCargoProductStock(cps);
			service.updateCargoInfo("status=0", "id="+ciBean.getId());
			CargoInfoLogBean logBean=new CargoInfoLogBean();//操作记录
			logBean.setCargoId(ciBean.getId());
			logBean.setOperDatetime(DateUtil.getNow());
			logBean.setOperAdminId(user.getId());
			logBean.setOperAdminName(user.getUsername());
			logBean.setRemark("货位绑定商品：商品"+product.getCode());
			service.addCargoInfoLog(logBean);
			service.getDbOp().commitTransaction();
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		request.setAttribute("tip", "操作成功");
		request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/cargoProduct.jsp");
		}
		return "/admin/tip";
	}
	/**
	 * 货位绑定产品--我要核实--取消返回
	 * 2013-8-20
	 * 朱爱林
	 */
	@RequestMapping("/cargoProductJsp")
	public String cargoProductJsp(HttpServletRequest request,HttpServletResponse response,
			@RequestParam String productCode,
			@RequestParam String cargoCode){
		return "forward:/admin/rec/sys/cargoProduct.jsp?productCode="+productCode+"&cargoCode="+cargoCode;
	}
	@RequestMapping("/cargoCodePrintJsp")
	public String cargoCodePrintJsp(HttpServletRequest request,HttpServletResponse response){
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		List wholeCityList=service.getCargoInfoCityList(null, -1, -1, "id desc");//所有城市列表
		request.setAttribute("wholeCityList", wholeCityList);
		return "forward:/admin/rec/sys/cargoCodePrint.jsp";
	}
	/**
	 * 货位条码打印
	 * 2013-8-20
	 * 朱爱林	
	 * @return
	 */
	@RequestMapping("/cargoCodePrint")
	public String cargoCodePrint(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
//		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			List wholeCityList=service.getCargoInfoCityList(null, -1, -1, "id desc");//所有城市列表
			List wholeStorageList=service.getCargoInfoStorageList(null, -1, -1, "id desc");//所有地区列表
			request.setAttribute("wholeStorageList", wholeStorageList);
			request.setAttribute("wholeCityList", wholeCityList);
			String cityId=StringUtil.convertNull(request.getParameter("cityId"));
			String areaId=StringUtil.convertNull(request.getParameter("areaId"));
			String storageId=StringUtil.convertNull(request.getParameter("storageId"));
			String stockAreaId=StringUtil.convertNull(request.getParameter("stockAreaId"));
			String shelfId=StringUtil.convertNull(request.getParameter("shelfId"));
			String passageId=StringUtil.convertNull(request.getParameter("passageId")).trim();
			String cargoCode=StringUtil.convertNull(request.getParameter("cargoCode")).trim();
			if(request.getParameter("storageId")!=null&&request.getParameter("stockAreaId")!=null&&request.getParameter("cityId")==null){
				CargoInfoStockAreaBean bean=service.getCargoInfoStockArea("id="+stockAreaId+" and storage_id="+storageId);
				request.setAttribute("cityId", bean.getCityId()+"");
				request.setAttribute("areaId", bean.getAreaId()+"");
			}
			if (request.getMethod().equalsIgnoreCase("get")) {
				return "forward:/admin/rec/sys/cargoCodePrint.jsp";
			}
			List cargoList=new ArrayList();//货架列表
			String condition="1=1";//查询条件
			if(!cityId.equals("")){
				condition+=" and city_id=";
				condition+=cityId;
			}
			if(!areaId.equals("")){
				condition+=" and area_id=";
				condition+=areaId;
			}
			if(!storageId.equals("")){
				condition+=" and storage_id=";
				condition+=storageId;
			}
			if(!stockAreaId.equals("")){
				condition+=" and stock_area_id=";
				condition+=stockAreaId;
			}
			if(!passageId.equals("")){
				condition+=" and passage_id="+passageId;
			}
			if(!shelfId.equals("")){
				condition+=" and shelf_id="+shelfId;
			}
			if(!cargoCode.equals("") && !cargoCode.equals("货位编号")){
				condition+=" and whole_code='";
				condition+=cargoCode;
				condition+="'";
			}
			if(condition.toString().equals("1=1")){
				request.setAttribute("tip", "请输入查询条件");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			cargoList=service.getCargoInfoList(condition,-1,-1,"whole_code asc");
			if(cargoList==null||cargoList.size()==0){
				request.setAttribute("tip", "没有找到对应的条码信息");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			request.setAttribute("cargoList", cargoList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return "forward:/admin/rec/sys/cargoCodePrint.jsp";
	}
	//AJAX
	@RequestMapping("/selection")
	public String selection(HttpServletRequest request, HttpServletResponse response){
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
//		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			if(request.getParameter("cityId")!=null){
				String cityId=request.getParameter("cityId");
				List cargoInfoAreaList=null;
				if(cityId.equals("")){
					cargoInfoAreaList=service.getCargoInfoAreaList("1=2", -1, -1, "id desc");
				}else{
					cargoInfoAreaList=service.getCargoInfoAreaList("city_id="+cityId, -1, -1, "id desc");
				}
				request.setAttribute("cargoInfoAreaList", cargoInfoAreaList);
			}else if(request.getParameter("areaId")!=null){
				String areaId=request.getParameter("areaId");
				List cargoInfoStorageList=null;
				if(areaId.equals("")){
					cargoInfoStorageList=service.getCargoInfoStorageList("1=2", -1, -1, "id desc");
				}else{
					cargoInfoStorageList=service.getCargoInfoStorageList("area_id="+areaId, -1, -1, "id desc");
				}
				request.setAttribute("cargoInfoStorageList", cargoInfoStorageList);
			}else if(request.getParameter("storageId")!=null){
				String storageId=request.getParameter("storageId");
				List cargoInfoStockAreaList=null;
				if(storageId.equals("")){
					cargoInfoStockAreaList=service.getCargoInfoStockAreaList("1=2", -1, -1, "id desc");
				}else{
					if(request.getParameter("stockType")==null){
						cargoInfoStockAreaList=service.getCargoInfoStockAreaList("storage_id="+storageId, -1, -1, "id desc");
					}else{
						cargoInfoStockAreaList=service.getCargoInfoStockAreaList("storage_id="+storageId+" and stock_type="+request.getParameter("stockType"), -1, -1, "id desc");
					}
				}
				if(request.getParameter("add")!=null&&request.getParameter("add").equals("1")){
					request.setAttribute("cargoInfoStockAreaList2", cargoInfoStockAreaList);
				}else{
					request.setAttribute("cargoInfoStockAreaList", cargoInfoStockAreaList);
				}
			}else if(request.getParameter("stockAreaId")!=null){
				String stockAreaId=request.getParameter("stockAreaId");
				List cargoInfoPassageList=null;
				if(stockAreaId.equals("")){
					cargoInfoPassageList=new ArrayList();
				}else{
					cargoInfoPassageList=service.getCargoInfoPassageList("stock_area_id="+stockAreaId, -1, -1, "id desc");
				}
				request.setAttribute("cargoInfoPassageList", cargoInfoPassageList);
			}else if(request.getParameter("passageId")!=null){
				String passageId=request.getParameter("passageId");
				List cargoInfoShelfList=null;
				if(passageId.equals("")){
					cargoInfoShelfList=new ArrayList();
				}else{
					cargoInfoShelfList=service.getCargoInfoShelfList("passage_id="+passageId, -1, -1, "id desc");
				}
				request.setAttribute("cargoInfoShelfList", cargoInfoShelfList);
				
			}else if(request.getParameter("shelfId")!=null){
				String shelfId=request.getParameter("shelfId");
				int floorCount=0;
				if(!shelfId.equals("")){
					floorCount=service.getCargoInfoShelf("id="+shelfId).getFloorCount();
				}
				request.setAttribute("floorCount", String.valueOf(floorCount));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return "forward:/admin/rec/sys/ajaxSelect.jsp";
	}
	/**
	 * 增城货位列表
	 * 2013-8-21
	 * 朱爱林	
	 */
	@RequestMapping("/zcCargoList")
	@ResponseBody
	public Object zcCargoList(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		CartonningInfoService cartonningService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ResultSet rs = null;
		ResultSet rs2 = null;
		Map<String,Object> resultMap = new HashMap<String, Object>();
		List<Map<String,String>> lists = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		resultMap.put("total","0");
		resultMap.put("rows",lists);
		try{
			String wholeCode=StringUtil.convertNull(request.getParameter("wholeCode")).trim();//货位号
			String productCode=StringUtil.convertNull(request.getParameter("productCode")).trim();//产品编号
			String stockCountStart=StringUtil.convertNull(request.getParameter("stockCountStart")).trim();//库存最小值
			String stockCountEnd=StringUtil.convertNull(request.getParameter("stockCountEnd")).trim();//库存最大值
//			String[] status=request.getParameterValues("status");
			String subStatus = StringUtil.convertNull(request.getParameter("status"));
			String[] status=null;
			if(!"".equals(subStatus)){
				status = subStatus.split(",");
			}
			String shelfId=StringUtil.convertNull(request.getParameter("shelfId"));//货架Id
			String passageId=StringUtil.convertNull(request.getParameter("passageId"));//巷道Id
			String storeType=StringUtil.convertNull(request.getParameter("storeType"));//存放类型
			String productLineId=StringUtil.convertNull(request.getParameter("productLineId"));//产品线Id
			String productName=StringUtil.convertNull(request.getParameter("productName")).trim();//产品原名称
			String paraProductName="";
			String dbProductName="";
			if(Encoder.decrypt(productName)==null){//第一次查询，未编码
				paraProductName=Encoder.encrypt(productName);
				dbProductName=productName;
			}else{//后面的查询，已编码
				paraProductName=productName;
				dbProductName=Encoder.decrypt(productName);
			}
			String type=StringUtil.convertNull(request.getParameter("type")).trim();//货位类型
			String storageId=StringUtil.convertNull(request.getParameter("storageId"));//仓库号
			String stockAreaId=StringUtil.convertNull(request.getParameter("stockAreaId"));//仓库区域
			String shelfCode=StringUtil.convertNull(request.getParameter("shelfCode")).trim();//货架代号
			String floorNum=StringUtil.convertNull(request.getParameter("floorNum")).trim();//第几层
			String areaId=StringUtil.convertNull(request.getParameter("areaId"));
			String cartonningCode = StringUtil.convertNull(request.getParameter("cartonningCode")).trim();//装箱单编号
			CartonningInfoBean ciBean1 = cartonningService.getCartonningInfo("code='"+cartonningCode+"'");
			String cargoCode = "";
			int product_id=0;
			if(ciBean1!=null){//装箱单编号所对应的货位号
				CargoInfoBean cargoInfoBean = service.getCargoInfo("id="+ciBean1.getCargoId());
				if(cargoInfoBean!=null){
					cargoCode = cargoInfoBean.getWholeCode();
					CartonningProductInfoBean bean = cartonningService.getCartonningProductInfo("cartonning_id="+ciBean1.getId());
				    if(bean!=null){
				    	 product_id = bean.getProductId();
				    }
				}
			}
			if(cartonningCode.equals("")&&cargoCode.equals("")){
				cargoCode="-1";
			}
//			if(!stockCountStart.equals("")&&!stockCountStart.matches("[0-9]*")){
//				request.setAttribute("tip", "货位当前库存，输入内容必须是数字，请重新输入！");
////				request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/zcCargoList.jsp");
//				request.setAttribute("result", "failure");
//				return "/admin/tip";
//			}
//			if(!stockCountEnd.equals("")&&!stockCountEnd.matches("[0-9]*")){
//				request.setAttribute("tip", "货位当前库存，输入内容必须是数字，请重新输入！");
//				request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/zcCargoList.jsp");
//				return "/admin/tip";
//			}
//			if(!shelfCode.equals("")&&!shelfCode.matches("[0-9]{2}")){
//				request.setAttribute("tip", "货架代号，输入内容必须是两位数字，请重新输入！");
//				request.setAttribute("result", "failure");
////				request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/zcCargoList.jsp");
//				return "/admin/tip";
//			}
//			if(!floorNum.equals("")&&!floorNum.matches("[0-9]*")){
//				request.setAttribute("tip", "货架层数，输入内容必须是数字，请重新输入！");
//				request.setAttribute("url", request.getContextPath()+"/admin/rec/sys/zcCargoList.jsp");
//				return "/admin/tip";
//			}
//			Map<String,Object> resultMap = new HashMap<String, Object>();
//			List<Map<String,String>> lists = new ArrayList<Map<String,String>>();
//			Map<String,String> map = null;
			
			String page1 = StringUtil.convertNull(request.getParameter("page"));
			String rows1 = StringUtil.convertNull(request.getParameter("rows"));
			int page = page1.equals("")?1:Integer.parseInt(page1);
			int rows = rows1.equals("")?20:Integer.parseInt(rows1);
			int start = (page-1)*rows;//开始下标
//			int countPerPage=20;
//			int pageIndex=0;
//			if(request.getParameter("pageIndex")!=null){
//				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
//			}
//            PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);
//            String para="";
//            para=(shelfId.equals("")?"":("&shelfId="+shelfId))
//            	+(areaId.equals("")?"":("&areaId="+areaId))
//    			+(wholeCode.equals("")?"":("&wholeCode="+wholeCode))
//    			+(productCode.equals("")?"":("&productCode="+productCode))
//    			+(stockCountStart.equals("")?"":("&stockCountStart="+stockCountStart))
//    			+(stockCountEnd.equals("")?"":("&stockCountEnd="+stockCountEnd))
//    			+(storeType.equals("")?"":("&storeType="+storeType))
//    			+(status!=null&&status.length>=1?("&status="+status[0]):"")
//    			+(status!=null&&status.length>=2?("&status="+status[1]):"")
//    			+(status!=null&&status.length>=3?("&status="+status[2]):"")
//    			+(productLineId.equals("")?"":("&productLineId="+productLineId))
//    			+(productName.equals("")?"":"&productName="+paraProductName)
//    			+(type.equals("")?"":("&type="+type))
//    			+(storageId.equals("")?"":("&storageId="+storageId))
//    			+(passageId.equals("")?"":("&passageId="+passageId))
//    			+(stockAreaId.equals("")?"":("&stockAreaId="+stockAreaId))
//    			+(shelfCode.equals("")?"":("&shelfCode="+shelfCode))
//    			+(floorNum.equals("")?"":("&floorNum="+floorNum))
//    			+(cartonningCode.equals("")?"":("&cartonningCode="+cartonningCode))
//    			;
//            paging.setCurrentPageIndex(pageIndex);
//            paging.setPrefixUrl("cargoInfo.do?method=zcCargoList"+para);
            
			rs = service.getDbOp().executeQuery("select * from cargo_product_stock cps join product p on cps.product_id = p.id right join cargo_info ci on ci.id = cps.cargo_id" 
					+(" where ci.store_type<>2 and ci.status<>3")
					+(!areaId.equals("")?" and ci.area_id = "+areaId:"")
					+(!shelfId.equals("")?" and ci.shelf_id = "+shelfId:"")
					+(!wholeCode.equals("")?" and ci.whole_code like '"+wholeCode+"%'":"")
					+(!productCode.equals("")?" and p.code = '"+productCode+"'":"")
					+(!stockCountStart.equals("")?" and cps.stock_count+cps.stock_lock_count>="+stockCountStart:"")
					+(!stockCountEnd.equals("")?" and cps.stock_count+cps.stock_lock_count<="+stockCountEnd:"")
					+(!storeType.equals("")?" and ci.store_type = "+storeType:"")
					+(status!=null&&status.length>=1?" and (ci.status="+status[0]:"")
					+(status!=null&&status.length>=2?" or ci.status="+status[1]:"")
					+(status!=null&&status.length>=3?" or ci.status="+status[2]:"")
					+(status!=null&&status.length>=1?")":"")
					+(!productLineId.equals("")?" and ci.product_line_id="+productLineId:"")
					+(!productName.equals("")?" and p.oriname='"+dbProductName+"'":"")
					+(!type.equals("")?" and ci.type="+type:"")
					+(!storageId.equals("")?" and ci.storage_id="+storageId:"")
					+(!passageId.equals("")?" and ci.passage_id="+passageId:"")
					+(!stockAreaId.equals("")?" and ci.stock_area_id="+stockAreaId:"")
					+(!shelfCode.equals("")?" and ci.whole_code regexp '"+"[A-Z0-9\\-]{7}"+shelfCode+"[0-9]{3}"+"'":"")
					+(!floorNum.equals("")?" and ci.floor_num="+floorNum:"")
						+(!cartonningCode.equals("")&&!cargoCode.equals("")?" and p.id ="+product_id+" and ci.whole_code='"+cargoCode+"'":!cartonningCode.equals("")?" and 1=2":"")
					+(" order by ci.whole_code asc")
					+(" limit "+start+","+rows)
					);
			List list = this.getCargoAndProductStockList(rs);
			rs2 = service.getDbOp().executeQuery("select count(ci.id) from cargo_product_stock cps join product p on cps.product_id = p.id right join cargo_info ci on ci.id = cps.cargo_id" 
					+(" where ci.store_type<>2 and ci.status<>3")
					+(!areaId.equals("")?" and ci.area_id = "+areaId:"")
					+(!shelfId.equals("")?" and ci.shelf_id = "+shelfId:"")
					+(!wholeCode.equals("")?" and ci.whole_code like '"+wholeCode+"%'":"")
					+(!productCode.equals("")?" and p.code = '"+productCode+"'":"")
					+(!stockCountStart.equals("")?" and cps.stock_count+cps.stock_lock_count>="+stockCountStart:"")
					+(!stockCountEnd.equals("")?" and cps.stock_count+cps.stock_lock_count<="+stockCountEnd:"")
					+(!storeType.equals("")?" and ci.store_type = "+storeType:"")
					+(status!=null&&status.length>=1?" and (ci.status="+status[0]:"")
					+(status!=null&&status.length>=2?" or ci.status="+status[1]:"")
					+(status!=null&&status.length>=3?" or ci.status="+status[2]:"")
					+(status!=null&&status.length>=1?")":"")
					+(!productLineId.equals("")?" and ci.product_line_id="+productLineId:"")
					+(!productName.equals("")?" and p.oriname='"+dbProductName+"'":"")
					+(!type.equals("")?" and ci.type="+type:"")
					+(!passageId.equals("")?" and ci.passage_id="+passageId:"")
					+(!storageId.equals("")?" and ci.storage_id="+storageId:"")
					+(!stockAreaId.equals("")?" and ci.stock_area_id="+stockAreaId:"")
					+(!shelfCode.equals("")?" and ci.whole_code regexp '"+"[A-Z0-9\\-]{7}"+shelfCode+"[0-9]{3}"+"'":"")
					+(!floorNum.equals("")?" and ci.floor_num="+floorNum:"")
				  	+(!cartonningCode.equals("")&&!cargoCode.equals("")?" and p.id ="+product_id+" and ci.whole_code='"+cargoCode+"'":!cartonningCode.equals("")?" and 1=2":"")
					+(" order by ci.whole_code asc")
					);
			
			int totalCount = 0; //根据条件得到 总数量
			if(rs2.next()){
				totalCount=rs2.getInt("count(ci.id)");
			}
//			paging.setTotalCount(totalCount);
//			paging.setTotalPageCount(totalCount%countPerPage==0?totalCount/countPerPage:totalCount/countPerPage+1);
			resultMap.put("total", totalCount+"");
			Iterator iter = list.listIterator();
			List productLineNameList=new ArrayList();//产品线列表
			List operCountList=new ArrayList();//未完成作业单数
			while(iter.hasNext()){
				CargoProductStockBean cps = (CargoProductStockBean)iter.next();
				voProduct product = new voProduct();
				if(cps.getProductId()>0){
					product = wareService.getProduct(cps.getProductId());
				}
				cps.setProduct(product);
				String productLineName="";
				if(cps.getCargoInfo().getProductLineId()>0){
					if(wareService.getProductLine("product_line.id="+cps.getCargoInfo().getProductLineId())!=null){						
						productLineName=wareService.getProductLine("product_line.id="+cps.getCargoInfo().getProductLineId()).getName();
					}
				}
				productLineNameList.add(productLineName);
				int count=0;
				operCountList.add(""+count);
			}
			List productLineList=new ArrayList();
			productLineList=wareService.getProductLineList("1=1");
			List storageList=new ArrayList();
			storageList=service.getCargoInfoStorageList("area_id="+areaId, -1, -1, "whole_code asc");
			StringBuilder str = new StringBuilder();
			StringBuilder str2 = new StringBuilder();
			String root = request.getContextPath();
			for(int i=0;i<list.size();i++){
				CargoProductStockBean cps=(CargoProductStockBean)list.get(i);
				cps.setCartonningList(new ArrayList());
				List cartonningList=cartonningService.getCartonningList("cargo_id="+cps.getCargoId()+" and status!=2", -1, -1, null);
				for(int j=0;j<cartonningList.size();j++){
					CartonningInfoBean cartonningBean=(CartonningInfoBean)cartonningList.get(j);
					CartonningProductInfoBean cartonningProduct=cartonningService.getCartonningProductInfo("cartonning_id="+cartonningBean.getId());
					if(cartonningProduct!=null&&cartonningProduct.getProductId()==cps.getProductId()){
						str.append(cartonningBean.getCode()+"<br/>");//装箱单号
						str2.append(cartonningProduct.getProductCount()+"<br/>");
//						cartonningBean.setProductBean(cartonningProduct);
//						cps.getCartonningList().add(cartonningBean);
					}
				}
				/*
				 * <td><a href="../admin/cargoInfo.do?method=updateCargoPage&cargoProductStockId=<%=bean.getId() %>&cargoId=<%=bean.getCargoInfo().getId()%>" target="_blank"><%=bean.getCargoInfo().getWholeCode() %></a></td>
				<td><%=productLineNameList.get(i) %></td>
				<td><%if(bean.getProduct().getCode()!=null){ %><a href="../admin/fproduct.do?id=<%=bean.getProductId() %>" target="_blank"><%=bean.getProduct().getCode() %></a><%}else{ %>-<%} %></td>
				<td><%if(bean.getProduct().getOriname()!=null){ %><a href="../admin/fproduct.do?id=<%=bean.getProductId() %>" target="_blank"><%=bean.getProduct().getOriname() %></a><%}else{ %>-<%} %></td>
				 */
				map = new HashMap<String, String>();
				map.put("cargo_info_wholecode", "<a href=\""+root+"/admin/cargoInfo.do?method=updateCargoPage&cargoProductStockId="
												+cps.getId()+"&cargoId="+cps.getCargoInfo().getId()+"\" target=\"_balank\">"
												+cps.getCargoInfo().getWholeCode()+"</a>");//货位号
				map.put("product_line_name", productLineNameList.get(i)+"");//货位产品线
				map.put("product_code", cps.getProduct()!=null&&cps.getProduct().getId()!=0?"<a href=\""+root+"/admin/fproduct.do?id="+cps.getProductId()
										+"\" target=\"_blank\">"+cps.getProduct().getCode()+"</a>":"-");//产品编号
				map.put("product_oriname", cps.getProduct()!=null&&cps.getProduct().getId()!=0?"<a href=\""+root+"/admin/fproduct.do?id="+cps.getProductId()
										+"\" target=\"_blank\">"+cps.getProduct().getOriname()+"</a>":"-");//产品原名称
				map.put("cargo_product_stock_stockcount", (cps.getStockCount()+cps.getStockLockCount())+"("+cps.getStockLockCount()+")");//<font color="#FFFFFF">当前货位库存（其中冻结量）</font>
				map.put("cartonning_info_code", str.toString());//装箱单号
				map.put("cartonning_product_info_productcount", str2.toString());//装箱数量
				map.put("cargo_info_spacelockcount", cps.getCargoInfo().getSpaceLockCount()+"");//货位空间冻结
				map.put("cargo_info_warnstockcount", cps.getCargoInfo().getWarnStockCount()+"");//货位警戒线
				map.put("cargo_info_maxstockcount", cps.getCargoInfo().getMaxStockCount()+"");//货位最大容量
				map.put("cargo_info_storetypename", cps.getCargoInfo().getStoreTypeName());//存放类型
				map.put("cargo_info_typename", cps.getCargoInfo().getTypeName());//货位类型
				map.put("cargo_info_statusname", cps.getCargoInfo().getStatusName());//货位状态
				map.put("cargo_info_remark", cps.getCargoInfo().getRemark().length()>3?cps.getCargoInfo().getRemark().substring(0,3)+"...":cps.getCargoInfo().getRemark());//备注
				map.put("cargo_product_stock_op", operCountList.get(i)+"");//未完成作业单数
				/*
				 * control.append("<a href=\'").append(root).append("/CargoController/printCartonningInfo.mmx?id=")
							.append(bean.getId())
							.append("\' target=\"blank\">打印装箱单</a>&nbsp;")
							.append("<a onclick=\"cancel(\'").append(bean.getId()).append("\',\'").append(bean.getCode())
							.append("\');\">");
				 */
				if(cps.getCargoInfo().getStatus()==1&&cps.getCargoInfo().getSpaceLockCount()==0){
					map.put("control", "<a onclick=\"closeCargo(\'"+cps.getCargoInfo().getId()+"\');\">关闭货位</a>");
				}else if(cps.getCargoInfo().getStatus()==0&&cps.getStockCount()==0&&cps.getStockLockCount()==0
						&&cps.getCargoInfo().getSpaceLockCount()==0&&operCountList.get(i).toString().equals("0")){
					map.put("control", "<a onclick=\"clearCargo(\'"+cps.getCargoInfo().getId()+"\',\'"+cps.getProductId()+"\');\">清空货位</a>");
				}
				if(cps.getProductId()>0){
					map.put("query", "<a href=\""+root+"/admin/rec/sys/cargoStockCard.jsp?productCode="+cps.getProduct().getCode()
										+"&cargoWholeCode="+cps.getCargoInfo().getWholeCode()+"&zz=1\" target=\"_blank\">查</a>");
//					map.put("query", "<a href="../admin/cargoInfo.do?method=cargoStockCard&productCode=<%=bean.getProduct().getCode()%>&cargoWholeCode=<%=bean.getCargoInfo().getWholeCode() %>" target="_blank">查</a>");
				}else{
					map.put("query", "");
				}
				str.delete(0, str.length());
				str2.delete(0, str2.length());
				lists.add(map);
			}
//			request.setAttribute("operCountList", operCountList);
//			request.setAttribute("storageList", storageList);
//			request.setAttribute("productLineList", productLineList);
//			request.setAttribute("productLineNameList", productLineNameList);
//			request.setAttribute("list", list);
//			request.setAttribute("paging", paging);
			resultMap.put("rows", lists);
//			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return resultMap;
	}
	public List getCargoAndProductStockList(ResultSet rs){
		List list = new ArrayList();
		try {
			while(rs.next()){
				CargoInfoBean ci = new CargoInfoBean();
				ci.setId(rs.getInt("ci.id"));
				ci.setCode(rs.getString("ci.code"));
				ci.setWholeCode(rs.getString("ci.whole_code"));
				ci.setStoreType(rs.getInt("ci.store_type"));
				ci.setWarnStockCount(rs.getInt("ci.warn_stock_count"));
				ci.setMaxStockCount(rs.getInt("ci.max_stock_count"));
				ci.setSpaceLockCount(rs.getInt("ci.space_lock_count"));
				ci.setProductLineId(rs.getInt("ci.product_line_id"));
				ci.setType(rs.getInt("ci.type"));
				ci.setLength(rs.getInt("ci.length"));
				ci.setWidth(rs.getInt("ci.width"));
				ci.setHigh(rs.getInt("ci.high"));
				ci.setFloorNum(rs.getInt("ci.floor_num"));
				ci.setStatus(rs.getInt("ci.status"));
				ci.setStockType(rs.getInt("ci.stock_type"));
				ci.setShelfId(rs.getInt("ci.shelf_id"));
				ci.setStockAreaId(rs.getInt("ci.stock_area_id"));
				ci.setStorageId(rs.getInt("ci.storage_id"));
				ci.setAreaId(rs.getInt("ci.area_id"));
				ci.setCityId(rs.getInt("ci.city_id"));
				ci.setRemark(rs.getString("ci.remark"));
				if(rs.getInt("ci.product_line_id")>0){
					ci.setProductLineId(rs.getInt("ci.product_line_id"));
				}
				CargoProductStockBean cps = new CargoProductStockBean();
				if(rs.getInt("cps.id")>0){
					cps.setId(rs.getInt("cps.id"));
					cps.setCargoId(rs.getInt("cps.cargo_id"));
					cps.setProductId(rs.getInt("cps.product_id"));
					cps.setStockCount(rs.getInt("cps.stock_count"));
					cps.setStockLockCount(rs.getInt("cps.stock_lock_count"));
				}
				cps.setCargoInfo(ci);

				list.add(cps);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	/**
	 * 关闭货位
	 * 2013-8-27
	 * 朱爱林
	 */
	@RequestMapping("/closeCargo")
	public void closeCargo(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		CargoInfoBean ciBean=null;
		synchronized(cargoLock){
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			service.getDbOp().startTransaction();
			String cargoId=StringUtil.convertNull(request.getParameter("cargoId"));//货位Id
			ciBean=service.getCargoInfo("id="+cargoId);
			if(ciBean.getStatus()!=1){
//				request.setAttribute("tip", "该货位状态不是未使用！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'该货位状态不是未使用！'}");
				response.getWriter().write(result.toString());
				return;
			}
			if(ciBean.getSpaceLockCount()!=0){
//				request.setAttribute("tip", "该货位空间冻结量不为0！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'该货位空间冻结量不为0！'}");
				response.getWriter().write(result.toString());
				return;
			}
			service.updateCargoInfo("status=2", "id="+cargoId);
			CargoInfoLogBean logBean=new CargoInfoLogBean();//操作记录
			logBean.setCargoId(Integer.parseInt(cargoId));
			logBean.setOperDatetime(DateUtil.getNow());
			logBean.setOperAdminId(user.getId());
			logBean.setOperAdminName(user.getUsername());
			logBean.setRemark("关闭货位");
			service.addCargoInfoLog(logBean);
			request.setAttribute("add", "1");
			service.getDbOp().commitTransaction();
			result.append("{result:'success',tip:'关闭货位成功！'}");
			response.getWriter().write(result.toString());
			return;
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
	}
	/**
	 * 清空货位
	 * 2013-8-27
	 * 朱爱林
	 */
	@RequestMapping("/clearCargo")
	public void clearCargo(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		CargoInfoBean ciBean=null;
		synchronized(cargoLock){
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		DbOperation dbOpSlave = new DbOperation();
		dbOpSlave.init(DbOperation.DB_SLAVE2);
		try{
			service.getDbOp().startTransaction();
			String cargoId=StringUtil.convertNull(request.getParameter("cargoId"));//货位Id
			String productId=StringUtil.convertNull(request.getParameter("productId"));//商品Id
			voProduct product=wareService.getProduct(Integer.parseInt(productId));//商品
			ciBean=service.getCargoInfo("id="+cargoId);
			CargoProductStockBean cpsBean=service.getCargoProductStock("cargo_id="+cargoId+" and product_id="+productId);
			if(ciBean.getStatus()!=0){
//				request.setAttribute("tip", "该货位状态是"+ciBean.getStatusName()+"，不能清空货位！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'该货位状态是"+ciBean.getStatusName()+"，不能清空货位！'}");
				response.getWriter().write(result.toString());
				return;
			}
			if(ciBean.getSpaceLockCount()!=0){
//				request.setAttribute("tip", "货位空间冻结量大于0，不能清空货位！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'货位空间冻结量大于0，不能清空货位！'}");
				response.getWriter().write(result.toString());
				return;
			}
			if(cpsBean!=null&&cpsBean.getStockCount()+cpsBean.getStockLockCount()!=0){
//				request.setAttribute("tip", "当前货位库存大于0，不能清空货位！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'当前货位库存大于0，不能清空货位！'}");
				response.getWriter().write(result.toString());
				return;
			}
			
			
			
			int count=0;
			ResultSet rs3=dbOpSlave.executeQuery("select count(distinct co.id) from cargo_operation co join cargo_operation_cargo coc on coc.oper_id=co.id where co.status in(1,2,3,4,5,6,10,11,12,13,14,15,19,20,21,22,23,24,28,29,30,31,32,33,37,38,39,40,41,42) and co.effect_status < 3 and coc.product_id="+product.getId()+" and (coc.in_cargo_whole_code='"+ciBean.getWholeCode()+"' or coc.out_cargo_whole_code='"+ciBean.getWholeCode()+"')");
			while(rs3.next()){
				count=rs3.getInt(1);
			}
			rs3.close();
			if(count!=0){
//				request.setAttribute("tip", "存在与该货位相关的货位作业单，不能清空货位！");
//				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'存在与该货位相关的货位作业单，不能清空货位！'}");
				response.getWriter().write(result.toString());
				return;
			}
			service.deleteCargoProductStock("cargo_id="+cargoId+" and product_id="+productId);
			CargoInfoLogBean logBean=new CargoInfoLogBean();//操作记录
			logBean.setCargoId(Integer.parseInt(cargoId));
			logBean.setOperDatetime(DateUtil.getNow());
			logBean.setOperAdminId(user.getId());
			logBean.setOperAdminName(user.getUsername());
			String remark="";
			if(service.getCargoProductStockCount("cargo_id="+cargoId)==0){//刚刚删除了该货位的最后一个商品库存记录
				service.updateCargoInfo("status=1", "id="+cargoId);
				remark="清空货位：";
				remark+="商品";
				remark+=product==null?"null":product.getCode();
				remark+="，";
				remark+="货位状态（使用中-未使用）";
				logBean.setRemark(remark);
				service.addCargoInfoLog(logBean);
			}else{
				remark="清空货位：";
				remark+="商品";
				remark+=product==null?"null":product.getCode();
				logBean.setRemark(remark);
				service.addCargoInfoLog(logBean);
			}
			request.setAttribute("add", "1");
			service.getDbOp().commitTransaction();
			result.append("{result:'success',tip:'清空货位操作成功！'}");
			response.getWriter().write(result.toString());
			return;
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
			dbOpSlave.release();
		}
		}
	}
	/**
	 * 导出全部货位
	 * 2013-8-21
	 * Administrator	
	 */
	@RequestMapping("/cargoListPrint")
	public String cargoListPrint(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ResultSet rs = null;
		try{
			String wholeCode=StringUtil.convertNull(request.getParameter("wholeCode")).trim();//货位号
			String productCode=StringUtil.convertNull(request.getParameter("productCode")).trim();//产品编号
			int stockCountStart=StringUtil.toInt(request.getParameter("stockCountStart"));//库存最小值
			int stockCountEnd=StringUtil.toInt(request.getParameter("stockCountEnd"));//库存最大值
			String[] status=StringUtil.convertNull(request.getParameter("status")).split(",");
			String shelfId=StringUtil.convertNull(request.getParameter("shelfId"));//货架Id
			String areaId=StringUtil.convertNull(request.getParameter("areaId"));//地区Id
			String storeType=StringUtil.convertNull(request.getParameter("storeType"));//存放类型
			String productLineId=StringUtil.convertNull(request.getParameter("productLineId"));//产品线Id
			String productName=StringUtil.convertNull(request.getParameter("productName")).trim();//产品原名称
			String type=StringUtil.convertNull(request.getParameter("type")).trim();//货位类型
			String storageId=StringUtil.convertNull(request.getParameter("storageId"));//仓库号
			String stockAreaId=StringUtil.convertNull(request.getParameter("stockAreaId"));//仓库区域
			String shelfCode=StringUtil.convertNull(request.getParameter("shelfCode")).trim();//货架代号
			String floorNum=StringUtil.convertNull(request.getParameter("floorNum")).trim();//第几层
			rs = service.getDbOp().executeQuery("select * from cargo_product_stock cps join product p on cps.product_id = p.id right join cargo_info ci on ci.id = cps.cargo_id left join product_line pl on ci.product_line_id=pl.id" 
					+(shelfId.equals("")?(" where ci.area_id="+areaId):(" where 1=1"))
					+(!shelfId.equals("")?" and ci.shelf_id = "+shelfId:"")
					+(!areaId.equals("")?" and ci.area_id = "+areaId:"")
					+(!wholeCode.equals("")?" and ci.whole_code like '"+wholeCode+"%'":"")
					+(!productCode.equals("")?" and p.code = '"+productCode+"'":"")
					+(stockCountStart>=0?" and (cps.stock_count+cps.stock_lock_count)>="+stockCountStart:"")
					+(stockCountEnd>=0?" and (cps.stock_count+cps.stock_lock_count)<="+stockCountEnd:"")
					+(!storeType.equals("")?" and ci.store_type = "+storeType:"")
					+(status!=null&&status.length>=1?" and (ci.status="+status[0]:"")
					+(status!=null&&status.length>=2?" or ci.status="+status[1]:"")
					+(status!=null&&status.length>=3?" or ci.status="+status[2]:"")
					+(status!=null&&status.length>=1?")":"")
					+(!productLineId.equals("")?" and ci.product_line_id="+productLineId:"")
					+(!productName.equals("")?" and p.oriname='"+productName+"'":"")
					+(!type.equals("")?" and ci.type="+type:"")
					+(!storageId.equals("")?" and ci.storage_id="+storageId:"")
					+(!stockAreaId.equals("")?" and ci.stock_area_id="+stockAreaId:"")
					+(!shelfCode.equals("")?" and ci.whole_code regexp '"+"[A-Z0-9\\-]{7}"+shelfCode+"[0-9]{3}"+"'":"")
					+(!floorNum.equals("")?" and ci.floor_num="+floorNum:"")
					+(shelfId.equals("")?" and ci.store_type in (0,1,4)":"")
					+(" order by ci.whole_code asc")
					);
			List productLineNameList=new ArrayList();//产品线列表
			while(rs.next()){
				String productLineName=rs.getString("pl.name");
				productLineNameList.add(productLineName==null?"":productLineName);
			}
			rs.beforeFirst();
			List list = this.getCargoAndProductStockList(rs);
			Iterator iter = list.listIterator();
			
			List operList=new ArrayList();
			List statusList=new ArrayList();
			while(iter.hasNext()){
				CargoProductStockBean cps = (CargoProductStockBean)iter.next();
				voProduct product = new voProduct();
				if(cps.getProductId()>0){
					product = wareService.getProduct(cps.getProductId());
				}
				cps.setProduct(product);
				CargoInfoBean ciBean=cps.getCargoInfo();//一个货位
				ResultSet countRS=service.getDbOp().executeQuery("select co.code,cop.status_name " +
						"from cargo_operation co join cargo_operation_cargo coc on co.id=coc.oper_id " +
						"join cargo_operation_process cop on co.status=cop.id "+
						"where co.status in (1,2,3,4,5,6,10,11,12,13,14,15,19,20,21,22,23,24,28,29,30,31,32,33,37,38,39,40,41,42) " +
						"and co.effect_status in (0,1) "+
						"and (coc.in_cargo_whole_code='"+ciBean.getWholeCode()+
						"' or coc.out_cargo_whole_code='"+ciBean.getWholeCode()+"') and coc.product_id="+product.getId());
				List operCodeList=new ArrayList();
				List statusNameList=new ArrayList();
				while(countRS.next()){
					String operCode=countRS.getString("co.code");
					String statusName=countRS.getString("cop.status_name");
					if(!operCodeList.contains(operCode)){
						operCodeList.add(operCode);
						statusNameList.add(statusName);
					}
				}
				countRS.close();
				operList.add(operCodeList);
				statusList.add(statusNameList);
			}
			String hasOperList="0";//是否应该显示未完成作业单列表，1为显示，0为不显示
			if(status!=null&&status.length==1&&status[0].equals("0")&&stockCountStart==0&&stockCountEnd==0){
				hasOperList="1";
			}
			request.setAttribute("hasOperList", hasOperList);
			request.setAttribute("operList", operList);
			request.setAttribute("statusList", statusList);
			request.setAttribute("productLineNameList", productLineNameList);
			request.setAttribute("list", list);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return "forward:/admin/rec/sys/cargoListPrint.jsp";
	}
	/**
	 * 装箱管理查询
	 * 2013-8-22
	 * 朱爱林	
	 */
	@RequestMapping("/cartonningInfo")
	@ResponseBody
	public Object cartonningInfo(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String select = StringUtil.convertNull(request.getParameter("select"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String userName = StringUtil.convertNull(request.getParameter("userName"));
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		String[] status=null;
		String stat = StringUtil.convertNull(request.getParameter("status"));
		if("".equals(stat)){
			status=new String[]{"0","1"};
		}else{
			status = stat.split(",");
		}
		Map<String,Object> resultMap = new HashMap<String, Object>();
		List<Map<String,String>> lists = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		resultMap.put("total","0");
		resultMap.put("rows",lists);
		try {
			StringBuilder url = new StringBuilder();
			url.append("cartonningInfoAction.do?method=cartonningInfo");
			StringBuilder sql = new StringBuilder();
			sql.append("ci.id>0");
			sql.append(" and (");
			for(int i=0;i<status.length;i++){
				if(i!=0){
					sql.append(" or ");
				}
				sql.append(" ci.status="+status[i]);
				url.append("&status=" +status[i]);
			}
			sql.append(") ");
			if(select.length()>0||select!=""){
				sql.append(" and ci.code="+"'"+select+"'");
				url.append("&select=" + Encoder.encrypt(select));
			}
			if(!"".equals(productCode)){
				sql.append(" and cpi.product_code="+"'"+productCode+"'");
				url.append("&productCode=" + productCode);
			}
			if(!"".equals(userName)){
				sql.append(" and ci.name="+"'"+userName+"'");
				url.append("&userName=" + userName);
			}
			if(!"".equals(startTime)){
				if("".equals(endTime)){
					endTime = DateUtil.getNowDateStr();
				}
				sql.append(" and left(ci.create_time,10) between '" + startTime + "' and '" + endTime + "'");
				url.append("&startTime=" + startTime + "&endTime=" + endTime);
			}
//			Map<String,Object> resultMap = new HashMap<String, Object>();
//			List<Map<String,String>> lists = new ArrayList<Map<String,String>>();
//			Map<String,String> map = null;
			
			String page1 = StringUtil.convertNull(request.getParameter("page"));
			String rows1 = StringUtil.convertNull(request.getParameter("rows"));
			int page = page1.equals("")?1:Integer.parseInt(page1);
			int rows = rows1.equals("")?20:Integer.parseInt(rows1);
			int start = (page-1)*rows;//开始下标
			int totalCount = service.getCartonningAndProductListCount(sql.toString(),wareService.getDbOp());
			resultMap.put("total", totalCount+"");
			
//			int countPerPage = 20;
//			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
//			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List list = service.getCartonningAndProductList(sql.toString(), start, rows, "ci.id desc",wareService.getDbOp());
			if(!sql.toString().equals("id>0")&&list.size()==0){
				request.setAttribute("tip", "无法找到装箱记录");
				request.setAttribute("result", "failure");
				resultMap.put("rows", lists);
				return resultMap;
			}
			UserGroupBean group = user.getGroup();
			boolean boo = group.isFlag(556);
			boolean boo2 = group.isFlag(557);
			StringBuilder control = new StringBuilder();
			String root = request.getContextPath();
			for(int i=0;i<list.size();i++){
				CartonningInfoBean bean = (CartonningInfoBean)list.get(i);
				map = new HashMap<String, String>();
				map.put("cargo_info_code",bean.getCode());//装箱单编号
				map.put("cargo_info_productcode",bean.getProductBean().getProductCode());//商品编号
				map.put("cargo_info_productcount",bean.getProductBean().getProductCount()+"");//装箱数量
				map.put("cargo_info_cargowholecode",bean.getCargoWholeCode());//货位号
				map.put("cargo_info_createtime",bean.getCreateTime().substring(0, 19));//创建时间
				map.put("cargo_info_name",bean.getName());//责任人
				map.put("cargo_info_statusname",bean.getStatusName());//状态
				if(bean.getStatus()!=2){
					control.append("<a href=\'").append(root).append("/CargoController/printCartonningInfo.mmx?id=")
							.append(bean.getId())
							.append("\' target=\"blank\">打印装箱单</a>&nbsp;")
							.append("<a  onclick=\"cancel(\'").append(bean.getId()).append("\',\'").append(bean.getCode())
							.append("\');\">");
					if(boo){
						control.append("作废&nbsp;");
					}
					control.append("</a>");
				}else{
					control.append("<span>打印装箱单&nbsp;");
					if(boo){
						control.append("作废&nbsp;");
					}
					control.append("</span>");
				}
				//cartonningCargo
				if(bean.getCargoId()==0&&bean.getStatus()!=2){
					control.append("<a ").append("  onclick=\"relate(\'").append(bean.getCode()).append("\');\"")
							.append(" target=\"_blank\">关联货位&nbsp;</a>");
				}else{
					control.append("<span>关联货位&nbsp;</span>");
				}
				if(bean.getStatus()!=2 && bean.getCargoId()!=0&&boo2){
//					control.append("<a href=\"").append(root).append("/admin/cartonningInfoAction.do?method=cartonningCargo&code=")
//							.append(bean.getCode())
//							.append("&flag=1\" target=\"_blank\">修正货位</a>");
					control.append("<a ").append(" onclick=\"update(\'").append(bean.getCode()).append("\',1);\"")
					.append(" target=\"_blank\">修正货位</a>");
				}else{
					control.append("<span>修正货位</span>");
				}
				map.put("cargo_info_control",control.toString());
				control.delete(0, control.length());
				lists.add(map);
			}
			resultMap.put("rows", lists);
//			request.setAttribute("cartonningList", list);
//			request.setAttribute("url", url.toString());
//			request.setAttribute("paging", paging);
//			request.setAttribute("status", status);
//			paging.setPrefixUrl(url.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return resultMap;
	}
	/**
	 * 创建装箱记录
	 * 2013-8-22
	 * 朱爱林
	 */
	@RequestMapping("/createCartonningInfo")
	public void createCartonningInfo(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if (user == null) {
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		String productCode = StringUtil.convertNull(request.getParameter("productCode2"));
		int productCount = StringUtil.StringToId(request.getParameter("count"));
		int cause = StringUtil.StringToId(request.getParameter("cause"));
		WareService wareService = new WareService();
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		synchronized(cargoLock){
			try {  
				voProduct pBean = wareService.getProduct2("code="+"'"+productCode+"'");
				if(pBean==null){
					ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+productCode+"'");
					if(bBean==null){
//						request.setAttribute("tip", "没有找到此商品");
//						request.setAttribute("result", "failure");
//						return mapping.findForward(IConstants.FAILURE_KEY);
						result.append("{result:'failure',tip:'没有找到此商品!'}");
						response.getWriter().write(result.toString());
						return;
					}else{
						pBean = wareService.getProduct2("a.id="+bBean.getProductId());
					}
				}
				String code = service.getZXCodeForToday();
				CartonningInfoBean bean =new CartonningInfoBean();
				bean.setCode(code);
				bean.setCause(cause);
				bean.setCreateTime(DateUtil.getNow());
				bean.setStatus(0);
				bean.setName(user.getUsername());
				CartonningProductInfoBean productBean=new CartonningProductInfoBean();
				productBean.setProductCount(productCount);
				productBean.setProductCode(pBean.getCode());
				productBean.setProductName(pBean.getName());
				productBean.setProductId(pBean.getId());
				bean.setProductBean(productBean);
				service.getDbOp().startTransaction();
				if( !service.addCartonningInfo(bean) ) {
					service.getDbOp().rollbackTransaction();
					result.append("{result:'failure',tip:'添加装箱单失败！'}");
					response.getWriter().write(result.toString());
					return;
				}
				if( !service.fixCartonningInfoCode(code, service.getDbOp(), bean) ) {
					service.getDbOp().rollbackTransaction();
					result.append("{result:'failure',tip:'添加装箱单失败！'}");
					response.getWriter().write(result.toString());
					return;
				}
				code = bean.getCode();
				CartonningInfoBean bean1 = service.getCartonningInfo("code="+"'"+code+"'");
				productBean.setCartonningId(bean1.getId());
				if ( !service.addCartonningProductInfo(productBean) ) {
					service.getDbOp().rollbackTransaction();
					result.append("{result:'failure',tip:'添加装箱单失败！'}");
					response.getWriter().write(result.toString());
					return;
				}
				service.getDbOp().commitTransaction();
				result.append("{result:'success',tip:'成功创建装箱记录！'}");
				response.getWriter().write(result.toString());
				return;
			} catch (Exception e) {
				boolean isAuto = service.getDbOp().getConn().getAutoCommit();
				if( !isAuto ) {
					service.getDbOp().rollbackTransaction();
				}
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}
//		String url="/admin/cartonningInfoAction.do?method=cartonningInfo";
//		ActionForward aUrl = new ActionForward(url);
//		aUrl.setRedirect(true);
	}
	/**
	 * 打印装箱单
	 * 2013-8-22
	 * 朱爱林	
	 */
	@RequestMapping("/printCartonningInfo")
	public String printCartonningInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		int id = StringUtil.StringToId(request.getParameter("id"));
		WareService wareService = new WareService();
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		synchronized(cargoLock){
			try {  
				CartonningInfoBean bean = service.getCartonningInfo("id="+id);
				CartonningProductInfoBean pBean = service.getCartonningProductInfo("cartonning_id="+id);
				service.updateCartonningInfo("status=1", "id="+id);
				bean.setProductBean(pBean);
				request.setAttribute("bean", bean);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				wareService.releaseAll();
			}
		}
		return "forward:/admin/rec/sys/cartonningInfoPrint.jsp";
	}
	/**
	 * 作废装箱单
	 * 2013-8-22
	 * 朱爱林	
	 */
	@RequestMapping("/cancelCartonningInfo")
	public void cancelCartonningInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if (user == null) {
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		String id = StringUtil.convertNull(request.getParameter("id"));
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,null);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		synchronized(cargoLock){
			try {  
				CartonningInfoBean cartonningInfo=service.getCartonningInfo("id="+id);
				CargoOperationBean cargoOperation = cargoService.getCargoOperation("id=" + cartonningInfo.getOperId());
				if(cargoOperation != null){
					if(cargoOperation.getStatus()!= 7 
							&& cargoOperation.getStatus()!= 8 
							&& cargoOperation.getStatus()!= 9 
							&& cargoOperation.getStatus()!= 16 
							&& cargoOperation.getStatus()!= 17
							&& cargoOperation.getStatus()!= 18 
							&& cargoOperation.getStatus()!= 25 
							&& cargoOperation.getStatus()!= 26 
							&& cargoOperation.getStatus()!= 27 
							&& cargoOperation.getStatus()!= 34 
							&& cargoOperation.getStatus()!= 35 
							&& cargoOperation.getStatus()!= 36){
//						request.setAttribute("result", "此装箱单关联的作业单为未完成状态！");
//						return mapping.findForward("soDownProduct");
						result.append("{result:'failure',tip:'此装箱单关联的作业单为未完成状态！'}");
						response.getWriter().write(result.toString());
						return;
					}
				}
				service.updateCartonningInfo("status=2", "id="+id);
				result.append("{result:'success',tip:'作废操作成功！'}");
				response.getWriter().write(result.toString());
				return;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}
//		String url="/admin/cartonningInfoAction.do?method=cartonningInfo";
//		return new ActionForward(url);
	}
	/**
	 * 关联货位
	 * 2013-8-23
	 * 朱爱林
	 */
	@RequestMapping("/cartonningCargo")
	public void cartonningCargo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if (user == null) {
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		String code=request.getParameter("code");//装箱单号
		String cargoWholeCode=request.getParameter("cargoWholeCode");//货位编号
		String flag = request.getParameter("flag");//用来判断是第一次关联货位，还是修改或为，flag为NULL表示第一次关联货位
		WareService wareService = new WareService();
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		synchronized(cargoLock){
			try {  
				if(code!=null){
					CartonningInfoBean bean = service.getCartonningInfo("code='"+code+"'");
					if(bean==null){
//						request.setAttribute("tip", "该装箱单不存在！");
//						request.setAttribute("result", "failure");
//						return mapping.findForward(IConstants.FAILURE_KEY);
						result.append("{result:'failure',tip:'该装箱单不存在！'}");
						response.getWriter().write(result.toString());
						return;
					}
					if(flag==null && bean.getCargoId()!=0){
//						request.setAttribute("tip", "该装箱单已经关联了货位！");
//						request.setAttribute("result", "failure");
//						return mapping.findForward(IConstants.FAILURE_KEY);
						result.append("{result:'failure',tip:'该装箱单已经关联了货位！'}");
						response.getWriter().write(result.toString());
						return;
					}
					if(bean.getStatus()==2){
//						request.setAttribute("tip", "该装箱单已作废！");
//						request.setAttribute("result", "failure");
//						return mapping.findForward(IConstants.FAILURE_KEY);
						result.append("{result:'failure',tip:'该装箱单已作废！'}");
						response.getWriter().write(result.toString());
						return;
					}
					if(code!=null&&cargoWholeCode!=null){//需要绑定
						CartonningProductInfoBean cartonningProduct=service.getCartonningProductInfo("cartonning_id="+bean.getId());
						if(cartonningProduct==null){
//							request.setAttribute("tip", "该货位号无效！请检查并输入正确的货位号！");
//							request.setAttribute("result", "failure");
//							return mapping.findForward(IConstants.FAILURE_KEY);
							result.append("{result:'failure',tip:'该货位号无效！请检查并输入正确的货位号！'}");
							response.getWriter().write(result.toString());
							return;
						}
						CargoInfoBean cargoInfo=cargoService.getCargoInfo("whole_code='"+cargoWholeCode+"'");



						if(cargoInfo==null){
//							request.setAttribute("tip", "该货位号无效！请检查并输入正确的货位号！");
//							request.setAttribute("result", "failure");
//							return mapping.findForward(IConstants.FAILURE_KEY);
							result.append("{result:'failure',tip:'该货位号无效！请检查并输入正确的货位号！'}");
							response.getWriter().write(result.toString());
							return;
						}
						List areaList = CargoDeptAreaService. getCargoDeptAreaList(request);
						if (areaList != null && areaList.contains(String.valueOf(cargoInfo.getAreaId()))) {
							CargoProductStockBean cps = cargoService.getCargoProductStock("cargo_id=" + cargoInfo.getId() + " and product_id=" + cartonningProduct.getProductId());
							if (cps == null) {
//								request.setAttribute("tip", "该货位未绑定该商品！");
//								request.setAttribute("result", "failure");
//								return mapping.findForward(IConstants.FAILURE_KEY);
								result.append("{result:'failure',tip:'该货位未绑定该商品！'}");
								response.getWriter().write(result.toString());
								return;
							}
							service.updateCartonningInfo("cargo_id=" + cargoInfo.getId(), "id=" + bean.getId());
							request.setAttribute("complete", "1");
						}else{
//							request.setAttribute("tip", "用户没有操作该货位的权限");
//							request.setAttribute("result", "failure");
//							return mapping.findForward(IConstants.FAILURE_KEY);
							result.append("{result:'failure',tip:'用户没有操作该货位的权限！'}");
							response.getWriter().write(result.toString());
							return;
						}
						service.updateCartonningInfo("cargo_id="+cargoInfo.getId(), "id="+bean.getId());
						request.setAttribute("reload", "reload");
						request.setAttribute("complete", "1");
						result.append("{result:'success',tip:'货位关联操作成功！'}");
						response.getWriter().write(result.toString());
						return;
					}
					if(cargoWholeCode!=null){
					}
				}
				request.setAttribute("flag", flag);
				result.append("{result:'success',tip:'可以进行货位关联操作！'}");
				response.getWriter().write(result.toString());
				return;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}
	}
	/**
	 * 货位关联跳转链接
	 * 2013-8-23
	 * 朱爱林
	 */
	@RequestMapping("/cartonningCargoJsp")
	public String cartonningCargoJsp(HttpServletRequest request,HttpServletResponse response,
				@RequestParam String code){
		request.setAttribute("code", code);
		return "forward:/admin/rec/sys/cartonningCargo.jsp";
	}
	/**
	 * 获取库类型下拉选
	 * 2013-8-26
	 * 朱爱林
	 */
	@RequestMapping("/querySelectStockType")
	@ResponseBody
	public Object querySelectStockType(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user==null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		/*
		 * HashMap stockMap = ProductStockBean.stockTypeMap;
			Iterator stockKeyIter = stockMap.keySet().iterator();
			while(stockKeyIter.hasNext()){
				Integer key = (Integer)stockKeyIter.next();
				if(key.intValue() == ProductStockBean.STOCKTYPE_QUALITYTESTING || 
						key.intValue() == ProductStockBean.STOCKTYPE_NIFFER)
					continue;
					
			<option value="<%= key.intValue() %>"><%= ProductStockBean.getStockTypeName(key.intValue()) %></option>
		 */
		String flag = StringUtil.convertNull(request.getParameter("flag"));
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> map = new HashMap<String, String>();
		boolean boo = "notAll".equals(flag);//判断是否需要显示“全部”
		if(!boo){
			map.put("id", "");
			map.put("text", "全部");
			map.put("selected", "true");
			map.put("index", "0");
			list.add(map);
		}
		HashMap stockMap = ProductStockBean.stockTypeMap;
		Iterator stockKeyIter = stockMap.keySet().iterator();
		int i=1;
		while(stockKeyIter.hasNext()){
			Integer key = (Integer)stockKeyIter.next();
			if(key.intValue() == ProductStockBean.STOCKTYPE_QUALITYTESTING || 
					key.intValue() == ProductStockBean.STOCKTYPE_NIFFER){
				continue;
			}
			map = new HashMap<String, String>();
			if(boo&&i==1){
				map.put("selected", "true");
			}
			map.put("id",key.intValue()+"");
			map.put("text", ProductStockBean.getStockTypeName(key.intValue()));
			map.put("index", i++ +"");
			list.add(map);
		}
		return list;
	}
	/**
	 * 获取库区域下拉选
	 * 2013-10-22
	 * 朱爱林
	 */
	@RequestMapping("/querySelectStockAreaAccess")
	@ResponseBody
	public Object querySelectStockAreaAccess(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user==null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		int stockType = -1;
		//这里这么写是照顾原来分库库存查询页面
		if(!"null".equals(request.getParameter("stockType"))){
			stockType = StringUtil.StringToId(StringUtil.convertNull(request.getParameter("stockType")));//库类型
		}
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> map = new HashMap<String, String>();
		map.put("id", "");
		map.put("text", "全部");
		map.put("selected", "true");
		map.put("index", "0");
		list.add(map);
		
		List<StockAreaBean> areaList = ProductStockBean.getStockAreaByType(stockType);
		if(areaList!=null){
			for(StockAreaBean bean:areaList){
				map = new HashMap<String, String>();
				map.put("id", bean.getId()+"");
				map.put("text", bean.getName());
				list.add(map);
			}
		}
		
		/*HashMap stockMap = ProductStockBean.stockTypeMap;
		Iterator stockKeyIter = stockMap.keySet().iterator();
		Integer[] areas = null;
		Map areaMap = ProductStockBean.areaMap;
		if(ProductStockBean.STOCKTYPE_QUALIFIED==stockType){//合格库
			areas = ProductStockBean.AREA_QUALIFIED;
			buildAreaSelect(list, areas,areaMap);
		}else if(ProductStockBean.STOCKTYPE_CHECK==stockType){//待检库
			areas = ProductStockBean.AREA_CHECK;
			buildAreaSelect(list, areas,areaMap);
		}else if(ProductStockBean.STOCKTYPE_REPAIR==stockType){//维修库
			areas = ProductStockBean.AREA_REPAIR;
			buildAreaSelect(list, areas,areaMap);
		}else if(ProductStockBean.STOCKTYPE_BACK==stockType){//返厂库
			areas = ProductStockBean.AREA_BACK;
			buildAreaSelect(list, areas,areaMap);
		}else if(ProductStockBean.STOCKTYPE_RETURN==stockType){//退货库
			areas = ProductStockBean.AREA_RETURN;
			buildAreaSelect(list, areas,areaMap);
		}else if(ProductStockBean.STOCKTYPE_DEFECTIVE==stockType){//残次品库
			areas = ProductStockBean.AREA_DEFECTIVE;
			buildAreaSelect(list, areas,areaMap);
		}else if(ProductStockBean.STOCKTYPE_SAMPLE==stockType){//样品库
			areas = ProductStockBean.AREA_SAMPLE;
			buildAreaSelect(list, areas,areaMap);
		}else if(ProductStockBean.STOCKTYPE_AFTER_SALE==stockType){//售后库
			areas = ProductStockBean.AREA_AFTER_SALE;
			buildAreaSelect(list, areas,areaMap);
		}*/
		
		return list;
	}
	
	/**
	 * 获取库区域下拉选
	 * 2013-10-22
	 * 朱爱林
	 */
	@RequestMapping("/querySelectStockAreaPermissionAccess")
	@ResponseBody
	public Object querySelectStockAreaPermissionAccess(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user==null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		int stockType = -1;
		//这里这么写是照顾原来分库库存查询页面
		if(!"null".equals(request.getParameter("stockType"))){
			stockType = StringUtil.toInt(StringUtil.convertNull(request.getParameter("stockType")));//库类型
		}
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> map = new HashMap<String, String>();
		map.put("id", "");
		map.put("text", "全部");
		map.put("selected", "true");
		map.put("index", "0");
		list.add(map);
		
		List<StockAreaBean> areaList = ProductStockBean.getStockAreaByType(stockType);
		List<String> cargoAreaList = CargoDeptAreaService.getCargoDeptAreaList(request, stockType);
		if(areaList!=null){
			for(StockAreaBean bean:areaList){
				for (String area : cargoAreaList) {
					if (area.equals(bean.getId() + "")) {
						map = new HashMap<String, String>();
						map.put("id", bean.getId()+"");
						map.put("text", bean.getName());
						list.add(map);
						break;
					}
				}
			}
		}
		
		return list;
	}
	
	private void buildAreaSelect(List<Map<String, String>> list, Integer[] areas,Map areaMap) {
		Map<String,String> map= null;
		for(int i=0;i<areas.length;i++){
			map = new HashMap<String, String>();
			map.put("id", areas[i]+"");
			map.put("text", areaMap.get(areas[i]).toString());
			list.add(map);
		}
	}
	/**
	 * 货位进销存列表
	 * 2013-8-26
	 * 朱爱林	
	 */
	@RequestMapping("/cargoStockCard")
	@ResponseBody
	public Object cargoStockCard(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		Map<String,Object> resultMap = new HashMap<String, Object>();
		List<Map<String,String>> lists = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		resultMap.put("total","0");
		resultMap.put("rows",lists);
		try{
//			int countPerPage = 30;
	        String stockType = StringUtil.convertNull(request.getParameter("stockType"));//库类型
	        String stockArea = StringUtil.convertNull(request.getParameter("stockArea"));//库区域
	        String code = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("code")));//单据号
	        String productOriName="";
	        if(Encoder.decrypt(StringUtil.dealParam(request.getParameter("productOriName")))!=null){
	        	productOriName =Encoder.decrypt(StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productOriName"))));
	        }else{
	        	productOriName=StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productOriName")));
	        }
	        String productCode=StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productCode")));
	        String productName="";
	        if(Encoder.decrypt(StringUtil.dealParam(request.getParameter("productName")))!=null){
	        	productName =Encoder.decrypt(StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productName"))));
	        }else{
	        	productName=StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productName")));
	        }
	        String startDate = StringUtil.dealParam(request.getParameter("startDate"));//起始时间
	        String endDate = StringUtil.dealParam(request.getParameter("endDate"));//截止时间
	        String cargoStoreType=StringUtil.dealParam(StringUtil.convertNull(request.getParameter("cargoStoreType")));//货位存放类型
	        String cargoWholeCode=StringUtil.dealParam(StringUtil.convertNull(request.getParameter("cargoWholeCode")));//货位号

			
			if(!StringUtil.isNull(startDate) && !StringUtil.isNull(endDate)){
        		Date start = DateUtil.parseDate(startDate);
        		Date end = DateUtil.parseDate(endDate);
        		if(end.getTime() < start.getTime()){
            		request.setAttribute("tip", "截止时间不能早于起始时间！");
                    request.setAttribute("result", "failure");
                    return resultMap;
        		}
        	}
            String condition = null;
            StringBuilder buf = new StringBuilder();
            StringBuilder paramBuf = new StringBuilder();
            boolean canQuery = false;
            if(!stockType.equals("")){
            	buf.append("stock_type=").append(stockType);
            	paramBuf.append("&stockType=").append(stockType);
            }
            if(!stockArea.equals("")&&!stockArea.equals("-1")){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("stock_area=").append(stockArea);
            	paramBuf.append("&stockArea=").append(stockArea);
            }
            if(!code.equals("")){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("code='").append(code).append("'");
            	paramBuf.append("&code=").append(code);
            }
            if(!StringUtil.isNull(startDate)){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("left(create_datetime, 10)>='").append(startDate).append("'");
            	paramBuf.append("&startDate=").append(startDate);
            }
            if(!StringUtil.isNull(endDate)){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("left(create_datetime, 10)<='").append(endDate).append("'");
            	paramBuf.append("&endDate=").append(endDate);
            }
            if(!cargoStoreType.equals("")){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("cargo_store_type=").append(cargoStoreType);
            	paramBuf.append("&cargoStoreType=").append(cargoStoreType);
            }
            if(!cargoWholeCode.equals("")){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("cargo_whole_code='").append(cargoWholeCode).append("'");
            	paramBuf.append("&cargoWholeCode=").append(cargoWholeCode);
            }
            voProduct p = null;
            StringBuilder pBuf = new StringBuilder();
            if(!productCode.equals("")){
            	if(pBuf.length() > 0){
            		pBuf.append(" and ");
            	}
            	pBuf.append("a.code='").append(productCode).append("'");
            	paramBuf.append("&productCode=").append(productCode);
            }
            if(!productName.equals("")){
            	paramBuf.append("&productName=").append(Encoder.encrypt(productName));
            	if(pBuf.length() > 0){
            		pBuf.append(" and ");
            	}
            	pBuf.append("a.name='").append(productName).append("'");
            }
        	if(!productOriName.equals("")){
            	if(pBuf.length() > 0){
            		pBuf.append(" and ");
            	}
            	pBuf.append("a.oriname='").append(productOriName).append("'");
            	paramBuf.append("&productOriName=").append(Encoder.encrypt(productOriName));
        	}
        	
        	if(pBuf.length() > 0){
        		p = wareService.getProduct2(pBuf.toString());
            	if(p != null){
            		if(buf.length() > 0){
            			buf.append(" and ");
            		}
            		buf.append("product_id=").append(p.getId());
            		canQuery = true;
            		request.setAttribute("product", p);
            	}
        	}
            if(buf.length() > 0){
            	condition = buf.toString();
            }
            if(canQuery && condition != null){
            	
    			
    			String page1 = StringUtil.convertNull(request.getParameter("page"));
    			String rows1 = StringUtil.convertNull(request.getParameter("rows"));
    			int page = page1.equals("")?1:Integer.parseInt(page1);
    			int rows = rows1.equals("")?20:Integer.parseInt(rows1);
    			int start = (page-1)*rows;//开始下标
	            //总数
	            int totalCount = service.getCargoStockCardCount(condition);
	            resultMap.put("total", totalCount+"");
	            //页码
//	            int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
//	            PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
	            List list = service.getCargoStockCardList(condition, start, rows, "create_datetime desc,id desc");
	            UserGroupBean group = user.getGroup();
	            boolean boo = group.isFlag(182);
	            StringBuilder str = new StringBuilder();
	            str.append("产品编号：").append(p.getCode()).append("&nbsp;&nbsp;")
	            	.append("小店名称：").append(StringUtil.toWml(p.getName())).append("&nbsp;&nbsp;")
	            	.append("原名称：").append(StringUtil.toWml(p.getOriname())).append("&nbsp;&nbsp;")
	            	.append("状态：").append(p.getStatusName());
	            if(list!=null){
	            	for(int i=0;i<list.size();i++){
	            		CargoStockCardBean bean = (CargoStockCardBean) list.get(i);
	            		map = new HashMap<String, String>();
	            		/*
	            		 * 	产品编号：<%= product.getCode() %>&nbsp;&nbsp;
	小店名称：<%= StringUtil.toWml(product.getName()) %>&nbsp;&nbsp;
	原名称：<%= StringUtil.toWml(product.getOriname()) %>&nbsp;&nbsp;
	状态：<%= product.getStatusName() %>
	            		 */
	            		map.put("product_detail", str.toString());
	            		map.put("cargo_stock_card_stocktype", bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":ProductStockBean.getStockTypeName(bean.getStockType()));//库类型
	            		map.put("cargo_stock_card_stockarea", bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":ProductStockBean.getAreaName(bean.getStockArea()));//库区域
	            		map.put("cargo_stock_card_code", bean.getCode());//单据号
	            		map.put("cargo_stock_card_cardtype", bean.getCardTypeName());//卡片类型
	            		map.put("cargo_stock_card_createdatetime", StringUtil.cutString(bean.getCreateDatetime(), 19));//创建时间
	            		map.put("cargo_stock_card_stockincount", (bean.getStockInCount() > 0)?String.valueOf(bean.getStockInCount()):"-");//入库量
	            		if(boo){
	            			map.put("cargo_stock_card_inpricesum", (bean.getStockInPriceSum() > 0)?StringUtil.formatDouble2(bean.getStockInPriceSum()):"-");//入库金额
	            			map.put("cargo_stock_card_outpricesum", (bean.getStockOutPriceSum() > 0)?StringUtil.formatDouble2(bean.getStockOutPriceSum()):"-");//出库金额
	            			map.put("cargo_stock_card_stockprice", bean.getStockPrice()+"");//库存单价
	            			map.put("cargo_stock_card_allstockpricesum", StringUtil.formatDouble2(bean.getAllStockPriceSum()));//结存总额
	            		}
	            		map.put("cargo_stock_card_outcount", (bean.getStockOutCount() > 0)?String.valueOf(bean.getStockOutCount()):"-");//出库量
	            		map.put("cargo_stock_card_wholecode", bean.getCargoWholeCode());//货位号
	            		map.put("cargo_stock_card_storetype", bean.getCargoStoreTypeName());//货位存放类型
	            		map.put("cargo_stock_card_currentcargostock", bean.getCurrentCargoStock()+"");//当前货位库存
	            		map.put("cargo_stock_card_currentstock", bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":bean.getCurrentStock()+"");//本区域本库类总库存
	            		map.put("cargo_stock_card_allstock", bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":bean.getAllStock()+"");//全库总库存
	            		lists.add(map);
	            	}
	            	resultMap.put("rows", lists);
	            }
//	            paging.setPrefixUrl("cargoInfo.do?method=cargoStockCard" + paramBuf.toString());
//	            request.setAttribute("paging", paging);
//	            request.setAttribute("list", list);
	            return resultMap;
            }
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		return resultMap;
	}
	/**
	 * 新库存管理-->进销存卡片
	 * 2013-8-26
	 * 朱爱林	
	 */
	@RequestMapping("/stockCardList")
	@ResponseBody
	public Object stockCardList(HttpServletRequest request,HttpServletResponse response){
//    	int countPerPage = 30;
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
        int stockType = StringUtil.toInt(request.getParameter("stockType"));
        int stockArea = StringUtil.toInt(request.getParameter("stockArea"));
        String code = StringUtil.dealParam(request.getParameter("code"));
        String productCode = StringUtil.dealParam(request.getParameter("productCode"));
        String productName = StringUtil.dealParam(request.getParameter("productName"));
        String productOriName = StringUtil.dealParam(request.getParameter("productOriName"));
        String startDate = StringUtil.dealParam(request.getParameter("startDate"));
        String endDate = StringUtil.dealParam(request.getParameter("endDate"));

        productName = Encoder.decrypt(productName);//解码为中文
		if(productName==null){//解码失败,表示已经为中文,则返回默认
			productName =StringUtil.dealParam(request.getParameter("productName"));//名称
		}
		if (productName==null) productName="";

		productOriName = Encoder.decrypt(productOriName);//解码为中文
		if(productOriName==null){//解码失败,表示已经为中文,则返回默认
			productOriName =StringUtil.dealParam(request.getParameter("productOriName"));//名称
		}
		if (productOriName==null) productOriName="";

		DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp_slave);
        IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
        Map<String,Object> resultMap = new HashMap<String, Object>();
		List<Map<String,String>> lists = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		resultMap.put("total","0");
		resultMap.put("rows",lists);
        try {
        	if(!StringUtil.isNull(startDate) && !StringUtil.isNull(endDate)){
        		Date start = DateUtil.parseDate(startDate);
        		Date end = DateUtil.parseDate(endDate);
        		if(end.getTime() < start.getTime()){
            		request.setAttribute("tip", "截止时间不能早于起始时间！");
                    request.setAttribute("result", "failure");
                    return resultMap;
        		}
        	}
            String condition = null;
            StringBuilder buf = new StringBuilder();
            StringBuilder paramBuf = new StringBuilder();
            boolean canQuery = false;
            if(stockType >= 0){
            	buf.append("stock_type=").append(stockType);
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("stockType=").append(stockType);
            }
            if(stockArea >= 0){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("stock_area=").append(stockArea);
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("stockArea=").append(stockArea);
            }
            if(!StringUtil.isNull(code)){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("code='").append(code).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("code=").append(code);
            }
            if(!StringUtil.isNull(startDate)){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("create_datetime >='").append(startDate).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("startDate=").append(startDate);
            }
            if(!StringUtil.isNull(endDate)){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append("create_datetime  <='").append(endDate).append(" 23:59:59'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("endDate=").append(endDate);
            }
            voProduct p = null;
            StringBuilder pBuf = new StringBuilder();
            if(!StringUtil.isNull(productCode)){
            	if(pBuf.length() > 0){
            		pBuf.append(" and ");
            	}
            	pBuf.append("a.code='").append(productCode).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("productCode=").append(productCode);
            }
            if(!StringUtil.isNull(productName)){
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("productName=").append(Encoder.encrypt(productName));
            	if(pBuf.length() > 0){
            		pBuf.append(" and ");
            	}
            	pBuf.append("a.name='").append(productName).append("'");
            }
        	if(!StringUtil.isNull(productOriName)){
            	if(pBuf.length() > 0){
            		pBuf.append(" and ");
            	}
            	pBuf.append("a.oriname='").append(productOriName).append("'");
            	if(paramBuf.length() > 0){
            		paramBuf.append("&");
            	}
            	paramBuf.append("productOriName=").append(Encoder.encrypt(productOriName));
        	}
        	if(pBuf.length() > 0){
        		p = wareService.getProduct2(pBuf.toString());
            	if(p != null){
            		if(buf.length() > 0){
            			buf.append(" and ");
            		}
            		buf.append("product_id=").append(p.getId());
            		canQuery = true;
            		request.setAttribute("product", p);
            	}
        	}
            if(buf.length() > 0){
            	condition = buf.toString();
            }
            if(canQuery && condition != null){
	            //总数
	            int totalCount = service.getStockCardCount(condition);
            	String page1 = StringUtil.convertNull(request.getParameter("page"));
    			String rows1 = StringUtil.convertNull(request.getParameter("rows"));
    			int page = page1.equals("")?1:Integer.parseInt(page1);
    			int rows = rows1.equals("")?20:Integer.parseInt(rows1);
    			int start = (page-1)*rows;//开始下标
	            resultMap.put("total", totalCount+"");
	            //页码
//	            int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
//	            PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
	            List list = service.getStockCardList(condition, start, rows, "create_datetime desc,id desc");
	            Collections.sort(list, new StockCardComparator());
//	            paging.setPrefixUrl("stockCardList.jsp?" + paramBuf.toString());
	            UserGroupBean group = user.getGroup();
	            boolean boo = group.isFlag(182);
	            StringBuilder str = new StringBuilder();
	            str.append("产品编号：").append(p.getCode()).append("&nbsp;&nbsp;")
	            	.append("小店名称：").append(StringUtil.toWml(p.getName())).append("&nbsp;&nbsp;")
	            	.append("原名称：").append(StringUtil.toWml(p.getOriname())).append("&nbsp;&nbsp;")
	            	.append("状态：").append(p.getStatusName());
	            if(list!=null){
	            	for(int i=0;i<list.size();i++){
	            		StockCardBean bean = (StockCardBean) list.get(i);
	            		map = new HashMap<String, String>();
	            		/*
	            		 * 产品编号：<%= product.getCode() %>&nbsp;&nbsp;
小店名称：<%= StringUtil.toWml(product.getName()) %>&nbsp;&nbsp;
原名称：<%= StringUtil.toWml(product.getOriname()) %>&nbsp;&nbsp;
状态：<%= product.getStatusName() %>
	            		 */
	            		map.put("product_detail", str.toString());
	            		map.put("stock_card_stocktype", bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":ProductStockBean.getStockTypeName(bean.getStockType()));//库类型
	            		map.put("stock_card_stockarea", bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":ProductStockBean.getAreaName(bean.getStockArea()));//库区域
	            		map.put("stock_card_code", bean.getCode());//单据号
	            		map.put("stock_card_cardtype", bean.getCardTypeName());//来源
	            		map.put("stock_card_createdatetime", StringUtil.cutString(bean.getCreateDatetime(), 19));//时间
	            		map.put("stock_card_stockincount", (bean.getStockInCount() > 0)?String.valueOf(bean.getStockInCount()):"-");//入库数量
	            		if(boo){
	            			map.put("stock_card_stockinprice", (bean.getStockInPriceSum() > 0)?StringUtil.formatDouble2(bean.getStockInPriceSum()):"-");//入库金额
	            			map.put("stock_card_stockoutpricesum", (bean.getStockOutPriceSum() > 0)?StringUtil.formatDouble2(bean.getStockOutPriceSum()):"-");//出库金额
	            			
	            		}
	            		map.put("stock_card_stockoutcount", (bean.getStockOutCount() > 0)?String.valueOf(bean.getStockOutCount()):"-");//出库数量
	            		map.put("stock_card_currentstock", bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":bean.getCurrentStock()+"");//当前结存
	            		map.put("stock_card_stockallarea", bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":bean.getStockAllArea()+"");//本库区域总结存
	            		map.put("stock_card_stockalltype", bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":bean.getStockAllType()+"");//本库类总结存
	            		map.put("stock_card_allstock", bean.getAllStock()+"");//全库总结存
	            		map.put("stock_card_stockprice", bean.getStockPrice()+"");//库存单价
	            		map.put("stock_card_allstockpricesum", StringUtil.formatDouble2(bean.getAllStockPriceSum())+"");//结存总额
	            		lists.add(map);
	            	}
	            	resultMap.put("rows", lists);
	            }
//	            return resultMap;
//	            request.setAttribute("paging", paging);
//	            request.setAttribute("list", list);
            }
        } finally {
        	dbOp_slave.release();
        }
        return resultMap;
    }
	/**
	 * 获取所有部门列表
	 * 2013-8-27
	 * 朱爱林
	 */
	@RequestMapping("/staffManagement")
	public void staffManagement(HttpServletRequest request,HttpServletResponse response)
		throws Exception{

		voUser user = (voUser) request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if (user == null) {
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return "/admin/error";
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String address = StringUtil.convertNull(request.getParameter("address"));
		try {
			List deptList=service.getCargoDeptList("parent_id0=0", -1, -1, null);//零级部门列表
			for(int i=0;i<deptList.size();i++){
				CargoDeptBean cd=(CargoDeptBean)deptList.get(i);
				List deptList2=service.getCargoDeptList("parent_id0="+cd.getId()+" and parent_id1=0", -1, -1, null);//该部门的一级部门列表
				cd.setJuniorDeptList(deptList2);
				for(int j=0;j<deptList2.size();j++){
					CargoDeptBean cd2=(CargoDeptBean)deptList2.get(j);
					List deptList3=service.getCargoDeptList("parent_id1="+cd2.getId()+" and parent_id2=0", -1, -1, null);//该部门的二级部门列表
					cd2.setJuniorDeptList(deptList3);
					for(int k=0;k<deptList3.size();k++){
						CargoDeptBean cd3=(CargoDeptBean)deptList3.get(k);
						List deptList4=service.getCargoDeptList("parent_id2="+cd3.getId()+" and parent_id3=0", -1, -1, null);//该部门的三级部门列表
						cd3.setJuniorDeptList(deptList4);
					}
				}
			}
			String root = request.getContextPath();
			StringBuilder json = new StringBuilder();
			json.append("[");
			for(int i=0;i<deptList.size();i++){
				CargoDeptBean cd=(CargoDeptBean)deptList.get(i); 
				int id = cd.getId();
				json.append("{id:\'").append(id).append("\',")
					.append("text:\'").append(cd.getName()).append("\',")
					.append("iconCls:\'icon-ok\',")
					.append("attributes:{url:\'").append(root)
					.append("/CargoController/"+address+".mmx").append("?id=").append(id).append("\'}");
				List deptList2=cd.getJuniorDeptList();
				if(deptList2!=null&&deptList2.size()>0){
					json.append(",children:[");
				}
				for(int j=0;j<deptList2.size();j++){
					CargoDeptBean cd2=(CargoDeptBean)deptList2.get(j);
					int id2 = cd2.getId();
					json.append("{id:\'").append(id2).append("\',")
					.append("text:\'").append(cd2.getName()).append("\',")
					.append("iconCls:\'icon-ok\',")
					.append("attributes:{url:\'").append(root)
					.append("/CargoController/"+address+".mmx").append("?id=").append(id)
					.append("&id2=").append(id2).append("\'}");
					List deptList3=cd2.getJuniorDeptList();
					if(deptList3!=null&&deptList3.size()>0){
						json.append(",children:[");
					}
					for(int k=0;k<deptList3.size();k++){
						CargoDeptBean cd3=(CargoDeptBean)deptList3.get(k);
						int id3 = cd3.getId();
						json.append("{id:\'").append(id3).append("\',")
						.append("text:\'").append(cd3.getName()).append("\',")
						.append("iconCls:\'icon-ok\',")
						.append("attributes:{url:\'").append(root)
						.append("/CargoController/"+address+".mmx").append("?id=").append(id)
						.append("&id2=").append(id2).append("&id3=").append(id3).append("\'}");
						List deptList4=cd3.getJuniorDeptList();
						if(deptList4!=null&&deptList4.size()>0){
							json.append(",children:[");
						}
						for(int l=0;l<deptList4.size();l++){
							CargoDeptBean cd4=(CargoDeptBean)deptList4.get(l);
							int id4 = cd4.getId();
							json.append("{id:\'").append(id4).append("\',")
							.append("text:\'").append(cd4.getName()).append("\',")
							.append("iconCls:\'icon-ok\',")
							.append("attributes:{url:\'").append(root)
							.append("/CargoController/"+address+".mmx").append("?id=").append(id)
							.append("&id2=").append(id2).append("&id3=").append(id3).append("&id4=").append(id4).append("\'}");
							if(l==deptList4.size()-1){
								json.append("}");
							}else{
								json.append("},");
							}
						}
						if(deptList4!=null&&deptList4.size()>0){
							json.append("]");
						}
						if(k==deptList3.size()-1){
							json.append("}");
						}else{
							json.append("},");
						}
					}
					if(deptList3!=null&&deptList3.size()>0){
						json.append("]");
					}
					if(j==deptList2.size()-1){
						json.append("}");
					}else{
						json.append("},");
					}
				}
				if(deptList2!=null&&deptList2.size()>0){
					json.append("]");
				}
				if(i==deptList.size()-1){
					json.append("}");
				}else{
					json.append("},");
				}
			}
			json.append("]");
//			System.out.println(json);
			result.append("{result:'success',tip:"+json.toString()+"}");
			response.getWriter().write(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	}
	/**
	 * 查询员工列表
	 * 2013-8-28
	 * 朱爱林	
	 */
	@RequestMapping("/queryStaffManagement")
	@ResponseBody
	public Object queryStaffManagement(HttpServletRequest request,HttpServletResponse response){

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		Map<String,Object> resultMap = new HashMap<String, Object>();
		List<Map<String,String>> lists = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		resultMap.put("total","0");
		resultMap.put("rows",lists);
		try {
//			int countPerPage = 20;
//			int pageIndex = 0;
			String page1 = StringUtil.convertNull(request.getParameter("page"));
			String rows1 = StringUtil.convertNull(request.getParameter("rows"));
			int page = page1.equals("")?1:Integer.parseInt(page1);
			int rows = rows1.equals("")?20:Integer.parseInt(rows1);
			int start = (page-1)*rows;//开始下标
			
			String condition = StringUtil.convertNull(request.getParameter("condition"));
			String kw = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("kw")));
			if(Encoder.decrypt(kw)==null){//第一次查询，未编码
				kw=Encoder.encrypt(kw);
			}
			String deptId = StringUtil.convertNull(request.getParameter("id"));
			String deptId2 = StringUtil.convertNull(request.getParameter("id2"));
			String deptId3 = StringUtil.convertNull(request.getParameter("id3"));
			String deptId4 = StringUtil.convertNull(request.getParameter("id4"));
//			if(request.getParameter("pageIndex")!=null){
//				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
//			}
//            PagingBean paging = new PagingBean(pageIndex, 0, countPerPage);
            String para = "";
            if(request.getParameter("para")!=null){
            	para=request.getParameter("para");
            }else{
            	para=(condition.equals("") ? "" : ("&condition=" + condition))
		            	+(kw.equals("") ? "" : ("&kw=" + kw))
		            	+(deptId.equals("") ? "" : ("&id=" + deptId))
		            	+(deptId2.equals("") ? "" : ("&id2=" + deptId2))
		            	+(deptId3.equals("") ? "" : ("&id3=" + deptId3))
		            	+(deptId4.equals("") ? "" : ("&id4=" + deptId4));
            }
//            paging.setCurrentPageIndex(pageIndex);
//            paging.setPrefixUrl("qualifiedStock.do?method=staffManagement"+para);
            
			List cargoStaffList = new ArrayList();
			List cargoStaffList2 = new ArrayList();
			if(request.getParameter("id") != null){ //按部门查询
				
				String deptCondition = "";
				deptCondition+= "parent_id0 = "+deptId;
				if(!"".equals(deptId4)){
					cargoStaffList = service.getCargoStaffList(" dept_id = "+deptId4, -1, -1, "create_datetime desc");
				}else{
					String temp = "";//记住当前id为哪个
					if(!"".equals(deptId3)){
						deptCondition+= " and parent_id2 = "+deptId3;
					}
					if(!"".equals(deptId2)){
						deptCondition+= " and parent_id1 = "+deptId2;
					}
					temp = !"".equals(deptId3)?deptId3:!"".equals(deptId2)?deptId2:deptId;
					List list = service.getCargoDeptList(deptCondition, -1, -1, " id desc");
					if(list!=null){
						CargoDeptBean cdb = null;
						StringBuilder ss = new StringBuilder();
						ss.append(" dept_id in(");
						for(int i=0;i<list.size();i++){
							cdb = (CargoDeptBean) list.get(i);
							ss.append(cdb.getId()+",");
						}
						ss.append(temp+")");
						cargoStaffList = service.getCargoStaffList(ss.toString(), -1, -1, "create_datetime desc");
					}else{
						cargoStaffList = service.getCargoStaffList(" dept_id = "+temp, -1, -1, "create_datetime desc");
					}
				}
				/*String code = "";
				if( !deptId.equals("")) {
						CargoDeptBean cdb = service.getCargoDept("id=" + deptId);
						if(  cdb!= null ) {
							code += cdb.getCode();
						}
					if(!deptId2.equals("")){
						code = code + service.getCargoDept("id=" + deptId2).getCode();
						if(!deptId3.equals("")){
							code = code + service.getCargoDept("id=" + deptId3).getCode();
							if(!deptId4.equals("")){
								code = code + service.getCargoDept("id=" + deptId4).getCode();
							}
						}
					}
				}
				
				while(cargoStaffList.size()==0&&(page-1)>=0){
					cargoStaffList = service.getCargoStaffList("code like '" + code + "%'", start, rows, "create_datetime desc");
					if(cargoStaffList.size()==0&&(page-1)>=0){
						page--;
//						paging.setCurrentPageIndex(pageIndex);
					}
				}*/
			}else{
				if( "".equals(condition)){ //查询所有
					cargoStaffList = service.getCargoStaffList("id>0", start, rows, "create_datetime desc");
					cargoStaffList2 = service.getCargoStaffList("id>0", -1, -1, "create_datetime desc");
				}else{ //按指定条件查询
					String conditionStr = "1 = 1";
					kw=Encoder.decrypt(kw);//解码
					if(!"dept_name".equals(condition)){
						if("create_datetime".equals(condition)){
							conditionStr = condition + " like '" + kw + "%'";
						}else{
							conditionStr = condition + "='" + kw +"'";
						}
					}else{
						List staffAllList=service.getCargoStaffList("id>0", -1, -1, null);//所有员工
						String ids="";//符合条件的员工id
						for(int i=0;i<staffAllList.size();i++){//分别拼出部门全称
							CargoStaffBean csb = (CargoStaffBean)staffAllList.get(i);
							CargoDeptBean deptBean = service.getCargoDept("id="+csb.getDeptId());
							String deptName0 = "";
							String deptName1 = "";
							String deptName2 = "";
							String deptName3 = "";
							if (deptBean.getParentId0() != 0) {
								deptName0 = service.getCargoDept("id=" + deptBean.getParentId0()).getName();
								if (deptBean.getParentId1() != 0) {
									deptName1 = service.getCargoDept("id=" + deptBean.getParentId1()).getName();
									if (deptBean.getParentId2() != 0) {
										deptName2 = service.getCargoDept("id=" + deptBean.getParentId2()).getName();
										deptName3 = service.getCargoDept("id=" + deptBean.getId()).getName();
									} else {
										deptName2 = service.getCargoDept("id=" + deptBean.getId()).getName();
									}
								} else {
									deptName1 = service.getCargoDept("id=" + deptBean.getId()).getName();
								}
							} else {
								deptName0 = service.getCargoDept("id=" + deptBean.getId()).getName();
							}
							//}
							String deptName = deptName0 + deptName1 + deptName2 + deptName3;
							if("物流中心".equals(deptName)){
								csb.setDeptName("物流中心直属");
							}
							if(deptName.indexOf(kw)>=0){//部门全程中包含查询条件
								ids+=csb.getId()+",";
							}
						}
						if(ids.length()>0){
							ids=ids.substring(0,ids.length()-1);//去掉最后的","
							conditionStr+=" and id in ("+ids+")";
						}else{
							conditionStr+=" and 1=2";
						}
						
					}
					while(cargoStaffList.size()==0&&(page-1)>=0){
						cargoStaffList = service.getCargoStaffList(conditionStr, start, rows, "create_datetime desc");
						if(cargoStaffList.size()==0&&page>=0){
							page--;
//							paging.setCurrentPageIndex(pageIndex);
						}
					}
					
					cargoStaffList2 = service.getCargoStaffList(conditionStr, -1, -1, "create_datetime desc");
					if(cargoStaffList.size() == 0){
						request.setAttribute("tip", "无法找到您查询的内容！");
						request.setAttribute("result", "failure");
						return resultMap;
					}
				}
			}
			if(cargoStaffList != null){
				UserGroupBean group = user.getGroup();
				boolean b1 = group.isFlag(419);
				boolean b2 = group.isFlag(418);
				boolean b3 = group.isFlag(420);
				String root = request.getContextPath();
				StringBuilder str = new StringBuilder();
				for(int i = 0; i < cargoStaffList.size(); i++){
					CargoStaffBean csb = (CargoStaffBean)cargoStaffList.get(i);
					CargoDeptBean deptBean = service.getCargoDept("id="+csb.getDeptId());
					String deptName1 = "";
					String deptName2 = "";
					String deptName3 = "";
					String deptName0 = "";
					
					if(deptBean!=null){
						if( deptBean.getParentId0()!=0){
							deptName0 = service.getCargoDept("id="+deptBean.getParentId0()).getName();
							if(deptBean.getParentId1()!=0){
								deptName1 = service.getCargoDept("id="+deptBean.getParentId1()).getName();
								if(deptBean.getParentId2()!=0){
									deptName2 = service.getCargoDept("id="+deptBean.getParentId2()).getName();
									deptName3 = service.getCargoDept("id="+deptBean.getId()).getName();
								}else{
									deptName2 = service.getCargoDept("id="+deptBean.getId()).getName();
								}
							}
							else{
								deptName1 = service.getCargoDept("id="+deptBean.getId()).getName();
							}
						}else{
							deptName0 = service.getCargoDept("id="+deptBean.getId()).getName();
						}
					}
					//}
					
					String deptName = deptName0 + deptName1 + deptName2 + deptName3;
					if("物流中心".equals(deptName)){
						csb.setDeptName("物流中心直属");
					}else{
						csb.setDeptName(deptName);
					}
					map = new HashMap<String, String>();
					map.put("cargo_staff_name", csb.getName());//姓名
					map.put("cargo_staff_username", csb.getUserName());//后台账号
					map.put("cargo_staff_code", csb.getCode());//员工编号
					map.put("cargo_staff_createdatetime", csb.getCreateDatetime().substring(0,10));//创建时间
					map.put("cargo_staff_detpname",csb.getDeptName());//归属部门
					map.put("cargo_staff_phone", csb.getPhone());//电话
					if(csb.getStatus()==0&&b1){
						str.append("<a href=\'").append(root).append("/CargoController/editStaffJsp.mmx?id="+csb.getId()+"\'><font color=\"#0000ff\">编辑</font></a>&nbsp;|&nbsp;");
						if(b2){
							str.append("<a onclick=\"deleteStaff(\'")
								.append(csb.getId())
								.append("\')\"><font color=\"#0000ff\">删除</font></a>&nbsp;|&nbsp;</a>");
							if(b3){
								str.append("<a href=\"").append(root).append("/admin/cargo/staffCodePrint.jsp?code=")
								.append(csb.getCode()).append("\" target=\"_blank\"><font color=\"#0000ff\">条码打印</font></a>");
							}
						}
					}else{
						str.append("<a onclick=\"recoverStaff(\'")
						.append(csb.getId())
						.append("\')\"><font color=\"#0000ff\">恢复</font></a>&nbsp;|&nbsp;</a>");
					}
					map.put("cargo_staff_control", str.toString());//操作
					str.delete(0, str.length());
					lists.add(map);
				}
			}
			int totalCount = cargoStaffList2.size();
			resultMap.put("total",totalCount+"");
			resultMap.put("rows", lists);
//			int totalPageCount = totalCount % countPerPage == 0 ? totalCount / countPerPage : ((totalCount - totalCount % countPerPage) / countPerPage + 1);
//			paging.setTotalCount(totalCount);
//			paging.setTotalPageCount(totalPageCount);
//			request.setAttribute("paging", paging);
//			request.setAttribute("para", para);
//			request.setAttribute("cargoStaffList", cargoStaffList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return resultMap;
	}
	/**
	 * 编辑员工信息页面
	 * 2013-8-28
	 * 朱爱林
	 */
	@RequestMapping("/editStaffJsp")
	public String editStaffJsp(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		try{
			if(request.getParameter("id") != null){ 
				int id = StringUtil.toInt(request.getParameter("id"));
				CargoStaffBean csb = service.getCargoStaff("id =" + id);
				CargoDeptBean deptBean = service.getCargoDept("id="+csb.getDeptId());
				List cargoDeptList = new ArrayList();
				if(deptBean!=null){
					if( deptBean.getParentId0()!=0){
						cargoDeptList.add(service.getCargoDept("id="+deptBean.getParentId0()));
						if(deptBean.getParentId1()!=0){
							cargoDeptList.add(service.getCargoDept("id="+deptBean.getParentId1()));
							if(deptBean.getParentId2()!=0){
								cargoDeptList.add(service.getCargoDept("id="+deptBean.getParentId2()));
								cargoDeptList.add(service.getCargoDept("id="+deptBean.getId()));
							}
							else{
								cargoDeptList.add(service.getCargoDept("id="+deptBean.getId()));	
							}
						}else{
							cargoDeptList.add(service.getCargoDept("id="+deptBean.getId()));	
						}
					}else{
						cargoDeptList.add(service.getCargoDept("id="+deptBean.getId()));	
					}
				}
				
				//}
				List deptList0=service.getCargoDeptList("parent_id0 = 0 and parent_id1=0", -1, -1, null);
				StringBuilder str = new StringBuilder();
				String ss = "[{id:\'\',text:\'选择所属地区\'";
				boolean boo = false;
				String name="";
				if(deptBean!=null){
					//当为parent_id为0时，说明是顶级了
					if(deptBean.getParentId0()==0){
						name = deptBean.getName();
					}else{
						name = service.getCargoDept("id="+deptBean.getParentId0()).getName();
					}
				}
				for(int i=0;i<deptList0.size();i++){
					CargoDeptBean dept0=(CargoDeptBean)deptList0.get(i);
					//<option value="<%=dept0.getCode() %>"><%=dept0.getName() %></option>
					str.append("{id:\'").append(dept0.getCode()).append("\',")
					.append("text:\'").append(dept0.getName()).append("\'");
					if(name.equals(dept0.getName())){
						str.append(",selected:\'true\'},");
						boo = true;
					}else{
						str.append("},");
					}
				}
				if(!boo){
					ss+=",selected:\'true\'},";
				}else{
					ss+="},";
				}
				str.deleteCharAt(str.length()-1);
				str.append("]");
				str.insert(0, ss);
//				System.out.println(str.toString());
				//这里将
				request.setAttribute("deptList0", deptList0);
				request.setAttribute("deptList0Select", str.toString());
				//这里的imagehead是没有值得，如果要显示，则加入下面一行
//			csb.setImageHead(Constants.STAFF_PHOTO_URL);
				request.setAttribute("csb", csb);
				request.setAttribute("cargoDeptList", cargoDeptList);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return "forward:/admin/rec/sys/editStaff.jsp";
	}
	/**
	 * 获取库类型下拉选
	 * 2013-8-26
	 * 朱爱林
	 */
	@RequestMapping("/selectDeptcode")
	@ResponseBody
	public Object selectDeptcode(HttpServletRequest request,HttpServletResponse response) throws Exception{
	
	voUser user = (voUser)request.getSession().getAttribute("userView");
	if(user == null){
		request.setAttribute("tip", "当前没有登录，操作失败！");
		request.setAttribute("result", "failure");
		return "/admin/error";
	}
	List<Map<String,String>> list = new ArrayList<Map<String,String>>();
	Map<String,String> map = new HashMap<String, String>();
	map.put("id", "");
//	map.put("text", "全部");
//	map.put("selected", "true");
	request.setCharacterEncoding("UTF-8");
	int selectIndex=Integer.parseInt(request.getParameter("selectIndex"));//序号，从1开始
	String deptCode = new String(StringUtil.convertNull(request.getParameter("deptCode")).getBytes("ISO-8859-1"),"UTF-8");
	Boolean boo = true;
	DbOperation dbOp = new DbOperation();
	dbOp.init("adult_slave");
	WareService wareService = new WareService(dbOp);
	ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
	try{
		if(selectIndex==0){//员工添加页面，选择一级部门后的查询
			String deptCode0=request.getParameter("deptCode0");
			if(deptCode0==null || deptCode0.length()==0){
				map.put("text", "选择一级部门");
				map.put("selected", "true");
				list.add(map);
				return list;
			}
//			map.put("selected", "true");
			list.add(map);
			CargoDeptBean dept0=service.getCargoDept("code='"+deptCode0+"' and parent_id0=0 and parent_id1=0");//0级部门
			List deptList1=new ArrayList();
			if(dept0!=null){
				deptList1=service.getCargoDeptList("parent_id0="+dept0.getId()+" and parent_id1=0", -1, -1, null);
			}
			if(deptList1.size()>0){
				for(int i=0;i<deptList1.size();i++){
					map = new HashMap<String, String>();
					CargoDeptBean dept1=(CargoDeptBean)deptList1.get(i);
					map.put("id",dept1.getCode());
					map.put("text", dept1.getName());
					if(deptCode.equals(dept1.getName())){
						boo = false;
						map.put("selected", "true");
					}
					list.add(map);
				}
				map = new HashMap<String, String>();
				map.put("id", "");
				map.put("text", "选择一级部门");
				if(!boo){
					//说明有与下拉选匹配的值，这里设置默认选中
					map.put("selected", "true");
				}
				list.add(0,map);
			}
//			request.setAttribute("deptList1", deptList1);
		} else if (selectIndex==1){//员工添加页面，选择一级部门后的查询
			String deptCode0 = request.getParameter("deptCode0");
			String deptCode1=request.getParameter("deptCode1");
			if(deptCode1==null || deptCode1.length()==0){
				map.put("text", "选择二级部门");
				map.put("selected", "true");
				list.add(map);
				return list;
			}
			CargoDeptBean dept0 = service.getCargoDept("code='" +deptCode0 + "' and parent_id0 = 0");
			CargoDeptBean dept1=service.getCargoDept("code='"+deptCode1+"' and parent_id0=" + dept0.getId() + " and parent_id1=0");//一级部门
			List deptList2=new ArrayList();
			if(dept1!=null){
				deptList2=service.getCargoDeptList("parent_id0 = "+ dept0.getId() +" and parent_id1="+dept1.getId()+" and parent_id2=0", -1, -1, null);
			}
			if(deptList2.size()>0){
				for(int i=0;i<deptList2.size();i++){
					map = new HashMap<String, String>();
					CargoDeptBean dept2=(CargoDeptBean)deptList2.get(i);
					map.put("id",dept2.getCode());
					map.put("text", dept2.getName());
					if(deptCode.equals(dept2.getName())){
						boo = false;
						map.put("selected", "true");
					}
					list.add(map);
				}
				map = new HashMap<String, String>();
				map.put("id", "");
				map.put("text", "选择二级部门");
				if(!boo){
					//说明有与下拉选匹配的值，这里设置默认选中
					map.put("selected", "true");
				}
				list.add(0,map);
			}
//			request.setAttribute("deptList2", deptList2);
		}else if(selectIndex==2){//员工添加页面，选择二级部门后的查询
			String deptCode0 = request.getParameter("deptCode0");
			CargoDeptBean dept0 = service.getCargoDept("code='" +deptCode0 + "' and parent_id0 = 0");
			String deptCode1=request.getParameter("deptCode1");
			CargoDeptBean dept1=service.getCargoDept("code='"+deptCode1+"' and parent_id0 = " + dept0.getId() + " and parent_id1=0");//一级部门
			if(dept1!=null){
				String deptCode2=request.getParameter("deptCode2");
				if(deptCode2==null || deptCode2.length()==0){
					map.put("text", "选择三级部门");
					map.put("selected", "true");
					list.add(map);
					return list;
				}
				CargoDeptBean dept2=service.getCargoDept("code='"+deptCode2+"' and parent_id1="+dept1.getId()+" and parent_id2=0");//二级部门
				if(dept2!=null){
					List deptList3=new ArrayList();
					deptList3=service.getCargoDeptList("parent_id0 = "+ dept0.getId() +" and parent_id1=" + dept1.getId() + " and parent_id2="+dept2.getId()+" and parent_id3=0", -1, -1, null);
					if(deptList3.size()>0){
						for(int i=0;i<deptList3.size();i++){
							map = new HashMap<String, String>();
							CargoDeptBean dept3=(CargoDeptBean)deptList3.get(i);
							map.put("id",dept3.getCode());
							map.put("text", dept3.getName());
							if(deptCode.equals(dept3.getName())){
								boo = false;
								map.put("selected", "true");
							}
							list.add(map);
						}
						map = new HashMap<String, String>();
						map.put("id", "");
						map.put("text", "选择三级部门");
						if(!boo){
							//说明有与下拉选匹配的值，这里设置默认选中
							map.put("selected", "true");
						}
						list.add(0,map);
					}
//					request.setAttribute("deptList3", deptList3);
				}
			}
		}else if(selectIndex==3){//时效设置页面，归属部门的查询
			String deptId=request.getParameter("deptId");//一级部门Id
			String name=request.getParameter("name");//二级部门select的name属性
			List deptList=new ArrayList();//二级部门列表
			if(!deptId.equals("")){
				deptList=service.getCargoDeptList("parent_id1="+deptId+" and parent_id2=0", -1, -1, null);
			}
			request.setAttribute("deptList", deptList);
			request.setAttribute("name", name);
		}else if(selectIndex==4){//合格库作业动态明细，每个标签下的筛选查询
			int condition1=StringUtil.StringToId(request.getParameter("condition1"));//作业状态
			int condition2=StringUtil.StringToId(request.getParameter("condition2"));//时效状态
			String date=request.getParameter("date");//日期，yyyy-MM-dd
			int operType=StringUtil.toInt(request.getParameter("operType"));//作业单类型，-1是全部
			int num=StringUtil.toInt(request.getParameter("num"));//标签代号
			
			List qualifiedStockDetailList=new ArrayList();
			String query="";//查询条件

			if(operType==-1){
				query+="type>=0";
			}else{
				query+="type=";
				query+=operType;
			}
			
			if(!date.equals("0")){
				query+=" and confirm_datetime>'";
				query+=date;
				query+=" 00:00:00' and confirm_datetime<'";
				query+=date;
				query+=" 23:59:59'";
			}else{
				query+=" and confirm_datetime>'";
				query+=DateUtil.getNowDateStr();
				query+=" 00:00:00' and confirm_datetime<'";
				query+=DateUtil.getNowDateStr();
				query+=" 23:59:59'";
			}
			if(condition1==1){
				switch (operType) {
				case -1:
					query+=" and status in (1,2,10,11,19,20,28,29)";
					break;
				case 0:
					query+=" and status in (1,2)";
					break;
				case 1:
					query+=" and status in (10,11)";
					break;
				case 2:
					query+=" and status in (19,20)";
					break;
				case 3:
					query+=" and status in (28,29)";
				default:
					break;
				}
				
				if(condition2>0){
					query+=" and effect_status = " + (condition2-1);
				}
			}else if(condition1==2){
				switch (operType) {
				case -1:
					query+=" and status in (3,4,5,6,12,13,14,15,21,22,23,24,30,31,32,33)";
					break;
				case 0:
					query+=" and status in (3,4,5,6)";
					break;
				case 1:
					query+=" and status in (12,13,14,15)";
					break;
				case 2:
					query+=" and status in (21,22,23,24)";
					break;
				case 3:
					query+=" and status in (30,31,32,33)";
				default:
					break;
				}
				
				if(condition2>0){
					query+=" and effect_status = " + (condition2-1);
				}
			}else if(condition1==3){
				switch (operType) {
				case -1:
					query+=" and status in (7,8,9,16,17,18,25,26,27,34,35,36)";
					break;
				case 0:
					query+=" and status in (7,8,9)";
					break;
				case 1:
					query+=" and status in (16,17,18)";
					break;
				case 2:
					query+=" and status in (25,26,27)";
					break;
				case 3:
					query+=" and status in (34,35,36)";
					break;
				default:
					break;
				}
				
				if(condition2>0){
					query+=" and effect_status = " + (condition2-1);
				}
			}
			
			int pageIndex=StringUtil.StringToId(request.getParameter("pageIndex"));//分页页码
			int countPerPage=20;
			PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);//标签‘全部’的分页
			paging.setCurrentPageIndex(pageIndex);
			int totalCount=service.getCargoOperationCount(query.toString());
			paging.setTotalCount(totalCount);
			int totalPageCount=totalCount%countPerPage==0?totalCount/countPerPage:((totalCount-totalCount%countPerPage)/countPerPage+1);
			paging.setTotalPageCount(totalPageCount);
			paging.setPrefixUrl("#");
			if(num==0){
				paging.setJsFunction("shaixuan('0','pageIndex');");
			}else if(num==1){
				paging.setJsFunction("shaixuan('1','pageIndex');");
			}else if(num==2){
				paging.setJsFunction("shaixuan('2','pageIndex');");
			}else if(num==3){
				paging.setJsFunction("shaixuan('3','pageIndex');");
			}else if(num==4){
				paging.setJsFunction("shaixuan('4','pageIndex');");
			}
			request.setAttribute("paging", paging);
			
			qualifiedStockDetailList=service.getCargoOperationList(query, pageIndex*countPerPage, countPerPage, "confirm_datetime desc");
			List cargoOperationCargoList=new ArrayList();
			for(int i = 0; i < qualifiedStockDetailList.size(); i++){
				CargoOperationBean cob = (CargoOperationBean)qualifiedStockDetailList.get(i);
				int id = cob.getId();
				int status = cob.getStatus();
				
				//货位名称
				CargoOperationCargoBean cocb = new CargoOperationCargoBean();
				List cargoOperationCargoList1 = service.getCargoOperationCargoList("out_cargo_whole_code != '' and oper_id =" + id, -1, -1, "id asc");
				if(cargoOperationCargoList1 != null && cargoOperationCargoList1.size() > 0){
					cocb = (CargoOperationCargoBean)cargoOperationCargoList1.get(0); //源货位bean
				}
				List cargoOperationCargoList2 = service.getCargoOperationCargoList("in_cargo_whole_code != '' and oper_id =" + id, -1, -1, "id asc");
				if(cargoOperationCargoList2 != null && cargoOperationCargoList2.size() > 0){
					String inCargo = ((CargoOperationCargoBean)cargoOperationCargoList2.get(0)).getInCargoWholeCode();
					cocb.setInCargoWholeCode(inCargo); //目的货位
				}
				cargoOperationCargoList.add(cocb);
				
				//作业状态名称
				CargoOperationProcessBean copb = service.getCargoOperationProcess("id =" + status); 
				if(copb != null){
					cob.setStatusName(StringUtil.convertNull(copb.getStatusName())); 
				}
			}
			
			request.setAttribute("qualifiedStockDetailList", qualifiedStockDetailList);
			request.setAttribute("cargoOperationCargoList", cargoOperationCargoList);
		}else if(selectIndex==5){//待作业管理
//			int num=StringUtil.StringToId(request.getParameter("num"));
//			if(num==0){//待补货
//				List refillList=refillTodo(mapping, form, request, response, wareService.getDbOp());
//				request.setAttribute("refillList", refillList);
//			}else if(num==1){//待上架
//				List upShelfList=upShelfTodo(mapping, form, request, response, wareService.getDbOp());
//				request.setAttribute("upShelfList", upShelfList);
//			}else if(num==2){//待下架
//				List downShelfList=downShelfTodo(mapping, form, request, response, wareService.getDbOp());
//				request.setAttribute("downShelfList", downShelfList);
//			}else if(num==3){//待调拨
//				List exchangeList=exchangeTodo(mapping, form, request, response, wareService.getDbOp());
//				request.setAttribute("exchangeList", exchangeList);
//			}
		}
		
	}catch (Exception e) {
		e.printStackTrace();
	}finally{
		service.releaseAll();
	}
	return list;
	}
	/**
	 * 保存员工资料
	 * 2013-8-29
	 * 朱爱林	
	 */
	@RequestMapping("/qualifiedStock")
	public void qualifiedStock(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if (user == null) {
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		AdminService adminService = new AdminService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		try {
			//保存修改后的员工档案
			synchronized(cargoLock) {
				String photoUrl = StringUtil.convertNull(request.getParameter("photoUrl"));
				service.getDbOp().startTransaction();
				int id = 0;
				String name = StringUtil.convertNull(request.getParameter("nameStr").trim());
				String phone = StringUtil.convertNull(request.getParameter("phone"));
				int userId = 0;
				String userName = StringUtil.convertNull(request.getParameter("userName").trim());
				String deptCode0 = "00";
				String deptCode1 = "00";
				String deptCode2 = "00";
				String deptCode3 = "00";
				
				if(request.getParameter("id") != null){
					id = StringUtil.toInt(request.getParameter("id"));
				}
				if(!"".equals(userName)){
					if(adminService.getAdmin(userName) != null){
						userId = adminService.getAdmin(userName).getId();
					}else{
//							request.setAttribute("tip", "后台帐户输入不正确！");
//							request.setAttribute("result", "failure");
//							return mapping.findForward(IConstants.FAILURE_KEY);
						result.append("{result:'failure',tip:'后台帐户输入不正确！'}");
						response.getWriter().write(result.toString());
						return;
					}
					CargoStaffBean csTemp = service.getCargoStaff("status=0 and user_name='" + userName + "'");
					if( csTemp != null && csTemp.getId() != id  ) {
//							request.setAttribute("tip", "该后台账户已经被别的物流员工使用了！");
//							request.setAttribute("result", "failure");
//							return mapping.findForward(IConstants.FAILURE_KEY);
						result.append("{result:'failure',tip:'该后台账户已经被别的物流员工使用了！'}");
						response.getWriter().write(result.toString());
						return;
					}
				}
				if (!"".equals(request.getParameter("deptCode0"))) {
					deptCode0 = request.getParameter("deptCode0");
				}
				if (!"".equals(request.getParameter("deptCode1"))) {
					deptCode1 = request.getParameter("deptCode1");
				}
				if (!"".equals(request.getParameter("deptCode2"))) {
					deptCode2 = request.getParameter("deptCode2");
				} 
				if (!"".equals(request.getParameter("deptCode3"))) {
					deptCode3 = request.getParameter("deptCode3");
				}
				
				int deptId0 = 0;//员工所属0级部门id
				int deptId1 = 0;//员工所属1级部门id
				int deptId2 = 0;//员工所属2级部门id
				int deptId3 = 0;//员工所属3级部门id
				CargoDeptBean cargoDept0 = service.getCargoDept("code='" + deptCode0 + "' and parent_id0=0 and parent_id1=0 and parent_id2=0 and parent_id3=0");
				if (cargoDept0 != null) {
					deptId0 = cargoDept0.getId();
					CargoDeptBean cargoDept1 = service.getCargoDept("code='" + deptCode1 + "' and parent_id0=" + deptId0 + " and parent_id1=0 and parent_id2=0 and parent_id3=0");
					if (cargoDept1 != null) {
						deptId1 = cargoDept1.getId();
						CargoDeptBean cargoDept2 = service.getCargoDept("code='" + deptCode2 + "' and parent_id0=" + deptId0 + " and parent_id1=" + deptId1 + " and parent_id2=0 and parent_id3=0");
						if (cargoDept2 != null) {
							deptId2 = cargoDept2.getId();
							CargoDeptBean cargoDept3 = service.getCargoDept("code='" + deptCode3 + "' and parent_id0=" + deptId0 + " and parent_id1=" + deptId1 + " and parent_id2=" + deptId2 + " and parent_id3=0");
							if (cargoDept3 != null) {
								deptId3 = cargoDept3.getId();
							}else if(deptCode3.equals("00")){//如果查询不到3级部门，并且员工3级部门编号为"00"则员工所属部门id为2级部门id
								deptId3 = cargoDept2.getId();
							}
						} else if(deptCode2.equals("00")){//如果查询不到2级部门，并且员工2级部门编号为"00"则员工所属部门id为1级部门id
							deptId3 = cargoDept1.getId();
						}
					} else if(deptCode1.equals("00")){//如果查询不到1级部门，并且员工1级部门编号为"00"则员工所属部门id为0级部门id
						deptId3 = cargoDept0.getId();
					}
				}
				String set = "name ='" + name + "',dept_id =" + deptId3 + ",phone ='" + phone + "',user_id=" + userId + ",user_name='" + userName + "'";
				if( !photoUrl.equals("") && !photoUrl.equals("null") ) {
					set += ", photo_url='" + photoUrl + "'";
				} 
				service.updateCargoStaff(set, " id =" + id);
				service.getDbOp().commitTransaction();
			}
			result.append("{result:'success',tip:'保存成功！'}");
			response.getWriter().write(result.toString());
			return;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	}
	/**
	 * 删除物流员工
	 * 2013-8-29
	 * 朱爱林
	 */
	@RequestMapping("/delStaff")
	public void delStaff(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if (user == null) {
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		try {
			synchronized ( cargoLock) {
				service.getDbOp().startTransaction();
				int id =0;
				if(request.getParameter("staffId") != null){
					id = StringUtil.toInt(request.getParameter("staffId"));
				}
				CargoStaffBean csBean = service.getCargoStaff("id=" + id );
				if( csBean == null ) {
//					request.setAttribute("tip", "没有找到要删除的用户信息，操作失败！");
//					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
//					return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'没有找到要删除的用户信息，操作失败！'}");
					response.getWriter().write(result.toString());
					return;
				} else {
					if( csBean.getPhotoUrl() != null && !csBean.getPhotoUrl().equals("")) {
						String localHead = Constants.WARE_UPLOAD;
						String allPath = localHead + csBean.getPhotoUrl();
						if( allPath.indexOf("ware/staffPhoto") != -1 ) {
							   boolean results = false;
							   File image = new File(allPath);
							   if( image.exists() ) {
								  results = image.delete();
							   } else {
								   results = true;
							   }
							if( !results ) {
//								request.setAttribute("tip", "头像图片删除失败");
//								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
//								return mapping.findForward(IConstants.FAILURE_KEY);
								result.append("{result:'failure',tip:'头像图片删除失败！'}");
								response.getWriter().write(result.toString());
								return;
							}
						} else {
//							request.setAttribute("tip", "图片路径有错误");
//							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
//							return mapping.findForward(IConstants.FAILURE_KEY);
							result.append("{result:'failure',tip:'图片路径有错误！'}");
							response.getWriter().write(result.toString());
							return;
						}
					}
					if( !service.updateCargoStaff("status=1", "id =" + id) ) {
//						request.setAttribute("tip", "删除用户信息时，数据库操作失败！");
//						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
//						return mapping.findForward(IConstants.FAILURE_KEY);
						result.append("{result:'failure',tip:'删除用户信息时，数据库操作失败！'}");
						response.getWriter().write(result.toString());
						return;
					}
				}
				service.getDbOp().commitTransaction();
				service.getDbOp().getConn().setAutoCommit(true);
				result.append("{result:'success',tip:'删除成功！'}");
				response.getWriter().write(result.toString());
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	}
	/**
	 * 恢复物流员工
	 * 2013-8-29
	 * 朱爱林
	 */
	@RequestMapping("/recoverStaff")
	public void recoverStaff(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if (user == null) {
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized ( cargoLock) {
				service.getDbOp().startTransaction();
				int id =StringUtil.toInt(request.getParameter("id"));
				service.updateCargoStaff("status=0", "id="+id);
				service.getDbOp().commitTransaction();
				service.getDbOp().getConn().setAutoCommit(true);
				result.append("{result:'success',tip:'恢复成功！'}");
				response.getWriter().write(result.toString());
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	}
	/**
	 * 添加员工跳转到页面
	 * 2013-8-29
	 * 朱爱林
	 */
	@RequestMapping("/addStaffJsp")
	public String addStaffJsp(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser) request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		try{
				List deptList0=service.getCargoDeptList("parent_id0 = 0 and parent_id1=0", -1, -1, null);
				StringBuilder str = new StringBuilder();
				str.append("[{id:\'\',text:\'选择所属地区\',selected:'true'},");
				for(int i=0;i<deptList0.size();i++){
					CargoDeptBean dept0=(CargoDeptBean)deptList0.get(i);
					//<option value="<%=dept0.getCode() %>"><%=dept0.getName() %></option>
					str.append("{id:\'").append(dept0.getCode()).append("\',")
					.append("text:\'").append(dept0.getName()).append("\'},");
				}
				str.deleteCharAt(str.length()-1);
				str.append("]");
				request.setAttribute("deptList0Select", str.toString());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return "forward:/admin/rec/sys/addStaff.jsp";
	}
	/**
	 * 添加员工第一步
	 * 2013-8-29
	 * 朱爱林
	 */
	@RequestMapping("/addStaff")
	public void addStaff(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if (user == null) {
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		int id = 0;
		WareService wareService = new WareService();
		AdminService adminService = new AdminService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized(cargoLock) {
				service.getDbOp().startTransaction();
				String name = "";
				String code = "";
				String createDatetime = "";
				String phone = "";
				int userId = 0;
				String userName = "";
				String deptCode0 = "00";
				String deptCode1 = "00";
				String deptCode2 = "00";
				String deptCode3 = "00";
				if (!"".equals(request.getParameter("deptCode0"))) {
					deptCode0 = request.getParameter("deptCode0");
				}
				if (!"".equals(request.getParameter("deptCode1"))) {
					deptCode1 = request.getParameter("deptCode1");
				}
				if (!"".equals(request.getParameter("deptCode2"))) {
					deptCode2 = request.getParameter("deptCode2");
				}
				if (!"".equals(request.getParameter("deptCode3"))) {
					deptCode3 = request.getParameter("deptCode3");
				}
				CargoDeptBean cdb0 = service.getCargoDept("parent_id0 = 0 and code =" + deptCode0);
				int deptId =0;
				int id0 = cdb0.getId();
				deptId=cdb0.getId();
				if(!"00".equals(deptCode1)){
					CargoDeptBean cdb1 = service.getCargoDept("parent_id0 = " + id0 + " and parent_id1 = 0 and code =" + deptCode1);
					int id1 = cdb1.getId();
					deptId=cdb1.getId();
					if(!"00".equals(deptCode2)){
						CargoDeptBean cdb2 = service.getCargoDept("parent_id1 =" + id1 + " and parent_id2 = 0 and code =" + deptCode2);
						int id2 = cdb2.getId();
						deptId=cdb2.getId();
						if(!"00".equals(deptCode3)){
							CargoDeptBean cdb3 = service.getCargoDept("parent_id1 =" + id1 + " and parent_id2 =" + id2 + " and parent_id3 = 0 and code =" + deptCode3);
							deptId=cdb3.getId();
						}
					}
				}
				
				if (request.getParameter("nameStr") != null && 
						request.getParameter("nameStr").trim() !="") {
					name = request.getParameter("nameStr").trim();
				}
				createDatetime = DateUtil.getNow();
				if (request.getParameter("phone") != null) {
					phone = "输入电话号码".equals(request.getParameter("phone")) ? "" : request.getParameter("phone").trim();
				}
				if (request.getParameter("userName") != null) {
					String tempUserName = "输入账号...".equals(request.getParameter("userName")) ? "" : request.getParameter("userName").trim();
					if(!"".equals(tempUserName)){
						if(adminService.getAdmin(tempUserName) != null){
							userId = adminService.getAdmin(tempUserName).getId();
							userName = tempUserName;
						}else{
//								request.setAttribute("tip", "后台帐户输入不正确！");
//								request.setAttribute("result", "failure");
//								return mapping.findForward(IConstants.FAILURE_KEY);
							result.append("{result:'failure',tip:'后台帐户输入不正确！'}");
							response.getWriter().write(result.toString());
							return;
						}
						if( service.getCargoStaff("status=0 and user_name='" + tempUserName + "'") != null ) {
//								request.setAttribute("tip", "该后台账户已经被别的物流员工使用了！");
//								request.setAttribute("result", "failure");
//								return mapping.findForward(IConstants.FAILURE_KEY);
							result.append("{result:'failure',tip:'该后台账户已经被别的物流员工使用了！'}");
							response.getWriter().write(result.toString());
							return;
						}
						else if( service.getCargoStaff("status=1 and user_name='" + tempUserName + "'") != null ) {
							CargoStaffBean staffBean = service.getCargoStaff("status=1 and user_name='" + tempUserName + "'");
							service.updateCargoStaff("status=0,dept_id="+deptId+",phone='"+phone+"',name='"+name+"'", "user_name='" + tempUserName + "'");
//								return new ActionForward("/admin/qualifiedStock.do?method=editStaff&id="+staffBean.getId()+"&type=2");
							result.append("{result:'success',tip:'").append(staffBean.getId()).append("'}");
							response.getWriter().write(result.toString());
							return;
						}
					}
				}
				// 拼接出合适员工编号
				
				String condition = "code like '" + deptCode0 + deptCode1 + deptCode2 + deptCode3 + "%' and length(code)=12 ";
				String subCode = "0001";	//员工编号末两位
				int intSubCode = 0;
				
				CargoStaffBean staffBean = service.getCargoStaff(condition + "  ORDER BY code DESC LIMIT 1");
				if (staffBean != null) {
					int num = StringUtil.StringToId(staffBean.getCode().substring(8, staffBean.getCode().length()));
					intSubCode = num + 1;
					if (intSubCode < 10) {
						subCode = "000" + intSubCode;
					} else if (10 <= intSubCode && intSubCode <= 99) {
						subCode = "00" + intSubCode;
					} else if (100 <= intSubCode && intSubCode <= 999) {
						subCode = "0" + intSubCode;
					} else {
						subCode = String.valueOf(intSubCode);
					}
				}

				code = deptCode0 + deptCode1 + deptCode2 + deptCode3 + subCode;

				if (!"".equals(name)) {
					CargoStaffBean csb = new CargoStaffBean();
					csb.setName(name);
					csb.setCode(code);
					csb.setDeptId(deptId);
					csb.setCreateDatetime(createDatetime);
					csb.setPhone(phone);
					csb.setUserId(userId);
					csb.setUserName(userName);
					service.addCargoStaff(csb);
				}
				id = service.getDbOp().getLastInsertId();
				service.getDbOp().commitTransaction();
				service.getDbOp().getConn().setAutoCommit(true);
				result.append("{result:'success',tip:'").append(id).append("'}");
				response.getWriter().write(result.toString());
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
			wareService.releaseAll();
		}
//		return new ActionForward("/admin/qualifiedStock.do?method=editStaff&id="+id+"&type=2");
	}
	/**
	 * 添加员工下一步的页面跳转
	 * 2013-8-29
	 * 朱爱林
	 */
	@RequestMapping("/addStaffNextJsp")
	public String addStaffNextJsp(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		List cargoDeptList = new ArrayList();
		CargoStaffBean csb = null;
		try{
			if(request.getParameter("id") != null){ 
				int id = StringUtil.toInt(request.getParameter("id"));
				csb = service.getCargoStaff("id =" + id);
				CargoDeptBean deptBean = service.getCargoDept("id="+csb.getDeptId());
				if(deptBean!=null){
					if( deptBean.getParentId0()!=0){
						cargoDeptList.add(service.getCargoDept("id="+deptBean.getParentId0()));
						if(deptBean.getParentId1()!=0){
							cargoDeptList.add(service.getCargoDept("id="+deptBean.getParentId1()));
							if(deptBean.getParentId2()!=0){
								cargoDeptList.add(service.getCargoDept("id="+deptBean.getParentId2()));
								cargoDeptList.add(service.getCargoDept("id="+deptBean.getId()));
							}
							else{
								cargoDeptList.add(service.getCargoDept("id="+deptBean.getId()));	
							}
						}else{
							cargoDeptList.add(service.getCargoDept("id="+deptBean.getId()));	
						}
					}else{
						cargoDeptList.add(service.getCargoDept("id="+deptBean.getId()));	
					}
				}
			}
			request.setAttribute("csb", csb);
			request.setAttribute("cargoDeptList", cargoDeptList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return "forward:/admin/rec/sys/addStaffNext.jsp";
	}
	/**
	 * 添加员工中的添加头像后保持
	 * 2013-8-30
	 * 朱爱林
	 */
	@RequestMapping("/addStaffNext")
	public void addStaffNext(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if (user == null) {
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		
		int id = StringUtil.parstInt(request.getParameter("id"));
		String photoUrl = StringUtil.convertNull(request.getParameter("photoUrl"));
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized(cargoLock) {
				if( !photoUrl.equals("") && !photoUrl.equals("null")) {
					service.getDbOp().startTransaction();
					CargoStaffBean csBean = service.getCargoStaff("id=" + id);
					if( csBean == null ) {
//						request.setAttribute("tip", "找不到用户信息，操作失败！");
						service.getDbOp().rollbackTransaction();
//						request.setAttribute("result", "failure");
//						return mapping.findForward(IConstants.FAILURE_KEY);
						result.append("{result:'failure',tip:'找不到用户信息，操作失败！'}");
						response.getWriter().write(result.toString());
						return;
					} else {
						if( !service.updateCargoStaff("photo_url='" + photoUrl + "'", "id=" + id)) {
//							request.setAttribute("tip", "保存图片时，数据库操作失败！");
							service.getDbOp().rollbackTransaction();
//							request.setAttribute("result", "failure");
//							return mapping.findForward(IConstants.FAILURE_KEY);
							result.append("{result:'failure',tip:'保存图片时，数据库操作失败！'}");
							response.getWriter().write(result.toString());
							return;
						}
					}
					service.getDbOp().commitTransaction();
					service.getDbOp().getConn().setAutoCommit(true);
				}
				result.append("{result:'success',tip:'添加成功！'}");
				response.getWriter().write(result.toString());
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	}
	/**
	 * 部门地区库管理
	 * 2013-8-30
	 * 朱爱林	
	 */
	@RequestMapping("/departmentAreaStockType")
	public String departmentAreaStockType(HttpServletRequest request ,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if (user == null) {
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return null;
		}
		String deptId = StringUtil.convertNull(request.getParameter("id"));
		String deptId2 = StringUtil.convertNull(request.getParameter("id2"));
		String deptId3 = StringUtil.convertNull(request.getParameter("id3"));
		String deptId4 = StringUtil.convertNull(request.getParameter("id4"));
		String deptName =new String();
		String deptName2 =new String();
		String deptName3 =new String();
		String deptName4 =new String();
		String deptNameFull =new String();
		try {
			
			// 查询出各级部门的名称，拼接成最终部门名称
			CargoDeptBean cargoDeptBean =null;
			if( deptId!=null && deptId.length()>0) {
				CargoDeptBean cdb0 = mCargoDeptService.getCargoDept("id="+deptId);
				cargoDeptBean = mCargoDeptService.getCargoDept("id="+deptId);
				deptName = cdb0.getName();
				if(deptId2!=null && deptId2.length()>0){
					CargoDeptBean cdb1 = mCargoDeptService.getCargoDept("id="+deptId2);
					cargoDeptBean = mCargoDeptService.getCargoDept("id="+deptId2);
					deptName2 = cdb1.getName();
					if(deptId3!=null && deptId3.length()>0){
						CargoDeptBean cdb2 = mCargoDeptService.getCargoDept("id="+deptId3);
						cargoDeptBean = mCargoDeptService.getCargoDept("id="+deptId3);
						deptName3 = cdb2.getName();
						if(deptId4!=null && deptId4.length()>0){
							CargoDeptBean cdb3 = mCargoDeptService.getCargoDept("id="+deptId4);
							cargoDeptBean = mCargoDeptService.getCargoDept("id="+deptId4);
							deptName4 = cdb3.getName();
						}
					}
				}
			}
			
		    deptNameFull = deptName + deptName2 + deptName3 + deptName4;
			HashMap<Integer, List<CargoDeptAreaBean>> map = new HashMap<Integer, List<CargoDeptAreaBean>>();
			List stockList = null;

			if (cargoDeptBean!=null) {
				
				HashMap<Integer,String> areaMap = ProductStockBean.areaMap;
				// 该部门库地区所对应的列表
				for (int key : areaMap.keySet()) {
					List areaList = mCargoDeptAreaService.getCargoDeptAreaListSlave("area=" + key + " and dept_id=" + cargoDeptBean.getId(), -1, -1, "id");
					if (areaList != null) {
						stockList = new ArrayList();
						for (int i = 0; i < areaList.size(); i++) {
							CargoDeptAreaBean cdaBean = (CargoDeptAreaBean) areaList.get(i);
							stockList.add(cdaBean.getStockType());
						}
						map.put(key, stockList);
					}
				}
				request.setAttribute("deptId", cargoDeptBean.getId()+"");
			}
			request.setAttribute("deptNameFull", deptNameFull);
		    request.setAttribute("map", map);
			request.setAttribute("deptId1", deptId);
			request.setAttribute("deptId2", deptId2);
			request.setAttribute("deptId3", deptId3);
			request.setAttribute("deptId4", deptId4);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return "forward:/admin/rec/sys/departmentAreaStockType.jsp";
	}
	/**
	 * 部门地区库类型 修改提交
	 * 2013-8-30
	 * 朱爱林
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws Exception 
	 */
	@RequestMapping("/assignAreaStockType")
	public String assignAreaStockType(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		String deptId = StringUtil.convertNull(request.getParameter("deptId"));
		String id = StringUtil.convertNull(request.getParameter("id"));
		String id2 = StringUtil.convertNull(request.getParameter("id2"));
		String id3 = StringUtil.convertNull(request.getParameter("id3"));
		String id4 = StringUtil.convertNull(request.getParameter("id4"));
		String parm ="";
		if(id!=null&&id.length()>0){
			parm="id="+id;
		}
		if(id2!=null&&id2.length()>0){
			parm="id2="+id2;
		}
		if(id3!=null&&id3.length()>0){
			parm="id3="+id3;
		}
		if(id4!=null&&id4.length()>0){
			parm="id4="+id4;
		}
		try {
			mCargoDeptAreaService.deleteAndAddCargoDeptArea(request, deptId);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("msg", "异常！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		} finally {
		}
		return "forward:/CargoController/departmentAreaStockType.mmx?"+parm;
	}
	/**
	 * 维护组织结构--获取所有部门列表
	 * 2013-9-03
	 * 朱爱林
	 */
	@RequestMapping("/staffManagementOther")
	public void staffManagementOther(HttpServletRequest request,HttpServletResponse response)
		throws Exception{

		voUser user = (voUser) request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if (user == null) {
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return "/admin/error";
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String address = StringUtil.convertNull(request.getParameter("address"));
		try {
			List deptList=service.getCargoDeptList("parent_id0=0", -1, -1, null);//零级部门列表
			for(int i=0;i<deptList.size();i++){
				CargoDeptBean cd=(CargoDeptBean)deptList.get(i);
				List deptList2=service.getCargoDeptList("parent_id0="+cd.getId()+" and parent_id1=0", -1, -1, null);//该部门的一级部门列表
				cd.setJuniorDeptList(deptList2);
				for(int j=0;j<deptList2.size();j++){
					CargoDeptBean cd2=(CargoDeptBean)deptList2.get(j);
					List deptList3=service.getCargoDeptList("parent_id1="+cd2.getId()+" and parent_id2=0", -1, -1, null);//该部门的二级部门列表
					cd2.setJuniorDeptList(deptList3);
					for(int k=0;k<deptList3.size();k++){
						CargoDeptBean cd3=(CargoDeptBean)deptList3.get(k);
						List deptList4=service.getCargoDeptList("parent_id2="+cd3.getId()+" and parent_id3=0", -1, -1, null);//该部门的三级部门列表
						cd3.setJuniorDeptList(deptList4);
					}
				}
			}
			String root = request.getContextPath();
			StringBuilder json = new StringBuilder();
			json.append("[");
//			<%=cd.getName() %>&nbsp;
//			<%=cd.getCode() %>&nbsp;
//			<a href="qualifiedStock.do?method=deptManagement&deptId=<%=cd.getId() %>">下级部门</a>&nbsp;
//			<a href="qualifiedStock.do?method=deleteDept&deptId=<%=cd.getId() %>">删除部门</a>
			for(int i=0;i<deptList.size();i++){
				CargoDeptBean cd=(CargoDeptBean)deptList.get(i); 
				int id = cd.getId();
				json.append("{id:\'").append(id).append("\',")
					.append("text:\'").append(cd.getName()+"&nbsp;")
					.append(cd.getCode()+"&nbsp;").append("<a onclick=\"nextDept("+cd.getId()+")\">下级部门</a>&nbsp;")
					.append("<a onclick=\"deleteDept("+cd.getId()+")\">删除部门</a>")
					.append("\',")
					.append("iconCls:\'icon-ok\'");
				List deptList2=cd.getJuniorDeptList();
				if(deptList2!=null&&deptList2.size()>0){
					json.append(",children:[");
				}
				for(int j=0;j<deptList2.size();j++){
					CargoDeptBean cd2=(CargoDeptBean)deptList2.get(j);
					int id2 = cd2.getId();
					json.append("{id:\'").append(id2).append("\',")
					.append("text:\'").append(cd2.getName()+"&nbsp;")
					.append(cd2.getCode()+"&nbsp;").append("<a onclick=\"nextDept("+cd2.getId()+")\">下级部门</a>&nbsp;")
					.append("<a onclick=\"deleteDept("+cd2.getId()+")\">删除部门</a>")
					.append("\',")
					.append("iconCls:\'icon-ok\'");
					List deptList3=cd2.getJuniorDeptList();
					if(deptList3!=null&&deptList3.size()>0){
						json.append(",children:[");
					}
					for(int k=0;k<deptList3.size();k++){
						CargoDeptBean cd3=(CargoDeptBean)deptList3.get(k);
						int id3 = cd3.getId();
						json.append("{id:\'").append(id3).append("\',")
						.append("text:\'").append(cd3.getName()+"&nbsp;")
						.append(cd3.getCode()+"&nbsp;").append("<a onclick=\"nextDept("+cd3.getId()+")\">下级部门</a>&nbsp;")
						.append("<a onclick=\"deleteDept("+cd3.getId()+")\">删除部门</a>")
						.append("\',")
						.append("iconCls:\'icon-ok\'");
						List deptList4=cd3.getJuniorDeptList();
						if(deptList4!=null&&deptList4.size()>0){
							json.append(",children:[");
						}
						for(int l=0;l<deptList4.size();l++){
							CargoDeptBean cd4=(CargoDeptBean)deptList4.get(l);
							int id4 = cd4.getId();
							json.append("{id:\'").append(id4).append("\',")
							.append("text:\'").append(cd4.getName()+"&nbsp;")
							.append(cd4.getCode()+"&nbsp;")
							.append("<a onclick=\"deleteDept("+cd4.getId()+")\">删除部门</a>")
							.append("\',")
							.append("iconCls:\'icon-ok\'");
							if(l==deptList4.size()-1){
								json.append("}");
							}else{
								json.append("},");
							}
						}
						if(deptList4!=null&&deptList4.size()>0){
							json.append("]");
						}
						if(k==deptList3.size()-1){
							json.append("}");
						}else{
							json.append("},");
						}
					}
					if(deptList3!=null&&deptList3.size()>0){
						json.append("]");
					}
					if(j==deptList2.size()-1){
						json.append("}");
					}else{
						json.append("},");
					}
				}
				if(deptList2!=null&&deptList2.size()>0){
					json.append("]");
				}
				if(i==deptList.size()-1){
					json.append("}");
				}else{
					json.append("},");
				}
			}
			json.append("]");
//			System.out.println(json);
			result.append("{result:'success',tip:"+json.toString()+"}");
			response.getWriter().write(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	}
	/**
	 * 获取部门设置列表
	 * 2013-9-3
	 * Administrator	
	 * @return
	 */
	@RequestMapping("/deptDetailList")
	@ResponseBody
	public Object deptDetailList(HttpServletRequest request,HttpServletResponse response){
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		
		String deptId=request.getParameter("deptId");//部门Id
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		Map<String,Object> resultMap = new HashMap<String, Object>();
		List<Map<String,String>> lists = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		resultMap.put("total","0");
		try{
			CargoDeptBean dept=null;
			if(deptId!=null){
				dept=service.getCargoDept("id="+deptId);
				if(dept==null){
					request.setAttribute("tip", "无此部门！");
					request.setAttribute("result", "failure");
					return resultMap;
				}
			}else{
				dept=service.getCargoDept("parent_id0=0");//零级分类，物流中心
			}
			List detailDeptList=new ArrayList();//右边可更改的部门列表
			if(dept!=null){
				if(dept.getParentId0()==0){//零级分类
					detailDeptList=service.getCargoDeptList("parent_id0="+dept.getId()+" and parent_id1=0", -1, -1, null);
				}else if(dept.getParentId1()==0){//一级分类
					detailDeptList=service.getCargoDeptList("parent_id1="+dept.getId()+" and parent_id2=0", -1, -1, null);
				}else if(dept.getParentId2()==0){//二级分类
					detailDeptList=service.getCargoDeptList("parent_id2="+dept.getId()+" and parent_id3=0", -1, -1, null); 
				}else if(dept.getParentId3()==0){//三级分类
					detailDeptList=service.getCargoDeptList("parent_id3="+dept.getId(), -1, -1, null); 
				}
			}
			for(int i=0;i<detailDeptList.size();i++){
				CargoDeptBean cd=(CargoDeptBean)detailDeptList.get(i);
				map = new HashMap<String, String>();
				map.put("cargo_dept_name",cd.getName());//部门名称
				map.put("cargo_dept_code",cd.getCode());//部门代码
				lists.add(map);
			}
			resultMap.put("rows",lists);
			String title = "";
			if(dept==null||dept.getParentId0()==0){
				title = "一级部门设置";
			}else if(dept.getParentId1()==0){
				title = "二级部门设置";
			}else if(dept.getParentId2()==0){
				title = "三级部门设置";
			}
			resultMap.put("title", title);
			resultMap.put("deptId", deptId);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return resultMap;
	}
	/**
	 * 修改维护组织结构
	 * 2013-9-3
	 * 朱爱林	
	 * @return
	 */
	@RequestMapping("/updateDept")
	public void updateDept(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		
		String deptId=StringUtil.convertNull(request.getParameter("deptId"));//上级部门Id
		String[] nameList=request.getParameterValues("deptName");//部门名称列表
		String[] codeList=request.getParameterValues("deptCode");//部门代码列表
		
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			service.getDbOp().startTransaction();
			List deptList=new ArrayList();
			CargoDeptBean dept=null;//上级部门
			if("".equals(deptId)){//一级分类列表
				dept=service.getCargoDept("parent_id0=0");//零级分类，物流中心
			}else{
				dept=service.getCargoDept("id="+deptId);
			}
			if(dept==null){
//				request.setAttribute("tip", "上级部门不存在，无法修改！");
//				request.setAttribute("result", "failure");
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'上级部门不存在，无法修改！'}");
				response.getWriter().write(result.toString());
				return;
			}
			if(dept.getParentId0()==0){//零级分类
				deptList=service.getCargoDeptList("parent_id0="+dept.getId()+" and parent_id1=0", -1, -1, "id asc");
			}else if(dept.getParentId0()!=0&&dept.getParentId1()==0){//一级分类
				deptList=service.getCargoDeptList("parent_id1="+deptId+" and parent_id2=0", -1, -1, "id asc");
			}else if(dept.getParentId0()!=0&&dept.getParentId1()!=0&&dept.getParentId2()==0){//二级分类
				deptList=service.getCargoDeptList("parent_id2="+deptId+" and parent_id3=0", -1, -1, "id asc");
			}
			
			for(int i=0;i<(nameList.length>deptList.size()?nameList.length:deptList.size());i++){
				String name="";//输入框的中输入的第i个部门名字
				String code="";//输入框的中输入的第i个部门代码
				CargoDeptBean cargoDept=null;//数据库中的第i个部门
				if(nameList.length>=i+1){
					name=nameList[i];
					code=codeList[i];
				}
				if(deptList.size()>=i+1){
					cargoDept=(CargoDeptBean)deptList.get(i);
				}
				if(name.equals("")&&cargoDept!=null){//输入框清空，数据库里有值，应删除数据库中该部门记录
					//删除部门
					int parentId0=cargoDept.getParentId0();
					int parentId1=cargoDept.getParentId1();
					int parentId2=cargoDept.getParentId2();
					int parentId3=cargoDept.getParentId3();
					String staffCode="";
					if(parentId0!=0){//有零级上级部门
						CargoDeptBean dept0=service.getCargoDept("id="+parentId0);
						if(dept0==null){
//							request.setAttribute("tip", "零级部门不存在，操作失败！");
//							request.setAttribute("result", "failure");
//							return mapping.findForward(IConstants.FAILURE_KEY);
							result.append("{result:'failure',tip:'零级部门不存在，操作失败！'}");
							response.getWriter().write(result.toString());
							return;
						}
						staffCode+=dept0.getCode();
						if(parentId1!=0){//有一级上级部门
							CargoDeptBean dept1=service.getCargoDept("id="+parentId1);
							if(dept1==null){
//								request.setAttribute("tip", "一级部门不存在，操作失败！");
//								request.setAttribute("result", "failure");
//								return mapping.findForward(IConstants.FAILURE_KEY);
								result.append("{result:'failure',tip:'一级部门不存在，操作失败！'}");
								response.getWriter().write(result.toString());
								return;
							}
							staffCode+=dept1.getCode();
							if(parentId2!=0){//有二级上级部门
								CargoDeptBean dept2=service.getCargoDept("id="+parentId2);
								if(dept2==null){
//									request.setAttribute("tip", "二级级部门不存在，操作失败！");
//									request.setAttribute("result", "failure");
//									return mapping.findForward(IConstants.FAILURE_KEY);
									result.append("{result:'failure',tip:'二级级部门不存在，操作失败！'}");
									response.getWriter().write(result.toString());
									return;
								}
								staffCode+=dept2.getCode();
								if(parentId3!=0){//有三级上级部门
									CargoDeptBean dept3=service.getCargoDept("id="+parentId3);
									if(dept3==null){
//										request.setAttribute("tip", "三级部门不存在，操作失败！");
//										request.setAttribute("result", "failure");
//										return mapping.findForward(IConstants.FAILURE_KEY);
										result.append("{result:'failure',tip:'三级部门不存在，操作失败！'}");
										response.getWriter().write(result.toString());
										return;
									}
									staffCode+=dept3.getCode();
								}
							}
						}
						staffCode+=cargoDept.getCode();
					}else{
						staffCode+="00";
					}
					int cargoStaffCount=service.getCargoStaffCount("status=0 and code like '"+staffCode+"%'");//
					if(cargoStaffCount!=0){
//						request.setAttribute("tip", staffCode+"部门中仍有员工，不能删除");
//						request.setAttribute("result", "failure");
//						return mapping.findForward(IConstants.FAILURE_KEY);
						result.append("{result:'failure',tip:'"+staffCode+"部门中仍有员工，不能删除！'}");
						response.getWriter().write(result.toString());
						return;
					}
					service.deleteCargoDept("id="+cargoDept.getId());
				}else if(!name.equals("")&&cargoDept==null){//输入框有值，数据库无记录，应添加部门记录
					//添加部门
					int parentId0=dept.getParentId0();//上级部门的零级上级部门Id
					int parentId1=dept.getParentId1();
					int parentId2=dept.getParentId2();
					int parentId3=dept.getParentId3();
					CargoDeptBean newDept=new CargoDeptBean();//新部门
					newDept.setName(name);
					newDept.setCode(code);
					if(parentId0==0){//上级部门是零级部门
						newDept.setParentId0(dept.getId());
						newDept.setParentId1(0);
						newDept.setParentId2(0);
						newDept.setParentId3(0);
					}else if(parentId1==0){//上级部门是一级部门
						newDept.setParentId0(dept.getParentId0());
						newDept.setParentId1(dept.getId());
						newDept.setParentId2(0);
						newDept.setParentId3(0);
					}else if(parentId2==0){//上级部门是二级部门
						newDept.setParentId0(dept.getParentId0());
						newDept.setParentId1(dept.getParentId1());
						newDept.setParentId2(dept.getId());
						newDept.setParentId3(0);
					}else if(parentId3==0){//上级部门是三级部门
						newDept.setParentId0(dept.getParentId0());
						newDept.setParentId1(dept.getParentId1());
						newDept.setParentId2(dept.getParentId2());
						newDept.setParentId3(dept.getId());
					}
					/*
					 * 此处添加在修改下级部门时判断当前新增加的code是否在同级目录下重复
					 */
					List sameDeptList=new ArrayList();
					if(newDept.getParentId0()!=0&&newDept.getParentId1()==0){//一级分类
						sameDeptList=service.getCargoDeptList("parent_id0="+newDept.getParentId0()+" and parent_id1=0 and code='"+code+"' and id!="+newDept.getId(), -1, -1, null);
					}else if(newDept.getParentId1()!=0&&newDept.getParentId2()==0){//二级分类
						sameDeptList=service.getCargoDeptList("parent_id1="+newDept.getParentId1()+" and parent_id2=0 and code='"+code+"' and id!="+newDept.getId(), -1, -1, null);
					}else if(newDept.getParentId2()!=0&&newDept.getParentId3()==0){//三级分类
						sameDeptList=service.getCargoDeptList("parent_id2="+newDept.getParentId2()+" and parent_id3=0 and code='"+code+"' and id!="+newDept.getId(), -1, -1, null);
					}
					if(sameDeptList.size()>0){
//						request.setAttribute("tip", "部门编号有重复，修改失败！");
//						request.setAttribute("result", "failure");
//						return mapping.findForward(IConstants.FAILURE_KEY);
						result.append("{result:'failure',tip:'部门编号有重复，修改失败！'}");
						response.getWriter().write(result.toString());
						return;
					}
					List tempDeptList=service.getCargoDeptList("1=1", -1, -1, "id desc");
					if(tempDeptList.size()>0){
						CargoDeptBean tempDept=(CargoDeptBean)tempDeptList.get(0);
						newDept.setId(tempDept.getId()+1);
					}
					service.addCargoDept(newDept);
					
				}else if(!name.equals("")&&cargoDept!=null){//输入框有值，数据库有记录，应修改部门记录
					//修改部门
					List sameDeptList=new ArrayList();
					if(cargoDept.getParentId0()!=0&&cargoDept.getParentId1()==0){//一级分类
						sameDeptList=service.getCargoDeptList("parent_id0="+cargoDept.getParentId0()+" and parent_id1=0 and code='"+code+"' and id!="+cargoDept.getId(), -1, -1, null);
					}else if(cargoDept.getParentId1()!=0&&cargoDept.getParentId2()==0){//二级分类
						sameDeptList=service.getCargoDeptList("parent_id1="+cargoDept.getParentId1()+" and parent_id2=0 and code='"+code+"' and id!="+cargoDept.getId(), -1, -1, null);
					}else if(cargoDept.getParentId2()!=0&&cargoDept.getParentId3()==0){//三级分类
						sameDeptList=service.getCargoDeptList("parent_id2="+cargoDept.getParentId2()+" and parent_id3=0 and code='"+code+"' and id!="+cargoDept.getId(), -1, -1, null);
					}
					if(sameDeptList.size()>0){
//						request.setAttribute("tip", "部门编号有重复，修改失败！");
//						request.setAttribute("result", "failure");
//						return mapping.findForward(IConstants.FAILURE_KEY);
						result.append("{result:'failure',tip:'部门编号有重复，修改失败！'}");
						response.getWriter().write(result.toString());
						return;
					}
					service.updateCargoDept("name='"+name+"',code='"+code+"'", "id="+cargoDept.getId());
					String tempCode="";//该部门完整编号
					CargoDeptBean tempDept0=service.getCargoDept("id="+cargoDept.getParentId0());//零级部门
					if(tempDept0!=null){
						tempCode+=tempDept0.getCode();
					}
					CargoDeptBean tempDept1=service.getCargoDept("id="+cargoDept.getParentId1());//一级部门
					if(tempDept1!=null){
						tempCode+=tempDept1.getCode();
					}
					CargoDeptBean tempDept2=service.getCargoDept("id="+cargoDept.getParentId2());//二级部门
					if(tempDept2!=null){
						tempCode+=tempDept2.getCode();
					}
					CargoDeptBean tempDept3=service.getCargoDept("id="+cargoDept.getParentId3());//三级部门
					if(tempDept3!=null){
						tempCode+=tempDept3.getCode();
					}
					tempCode+=cargoDept.getCode();
					List staffList=service.getCargoStaffList("status=0 and code like '"+tempCode+"%'", -1, -1, null);//该部门下所有员工
					for(int j=0;j<staffList.size();j++){
						CargoStaffBean staff=(CargoStaffBean)staffList.get(j);
						String staffCode=staff.getCode();
						String code1="";
						String code2="";
						String code3="";
						//1234567890
						if(cargoDept.getParentId0()!=0&&cargoDept.getParentId1()==0){//一级分类
							code1=staffCode.substring(0,2);
							code2=code;
							code3=staffCode.substring(4,10);
						}else if(cargoDept.getParentId1()!=0&&cargoDept.getParentId2()==0){//二级分类
							code1=staffCode.substring(0,4);
							code2=code;
							code3=staffCode.substring(6,10);
						}else if(cargoDept.getParentId2()!=0&&cargoDept.getParentId3()==0){//三级分类
							code1=staffCode.substring(0,6);
							code2=code;
							code3=staffCode.substring(8,10);
						}
						//service.updateCargoStaff("code='"+code1+code2+code3+"'", "id="+staff.getId());
					}
				}
			}
			service.getDbOp().commitTransaction();
			result.append("{result:'success',tip:'修改成功！'}");
			response.getWriter().write(result.toString());
			return;
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
	}
	/**
	 * 删除部门
	 * 2013-9-3
	 * 朱爱林
	 */
	@RequestMapping("/deleteDept")
	public void deleteDept(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		
		String deptId=request.getParameter("deptId");//待删除部门Id
		
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			service.getDbOp().startTransaction();
			CargoDeptBean cargoDept=service.getCargoDept("id="+deptId);
			if(cargoDept==null){
//				request.setAttribute("tip", "部门不存在，无法删除！");
//				request.setAttribute("result", "failure");
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'部门不存在，无法删除！'}");
				response.getWriter().write(result.toString());
				return;
			}
			int parentId0=cargoDept.getParentId0();
			int parentId1=cargoDept.getParentId1();
			int parentId2=cargoDept.getParentId2();
			int parentId3=cargoDept.getParentId3();
			String staffCode="";
			StringBuilder sb = new StringBuilder();
			if(parentId0!=0){//有零级上级部门
				sb.append(" parent_id0 = "+parentId0);
				CargoDeptBean dept0=service.getCargoDept("id="+parentId0);
				if(dept0==null){
//					request.setAttribute("tip", "零级部门不存在，操作失败！");
//					request.setAttribute("result", "failure");
//					return mapping.findForward(IConstants.FAILURE_KEY);
					result.append("{result:'failure',tip:'零级部门不存在，操作失败！'}");
					response.getWriter().write(result.toString());
					return;
				}
				staffCode+=dept0.getCode();
				if(parentId1!=0){//有一级上级部门
					sb.append(" and parent_id1 = "+parentId1);
					CargoDeptBean dept1=service.getCargoDept("id="+parentId1);
					if(dept1==null){
//						request.setAttribute("tip", "一级部门不存在，操作失败！");
//						request.setAttribute("result", "failure");
//						return mapping.findForward(IConstants.FAILURE_KEY);
						result.append("{result:'failure',tip:'一级部门不存在，操作失败！'}");
						response.getWriter().write(result.toString());
						return;
					}
					staffCode+=dept1.getCode();
					if(parentId2!=0){//有二级上级部门
						sb.append(" and parent_id2 = "+parentId2);
						CargoDeptBean dept2=service.getCargoDept("id="+parentId2);
						if(dept2==null){
//							request.setAttribute("tip", "二级级部门不存在，操作失败！");
//							request.setAttribute("result", "failure");
//							return mapping.findForward(IConstants.FAILURE_KEY);
							result.append("{result:'failure',tip:'二级级部门不存在，操作失败！'}");
							response.getWriter().write(result.toString());
							return;
						}
						staffCode+=dept2.getCode();
						if(parentId3!=0){//有三级上级部门
							sb.delete(0, sb.length());
							sb.append(" id = "+deptId);//说明没有下级部门了
							CargoDeptBean dept3=service.getCargoDept("id="+parentId3);
							if(dept3==null){
//								request.setAttribute("tip", "三级部门不存在，操作失败！");
//								request.setAttribute("result", "failure");
//								return mapping.findForward(IConstants.FAILURE_KEY);
								result.append("{result:'failure',tip:'三级部门不存在，操作失败！'}");
								response.getWriter().write(result.toString());
								return;
							}
							staffCode+=dept3.getCode();
						}else{
							sb.append(" and parent_id3="+deptId);
						}
					}else{
						sb.append(" and parent_id2="+deptId);
					}
				}else{
					sb.append(" and parent_id1="+deptId);
				}
				staffCode+=cargoDept.getCode();
			}else{
				staffCode+="00";
				sb.append("parent_id0="+deptId);
			}
			/*
			 * 先找子部门下的dept_id，然后找dept_staff
			 */
			List list = service.getCargoDeptList(sb.toString(), -1, -1, " id desc");
			int cargoStaffCount=0;//
			if(list!=null){
				sb.delete(0, sb.length());
				sb.append(" dept_id in (");
				for(int z=0;z<list.size();z++){
					CargoDeptBean csb = (CargoDeptBean) list.get(z);
					sb.append(csb.getId()+",");
				}
				sb.append(deptId+")");
				cargoStaffCount=service.getCargoStaffCount(sb.toString());
			}
//			int cargoStaffCount=service.getCargoStaffCount("code like '"+staffCode+"%'");//
			if(cargoStaffCount!=0){
//				request.setAttribute("tip", staffCode+"部门中仍有员工，不能删除");
//				request.setAttribute("result", "failure");
//				return mapping.findForward(IConstants.FAILURE_KEY);
				result.append("{result:'failure',tip:'"+staffCode+"部门中仍有员工，不能删除！'}");
				response.getWriter().write(result.toString());
				return;
			}
			service.deleteCargoDept("parent_id0="+cargoDept.getId());
			service.deleteCargoDept("parent_id1="+cargoDept.getId());
			service.deleteCargoDept("parent_id2="+cargoDept.getId());
			service.deleteCargoDept("parent_id3="+cargoDept.getId());
			service.deleteCargoDept("id="+cargoDept.getId());
			service.getDbOp().commitTransaction();
			result.append("{result:'success',tip:'删除部门成功！'}");
			response.getWriter().write(result.toString());
			return;
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
	}
}
