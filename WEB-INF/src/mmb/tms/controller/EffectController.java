package mmb.tms.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.tms.service.IEffectService;
import mmb.util.excel.ExportExcel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.util.DateUtil;

@RequestMapping("EffectController")
@Controller
public class EffectController {
	@Autowired
	private IEffectService iEffectService;
	/**
	 * 获取常规类、时效类、观察类
	 * @param type:1：常规类 2:时效类,3客诉类,4:观察类
	 * @param  注：修改了原EffectDeliverController
	 * @author ahc
	 */
	@RequestMapping("/getCommonTypeList")
	@ResponseBody
	public EasyuiDataGridJson getCommonTypeList(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response,
			String param,String startTime,String endTime,String type) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		EasyuiDataGridJson dataGrid = new EasyuiDataGridJson();
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		if (startTime.equals("") || endTime.equals("")) {
			request.setAttribute("msg", "请选择时间范围！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		if (DateUtil.daysBetween(DateUtil.parseDate(endTime), DateUtil.parseDate(startTime)) > 30) {
			request.setAttribute("msg", "最多查询30天的信息！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		if("".equals(param) ||  param==null){
			dataGrid.setRows(list);
			dataGrid.setTotal((long)0);
			return dataGrid;
		}
		Map<String,String> map = new HashMap<String,String>();
		StringBuffer group = new StringBuffer();
		StringBuffer group2 = new StringBuffer();
		StringBuffer group3 = new StringBuffer();
		StringBuffer condition = new StringBuffer();
		StringBuffer condition2 = new StringBuffer();
		StringBuffer tt = new StringBuffer();
		StringBuffer tt2 = new StringBuffer();
		String groups[] = param.split(",");
		for(String by :groups){
			group.append(by);
			group.append(",");
			String s =by.substring(0,1);
			tt.append("IFNULL("+s);
			tt.append(".id,0)");
			tt.append(",'-',");
			
			tt2.append("IFNULL("+s);
			tt2.append("_id,0)");
			tt2.append(",'-',");
			
			if("d.name".equals(by)){
				group2.append("stock");
				group2.append(",");
				
				group3.append("aa.stock");
				group3.append(",");
			}
			if("c.name".equals(by)){
				group2.append("deliver");
				group2.append(",");
				
				group3.append("aa.deliver");
				group3.append(",");
			}
			if("f.name".equals(by)){
				group2.append("sheng");
				group2.append(",");
				
				group3.append("aa.sheng");
				group3.append(",");
			}
			if("g.city".equals(by)){
				group2.append("city");
				group2.append(",");
				
				group3.append("aa.city");
				group3.append(",");
			}
			if("h.area".equals(by)){
				group2.append("area");
				group2.append(",");
				
				group3.append("aa.area");
				group3.append(",");
			}
			if("i.street".equals(by)){
				group2.append("street");
				group2.append(",");
				
				group3.append("aa.street");
				group3.append(",");
			}
			if("k.name".equals(by)){
				group2.append("chanpinxian");
				group2.append(",");
				
				group3.append("aa.chanpinxian");
				group3.append(",");
			}
		}
		
		group.delete(group.length()-1, group.length());
		group2.delete(group2.length()-1, group2.length());
		group3.delete(group3.length()-1, group3.length());
		tt.delete(tt.length()-1, tt.length());
		tt2.delete(tt2.length()-1, tt2.length());
		if (!"".equals(startTime) && !"".equals(endTime)) {
			 condition.append(" and mb.transit_datetime between '" + startTime +" 00:00:00' and '"+endTime+" 23:59:59' ");
			 condition2.append(" lco.create_time >= '"+startTime+" 00:00:00' and lco.create_time <='"+endTime+" 23:59:59' ");
		}
		map.put("group", group.toString());
		map.put("group2", group2.toString());
		map.put("group3", group3.toString());
		map.put("condition", condition.toString());
		map.put("condition2", condition2.toString());
		map.put("tt",tt.toString());
		map.put("tt2",tt2.toString());
		if("1".equals(type)){
			int rowCount =iEffectService.getRegularClazzCount(map);
			dataGrid.setTotal((long)rowCount);
		}
		if("2".equals(type)){
			int rowCount =iEffectService.getPrescriptionCount(map);
			dataGrid.setTotal((long)rowCount);
		}
		if("3".equals(type)){
			int rowCount =iEffectService.getCustomerCount(map);
			dataGrid.setTotal((long)rowCount);
		}
		if("4".equals(type)){
			int rowCount =iEffectService.getObservationCount(map);
			dataGrid.setTotal((long)rowCount);
		}
		map.put("start", (page.getPage()-1) * page.getRows() + "");
		map.put("count", page.getRows() + "");
		if("1".equals(type)){
			list =iEffectService.getRegularClazz(map);	
		}
		if("2".equals(type)){
			list =iEffectService.getPrescriptionList(map);
		}
		if("3".equals(type)){
			map.put("condition2", condition2.toString());
			list =iEffectService.getCustomerList(map);
		}
		if("4".equals(type)){
			list =iEffectService.getObservationList(map);
		}
		dataGrid.setRows(list);
		return dataGrid;
	}
	
	
	/**
	 * 导出Excel
	 * @param type:1：常规类 2:时效类 3：客诉类 4：观察类
	 * @throws Exception
	 * @author ahc
	 */
	@RequestMapping("/portExcel")
	@ResponseBody
	public Object portExcel (EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response,
			String param,String startTime,String endTime,String type) throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		EasyuiDataGridJson dataGrid = new EasyuiDataGridJson();
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
		}
		if (startTime.equals("") || endTime.equals("")) {
			request.setAttribute("msg", "请选择时间范围！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
		}
		if (DateUtil.daysBetween(DateUtil.parseDate(endTime), DateUtil.parseDate(startTime)) > 30) {
			request.setAttribute("msg", "最多查询30天的信息！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
		}
		if("".equals(param) ||  param==null){
			dataGrid.setRows(list);
			dataGrid.setTotal((long)0);
			request.setAttribute("msg", "请先输入查询信息！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		Map<String,String> map = new HashMap<String,String>();
		StringBuffer group = new StringBuffer();
		StringBuffer group2 = new StringBuffer();
		StringBuffer group3 = new StringBuffer();
		StringBuffer condition = new StringBuffer();
		StringBuffer condition2 = new StringBuffer();
		StringBuffer tt = new StringBuffer();
		StringBuffer tt2 = new StringBuffer();
		String groups[] = param.split(",");
		for(String by :groups){
			group.append(by);
			group.append(",");
			String s =by.substring(0,1);
			tt.append("IFNULL("+s);
			tt.append(".id,0)");
			tt.append(",'-',");
			
			tt2.append("IFNULL("+s);
			tt2.append("_id,0)");
			tt2.append(",'-',");
			
			if("d.name".equals(by)){
				group2.append("stock");
				group2.append(",");
				
				group3.append("aa.stock");
				group3.append(",");
			}
			if("c.name".equals(by)){
				group2.append("deliver");
				group2.append(",");
				
				group3.append("aa.deliver");
				group3.append(",");
			}
			if("f.name".equals(by)){
				group2.append("sheng");
				group2.append(",");
				
				group3.append("aa.sheng");
				group3.append(",");
			}
			if("g.city".equals(by)){
				group2.append("city");
				group2.append(",");
				
				group3.append("aa.city");
				group3.append(",");
			}
			if("h.area".equals(by)){
				group2.append("area");
				group2.append(",");
				
				group3.append("aa.area");
				group3.append(",");
			}
			if("i.street".equals(by)){
				group2.append("street");
				group2.append(",");
				
				group3.append("aa.street");
				group3.append(",");
			}
			if("k.name".equals(by)){
				group2.append("chanpinxian");
				group2.append(",");
				
				group3.append("aa.chanpinxian");
				group3.append(",");
			}
		}
		
		group.delete(group.length()-1, group.length());
		group2.delete(group2.length()-1, group2.length());
		group3.delete(group3.length()-1, group3.length());
		tt.delete(tt.length()-1, tt.length());
		tt2.delete(tt2.length()-1, tt2.length());
		if (!"".equals(startTime) && !"".equals(endTime)) {
			 condition.append(" and mb.transit_datetime between '" + startTime +" 00:00:00' and '"+endTime+" 23:59:59' ");
			 condition2.append(" lco.create_time >= '"+startTime+"' and lco.create_time <='"+endTime+"' ");
		}
		map.put("group", group.toString());
		map.put("group2", group2.toString());
		map.put("group3", group3.toString());
		map.put("condition", condition.toString());
		map.put("condition2", condition2.toString());
		map.put("tt",tt.toString());
		map.put("tt2",tt2.toString());
		if("1".equals(type)){
			list =iEffectService.getRegularClazz(map);
			iEffectService.portExcel("1", list, response);
		}
		if("2".equals(type)){
			list =iEffectService.getPrescriptionList(map);
			iEffectService.portExcel("2", list, response);
		}
		if("3".equals(type)){
			list =iEffectService.getCustomerList(map);
			iEffectService.portExcel("3", list, response);
		}
		if("4".equals(type)){
			list =iEffectService.getObservationList(map);
			iEffectService.portExcel("4", list, response);
		}
		return null;
	}
}
