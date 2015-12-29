package mmb.bi.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.spec.PSource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.bi.model.BIBaseCountBean;
import mmb.bi.model.BIHichartJsonBean;
import mmb.bi.model.BIHichartPostBean;
import mmb.bi.model.BIInServiceCountBean;
import mmb.bi.model.BIOnGuradCountBean;
import mmb.bi.model.BISmsNumberBean;
import mmb.bi.model.BIStandardCapacityBean;
import mmb.bi.model.BITableBean;
import mmb.bi.model.EBIArea;
import mmb.bi.model.EBILayerType;
import mmb.bi.model.EBIOperType;
import mmb.bi.service.BIStoreService;
import mmb.rec.sys.easyui.EasyuiComBoBoxBean;
import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.Json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;

/**
 * BI仓储效能分析 Controller
 * 
 * @author mengqy
 * 
 */
@RequestMapping("/BIStoreController")
@Controller
public class BIStoreController {

	private static byte[] lock = new byte[0];
	
	@Autowired
	private BIStoreService storeService;

	/**
	 * 保存在职人力
	 * 
	 * @param request
	 * @param response
	 * @param bean
	 * @return
	 */
	@RequestMapping("/addBIInServiceCountBean")
	@ResponseBody
	public Json addBIInServiceCountBean(HttpServletRequest request, HttpServletResponse response, BIInServiceCountBean bean) {
		Json json = new Json();
		if(!this.hasGroup(request, 2131)){
			json.setMsg("您没有相应的操作权限");
			return json;
		}
		synchronized (lock) {			
			try {
				storeService.addBIInServiceCountBean(bean);
				json.setMsg("在职人力保存成功");
				json.setSuccess(true);
				return json;
			} catch (RuntimeException e) {
				json.setMsg(e.getMessage());
				return json;
			}			
		}
	}

	/**
	 * 查询在职人力列表
	 * 
	 * @param request
	 * @param response
	 * @param startDate
	 * @param endDate
	 * @param areaId
	 * @return
	 */
	@RequestMapping("/getBIInServiceCountList")
	@ResponseBody
	public EasyuiDataGridJson getBIInServiceCountList(HttpServletRequest request, HttpServletResponse response, 
			EasyuiDataGrid page, String startDate, String endDate, String areaId) {
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();

		if(!this.hasGroup(request, 2134)){
			throw new RuntimeException("您没有相应的操作权限");
		}
		try {
			StringBuffer sbCondition = new StringBuffer();
			sbCondition.append(" area_id = ").append(areaId).append(" AND ");
			startDate = startDate + " 00:00:00";
			endDate = endDate + " 23:59:59";
			sbCondition.append(" datetime between '").append(startDate).append("' and '").append(endDate).append("' ");

			long total = storeService.getBIInServiceCountListCount(sbCondition.toString());
			datagrid.setTotal(total);
			List<BIInServiceCountBean> rows = storeService.getBIInServiceCountList(sbCondition.toString(), (page.getPage() - 1) * page.getRows(), page.getRows(), " id ASC ");
			datagrid.setRows(rows);

		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		return datagrid;
	}

	/**
	 * 审核在职人力
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/checkBIInServiceCountBean")
	@ResponseBody
	public Json checkBIInServiceCountBean(HttpServletRequest request, HttpServletResponse response, String id, String datetime, String areaId) {
		Json json = new Json();
		if(!this.hasGroup(request, 2132)){
			json.setMsg("您没有相应的操作权限");
			return json;
		}
		synchronized (lock) {			
			try {
				this.storeService.checkBIInServiceCountBean(id, datetime, areaId);
				json.setMsg("审核在职人力成功");
				json.setSuccess(true);
				return json;
			} catch (RuntimeException e) {
				e.printStackTrace();
				json.setMsg(e.getMessage());
				return json;
			}	
		}
	}

	/**
	 * 作废在职人力
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/deleteBIInServiceCountBean")
	@ResponseBody
	public Json deleteBIInServiceCountBean(HttpServletRequest request, HttpServletResponse response, String id, String datetime, String areaId, String updateTime) {
		Json json = new Json();
		if(!this.hasGroup(request, 2133)){
			json.setMsg("您没有相应的操作权限");
			return json;
		}
		synchronized (lock) {		
			try {
				this.storeService.deleteBIInServiceCountBean(id, datetime, areaId, updateTime);
				json.setMsg("作废在职人力成功");
				json.setSuccess(true);
				return json;
			} catch (RuntimeException e) {
				e.printStackTrace();
				json.setMsg(e.getMessage());
				return json;
			}	
		}
	}

	/**
	 * 修改在职人力
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/updateBIInServiceCountBean")
	@ResponseBody
	public Json updateBIInServiceCountBean(HttpServletRequest request, HttpServletResponse response, BIInServiceCountBean bean) {
		Json json = new Json();
		synchronized (lock) {			
			try {
				this.storeService.updateBIInServiceCountBean(bean);
				json.setMsg("修改在职人力成功");
				json.setSuccess(true);
				return json;
			} catch (RuntimeException e) {
				e.printStackTrace();
				json.setMsg(e.getMessage());
				return json;
			}	
		}
	}

	/**
	 * 保存在岗人力
	 * 
	 * @param request
	 * @param response
	 * @param bean
	 * @return
	 */
	@RequestMapping("/addBIOnGuradCountBean")
	@ResponseBody
	public Json addBIOnGuradCountBean(HttpServletRequest request, HttpServletResponse response, BIOnGuradCountBean bean) {
		Json json = new Json();
		if(!this.hasGroup(request, 2135)){
			json.setMsg("您没有相应的操作权限");
			return json;
		}
		synchronized (lock) {	
			try {
				storeService.addBIOnGuradCountBean(bean);
				json.setMsg("在岗人力保存成功");
				json.setSuccess(true);
				return json;
			} catch (RuntimeException e) {
				json.setMsg(e.getMessage());
				return json;
			}	
		}
	}

	/**
	 * 查询在岗人力列表
	 * 
	 * @param request
	 * @param response
	 * @param datetime
	 * @param endDate
	 * @param areaId
	 * @return
	 */
	@RequestMapping("/getBIOnGuradCountList")
	@ResponseBody
	public EasyuiDataGridJson getBIOnGuradCountList(HttpServletRequest request, HttpServletResponse response, 
			EasyuiDataGrid page, String datetime, String areaId, String type) {
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		if(!this.hasGroup(request, 2138)){
			throw new RuntimeException("您没有相应的操作权限");			
		}
		try {
			StringBuffer sbCondition = new StringBuffer();
			sbCondition.append(" area_id = ").append(areaId);
			datetime = datetime + " 00:00:00";
			String endDate = datetime + " 23:59:59";
			sbCondition.append(" AND datetime between '").append(datetime).append("' and '").append(endDate).append("' AND type = ").append(type);

			String orderBy = " id ASC ";
			if (Integer.valueOf(type) == BIOnGuradCountBean.EType.Type0.getIndex()) {
				orderBy = " oper_type ASC ";
			} else {
				orderBy = " department ASC ";
			}

			long total = storeService.getBIOnGuradCountListCount(sbCondition.toString());
			datagrid.setTotal(total);
			List<BIOnGuradCountBean> rows = storeService.getBIOnGuradCountList(sbCondition.toString(), (page.getPage() - 1) * page.getRows(), page.getRows(), orderBy, type);
			datagrid.setRows(rows);

		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		return datagrid;
	}

	/**
	 * 审核在岗人力
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/checkBIOnGuradCountBean")
	@ResponseBody
	public Json checkBIOnGuradCountBean(HttpServletRequest request, HttpServletResponse response, String id, String datetime, String areaId) {
		Json json = new Json();
		if(!this.hasGroup(request, 2136)){
			json.setMsg("您没有相应的操作权限");
			return json;
		}
		synchronized (lock) {			
			try {
				this.storeService.checkBIOnGuradCountBean(id, datetime, areaId);
				json.setMsg("审核在岗人力成功");
				json.setSuccess(true);
				return json;
			} catch (RuntimeException e) {
				e.printStackTrace();
				json.setMsg(e.getMessage());
				return json;
			}	
		}
	}

	/**
	 * 作废在岗人力
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/deleteBIOnGuradCountBean")
	@ResponseBody
	public Json deleteBIOnGuradCountBean(HttpServletRequest request, HttpServletResponse response, String id, String datetime, String areaId, String updateTime) {
		Json json = new Json();
		if(!this.hasGroup(request, 2137)){
			json.setMsg("您没有相应的操作权限");
			return json;
		}
		synchronized (lock) {			
			try {
				this.storeService.deleteBIOnGuradCountBean(id, datetime, areaId, updateTime);
				json.setMsg("作废在岗人力成功");
				json.setSuccess(true);
				return json;
			} catch (RuntimeException e) {
				e.printStackTrace();
				json.setMsg(e.getMessage());
				return json;
			}	
		}
	}

	/**
	 * 修改在岗人力
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/updateBIOnGuradCountBean")
	@ResponseBody
	public Json updateBIOnGuradCountBean(HttpServletRequest request, HttpServletResponse response, BIOnGuradCountBean bean) {
		Json json = new Json();
		synchronized (lock) {			
			try {
				this.storeService.updateBIOnGuradCountBean(bean);
				json.setMsg("修改在岗人力成功");
				json.setSuccess(true);
				return json;
			} catch (RuntimeException e) {
				e.printStackTrace();
				json.setMsg(e.getMessage());
				return json;
			}	
		}
	}

	/**
	 * 查询人力基础数据
	 * 
	 * @param request
	 * @param response
	 * @param datetime
	 * @param endDate
	 * @param areaId
	 * @return
	 */
	@RequestMapping("/getBIBaseCountList")
	@ResponseBody
	public EasyuiDataGridJson getBIBaseCountList(HttpServletRequest request, HttpServletResponse response, 
			EasyuiDataGrid page, String startDate, String endDate, String areaId) {
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		if(!this.hasGroup(request, 2138)){
			throw new RuntimeException("您没有相应的操作权限");			
		}
		try {
			StringBuffer sbCondition = new StringBuffer();
			if (Integer.valueOf(areaId) != EBIArea.AreaAll.getIndex()) {
				sbCondition.append(" area_id = ").append(areaId).append(" AND ");
			}
			startDate = startDate + " 00:00:00";
			endDate = endDate + " 23:59:59";
			sbCondition.append(" datetime between '").append(startDate).append("' and '").append(endDate).append("' GROUP BY datetime ");

			List<BIBaseCountBean> list = storeService.getBIBaseCountList(sbCondition.toString(), 0, 31, " datetime ASC ");
			datagrid.setTotal(Long.valueOf(list == null ? 0 : list.size()));
			datagrid.setRows(list);

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return datagrid;
	}

	/**
	 * 保存标准产能
	 * 
	 * @param request
	 * @param response
	 * @param bean
	 * @return
	 */
	@RequestMapping("/addBIStandardCapacityBean")
	@ResponseBody
	public Json addBIStandardCapacityBean(HttpServletRequest request, HttpServletResponse response, BIStandardCapacityBean bean) {
		Json json = new Json();
		if(!this.hasGroup(request, 2140)){
			json.setMsg("您没有相应的操作权限");
			return json;
		}
		synchronized (lock) {			
			try {
				this.storeService.addBIStandardCapacityBean(bean);
				json.setMsg("标准产能保存成功");
				json.setSuccess(true);
				return json;
			} catch (RuntimeException e) {
				json.setMsg(e.getMessage());
				return json;
			}	
		}
	}

	/**
	 * 查询标准产能列表
	 * 
	 * @param request
	 * @param response
	 * @param startDate
	 * @param endDate
	 * @param areaId
	 * @return
	 */
	@RequestMapping("/getBIStandardCapacityList")
	@ResponseBody
	public EasyuiDataGridJson getBIStandardCapacityList(HttpServletRequest request, HttpServletResponse response, 
			EasyuiDataGrid page, String areaId, String operType, String status) {
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		if(!this.hasGroup(request, 2141)){
			throw new RuntimeException("您没有相应的操作权限");			
		}
		try {
			StringBuffer sbCondition = new StringBuffer();
			sbCondition.append(" area_id = ").append(areaId).append(" ");
			if (Integer.valueOf(operType) != -1) {
				sbCondition.append(" AND oper_type = ").append(operType).append(" ");
			}
			int nStatus = Integer.valueOf(status);
			if (nStatus == BIStandardCapacityBean.EStatus.Status0.getIndex()) {
				sbCondition.append(" AND start_time > '").append(DateUtil.getNow()).append("' AND ( stop_time IS NULL OR stop_time > start_time ) ");
			} else if (nStatus == BIStandardCapacityBean.EStatus.Status1.getIndex()) {
				sbCondition.append(" AND start_time <= '").append(DateUtil.getNow()).append("' AND ( stop_time IS NULL OR stop_time > '").append(DateUtil.getNow()).append("' ) ");
			} else if (nStatus == BIStandardCapacityBean.EStatus.Status2.getIndex()) {
				sbCondition.append(" AND ( stop_time <= '").append(DateUtil.getNow()).append("' OR stop_time <= start_time ) ");
			}

			long total = storeService.getBIStandardCapacityListCount(sbCondition.toString());
			datagrid.setTotal(total);
			List<BIStandardCapacityBean> rows = this.storeService.getBIStandardCapacityList(sbCondition.toString(), (page.getPage() - 1) * page.getRows(), page.getRows(), " id DESC ");
			datagrid.setRows(rows);

		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		return datagrid;
	}

	/**
	 * 整体效能 单仓效能
	 * 
	 * @param request
	 * @param response
	 * @param postBean
	 * @return
	 */
	@RequestMapping("/getSingleOrderCountChart")
	@ResponseBody
	public Json getSingleOrderCountChart(HttpServletRequest request, HttpServletResponse response, BIHichartPostBean postBean) {
		Json json = new Json();
		if(!this.hasGroup(request, 2142)){
			json.setMsg("您没有相应的操作权限");
			return json;
		}
		try {
			BIHichartJsonBean jsonBean = this.storeService.getSingleOrderCountChart(postBean);
			if (jsonBean.getCatList().size() == 0) {
				json.setMsg("没有查询到结果");
				return json;
			}
			json.setObj(jsonBean);
			json.setSuccess(true);
			return json;
		} catch (RuntimeException e) {
			e.printStackTrace();
			json.setMsg(e.getMessage());
		}
		return json;
	}

	/**
	 * 整体效能 分仓对比
	 * 
	 * @param request
	 * @param response
	 * @param postBean
	 * @return
	 */
	@RequestMapping("/getMultiOrderCountChart")
	@ResponseBody
	public Json getMultiOrderCountChart(HttpServletRequest request, HttpServletResponse response, BIHichartPostBean postBean) {
		Json json = new Json();
		if(!this.hasGroup(request, 2142)){
			json.setMsg("您没有相应的操作权限");
			return json;
		}
		try {
			BIHichartJsonBean jsonBean = this.storeService.getMultiOrderCountChart(postBean);
			if (jsonBean.getCatList().size() == 0) {
				json.setMsg("没有查询到结果");
				return json;
			}
			json.setObj(jsonBean);
			json.setSuccess(true);
			return json;
		} catch (RuntimeException e) {
			e.printStackTrace();
			json.setMsg(e.getMessage());
		}
		return json;
	}

	/**
	 * 整体效能 在岗率
	 * 
	 * @param request
	 * @param response
	 * @param postBean
	 * @return
	 */
	@RequestMapping("/getOnGuradPerChart")
	@ResponseBody
	public Json getOnGuradPerChart(HttpServletRequest request, HttpServletResponse response, BIHichartPostBean postBean) {
		Json json = new Json();
		if(!this.hasGroup(request, 2142)){
			json.setMsg("您没有相应的操作权限");
			return json;
		}
		try {
			BIHichartJsonBean jsonBean = this.storeService.getOnGuradPerChart(postBean);
			if (jsonBean.getCatList().size() == 0) {
				json.setMsg("没有查询到结果");
				return json;
			}
			json.setObj(jsonBean);
			json.setSuccess(true);
			return json;
		} catch (RuntimeException e) {
			e.printStackTrace();
			json.setMsg(e.getMessage());
		}
		return json;
	}
	

	/**
	 * 整体能效
	 * @param request
	 * @param response
	 * @param page
	 * @param areaId
	 * @param layer
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping("/getOrderCountTableList")
	@ResponseBody
	public EasyuiDataGridJson getOrderCountTableList(HttpServletRequest request, HttpServletResponse response, 
			EasyuiDataGrid page, String areaId, String layer, String startDate, String endDate) {
		if(!this.hasGroup(request, 2143)){
			throw new RuntimeException("您没有相应的操作权限");			
		}
		startDate = startDate + " 00:00:00";
		endDate = endDate + " 23:59:59";
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("beginDate", startDate);
		map.put("endDate", endDate);
		map.put("areaId", areaId);
		if (Integer.valueOf(layer) == EBILayerType.All.getIndex().intValue()) {
			map.put("field", "total");	
		} else {
			map.put("field", "ware");
		}
		map.put("count", "31");
		
		EasyuiDataGridJson json = new EasyuiDataGridJson();		
		try {			
			List<HashMap<String, String>> rows = this.storeService.getOrderCountTableList(map);			
			json.setRows(rows);		
			json.setTotal(Long.valueOf(rows == null ? 0 : rows.size()));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
	
	
	/**
	 * 作业环节 单仓效能
	 * 
	 * @param request
	 * @param response
	 * @param postBean
	 * @return
	 */
	@RequestMapping("/getSingleOperTypeChart")
	@ResponseBody
	public Json getSingleOperTypeChart(HttpServletRequest request, HttpServletResponse response, BIHichartPostBean postBean) {
		Json json = new Json();
		if(!this.hasGroup(request, 2144)){
			json.setMsg("您没有相应的操作权限");
			return json;
		}
		try {
			BIHichartJsonBean jsonBean = this.storeService.getSingleOperTypeChart(postBean);
			if (jsonBean.getCatList().size() == 0) {
				json.setMsg("没有查询到结果");
				return json;
			}
			json.setObj(jsonBean);
			json.setSuccess(true);
			return json;
		} catch (RuntimeException e) {
			e.printStackTrace();
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**
	 * 作业环节 分仓对比
	 * 
	 * @param request
	 * @param response
	 * @param postBean
	 * @return
	 */
	@RequestMapping("/getMultiOperTypeChart")
	@ResponseBody
	public Json getMultiOperTypeChart(HttpServletRequest request, HttpServletResponse response, BIHichartPostBean postBean) {
		Json json = new Json();
		if(!this.hasGroup(request, 2144)){
			json.setMsg("您没有相应的操作权限");
			return json;
		}
		try {
			BIHichartJsonBean jsonBean = this.storeService.getMultiOperTypeChart(postBean);
			if (jsonBean.getCatList().size() == 0) {
				json.setMsg("没有查询到结果");
				return json;
			}
			json.setObj(jsonBean);
			json.setSuccess(true);
			return json;
		} catch (RuntimeException e) {
			e.printStackTrace();
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**
	 * 作业环节 日在岗人均产能表格
	 * @param request
	 * @param response
	 * @param page
	 * @param areaId
	 * @param operType
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping("/getOperTypeTableList")
	@ResponseBody
	public EasyuiDataGridJson getOperTypeTableList(HttpServletRequest request, HttpServletResponse response, 
			EasyuiDataGrid page, String areaId, String operType, String startDate, String endDate){
		if(!this.hasGroup(request, 2144)){
			throw new RuntimeException("您没有相应的操作权限");			
		}
		startDate = startDate + " 00:00:00";
		endDate = endDate + " 23:59:59";
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("beginDate", startDate);
		map.put("endDate", endDate);
		map.put("areaId", areaId);
		map.put("operType", operType);
		map.put("count", "31");
		
		
		EasyuiDataGridJson json = new EasyuiDataGridJson();		
		try {			
			List<BITableBean> rows = this.storeService.getOperTypeTableList(map);			
			json.setRows(rows);		
			json.setTotal(Long.valueOf(rows == null ? 0 : rows.size()));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
	/**
	 * 保存定时短信手机号
	 * 
	 * @param request
	 * @param response
	 * @param bean
	 * @return
	 */
	@RequestMapping("/saveBISmsNumberBean")
	@ResponseBody
	public Json saveBISmsNumberBean(HttpServletRequest request, HttpServletResponse response, BISmsNumberBean bean) {
		Json json = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			json.setMsg("当前没有登录,操作失败!");
			return json;
		}
		
		if (bean.getId() <= 0) {
			// 录入
			if(!this.hasGroup(request, 2145)){
				json.setMsg("您没有相应的操作权限");
				return json;
			}
		} else {
			// 修改
			if(!this.hasGroup(request, 2146)){
				json.setMsg("您没有相应的操作权限");
				return json;
			}
		}
		synchronized (lock) {			
			try {
				storeService.saveBISmsNumberBean(bean, user);
				json.setMsg("定时短信手机号保存成功");
				json.setSuccess(true);
				return json;
			} catch (RuntimeException e) {
				json.setMsg(e.getMessage());
				return json;
			}			
		}
	}

	/**
	 * 查询定时短信手机号列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/getBISmsNumberList")
	@ResponseBody
	public EasyuiDataGridJson getBISmsNumberList(HttpServletRequest request, HttpServletResponse response, 
			EasyuiDataGrid page, BISmsNumberBean bean, String statusList, String startDate, String endDate) {
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();

		try {
			StringBuilder sb = new StringBuilder();		

			if (bean.getName() != null && bean.getName().length() > 0) {
				if(sb.length() > 0)
					sb.append(" AND ");
				sb.append(" name LIKE '%").append(StringUtil.dealParam(bean.getName())).append("%' ");
			}
			
			if (bean.getNumber() != null && bean.getNumber().length() > 0) {
				if(sb.length() > 0)
					sb.append(" AND ");
				sb.append(" number LIKE '%").append(StringUtil.dealParam(bean.getNumber())).append("%' ");
			}
			
			if (bean.getDepartment() > -1) {
				if(sb.length() > 0)
					sb.append(" AND ");
				sb.append(" department = ").append(bean.getDepartment());
			}
			
			if (bean.getTitle() > -1) {
				if(sb.length() > 0)
					sb.append(" AND ");
				sb.append(" title = ").append(bean.getTitle());
			}
			
			if(startDate != null && startDate.length() > 0 && endDate != null && endDate.length() > 0) {
				startDate = startDate + " 00:00:00";
				endDate = endDate + " 23:59:59";
				if(sb.length() > 0)
					sb.append(" AND ");
				sb.append(" create_time between '").append(startDate).append("' and '").append(endDate).append("' ");
			}
			
			if (statusList != null && statusList.length() > 0) {
				if(sb.length() > 0)
					sb.append(" AND ");
				if (statusList.indexOf(",") > -1) {
					sb.append(" status IN ( ").append(statusList).append(" )");	
				} else {
					sb.append(" status = ").append(statusList);
				}
			}
			
			String condition = sb.length() == 0 ? " id > 0 " :  sb.toString();			
			long total = storeService.getBISmsNumberListCount(condition);
			datagrid.setTotal(total);
			List<BISmsNumberBean> rows = storeService.getBISmsNumberList(condition, (page.getPage() - 1) * page.getRows(), page.getRows(), " id ASC ");
			datagrid.setRows(rows);

		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		return datagrid;
	}

	/**
	 * 审核定时短信手机号
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/checkBISmsNumberBean")
	@ResponseBody
	public Json checkBISmsNumberBean(HttpServletRequest request, HttpServletResponse response, String id, String status) {
		Json json = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			json.setMsg("当前没有登录,操作失败!");
			return json;
		}
		
		int temp = Integer.valueOf(status).intValue();
		if (temp == 1) {
			// 审核通过
			if(!this.hasGroup(request, 2149)){
				json.setMsg("您没有相应的操作权限");
				return json;
			}
		} else {
			// 审核不通过
			if(!this.hasGroup(request, 2150)){
				json.setMsg("您没有相应的操作权限");
				return json;
			}
		}
		synchronized (lock) {
			try {
				this.storeService.checkBISmsNumberBean(id, status, user);
				json.setMsg("审核定时短信手机号成功");
				json.setSuccess(true);
				return json;
			} catch (RuntimeException e) {
				e.printStackTrace();
				json.setMsg(e.getMessage());
				return json;
			}
		}
	}

	/**
	 * 删除定时短信手机号
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/deleteBISmsNumberBean")
	@ResponseBody
	public Json deleteBISmsNumberBean(HttpServletRequest request, HttpServletResponse response, String id) {
		Json json = new Json();
		if(!this.hasGroup(request, 2147)){
			json.setMsg("您没有相应的操作权限");
			return json;
		}
		synchronized (lock) {		
			try {
				this.storeService.deleteBISmsNumberBean(id);
				json.setMsg("删除定时短信手机号成功");
				json.setSuccess(true);
				return json;
			} catch (RuntimeException e) {
				e.printStackTrace();
				json.setMsg(e.getMessage());
				return json;
			}	
		}
	}	
	
	/**
	 * 职称
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getETitleType")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getETitleType(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("-1");
		bean.setSelected(true);
		bean.setText("请选择");
		list.add(bean);

		for (BISmsNumberBean.ETitle type : BISmsNumberBean.ETitle.values()) {
			bean = new EasyuiComBoBoxBean();
			bean.setId(type.getIndex().toString());
			bean.setText(type.getName());
			list.add(bean);
		}

		return list;
	}
	
	/**
	 * 人员类型
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getStaffType")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getStaffType(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("-1");
		bean.setSelected(true);
		bean.setText("请选择");
		list.add(bean);

		for (BIOnGuradCountBean.EType type : BIOnGuradCountBean.EType.values()) {
			bean = new EasyuiComBoBoxBean();
			bean.setId(type.getIndex().toString());
			bean.setText(type.getName());
			list.add(bean);
		}

		return list;
	}

	/**
	 * 部门列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getDepartList")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getDepartList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("-1");
		bean.setSelected(true);
		bean.setText("请选择");
		list.add(bean);

		for (BIOnGuradCountBean.EDepartment depart : BIOnGuradCountBean.EDepartment.values()) {
			bean = new EasyuiComBoBoxBean();
			bean.setId(depart.getIndex().toString());
			bean.setText(depart.getName());
			list.add(bean);
		}

		return list;
	}

	/**
	 * 作业环节列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getOperTypeList")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getOperTypeList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("-1");
		bean.setSelected(true);
		bean.setText("请选择");
		list.add(bean);

		for (EBIOperType type : EBIOperType.values()) {
			bean = new EasyuiComBoBoxBean();
			bean.setId(type.getIndex().toString());
			bean.setText(type.getName());
			list.add(bean);
		}

		return list;
	}

	/**
	 * 作业环节效能页面 作业环节列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getEnableOperTypeList")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getEnableOperTypeList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("-1");
		bean.setSelected(true);
		bean.setText("请选择");
		list.add(bean);

		for (EBIOperType type : EBIOperType.values()) {
			if(type.getIndex() == EBIOperType.Type8.getIndex() ||type.getIndex() == EBIOperType.Type9.getIndex())
				continue;
			bean = new EasyuiComBoBoxBean();
			bean.setId(type.getIndex().toString());
			bean.setText(type.getName());
			list.add(bean);
		}

		return list;
	}
	
	/**
	 * 作业环节效能页面 作业环节列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getEnableSCOperTypeList")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getEnableSCOperTypeList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("-1");
		bean.setSelected(true);
		bean.setText("请选择");
		list.add(bean);

		for (EBIOperType type : EBIOperType.values()) {
			if(type.getIndex() == EBIOperType.Type7.getIndex() 
					||type.getIndex() == EBIOperType.Type8.getIndex() 
					||type.getIndex() == EBIOperType.Type9.getIndex())
				continue;
			bean = new EasyuiComBoBoxBean();
			bean.setId(type.getIndex().toString());
			bean.setText(type.getName());
			list.add(bean);
		}

		return list;
	}
	
	/**
	 * BI仓库列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getBIArea")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getBIArea(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("-1");
		bean.setSelected(true);
		bean.setText("请选择");
		list.add(bean);

		for (Integer key : ProductStockBean.stockoutAvailableAreaMap.keySet()) {
			bean = new EasyuiComBoBoxBean();
			bean.setId(key.toString());
			bean.setText(ProductStockBean.stockoutAvailableAreaMap.get(key));
			list.add(bean);
		}

		return list;
	}

	/**
	 * BI仓库列表 + 迈世(全体)
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getBIAllArea")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getBIAllArea(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = null;
		bean = new EasyuiComBoBoxBean();
		bean.setSelected(true);
		bean.setId(EBIArea.AreaAll.getIndex().toString());
		bean.setText(EBIArea.AreaAll.getName().toString());
		list.add(bean);
		
		for (Integer key : ProductStockBean.stockoutAvailableAreaMap.keySet()) {
			bean = new EasyuiComBoBoxBean();
			bean.setId(key.toString());
			bean.setText(ProductStockBean.stockoutAvailableAreaMap.get(key));
			list.add(bean);
		}
		
		return list;
	}

	/**
	 * BI标准产能状态
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getBISCStatus")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getBISCStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("-1");
		bean.setSelected(true);
		bean.setText("请选择");
		list.add(bean);

		for (BIStandardCapacityBean.EStatus status : BIStandardCapacityBean.EStatus.values()) {
			bean = new EasyuiComBoBoxBean();
			bean.setId(status.getIndex().toString());
			bean.setText(status.getName());
			list.add(bean);
		}

		return list;
	}

	/**
	 * 层级：整体、物流中心
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getBILayerType")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getBILayerType(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = null;

		for (EBILayerType type : EBILayerType.values()) {
			bean = new EasyuiComBoBoxBean();
			if (type.getIndex() == EBILayerType.All.getIndex()) {
				bean.setSelected(true);
			}
			bean.setId(type.getIndex().toString());
			bean.setText(type.getName());
			list.add(bean);
		}

		return list;
	}

	/**
	 * 年列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getYearList")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getYearList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("-1");
		bean.setSelected(true);
		bean.setText("请选择");
		list.add(bean);

		for (int i = 2014; i < 2027; i++) {
			bean = new EasyuiComBoBoxBean();
			bean.setId(i + "");
			bean.setText(i + "");
			list.add(bean);
		}

		return list;
	}

	/**
	 * 月列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getMonthList")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getMonthList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("-1");
		bean.setSelected(true);
		bean.setText("请选择");
		list.add(bean);

		for (int i = 1; i < 13; i++) {
			bean = new EasyuiComBoBoxBean();
			bean.setId(i + "");
			bean.setText(i + "");
			list.add(bean);
		}

		return list;
	}

	/**
	 * 日列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getDayList")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getDayList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("-1");
		bean.setSelected(true);
		bean.setText("请选择");
		list.add(bean);

		for (int i = 1; i < 32; i++) {
			bean = new EasyuiComBoBoxBean();
			bean.setId(i + "");
			bean.setText(i + "");
			list.add(bean);
		}

		return list;
	}

	private boolean hasGroup(HttpServletRequest request, int groupFlag){
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (groupFlag > 0) {
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(groupFlag)) {
				return false;
			}
		}
		return true;
	}
	
}
