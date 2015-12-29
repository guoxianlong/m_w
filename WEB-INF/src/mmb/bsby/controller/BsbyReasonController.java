package mmb.bsby.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.bsby.model.BsbyReason;
import mmb.bsby.model.BsbyReasonLog;
import mmb.bsby.service.BsbyReasonLogService;
import mmb.bsby.service.BsbyReasonService;
import mmb.easyui.Json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;

@Controller
@RequestMapping("/admin/BsbyReason")
public class BsbyReasonController {
	@Autowired
	private BsbyReasonService bsbyReasonService;
	@Autowired
	private BsbyReasonLogService bsbyReasonLogService;
	
	/**
	 * 获取集合
	 * 
	 */
	@RequestMapping("/queryBsbyReasons")
	public String queryBsbyReasons(HttpServletRequest request,HttpServletResponse response){
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer condition = new StringBuffer();
		condition.append("1=1");
		/*// 时间查询
		if (!"".equals(StringUtil.checkNull(dynamicCheckBean.getBeginDatetime()))&& !"".equals(StringUtil.checkNull(dynamicCheckBean.getEndDatetime()))) {
			condition.append(" and create_time between '"
					+ dynamicCheckBean.getBeginDatetime() + " 00:00:00" + "'");
			condition.append(" and '" + dynamicCheckBean.getEndDatetime()
					+ " 23:59:59" + "'");
		}*/

		map.put("condition", condition.toString());
//		int rowCount = dCheckService.getDynamicCheckBeanCount(map);
//		datagrid.setTotal((long) rowCount);
		map.put("start", "-1");
		map.put("count", "-1");
		String order = null;
		
		if (order == null || "".equals(order)) {
			order = " id desc";
		}
		map.put("order", order);
		
	  List<BsbyReason> bsbyReasons=	this.bsbyReasonService.queryBsbyReasons(map);
	   request.setAttribute("bsbyReasons", bsbyReasons);
	   return "admin/bsby/bsbyReason";
	}
	@RequestMapping("/addBsbyReason")
	@ResponseBody
	public String addBsbyReason(HttpServletRequest request,HttpServletResponse response){
		String msg="";
		String reason=request.getParameter("reason");
		String type=request.getParameter("type");
		voUser user = (voUser)request.getSession(false).getAttribute("userView");
		if(user == null) {
			msg="nologin";
			return msg;
		}
		
		
		// 先查询一下该原因是否存在
		BsbyReason temp=this.bsbyReasonService.queryBsbyReasonByCondition(" type="+type+" and reason='"+reason+"'");
		
		if(temp == null){
		BsbyReason bsbyReason=new BsbyReason();
		bsbyReason.setType(Integer.parseInt(type));
		bsbyReason.setReason(reason);
		
		int id=this.bsbyReasonService.addBsbyReason(bsbyReason);
		BsbyReasonLog bsbyReasonLog=new BsbyReasonLog();
		bsbyReasonLog.setOperDateTime(DateUtil.formatTime(new Date()));
		bsbyReasonLog.setOperType("添加");
		bsbyReasonLog.setOperUserId(user.getId());
		bsbyReasonLog.setOperUserName(user.getUsername());
		bsbyReasonLog.setType(bsbyReason.getType());
		bsbyReasonLog.setReason(reason);
		bsbyReasonLogService.addBsbyReasonLog(bsbyReasonLog);
		if(id>0){
			msg="success";
		}else{
			msg="error";
		}
		}else {
			msg="failed";
		}
		return msg;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/deleteBsbyReason")
	@ResponseBody 
   public Json	deleteBsbyReason(HttpServletRequest request,HttpServletResponse response){
	   Json json=new Json();
	   voUser user = (voUser)request.getSession(false).getAttribute("userView");
		if(user == null) {
			json.setMsg("nologin");
			return json;
		}
	   String ids=request.getParameter("ids");
	   if(!"".equals(StringUtil.checkNull(ids))){
		   String[] idsarr=ids.split(",");
		   for (int i = 0; i < idsarr.length; i++) {
			   BsbyReason bsbyReason=this.bsbyReasonService.selectBsbyReasonById(Integer.parseInt(idsarr[i]));
			   
			   
			    BsbyReasonLog bsbyReasonLog=new BsbyReasonLog();
				bsbyReasonLog.setOperDateTime(DateUtil.formatTime(new Date()));
				bsbyReasonLog.setOperType("删除");
				bsbyReasonLog.setOperUserId(user.getId());
				bsbyReasonLog.setOperUserName(user.getUsername());
				if(bsbyReason!=null){
				bsbyReasonLog.setType(bsbyReason.getType());
				bsbyReasonLog.setReason(bsbyReason.getReason());
				}
			   bsbyReasonLogService.addBsbyReasonLog(bsbyReasonLog);
			   
			  int count=this.bsbyReasonService.deleteBsbyReasonById(Integer.parseInt(idsarr[i]));
			  if(count ==0){
				  json.setMsg("error");
				  return json;
			  }else{
				  json.setMsg("success");  
			  }
		   }
	  }else{
		   json.setMsg("error");
	   }
	   
	   return json;
   }
	
	
	
	/**
	 * 跳转编辑页面
	 * 
	 */
	 
	@RequestMapping("/toeditBsbyReason")
	@ResponseBody 
	public Json toeditBsbyReason(HttpServletRequest request,HttpServletResponse response){
		Json json=new Json();
		String idstr=request.getParameter("id");
		
		if(!"".equals(StringUtil.checkNull(idstr))){
			
			int id=Integer.parseInt(idstr);
			BsbyReason bsbyReason=this.bsbyReasonService.selectBsbyReasonById(id);
			json.setObj(bsbyReason);
			json.setMsg("success");
		}else{
			json.setMsg("error");
		}
		return json;
	}
	
	
	
	
	/**
	 * 编辑
	 * 
	 */
	 
	@RequestMapping("/editBsbyReason")
	@ResponseBody 
	public String editBsbyReason(HttpServletRequest request,HttpServletResponse response){
		String msg="";
		voUser user = (voUser)request.getSession(false).getAttribute("userView");
		if(user == null) {
			msg="nologin";
			return msg;
		}
		String idstr=request.getParameter("id");
		if(!"".equals(StringUtil.checkNull(idstr))){
			
			int id=Integer.parseInt(idstr);
			String reason=request.getParameter("reason");
			String type=request.getParameter("type");
			
			
			BsbyReason bsbyReason=this.bsbyReasonService.selectBsbyReasonById(id);
			   
			   
		    BsbyReasonLog bsbyReasonLog=new BsbyReasonLog();
			bsbyReasonLog.setOperDateTime(DateUtil.formatTime(new Date()));
			bsbyReasonLog.setOperType("编辑");
			bsbyReasonLog.setOperUserId(user.getId());
			bsbyReasonLog.setOperUserName(user.getUsername());
			if(bsbyReason!=null){
			bsbyReasonLog.setType(bsbyReason.getType());
			bsbyReasonLog.setReason(bsbyReason.getReason());
			}
		   bsbyReasonLogService.addBsbyReasonLog(bsbyReasonLog);
			
			
		    int count=bsbyReasonService.updateBsbyReason("type="+type+",reason='"+reason+"'"," id="+id);
		    if(count>0){
				msg="success";
			}else{
				msg="error";
			}
			
			
		}else{
			msg="error";
		}
		
		
		return msg;
	}
	/**
	 * 获取报损报溢操作记录信息
	 * 
	 */
	@RequestMapping("/queryBsbyReasonLogs")
	public String queryBsbyReasonLogs(HttpServletRequest request,HttpServletResponse response){
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer condition = new StringBuffer();
		
		int countPerPage = 5;
		int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
		
		StringBuilder url = new StringBuilder();
		url.append("queryBsbyReasonLogs.mmx");
		condition.append("1=1");
		/*// 时间查询
		if (!"".equals(StringUtil.checkNull(dynamicCheckBean.getBeginDatetime()))&& !"".equals(StringUtil.checkNull(dynamicCheckBean.getEndDatetime()))) {
			condition.append(" and create_time between '"
					+ dynamicCheckBean.getBeginDatetime() + " 00:00:00" + "'");
			condition.append(" and '" + dynamicCheckBean.getEndDatetime()
					+ " 23:59:59" + "'");
		}*/

		map.put("condition", condition.toString());
		int totalCount = bsbyReasonLogService.getBsbyReasonLogCount(condition.toString());
		PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
		map.put("start", ""+paging.getCurrentPageIndex() * countPerPage);
		map.put("count", ""+ countPerPage);
		String order = null;
		
		if (order == null || "".equals(order)) {
			order = " id desc";
		}
		map.put("order", order);
		List<BsbyReasonLog> bsbyReasonLogs=	this.bsbyReasonLogService.queryBsbyReasonLogs(map);
	
		
	  paging.setPrefixUrl(url.toString());
	  request.setAttribute("paging", paging);
	  
	   request.setAttribute("bsbyReasonLogs", bsbyReasonLogs);
	   return "admin/bsby/bsbyReasonLog";
	}
	
	
}
