package adultadmin.action.cargo;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.bybs.ByBsAction;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.bybs.BsbyOperationRecordBean;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.bybs.BsbyProductBean;
import adultadmin.bean.bybs.BsbyProductCargoBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoInventoryBean;
import adultadmin.bean.cargo.CargoInventoryLogBean;
import adultadmin.bean.cargo.CargoInventoryMissionBean;
import adultadmin.bean.cargo.CargoInventoryMissionProductBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.framework.IConstants;
import adultadmin.service.IBsByServiceManagerService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class CargoInventoryAction extends DispatchAction {
	
	public Log stockLog = LogFactory.getLog("stock.Log");
	
	/**
	 *	添加盘点作业单
	 */
	public ActionForward addCargoInventory(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		
		if(!group.isFlag(410)){
			request.setAttribute("tip", "您无权添加盘点作业单！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		synchronized(CargoOperationAction.cargoLock){

			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult_slave");
			WareService wareService = new WareService(dbOp);
			try{
				//货位仓库及区域信息
				String action = StringUtil.convertNull(request.getParameter("action"));
				if(action.equals("add")){
					int type = StringUtil.toInt(request.getParameter("type"));
					int stockType = StringUtil.toInt(request.getParameter("stockType"));
					int storageId = StringUtil.toInt(request.getParameter("storageId"));
					String[] stockAreaIds = request.getParameterValues("stockAreaId");
					String remark = StringUtil.convertNull(request.getParameter("remark"));
					
					if(type < 0){
						request.setAttribute("tip", "作业单类型不能为空！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(stockType < 0){
						request.setAttribute("tip", "库存类型不能为空！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(storageId < 0){
						request.setAttribute("tip", "作业仓库不能为空！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(stockAreaIds == null){
						request.setAttribute("tip", "作业仓库区域不能为空！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(remark.length()>255){
						request.setAttribute("tip", "备注最多只允许输入255个字！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					String stockAreaId = "";
					for(int i=0;i<stockAreaIds.length;i++){
						stockAreaId = stockAreaId + stockAreaIds[i] + ",";
					}
					if(stockAreaId.endsWith(",")){
						stockAreaId = stockAreaId.substring(0, stockAreaId.length()-1);
					}

					service.getDbOp().startTransaction();
					
					String code = "PD"+DateUtil.getNow().substring(0,10).replace("-", "");
					int maxid = service.getNumber("id", "cargo_inventory", "max", "id > 0");
					CargoInventoryBean inventory = service.getCargoInventory("code like '" + code + "%'");
					if(inventory == null){
						//当日第一份单据，编号最后三位 001
						code += "001";
					}else {
						//获取当日计划编号最大值
						inventory = service.getCargoInventory("id =" + maxid); 
						String _code = inventory.getCode();
						int number = Integer.parseInt(_code.substring(_code.length()-3));
						number++;
						code += String.format("%03d",new Object[]{new Integer(number)});
					}
					
					//添加盘点作业单
					inventory = new CargoInventoryBean();
					inventory.setCode(code);
					inventory.setType(type);
					inventory.setCreateDatetime(DateUtil.getNow());
					inventory.setCreateAdminId(user.getId());
					inventory.setCreateAdminName(user.getUsername());
					inventory.setStatus(CargoInventoryBean.STATUS1);
					inventory.setStage(1);
					inventory.setRemark(remark);
					if(!service.addCargoInventory(inventory)){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "操作失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					inventory.setId(service.getDbOp().getLastInsertId());
					
					
					List cargoList = service.getCargoInfoList("status in (0,1) and storage_id = "+storageId+" and stock_area_id in ("+stockAreaId+") and stock_type = "+stockType, -1, -1, "whole_code asc");
					Iterator iter = cargoList.listIterator();
					while(iter.hasNext()){
						CargoInfoBean cargo = (CargoInfoBean)iter.next();
						
						//原始数据快照
						CargoInventoryMissionBean mission = new CargoInventoryMissionBean();
						mission.setCargoInventoryId(inventory.getId());
						mission.setType(0);
						mission.setStatus(CargoInventoryMissionBean.STATUS0);
						mission.setCargoId(cargo.getId());
						mission.setCargoWholeCode(cargo.getWholeCode());
						mission.setStockType(cargo.getStockType());
						mission.setCargoStockAreaId(cargo.getStockAreaId());
						mission.setCargoStockAreaCode(cargo.getWholeCode().substring(6, 7));
						mission.setCargoStorageId(cargo.getStorageId());
						mission.setCargoStorageCode(cargo.getWholeCode().substring(3,4));
						if(cargo.getAreaId() == 1){
							mission.setCargoPassageId(0);
							mission.setCargoPassageCode("");
							mission.setCargoShelfId(cargo.getShelfId());
							mission.setCargoShelfCode(cargo.getWholeCode().substring(7, 9));
						}else if(cargo.getAreaId() == 3){
							mission.setCargoPassageId(cargo.getPassageId());
							mission.setCargoPassageCode(cargo.getWholeCode().substring(7, 9));
							mission.setCargoShelfId(cargo.getShelfId());
							mission.setCargoShelfCode(cargo.getWholeCode().substring(9, 11));
						}
						mission.setInventoryResult(0);
						if(!service.addCargoInventoryMission(mission)){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "操作失败！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						mission.setId(service.getDbOp().getLastInsertId());
						
						List cargoProductStockList = service.getCargoProductStockList("cargo_id = "+cargo.getId(), -1, -1, "id asc");
						Iterator iter2 = cargoProductStockList.listIterator();
						while(iter2.hasNext()){
							CargoProductStockBean cps = (CargoProductStockBean)iter2.next();
							voProduct product = wareService.getProductSimple(cps.getProductId());
							
							if(product!=null){
								CargoInventoryMissionProductBean missionProduct = new CargoInventoryMissionProductBean();
								missionProduct.setCargoInventoryId(inventory.getId());
								missionProduct.setCargoInventoryMissionId(mission.getId());
								missionProduct.setProductId(cps.getProductId());
								missionProduct.setProductCode(product.getCode());
								missionProduct.setProductOriname(product.getOriname());
								missionProduct.setProductParentId1(product.getParentId1());
								missionProduct.setStockCount(cps.getStockCount()+cps.getStockLockCount());
								if(!service.addCargoInventoryMissionProduct(missionProduct)){
									service.getDbOp().rollbackTransaction();
									request.setAttribute("tip", "操作失败！");
									request.setAttribute("result", "failure");
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
							}
						}
						
						int oriMissionId = mission.getId();
						
						//初盘未分配数据
						mission = new CargoInventoryMissionBean();
						mission.setCargoInventoryId(inventory.getId());
						mission.setType(1);
						mission.setStatus(CargoInventoryMissionBean.STATUS0);
						mission.setCargoId(cargo.getId());
						mission.setCargoWholeCode(cargo.getWholeCode());
						mission.setStockType(cargo.getStockType());
						if(cargo.getAreaId() == 1){
							mission.setCargoPassageId(0);
							mission.setCargoPassageCode("");
							mission.setCargoShelfId(cargo.getShelfId());
							mission.setCargoShelfCode(cargo.getWholeCode().substring(7, 9));
						}else if(cargo.getAreaId() == 3){
							mission.setCargoPassageId(cargo.getPassageId());
							mission.setCargoPassageCode(cargo.getWholeCode().substring(7, 9));
							mission.setCargoShelfId(cargo.getShelfId());
							mission.setCargoShelfCode(cargo.getWholeCode().substring(9, 11));
						}
						mission.setCargoStockAreaId(cargo.getStockAreaId());
						mission.setCargoStockAreaCode(cargo.getWholeCode().substring(6, 7));
						mission.setCargoStorageId(cargo.getStorageId());
						mission.setCargoStorageCode(cargo.getWholeCode().substring(3,4));
						mission.setOriMissionId(oriMissionId);
						if(!service.addCargoInventoryMission(mission)){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "操作失败！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
					
					CargoInventoryLogBean log = new CargoInventoryLogBean();
					log.setCargoInventoryId(inventory.getId());
					log.setLogType(0);
					log.setOperDatetime(DateUtil.getNow());
					log.setOperAdminId(user.getId());
					log.setOperAdminName(user.getUsername());
					log.setRemark("生成盘点作业单："+inventory.getCode());
					service.addCargoInventoryLog(log);

					service.getDbOp().commitTransaction();
					
					return new ActionForward("/admin/cargoInventory.do?method=assignCargoInventoryMissionPage&id="+inventory.getId());
				}
				
			}catch (Exception e) {
				e.printStackTrace();
				stockLog.error(StringUtil.getExceptionInfo(e));
			}finally{
				service.releaseAll();
				wareService.releaseAll();
			}
		}

		return mapping.findForward("addCargoInventory");
	}
	
	
	/**
	 *	盘点作业单列表
	 */
	public ActionForward cargoInventoryList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		
		if(!group.isFlag(411)){
			request.setAttribute("tip", "您无权查看盘点作业单列表！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			int countPerPage = 20;
			//总数
			int totalCount = service.getCargoInventoryCount(null);
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List list = service.getCargoInventoryList(null, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
			Iterator iter = list.listIterator();
			while(iter.hasNext()){
				CargoInventoryBean inventory = (CargoInventoryBean)iter.next();
				
				//计算数量
				ResultSet rs = service.getDbOp().executeQuery(
						"SELECT count(distinct cargo_stock_area_id),count(distinct cargo_shelf_id),count(distinct cargo_id),stock_type,left(cargo_whole_code,5),group_concat(distinct cargo_stock_area_code),count(distinct staff_id) " +
						"FROM cargo_inventory_mission where cargo_inventory_id = "+inventory.getId()+" and type = 1 group by cargo_inventory_id");
				if(rs.next()){
					inventory.setStockAreaCount(rs.getInt(1));
					inventory.setShelfCount(rs.getInt(2));
					inventory.setCargoCount(rs.getInt(3));
					inventory.setStockType(rs.getInt(4));
					inventory.setStorageCode(rs.getString(5));
					inventory.setStockAreaCode(rs.getString(6));
					inventory.setMemberCount(rs.getInt(7));
				}
				
				//判断是否需要结束
				if(service.getCargoInventoryMissionCount("cargo_inventory_id = "+inventory.getId()+" and type = "+inventory.getStage()+" and status < 6")==0 && inventory.getStatus() == CargoInventoryBean.STATUS3){
					service.updateCargoInventory("status = "+CargoInventoryBean.STATUS4, "id = "+inventory.getId());
				}
			}
			request.setAttribute("list", list);
			
			paging.setPrefixUrl("cargoInventory.do?method=cargoInventoryList");
			request.setAttribute("pageBean", paging);
		}catch (Exception e) {
			e.printStackTrace();
			stockLog.error(StringUtil.getExceptionInfo(e));
		}finally{
			service.releaseAll();
		}
		
		return mapping.findForward("cargoInventoryList");
	}
	
	/**
	 *	删除盘点作业单
	 */
	public ActionForward deleteCargoInventory(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		
		synchronized(CargoOperationAction.cargoLock){

			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
			try{
				
				int id = StringUtil.StringToId(request.getParameter("id"));
				
				CargoInventoryBean inventory = service.getCargoInventory("id = "+id);
				if(inventory == null){
					request.setAttribute("tip", "盘点作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(inventory.getStatus() >= CargoInventoryBean.STATUS3 || inventory.getStage()>1){
					request.setAttribute("tip", "任务已启动，无法删除这个盘点作业单！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(inventory.getCreateAdminId()!=user.getId()&&!group.isFlag(412)){
					request.setAttribute("tip", "您无权删除这个盘点作业单！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				
				service.getDbOp().startTransaction();
				service.deleteCargoInventoryMissionProduct("cargo_inventory_id = "+id);
				service.deleteCargoInventoryMission("cargo_inventory_id = "+id);
				service.deleteCargoInventory("id = "+id);
				service.getDbOp().commitTransaction();
				
			}catch (Exception e) {
				e.printStackTrace();
				stockLog.error(StringUtil.getExceptionInfo(e));
			}finally{
				service.releaseAll();
			}
		}
		return cargoInventoryList(mapping, form, request, response);
	}
	
	/**
	 *	删除盘点作业任务(已分配、未分配)
	 */
	public ActionForward deleteCargoInventoryMission(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
//		UserGroupBean group = user.getGroup();
		
		synchronized(CargoOperationAction.cargoLock){

			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
			try{
				
				int id = StringUtil.StringToId(request.getParameter("id"));
				
				CargoInventoryBean inventory = service.getCargoInventory("id = "+id);
				if(inventory == null){
					request.setAttribute("tip", "盘点作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				
				int missionStatus = StringUtil.toInt(request.getParameter("missionStatus"));
				
				if(missionStatus == 0){
					String[] shelfIds = request.getParameterValues("shelfId");
					if(shelfIds == null || shelfIds.length == 0){
						request.setAttribute("tip", "请选择货架号再删除！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					String shelfId = "-1";
					for(int i=0;i<shelfIds.length;i++){
						shelfId = shelfId + "," + shelfIds[i];
					}
					
					service.getDbOp().startTransaction();
					
					List missionList = service.getCargoInventoryMissionList("cargo_inventory_id = "+id+" and status = 0 and type = "+inventory.getStage()+" and cargo_shelf_id in ("+shelfId+")", -1, -1, "id asc");
					Iterator iter = missionList.listIterator();
					while(iter.hasNext()){
						CargoInventoryMissionBean mission = (CargoInventoryMissionBean)iter.next();
						service.deleteCargoInventoryMissionProduct("cargo_inventory_mission_id = "+mission.getId());
						service.deleteCargoInventoryMission("id = "+mission.getId());
					}
					if(inventory.getStage()==1){
						missionList = service.getCargoInventoryMissionList("cargo_inventory_id = "+id+" and type = 0 and cargo_shelf_id in ("+shelfId+")", -1, -1, "id asc");
						iter = missionList.listIterator();
						while(iter.hasNext()){
							CargoInventoryMissionBean mission = (CargoInventoryMissionBean)iter.next();
							service.deleteCargoInventoryMissionProduct("cargo_inventory_mission_id = "+mission.getId());
							service.deleteCargoInventoryMission("id = "+mission.getId());
						}
					}
					
					//判断如果全部分配完毕，作业单状态改完未启动
					if(service.getCargoInventoryMissionCount("cargo_inventory_id = "+id+" and status = 0 and type = "+inventory.getStage())==0){
						service.updateCargoInventory("status = 2", "id = "+id);
						service.getDbOp().commitTransaction();
						return cargoInventory(mapping, form, request, response);
					}
					
					service.getDbOp().commitTransaction();
					
				}else if(missionStatus == 1){
					String[] staffIds = request.getParameterValues("assignStaffId");
					if(staffIds == null || staffIds.length == 0){
						request.setAttribute("tip", "请选择员工再删除！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					String staffId = "-1";
					for(int i=0;i<staffIds.length;i++){
						staffId = staffId + "," + staffIds[i];
					}
					
					service.updateCargoInventoryMission(
							"status = 0,staff_id = 0,staff_code = null,staff_name = null", "cargo_inventory_id = "+id+" and status = 1 and type = "+inventory.getStage()+" and staff_id in ("+staffId+")");
				}
				
			}catch (Exception e) {
				e.printStackTrace();
				stockLog.error(StringUtil.getExceptionInfo(e));
			}finally{
				service.releaseAll();
			}
		}
		return assignCargoInventoryMissionPage(mapping, form, request, response);
	}
	
	/**
	 *	盘点作业单分配任务页面
	 */
	public ActionForward assignCargoInventoryMissionPage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		try{
			
			int id = StringUtil.StringToId(request.getParameter("id"));
			CargoInventoryBean inventory = service.getCargoInventory("id = "+id);
			if(inventory == null){
				//System.out.print("inventoryID:"+id);
				request.setAttribute("tip", "盘点作业单不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			List staffList = service.getCargoStaffList(null, -1, -1, null);
			//计算数量
			ResultSet rs = service.getDbOp().executeQuery(
					"SELECT count(distinct cargo_stock_area_id),count(distinct cargo_shelf_id),count(distinct cargo_id),max(`type`) FROM cargo_inventory_mission where status <> 6 and type = "+inventory.getStage()+" and cargo_inventory_id = "+id);
			if(rs.next()){
				inventory.setStockAreaCount(rs.getInt(1));
				inventory.setShelfCount(rs.getInt(2));
				inventory.setCargoCount(rs.getInt(3));
			}
			
			//未分配任务表
			List noAssignMissionList = new ArrayList();
			rs = service.getDbOp().executeQuery(
					"select left(cargo_whole_code,11),cargo_shelf_id,count(cargo_id) c from cargo_inventory_mission where status = 0 and type = "+inventory.getStage()+" and cargo_inventory_id = "+id+" group by cargo_shelf_id order by cargo_whole_code asc");
			while(rs.next()){
				HashMap map = new HashMap();
				map.put("shelfWholeCode", rs.getString(1));
				map.put("shelfId", rs.getString(2));
				map.put("cargoCount", rs.getString(3));
				noAssignMissionList.add(map);
			}

			//已分配任务列表
			LinkedHashMap assignMissionMap = new LinkedHashMap();
			rs = service.getDbOp().executeQuery(
					"select staff_id,staff_code,staff_name,group_concat(distinct left(cargo_whole_code,11)),count(distinct cargo_shelf_id),count(distinct cargo_id) from cargo_inventory_mission where status = 1 and type = "+inventory.getStage()+" and cargo_inventory_id = "+id+" group by staff_id order by staff_code asc");
			while(rs.next()){
				HashMap map = new HashMap();
				map.put("staffId", rs.getString(1));
				map.put("staffCode", rs.getString(2));
				map.put("staffName", rs.getString(3));
				map.put("shelfWholeCode", rs.getString(4));
				map.put("shelfCount", rs.getString(5));
				map.put("cargoCount", rs.getString(6));
				assignMissionMap.put(rs.getString(1), map);
			}
			
			request.setAttribute("inventory", inventory);
			request.setAttribute("staffList", staffList);
			request.setAttribute("noAssignMissionList", noAssignMissionList);
			request.setAttribute("assignMissionMap", assignMissionMap);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		
		return mapping.findForward("assignCargoInventoryMission");
	}
	
	/**
	 *	分配盘点任务
	 */
	public ActionForward assignCargoInventoryMission(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			synchronized(CargoOperationAction.cargoLock){
				int id = StringUtil.StringToId(request.getParameter("id"));
				CargoInventoryBean inventory = service.getCargoInventory("id = "+id);
				if(inventory == null){
					request.setAttribute("tip", "盘点作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				String[] shelfIds = request.getParameterValues("shelfId");
				if(shelfIds == null || shelfIds.length == 0){
					request.setAttribute("tip", "请选择货架号再分配！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				int staffId = StringUtil.StringToId(request.getParameter("staffId"));
				if(staffId == 0){
					request.setAttribute("tip", "请选择员工！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				CargoStaffBean staff = service.getCargoStaff("id = "+staffId);
				if(staff == null){
					request.setAttribute("tip", "员工信息不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				String shelfId = "-1";
				for(int i=0;i<shelfIds.length;i++){
					shelfId = shelfId + "," + shelfIds[i];
				}

				service.updateCargoInventoryMission(
						"status = "+CargoInventoryMissionBean.STATUS1+",staff_id="+staff.getId()+
						",staff_code='"+staff.getCode()+"',staff_name='"+staff.getName()+"'", 
						"cargo_inventory_id = "+id+" and cargo_shelf_id in ("+shelfId+") and type = "+inventory.getStage()+" and status = 0");

				//判断如果全部分配完毕，作业单状态改完未启动
				if(service.getCargoInventoryMissionCount("cargo_inventory_id = "+id+" and status = 0 and type = "+inventory.getStage())==0){
					service.updateCargoInventory("status = 2", "id = "+id);
					return cargoInventory(mapping, form, request, response);
				}

				request.setAttribute("inventory", inventory);

			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		
		return assignCargoInventoryMissionPage(mapping, form, request, response);
	}
	
	/**
	 *	分配复盘盘点任务
	 */
	public ActionForward assignCargoReinventoryMission(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			synchronized(CargoOperationAction.cargoLock){
				int id = StringUtil.StringToId(request.getParameter("id"));
				CargoInventoryBean inventory = service.getCargoInventory("id = "+id);
				if(inventory == null){
					request.setAttribute("tip", "盘点作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				String addAction = StringUtil.convertNull(request.getParameter("addAction"));
				int type = StringUtil.StringToId(request.getParameter("type"));
				String[] cargoIds = request.getParameterValues("cargoId");
				if((cargoIds == null || cargoIds.length == 0)&&addAction.equals("")){
					request.setAttribute("tip", "请选择货位再添加！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				String cargoId = "-1";
				if(cargoIds != null){
					for(int i=0;i<cargoIds.length;i++){
						cargoId = cargoId + "," + cargoIds[i];
					}
				}

				String condition = "cargo_inventory_id = "+id;
				if(addAction.equals("")){
					condition = condition + " and cargo_id in ("+cargoId+")";
				}
				if(inventory.getStatus() == CargoInventoryBean.STATUS4){
					condition = condition + " and type = "+inventory.getStage();
				}else if(inventory.getStatus() == CargoInventoryBean.STATUS1){
					condition = condition + " and type = "+(inventory.getStage()-1);
				}
				if(type == 1){
					condition = condition + " and inventory_result = 1";
				}
				
				List missionList = service.getCargoInventoryMissionList(condition, -1, -1, "id asc");
				Iterator iter = missionList.listIterator();
				while(iter.hasNext()){
					CargoInventoryMissionBean mission = (CargoInventoryMissionBean)iter.next();
					
					//判断是否已生成复盘任务
					if(service.getCargoInventoryMissionCount("cargo_inventory_id = "+id+" and type > "+mission.getType()+" and cargo_id = "+mission.getCargoId())>0){
						continue;
					}
					
					CargoInventoryMissionBean newMission = new CargoInventoryMissionBean();
					newMission.setCargoInventoryId(inventory.getId());
					newMission.setType(mission.getType()+1);
					newMission.setStatus(CargoInventoryMissionBean.STATUS0);
					newMission.setCargoId(mission.getCargoId());
					newMission.setCargoWholeCode(mission.getCargoWholeCode());
					newMission.setStockType(mission.getStockType());
					newMission.setCargoShelfId(mission.getCargoShelfId());
					newMission.setCargoShelfCode(mission.getCargoWholeCode().substring(7, 9));
					newMission.setCargoStockAreaId(mission.getCargoStockAreaId());
					newMission.setCargoStockAreaCode(mission.getCargoWholeCode().substring(6, 7));
					newMission.setCargoStorageId(mission.getCargoStorageId());
					newMission.setCargoStorageCode(mission.getCargoWholeCode().substring(3,4));
					if(!service.addCargoInventoryMission(newMission)){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "操作失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					
				}

				if(inventory.getStatus() == CargoInventoryBean.STATUS4){
					service.updateCargoInventory("status = "+CargoInventoryBean.STATUS1+",stage = stage+1", "id = "+inventory.getId());
				}

				request.setAttribute("inventory", inventory);

			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		
		return cargoInventory(mapping, form, request, response);
	}
	
	/**
	 *	启动盘点
	 */
	public ActionForward startCargoInventory(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			synchronized(CargoOperationAction.cargoLock){
				int id = StringUtil.StringToId(request.getParameter("id"));
				CargoInventoryBean inventory = service.getCargoInventory("id = "+id);
				if(inventory == null){
					request.setAttribute("tip", "盘点作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				
				service.getDbOp().startTransaction();
				if(!service.updateCargoInventory("status = "+CargoInventoryBean.STATUS3+",start_datetime = '"+DateUtil.getNow()+"'", "id = "+id)){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(!service.updateCargoInventoryMission("status = "+CargoInventoryMissionBean.STATUS2,"cargo_inventory_id = "+id+" and type = "+inventory.getStage()+" and status = 1")){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				service.getDbOp().commitTransaction();

				request.setAttribute("inventory", inventory);

			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		
		return cargoInventory(mapping, form, request, response);
	}
	
	/**
	 *	强制结束盘点
	 */
	public ActionForward endCargoInventory(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String back = StringUtil.convertNull(request.getParameter("back"));
		
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			synchronized(CargoOperationAction.cargoLock){
				int id = StringUtil.StringToId(request.getParameter("id"));
				CargoInventoryBean inventory = service.getCargoInventory("id = "+id);
				if(inventory == null){
					request.setAttribute("tip", "盘点作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				
				service.getDbOp().startTransaction();
				if(!service.updateCargoInventory("status = "+CargoInventoryBean.STATUS4+",end_datetime = '"+DateUtil.getNow()+"'", "id = "+id)){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				service.updateCargoInventoryMission("status = "+CargoInventoryMissionBean.STATUS6,"cargo_inventory_id = "+id+" and type = "+inventory.getStage()+" and status <= 6 and status >3");
				service.updateCargoInventoryMission("status = "+CargoInventoryMissionBean.STATUS7,"cargo_inventory_id = "+id+" and type = "+inventory.getStage()+" and status <=3");
				
				service.getDbOp().commitTransaction();

				request.setAttribute("inventory", inventory);

			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		
		if(back.equals("list")){
			return cargoInventoryList(mapping, form, request, response);
		}
		
		return cargoInventory(mapping, form, request, response);
	}
	
	/**
	 *	盘点作业单详细页
	 */
	public ActionForward cargoInventory(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		IBsByServiceManagerService bsbyService = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService service2 = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		try{
			
			int id = StringUtil.StringToId(request.getParameter("id"));
			CargoInventoryBean inventory = service.getCargoInventory("id = "+id);
			if(inventory == null){
				request.setAttribute("tip", "盘点作业单不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			//计算数量
			ResultSet rs = service.getDbOp().executeQuery(
					"SELECT count(distinct cargo_stock_area_id),count(distinct cargo_shelf_id),count(distinct cargo_id),stock_type,left(cargo_whole_code,5),group_concat(distinct cargo_stock_area_code) " +
					"FROM cargo_inventory_mission where cargo_inventory_id = "+id+" and type = "+inventory.getStage()+" group by cargo_inventory_id");
			if(rs.next()){
				inventory.setStockAreaCount(rs.getInt(1));	
				inventory.setShelfCount(rs.getInt(2));
				inventory.setCargoCount(rs.getInt(3));
				inventory.setStockType(rs.getInt(4));
				inventory.setStorageCode(rs.getString(5));
				inventory.setStockAreaCode(rs.getString(6));
			}
			
			//任务列表
			LinkedHashMap missionMap = new LinkedHashMap();
			LinkedHashMap missionMap2 = new LinkedHashMap();
			for(int i=1;i<=inventory.getStage();i++){
				List missionList = new ArrayList();
				rs = service.getDbOp().executeQuery(
						"select staff_id,staff_code,staff_name,group_concat(distinct concat(cargo_stock_area_code,cargo_passage_code,'-',cargo_shelf_code)),count(distinct cargo_shelf_id),count(distinct cargo_id)," +
						"(select count(id) from cargo_inventory_mission where (status < 5 or status = 7) and type = "+i+" and cargo_inventory_id = "+id+" and staff_id = a.staff_id),min(start_datetime),max(end_datetime) "+
						"from cargo_inventory_mission a where status <> 0 and type = "+i+" and cargo_inventory_id = "+id+" group by staff_id order by staff_code asc");
				while(rs.next()){
					HashMap map = new HashMap();
					map.put("staffId", rs.getString(1));
					map.put("staffCode", rs.getString(2));
					map.put("staffName", rs.getString(3));
					map.put("shelfCode", rs.getString(4));
					map.put("shelfCount", rs.getString(5));
					map.put("cargoCount", rs.getString(6));
					map.put("unCompleteCount", rs.getString(7));
					map.put("startDatetime", rs.getString(8));
					map.put("endDatetime", rs.getString(9));
					missionList.add(map);
				}
				missionMap.put(String.valueOf(i), missionList);
				
				//任务数量
				HashMap missionCountMap = new HashMap();
				rs = service.getDbOp().executeQuery(
						"select count(id)," +
						"(select count(id) from cargo_inventory_mission where status = 0 and type = "+i+" and cargo_inventory_id = "+id+"), "+
						"(select count(id) from cargo_inventory_mission where status <> 0 and type = "+i+" and cargo_inventory_id = "+id+"),"+
						"(select count(id) from cargo_inventory_mission where status < 5 and type = "+i+" and cargo_inventory_id = "+id+"),"+
						"(select count(id) from cargo_inventory_mission where status >= 5 and type = "+i+" and cargo_inventory_id = "+id+"),"+
						"(select count(id) from cargo_inventory_mission where status >= 5 and inventory_result = 1 and type = "+i+" and cargo_inventory_id = "+id+")"+
						"from cargo_inventory_mission where type = "+i+" and cargo_inventory_id = "+id);
				if(rs.next()){
					missionCountMap.put("cargoCount", rs.getString(1));
					missionCountMap.put("noAssignCargoCount", rs.getString(2));
					missionCountMap.put("assignCargoCount", rs.getString(3));
					missionCountMap.put("unCompleteCargoCount", rs.getString(4));
					missionCountMap.put("completeCargoCount", rs.getString(5));
					missionCountMap.put("differentCargoCount", rs.getString(6));
				}
				missionMap2.put(String.valueOf(i), missionCountMap);
			}
			
			request.setAttribute("inventory", inventory);
			request.setAttribute("missionMap", missionMap);
			request.setAttribute("missionMap2", missionMap2);
			
			//库存调整部分
			if(inventory.getStatus() == CargoInventoryBean.STATUS5){
				List bsbyList = bsbyService.getByBsOperationnoteList("source = "+inventory.getId()+" and if_del = 0", -1, -1, null);
				request.setAttribute("bsbyList", bsbyList);
				
				if(bsbyList != null && bsbyList.size() > 0){  //货位数、调整数量
					HashMap bsbyMap = new HashMap();
					for(int i=0;i<bsbyList.size();i++){
						BsbyOperationnoteBean bean = (BsbyOperationnoteBean)bsbyList.get(i);
						rs = service.getDbOp().executeQuery("select count(distinct cargo_id) c, sum(count) from bsby_product_cargo where bsby_oper_id = "+bean.getId());
						if(rs.next()){
							HashMap map = new HashMap();
							map.put("countCargo", String.valueOf(rs.getInt(1)));
							map.put("sumBsbyCount", String.valueOf(rs.getInt(2)));
							bsbyMap.put(String.valueOf(bean.getId()), map);
						}
					}
					request.setAttribute("bsbyMap", bsbyMap);
				}
			}
			
			//判断是否需要结束
			if(service.getCargoInventoryMissionCount("cargo_inventory_id = "+id+" and type = "+inventory.getStage()+" and status < 6")==0 && inventory.getStatus() == CargoInventoryBean.STATUS3){
				service2.updateCargoInventory("status = "+CargoInventoryBean.STATUS4, "id = "+id);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
			service2.releaseAll();
		}
		
		return mapping.findForward("cargoInventory");
	}
	
	/**
	 *	复盘任务
	 */
	public ActionForward assignCargoReinventoryMissionPage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		try{
			
			int id = StringUtil.StringToId(request.getParameter("id"));
			CargoInventoryBean inventory = service.getCargoInventory("id = "+id);
			if(inventory == null){
				request.setAttribute("tip", "盘点作业单不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			int type = StringUtil.StringToId(request.getParameter("type"));
			String condition = "cargo_inventory_id = "+id;
			if(inventory.getStatus() == CargoInventoryBean.STATUS4){
				condition = condition + " and type <= "+inventory.getStage();
			}else if(inventory.getStatus() == CargoInventoryBean.STATUS1){
				condition = condition + " and type <= "+(inventory.getStage()-1);
			}
			if(type == 1){
				condition = condition + " and inventory_result = 1";
			}
			List allMissionList = service.getCargoInventoryMissionList(condition, -1, -1, "type asc,cargo_whole_code asc");
			Iterator iter = allMissionList.listIterator();
			HashMap map = new LinkedHashMap();
			int maxStage = 0;
			while(iter.hasNext()){
				CargoInventoryMissionBean mission = (CargoInventoryMissionBean)iter.next();
				if(map.get(mission.getCargoWholeCode())!=null){
					continue;
				}
				//判断是否已生成复盘任务
				if(service.getCargoInventoryMissionCount("cargo_inventory_id = "+id+" and type > "+mission.getType()+" and cargo_id = "+mission.getCargoId())>0){
					continue;
				}
				
				List list = service.getCargoInventoryMissionList("cargo_inventory_id = "+id+" and (type=0 or (type>0 and status>5)) and cargo_id = "+mission.getCargoId(), -1, -1, "type asc");
				if(list.size()>maxStage){
					maxStage = list.size();
				}
				for(int i=0;i<list.size();i++){
					CargoInventoryMissionBean bean = (CargoInventoryMissionBean)list.get(i);
					
					List missionProductList = service.getCargoInventoryMissionProductList("cargo_inventory_mission_id = "+bean.getId(), -1, -1, "id asc");
					bean.setCargoInventoryMissionProductList(missionProductList);
				}
				
				map.put(mission.getCargoWholeCode(), list);
			}
			
			//补齐盘点货位表
			iter = allMissionList.listIterator();
			while(iter.hasNext()){
				CargoInventoryMissionBean mission = (CargoInventoryMissionBean)iter.next();
				if(mission.getType()>1){
					continue;
				}
				//判断是否已生成复盘任务
				if(service.getCargoInventoryMissionCount("cargo_inventory_id = "+id+" and type > "+mission.getType()+" and cargo_id = "+mission.getCargoId())>0){
					continue;
				}
				
				List list = (List)map.get(mission.getCargoWholeCode());
				if(list.size()<maxStage){
					for(int i=0;i<maxStage-list.size();i++){
						CargoInventoryMissionBean newMission = new CargoInventoryMissionBean();
						newMission.setCargoInventoryId(mission.getCargoInventoryId());
						newMission.setCargoWholeCode(mission.getCargoWholeCode());
						newMission.setCargoId(mission.getCargoId());
						newMission.setStatus(7);
						list.add(newMission);
					}
				}
				
				map.put(mission.getCargoWholeCode(), list);
			}
			
			request.setAttribute("inventory", inventory);
			request.setAttribute("map", map);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		
		return mapping.findForward("assignCargoReinventoryMission");
	}
	
	/**
	 *	盘点表
	 */
	public ActionForward cargoInventoryCollect(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String action = StringUtil.convertNull(request.getParameter("act"));
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		try{
			
			String code = StringUtil.convertNull(request.getParameter("code"));
			String param = "code="+code;
			if(code.equals("")){
				request.setAttribute("tip", "作业单编号不能为空！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			CargoInventoryBean inventory = service.getCargoInventory("code = '"+code+"'");
			if(inventory == null){
				request.setAttribute("tip", "盘点作业单不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			int type = StringUtil.StringToId(request.getParameter("type"));
			String cargoShelfCode = StringUtil.convertNull(request.getParameter("cargoShelfCode"));
			String cargoWholeCode = StringUtil.convertNull(request.getParameter("cargoWholeCode"));
			String productCode = StringUtil.convertNull(request.getParameter("productCode"));
			
			String condition = "cargo_inventory_id = "+inventory.getId()+" and type > 0 and status > 4";
			if(type > 0){
				switch (type) {
				case 1:
					condition = condition + " and inventory_result = 1";
					break;
				case 2:
					condition = condition + " and inventory_result = 0";
					break;
				default:
					break;
				}
				param = param + "&type="+type;
			}
			if(!cargoShelfCode.equals("")){
				condition = condition + " and cargo_shelf_code = '"+cargoShelfCode+"'";
				param = param + "&cargoShelfCode="+cargoShelfCode;
			}
			if(!cargoWholeCode.equals("")){
				condition = condition + " and cargo_whole_code = '"+cargoWholeCode+"'";
				param = param + "&cargoWholeCode="+cargoWholeCode;
			}
			if(!productCode.equals("")){
				condition = condition + " and cargo_id in (select cim.cargo_id from cargo_inventory_mission cim join cargo_inventory_mission_product cimp on cim.id = cimp.cargo_inventory_mission_id " +
							"where cimp.cargo_inventory_id = "+inventory.getId()+" and cimp.product_code ='"+productCode+"')";
			}
			
			int countPerPage = 20;
			//总数
			int totalCount = service.getCargoInventoryMissionCount(condition);
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List allMissionList = new ArrayList();
			if(action.equals("")){
				allMissionList = service.getCargoInventoryMissionList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "cargo_whole_code asc");
			}else{
				allMissionList = service.getCargoInventoryMissionList(condition, -1, -1, "cargo_whole_code asc");
			}
			Iterator iter = allMissionList.listIterator();
			HashMap map = new LinkedHashMap();
			HashMap bsbyMap = new HashMap();
			int maxStage = 0;
			while(iter.hasNext()){
				CargoInventoryMissionBean mission = (CargoInventoryMissionBean)iter.next();
				if(mission.getType()>1){
					continue;
				}
				
				List list = service.getCargoInventoryMissionList("cargo_inventory_id = "+inventory.getId()+" and (type=0 or (type>0 and status>5)) and cargo_id = "+mission.getCargoId(), -1, -1, "type asc");
				if(list.size()>maxStage){
					maxStage = list.size();
				}
				for(int i=0;i<list.size();i++){
					CargoInventoryMissionBean bean = (CargoInventoryMissionBean)list.get(i);
					
					List missionProductList = service.getCargoInventoryMissionProductList("cargo_inventory_mission_id = "+bean.getId(), -1, -1, "id asc");
					bean.setCargoInventoryMissionProductList(missionProductList);
					
					//库存调整量
					if(missionProductList.size()>0){
						CargoInventoryMissionProductBean missionProduct = (CargoInventoryMissionProductBean)missionProductList.get(0);
						ResultSet rs = service.getDbOp().executeQuery(
								"select sum(bpc.count) from bsby_operationnote bo join bsby_product bp on bo.id = bp.operation_id join bsby_product_cargo bpc on bp.id = bpc.bsby_product_id "+
								"where bo.current_type = 4 and bo.source = "+inventory.getId()+" and bp.product_id = "+missionProduct.getProductId()+" and bpc.cargo_id = "+bean.getCargoId());
						if(rs.next()){
							bsbyMap.put(mission.getCargoWholeCode(), String.valueOf(rs.getInt(1)));
						}
					}
				}
				
				map.put(mission.getCargoWholeCode(), list);
			}
			
			//补齐盘点货位表
			iter = allMissionList.listIterator();
			while(iter.hasNext()){
				CargoInventoryMissionBean mission = (CargoInventoryMissionBean)iter.next();
				if(mission.getType()>1){
					continue;
				}
				
				List list = (List)map.get(mission.getCargoWholeCode());
				if(list.size()<maxStage){
					for(int i=0;i<maxStage-list.size();i++){
						CargoInventoryMissionBean newMission = new CargoInventoryMissionBean();
						newMission.setCargoInventoryId(mission.getCargoInventoryId());
						newMission.setCargoWholeCode(mission.getCargoWholeCode());
						newMission.setCargoId(mission.getCargoId());
						newMission.setStatus(7);
						list.add(newMission);
					}
				}
				
				map.put(mission.getCargoWholeCode(), list);
			}
			
			request.setAttribute("inventory", inventory);
			request.setAttribute("map", map);
			request.setAttribute("bsbyMap", bsbyMap);
			paging.setPrefixUrl("cargoInventory.do?method=cargoInventoryCollect&"+param);
			request.setAttribute("pageBean", paging);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		
		if(action.equals("export")){
			return mapping.findForward("cargoInventoryCollectPrint");
		}
		
		return mapping.findForward("cargoInventoryCollect");
	}
	
	
	/**
	 *	盘点差异表
	 */
	public ActionForward cargoInventoryDifferent(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		try{
			
			String code = StringUtil.convertNull(request.getParameter("code"));
			if(code.equals("")){
				request.setAttribute("tip", "作业单编号不能为空！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			CargoInventoryBean inventory = service.getCargoInventory("code = '"+code+"'");
			if(inventory == null){
				request.setAttribute("tip", "盘点作业单不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			String cargoShelfCode = StringUtil.convertNull(request.getParameter("cargoShelfCode"));
			String cargoWholeCode = StringUtil.convertNull(request.getParameter("cargoWholeCode"));
			String productCode = StringUtil.convertNull(request.getParameter("productCode"));
			
			String condition = "cargo_inventory_id = "+inventory.getId()+" and type <= "+inventory.getStage()+" and type > 0 and inventory_result = 1";
			if(!cargoShelfCode.equals("")){
				condition = condition + " and cargo_shelf_code = '"+cargoShelfCode+"'";
			}
			if(!cargoWholeCode.equals("")){
				condition = condition + " and cargo_whole_code = '"+cargoWholeCode+"'";
			}
			if(!productCode.equals("")){
				condition = condition + " and cargo_id in (select cim.cargo_id from cargo_inventory_mission cim join cargo_inventory_mission_product cimp on cim.id = cimp.cargo_inventory_mission_id " +
							"where cimp.cargo_inventory_id = "+inventory.getId()+" and cimp.product_code ='"+productCode+"')";
			}	

			List allMissionList = service.getCargoInventoryMissionList(condition, -1, -1, "type asc,cargo_whole_code asc");
			Iterator iter = allMissionList.listIterator();
			HashMap map = new LinkedHashMap();
			HashMap bsbyMap = new HashMap();
			int maxStage = 0;
			while(iter.hasNext()){
				CargoInventoryMissionBean mission = (CargoInventoryMissionBean)iter.next();
				if(map.get(mission.getCargoWholeCode())!=null){
					continue;
				}
				List list = service.getCargoInventoryMissionList("cargo_inventory_id = "+inventory.getId()+" and (type=0 or (type>0 and status>5)) and cargo_id = "+mission.getCargoId(), -1, -1, "type asc");
				if(list.size()>maxStage){
					maxStage = list.size();
				}
				for(int i=0;i<list.size();i++){	
					CargoInventoryMissionBean bean = (CargoInventoryMissionBean)list.get(i);
					
					List missionProductList = service.getCargoInventoryMissionProductList("cargo_inventory_mission_id = "+bean.getId(), -1, -1, "id asc");
					bean.setCargoInventoryMissionProductList(missionProductList);
					
					//库存调整量
					if(missionProductList.size()>0){
						CargoInventoryMissionProductBean missionProduct = (CargoInventoryMissionProductBean)missionProductList.get(0);
						ResultSet rs = service.getDbOp().executeQuery(
								"select sum(bpc.count) from bsby_operationnote bo join bsby_product bp on bo.id = bp.operation_id join bsby_product_cargo bpc on bp.id = bpc.bsby_product_id "+
								"where bo.current_type = 4 and bo.source = "+inventory.getId()+" and bp.product_id = "+missionProduct.getProductId()+" and bpc.cargo_id = "+bean.getCargoId());
						if(rs.next()){
							bsbyMap.put(mission.getCargoWholeCode(), String.valueOf(rs.getInt(1)));
						}
					}
				}
				
				map.put(mission.getCargoWholeCode(), list);
			}

			//补齐盘点货位表
			iter = allMissionList.listIterator();
			while(iter.hasNext()){
				CargoInventoryMissionBean mission = (CargoInventoryMissionBean)iter.next();
				if(mission.getType()>1){
					continue;
				}
				
				List list = (List)map.get(mission.getCargoWholeCode());
				if(list.size()<maxStage){
					for(int i=0;i<maxStage-list.size();i++){
						CargoInventoryMissionBean newMission = new CargoInventoryMissionBean();
						newMission.setCargoInventoryId(mission.getCargoInventoryId());
						newMission.setCargoWholeCode(mission.getCargoWholeCode());
						newMission.setCargoId(mission.getCargoId());
						newMission.setStatus(7);
						list.add(newMission);
					}
				}
				
				map.put(mission.getCargoWholeCode(), list);
			}
			
			request.setAttribute("inventory", inventory);
			request.setAttribute("map", map);
			request.setAttribute("bsbyMap", bsbyMap);
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		
		return mapping.findForward("cargoInventoryDifferent");
	}
	
	
	/**
	 *	自动生成报损报溢单
	 */
	public ActionForward cargoReinventoryToBsby(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
		IBsByServiceManagerService bsbyService = ServiceFactory.createBsByServiceManagerService(
				IBaseService.CONN_IN_SERVICE, service.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		try{
			synchronized(CargoOperationAction.cargoLock){
				int id = StringUtil.StringToId(request.getParameter("id"));
				CargoInventoryBean inventory = service.getCargoInventory("id = "+id);
				if(inventory == null){
					request.setAttribute("tip", "盘点作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

//				int type = StringUtil.StringToId(request.getParameter("type"));
				String[] cargoIds = request.getParameterValues("cargoId");
				if((cargoIds == null || cargoIds.length == 0)){
					request.setAttribute("tip", "请选择货位再添加！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				String cargoId = "-1";
				if(cargoIds != null){
					for(int i=0;i<cargoIds.length;i++){
						cargoId = cargoId + "," + cargoIds[i];
					}
				}

				String condition = "cargo_inventory_id = "+id+" and type = 0 and cargo_id in ("+cargoId+")";
				
				service.getDbOp().startTransaction();
				
				//生成报损报溢单
				int bsId = 0;
				int byId = 0;
//				BsbyOperationnoteBean byOperation = null;
				BsbyOperationnoteBean bsOperation = null;
				for(int i=0;i<cargoIds.length;i++){
					int differentStockCount = StringUtil.StringToId(request.getParameter("differentStockCount"+cargoIds[i]));
					if(differentStockCount > 0 && byId == 0){
						String receipts_number = "";
						String title = "";// 日志的内容
						int typeString = 0;
						String code = "BY" + DateUtil.getNow().substring(0, 10).replace("-", "");
						receipts_number = ByBsAction.createCode(code);// BY+年月日+3位自动增长数
						title = "对盘点作业单"+inventory.getCode()+"进行库存调整，创建新的报溢表" + receipts_number;
						typeString = 1;
						String nowTime = DateUtil.getNow();
						BsbyOperationnoteBean bsbyOperationnoteBean = new BsbyOperationnoteBean();
						bsbyOperationnoteBean.setAdd_time(nowTime);
						bsbyOperationnoteBean.setCurrent_type(0);
						bsbyOperationnoteBean.setOperator_id(user.getId());
						bsbyOperationnoteBean.setOperator_name(user.getUsername());
						bsbyOperationnoteBean.setReceipts_number(receipts_number);
						bsbyOperationnoteBean.setWarehouse_area(1);
						bsbyOperationnoteBean.setWarehouse_type(0);
						bsbyOperationnoteBean.setType(typeString);
						bsbyOperationnoteBean.setIf_del(0);
						bsbyOperationnoteBean.setFinAuditId(0);
						bsbyOperationnoteBean.setFinAuditName("");
						bsbyOperationnoteBean.setFinAuditRemark("");
						bsbyOperationnoteBean.setSource(inventory.getId());
						if(!bsbyService.addBsbyOperationnoteBean(bsbyOperationnoteBean)){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "操作失败！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						
						byId = bsbyService.getDbOp().getLastInsertId();
						bsbyOperationnoteBean.setId(byId);
						
//						byOperation = bsbyOperationnoteBean;
						
						BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
						bsbyOperationRecordBean.setOperator_id(user.getId());
						bsbyOperationRecordBean.setOperator_name(user.getUsername());
						bsbyOperationRecordBean.setTime(nowTime);
						bsbyOperationRecordBean.setInformation(title);
						bsbyOperationRecordBean.setOperation_id(bsbyOperationnoteBean.getId());
						bsbyService.addBsbyOperationRecord(bsbyOperationRecordBean);
					}
					
					if(differentStockCount < 0 && bsId == 0){
						String receipts_number = "";
						String title = "";// 日志的内容
						int typeString = 0;
						String code = "BS" + DateUtil.getNow().substring(0, 10).replace("-", "");
						receipts_number = ByBsAction.createCode(code);// BS+年月日+3位自动增长数
						title = "对盘点作业单"+inventory.getCode()+"进行库存调整，创建新的报损表" + receipts_number;
						typeString = 0;
						String nowTime = DateUtil.getNow();
						BsbyOperationnoteBean bsbyOperationnoteBean = new BsbyOperationnoteBean();
						bsbyOperationnoteBean.setAdd_time(nowTime);
						bsbyOperationnoteBean.setCurrent_type(0);
						bsbyOperationnoteBean.setOperator_id(user.getId());
						bsbyOperationnoteBean.setOperator_name(user.getUsername());
						bsbyOperationnoteBean.setReceipts_number(receipts_number);
						bsbyOperationnoteBean.setWarehouse_area(1);
						bsbyOperationnoteBean.setWarehouse_type(0);
						bsbyOperationnoteBean.setType(typeString);
						bsbyOperationnoteBean.setIf_del(0);
						bsbyOperationnoteBean.setFinAuditId(0);
						bsbyOperationnoteBean.setFinAuditName("");
						bsbyOperationnoteBean.setFinAuditRemark("");
						bsbyOperationnoteBean.setSource(inventory.getId());
						if(!bsbyService.addBsbyOperationnoteBean(bsbyOperationnoteBean)){
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "操作失败！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						
						bsId = bsbyService.getDbOp().getLastInsertId();
						bsbyOperationnoteBean.setId(bsId);
						
						bsOperation = bsbyOperationnoteBean;
						
						BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
						bsbyOperationRecordBean.setOperator_id(user.getId());
						bsbyOperationRecordBean.setOperator_name(user.getUsername());
						bsbyOperationRecordBean.setTime(nowTime);
						bsbyOperationRecordBean.setInformation(title);
						bsbyOperationRecordBean.setOperation_id(bsbyOperationnoteBean.getId());
						bsbyService.addBsbyOperationRecord(bsbyOperationRecordBean);
					}
				}
				
				List missionList = service.getCargoInventoryMissionList(condition, -1, -1, "id asc");
				Iterator iter = missionList.listIterator();
				while(iter.hasNext()){
					CargoInventoryMissionBean mission = (CargoInventoryMissionBean)iter.next();
					int differentStockCount = StringUtil.StringToId(request.getParameter("differentStockCount"+mission.getCargoId()));
					
					List missionProductList = service.getCargoInventoryMissionProductList("cargo_inventory_mission_id = "+mission.getId(), -1, -1, "id asc");
					CargoInventoryMissionProductBean missionProduct = (CargoInventoryMissionProductBean)missionProductList.get(0);
					
					//新货位管理判断
					CargoProductStockBean cps = null;
					if(differentStockCount < 0){
						CargoInfoAreaBean outCargoArea = cargoService.getCargoInfoArea("old_id = "+bsOperation.getWarehouse_area());
						List cpsOutList = cargoService.getCargoAndProductStockList("ci.stock_type = "+bsOperation.getWarehouse_type()+" and ci.area_id = "+outCargoArea.getId()+" and cps.product_id = "+missionProduct.getProductId()+" and ci.whole_code = '"+mission.getCargoWholeCode()+"'", -1, -1, "ci.id asc");
				        cps = (CargoProductStockBean)cpsOutList.get(0);
					}else{
				        
				        List cpsOutList = cargoService.getCargoAndProductStockList("cps.product_id = "+missionProduct.getProductId()+" and cps.cargo_id = "+mission.getCargoId(), -1, -1, "ci.id asc");
				        if(cpsOutList == null || cpsOutList.size()==0){
				        	cps = new CargoProductStockBean();
				        	cps.setCargoId(mission.getCargoId());
				        	cps.setProductId(missionProduct.getProductId());
				        	cps.setStockCount(0);
				        	cps.setStockLockCount(0);
				        	cargoService.addCargoProductStock(cps);
				        	
				        	cps.setId(cargoService.getDbOp().getLastInsertId());
				        	
				        	cargoService.updateCargoInfo("status = "+CargoInfoBean.STATUS0, "id = "+mission.getCargoId());
				        }else{
				        	cps = (CargoProductStockBean)cpsOutList.get(0);
				        }
					}
			        

					BsbyProductBean bsbyProductBean = new BsbyProductBean();
					bsbyProductBean.setBsby_count(Math.abs(differentStockCount));
					if(differentStockCount<0){
						bsbyProductBean.setOperation_id(bsId);
					}else{
						bsbyProductBean.setOperation_id(byId);
					}
					bsbyProductBean.setProduct_code(missionProduct.getProductCode());
					bsbyProductBean.setProduct_id(missionProduct.getProductId());
					bsbyProductBean.setProduct_name(missionProduct.getProductOriname());
					bsbyProductBean.setOriname(missionProduct.getProductOriname());
					boolean falg = bsbyService.addBsbyProduct(bsbyProductBean);
					if (!falg) {
						request.setAttribute("tip", "添加失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					BsbyProductCargoBean bsbyCargo = new BsbyProductCargoBean();
					if(differentStockCount<0){
						bsbyCargo.setBsbyOperId(bsId);
					}else{
						bsbyCargo.setBsbyOperId(byId);
					}
					bsbyCargo.setBsbyProductId(service.getDbOp().getLastInsertId());
					bsbyCargo.setCount(Math.abs(differentStockCount));
					bsbyCargo.setCargoProductStockId(cps.getId());
					bsbyCargo.setCargoId(cps.getCargoId());
					bsbyService.addBsbyProductCargo(bsbyCargo);
					
				}

				service.updateCargoInventory("status = "+CargoInventoryBean.STATUS5, "id = "+inventory.getId());

				request.setAttribute("inventory", inventory);

				service.getDbOp().commitTransaction();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		
		return cargoInventory(mapping, form, request, response);
	}
}
