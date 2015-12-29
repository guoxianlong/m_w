package mmb.tp.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.Json;
import mmb.tp.service.TPservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.util.StringUtil;

@Controller
@RequestMapping("/TPcontroller")
public class TPcontroller {
	@Autowired
	public TPservice tpservice;
	/**
	 * 获取第三方用户信息
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getdAdminUserInfo")
	@ResponseBody
	public EasyuiDataGridJson getdAdminUserInfo(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		Json j = tpservice.getdAdminUserInfo(request);
		if (j.isSuccess()) {
			return (EasyuiDataGridJson)j.getObj();
		} else {
			returnErrJsp(request, response, j);
			return null;
		}
	}
	/**
	 * 添加用户
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/addAdminUser")
	@ResponseBody
	public Json addAdminUser(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		Json j = new Json();
		j = tpservice.addAdminUser(request);
		return j;
	}
	/**
	 * 停用启用账户
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/changeUserStatus")
	@ResponseBody
	public Json changeUserStatus(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		Json j = new Json();
		j = tpservice.changeUserStatus(request);
		return j;
	}
	/**
	 * 修改用户信息
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/updataUserInfo")
	@ResponseBody
	public Json updataUserInfo(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		Json j = new Json();
		j = tpservice.updataUserInfo(request);
		return j;
	}
	/**
	 * 统计监控
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/getStaticInfo")
	@ResponseBody
	public EasyuiDataGridJson getStaticInfo(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		String page = StringUtil.convertNull(request.getParameter("page"));
		String rows = StringUtil.convertNull(request.getParameter("rows"));
		String sort = StringUtil.convertNull(request.getParameter("sort"));
		String order = StringUtil.convertNull(request.getParameter("order"));
		EasyuiDataGrid easyUiPage = new EasyuiDataGrid();
		easyUiPage.setOrder(order);
		easyUiPage.setPage(StringUtil.toInt(page));
		easyUiPage.setRows(StringUtil.toInt(rows));
		easyUiPage.setSort(sort);
		Json j = tpservice.getStaticInfo(request,easyUiPage);
		if (j.isSuccess()) {
			return (EasyuiDataGridJson)j.getObj();
		} else {
			returnErrJsp(request, response, j);
			return null;
		}
	}
	/**
	 * 导出excel用户查询日志
	 * @throws Exception 
	 */
	@RequestMapping("/exportUserLogList")
	@ResponseBody
	public Json exportUserLogList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		Json j = tpservice.exportUserLogList(request, response);
	    return j;
	}
	public void returnErrJsp(HttpServletRequest request,HttpServletResponse response, Json j) throws ServletException, IOException {
		request.setAttribute("msg", j.getMsg());
		request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
	}
}
