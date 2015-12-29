package adultadmin.action.system;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import adultadmin.bean.system.TextDictBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ISystemService;
import adultadmin.util.StringUtil;

/**
 * 
 *  <code>TextDictAction.java</code>
 *  <p>功能:未成交订单原因管理
 *  
 *  <p>Copyright 商机无限 2012 All right reserved.
 *  @author 李双 lishuang@ebinf.com 时间 Oct 10, 2012 4:34:47 PM	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class TextDictAction extends DispatchAction{
	
	static String orderStatus ="type in(9,10,11,15,16)";
	static String stockOutStatus = "type in(2,6,7)";
	
	public void textDictList(HttpServletRequest request, HttpServletResponse response) {
		
		int type = StringUtil.parstInt(request.getParameter("type"));
		
		ISystemService service = ServiceFactory.createSystemService(IBaseService.CONN_IN_SERVICE, null);
		try{
			
			List list =null;
			if(type==0){
				list= service.getTextDictList(orderStatus, -1, -1, "type asc");
			}else{
				list= service.getTextDictList(stockOutStatus, -1, -1,"type asc");
			}
			Map map = service.getTextDictNumMap();
			
			request.setAttribute("list", list);
			request.setAttribute("map", map);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		
	}
	
	public ActionForward addTextDict(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int adType = StringUtil.parstInt(request.getParameter("adType"));
		
		int type=0;
		if(adType==1){
			type= StringUtil.parstInt(request.getParameter("otype"));
		}else{
			type= StringUtil.parstInt(request.getParameter("ttype"));
		}
		String content = StringUtil.convertNull(request.getParameter("content"));
		String mark = StringUtil.convertNull(request.getParameter("isReception"));
		ISystemService service = ServiceFactory.createSystemService(IBaseService.CONN_IN_SERVICE, null);
		try{
			TextDictBean exist = service.getTextDict(" type="+type+" and content='"+content+"'");
			if(exist!=null){
				request.setAttribute("tip", "已经存在该原因，不能重复添加");
				return mapping.findForward("error");
			}
			
			TextDictBean bean = new TextDictBean();
			bean.setType(type);
			bean.setContent(content);
			bean.setMark(mark);
			if(!service.addTextDict(bean)){
				request.setAttribute("tip", "数据库连接异常。");
				return mapping.findForward("error");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("success");
	}
	
	public ActionForward del(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int id = StringUtil.parstInt(request.getParameter("id"));
		if(id==0){
			request.setAttribute("tip", "传值错误");
			return mapping.findForward("error");
		}
		if(id==10){
			request.setAttribute("tip", "该选项不能删除!");
			return mapping.findForward("error");
		}
		ISystemService service = ServiceFactory.createSystemService(IBaseService.CONN_IN_SERVICE, null);
		try{
			service.deleteTextDict(" id="+id);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		
		return mapping.findForward("success");
	}
	
	public ActionForward find(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int id = StringUtil.parstInt(request.getParameter("id"));
		if(id==0){
			request.setAttribute("tip", "传值错误");
			return mapping.findForward("error");
		}   
		ISystemService service = ServiceFactory.createSystemService(IBaseService.CONN_IN_SERVICE, null);
		try{
			TextDictBean bean = service.getTextDict("id="+id);
			if(bean!=null){
				StringBuilder sb = new StringBuilder();
				sb.append("{\"statuName\":").append("\"").append(bean.getTypeName()).append("\",")
				  .append("\"type\":").append(bean.getType())
				  .append(",\"mark\":\"").append(bean.getMark()).append("\"")
				  .append(",\"content\":").append("\"").append(bean.getContent()).append("\"}");
				
				response.setCharacterEncoding("utf-8");
				response.setContentType("text/html");
				response.getWriter().write(sb.toString());
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		
		return null;
	}
	
	public ActionForward modify(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int id = StringUtil.parstInt(request.getParameter("id"));
		int mdType = StringUtil.parstInt(request.getParameter("mdType"));
		String content = StringUtil.convertNull(request.getParameter("content"));
		String mark = StringUtil.convertNull(request.getParameter("isReception"));
		if(id==0){
			request.setAttribute("tip", "传值错误");
			return mapping.findForward("error");
		}  
		if(content.equals("")){
			request.setAttribute("tip", "原因选项不能为空");
			return mapping.findForward("error");
		}
		ISystemService service = ServiceFactory.createSystemService(IBaseService.CONN_IN_SERVICE, null);
		try{
			
			StringBuilder sb =new StringBuilder();
			sb.append("content ='").append(StringUtil.toSql(content)).append("'");
			if(service.getTextDictCount(" type="+mdType+" and id <>"+id +" and "+sb)>0){
				request.setAttribute("tip", "已经存在该原因，不能重复添加");
				return mapping.findForward("error");
			}
			if(!mark.equals("")){
				sb.append(",mark='").append(mark).append("'");
			}
			service.updateTextDict(sb.toString(), "id="+id);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("success");
	}
	public ActionForward listDict(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int type = StringUtil.parstInt(request.getParameter("type"));
		   
		ISystemService service = ServiceFactory.createSystemService(IBaseService.CONN_IN_SERVICE, null);
		try{
			List list = service.getTextDictList(" type= "+type, -1, -1, null);
			JSONArray jsonArray2 = JSONArray.fromObject(list);
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html");
			response.getWriter().write("{\"dictList\":"+jsonArray2.toString()+"}");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return null;
	}
}
