package adultadmin.action.cargo;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.stock.cargo.CartonningInfoBean;
import mmb.stock.cargo.CartonningInfoService;
import mmb.stock.cargo.CartonningProductInfoBean;
import mmb.util.excel.ExportExcel;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.bybs.BsbyProductCargoBean;
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
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductCargoBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.StockExchangeBean;
import adultadmin.bean.stock.StockExchangeProductCargoBean;
import adultadmin.framework.IConstants;
import adultadmin.service.IBsByServiceManagerService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.Encoder;
import adultadmin.util.NumberUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;



public class CargoInfoAction extends DispatchAction{
	
	public static byte[] cargoLock = new byte[0];
	
	//城市列表
	public ActionForward cargoInfoCityList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			List cityList=service.getCargoInfoCityList(null, -1, -1, "code asc");
			List areaCountList=new ArrayList();
			for(int i=0;i<cityList.size();i++){
				CargoInfoCityBean bean=(CargoInfoCityBean)cityList.get(i);
				int cityId=bean.getId();
				int areaCount=service.getCargoInfoAreaCount("city_id="+cityId);
				areaCountList.add(Integer.valueOf(areaCount));
			}
			request.setAttribute("cityList", cityList);
			request.setAttribute("areaCountList", areaCountList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("city");
	}
	
	//添加城市
	public ActionForward addCity(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		synchronized(cargoLock){
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			service.getDbOp().startTransaction();
			String code=StringUtil.convertNull(request.getParameter("code")).trim();
			String name=StringUtil.convertNull(request.getParameter("name")).trim();
			char[] ccode=code.toCharArray();
			if(code.equals("")){
				request.setAttribute("tip", "请填写城市代号！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(ccode.length>2||ccode[0]<65||ccode[0]>90||ccode[1]<65||ccode[1]>90){
				request.setAttribute("tip", "请填写有效的城市代号！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			int codeCount=service.getCargoInfoCityCount("code='"+code+"'");
			if(codeCount!=0){
				request.setAttribute("tip", "城市代号与其他重复，请重新填写！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(name.equals("")){
				request.setAttribute("tip", "请填写城市名称！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(name.length()>10){
				request.setAttribute("tip", "城市名称最大支持10个字符！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			int nameCount=service.getCargoInfoCityCount("name='"+name+"'");
			if(nameCount!=0){
				request.setAttribute("tip", "城市名称与其他重复，请重新填写！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			CargoInfoCityBean bean=new CargoInfoCityBean();
			bean.setCode(code);
			bean.setName(name);
			bean.setWholeCode(code);
			service.addCargoInfoCity(bean);
			service.getDbOp().commitTransaction();
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
		return mapping.findForward("cityList");
	}
	
	//删除城市
	public ActionForward deleteCity(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		synchronized(cargoLock){
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			service.getDbOp().startTransaction();
			int id=Integer.valueOf(request.getParameter("cityId")).intValue();
			if(service.getCargoInfoAreaCount("city_id="+id)>0){
				request.setAttribute("tip", "该城市下已添加地区，不能删除！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			service.deleteCargoInfoCity("id="+id);
			request.setAttribute("deleted", Integer.valueOf(1));
			service.getDbOp().commitTransaction();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		}
		return mapping.findForward("cityList");
	}
	
	//地区列表
	public ActionForward cargoInfoAreaList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			List wholeCityList=service.getCargoInfoCityList(null, -1, -1, "id desc");//所有的城市列表
			List areaList=new ArrayList();//区域列表
			if(request.getParameter("cityId")==null||request.getParameter("cityId").equals("")){
				areaList=service.getCargoInfoAreaList(null, -1, -1, "code asc");
			}else{
				String cityId=request.getParameter("cityId");//城市Id,查询条件
				areaList=service.getCargoInfoAreaList("city_id="+cityId, -1, -1, "code asc");
			}
			List storageCountList=new ArrayList();//区域内地区数列表
			List cityNameList=new ArrayList();//城市名称列表
			for(int i=0;i<areaList.size();i++){
				CargoInfoAreaBean bean=(CargoInfoAreaBean)areaList.get(i);
				int areaId=bean.getId();//区域Id
				int storageCount=service.getCargoInfoStorageCount("area_id="+areaId);					
				storageCountList.add(Integer.valueOf(storageCount));
				int cityId2=bean.getCityId();
				String cityName=service.getCargoInfoCity("id="+cityId2).getName();
				cityNameList.add(cityName);
			}
			request.setAttribute("areaList", areaList);
			request.setAttribute("storageCountList", storageCountList);
			request.setAttribute("cityNameList", cityNameList);
			request.setAttribute("wholeCityList", wholeCityList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("area");
	}
	
	//添加地区
	public ActionForward addArea(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
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
				request.setAttribute("tip", "请选择一个城市！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(areaCode.equals("")){
				request.setAttribute("tip", "请填写地区代号！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			String cityCode=service.getCargoInfoCity("id="+cityId).getCode();//城市代号
			String wholeCode=cityCode+areaCode;//完整编号
			char[] ccode=areaCode.toCharArray();
			
			if(ccode.length>1||ccode[0]<65||ccode[0]>90){
				request.setAttribute("tip", "请填写有效的地区代号");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			int codeCount=service.getCargoInfoAreaCount("code='"+areaCode+"' and city_id="+cityId);
			if(codeCount!=0){
				request.setAttribute("tip", "地区代号与其他重复，请重新填写！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(areaName.equals("")){
				request.setAttribute("tip", "请填写地区名称！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(areaName.length()>10){
				request.setAttribute("tip", "地区名称最大支持10个字符！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			int nameCount=service.getCargoInfoAreaCount("name='"+areaName+"' and city_id="+cityId);
			if(nameCount!=0){
				request.setAttribute("tip", "地区名称与其他重复，请重新填写！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			CargoInfoAreaBean bean=new CargoInfoAreaBean();
			bean.setCode(areaCode);
			bean.setWholeCode(wholeCode);
			bean.setName(areaName);
			bean.setCityId(Integer.parseInt(cityId));
			if(!areaId.equals("")){
				int areaIdCount=service.getCargoInfoAreaCount("id="+areaId);
				if(areaIdCount!=0){
					request.setAttribute("tip", "地区Id与其他重复，请重新填写！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				bean.setId(Integer.parseInt(areaId));
				bean.setOldId(Integer.parseInt(areaId));
			}
			service.addCargoInfoArea(bean);
			service.getDbOp().commitTransaction();
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
		return mapping.findForward("areaList");
	}
	
	//删除地区
	public ActionForward deleteArea(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		synchronized(cargoLock){
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			service.getDbOp().startTransaction();
			int areaId=Integer.parseInt(request.getParameter("areaId"));
			if(service.getCargoInfoStorageCount("area_id="+areaId)>0){
				request.setAttribute("tip", "该地区下已添加仓库，不能删除！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			service.deleteCargoInfoArea("id="+areaId);
			request.setAttribute("deleted", Integer.valueOf(1));
			service.getDbOp().commitTransaction();
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
		return mapping.findForward("areaList");
	}
	
	//仓库列表
	public ActionForward cargoInfoStorageList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
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
			List stockAreaCountList=new ArrayList();//区域内地区数列表
			List cityList=new ArrayList();//城市列表(bean)
			List areaList=new ArrayList();//地区列表(bean)
			for(int i=0;i<storageList.size();i++){
				CargoInfoStorageBean bean=(CargoInfoStorageBean)storageList.get(i);//仓库bean
				int storageId=bean.getId();//区域Id
				int stockAreaCount=service.getCargoInfoStockAreaCount("storage_id="+storageId);//区域内地区数
				stockAreaCountList.add(Integer.valueOf(stockAreaCount));
				int cityId=bean.getCityId();
				CargoInfoCityBean cicb=service.getCargoInfoCity("id="+cityId);
				cityList.add(cicb);
				int areaId=bean.getAreaId();
				CargoInfoAreaBean ciab=service.getCargoInfoArea("id="+areaId);
				areaList.add(ciab);
			}
			request.setAttribute("storageList", storageList);
			request.setAttribute("stockAreaCountList", stockAreaCountList);
			request.setAttribute("cityList", cityList);
			request.setAttribute("areaList", areaList);
			request.setAttribute("wholeCityList", wholeCityList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("storage");
	}
	
	//添加仓库
	public ActionForward addStorage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
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
				request.setAttribute("tip", "请选择一个城市！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(areaId.equals("")){
				request.setAttribute("tip", "请选择一个地区！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(storageCode.equals("")){
				request.setAttribute("tip", "请填写仓库代号！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			char[] ccode=storageCode.toCharArray();
			if(ccode.length>2||ccode[0]<48||ccode[0]>57||ccode[1]<48||ccode[1]>57){
				request.setAttribute("tip", "请填写有效的仓库代号！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			int storageCount=service.getCargoInfoStorageCount("code='"+storageCode+"' and area_id="+areaId);
			if(storageCount!=0){
				request.setAttribute("tip","仓库代号与其他重复，请重新填写！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(storageName.equals("")){
				request.setAttribute("tip", "请填写仓库名称！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			int storageNameCount=service.getCargoInfoStorageCount("name='"+storageName+"' and area_id="+areaId);
			if(storageNameCount!=0){
				request.setAttribute("tip","仓库名称与其他重复，请重新填写！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
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
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
		return mapping.findForward("storageList");
	}
	
	//删除仓库
	public ActionForward deleteStorage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		synchronized(cargoLock){
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			service.getDbOp().startTransaction();
			int storageId=Integer.parseInt(request.getParameter("storageId"));
			if(service.getCargoInfoStockAreaCount("storage_id="+storageId)>0){
				request.setAttribute("tip", "该仓库下已添加仓库区域，不能删除！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			service.deleteCargoInfoStorage("id="+storageId);
			request.setAttribute("deleted", Integer.valueOf(1));
			service.getDbOp().commitTransaction();
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
		return mapping.findForward("storageList");
	}
	
	//AJAX
	public ActionForward selection(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
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
		return mapping.findForward("ajaxSelect");
	}
	
	//仓库区域列表
	public ActionForward cargoInfoStockAreaList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
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
			int countPerPage=20;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			}
            PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);
            String para="";
            para=(cityId.equals("")?"":"&cityId="+cityId)
    			+(areaId.equals("")?"":"&areaId="+areaId)
    			+(storageId.equals("")?"":"&storageId="+storageId)
    			+(stockAreaCode.equals("")?"":"&stockAreaCode="+stockAreaCode)
    			;
            paging.setCurrentPageIndex(pageIndex);
            paging.setPrefixUrl("cargoInfo.do?method=cargoInfoStockAreaList"+para);
            
			String condition="";
			if(!cityId.equals("")){
				condition+="city_id=";
				condition+=cityId;
			}
			if(!areaId.equals("")){
				if(condition.equals("")){
					condition+="area_id=";
				}else{
					condition+=" and area_id=";
				}
				condition+=areaId;
			}
			if(!storageId.equals("")){
				if(condition.equals("")){
					condition+="storage_id=";
				}else{
					condition+=" and storage_id=";
				}
				condition+=storageId;
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
			}
			
			if(condition.toString().equals("")){
				condition=null;
			}
			stockAreaList=service.getCargoInfoStockAreaList(condition,paging.getCurrentPageIndex() * countPerPage,countPerPage,"whole_code asc");
			if(stockAreaList.size()==0&&paging.getCurrentPageIndex()!=1){
				stockAreaList=service.getCargoInfoStockAreaList(condition,(paging.getCurrentPageIndex()-1) * countPerPage,countPerPage,"whole_code asc");
			}
			int totalCount = service.getCargoInfoStockAreaCount(condition); //根据条件得到 总数量
			paging.setTotalCount(totalCount);
			paging.setTotalPageCount(totalCount%countPerPage==0?totalCount/countPerPage:totalCount/countPerPage+1);
			
			List storageCodeList=new ArrayList();//所属仓库代号列表
			List passageCountList=new ArrayList();//巷道数列表
			for(int i=0;i<stockAreaList.size();i++){
				CargoInfoStockAreaBean bean=(CargoInfoStockAreaBean)stockAreaList.get(i);
				int storageId2=bean.getStorageId();
				String storageCode=service.getCargoInfoStorage("id="+storageId2).getWholeCode();
				storageCodeList.add(storageCode);
				int passageCount=service.getCargoInfoPassageCount("stock_area_id="+bean.getId());
				passageCountList.add(Integer.valueOf(passageCount));
			}
			request.setAttribute("wholeStorageList", wholeStorageList);
			request.setAttribute("stockAreaList", stockAreaList);
			request.setAttribute("storageCodeList", storageCodeList);
			request.setAttribute("passageCountList", passageCountList);
			request.setAttribute("wholeCityList", wholeCityList);
			request.setAttribute("paging", paging);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("stockArea");
	}
	
	//添加仓库区域
	public ActionForward addStockArea(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
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
				request.setAttribute("tip", "请选择所属仓库！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(stockAreaCode.equals("")){
				request.setAttribute("tip", "请填写区域代号！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			char[] ccode=stockAreaCode.toCharArray();
			if(ccode.length>1||ccode[0]<65||ccode[0]>90){
				request.setAttribute("tip", "请填写有效的区域代号！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			int stockAreaCount=service.getCargoInfoStockAreaCount("code='"+stockAreaCode+"' and storage_id="+storageId);
			if(stockAreaCount!=0){
				request.setAttribute("tip","区域代号与其他重复，请重新填写！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(stockAreaName.equals("")){
				request.setAttribute("tip", "请填写区域名称！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			int stockAreaNameCount=service.getCargoInfoStockAreaCount("name='"+stockAreaName+"' and storage_id="+storageId);
			if(stockAreaNameCount!=0){
				request.setAttribute("tip","区域名称与其他重复，请重新填写！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(passageCount.equals("")){
				request.setAttribute("tip","请填写巷道个数！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(!(passageCount.matches("^[0-9]{1}$")||(passageCount.matches("^[0-9]{2}$")))){
				request.setAttribute("tip","巷道个数只允许输入两位纯数字！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
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
			request.setAttribute("cityId", ""+cityId);
			request.setAttribute("areaId", ""+areaId);
			request.setAttribute("storageId", storageId);
			
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
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
		return mapping.findForward("stockAreaList");
	}
	
	//删除仓库区域
	public ActionForward deleteStockArea(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		synchronized(cargoLock){
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			service.getDbOp().startTransaction();
			int stockAreaId=Integer.parseInt(request.getParameter("stockAreaId"));
			if(service.getCargoInfoShelfCount("stock_area_id="+stockAreaId)>0){
				request.setAttribute("tip", "该仓库区域下已添加货架，不能删除！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
//			CargoInfoStockAreaBean bean=service.getCargoInfoStockArea("id="+stockAreaId);
//			request.setAttribute("storageId", bean.getStorageId()+"");
			service.deleteCargoInfoStockArea("id="+stockAreaId);
			request.setAttribute("deleted", Integer.valueOf(1));
			service.getDbOp().commitTransaction();
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
		return mapping.findForward("stockAreaList");
	}
	
	//货架列表
	public ActionForward cargoInfoShelfList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
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
			int countPerPage=20;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			}
            PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);
            String para="";
            para=(cityId.equals("")?"":"&cityId="+cityId)
    			+(areaId.equals("")?"":"&areaId="+areaId)
    			+(storageId.equals("")?"":"&storageId="+storageId)
    			+(stockAreaId.equals("")?"":"&stockAreaId="+stockAreaId)
    			+(passageId.equals("")?"":"&passageId="+passageId)
    			+(stockAreaCode.equals("")?"":"&stockAreaCode="+stockAreaCode)
    			+(shelfCode.equals("")?"":"&shelfCode="+shelfCode)
    			;
            paging.setCurrentPageIndex(pageIndex);
            paging.setPrefixUrl("cargoInfo.do?method=cargoInfoShelfList"+para);
			
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
			shelfList=service.getCargoInfoShelfList(condition,paging.getCurrentPageIndex() * countPerPage,countPerPage,"whole_code asc");
			int totalCount = service.getCargoInfoShelfCount(condition); //根据条件得到 总数量
			paging.setTotalCount(totalCount);
			paging.setTotalPageCount(totalCount%countPerPage==0?totalCount/countPerPage:totalCount/countPerPage+1);
			List stockAreaCodeList=new ArrayList();//所属区域代号列表
			List cargoCountList=new ArrayList();//货位数列表
			for(int i=0;i<shelfList.size();i++){
				CargoInfoShelfBean bean=(CargoInfoShelfBean)shelfList.get(i);//一个货架
				
				int stockAreaId2=bean.getStockAreaId();
				String stockAreaCode2=service.getCargoInfoStockArea("id="+stockAreaId2).getWholeCode();
				stockAreaCodeList.add(stockAreaCode2);
				
				int floorCount=bean.getFloorCount();//该货架的层数
				List cargoSheldCountList=new ArrayList();//每层的货位数列表
				for(int j=1;j<=floorCount;j++){
					int cargoCount=service.getCargoInfoCount("shelf_id="+bean.getId()+" and floor_num="+j+" and status<>3");
					cargoSheldCountList.add(Integer.valueOf(cargoCount));
				}
				cargoCountList.add(cargoSheldCountList);
			}
			request.setAttribute("shelfList", shelfList);
			request.setAttribute("wholeStorageList", wholeStorageList);
			request.setAttribute("wholeCityList", wholeCityList);
			request.setAttribute("stockAreaCodeList", stockAreaCodeList);
			request.setAttribute("cargoCountList", cargoCountList);
			request.setAttribute("paging", paging);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("shelf");
	}
	
	//删除货架
	public ActionForward deleteShelf(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		synchronized(cargoLock){
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			service.getDbOp().startTransaction();
			int shelfId=Integer.parseInt(request.getParameter("shelfId"));
			if(service.getCargoInfoCount("shelf_id="+shelfId+" and status<>3")>0){
				request.setAttribute("tip", "该货架下已添加货位，不能删除！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			service.deleteCargoInfoShelf("id="+shelfId);
			request.setAttribute("deleted", Integer.valueOf(1));
			service.getDbOp().commitTransaction();
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
		return mapping.findForward("shelfList");
	}
	
	//添加货架
	public ActionForward addShelf(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
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
				request.setAttribute("tip", "请选择所属仓库！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(stockAreaId.equals("")){
				request.setAttribute("tip", "请选择所属区域！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
//			if(passageId.equals("")){
//				request.setAttribute("tip", "请选择巷道！");
//				request.setAttribute("result", "failure");
//				service.getDbOp().rollbackTransaction();
//				return mapping.findForward(IConstants.FAILURE_KEY);
//			}
			char[] ccode=shelfCode.toCharArray();
			if(ccode.length==1||(ccode.length==2&&(ccode[0]<48||ccode[0]>57||ccode[1]<48||ccode[1]>57))||ccode.length>2||shelfCode.equals("00")){
				request.setAttribute("tip", "请填写有效的货架号！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			int shelfCodeCount=service.getCargoInfoShelfCount("code='"+shelfCode+"' and stock_area_id="+stockAreaId+(passageId.equals("")?"":" and passage_id="+passageId));
			if(shelfCodeCount!=0){
				request.setAttribute("tip","该货架号已存在，请重新填写！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(floorCount.equals("")){
				request.setAttribute("tip", "请填写货架层数！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			char[] cfloor=floorCount.toCharArray();
			if(cfloor.length>1||cfloor[0]<48||cfloor[0]>57){
				request.setAttribute("tip", "货架层数必须是1位的数字，请重新填写！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(shelfCount.equals("")){
				request.setAttribute("tip", "请填写货架个数");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			char[] ccount=shelfCount.toCharArray();
			if(ccount.length==1){
				if(ccount[0]<48||ccount[0]>57){
					request.setAttribute("tip", "货架个数必须是1位或2位的数字，请重新填写！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}else if(ccount.length==2){
				if(ccount[0]<48||ccount[0]>57||ccount[1]<48||ccount[1]>57){
					request.setAttribute("tip", "货架个数必须是1位或2位的数字，请重新填写！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}else if(ccount.length>2){
				request.setAttribute("tip", "货架个数必须是1位或2位的数字，请重新填写！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			List passageList=service.getCargoInfoPassageList("stock_area_id="+stockAreaId+(passageId.equals("")?"":" and id="+passageId), -1, -1, null);
			for(int i=0;i<passageList.size();i++){
				CargoInfoPassageBean passage=(CargoInfoPassageBean)passageList.get(i);
				int currentCount=service.getCargoInfoShelfCount("passage_id="+passage.getId());
				int totalCount=Integer.parseInt(shelfCount)+currentCount;
				if(totalCount>99){
					request.setAttribute("tip", "仓库每个巷道内的货架个数不得超过99个。"+"\\r"+passage.getWholeCode()+"巷道内当前货架个数为"+currentCount+"个，" +
							"本次要添加"+Integer.parseInt(shelfCount)+"个。"+"\\r已超出巷道内货架个数限定范围，请重新填写！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
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
						request.setAttribute("tip", "编号超过上限，无法添加货架！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
			}else{//没输入货架号
				for(int k=0;k<passageList.size();k++){
					CargoInfoPassageBean passage=(CargoInfoPassageBean)passageList.get(k);
					List cargoInfoShelfList=service.getCargoInfoShelfList("passage_id="+passage.getId(), -1, -1, "code desc");
					if(cargoInfoShelfList.size()>0){
						CargoInfoShelfBean bean=(CargoInfoShelfBean)cargoInfoShelfList.get(0);
						if(Integer.parseInt(bean.getCode())+Integer.parseInt(shelfCount)>99){
							request.setAttribute("tip", "编号超过上限，无法添加货架！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
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
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
		return mapping.findForward("shelfList");
	}
	
	//批量添加货位(列表)
	public ActionForward addCargoList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbSlave);
		WareService wareService = new WareService(service.getDbOp());
		try{
			List wholeStorageList=service.getCargoInfoStorageList(null, -1, -1, "id asc");//所有仓库列表
			String shelfId=StringUtil.convertNull(request.getParameter("shelfId"));
			if(!shelfId.equals("")){
				List cargoCountList=new ArrayList();//每层货架的货位数列表
				CargoInfoShelfBean bean=service.getCargoInfoShelf("id="+shelfId);//一个货架
				int floorCount=bean.getFloorCount();//货架层数
				for(int i=1;i<=floorCount;i++){
					int cargoCount=service.getCargoInfoCount("shelf_id="+shelfId+" and floor_num="+i+" and status<>3");
					cargoCountList.add(Integer.valueOf(cargoCount));
				}
				String wholeShelfCode=bean.getWholeCode();//货架完整编号
				String cityName=((CargoInfoCityBean)service.getCargoInfoCity("id="+bean.getCityId())).getName();//城市名称
				String areaName=((CargoInfoAreaBean)service.getCargoInfoArea("id="+bean.getAreaId())).getName();//地区名称
				String storageCode=((CargoInfoStorageBean)service.getCargoInfoStorage("id="+bean.getStorageId())).getCode();//仓库号
				String stockAreaCode=((CargoInfoStockAreaBean)service.getCargoInfoStockArea("id="+bean.getStockAreaId())).getCode();//仓库区域名称
				String passageCode=null;//巷道号
				CargoInfoPassageBean passageBean=service.getCargoInfoPassage("id="+bean.getPassageId());
				if(passageBean!=null){
					passageCode=passageBean.getCode();
				}
				String stockType=((CargoInfoStockAreaBean)service.getCargoInfoStockArea("id="+bean.getStockAreaId())).getStockTypeName();
				String shelfAddress=cityName+areaName+storageCode+"号仓"+stockAreaCode+"区"+(passageCode==null?"":(passageCode+"巷道"))+"第"+bean.getCode()+"排货架-"+stockType;
				List productLineList=wareService.getProductLineList("1=1");//所有产品线列表
				request.setAttribute("wholeShelfCode", wholeShelfCode);
				request.setAttribute("shelfAddress", shelfAddress);
				request.setAttribute("cargoCountList", cargoCountList);
				request.setAttribute("productLineList", productLineList);
				request.setAttribute("shelfId", shelfId);//用于识别货架
				request.setAttribute("areaId", bean.getAreaId());
			}
			request.setAttribute("wholeStorageList", wholeStorageList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("cargo");
	}
	
	//批量添加货位（添加）
	public ActionForward cargoInfoAddCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		CargoInfoShelfBean shelfBean=null;
		synchronized(cargoLock){
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			service.getDbOp().startTransaction();
			String shelfId=request.getParameter("shelfId");//货架id
			String status=request.getParameter("status");//是否开通，1是开通，2是不开通
			shelfBean=service.getCargoInfoShelf("id="+shelfId);
			if(request.getParameter("checkAll")!=null){//统一添加
				int floorCount=shelfBean.getFloorCount();//层数
				for(int i=1;i<=floorCount;i++){
					if(StringUtil.convertNull(request.getParameter("cargoCount")).trim().equals("")){
						request.setAttribute("tip", "请输入最后一行的货位个数！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					String sCargoCount=StringUtil.convertNull(request.getParameter("cargoCount")).trim();
					char[] cCargoCount=sCargoCount.toCharArray();
					if((cCargoCount.length==2&&(cCargoCount[0]<48||cCargoCount[0]>57||cCargoCount[1]<48||cCargoCount[1]>57))||
						(cCargoCount.length==1&&(cCargoCount[0]<48||cCargoCount[0]>57))){
						request.setAttribute("tip", "最后一行的货位个数，输入不正确，请输入大于0的整数！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					int cargoCount=Integer.parseInt(StringUtil.convertNull(request.getParameter("cargoCount")).trim());//货位个数
					String maxCode="";//该货架该层最大code
					List cargoInfoList=service.getCargoInfoList("shelf_id="+shelfId+" and floor_num="+i, -1, -1, "code desc");
					if(cargoInfoList.size()==0){
						maxCode=shelfBean.getWholeCode()+i+"00";
					}else{
						maxCode=((CargoInfoBean)(cargoInfoList.get(0))).getWholeCode();
					}
					String code1=maxCode.substring(0, 12);//到层
					String code2=maxCode.substring(12, 14);//货位code
					int code=Integer.parseInt(code2);//最大货位code（int）
					if(code+cargoCount>99){
						request.setAttribute("tip", "每层最多99个货位！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					for(int j=0;j<cargoCount;j++){
						CargoInfoBean bean=new CargoInfoBean();
						if(code+j+1<10){
							bean.setCode("0"+String.valueOf(code+j+1));
						}else{
							bean.setCode(String.valueOf(code+j+1));
						}
						if(code+j+1<10){
							bean.setWholeCode(code1+"0"+String.valueOf(code+j+1));
						}else{
							bean.setWholeCode(code1+String.valueOf(code+j+1));
						}
						
						bean.setStoreType(Integer.parseInt(request.getParameter("storeType")));
						bean.setMaxStockCount(999);
						bean.setWarnStockCount(999);
						bean.setSpaceLockCount(0);
						if(StringUtil.convertNull(request.getParameter("productLineId")).equals("")){
							bean.setProductLineId(0);
						}else{
							bean.setProductLineId(Integer.parseInt(request.getParameter("productLineId")));
						}
						bean.setType(Integer.parseInt(request.getParameter("type")));
						if(StringUtil.convertNull(request.getParameter("length")).equals("")){
							bean.setLength(0);
						}else{
							String length=StringUtil.convertNull(request.getParameter("length")).trim();
							char[] cLength=length.toCharArray();
							for(int k=0;k<cLength.length;k++){
								if(cLength[k]<48||cLength[k]>57){
									request.setAttribute("tip", "最后一行的长度，输入不正确，请输入大于0的整数！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
							}
							bean.setLength(Integer.parseInt(length));
						}
						if(StringUtil.convertNull(request.getParameter("width")).trim().equals("")){
							bean.setWidth(0);
						}else{
							String width=StringUtil.convertNull(request.getParameter("width")).trim();
							char[] cWidth=width.toCharArray();
							for(int k=0;k<cWidth.length;k++){
								if(cWidth[k]<48||cWidth[k]>57){
									request.setAttribute("tip", "最后一行的宽度，输入不正确，请输入大于0的整数！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
							}
							bean.setWidth(Integer.parseInt(width));
						}
						if(StringUtil.convertNull(request.getParameter("high")).trim().equals("")){
							bean.setHigh(0);
						}else{
							String high=StringUtil.convertNull(request.getParameter("high")).trim();
							char[] cHigh=high.toCharArray();
							for(int k=0;k<cHigh.length;k++){
								if(cHigh[k]<48||cHigh[k]>57){
									request.setAttribute("tip", "最后一行的高度，输入不正确，请输入大于0的整数！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
							}
							bean.setHigh(Integer.parseInt(high));
						}
						
						bean.setFloorNum(i);
						bean.setStatus(Integer.parseInt(status));
						//根据shelfId得到stockType
						int stockType=((CargoInfoStockAreaBean)(service.getCargoInfoStockArea("id="+shelfBean.getStockAreaId()))).getStockType();
						bean.setStockType(stockType);
						bean.setShelfId(Integer.parseInt(shelfId));
						bean.setPassageId(shelfBean.getPassageId());
						bean.setStockAreaId(shelfBean.getStockAreaId());
						bean.setStorageId(shelfBean.getStorageId());
						bean.setAreaId(shelfBean.getAreaId());
						bean.setCityId(shelfBean.getCityId());
						bean.setRemark("");
						service.addCargoInfo(bean);
						
						CargoInfoLogBean logBean=new CargoInfoLogBean();//操作记录
						CargoInfoBean ciBean=service.getCargoInfo("whole_code='"+bean.getWholeCode()+"'");//刚刚添加的货位
						logBean.setCargoId(ciBean.getId());
						logBean.setOperDatetime(DateUtil.getNow());
						logBean.setOperAdminId(user.getId());
						logBean.setOperAdminName(user.getUsername());
						logBean.setRemark("新建货位");
						service.addCargoInfoLog(logBean);
						if(status.equals("1")){//选择开通货位
							CargoInfoLogBean statusLogBean=new CargoInfoLogBean();//开通货位操作记录
							statusLogBean.setCargoId(ciBean.getId());
							statusLogBean.setOperDatetime(DateUtil.getNow());
							statusLogBean.setOperAdminId(user.getId());
							statusLogBean.setOperAdminName(user.getUsername());
							statusLogBean.setRemark("开通货位");
							service.addCargoInfoLog(statusLogBean);
						}
					}
				}
			}else if(request.getParameter("check")!=null){//选择添加
				String[] floorList=request.getParameterValues("check");//已勾选的层
				for(int i=1;i<=floorList.length;i++){
					String floorNum=floorList[i-1];//层号
					if(StringUtil.convertNull(request.getParameter("cargoCount"+floorNum)).trim().equals("")){
						request.setAttribute("tip", "请输入第"+floorNum+"层的货位个数！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					String sCargoCount=StringUtil.convertNull(request.getParameter("cargoCount"+floorNum)).trim();
					char[] cCargoCount=sCargoCount.toCharArray();
					if((cCargoCount.length==2&&(cCargoCount[0]<48||cCargoCount[0]>57||cCargoCount[1]<48||cCargoCount[1]>57))||
						(cCargoCount.length==1&&(cCargoCount[0]<48||cCargoCount[0]>57))){
						request.setAttribute("tip", "第"+floorNum+"层的货位个数，输入不正确，请输入大于0的整数！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					int cargoCount=Integer.parseInt(StringUtil.convertNull(request.getParameter("cargoCount"+floorNum)).trim());//货位个数
					String maxCode="";//该货架该层最大code
					List cargoInfoList=service.getCargoInfoList("shelf_id="+shelfId+" and floor_num="+floorNum, -1, -1, "code desc");
					if(cargoInfoList.size()==0){
						maxCode=shelfBean.getWholeCode()+floorNum+"00";
					}else{
						maxCode=((CargoInfoBean)(cargoInfoList.get(0))).getWholeCode();
					}
					String code1=maxCode.substring(0, 12);//到层数
					String code2=maxCode.substring(12, 14);//货位code
					int code=Integer.parseInt(code2);//最大货位code（int）
					if(code+cargoCount>99){
						request.setAttribute("tip", "每层最多99个货位！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					for(int j=0;j<cargoCount;j++){
						CargoInfoBean bean=new CargoInfoBean();
						if(code+j+1<10){
							bean.setCode("0"+String.valueOf(code+j+1));
						}else{
							bean.setCode(String.valueOf(code+j+1));
						}
						if(code+j+1<10){
							bean.setWholeCode(code1+"0"+String.valueOf(code+j+1));
						}else{
							bean.setWholeCode(code1+String.valueOf(code+j+1));
						}
						bean.setStoreType(Integer.parseInt(request.getParameter("storeType"+floorNum)));
					
						bean.setMaxStockCount(999);
						bean.setWarnStockCount(999);
						bean.setSpaceLockCount(0);
						if(StringUtil.convertNull(request.getParameter("productLineId"+floorNum)).equals("")){
							bean.setProductLineId(0);
						}else{
							bean.setProductLineId(Integer.parseInt(request.getParameter("productLineId"+floorNum)));
						}
						bean.setType(Integer.parseInt(request.getParameter("type"+floorNum)));
						if(StringUtil.convertNull(request.getParameter("length"+floorNum)).trim().equals("")){
							bean.setLength(0);
						}else{
							String length=StringUtil.convertNull(request.getParameter("length"+floorNum)).trim();
							char[] cLength=length.toCharArray();
							for(int k=0;k<cLength.length;k++){
								if(cLength[k]<48||cLength[k]>57){
									request.setAttribute("tip", "第"+floorNum+"层的长宽高，输入不正确，请输入正整数！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
							}
							bean.setLength(Integer.parseInt(length));
						}
						if(StringUtil.convertNull(request.getParameter("width"+floorNum)).trim().equals("")){
							bean.setWidth(0);
						}else{
							String width=StringUtil.convertNull(request.getParameter("width"+floorNum)).trim();
							char[] cWidth=width.toCharArray();
							for(int k=0;k<cWidth.length;k++){
								if(cWidth[k]<48||cWidth[k]>57){
									request.setAttribute("tip", "第"+floorNum+"层的长宽高，输入不正确，请输入正整数！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
							}
							bean.setWidth(Integer.parseInt(width));
						}
						if(StringUtil.convertNull(request.getParameter("high"+floorNum)).trim().equals("")){
							bean.setHigh(0);
						}else{
							String high=StringUtil.convertNull(request.getParameter("high"+floorNum)).trim();
							char[] cHigh=high.toCharArray();
							for(int k=0;k<cHigh.length;k++){
								if(cHigh[k]<48||cHigh[k]>57){
									request.setAttribute("tip", "第"+floorNum+"层的长宽高，输入不正确，请输入正整数！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
							}
							bean.setHigh(Integer.parseInt(high));
						}
						bean.setFloorNum(Integer.parseInt(floorNum));
						bean.setStatus(Integer.parseInt(status));
						//根据shelfId得到stockType
						int stockType=((CargoInfoStockAreaBean)(service.getCargoInfoStockArea("id="+shelfBean.getStockAreaId()))).getStockType();
						bean.setStockType(stockType);
						bean.setShelfId(Integer.parseInt(shelfId));
						bean.setPassageId(shelfBean.getPassageId());
						bean.setStockAreaId(shelfBean.getStockAreaId());
						bean.setStorageId(shelfBean.getStorageId());
						bean.setAreaId(shelfBean.getAreaId());
						bean.setCityId(shelfBean.getCityId());
						bean.setRemark("");
						service.addCargoInfo(bean);
						

						CargoInfoLogBean logBean=new CargoInfoLogBean();//操作记录
						CargoInfoBean ciBean=service.getCargoInfo("whole_code='"+bean.getWholeCode()+"'");//刚刚添加的货位
						logBean.setCargoId(ciBean.getId());
						logBean.setOperDatetime(DateUtil.getNow());
						logBean.setOperAdminId(user.getId());
						logBean.setOperAdminName(user.getUsername());
						logBean.setRemark("新建货位");
						service.addCargoInfoLog(logBean);
						if(status.equals("1")){//选择开通货位
							CargoInfoLogBean statusLogBean=new CargoInfoLogBean();//开通货位操作记录
							statusLogBean.setCargoId(ciBean.getId());
							statusLogBean.setOperDatetime(DateUtil.getNow());
							statusLogBean.setOperAdminId(user.getId());
							statusLogBean.setOperAdminName(user.getUsername());
							statusLogBean.setRemark("开通货位");
							service.addCargoInfoLog(statusLogBean);
						}
					}
				}
			}else{//未选择
				request.setAttribute("tip", "请至少选中货架的某一层！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			request.setAttribute("shelfId", shelfId);
			request.setAttribute("add", "1");
			service.getDbOp().commitTransaction();
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
		if(shelfBean!=null&shelfBean.getAreaId()!=1){
			return mapping.findForward("toZCCargoList");
		}else{
			return mapping.findForward("toCargoList");
		}
	}
	
	//芳村仓货位列表
	public ActionForward fangcunCargoList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation db=new DbOperation();
		db.init("adult_slave");
		WareService wareService = new WareService(db);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, db);
		CartonningInfoService cartonningService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,db);
		
		ResultSet rs = null;
		ResultSet rs2 = null;
		try{
			String wholeCode=StringUtil.convertNull(request.getParameter("wholeCode")).trim();//货位号
			String productCode=StringUtil.convertNull(request.getParameter("productCode")).trim();//产品编号
			String stockCountStart=StringUtil.convertNull(request.getParameter("stockCountStart")).trim();//库存最小值
			String stockCountEnd=StringUtil.convertNull(request.getParameter("stockCountEnd")).trim();//库存最大值
			String[] status=request.getParameterValues("status");
			String areaId=request.getParameter("areaId");
			String storeType=StringUtil.convertNull(request.getParameter("storeType"));//存放类型
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
			String condition=StringUtil.convertNull(request.getParameter("condition"));
			if(condition.equals("")){
				condition=(!wholeCode.equals("")?" and ci.whole_code = '"+wholeCode+"'":""+" ")
				+(!productCode.equals("")?" and p.code = '"+productCode+"'":"")
				+(!stockCountStart.equals("")?" and cps.stock_count+cps.stock_lock_count>="+stockCountStart:"")
				+(!stockCountEnd.equals("")?" and cps.stock_count+cps.stock_lock_count<="+stockCountEnd:"")
				+(!storeType.equals("")?" and ci.store_type = "+storeType:"")
				+(status!=null&&status.length>=1?" and (ci.status="+status[0]:"")
				+(status!=null&&status.length>=2?" or ci.status="+status[1]:"")
				+(status!=null&&status.length>=3?" or ci.status="+status[2]:"")
				+(status!=null&&status.length>=1?")":"")
				+(!cartonningCode.equals("")&&!cargoCode.equals("")?" and p.id ="+product_id+" and ci.whole_code='"+cargoCode+"'":!cartonningCode.equals("")?" and 1=2":"");
			}
			int countPerPage=20;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			}
            PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);
            String para="";
            if(request.getParameter("para")!=null){
            	para=request.getParameter("para");
            }else{
            	para=(areaId.equals("")?"":("&areaId="+areaId))
            			+(wholeCode.equals("")?"":("&wholeCode="+wholeCode))
            			+(productCode.equals("")?"":("&productCode="+productCode))
            			+(stockCountStart.equals("")?"":("&stockCountStart="+stockCountStart))
            			+(stockCountEnd.equals("")?"":("&stockCountEnd="+stockCountEnd))
            			+(status!=null&&status.length>=1?("&status="+status[0]):"")
            			+(status!=null&&status.length>=2?("&status="+status[1]):"")
            			+(status!=null&&status.length>=3?("&status="+status[2]):"")
            			+(storeType.equals("")?"":("&storeType="+storeType))
            			+(cartonningCode.equals("")?"":("&cartonningCode="+cartonningCode))
            			;
            }
            paging.setCurrentPageIndex(pageIndex);
            paging.setPrefixUrl("cargoInfo.do?method=fangcunCargoList"+para);
			
			rs = service.getDbOp().executeQuery("select * from cargo_product_stock cps join product p on cps.product_id = p.id right join cargo_info ci on ci.id = cps.cargo_id" 
					+(" where ci.area_id="+areaId+" ")
					+(!wholeCode.equals("")?" and ci.whole_code = '"+wholeCode+"'":"")
					+(!productCode.equals("")?" and p.code = '"+productCode+"'":"")
					+(!stockCountStart.equals("")?" and cps.stock_count+cps.stock_lock_count>="+stockCountStart:"")
					+(!stockCountEnd.equals("")?" and cps.stock_count+cps.stock_lock_count<="+stockCountEnd:"")
					+(!storeType.equals("")?" and ci.store_type = "+storeType:"")
					+(status!=null&&status.length>=1?" and (ci.status="+status[0]:"")
					+(status!=null&&status.length>=2?" or ci.status="+status[1]:"")
					+(status!=null&&status.length>=3?" or ci.status="+status[2]:"")
					+(status!=null&&status.length>=1?")":"")
					+condition
					+(" order by ci.whole_code asc")
					+(" limit "+paging.getCurrentPageIndex() * countPerPage+","+countPerPage)
					);
			List list = this.getCargoAndProductStockList(rs);
			Iterator iter = list.listIterator();
			List productLineNameList=new ArrayList();//产品线列表
			List operCountList=new ArrayList();//未完成作业单数
			while(iter.hasNext()){
				CargoProductStockBean cps = (CargoProductStockBean)iter.next();//该cps，可能没有id，一定没有product，一定有cargoInfo
				voProduct product = new voProduct();
				if(cps.getProductId()>0){
					product=wareService.getProduct(cps.getProductId());
				}
				cps.setProduct(product);
				String productLineName="";
				if(cps.getCargoInfo().getProductLineId()>0){
					if(wareService.getProductLine("product_line.id="+cps.getCargoInfo().getProductLineId())!=null){
						productLineName=wareService.getProductLine("product_line.id="+cps.getCargoInfo().getProductLineId()).getName();
					}
				}
				productLineNameList.add(productLineName);
				CargoInfoBean ciBean=cps.getCargoInfo();//一个货位
				int count=0;
				ResultSet rs3=service.getDbOp().executeQuery("select count(distinct co.id) from cargo_operation co join cargo_operation_cargo coc on coc.oper_id=co.id where co.status in(0,1,2,3) and coc.product_id="+product.getId()+" and (coc.in_cargo_whole_code='"+ciBean.getWholeCode()+"' or coc.out_cargo_whole_code='"+ciBean.getWholeCode()+"')");
				while(rs3.next()){
					count=rs3.getInt(1);
				}
				rs3.close();
				operCountList.add(""+count);
			}
			
			rs2 = service.getDbOp().executeQuery("select * from cargo_product_stock cps join product p on cps.product_id = p.id right join cargo_info ci on ci.id = cps.cargo_id" 
					+(" where ci.area_id="+areaId+" ")
					+(!wholeCode.equals("")?" and ci.whole_code = '"+wholeCode+"'":"")
					+(!productCode.equals("")?" and p.code = '"+productCode+"'":"")
					+(!stockCountStart.equals("")?" and cps.stock_count+cps.stock_lock_count>="+stockCountStart:"")
					+(!stockCountEnd.equals("")?" and cps.stock_count+cps.stock_lock_count<="+stockCountEnd:"")
					+(!storeType.equals("")?" and ci.store_type = "+storeType:"")
					+(status!=null&&status.length>=1?" and (ci.status="+status[0]:"")
					+(status!=null&&status.length>=2?" or ci.status="+status[1]:"")
					+(status!=null&&status.length>=3?" or ci.status="+status[2]:"")
					+(status!=null&&status.length>=1?")":"")
					+condition
					+(" order by ci.whole_code asc")
					);
			List list2 = this.getCargoAndProductStockList(rs2);
			int totalCount = list2.size(); //根据条件得到 总数量
			paging.setTotalCount(totalCount);
			paging.setTotalPageCount(list2.size()%countPerPage==0?list2.size()/countPerPage:list2.size()/countPerPage+1);
			List productLineList=new ArrayList();
			productLineList=wareService.getProductLineList("1=1");
			List storageList=new ArrayList();
			storageList=service.getCargoInfoStorageList("area_id="+areaId, -1, -1, "whole_code asc");
			for(int i=0;i<list.size();i++){
				CargoProductStockBean cps=(CargoProductStockBean)list.get(i);
				cps.setCartonningList(new ArrayList());
				List cartonningList=cartonningService.getCartonningList("cargo_id="+cps.getCargoId()+" and status!=2", -1, -1, null);
				for(int j=0;j<cartonningList.size();j++){
					CartonningInfoBean cartonningBean=(CartonningInfoBean)cartonningList.get(j);
					CartonningProductInfoBean cartonningProduct=cartonningService.getCartonningProductInfo("cartonning_id="+cartonningBean.getId());
					if(cartonningProduct.getProductId()==cps.getProductId()){
						cartonningBean.setProductBean(cartonningProduct);
						cps.getCartonningList().add(cartonningBean);
					}
				}
			}
			request.setAttribute("operCountList", operCountList);
			request.setAttribute("storageList", storageList);
			request.setAttribute("productLineList", productLineList);
			request.setAttribute("paging", paging);
			request.setAttribute("list", list);
			request.setAttribute("fangcun", "1");
			request.setAttribute("para", para);
			request.setAttribute("condition", condition);
			request.setAttribute("productLineNameList", productLineNameList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("fangcunCargoList");
	}
	
	//芳村仓货位查询
	public ActionForward selectFangcunList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation db=new DbOperation();
		db.init("adult_slave");
		WareService wareService = new WareService(db);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, db);
		CartonningInfoService cartonningService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,db);
		ResultSet rs = null;
		ResultSet rs2 = null;
		try{
			String wholeCode=StringUtil.convertNull(request.getParameter("wholeCode")).trim();//货位号
			String productCode=StringUtil.convertNull(request.getParameter("productCode")).trim();//产品编号
			String stockCountStart=StringUtil.convertNull(request.getParameter("stockCountStart")).trim();//库存最小值
			String stockCountEnd=StringUtil.convertNull(request.getParameter("stockCountEnd")).trim();//库存最大值
			String[] status=request.getParameterValues("status");
			String shelfId=StringUtil.convertNull(request.getParameter("shelfId"));//货架Id
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
			String areaId="1";//未能和viewtree.jsp保持同步
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
			if(!stockCountStart.equals("")&&!stockCountStart.matches("[0-9]*")){
				request.setAttribute("tip", "货位当前库存，输入内容必须是数字，请重新输入！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(!stockCountEnd.equals("")&&!stockCountEnd.matches("[0-9]*")){
				request.setAttribute("tip", "货位当前库存，输入内容必须是数字，请重新输入！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(!shelfCode.equals("")&&!shelfCode.matches("[0-9]{2}")){
				request.setAttribute("tip", "货架代号，输入内容必须是两位数字，请重新输入！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(!floorNum.equals("")&&!floorNum.matches("[0-9]*")){
				request.setAttribute("tip", "货架层数，输入内容必须是数字，请重新输入！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			int countPerPage=20;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			}
            PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);
            String para="";
            para=(shelfId.equals("")?"":("&shelfId="+shelfId))
    			+(wholeCode.equals("")?"":("&wholeCode="+wholeCode))
    			+(productCode.equals("")?"":("&productCode="+productCode))
    			+(stockCountStart.equals("")?"":("&stockCountStart="+stockCountStart))
    			+(stockCountEnd.equals("")?"":("&stockCountEnd="+stockCountEnd))
    			+(storeType.equals("")?"":("&storeType="+storeType))
    			+(status!=null&&status.length>=1?("&status="+status[0]):"")
    			+(status!=null&&status.length>=2?("&status="+status[1]):"")
    			+(status!=null&&status.length>=3?("&status="+status[2]):"")
    			+(productLineId.equals("")?"":("&productLineId="+productLineId))
    			+(productName.equals("")?"":"&productName="+paraProductName)
    			+(type.equals("")?"":("&type="+type))
    			+(storageId.equals("")?"":("&storageId="+storageId))
    			+(stockAreaId.equals("")?"":("&stockAreaId="+stockAreaId))
    			+(shelfCode.equals("")?"":("&shelfCode="+shelfCode))
    			+(floorNum.equals("")?"":("&floorNum="+floorNum))
    				+(cartonningCode.equals("")?"":("&cartonningCode="+cartonningCode))
    			;
            paging.setCurrentPageIndex(pageIndex);
            paging.setPrefixUrl("cargoInfo.do?method=selectFangcunList"+para);
			rs = service.getDbOp().executeQuery("select * from cargo_product_stock cps join product p on cps.product_id = p.id right join cargo_info ci on ci.id = cps.cargo_id" 
					+(" where ci.area_id="+areaId+" and ci.store_type<>2 and ci.status<>3")
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
					+(!stockAreaId.equals("")?" and ci.stock_area_id="+stockAreaId:"")
					+(!shelfCode.equals("")?" and ci.whole_code regexp '"+"[A-Z0-9\\-]{7}"+shelfCode+"[0-9]{3}"+"'":"")
					+(!floorNum.equals("")?" and ci.floor_num="+floorNum:"")
					+(!cartonningCode.equals("")&&!cargoCode.equals("")?" and p.id ="+product_id+" and ci.whole_code='"+cargoCode+"'":!cartonningCode.equals("")?" and 1=2":"")
					+(" order by ci.whole_code asc")
					+(" limit "+paging.getCurrentPageIndex() * countPerPage+","+countPerPage)
					);
			List list = this.getCargoAndProductStockList(rs);
			rs2 = service.getDbOp().executeQuery("select * from cargo_product_stock cps join product p on cps.product_id = p.id right join cargo_info ci on ci.id = cps.cargo_id" 
					+(" where ci.area_id="+areaId+" and ci.store_type<>2 and ci.status<>3")
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
					+(!stockAreaId.equals("")?" and ci.stock_area_id="+stockAreaId:"")
					+(!shelfCode.equals("")?" and ci.whole_code regexp '"+"[A-Z0-9\\-]{7}"+shelfCode+"[0-9]{3}"+"'":"")
					+(!floorNum.equals("")?" and ci.floor_num="+floorNum:"")
				    +(!cartonningCode.equals("")&&!cargoCode.equals("")?" and p.id ="+product_id+" and ci.whole_code='"+cargoCode+"'":!cartonningCode.equals("")?" and 1=2":"")
					+(" order by ci.whole_code asc")
					);
			List list2 = this.getCargoAndProductStockList(rs2);
			int totalCount = list2.size(); //根据条件得到 总数量
			paging.setTotalCount(totalCount);
			paging.setTotalPageCount(list2.size()%countPerPage==0?list2.size()/countPerPage:list2.size()/countPerPage+1);
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
				CargoInfoBean ciBean=cps.getCargoInfo();//一个货位
				int count=0;
				ResultSet rs3=service.getDbOp().executeQuery("select count(distinct co.id) from cargo_operation co join cargo_operation_cargo coc on coc.oper_id=co.id where co.status in(0,1,2,3) and coc.product_id="+product.getId()+" and (coc.in_cargo_whole_code='"+ciBean.getWholeCode()+"' or coc.out_cargo_whole_code='"+ciBean.getWholeCode()+"')");
				while(rs3.next()){
					count=rs3.getInt(1);
				}
				rs3.close();
				operCountList.add(""+count);
			}
			List productLineList=new ArrayList();
			productLineList=wareService.getProductLineList("1=1");
			List storageList=new ArrayList();
			storageList=service.getCargoInfoStorageList("area_id="+areaId, -1, -1, "whole_code asc");
			for(int i=0;i<list.size();i++){
				CargoProductStockBean cps=(CargoProductStockBean)list.get(i);
				cps.setCartonningList(new ArrayList());
				List cartonningList=cartonningService.getCartonningList("cargo_id="+cps.getCargoId()+" and status!=2", -1, -1, null);
				for(int j=0;j<cartonningList.size();j++){
					CartonningInfoBean cartonningBean=(CartonningInfoBean)cartonningList.get(j);
					CartonningProductInfoBean cartonningProduct=cartonningService.getCartonningProductInfo("cartonning_id="+cartonningBean.getId());
					if(cartonningProduct.getProductId()==cps.getProductId()){
						cartonningBean.setProductBean(cartonningProduct);
						cps.getCartonningList().add(cartonningBean);
					}
				}
			}
			request.setAttribute("operCountList", operCountList);
			request.setAttribute("storageList", storageList);
			request.setAttribute("productLineList", productLineList);
			request.setAttribute("fangcun", "1");
			request.setAttribute("productLineNameList", productLineNameList);
			request.setAttribute("list", list);
			request.setAttribute("paging", paging);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("fangcunCargoList");
	}
	
	//货位列表----到芳村货位列表页面
	public ActionForward selectCargoList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation db=new DbOperation();
		db.init("adult_slave");
		WareService wareService = new WareService(db);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, db);
		CartonningInfoService cartonningService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,db);
		ResultSet rs = null;
		ResultSet rs2 = null;
		int areaId=0;
		try{
			String floorNum=StringUtil.convertNull(request.getParameter("floorNum"));//层号
			String shelfId=StringUtil.convertNull(request.getParameter("shelfId"));//货架Id
			String passageId=StringUtil.convertNull(request.getParameter("passageId"));//巷道Id
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
			if(!shelfId.equals("")){
				CargoInfoShelfBean shelfBean=service.getCargoInfoShelf("id="+shelfId);
				if(shelfBean!=null){
					areaId=shelfBean.getAreaId();
				}
			}
			if(!passageId.equals("")){
				CargoInfoPassageBean passage=service.getCargoInfoPassage("id="+passageId);
				if(passage!=null){
					areaId=passage.getAreaId();
				}
			}
			int countPerPage=20;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			}
            PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);
            String para="";
            if(request.getParameter("para")!=null){
            	para=request.getParameter("para");
            }else{
            	para=(shelfId.equals("")?"":"&shelfId="+shelfId)
            			+(floorNum.equals("")?"":("&floorNum="+floorNum))
            			+(cartonningCode.equals("")?"":("&cartonningCode="+cartonningCode))
            			;
            }
            paging.setCurrentPageIndex(pageIndex);
            paging.setPrefixUrl("cargoInfo.do?method=selectCargoList"+para);
			rs = service.getDbOp().executeQuery("select * from cargo_product_stock cps join product p on cps.product_id = p.id right join cargo_info ci on ci.id = cps.cargo_id" 
					+(" where ci.status<>3")
					+(!shelfId.equals("")?" and ci.shelf_id = "+shelfId:"")
					+(!floorNum.equals("")?" and ci.floor_num = "+floorNum:"")
					+(!passageId.equals("")?" and ci.passage_id = "+passageId:"")
						+(!cartonningCode.equals("")&&!cargoCode.equals("")?" and p.id ="+product_id+" and ci.whole_code='"+cargoCode+"'":!cartonningCode.equals("")?" and 1=2":"")
					+(" order by ci.whole_code asc")
					+(" limit "+paging.getCurrentPageIndex() * countPerPage+","+countPerPage)
					);
			List list = this.getCargoAndProductStockList(rs);
			rs2 = service.getDbOp().executeQuery("select * from cargo_product_stock cps join product p on cps.product_id = p.id right join cargo_info ci on ci.id = cps.cargo_id" 
					+(" where ci.status<>3")
					+(!shelfId.equals("")?" and ci.shelf_id = "+shelfId:"")
					+(!floorNum.equals("")?" and ci.floor_num = "+floorNum:"")
					+(!passageId.equals("")?" and ci.passage_id = "+passageId:"")
			+(!cartonningCode.equals("")&&!cargoCode.equals("")?" and p.id ="+product_id+" and ci.whole_code='"+cargoCode+"'":!cartonningCode.equals("")?" and 1=2":"")
					+(" order by ci.whole_code asc")
					);
			List list2 = this.getCargoAndProductStockList(rs2);
			int totalCount = list2.size(); //根据条件得到 总数量
			paging.setTotalCount(totalCount);
			paging.setTotalPageCount(list2.size()%countPerPage==0?list2.size()/countPerPage:list2.size()/countPerPage+1);		
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
				String productLineName="-";
				if(cps.getCargoInfo().getProductLineId()>0){
					if(wareService.getProductLine("product_line.id="+cps.getCargoInfo().getProductLineId())!=null){
						productLineName=wareService.getProductLine("product_line.id="+cps.getCargoInfo().getProductLineId()).getName();
					}
				}
				productLineNameList.add(productLineName);
				CargoInfoBean ciBean=cps.getCargoInfo();//一个货位
				int count=0;
				ResultSet rs3=service.getDbOp().executeQuery("select count(distinct co.id) from cargo_operation co join cargo_operation_cargo coc on coc.oper_id=co.id " +
						"where co.status in(0,1,2,3) " +
						"and coc.product_id="+product.getId()+" " +
						"and (coc.in_cargo_whole_code='"+ciBean.getWholeCode()+"' " +
						"or coc.out_cargo_whole_code='"+ciBean.getWholeCode()+"')");
				while(rs3.next()){
					count=rs3.getInt(1);
				}
				rs3.close();
				operCountList.add(""+count);
			}
			List productLineList=new ArrayList();
			productLineList=wareService.getProductLineList("1=1");
			List storageList=new ArrayList();
			storageList=service.getCargoInfoStorageList("area_id="+areaId, -1, -1, "whole_code asc");
			for(int i=0;i<list.size();i++){
				CargoProductStockBean cps=(CargoProductStockBean)list.get(i);
				cps.setCartonningList(new ArrayList());
				List cartonningList=cartonningService.getCartonningList("cargo_id="+cps.getCargoId()+" and status!=2", -1, -1, null);
				for(int j=0;j<cartonningList.size();j++){
					CartonningInfoBean cartonningBean=(CartonningInfoBean)cartonningList.get(j);
					CartonningProductInfoBean cartonningProduct=cartonningService.getCartonningProductInfo("cartonning_id="+cartonningBean.getId());
					if(cartonningProduct!=null){
						if(cartonningProduct.getProductId()==cps.getProductId()){
							cartonningBean.setProductBean(cartonningProduct);
							cps.getCartonningList().add(cartonningBean);
						}
					}
				}
			}
			request.setAttribute("operCountList", operCountList);
			request.setAttribute("storageList", storageList);
			request.setAttribute("productLineList", productLineList);
			request.setAttribute("paging", paging);
			request.setAttribute("productLineNameList", productLineNameList);
			request.setAttribute("list", list);
			if(request.getAttribute("add")!=null){
				request.setAttribute("add", "1");
			}
			request.setAttribute("para", para);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		if(areaId == 3){
			return mapping.findForward("zcCargoList");
		}else{
			return mapping.findForward("fangcunCargoList");
		}
		
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
	
	//关闭货位
	public ActionForward closeCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		CargoInfoBean ciBean=null;
		synchronized(cargoLock){
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			service.getDbOp().startTransaction();
			String cargoId=StringUtil.convertNull(request.getParameter("cargoId"));//货位Id
			ciBean=service.getCargoInfo("id="+cargoId);
			if(ciBean.getStatus()!=1){
				request.setAttribute("tip", "该货位状态不是未使用！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(ciBean.getSpaceLockCount()!=0){
				request.setAttribute("tip", "该货位空间冻结量不为0！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
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
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
		return mapping.findForward("toZCCargoList");
	}
	
	//清空货位
	public ActionForward clearCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
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
				request.setAttribute("tip", "该货位状态是"+ciBean.getStatusName()+"，不能清空货位！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(ciBean.getSpaceLockCount()!=0){
				request.setAttribute("tip", "货位空间冻结量大于0，不能清空货位！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(cpsBean!=null&&cpsBean.getStockCount()+cpsBean.getStockLockCount()!=0){
				request.setAttribute("tip", "当前货位库存大于0，不能清空货位！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			
			
			int count=0;
			ResultSet rs3=dbOpSlave.executeQuery("select count(distinct co.id) from cargo_operation co join cargo_operation_cargo coc on coc.oper_id=co.id where co.status in(1,2,3,4,5,6,10,11,12,13,14,15,19,20,21,22,23,24,28,29,30,31,32,33,37,38,39,40,41,42) and co.effect_status < 3 and coc.product_id="+product.getId()+" and (coc.in_cargo_whole_code='"+ciBean.getWholeCode()+"' or coc.out_cargo_whole_code='"+ciBean.getWholeCode()+"')");
			while(rs3.next()){
				count=rs3.getInt(1);
			}
			rs3.close();
			if(count!=0){
				request.setAttribute("tip", "存在与该货位相关的货位作业单，不能清空货位！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
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
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
			dbOpSlave.release();
		}
		}
		return mapping.findForward("toZCCargoList");
	}
	
	//货位修改页
	public ActionForward updateCargoPage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String cargoAddress="";
			
			String cargoId=request.getParameter("cargoId");
			CargoInfoBean ciBean=service.getCargoInfo("id="+cargoId);
			String cityName=service.getCargoInfoCity("id="+ciBean.getCityId()).getName();
			cargoAddress+=cityName;
			String areaName=service.getCargoInfoArea("id="+ciBean.getAreaId()).getName();
			cargoAddress+=areaName;
			String storageName=service.getCargoInfoStorage("id="+ciBean.getStorageId()).getName();
			cargoAddress+=storageName;
			String stockAreaCode=service.getCargoInfoStockArea("id="+ciBean.getStockAreaId()).getCode();
			cargoAddress+=stockAreaCode;
			cargoAddress+="区";
			int passageId=ciBean.getPassageId();
			if(passageId!=0){
				String passageCode=service.getCargoInfoPassage("id="+ciBean.getPassageId()).getCode();
				cargoAddress+=passageCode;
				cargoAddress+="巷道";
			}
			cargoAddress+="第";
			String shelfCode=service.getCargoInfoShelf("id="+ciBean.getShelfId()).getCode();
			cargoAddress+=shelfCode;
			cargoAddress+="排货架第";
			int floorNum=service.getCargoInfo("id="+ciBean.getId()).getFloorNum();
			cargoAddress+=floorNum;
			cargoAddress+="层第";
			int cargoNum=Integer.parseInt(service.getCargoInfo("id="+ciBean.getId()).getCode());
			cargoAddress+=cargoNum;
			cargoAddress+="货位";
			if(request.getParameter("cargoProductStockId")!=null&&(!request.getParameter("cargoProductStockId").equals("0"))){
				String cargoProductStockId=request.getParameter("cargoProductStockId");
				CargoProductStockBean cpsBean=service.getCargoProductStock("id="+cargoProductStockId);
				voProduct product=wareService.getProduct(cpsBean.getProductId());
				cpsBean.setCargoInfo(ciBean);
				cpsBean.setProduct(product);
				request.setAttribute("cargoProductStockBean", cpsBean);
			}
			request.setAttribute("ciBean", ciBean);
			request.setAttribute("cargoAddress", cargoAddress);
			List productLineList=wareService.getProductLineList("1=1");
			request.setAttribute("productLineList", productLineList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("updateCargo");
	}
	
	//货位修改
	public ActionForward updateCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if (!user.getGroup().isFlag(3053)) {
			request.setAttribute("tip", "没有修改权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		synchronized(cargoLock){
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			service.getDbOp().startTransaction();
			int cargoId=Integer.parseInt(request.getParameter("cargoId"));
			CargoInfoBean ciBean=service.getCargoInfo("id="+cargoId);
			String productLineId=request.getParameter("productLineId");
			String type=request.getParameter("type");
			String length=StringUtil.convertNull(request.getParameter("length")).trim();
			String width=StringUtil.convertNull(request.getParameter("width")).trim();
			String high=StringUtil.convertNull(request.getParameter("high")).trim();
			String warnStockCount=StringUtil.convertNull(request.getParameter("warnStockCount")).trim();
			String maxStockCount=StringUtil.convertNull(request.getParameter("maxStockCount")).trim();
			String storeType=StringUtil.convertNull(request.getParameter("storeType"));
			String remark=StringUtil.convertNull(request.getParameter("remark")).trim();
			String set="";
			String logRemark="修改货位：";//货位修改操作记录的说明
			if(ciBean.getStatus()==0&&(!storeType.equals("")&&!storeType.equals(ciBean.getStoreType()+""))){
				request.setAttribute("tip", "货位状态为已使用，不能修改货位状态！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(ciBean.getStatus()==3){
				request.setAttribute("tip", "货位已被删除，不能修改货位状态！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(!productLineId.equals("")){
				if(!set.equals("")){
					set+=",product_line_id=";
					set+=productLineId;
				}else{
					set+="product_line_id=";
					set+=productLineId;
				}
				if(Integer.parseInt(productLineId)!=ciBean.getProductLineId()){
					logRemark+="货位产品线（";
					voProductLine productLine1=wareService.getProductLine("product_line.id="+ciBean.getProductLineId());
					voProductLine productLine2=wareService.getProductLine("product_line.id="+productLineId);
					if(productLine1==null){
						logRemark+="null";
					}else{
						logRemark+=productLine1.getName();
					}
					logRemark+="-";
					if(productLine2==null){
						logRemark+="null";
					}else{
						logRemark+=productLine2.getName();
					}
					logRemark+="），";
				}
			}else{
				if(!set.equals("")){
					set+=",product_line_id=";
					set+=0;
				}else{
					set+="product_line_id=";
					set+=0;
				}
				if(ciBean.getProductLineId()!=0){
					logRemark+="货位产品线（";
					voProductLine productLine=wareService.getProductLine("product_line.id="+ciBean.getProductLineId());
					if(productLine!=null){
						logRemark+=productLine.getName();
					}else{
						logRemark+="null";
					}
					logRemark+="-";
					logRemark+="null";
					logRemark+="），";
				}
			}
			
			if(!type.equals("")){
				if(!set.equals("")){
					set+=",type=";
					set+=type;
				}else{
					set+="type=";
					set+=type;
				}
				if(Integer.parseInt(type)!=ciBean.getType()){
					logRemark+="货位类型（";
					logRemark+=ciBean.getTypeName();
					logRemark+="-";
					CargoInfoBean newBean=new CargoInfoBean();
					newBean.setType(Integer.parseInt(type));
					logRemark+=newBean.getTypeName();
					logRemark+="），";
				}
			}
			
			if(!length.equals("")){
				if(!set.equals("")){
					set+=",length=";
					set+=length;
				}else{
					set+="length=";
					set+=length;
				}
				if(Integer.parseInt(length)!=ciBean.getLength()){
					logRemark+="长度（";
					logRemark+=ciBean.getLength();
					logRemark+="-";
					logRemark+=length;
					logRemark+="），";
				}
			}else{
				if(!set.equals("")){
					set+=",length=";
					set+=0;
				}else{
					set+="length=";
					set+=0;
				}
				if(ciBean.getLength()!=0){
					logRemark+="长度（";
					logRemark+=ciBean.getLength();
					logRemark+="-";
					logRemark+="0";
					logRemark+="），";
				}
			}
			
			if(!width.equals("")){
				if(!set.equals("")){
					set+=",width=";
					set+=width;
				}else{
					set+="width=";
					set+=width;
				}
				if(Integer.parseInt(width)!=ciBean.getWidth()){
					logRemark+="宽度（";
					logRemark+=ciBean.getWidth();
					logRemark+="-";
					logRemark+=width;
					logRemark+="），";
				}
			}else{
				if(!set.equals("")){
					set+=",width=";
					set+=0;
				}else{
					set+="width=";
					set+=0;
				}
				if(ciBean.getWidth()!=0){
					logRemark+="宽度（";
					logRemark+=ciBean.getWidth();
					logRemark+="-";
					logRemark+="0";
					logRemark+="），";
				}
			}
			
			if(!high.equals("")){
				if(!set.equals("")){
					set+=",high=";
					set+=high;
				}else{
					set+="high=";
					set+=high;
				}
				if(Integer.parseInt(high)!=ciBean.getHigh()){
					logRemark+="高度（";
					logRemark+=ciBean.getHigh();
					logRemark+="-";
					logRemark+=high;
					logRemark+="），";
				}
			}else{
				if(!set.equals("")){
					set+=",high=";
					set+=0;
				}else{
					set+="high=";
					set+=0;
				}
				if(ciBean.getHigh()!=0){
					logRemark+="高度（";
					logRemark+=ciBean.getHigh();
					logRemark+="-";
					logRemark+="0";
					logRemark+="），";
				}
			}
			
			if(!warnStockCount.equals("")){
				if(!set.equals("")){
					set+=",warn_stock_count=";
					set+=warnStockCount;
				}else{
					set+="warn_stock_count=";
					set+=warnStockCount;
				}
				if(Integer.parseInt(warnStockCount)!=ciBean.getWarnStockCount()){
					logRemark+="警戒线（";
					logRemark+=ciBean.getWarnStockCount();
					logRemark+="-";
					logRemark+=warnStockCount;
					logRemark+="），";
				}
			}else{
				if(!set.equals("")){
					set+=",warn_stock_count=";
					set+=0;
				}else{
					set+="warn_stock_count=";
					set+=0;
				}
				if(ciBean.getWarnStockCount()!=0){
					logRemark+="警戒线（";
					logRemark+=ciBean.getWarnStockCount();
					logRemark+="-";
					logRemark+="0";
					logRemark+="），";
				}
			}
			
			if(!maxStockCount.equals("")){
				if(!set.equals("")){
					set+=",max_stock_count=";
					set+=maxStockCount;
				}else{
					set+="max_stock_count=";
					set+=maxStockCount;
				}
				if(Integer.parseInt(maxStockCount)!=ciBean.getMaxStockCount()){
					logRemark+="货位最大容量（";
					logRemark+=ciBean.getMaxStockCount();
					logRemark+="-";
					logRemark+=maxStockCount;
					logRemark+="），";
				}
			}else{
				if(!set.equals("")){
					set+=",max_stock_count=";
					set+=0;
				}else{
					set+="max_stock_count=";
					set+=0;
				}
				if(ciBean.getMaxStockCount()!=0){
					logRemark+="货位最大容量（";
					logRemark+=ciBean.getMaxStockCount();
					logRemark+="-";
					logRemark+="0";
					logRemark+="），";
				}
			}
			
			if(!remark.equals("")){
				if(!set.equals("")){
					set+=",remark='";
					set+=remark;
					set+="'";
				}else{
					set+="remark='";
					set+=remark;
					set+="'";
				}
				if(!remark.equals(ciBean.getRemark())){
					logRemark+="备注（";
					logRemark+=ciBean.getRemark();
					logRemark+="-";
					logRemark+=remark;
					logRemark+="），";
				}
			}else{
				if(!set.equals("")){
					set+=",remark=''";
				}else{
					set+="remark=''";
				}
				if(!ciBean.getRemark().equals("")){
					logRemark+="备注（";
					logRemark+=ciBean.getRemark();
					logRemark+="-";
					logRemark+="";
					logRemark+="），";
				}
			}
			
			if(!storeType.equals("")){
				if(!set.equals("")){
					set+=",store_type=";
					set+=storeType;
				}else{
					set+="store_type=";
					set+=storeType;
				}
				if(Integer.parseInt(storeType)!=ciBean.getStoreType()){
					logRemark+="存放类型（";
					logRemark+=ciBean.getStoreType();
					logRemark+="-";
					logRemark+=storeType;
					logRemark+="），";
				}
			}
			if(!logRemark.equals("修改货位：")){
				CargoInfoLogBean logBean=new CargoInfoLogBean();//货位修改操作记录
				logBean.setCargoId(cargoId);
				logBean.setOperDatetime(DateUtil.getNow());
				logBean.setOperAdminId(user.getId());
				logBean.setOperAdminName(user.getUsername());
				logBean.setRemark(logRemark.substring(0,logRemark.length()-1));
				service.addCargoInfoLog(logBean);
			}
			service.updateCargoInfo(set, "id="+cargoId);
			service.getDbOp().commitTransaction();
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
		return mapping.findForward("updateCargoPage");
	}
	
	//货位进销存卡片
	public ActionForward cargoStockCard(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			int countPerPage = 30;
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
                    return mapping.findForward(IConstants.FAILURE_KEY);
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
	            //总数
	            int totalCount = service.getCargoStockCardCount(condition);
	            //页码
	            int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
	            PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
	            List list = service.getCargoStockCardList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "create_datetime desc,id desc");
	            paging.setPrefixUrl("cargoInfo.do?method=cargoStockCard" + paramBuf.toString());
	            request.setAttribute("paging", paging);
	            request.setAttribute("list", list);
            }
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("cargoStockCard");
	}
	
	//货位列表打印
	public ActionForward cargoListPrint(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
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
			String[] status=request.getParameterValues("status");
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
			int stockType = StringUtil.toInt(request.getParameter("stockType"));
			rs = service.getDbOp().executeQuery("select * from cargo_product_stock cps join product p on cps.product_id = p.id right join cargo_info ci on ci.id = cps.cargo_id left join product_line pl on ci.product_line_id=pl.id" 
					+(shelfId.equals("")?(" where ci.area_id="+areaId):(" where 1=1"))
					+(!shelfId.equals("")?" and ci.shelf_id = "+shelfId:"")
					+(!areaId.equals("")?" and ci.area_id = "+areaId:"")
					+(stockType != -1?" and ci.stock_type = "+stockType:"")
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
					+(shelfId.equals("")?" and ci.store_type in (0,1,2,4)":"")
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
			cargoListPoiPrint(list,productLineNameList,hasOperList,operList,statusList,response);;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return null;
	}
    //poi方式导出货位列表
	public void cargoListPoiPrint(List cargoList, List productLineNameList, String hasOperList, List operList, List statusList, HttpServletResponse response) throws Exception {

		CargoProductStockBean bean = null;
		int size = 0;

		String fileName = DateUtil.getNow().replace(" ","-").replace(":","-");
		// 设置表头
		ExportExcel excel = new ExportExcel();
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
		header.add("货位号");
		header.add("货位产品线");
		header.add("产品编号");
		header.add("产品原名称");
		header.add("当前货位库存（其中冻结量）");
		header.add("货位空间冻结");
		header.add("货位警戒线");
		header.add("货位最大容量");
		header.add("存放类型");
		header.add("库存类型");
		header.add("货位类型");
		header.add("货位状态");
		header.add("备注");
		header.add("未完成作业单数");
		if (hasOperList.equals("1")) {
			header.add("作业单状态");
			header.add("作业单号");
		}

		size = header.size();

		if (cargoList != null && cargoList.size() > 0) {
			int x = cargoList.size();
			for (int i = 0; i < x; i++) {
				List operCodeList = (List) operList.get(i);
				List statusNameList = (List) statusList.get(i);
				bean = (CargoProductStockBean) cargoList.get(i);
				ArrayList<String> tmp = new ArrayList<String>();
				tmp.add(bean.getCargoInfo().getWholeCode());
				tmp.add(productLineNameList.get(i) + "");
				if (bean.getProduct().getCode() == null) {
					tmp.add("-");
				} else {
					tmp.add(bean.getProduct().getCode());
				}
				if (bean.getProduct().getOriname() != null) {
					tmp.add(bean.getProduct().getOriname());
				} else {
					tmp.add("-");
				}
				tmp.add(bean.getStockCount() + bean.getStockLockCount() + "(" + bean.getStockLockCount() + ")");
				tmp.add(bean.getCargoInfo().getSpaceLockCount()+ "");
				tmp.add(bean.getCargoInfo().getWarnStockCount() + "");
				tmp.add(bean.getCargoInfo().getMaxStockCount() + "");
				tmp.add(bean.getCargoInfo().getStoreTypeName());
				tmp.add(bean.getCargoInfo().getStockTypeName());
				tmp.add(bean.getCargoInfo().getTypeName());
				tmp.add(bean.getCargoInfo().getStatusName());
				if (bean.getCargoInfo().getRemark().length() > 3) {
					tmp.add(bean.getCargoInfo().getRemark().substring(0, 3) + "...");
				} else {
					tmp.add(bean.getCargoInfo().getRemark());
				}
				tmp.add(operCodeList.size() + "");
				if (hasOperList.equals("1")) {
					for (int j = 0; j < statusNameList.size(); j++) {
						if (j != statusNameList.size() - 1) {
							tmp.add(statusNameList.get(j).toString() + "<br/>");
						} else {
							tmp.add(statusNameList.get(j).toString());
						}
					}
					for (int j = 0; j < operCodeList.size(); j++) {
						if (j != operCodeList.size() - 1) {
							tmp.add(operCodeList.get(j).toString() + "<br/>");
						} else {
							tmp.add(operCodeList.get(j).toString());
						}
					}
				}
				bodies.add(tmp);
			}
		}
		headers.add(header);

		/* 允许合并列,下标从0开始，即0代表第一列 */
		List<Integer> mayMergeColumn = new ArrayList<Integer>();
		excel.setMayMergeColumn(mayMergeColumn);

		/* 允许合并行,下标从0开始，即0代表第一行 */
		List<Integer> mayMergeRow = new ArrayList<Integer>();
		excel.setMayMergeRow(mayMergeRow);

		/*
		 * 该行为固定写法 （设置该值为导出excel最大列宽 ,下标从1开始）
		 * 需要注意，如果没有headers,只有bodies,则该行中setColMergeCount 为bodies的最大列数
		 */
		excel.setColMergeCount(size);

		/*
		 * 设置需要自己设置样式的行，以每个bodies为参照 具体的样式设置参考 DemoExcel.java中的setStyle方法
		 * 具体可以参照执行后导出的excel样式及DemoExcel中的setStyle方法
		 */
		List<Integer> row = new ArrayList<Integer>();

		/* 设置需要自己设置样式的列，以每个bodies为参照 */
		List<Integer> col = new ArrayList<Integer>();

		excel.setRow(row);
		excel.setCol(col);

		// 调用填充表头方法
		excel.buildListHeader(headers);

		// 调用填充数据区方法
		excel.buildListBody(bodies);
		// 文件输出
		excel.exportToExcel(fileName, response, "");
	}
	
	
	
	//批量开通货位列表
	public ActionForward openCargoList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
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
			StringBuilder para=new StringBuilder();//分页条件
			boolean isQuery=false;
			if(!wholeCode.equals("")){
				buf.append(" and whole_code like'");
				buf.append(wholeCode);
				buf.append("%'");
				para.append("&wholeCode=");
				para.append(wholeCode);
				isQuery=true;
			}
			if(!storageId.equals("")){
				buf.append(" and storage_id=");
				buf.append(storageId);
				para.append("&storageId=");
				para.append(storageId);
				isQuery=true;
			}
			if(!stockAreaId.equals("")){
				buf.append(" and stock_area_id=");
				buf.append(stockAreaId);
				para.append("&stockAreaId=");
				para.append(stockAreaId);
				isQuery=true;
			}
			if(!passageId.equals("")){
				buf.append(" and passage_id=");
				buf.append(passageId);
				para.append("&passageId=");
				para.append(passageId);
				isQuery=true;
			}
			if(!shelfCode.equals("")){
				buf.append(" and whole_code regexp");
				buf.append(" '^[A-Z0-9\\-]{7}");
				buf.append(shelfCode);
				buf.append("[0-9]{3}$'");
				para.append("&shelfCode=");
				para.append(shelfCode);
				isQuery=true;
			}
			if(!floorNum.equals("")){
				if(!floorNum.matches("[0-9]*")){
					request.setAttribute("tip", "层数输入错误，只能输入正整数！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				buf.append(" and floor_num=");
				buf.append(floorNum);
				para.append("&floorNum=");
				para.append(floorNum);
				isQuery=true;
			}
			if(!stockType.equals("")){
				buf.append(" and stock_type=");
				buf.append(stockType);
				para.append("&stockType=");
				para.append(stockType);
				isQuery=true;
			}
			if(!storeType.equals("")){
				buf.append(" and store_type=");
				buf.append(storeType);
				para.append("&storeType=");
				para.append(storeType);
				isQuery=true;
			}
			if(!productLineId.equals("")){
				buf.append(" and product_line_id=");
				buf.append(productLineId);
				para.append("&productLineId=");
				para.append(productLineId);
				isQuery=true;
			}
			if(!type.equals("")){
				buf.append(" and type=");
				buf.append(type);
				para.append("&type=");
				para.append(type);
				isQuery=true;
			}
			int countPerPage=20;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex=Integer.parseInt(request.getParameter("pageIndex"));
			}
			PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);
			paging.setCurrentPageIndex(pageIndex);
            paging.setPrefixUrl("cargoInfo.do?method=openCargoList"+para.toString());
            int total=0;
            if(isQuery){
            	total=service.getCargoInfoCount(buf.toString());
            }
            paging.setTotalCount(total);
            paging.setTotalPageCount(total%countPerPage==0?total/countPerPage:total/countPerPage+1);
            List cargoInfoList=new ArrayList();
            if(isQuery){
            	cargoInfoList=service.getCargoInfoList(buf.toString(), paging.getCountPerPage()*paging.getCurrentPageIndex(), paging.getCountPerPage(), "whole_code asc");
    			if(cargoInfoList!=null&&cargoInfoList.size()==0&&paging.getCurrentPageIndex()!=0){
    				paging.setCurrentPageIndex(paging.getCurrentPageIndex()-1);
    				cargoInfoList=service.getCargoInfoList(buf.toString(), paging.getCountPerPage()*paging.getCurrentPageIndex(), paging.getCountPerPage(), "whole_code asc");
    			}
            }
			List productLineNameList=new ArrayList();
			if(cargoInfoList!=null){
				for(int i=0;i<cargoInfoList.size();i++){
					CargoInfoBean ciBean=(CargoInfoBean)cargoInfoList.get(i);
					String productLineName="";
					if(ciBean.getProductLineId()>0){
						voProductLine productLine=wareService.getProductLine("product_line.id="+ciBean.getProductLineId());
						if(productLine!=null){
							productLineName=productLine.getName();
						}
					}
					productLineNameList.add(productLineName);
				}
			}
			List productLineList=wareService.getProductLineList("1=1");
			List wholeStorageList=service.getCargoInfoStorageList("1=1", -1, -1, "whole_code asc");
			request.setAttribute("wholeStorageList", wholeStorageList);
			request.setAttribute("productLineList", productLineList);
			request.setAttribute("productLineNameList", productLineNameList);
			request.setAttribute("list", cargoInfoList);
			request.setAttribute("paging", paging);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("openCargoList");
	}
	
	//批量开通货位
	public ActionForward openCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		synchronized(cargoLock){
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			service.getDbOp().startTransaction();
			String[] cargoIdList=request.getParameterValues("cargoId");
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
				request.setAttribute("tip", tip.toString());
			}
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
		return mapping.findForward("toOpenCargoList");
	}
	
	//批量删除货位列表
	public ActionForward deleteCargoList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
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
			StringBuilder para=new StringBuilder();//分页条件
			boolean isQuery=false;
			if(!wholeCode.equals("")){
				buf.append(" and whole_code like'");
				buf.append(wholeCode);
				buf.append("%'");
				para.append("&wholeCode=");
				para.append(wholeCode);
				isQuery=true;
			}
			if(!storageId.equals("")){
				buf.append(" and storage_id=");
				buf.append(storageId);
				para.append("&storageId=");
				para.append(storageId);
				isQuery=true;
			}
			if(!stockAreaId.equals("")){
				buf.append(" and stock_area_id=");
				buf.append(stockAreaId);
				para.append("&stockAreaId=");
				para.append(stockAreaId);
				isQuery=true;
			}
			if(!passageId.equals("")){
				buf.append(" and passage_id=");
				buf.append(passageId);
				para.append("&passageId=");
				para.append(passageId);
				isQuery=true;
			}
			if(!shelfCode.equals("")){
				buf.append(" and whole_code regexp");
				buf.append(" '[A-Z0-9\\-]{7}");
				buf.append(shelfCode);
				buf.append("[0-9]{3}'");
				para.append("&shelfCode=");
				para.append(shelfCode);
				isQuery=true;
			}
			if(!floorNum.equals("")){
				if(!floorNum.matches("[0-9]*")){
					request.setAttribute("tip", "层数输入错误，只能输入正整数！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				buf.append(" and floor_num=");
				buf.append(floorNum);
				para.append("&floorNum=");
				para.append(floorNum);
				isQuery=true;
			}
			if(!stockType.equals("")){
				buf.append(" and stock_type=");
				buf.append(stockType);
				para.append("&stockType=");
				para.append(stockType);
				isQuery=true;
			}
			if(!storeType.equals("")){
				buf.append(" and store_type=");
				buf.append(storeType);
				para.append("&storeType=");
				para.append(storeType);
				isQuery=true;
			}
			if(!productLineId.equals("")){
				buf.append(" and product_line_id=");
				buf.append(productLineId);
				para.append("&productLineId=");
				para.append(productLineId);
				isQuery=true;
			}
			if(!type.equals("")){
				buf.append(" and type=");
				buf.append(type);
				para.append("&type=");
				para.append(type);
				isQuery=true;
			}
			int countPerPage=20;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex=Integer.parseInt(request.getParameter("pageIndex"));
			}
			PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);
			paging.setCurrentPageIndex(pageIndex);
            paging.setPrefixUrl("cargoInfo.do?method=deleteCargoList"+para.toString());
            int total=0;
            if(isQuery){
            	total=service.getCargoInfoCount(buf.toString());
            }
            paging.setTotalCount(total);
            paging.setTotalPageCount(total%countPerPage==0?total/countPerPage:total/countPerPage+1);
            List cargoInfoList=new ArrayList();
            if(isQuery){
            	cargoInfoList=service.getCargoInfoList(buf.toString(), paging.getCountPerPage()*paging.getCurrentPageIndex(), paging.getCountPerPage(), "whole_code asc");
    			if(cargoInfoList!=null&&cargoInfoList.size()==0&&paging.getCurrentPageIndex()!=0){
    				paging.setCurrentPageIndex(paging.getCurrentPageIndex()-1);
    				cargoInfoList=service.getCargoInfoList(buf.toString(), paging.getCountPerPage()*paging.getCurrentPageIndex(), paging.getCountPerPage(), "whole_code asc");
    			}
            }
			List productLineNameList=new ArrayList();
			if(cargoInfoList!=null){
				for(int i=0;i<cargoInfoList.size();i++){
					CargoInfoBean ciBean=(CargoInfoBean)cargoInfoList.get(i);
					String productLineName="";
					if(ciBean.getProductLineId()>0){
						voProductLine productLine=wareService.getProductLine("product_line.id="+ciBean.getProductLineId());
						if(productLine!=null){
							productLineName=productLine.getName();
						}
					}
					productLineNameList.add(productLineName);
				}
			}
			List productLineList=wareService.getProductLineList("1=1");
			List wholeStorageList=service.getCargoInfoStorageList("1=1", -1, -1, "whole_code asc");
			request.setAttribute("wholeStorageList", wholeStorageList);
			request.setAttribute("productLineList", productLineList);
			request.setAttribute("productLineNameList", productLineNameList);
			request.setAttribute("list", cargoInfoList);
			request.setAttribute("paging", paging);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("deleteCargoList");
	}
	
	//批量删除货位
	public ActionForward deleteCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		synchronized(cargoLock){
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			service.getDbOp().startTransaction();
			String[] cargoIdList=request.getParameterValues("cargoId");
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
				request.setAttribute("tip", tip.toString());
			}
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
		return mapping.findForward("toDeleteCargoList");
	}
	
	//货位绑定产品——核实产品页面
	public ActionForward checkCargoProduct(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String productCode=StringUtil.convertNull(request.getParameter("productCode")).trim();//产品编号
			String cargoCode=StringUtil.convertNull(request.getParameter("cargoCode")).trim();//货位号
			if(productCode.equals("")){
				request.setAttribute("tip", "请输入产品编号！");
				request.setAttribute("url", "cargoInfo.do?method=toCargoProduct&productCode="+productCode+"&cargoCode="+cargoCode);
				return mapping.findForward("tip");
			}
			if(cargoCode.equals("")){
				request.setAttribute("tip", "请输入货位号！");
				request.setAttribute("url", "cargoInfo.do?method=toCargoProduct&productCode="+productCode+"&cargoCode="+cargoCode);
				return mapping.findForward("tip");
			}
			voProduct product=wareService.getProduct(productCode);//产品
			if(product==null){
				request.setAttribute("tip", "请输入有效的产品编号！");
				request.setAttribute("url", "cargoInfo.do?method=toCargoProduct&productCode="+productCode+"&cargoCode="+cargoCode);
				return mapping.findForward("tip");
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
				request.setAttribute("url", "cargoInfo.do?method=toCargoProduct&productCode="+productCode+"&cargoCode="+cargoCode);
				return mapping.findForward("tip");
			}
			if(ciBean.getStatus()==3){
				request.setAttribute("tip", "货位号已被删除，请重新填写！");
				request.setAttribute("url", "cargoInfo.do?method=toCargoProduct&productCode="+productCode+"&cargoCode="+cargoCode);
				return mapping.findForward("tip");
			}
			if(ciBean.getStatus()==2){
				request.setAttribute("tip", "该货位未开通，请重新填写货位！");
				request.setAttribute("url", "cargoInfo.do?method=toCargoProduct&productCode="+productCode+"&cargoCode="+cargoCode);
				return mapping.findForward("tip");
			}
			if(service.getCargoProductStock("cargo_id="+ciBean.getId()+" and product_id="+product.getId())!=null){
				request.setAttribute("tip", "该货位已绑定该产品，请重新确认货位！");
				request.setAttribute("url", "cargoInfo.do?method=toCargoProduct&productCode="+productCode+"&cargoCode="+cargoCode);
				return mapping.findForward("tip");
			}
			if(ciBean.getStoreType()==0){
				CargoProductStockBean cps=service.getCargoProductStock("cargo_id="+ciBean.getId());
				if(cps!=null&&cps.getProductId()!=product.getId()){
					request.setAttribute("tip", "该货位已使用中，不能继续绑定其他产品，请重新确认货位！");
					request.setAttribute("url", "cargoInfo.do?method=toCargoProduct&productCode="+productCode+"&cargoCode="+cargoCode);
					return mapping.findForward("tip");
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
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("checkCargoProduct");
	}
	
	//货位绑定产品——提交
	public ActionForward cargoProduct(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
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
				request.setAttribute("url", "cargoInfo.do?method=toCargoProduct&productCode="+productCode+"&cargoCode="+cargoCode);
				return mapping.findForward("tip");
			}
			CargoInfoBean ciBean=service.getCargoInfo("whole_code='"+cargoCode+"'");//货位
			if(ciBean==null){
				request.setAttribute("tip", "请输入有效的货位号！");
				request.setAttribute("url", "cargoInfo.do?method=toCargoProduct&productCode="+productCode+"&cargoCode="+cargoCode);
				return mapping.findForward("tip");
			}
			if(ciBean.getStatus()==3){
				request.setAttribute("tip", "货位号已被删除，请重新填写！");
				request.setAttribute("url", "cargoInfo.do?method=toCargoProduct&productCode="+productCode+"&cargoCode="+cargoCode);
				return mapping.findForward("tip");
			}
			if(ciBean.getStatus()==2){
				request.setAttribute("tip", "该货位未开通，请重新填写货位！");
				request.setAttribute("url", "cargoInfo.do?method=toCargoProduct&productCode="+productCode+"&cargoCode="+cargoCode);
				return mapping.findForward("tip");
			}
			if(service.getCargoProductStock("cargo_id="+ciBean.getId()+" and product_id="+product.getId())!=null){
				request.setAttribute("tip", "该货位已绑定该产品，请重新确认货位！");
				request.setAttribute("url", "cargoInfo.do?method=toCargoProduct&productCode="+productCode+"&cargoCode="+cargoCode);
				return mapping.findForward("tip");
			}
			if(ciBean.getStoreType()==0){
				CargoProductStockBean cps=service.getCargoProductStock("cargo_id="+ciBean.getId());
				if(cps!=null&&cps.getProductId()!=product.getId()){
					request.setAttribute("tip", "该货位已使用中，不能继续绑定其他产品，请重新确认货位！");
					request.setAttribute("url", "cargoInfo.do?method=toCargoProduct&productCode="+productCode+"&cargoCode="+cargoCode);
					return mapping.findForward("tip");
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
		request.setAttribute("url", "cargoInfo.do?method=toCargoProduct");
		}
		return mapping.findForward("tip");
	}
	
	//货位绑定产品页面
	public ActionForward toCargoProduct(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		return mapping.findForward("cargoProduct");
	}
	
	//货位绑定产品——查询产品
	public ActionForward selectProduct(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser loginUser = (voUser)request.getSession().getAttribute("userView");
    	UserGroupBean group = loginUser.getGroup();

    	String name = request.getParameter("name");
        String code = request.getParameter("code");
        //增加条码查询
        String barcode= StringUtil.dealParam(request.getParameter("barcode"));
        String price = request.getParameter("price");
        String minPrice = request.getParameter("minPrice");
        String maxPrice = request.getParameter("maxPrice");
        int parentId1 = StringUtil.toInt(request.getParameter("parentId1"));
        int parentId2 = StringUtil.toInt(request.getParameter("parentId2"));
        if (name == null)
            name = "";
        if (code == null)
            code = "";
        if (barcode == null)
        	barcode = "";
        if (price == null)
            price = "";
        if (minPrice == null)
            minPrice = "";
        if (maxPrice == null)
            maxPrice = "";

        String[] strProductIds = request.getParameterValues("id");

        DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
        IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
        ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, psService.getDbOp());
        try {
        	StringBuilder buf = new StringBuilder();
        	buf.append(" (pb.barcode_status is null or pb.barcode_status=0) ");
        	if(strProductIds == null || strProductIds.length == 0){
	        	if(!StringUtil.isNull(name)){
	            	name = StringUtil.toSqlLike(request.getParameter("name"));
	            	if(buf.length() > 0){
	        			buf.append(" and ");
	        		}
	        		buf.append(" (a.name like '%");
	        		buf.append(name);
	        		buf.append("%' or a.oriname like '%");
	        		buf.append(name);
	        		buf.append("%') ");
	        	}
	        	if (!StringUtil.isNull(code)){
	        		if(buf.length() > 0){
	        			buf.append(" and ");
	        		}
	                buf.append(" a.code='");
	        		buf.append(code);
	        		buf.append("' ");
	        	}
	        	if (!StringUtil.isNull(barcode)){
	        		if(buf.length() > 0){
	        			buf.append(" and ");
	        		}
	        		buf.append(" pb.barcode='");
	        		buf.append(barcode.trim());
	        		buf.append("'");
	        	}
	            if (!StringUtil.isNull(price)) {
	        		if(buf.length() > 0){
	        			buf.append(" and ");
	        		}
	                price = StringUtil.toSqlLike(request.getParameter("price"));
	                buf.append(" (round(a.price,2) like '%");
	                buf.append(price);
	                buf.append("%' or round(a.price2,2) like '%");
	                buf.append(price);
	                buf.append("%')");
	            }
	            if (!StringUtil.isNull(minPrice)) {
	        		if(buf.length() > 0){
	        			buf.append(" and ");
	        		}
	            	buf.append(" (a.price >= ");
	            	buf.append(minPrice);
	            	buf.append(")");
	            }
	            if (!StringUtil.isNull(maxPrice)) {
	        		if(buf.length() > 0){
	        			buf.append(" and ");
	        		}
	            	buf.append(" (a.price <= ");
	            	buf.append(maxPrice);
	            	buf.append(")");
	            }
	            if(parentId1 > 0){
	        		if(buf.length() > 0){
	        			buf.append(" and ");
	        		}
	            	buf.append(" a.parent_id1=");
	            	buf.append(parentId1);
	            }
	            if(parentId2 > 0){
	        		if(buf.length() > 0){
	        			buf.append(" and ");
	        		}
	            	buf.append(" a.parent_id2=");
	            	buf.append(parentId2);
	            }
	
	            if(!group.isFlag(89)){
	            	if(buf.length() > 0){
	        			buf.append(" and ");
	        		}
	            	buf.append(" a.status <> 100");
	            }
        	} else {
        		buf.append("a.id in (");
        		for(int i=0; i<strProductIds.length; i++){
        			if(i != 0){
        				buf.append(",");
        			}
        			buf.append(strProductIds[i]);
        		}
        		buf.append(")");
        	}
        	List list=new ArrayList();
        	if(buf.toString().equals(" (pb.barcode_status is null or pb.barcode_status=0) ")){
        		list=new ArrayList();
        	}else{
        		list = wareService.searchProduct(buf.toString(), 0, 0, "a.status asc,a.id desc");
        	}
            Iterator iter = list.listIterator();
            while(iter.hasNext()){
            	voProduct product = (voProduct)iter.next();
            	List psList = psService.getProductStockList("product_id=" + product.getId(), -1, -1, null);
            	product.setPsList(psList);
            	product.setCargoPSList(cargoService.getCargoAndProductStockList("cps.product_id = "+product.getId(), -1, -1, "ci.whole_code asc"));
            }

            request.setAttribute("productList", list);

        } finally {
            wareService.releaseAll();
        }
		return mapping.findForward("selectProduct");
	}
	
	//货位绑定产品——查询货位
	public ActionForward selectCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String cargoCode=StringUtil.convertNull(request.getParameter("cargoCode")).trim();//货位号
			String mode=StringUtil.convertNull(request.getParameter("mode"));//查询条件，0为精确查询，1为左精确右模糊查询
			String status=StringUtil.convertNull(request.getParameter("status"));//货位状态
			String storageId=StringUtil.convertNull(request.getParameter("storageId"));//仓库代号
			String stockAreaId=StringUtil.convertNull(request.getParameter("stockAreaId"));//所属区域
			String shelfId=StringUtil.convertNull(request.getParameter("shelfId"));//货架代号
			String floorNum=StringUtil.convertNull(request.getParameter("floorNum"));//第几层
			String storeType=StringUtil.convertNull(request.getParameter("storeType"));//存放类型
			String productLineId=StringUtil.convertNull(request.getParameter("productLineId"));//产品线Id
			String type=StringUtil.convertNull(request.getParameter("type"));//货位类型
			String minMaxStockCount=StringUtil.convertNull(request.getParameter("minMaxStockCount")).trim();//最小货位最大容量
			String maxMaxStockCount=StringUtil.convertNull(request.getParameter("maxMaxStockCount")).trim();//最大货位最大容量
			int countPerPage=20;
			String stockType=StringUtil.convertNull(request.getParameter("stockType"));//库存类型
			StringBuilder condition= new StringBuilder("1=1 ");
			StringBuilder paramBuf = new StringBuilder("&mode="+mode); 
			if(!cargoCode.equals("")){
				if(mode.equals("0")){
					condition.append(" and whole_code='");
					condition.append(cargoCode);
					condition.append("'");
				}else if(mode.equals("1")){
					condition.append(" and whole_code like '");
					condition.append(cargoCode);
					condition.append("%'");
				}
				paramBuf.append("&whole_code="+cargoCode);
			}
			if((!status.equals(""))&&(!status.equals("2"))){
				condition.append(" and status=");
				condition.append(status);
				paramBuf.append("&status="+status); 
			}else{
				condition.append(" and status in(0,1,2)");
				paramBuf.append("&status=2");
			}
			if(!storageId.equals("")){
				condition.append(" and storage_id=");
				condition.append(storageId);
				paramBuf.append("&storageId="+storageId); 
			}
			if(!stockAreaId.equals("")){
				condition.append(" and stock_area_id=");
				condition.append(stockAreaId);
				paramBuf.append("&stockAreaId="+stockAreaId); 
			}
			if(!shelfId.equals("")){
				condition.append(" and shelf_id=");
				condition.append(shelfId);
				paramBuf.append("&shelfId="+shelfId); 
			}
			if(!floorNum.equals("")){
				condition.append(" and floor_num=");
				condition.append(floorNum);
				paramBuf.append("&floor_num="+floorNum); 
			}
			if(!storeType.equals("")){
				condition.append(" and store_type=");
				condition.append(storeType);
				paramBuf.append("&storeType="+storeType); 
			}
			if(!productLineId.equals("")){
				condition.append(" and product_line_id=");
				condition.append(productLineId);
				paramBuf.append("&productLineId="+productLineId); 
			}
			if(!type.equals("")){
				condition.append(" and type=");
				condition.append(type);
				paramBuf.append("&type="+type); 
			}
			if(!minMaxStockCount.equals("")){
				condition.append(" and max_stock_count>=");
				condition.append(minMaxStockCount);
				paramBuf.append("&minMaxStockCount="+minMaxStockCount); 
			}
			if(!maxMaxStockCount.equals("")){
				condition.append(" and max_stock_count<=");
				condition.append(maxMaxStockCount);
				paramBuf.append("&maxMaxStockCount="+maxMaxStockCount); 
			}
			if(!stockType.equals("")){
				condition.append(" and stock_type=");
				condition.append(stockType);
				paramBuf.append("&stockType="+stockType);
			}
			int productId=0;
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			List inCargoBeanList=new ArrayList();//目的货位列表
			int totalCount = 0;
			if(!condition.toString().equals("1=1  and status in(0,1,2)")){
				totalCount = service.getCargoInfoCount(condition.toString()); //根据条件得到 总数量
			}
            PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
			if(!mode.equals("")){//点查询到页面，mode有值
				inCargoBeanList=service.getCargoInfoList(condition.toString(), paging.getCurrentPageIndex() * countPerPage, countPerPage, "whole_code asc");
			}
			paging.setPrefixUrl("cargoInfo.do?method=selectCargo"+paramBuf.toString());
			List productLineList=wareService.getProductLineList("1=1");//所有产品线列表
			List storageList=service.getCargoInfoStorageList("1=1", -1, -1, "id asc");//所有仓库列表
			request.setAttribute("inCargoBeanList", inCargoBeanList);
			request.setAttribute("productLineList", productLineList);
			request.setAttribute("storageList", storageList);
			request.setAttribute("productId", ""+productId);
			request.setAttribute("paging", paging);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("selectCargo");
	}
	
	//货位人员操作记录
	public ActionForward cargoLog(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String cargoId=StringUtil.convertNull(request.getParameter("cargoId"));
			CargoInfoBean ciBean=service.getCargoInfo("id="+cargoId);
			int countPerPage=20;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex=Integer.parseInt(request.getParameter("pageIndex"));
			}
			String para="&cargoId="+cargoId;
			int totalCount=service.getCargoInfoLogCount("cargo_id="+cargoId);
			PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
			paging.setCurrentPageIndex(pageIndex);
			paging.setTotalPageCount(totalCount%countPerPage==0?totalCount/countPerPage:totalCount/countPerPage+1);
			paging.setPrefixUrl("cargoInfo.do?method=cargoLog"+para);
			List cargoLogList=service.getCargoInfoLogList("cargo_id="+cargoId, pageIndex*countPerPage, countPerPage, "id asc");
			request.setAttribute("cargoLogList", cargoLogList);
			request.setAttribute("paging", paging);
			request.setAttribute("ciBean", ciBean);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("cargoLog");
	}
	
	//盘点导出
	public ActionForward cargoInventory(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String action=StringUtil.convertNull(request.getParameter("action"));//select是查询，print是导出
		List list=new ArrayList();
		List productLineNameList=new ArrayList();
		List productNameList=new ArrayList();
		List productCodeList=new ArrayList();
		try{
			String cityId=StringUtil.convertNull(request.getParameter("cityId"));
			String areaId=StringUtil.convertNull(request.getParameter("areaId"));
			String storageId=StringUtil.convertNull(request.getParameter("storageId"));
			String stockAreaId=StringUtil.convertNull(request.getParameter("stockAreaId"));
			String stockType=StringUtil.convertNull(request.getParameter("stockType"));
			String productLineId=StringUtil.convertNull(request.getParameter("productLineId"));
			String storeType=StringUtil.convertNull(request.getParameter("storeType"));
			String type=StringUtil.convertNull(request.getParameter("type"));
			String shelfCodeStart=StringUtil.convertNull(request.getParameter("shelfCodeStart")).trim();//最小货架代号
			String shelfCodeEnd=StringUtil.convertNull(request.getParameter("shelfCodeEnd")).trim();//最大货架代号
			String floorNum=StringUtil.convertNull(request.getParameter("floorNum")).trim();
			String startDate=StringUtil.convertNull(request.getParameter("startDate")).trim();//动碰日期开始
			String endDate=StringUtil.convertNull(request.getParameter("endDate")).trim();//动碰日期结束
			String query=StringUtil.convertNull(request.getParameter("isQuery"));
			boolean isQuery=false;
			if(query.equals("1")){
				isQuery=true;
			}
			StringBuilder condition=new StringBuilder("ci.status=0");
			StringBuilder paraBuf=new StringBuilder();
			if(!cityId.equals("")){
				condition.append(" and ci.city_id="+cityId);
				paraBuf.append("&cityId="+cityId);
				isQuery=true;
			}
			if(!areaId.equals("")){
				condition.append(" and ci.area_id="+areaId);
				paraBuf.append("&areaId="+areaId);
				isQuery=true;
			}
			if(!storageId.equals("")){
				condition.append(" and ci.storage_id="+storageId);
				paraBuf.append("&storageId="+storageId);
				isQuery=true;
			}
			if(!stockAreaId.equals("")){
				condition.append(" and ci.stock_area_id="+stockAreaId);
				paraBuf.append("&stockAreaId="+stockAreaId);
				isQuery=true;
			}
			if(!stockType.equals("")){
				condition.append(" and ci.stock_type="+stockType);
				paraBuf.append("&stockType="+stockType);
				isQuery=true;
			}
			if(!productLineId.equals("")){
				condition.append(" and ci.product_line_id="+productLineId);
				paraBuf.append("&productLineId="+productLineId);
				isQuery=true;
			}
			if(!storeType.equals("")){
				condition.append(" and ci.store_type="+storeType);
				paraBuf.append("&storeType="+storeType);
				isQuery=true;
			}
			if(!type.equals("")){
				condition.append(" and ci.type="+type);
				paraBuf.append("&type="+type);
				isQuery=true;
			}
			if((!shelfCodeStart.equals(""))&&(!shelfCodeEnd.equals(""))){
				int c1=Integer.parseInt(shelfCodeStart);
				int c2=Integer.parseInt(shelfCodeEnd);
				StringBuilder reg=new StringBuilder("(");
				for(int i=c1;i<=c2;i++){
					if(i<10){
						reg.append("0"+i);
					}else{
						reg.append(i);
					}
					if(i!=c2){
						reg.append("|");
					}
				}
				reg.append(")");
				reg.append("{1}");
				condition.append(" and ci.whole_code regexp");
				condition.append(" '^[A-Z0-9\\-]{7}");
				condition.append(reg.toString());
				condition.append("[0-9]{3}$'");
				paraBuf.append("&shelfCodeStart=");
				paraBuf.append(shelfCodeStart);
				paraBuf.append("&shelfCodeEnd=");
				paraBuf.append(shelfCodeEnd);
				isQuery=true;
			}
			if(!floorNum.equals("")){
				condition.append(" and ci.floor_num="+floorNum);
				paraBuf.append("&floorNum="+floorNum);
				isQuery=true;
			}
			if((!startDate.equals(""))&&(!endDate.equals(""))){
				String d1=startDate+" 00:00:00";
				String d2=endDate+" 23:59:59";
				List cardList=service.getCargoStockCardList("create_datetime>='"+d1+"' and create_datetime<='"+d2+"'", -1, -1, null);
				if(cardList.size()>0){
					condition.append(" and cps.product_id in (");
					for(int i=0;i<cardList.size();i++){
						CargoStockCardBean card=(CargoStockCardBean)cardList.get(i);
						int productId=card.getProductId();
						condition.append(""+productId);
						if(i!=cardList.size()-1){
							condition.append(",");
						}
					}
					condition.append(")");
				}else{
					condition.append(" and 1=2");
				}
				paraBuf.append("&startDate="+startDate);
				paraBuf.append("&endDate="+endDate);
				isQuery=true;
			}
			int countPerPage=20;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex=Integer.parseInt(request.getParameter("pageIndex"));
			}
			PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);
			paging.setCurrentPageIndex(pageIndex);
            paging.setPrefixUrl("cargoInfo.do?method=cargoInventory&action=select"+paraBuf.toString());
            int total=0;
            if(isQuery){
            	total=service.getCargoAndProductStockList(condition.toString(), -1, -1, "ci.whole_code asc").size();
            }
            paging.setTotalCount(total);
            paging.setTotalPageCount(total%countPerPage==0?total/countPerPage:total/countPerPage+1);
//			List productLineNameList=new ArrayList();
            ResultSet rs=null;
            if(isQuery){
            	if(action.equals("select")){
            		//list=service.getCargoAndProductStockList(condition.toString(), paging.getCountPerPage()*paging.getCurrentPageIndex(), paging.getCountPerPage(), "ci.whole_code asc");            		
            		rs=service.getDbOp().executeQuery("select * from cargo_info ci left join cargo_product_stock cps on ci.id = cps.cargo_id join product p on p.id=cps.product_id" +
                    		" where "+condition+
                    		" order by ci.whole_code asc,p.code asc"+
            				" limit "+paging.getCountPerPage()*paging.getCurrentPageIndex()+","+paging.getCountPerPage());
                    list=this.getCargoAndProductStockList(rs);
            	}else if(action.equals("print")){
            		//list=service.getCargoAndProductStockList(condition.toString(), -1, -1, "ci.whole_code asc");
            		rs=service.getDbOp().executeQuery("select * from cargo_info ci left join cargo_product_stock cps on ci.id = cps.cargo_id join product p on p.id=cps.product_id" +
                    		" where "+condition+
                    		" order by ci.whole_code asc,p.code asc");
            		list=this.getCargoAndProductStockList(rs);
            	}
            }            	
			Iterator iter = list.listIterator();
//			List productCodeList=new ArrayList();
//			List productNameList=new ArrayList();
			if(rs!=null){
				rs.beforeFirst();
			}
			List productLineList=wareService.getProductLineList("1=1");
			HashMap productLineMap=new HashMap();
			for(int i=0;i<productLineList.size();i++){
				voProductLine productLine=(voProductLine)(productLineList.get(i));
				productLineMap.put(productLine.getId()+"", productLine.getName());
			}
			while(iter.hasNext()){
				rs.next();
				productCodeList.add(rs.getString("p.code"));
				productNameList.add(rs.getString("p.oriname"));
				
				CargoProductStockBean cpsBean=(CargoProductStockBean)iter.next();
				CargoInfoBean ciBean=cpsBean.getCargoInfo();
				Object productLineName=productLineMap.get(ciBean.getProductLineId()+"");
				if(productLineName!=null){
					productLineNameList.add(productLineName);
				}else{
					productLineNameList.add("");
				}
			}
			if(rs!=null){
				rs.close();
			}
			List wholeCityList=service.getCargoInfoCityList("1=1", -1, -1, null);
			request.setAttribute("list", list);
			request.setAttribute("productLineNameList", productLineNameList);
			request.setAttribute("productCodeList", productCodeList);
			request.setAttribute("productNameList", productNameList);
			request.setAttribute("wholeCityList", wholeCityList);
			request.setAttribute("productLineList", productLineList);
			request.setAttribute("paging", paging);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		if(action.equals("print")){
			exportCargoinfo(list,productLineNameList,productCodeList,productNameList,response);
			return null;
//			return mapping.findForward("cargoInventoryPrint");
		}else{
			return mapping.findForward("cargoInventory");
		}
	}
	/**
	 * 生成xlsx 的excel文件
	 * @param list
	 * @param productLineNameList
	 * @param productCodeList
	 * @param productNameList
	 * @param response
	 * @throws Exception
	 */
	private void exportCargoinfo(List list,List productLineNameList,List productCodeList,
				List productNameList,HttpServletResponse response) throws Exception {
		ExportExcel excel = new ExportExcel(ExportExcel.XSSF);
		List<ArrayList<String>> header = new ArrayList<ArrayList<String>>();
		ArrayList<String> header0 = new ArrayList<String>();
		header0.add("序号");
		header0.add("货位产品线");
		header0.add("产品编号");
		header0.add("产品原名称");
		header0.add("货位库存量");
		header0.add("货位冻结量");
		header0.add("货位号");
		header0.add("盘点量");
		header0.add("差异");
		header.add(header0);
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
		ArrayList<String> body = null;
		for(int i=0;i<list.size();i++){
			body = new ArrayList<String>();
			CargoProductStockBean cpsBean = (CargoProductStockBean)list.get(i);
			body.add((i+1)+"");
			body.add(productLineNameList.get(i)+"");
			body.add(productCodeList.get(i).toString());
			body.add(productNameList.get(i).toString());
			body.add((cpsBean.getStockCount()+cpsBean.getStockLockCount())+"");
			body.add((cpsBean.getStockLockCount())+"");
			body.add(cpsBean.getCargoInfo().getWholeCode());
			body.add("");
			body.add("");
			bodies.add(body);
		}
		/*设置需要自己设置样式的列，以每个bodies为参照*/
        List<Integer> col  = new ArrayList<Integer>();
        for(int i=0;i<header0.size();i++){
        	col.add(i);
        }
        excel.setCol(col);
		//调用填充表头方法
        excel.buildListHeader(header);
        
        //调用填充数据区方法
        excel.buildListBody(bodies);
        excel.exportToExcel("盘点导出_"+DateUtil.formatDate(new Date(),"yyyy-MM-dd-HH-mm-ss"), response, "");
	}

	//批量修改货位属性__货位列表
	public ActionForward changeCargoPropertyList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String storageId=StringUtil.convertNull(request.getParameter("storageId")).trim();
		String stockAreaId=StringUtil.convertNull(request.getParameter("stockAreaId")).trim();
		String passageId=StringUtil.convertNull(request.getParameter("passageId")).trim();
		String shelfCode=StringUtil.convertNull(request.getParameter("shelfCode")).trim();
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		ICargoService cargoService=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBsByServiceManagerService bsbyService = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			boolean query=false;//若查询条件都为空则不能查询
			StringBuilder paraBuf=new StringBuilder();
			String condition="(status=0 or status=1) ";
			if(!"".equals(storageId)){
				condition+=" and storage_id=";
				condition+=storageId;
				paraBuf.append("&storageId=");
				paraBuf.append(storageId);
				query=true;
			}
			if(!"".equals(stockAreaId)){
				condition+=" and stock_area_id=";
				condition+=stockAreaId;
				paraBuf.append("&stockAreaId=");
				paraBuf.append(stockAreaId);
				query=true;
			}
			if(!"".equals(passageId)){
				condition+=" and passage_id=";
				condition+=passageId;
				paraBuf.append("&passageId=");
				paraBuf.append(passageId);
				query=true;
			}
			if(!"".equals(shelfCode)){
				condition+=" and whole_code like '";
				condition+=shelfCode;
				condition+="%'";
				paraBuf.append("&shelfCode=");
				paraBuf.append(shelfCode);
				query=true;
			}
			
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			int countPerPage = 20;
			int totalCount=0;
			if(condition.length()>0){
				totalCount=cargoService.getCargoInfoCount(condition);
			}
			PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
			paging.setPrefixUrl("cargoInfo.do?method=changeCargoPropertyList"+paraBuf.toString());
			
			List cargoList=new ArrayList();//查询出的货位列表
			if(query){
				cargoList=cargoService.getCargoInfoList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, null);
			}
			
			List storageList=new ArrayList();//所有的仓库列表
			storageList=cargoService.getCargoInfoStorageList("1=1", -1, -1, "whole_code asc");
			
//			List relatedOrderCountList=new ArrayList();//货位相关单据数
//			for(int i=0;i<cargoList.size();i++){
//				CargoInfoBean cargo=(CargoInfoBean)cargoList.get(i);
//				voProductLine pl=wareService.getProductLine("product_line.id="+cargo.getProductLineId());
//				cargo.setProductLine(pl);
//				int relatedOrderCount=0;//关联单据数
//				//关联货位作业单
//				HashMap map1=new HashMap();//用于统计相关货位作业单数，去掉重复数据
//				List list1=cargoService.getCargoOperationCargoList("out_cargo_whole_code='"+cargo.getWholeCode()
//						+"' or in_cargo_whole_code='"+cargo.getWholeCode()+"'", -1, -1, null);
//				for(int j=0;j<list1.size();j++){
//					CargoOperationCargoBean coc=(CargoOperationCargoBean)list1.get(j);
//					int operId=coc.getOperId();
//					CargoOperationBean op=cargoService.getCargoOperation("id="+operId);
//					if(op!=null){
//						//已确认和已审核的作业单符合条件
//						if(op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS2
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS3
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS4
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS5
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS6
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS11
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS12
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS13
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS14
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS15
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS20
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS21
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS22
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS23
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS24
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS29
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS30
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS31
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS32
//								||op.getStatus()==CargoOperationProcessBean.OPERATION_STATUS33){
//							map1.put(op.getId()+"", "1");
//						}
//					}
//				}
//				relatedOrderCount+=map1.size();
//				//关联订单
//				List list2=stockService.getOrderStockProductCargoList("cargo_whole_code='"+cargo.getWholeCode()+"'", -1, -1, null);
//				for(int j=0;j<list2.size();j++){
//					OrderStockProductCargoBean ospc=(OrderStockProductCargoBean)list2.get(j);
//					int orderStockId=ospc.getOrderStockId();
//					OrderStockBean os=stockService.getOrderStock("id="+orderStockId);
//					if(os!=null){
//						//待出货，已确认，复核
//						if(os.getStatus()==OrderStockBean.STATUS1||os.getStatus()==OrderStockBean.STATUS6){
//							relatedOrderCount++;
//						}
//					}
//				}
//				//关联库存调拨单
//				HashMap map3=new HashMap();
//				List list3=psService.getStockExchangeProductCargoList("cargo_info_id="+cargo.getId(), -1, -1, null);
//				for(int j=0;j<list3.size();j++){
//					StockExchangeProductCargoBean sepc=(StockExchangeProductCargoBean)list3.get(j);
//					int stockExchangeId=sepc.getStockExchangeId();
//					StockExchangeBean se=psService.getStockExchange("id="+stockExchangeId);
//					if(se!=null){
//						if(se.getStatus()==StockExchangeBean.STATUS2||se.getStatus()==StockExchangeBean.STATUS3
//								||se.getStatus()==StockExchangeBean.STATUS5||se.getStatus()==StockExchangeBean.STATUS6){
//							map3.put(se.getId()+"", "1");
//						}
//					}
//				}
//				relatedOrderCount+=map3.size();
//				//关联报损报溢单
//				HashMap map4=new HashMap();
//				List list4=bsbyService.getBsbyProductCargoList("cargo_id="+cargo.getId(), -1, -1, null);
//				for(int j=0;j<list4.size();j++){
//					BsbyProductCargoBean bpc=(BsbyProductCargoBean)list4.get(j);
//					int bsbyOperId=bpc.getBsbyOperId();
//					BsbyOperationnoteBean bsbyOperBean = bsbyService.getBsbyOperationnoteBean("id="+bsbyOperId);
//					if(bsbyOperBean!=null){
//						if(bsbyOperBean.getCurrent_type()==BsbyOperationnoteBean.audit_ing){
//							map4.put(bsbyOperBean.getId()+"", "1");
//						}
//					}
//				}
//				relatedOrderCount+=map4.size();
//				relatedOrderCountList.add(relatedOrderCount+"");
//			}
			
			request.setAttribute("cargoList", cargoList);
			request.setAttribute("storageList", storageList);
//			request.setAttribute("relatedOrderCountList", relatedOrderCountList);
			request.setAttribute("paging", paging);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			cargoService.releaseAll();
		}
		return mapping.findForward("changeCargoPropertyList");
	}
	
	//批量修改货位属性__显示批量修改货位属性页面
	public ActionForward showChangeCargoProperty(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String storageId=StringUtil.convertNull(request.getParameter("storageId")).trim();
		String stockAreaId=StringUtil.convertNull(request.getParameter("stockAreaId")).trim();
		String shelfCode=StringUtil.convertNull(request.getParameter("shelfCode")).trim();
		String[] cargoIds=request.getParameterValues("cargoId");
		String selectAll=StringUtil.convertNull(request.getParameter("selectAll"));//是否点了全选
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService cargoService=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			List productLineList=wareService.getProductLineList("1=1");
			request.setAttribute("productLineList", productLineList);
			//判断货位状态
			if(selectAll.equals("on")){//全选
				StringBuilder condition=new StringBuilder();
				condition.append("(status=0 or status=1)");
				if(!storageId.equals("")){
					condition.append(" and storage_id=");
					condition.append(storageId);
				}
				if(!stockAreaId.equals("")){
					condition.append(" and stock_area_id=");
					condition.append(stockAreaId);
				}
				if(!shelfCode.equals("")){
					condition.append(" and whole_code like '");
					condition.append(shelfCode);
					condition.append("%'");
				}
				//该条件下的所有货位
				List cargoList=cargoService.getCargoInfoList(condition.toString(), -1, -1, null);
				for(int i=0;i<cargoList.size();i++){
					CargoInfoBean cargo=(CargoInfoBean)cargoList.get(i);
					if(cargo.getStatus()!=1){
						request.setAttribute("changed", "2");
						return mapping.findForward("changeCargoProperty");
					}
				}
			}else{//非全选
				for(int i=0;i<cargoIds.length;i++){
					String cargoId=cargoIds[i];
					CargoInfoBean cargo=cargoService.getCargoInfo("id="+cargoId);
					if(cargo.getStatus()!=1){
						request.setAttribute("changed", "2");
						return mapping.findForward("changeCargoProperty");
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			cargoService.releaseAll();
		}
		return mapping.findForward("changeCargoProperty");
	}
	
	
	//批量修改货位属性__属性修改
	public ActionForward changeCargoProperty(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String storageId=StringUtil.convertNull(request.getParameter("storageId")).trim();
		String stockAreaId=StringUtil.convertNull(request.getParameter("stockAreaId")).trim();
		String shelfCode=StringUtil.convertNull(request.getParameter("shelfCode")).trim();
		String[] cargoIds=request.getParameterValues("cargoId");
		String selectAll=StringUtil.convertNull(request.getParameter("selectAll"));//是否点了全选
		
		String stockType=StringUtil.convertNull(request.getParameter("stockType"));
		String storeType=StringUtil.convertNull(request.getParameter("storeType"));
		String type=StringUtil.convertNull(request.getParameter("type"));
		String productLineId=StringUtil.convertNull(request.getParameter("productLineId"));
		String warnStockCount=StringUtil.convertNull(request.getParameter("warnStockCount")).trim();
		String maxStockCount=StringUtil.convertNull(request.getParameter("maxStockCount")).trim();
		String length=StringUtil.convertNull(request.getParameter("length")).trim();
		String width=StringUtil.convertNull(request.getParameter("width")).trim();
		String high=StringUtil.convertNull(request.getParameter("high")).trim();
		
		WareService wareService = new WareService();
		ICargoService cargoService=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		synchronized (cargoLock) {
		try{
			wareService.getDbOp().startTransaction();
			request.setAttribute("productLineList", new ArrayList());
			StringBuilder set=new StringBuilder();
			if(!stockType.equals("")){
				set.append("stock_type=");
				set.append(stockType);
				set.append(",");
			}
			if(!storeType.equals("")){
				set.append("store_type=");
				set.append(storeType);
				set.append(",");
			}
			if(!type.equals("")){
				set.append("type=");
				set.append(type);
				set.append(",");
			}
			if(!productLineId.equals("")){
				set.append("product_line_id=");
				set.append(productLineId);
				set.append(",");
			}
			if(!warnStockCount.equals("")){
				set.append("warn_stock_count=");
				set.append(warnStockCount);
				set.append(",");
			}
			if(!maxStockCount.equals("")){
				set.append("max_stock_count=");
				set.append(maxStockCount);
				set.append(",");
			}
			if(!length.equals("")){
				set.append("length=");
				set.append(length);
				set.append(",");
			}
			if(!width.equals("")){
				set.append("width=");
				set.append(width);
				set.append(",");
			}
			if(!high.equals("")){
				set.append("high=");
				set.append(high);
				set.append(",");
			}
			set.deleteCharAt(set.length()-1);
			if(selectAll.equals("on")){//全选
				StringBuilder condition=new StringBuilder();
				condition.append("(status=0 or status=1)");
				if(!storageId.equals("")){
					condition.append(" and storage_id=");
					condition.append(storageId);
				}
				if(!stockAreaId.equals("")){
					condition.append(" and stock_area_id=");
					condition.append(stockAreaId);
				}
				if(!shelfCode.equals("")){
					condition.append(" and whole_code like '");
					condition.append(shelfCode);
					condition.append("%'");
				}
				
				
				//该条件下的所有货位
				List cargoList=cargoService.getCargoInfoList(condition.toString(), -1, -1, null);
				for(int i=0;i<cargoList.size();i++){
					CargoInfoBean cargo=(CargoInfoBean)cargoList.get(i);
					if(cargo.getStatus()!=1){
						request.setAttribute("changed", "2");
						return mapping.findForward("changeCargoProperty");
					}
				}
				cargoService.updateCargoInfo(set.toString(), condition.toString());
			}else{//非全选
				StringBuilder condition=new StringBuilder();
				condition.append("id in (");
				for(int i=0;i<cargoIds.length;i++){
					String cargoId=cargoIds[i];
					CargoInfoBean cargo=cargoService.getCargoInfo("id="+cargoId);
					if(cargo.getStatus()!=1){
						request.setAttribute("changed", "2");
						return mapping.findForward("changeCargoProperty");
					}
					condition.append(cargo.getId());
					condition.append(",");
				}
				condition.deleteCharAt(condition.length()-1);
				condition.append(")");
				cargoService.updateCargoInfo(set.toString(), condition.toString());
			}
			request.setAttribute("changed", "1");
			wareService.getDbOp().commitTransaction();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			cargoService.releaseAll();
		}
		}
		return mapping.findForward("changeCargoProperty");
	}
	
	//巷道列表
	public ActionForward cargoInfoPassageList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
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
		
		try{
			int countPerPage=20;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			}
			
			
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
			PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
            String para="";
            para=(cityId.equals("")?"":"&cityId="+cityId)
    			+(areaId.equals("")?"":"&areaId="+areaId)
    			+(storageId.equals("")?"":"&storageId="+storageId)
    			+(stockAreaId.equals("")?"":"&stockAreaId="+stockAreaId)
    			+(stockAreaCode.equals("")?"":"&stockAreaCode="+stockAreaCode)
    			+(passageCode.equals("")?"":"&passageCode="+passageCode)
    			;
            paging.setCurrentPageIndex(pageIndex);
            paging.setPrefixUrl("cargoInfo.do?method=cargoInfoPassageList"+para);
			
			List passageList=cargoService.getCargoInfoPassageList(condition, pageIndex*countPerPage, countPerPage, null);
			for(int i=0;i<passageList.size();i++){
				CargoInfoPassageBean passageBean=(CargoInfoPassageBean)passageList.get(i);
				int shelfCount=cargoService.getCargoInfoShelfCount("passage_id="+passageBean.getId());
				passageBean.setShelfCount(shelfCount);
			}
			List wholeCityList=cargoService.getCargoInfoCityList(null, -1, -1, null);//所有城市列表
			List wholeStorageList=cargoService.getCargoInfoStorageList(null, -1, -1, "id desc");//所有仓库列表
			request.setAttribute("list", passageList);
			request.setAttribute("wholeCityList", wholeCityList);
			request.setAttribute("wholeStorageList", wholeStorageList);
			request.setAttribute("paging", paging);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			cargoService.releaseAll();
		}
		return mapping.findForward("passageList");
	}
	
	//删除巷道
	public ActionForward deletePassage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
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
			
			request.setAttribute("list", passageList);
			List wholeCityList=cargoService.getCargoInfoCityList(null, -1, -1, null);//所有城市列表
			List wholeStorageList=cargoService.getCargoInfoStorageList(null, -1, -1, "id desc");//所有仓库列表
			request.setAttribute("wholeCityList", wholeCityList);
			request.setAttribute("wholeStorageList", wholeStorageList);
			cargoService.getDbOp().commitTransaction();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			cargoService.releaseAll();
		}
		}
		return mapping.findForward("passageList");
	}
	
	//添加巷道
	public ActionForward addPassage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
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
				request.setAttribute("tip", "请选择所属仓库！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(stockAreaId.equals("")){
				request.setAttribute("tip", "请选择所属区域！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			char[] ccode=passageCode.toCharArray();
			if(ccode.length==1||(ccode.length==2&&(ccode[0]<48||ccode[0]>57||ccode[1]<48||ccode[1]>57))||ccode.length>2||passageCode.equals("00")){
				request.setAttribute("tip", "请填写有效的巷道号！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			int passageCodeCount=service.getCargoInfoPassageCount("code='"+passageCode+"' and stock_area_id="+stockAreaId);
			if(passageCodeCount!=0){
				request.setAttribute("tip","该巷道号已存在，请重新填写！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			int currentCount=service.getCargoInfoPassageCount("stock_area_id="+stockAreaId);
			if(currentCount>=99){
				request.setAttribute("tip", "该仓库区域已有99个巷道，不能再添加！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(shelfCount>99){
				request.setAttribute("tip", "最多添加99个货架！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			CargoInfoStockAreaBean stockAreaBean=service.getCargoInfoStockArea("storage_id="+storageId+" and id="+stockAreaId);
			if(stockAreaBean==null){
				request.setAttribute("tip", "没有找到该仓库区域！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
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
					request.setAttribute("tip", "巷道号重复！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
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
			
			request.setAttribute("list", passageList);
			List wholeCityList=service.getCargoInfoCityList(null, -1, -1, null);//所有城市列表
			List wholeStorageList=service.getCargoInfoStorageList(null, -1, -1, "id desc");//所有仓库列表
			request.setAttribute("wholeCityList", wholeCityList);
			request.setAttribute("wholeStorageList", wholeStorageList);
			service.getDbOp().commitTransaction();
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		}finally{
			service.releaseAll();
		}
		}
		return mapping.findForward("passageList");
	}
	//货位条码打印
	public ActionForward cargoCodePrint(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
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
				return mapping.findForward("cargoCodePrint");
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
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			cargoList=service.getCargoInfoList(condition,-1,-1,"whole_code asc");
			if(cargoList==null||cargoList.size()==0){
				request.setAttribute("tip", "没有找到对应的条码信息");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			request.setAttribute("cargoList", cargoList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("cargoCodePrint");
	}
	
	
	
	//仓货位列表
	public ActionForward zcCargoList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		CartonningInfoService cartonningService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ResultSet rs = null;
		ResultSet rs2 = null;
		try{
			String wholeCode=StringUtil.convertNull(request.getParameter("wholeCode")).trim();//货位号
			String productCode=StringUtil.convertNull(request.getParameter("productCode")).trim();//产品编号
			String stockCountStart=StringUtil.convertNull(request.getParameter("stockCountStart")).trim();//库存最小值
			String stockCountEnd=StringUtil.convertNull(request.getParameter("stockCountEnd")).trim();//库存最大值
			String[] status=request.getParameterValues("status");
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
			int stockType = StringUtil.toInt(request.getParameter("stockType"));
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
			if(!stockCountStart.equals("")&&!stockCountStart.matches("[0-9]*")){
				request.setAttribute("tip", "货位当前库存，输入内容必须是数字，请重新输入！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(!stockCountEnd.equals("")&&!stockCountEnd.matches("[0-9]*")){
				request.setAttribute("tip", "货位当前库存，输入内容必须是数字，请重新输入！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(!shelfCode.equals("")&&!shelfCode.matches("[0-9]{2}")){
				request.setAttribute("tip", "货架代号，输入内容必须是两位数字，请重新输入！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(!floorNum.equals("")&&!floorNum.matches("[0-9]*")){
				request.setAttribute("tip", "货架层数，输入内容必须是数字，请重新输入！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			int countPerPage=20;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			}
            PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);
            String para="";
            para=(shelfId.equals("")?"":("&shelfId="+shelfId))
            	+(areaId.equals("")?"":("&areaId="+areaId))
    			+(wholeCode.equals("")?"":("&wholeCode="+wholeCode))
    			+(productCode.equals("")?"":("&productCode="+productCode))
    			+(stockCountStart.equals("")?"":("&stockCountStart="+stockCountStart))
    			+(stockCountEnd.equals("")?"":("&stockCountEnd="+stockCountEnd))
    			+(storeType.equals("")?"":("&storeType="+storeType))
    			+(status!=null&&status.length>=1?("&status="+status[0]):"")
    			+(status!=null&&status.length>=2?("&status="+status[1]):"")
    			+(status!=null&&status.length>=3?("&status="+status[2]):"")
    			+(productLineId.equals("")?"":("&productLineId="+productLineId))
    			+(productName.equals("")?"":"&productName="+paraProductName)
    			+(type.equals("")?"":("&type="+type))
    			+(storageId.equals("")?"":("&storageId="+storageId))
    			+(passageId.equals("")?"":("&passageId="+passageId))
    			+(stockAreaId.equals("")?"":("&stockAreaId="+stockAreaId))
    			+(shelfCode.equals("")?"":("&shelfCode="+shelfCode))
    			+(floorNum.equals("")?"":("&floorNum="+floorNum))
    			+(cartonningCode.equals("")?"":("&cartonningCode="+cartonningCode))
    			+(stockType == -1 ? "" : ("&stockType=" + stockType))
    			;
            paging.setCurrentPageIndex(pageIndex);
            paging.setPrefixUrl("cargoInfo.do?method=zcCargoList"+para);
            
			rs = service.getDbOp().executeQuery("select * from cargo_product_stock cps join product p on cps.product_id = p.id right join cargo_info ci on ci.id = cps.cargo_id" 
					+(" where ci.status<>3")
					+(!areaId.equals("")?" and ci.area_id = "+areaId:"")
					+(stockType != -1?" and ci.stock_type = "+stockType:"")
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
					+(" limit "+paging.getCurrentPageIndex() * countPerPage+","+countPerPage)
					);
			List list = this.getCargoAndProductStockList(rs);
			rs2 = service.getDbOp().executeQuery("select count(ci.id) from cargo_product_stock cps join product p on cps.product_id = p.id right join cargo_info ci on ci.id = cps.cargo_id" 
					+(" where ci.status<>3")
					+(!areaId.equals("")?" and ci.area_id = "+areaId:"")
					+(stockType != -1?" and ci.stock_type = "+stockType:"")
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
			paging.setTotalCount(totalCount);
			paging.setTotalPageCount(totalCount%countPerPage==0?totalCount/countPerPage:totalCount/countPerPage+1);
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
			if(!areaId.equals("")){
				storageList=service.getCargoInfoStorageList("area_id="+areaId, -1, -1, "whole_code asc");
			}
			for(int i=0;i<list.size();i++){
				CargoProductStockBean cps=(CargoProductStockBean)list.get(i);
				cps.setCartonningList(new ArrayList());
				List cartonningList=cartonningService.getCartonningList("cargo_id="+cps.getCargoId()+" and status!=2", -1, -1, null);
				for(int j=0;j<cartonningList.size();j++){
					CartonningInfoBean cartonningBean=(CartonningInfoBean)cartonningList.get(j);
					CartonningProductInfoBean cartonningProduct=cartonningService.getCartonningProductInfo("cartonning_id="+cartonningBean.getId());
					if(cartonningProduct!=null&&cartonningProduct.getProductId()==cps.getProductId()){
						cartonningBean.setProductBean(cartonningProduct);
						cps.getCartonningList().add(cartonningBean);
					}
				}
			}
			request.setAttribute("operCountList", operCountList);
			request.setAttribute("storageList", storageList);
			request.setAttribute("productLineList", productLineList);
			request.setAttribute("productLineNameList", productLineNameList);
			request.setAttribute("list", list);
			request.setAttribute("paging", paging);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("zcCargoList");
	}
	
}
