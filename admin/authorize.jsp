<%@ page contentType="text/html;charset=utf-8" %><%@ page import="adultadmin.util.*,adultadmin.bean.*"%><%@ page import="adultadmin.util.db.DbOperation,java.util.Date,adultadmin.action.vo.voUser,adultadmin.util.StringUtil,java.util.List,java.text.SimpleDateFormat,java.sql.ResultSet,java.util.ArrayList,mmb.system.admin.AdminService"%><%!
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
List<Object[]> getAuthorize(DbOperation dbOp,String sql){
	List ret = new ArrayList();
	ResultSet rs = null;
	try {
		rs = dbOp.executeQuery(sql);
		if (rs != null) {
			while (rs.next()) {
				int n = rs.getMetaData().getColumnCount();
				Object[] objs = new Object[n];
				for(int i = 0;i < n;i++)
					objs[i] = rs.getObject(i + 1);
				ret.add(objs);
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		dbOp.release();
	}
	return ret;
}
%><%
	adultadmin.framework.CustomAction action = new adultadmin.framework.CustomAction(request);
	voUser user = (voUser)session.getAttribute("myUser");
	voUser auser= (voUser)session.getAttribute("userView");;
	if(user==null){
		user = auser;
		auser = null;
	}
	//CookieUtil ck = new CookieUtil(request, response);
	//String auth_id = ck.getCookieValue("auth_id");
	String t=action.getParameterString("t");
	if(t!=null){
		if(t.equals("a")){
			String userName=action.getParameterString("user_name");
			String startTime=action.getParameterString("start_time");
			String endTime=action.getParameterString("end_time");
			Date start = new SimpleDateFormat("yyyy-MM-dd HH:00:00").parse(startTime);
			Date end = new SimpleDateFormat("yyyy-MM-dd HH:00:00").parse(endTime);
			if(start.getTime()>=end.getTime()){
				session.setAttribute("promptMsg", "开始时间应该在结束时间之前");
				response.sendRedirect("authorize.jsp");
				return;
			}
			if(userName==null||startTime==null||endTime==null){
				session.setAttribute("promptMsg", "添加失败");
				response.sendRedirect("authorize.jsp");
				return;
			}
			Date startDate = sdf.parse(startTime);
			Date endDate = sdf.parse(endTime);
			int seven = 7*24*3600*1000;
			if((endDate.getTime()-startDate.getTime())>seven){
				session.setAttribute("promptMsg", "添加失败");
				response.sendRedirect("authorize.jsp");
				return;
			}
			DbOperation dbOp = new DbOperation();
    		dbOp.init(DbOperation.DB);
    		int uid=dbOp.getInt("select id from admin_user where username='"+StringUtil.toSql(userName)+"' limit 1");
    		if(uid==0){
    			dbOp.release();
    			session.setAttribute("promptMsg", "用户不存在");
    			response.sendRedirect("authorize.jsp");
    			return;
    		}
    		if(uid==user.getId()){
    			dbOp.release();
    			session.setAttribute("promptMsg", "不能授权给自己");
    			response.sendRedirect("authorize.jsp");
    			return;
    		}
    		//需要限制同一时间范围内不允许授权给同一个人2次及以上
    		List<Object[]> list_name = getAuthorize(dbOp,"select suser_name from authorize where suser_id = "+uid+" and is_delete = 0 and muser_id="+user.getId()+" and ((start_time >= '"+StringUtil.toSql(startTime)+"' and start_time < '"+StringUtil.toSql(endTime)+"') or (end_time > '"+StringUtil.toSql(startTime)+"' and end_time <= '"+StringUtil.toSql(endTime)+"'))");
    		if(list_name.size()!=0){
    			dbOp.release();
    			session.setAttribute("promptMsg", "此时间内已经授权给["+list_name.get(0)[0]+"]了");
    			response.sendRedirect("authorize.jsp");
    			return;
    		}
    		dbOp.executeUpdate("insert into authorize set in_use = 0, muser_id="+user.getId()+",muser_name='"+StringUtil.toSql(user.getUsername())+
    			"',suser_id="+uid+",suser_name='"+StringUtil.toSql(userName)+"',start_time='"+StringUtil.toSql(startTime)+"',end_time='"+StringUtil.toSql(endTime)+"',is_delete=0,create_time='"+StringUtil.toSql(sdf.format(new Date()))+"'");
    		dbOp.release();
			session.setAttribute("promptMsg", "添加成功");
			response.sendRedirect("authorize.jsp");
			return;
		}
		if(t.equals("d")){
			int id=action.getParameterInt("id");
			if(id==0){
    			session.setAttribute("promptMsg", "授权不存在");
    			response.sendRedirect("authorize.jsp");
    			return;
			}
			DbOperation dbOp = new DbOperation();
    		dbOp.init(DbOperation.DB);
    		dbOp.executeUpdate("update authorize set in_use = 0,is_delete = 1 where id = "+id);
    		dbOp.release();
    		//使用记录截止
    		DbOperation dbOpSms = new DbOperation();
    		dbOpSms.init(DbOperation.DB_SMS);
    		dbOpSms.executeUpdate("update authorize_option_log set end_time='"+StringUtil.toSql(new SimpleDateFormat("yyyy-MM-dd HH:mm:00").format(new Date()))+"' where authorize_id = "+id+" and end_time is null");
    		dbOpSms.release();
    		session.setAttribute("promptMsg", "删除成功");
			response.sendRedirect("authorize.jsp");
			return;
		}
		if(t.equals("auth")){
			int id=action.getParameterInt("id");
			String userName=action.getParameterString("username");
			if(id==0||userName==null){
				session.setAttribute("promptMsg", "接受失败");
    			response.sendRedirect("authorize.jsp");
				return;
			}
			DbOperation dbOp = new DbOperation();
    		dbOp.init(DbOperation.DB);
			int uid=dbOp.getInt("select id from authorize where id="+id+" and muser_name='"+StringUtil.toSql(userName)+
				"' and suser_id="+user.getId()+" and now()>start_time and now()<end_time");
			//dbOp.release();
    		if(uid==0){
    			session.setAttribute("promptMsg", "接受失败");
    			response.sendRedirect("authorize.jsp");
				return;
    		}
			AdminService adminService=new AdminService();
			voUser vo =adminService.getAdmin(userName);
			adminService.releaseAll();
			if(vo==null){
				session.setAttribute("promptMsg", "接受失败");
				response.sendRedirect("authorize.jsp");
				return;
			}
			dbOp.executeUpdate("update authorize set in_use=0 where in_use=1 and suser_id="+user.getId());
			dbOp.executeUpdate("update authorize set in_use=1 where id="+uid);
			dbOp.release();
			//插入使用授权记录
			DbOperation dbOpSms = new DbOperation();
    		dbOpSms.init(DbOperation.DB_SMS);
    		dbOpSms.executeUpdate("update authorize_option_log set end_time='"+StringUtil.toSql(new SimpleDateFormat("yyyy-MM-dd HH:mm:00").format(new Date()))+"' where option_id = "+user.getId()+" and end_time is null");
    		dbOpSms.executeUpdate("insert into authorize_option_log set authorize_id="+uid+",option_id="+user.getId()+",option_name='"+StringUtil.toSql(user.getUsername())+
        			"',to_id="+vo.getId()+",to_name='"+StringUtil.toSql(userName)+"',start_time='"+StringUtil.toSql(new SimpleDateFormat("yyyy-MM-dd HH:mm:00").format(new Date()))+"',create_time='"+StringUtil.toSql(new SimpleDateFormat("yyyy-MM-dd HH:mm:00").format(new Date()))+"',option_type='"+StringUtil.toSql("接受")+"'");
    		dbOpSms.release();
			//授权操作
			request.getSession().setAttribute("userView", vo);
			request.getSession().setAttribute("myUser", user);
			//不同分支和账号切换自动登录授权账号
			// 吧授权的账号写入cookie
			//ck.setCookie("auth_username", vo.getUsername(), 2000000000);
			//ck.setCookie("auth_muser_id", String.valueOf(user.getId()), 2000000000);
			//ck.setCookie("auth_id", String.valueOf(uid), 2000000000);
			%><script type="text/javascript">if(top.topFrame!=null){window.parent.location.reload('../default.do')}</script><%
			return;
		}
		if(t.equals("back")){
			int id=action.getParameterInt("id");
			DbOperation dbOp = new DbOperation();
    		dbOp.init(DbOperation.DB);
    		dbOp.executeUpdate("update authorize set in_use=0 where id="+id);
			dbOp.release();
			//插入使用授权记录
			DbOperation dbOpSms = new DbOperation();
    		dbOpSms.init(DbOperation.DB_SMS);
    		dbOpSms.executeUpdate("update authorize_option_log set end_time='"+StringUtil.toSql(new SimpleDateFormat("yyyy-MM-dd HH:mm:00").format(new Date()))+"' where authorize_id="+id+" and end_time is null");
    		dbOpSms.release();
			//取消授权操作
			request.getSession().setAttribute("userView", user);
			request.getSession().removeAttribute("myUser");
			//不同分支和账号切换自动登录授权账号删除
			//ck.removeCookie("auth_muser_id");
			//ck.removeCookie("auth_username");
			//ck.removeCookie("auth_id");
			%><script type="text/javascript">if(top.topFrame!=null){window.parent.location.reload('../default.do')}</script><%
			return;
		}
	}
	DbOperation dbOp = new DbOperation();
    dbOp.init(DbOperation.DB);
    //dbOp.executeUpdate("delete from authorize where (muser_id="+user.getId()+" or suser_id="+user.getId()+") and now()>end_time");
    PagingBean page_muser_list = (PagingBean)request.getSession().getAttribute("authorize_jsp_page_muser_list");
    PagingBean page_suser_list = (PagingBean)request.getSession().getAttribute("authorize_jsp_page_suser_list");
    List<Object[]> muser_list  = null;
    List<Object[]> suser_list  = null;
    	muser_list=getAuthorize(dbOp,"select id from authorize where (muser_id="+user.getId()+")");
    	if(page_muser_list==null){
    		page_muser_list = new PagingBean();
    		page_muser_list.setCurrentPageIndex(1);
    	}else{
    		page_muser_list.setCurrentPageIndex(page_muser_list.getCurrentPageIndex());
    	}
    	page_muser_list.setTotalCount(muser_list.size());
    	page_muser_list.setTotalPageCount((muser_list.size() -1)/ 10 + 1);
    	
    	
    	suser_list=getAuthorize(dbOp,"select id from authorize where (suser_id="+user.getId()+")");
    	if(page_suser_list==null){
    		page_suser_list = new PagingBean();
    		page_suser_list.setCurrentPageIndex(1);
    	}else{
    		page_suser_list.setCurrentPageIndex(page_suser_list.getCurrentPageIndex());
    	}
    	page_suser_list.setTotalCount(suser_list.size());
    	page_suser_list.setTotalPageCount((suser_list.size()-1) / 10 + 1);
    	
    String pa = action.getParameterString("pa");
    String upa = action.getParameterString("upa");
    if(pa!=null){
    	if(pa.equals("pre")){
    		page_muser_list.setCurrentPageIndex(page_muser_list.getCurrentPageIndex()-1);
    		muser_list = getAuthorize(dbOp,"select * from authorize where (muser_id="+user.getId()+") order by create_time desc limit "+(page_muser_list.getCurrentPageIndex()-1)*10+",10");
    	}
    	if(pa.equals("nex")){
    		page_muser_list.setCurrentPageIndex(page_muser_list.getCurrentPageIndex()+1);
    		muser_list = getAuthorize(dbOp,"select * from authorize where (muser_id="+user.getId()+") order by create_time desc limit "+(page_muser_list.getCurrentPageIndex()-1)*10+",10");
    	}
    	if(pa.equals("las")){
    		page_muser_list.setCurrentPageIndex(page_muser_list.getTotalPageCount());
    		muser_list = getAuthorize(dbOp,"select * from authorize where (muser_id="+user.getId()+") order by create_time desc limit "+(page_muser_list.getTotalPageCount()-1)*10+",10");
    	}
    	if(pa.equals("1")){
    		page_muser_list.setCurrentPageIndex(1);
    		muser_list = getAuthorize(dbOp,"select * from authorize where (muser_id="+user.getId()+") order by create_time desc limit "+(page_muser_list.getCurrentPageIndex()-1)*10+",10");
    	}
    }else{
    	muser_list = getAuthorize(dbOp,"select * from authorize where (muser_id="+user.getId()+") order by create_time desc limit "+(page_muser_list.getCurrentPageIndex()-1)*10+",10");
    }
    if(upa!=null){
    	if(upa.equals("upre")){
    		page_suser_list.setCurrentPageIndex(page_suser_list.getCurrentPageIndex()-1);
    		suser_list = getAuthorize(dbOp,"select * from authorize where (suser_id="+user.getId()+") order by create_time desc limit "+(page_suser_list.getCurrentPageIndex()-1)*10+",10");
    	}
    	if(upa.equals("unex")){
    		page_suser_list.setCurrentPageIndex(page_suser_list.getCurrentPageIndex()+1);
    		suser_list = getAuthorize(dbOp,"select * from authorize where (suser_id="+user.getId()+") order by create_time desc limit "+(page_suser_list.getCurrentPageIndex()-1)*10+",10");
    	}
    	if(upa.equals("ulas")){
    		page_suser_list.setCurrentPageIndex(page_suser_list.getTotalPageCount());
    		suser_list = getAuthorize(dbOp,"select * from authorize where (suser_id="+user.getId()+") order by create_time desc limit "+(page_suser_list.getTotalPageCount()-1)*10+",10");
    	}
    	if(upa.equals("1")){
    		page_suser_list.setCurrentPageIndex(1);
    		suser_list = getAuthorize(dbOp,"select * from authorize where (suser_id="+user.getId()+") order by create_time desc limit "+(page_suser_list.getCurrentPageIndex()-1)*10+",10");
    	}
    }else{
    	suser_list = getAuthorize(dbOp,"select * from authorize where (suser_id="+user.getId()+") order by create_time desc limit "+(page_suser_list.getCurrentPageIndex()-1)*10+",10");
    }
    request.getSession().setAttribute("authorize_jsp_page_muser_list", page_muser_list);
    request.getSession().setAttribute("authorize_jsp_page_suser_list", page_suser_list);
%><!DOCTYPE html>
<html>
<head>
    <title>授权页面</title>
    <link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery/jquery-1.7.1.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
    <script type="text/javascript">
    	 function add(){
    	    var username=$("#username");
    	    //计算出相差天数
    	    if($.trim(username.val())==""){
    	    	username.focus();
    	    	alert("用户名不能为空");
    	    	return false;
    	    }
    	    var startTime = $("#startTime");
    	    var endTime = $("#endTime");
    	   var start = $("#startTime").val();
    	    var end = $("#endTime").val();
    	    start = start.replace(/-/g,"/");
    	    end = end.replace(/-/g,"/");
    	    var date1=new Date(start);
    	    var date2=new Date(end);
    	    var date3=date2.getTime()-date1.getTime();  //时间差的毫秒数
    	    var seven = 7*24*3600*1000;
    	    if(date3 > seven){
    	    	 alert("可选择的最大时间范围是7天！");
    	    	 return false;
    	    }
    	    if ($.trim(startTime.val()) == "") {
                alert("开始时间不能为空！");
                startTime.focus();
    	        return false;
    	    }
    	    var endTime = $("#endTime");
    	    if ($.trim(endTime.val()) == "") {
    	    	alert("结束时间不能为空");
    	    	endTime.focus();
    	    }
    	    $("#f1").submit();
		}
    </script>
</head>
<body>
<%@include file="../../header.jsp"%>
<fieldset  style="padding: 0; width:94%;">
    <legend>授权添加</legend>
    <form id="f1" action="authorize.jsp?t=a" method="post">
        <table cellpadding="3" cellspacing="1">
            <tr><td width="400">登录名: <input name="user_name" id="username" size="23" maxlength="23"  /></td></tr>
            <tr><td>开始时间: <input name="start_time" id="startTime" class="Wdate" size="21" type="text" value="<%=sdf.format(new Date())%>" 
            	onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:00:00'})" /></td></tr>
            <tr><td>结束时间: <input name="end_time" id="endTime" class="Wdate" type="text" size="21" value="<%=sdf.format(new Date(System.currentTimeMillis()+24*60*60*1000))%>" 
            	onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:00:00',minDate:'#F{$dp.$D(\'startTime\')}'})" /></td></tr>
        </table>
        <input type="button" value="确认添加" onclick="add()" style="margin:5px;"/>
    </form>
</fieldset>
<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
	<tr bgcolor="#4688D6" align="center"><td colspan="9"><font color="#EE2C2C">我授权给别人的</font></td></tr>
    <tr bgcolor="#4688D6" align="center">
    	<td style="width: 70px;"><font color="#FFFFFF">序号</font></td>
        <td style="width: 70px;"><font color="#FFFFFF">授权人ID</font></td>
        <td><font color="#FFFFFF">授权人用户名</font></td>
        <td style="width: 80px;"><font color="#FFFFFF">被授权人ID</font></td>
        <td><font color="#FFFFFF">被授权人用户名</font></td>
        <td style="width: 160px;"><font color="#FFFFFF">开始时间</font></td>
        <td style="width: 160px;"><font color="#FFFFFF">结束时间</font></td>
        <td style="width: 80px;"><font color="#FFFFFF">状态</font></td>
        <td style="width: 80px;"><font color="#FFFFFF">操作</font></td>
    </tr><%
    if(muser_list!=null&&muser_list.size()>0){
    	 for(int i=0;i<muser_list.size();i++){
    		 Object[] objArray=muser_list.get(i);%>
			    <tr bgcolor='#F8F8F8' align="center" class="dataRow">
			    <td><%=i+1%></td>
			    <td><%=user.getId()%></td>
			    <td><%=user.getUsername()%></td>
			    <td><%=objArray[2]%></td>
			    <td><%=objArray[3]%></td>
			    <td><%=objArray[4].toString().substring(0,19)%></td>
			    <td><%=objArray[5].toString().substring(0,19)%></td>
			    <td><%if("1".equals(objArray[7].toString())){%>已删除</td><td><span style="color: gray;">删除</span></td><%}
			    	else if(new SimpleDateFormat("yyyy-MM-dd HH:mm:00").parse(objArray[5].toString()).getTime()<new Date().getTime()){%>已过期</td><td><a style="color:black;" href="authorize.jsp?t=d&id=<%=objArray[0]%>">删除</a></td><%}
			    	else if(new SimpleDateFormat("yyyy-MM-dd HH:mm:00").parse(objArray[4].toString()).getTime()>new Date().getTime()){%>未开始</td><td><a style="color:black;" href="authorize.jsp?t=d&id=<%=objArray[0]%>">删除</a></td><%}
			    	else if("1".equals(objArray[8].toString())){%>使用中</td><td><a style="color:black;" href="authorize.jsp?t=d&id=<%=objArray[0]%>">删除</a></td><%}
			    	else{%>正常</td><td><a style="color:black;" href="authorize.jsp?t=d&id=<%=objArray[0]%>">删除</a></td><%}%>
			    
			    
			    </tr><%
	    }%>
    <tr align="center">
     	<td><%if(page_muser_list.getCurrentPageIndex()>1){
     	%><a href="authorize.jsp?pa=1">首页</a><%}else{%>首页<%}%></td>
	 	<td><%if(page_muser_list.getCurrentPageIndex()>1){%>
	 	<a href="authorize.jsp?pa=pre">上一页</a><%}else{%>上一页<%}%>
	 	</td>
		<td>第<%=page_muser_list.getCurrentPageIndex() %>页</td>
		<td><%if(page_muser_list.getCurrentPageIndex()<page_muser_list.getTotalPageCount()){%><a href="authorize.jsp?pa=nex">下一页</a><%}else{%>下一页<%}%></td>
	 	<td><%if(page_muser_list.getCurrentPageIndex()<page_muser_list.getTotalPageCount()){%><a href="authorize.jsp?pa=las">尾页</a><%}else{%>尾页<%}%></td>
	 	<td>共<%=page_muser_list.getTotalPageCount()%>页</td>
    </tr>
    <%}%>
</table><br/>
<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
	<tr bgcolor="#4688D6" align="center"><td colspan="9"><font color="#FFFFFF">别人授权给我的</font></td></tr>
    <tr bgcolor="#4688D6" align="center">
    	<td style="width: 70px;"><font color="#FFFFFF">序号</font></td>
        <td style="width: 70px;"><font color="#FFFFFF">授权人ID</font></td>
        <td><font color="#FFFFFF">授权人用户名</font></td>
         <td style="width: 80px;"><font color="#FFFFFF">被授权人ID</font></td>
        <td><font color="#FFFFFF">被授权人用户名</font></td>
        <td style="width: 160px;"><font color="#FFFFFF">开始时间</font></td>
        <td style="width: 160px;"><font color="#FFFFFF">结束时间</font></td>
        <td style="width: 80px;"><font color="#FFFFFF">状态</font></td>
        <td style="width: 80px;"><font color="#FFFFFF">操作</font></td>
    </tr><%
    if(suser_list!=null&&suser_list.size()>0){
    	 for(int i=0;i<suser_list.size();i++){
    		 Object[] objArray=suser_list.get(i);%>
			    <tr bgcolor='#F8F8F8' align="center" class="dataRow">
			    <td><%=i+1%></td>
			    <td><%=objArray[1]%></td>
			    <td><%=objArray[6]%></td>
			     <td><%=user.getId()%></td>
			    <td><%=user.getUsername()%></td>
			    <td><%=objArray[4].toString().substring(0,19)%></td>
			    <td><%=objArray[5].toString().substring(0,19)%></td>
			    <td><%if("1".equals(objArray[7].toString())){%>已删除</td><td><span style="color: gray;">返回</span></td><%}
			    	else if(new SimpleDateFormat("yyyy-MM-dd HH:mm:00").parse(objArray[5].toString()).getTime()<new Date().getTime()){%>已过期</td><td><span style="color: gray;">返回</span></td><%}
			    	else if(new SimpleDateFormat("yyyy-MM-dd HH:mm:00").parse(objArray[4].toString()).getTime()>new Date().getTime()){%>未开始</td><td><span style="color: gray;">返回</span></td><%}
			    	else if("0".equals(objArray[8].toString())){%>正常</td><td><a href="authorize.jsp?t=auth&username=<%=objArray[6]%>&id=<%=objArray[0]%>" onclick="if(confirm('确定切换到当前帐号吗?')==false)return false;" style="color: black;">接受</a></td><%
			    	}else if("1".equals(objArray[8].toString())){%>使用中</td><td><a style="color: black;" href="authorize.jsp?t=back&id=<%=objArray[0] %>" onclick="if(confirm('确定返回?')==false)return false;">返回</a></td><%
			    	}%>
			    </tr><%
		    }%>
    	<tr  align="center">
     	<td><%if(page_suser_list.getCurrentPageIndex()>1){
     	%><a href="authorize.jsp?upa=1">首页</a><%}else{%>首页<%}%></td>
	 	<td><%if(page_suser_list.getCurrentPageIndex()>1){%>
	 	<a href="authorize.jsp?upa=upre">上一页</a><%}else{%>上一页<%}%>
	 	</td>
		<td>第<%=page_suser_list.getCurrentPageIndex() %>页</td>
		<td><%if(page_suser_list.getCurrentPageIndex()<page_suser_list.getTotalPageCount()){%><a href="authorize.jsp?upa=unex">下一页</a><%}else{%>下一页<%}%></td>
	 	<td><%if(page_suser_list.getCurrentPageIndex()<page_suser_list.getTotalPageCount()){%><a href="authorize.jsp?upa=ulas">尾页</a><%}else{%>尾页<%}%></td>
	 	<td>共<%=page_suser_list.getTotalPageCount()%>页</td>
    </tr> 
    <%}%>
</table>
</body>
</html>
 