<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*,adultadmin.bean.*"%>
<%@ page import="adultadmin.util.db.DbOperation,java.util.Date,adultadmin.action.vo.voUser,adultadmin.util.StringUtil,java.util.List,java.text.SimpleDateFormat,java.sql.ResultSet,java.util.ArrayList,mmb.system.admin.AdminService"%><%!
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
List<Object[]> getAuthorizeLog(DbOperation dbOp,String sql){
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
}%>
<%
adultadmin.framework.CustomAction action = new adultadmin.framework.CustomAction(request);



List<Object[]> authorizeLogs;
DbOperation dbOpSms = new DbOperation();
dbOpSms.init(DbOperation.DB);
authorizeLogs = getAuthorizeLog(dbOpSms,"select id from authorize");
PagingBean pageBean = (PagingBean)request.getSession().getAttribute("auth_log_page");

if(pageBean==null){
	pageBean = new PagingBean();
	pageBean.setCurrentPageIndex(1);
}else{
	pageBean.setCurrentPageIndex(pageBean.getCurrentPageIndex());
}
pageBean.setTotalCount(authorizeLogs.size());
pageBean.setTotalPageCount((authorizeLogs.size() -1)/ 20 + 1);
dbOpSms.release();
String p = action.getParameterString("p");
String userId=action.getParameterString("userId")==null?"":action.getParameterString("userId");
String userName=action.getParameterString("userName")==null?"":action.getParameterString("userName");
String authType=action.getParameterString("authType")==null?"":action.getParameterString("authType");
String start_time=action.getParameterString("start_time")==null?"":action.getParameterString("start_time");
String end_time=action.getParameterString("end_time")==null?"":action.getParameterString("end_time");
String sql = "";
	if(authType!=null&&!"".equals(authType)){
		if(authType.equals("1")){
			if(userId!=null&&!"".equals(userId)){
				sql = sql + " and muser_id = "+userId;
			}
			if(userName!=null&&!"".equals(userName)){
				sql = sql + " and muser_name = '"+StringUtil.toSql(userName)+"'";
			}
		}else if(authType.equals("2")){
			if(userId!=null&&!"".equals(userId)){
				sql = sql + " and suser_id = "+userId;
			}
			if(userName!=null&&!"".equals(userName)){
				sql = sql + " and suser_name = '"+StringUtil.toSql(userName)+"'";
			}
		}else{
			if(userId!=null&&!"".equals(userId)){
				sql = sql + " and (muser_id = "+userId+" or suser_id="+userId+")";
			}
			if(userName!=null&&!"".equals(userName)){
				sql = sql + " and (muser_name = '"+StringUtil.toSql(userName)+"' or suser_name = '"+StringUtil.toSql(userName)+"')";
			}
		}
	}
	if((start_time!=null&&!"".equals(start_time))&&(end_time!=null&&!"".equals(end_time))){
		sql = sql + " and ((start_time >= '"+StringUtil.toSql(start_time)+"' and start_time <= '"+StringUtil.toSql(end_time)+"') or (end_time >= '"+StringUtil.toSql(start_time)+"' and end_time <= '"+StringUtil.toSql(end_time)+"'))";
	}
if(p!=null){
	if(p.equals("d")){
		int id=action.getParameterInt("id");
		if(id==0){
			session.setAttribute("promptMsg", "授权记录不存在");
			response.sendRedirect("authorizeLog.jsp");
			return;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		dbOp.executeUpdate("update authorize set is_delete = 1 where id = "+id);
		dbOp.release();
		session.setAttribute("promptMsg", "删除成功");
		response.sendRedirect("authorizeLog.jsp");
		return;
	}
	if(p.equals("s")){
		String sqlStr = "select * from authorize where 1 = 1"+sql+" order by create_time desc limit ";
		String v=action.getParameterString("v");
		if(v.equals("pre")){
			dbOpSms = new DbOperation();
			dbOpSms.init(DbOperation.DB);
			pageBean.setCurrentPageIndex(pageBean.getCurrentPageIndex()-1);
			authorizeLogs = getAuthorizeLog(dbOpSms,sqlStr+(pageBean.getCurrentPageIndex()-1)*20+",20");
    	}
		else if(v.equals("nex")){
    		dbOpSms = new DbOperation();
    		dbOpSms.init(DbOperation.DB);
    		pageBean.setCurrentPageIndex(pageBean.getCurrentPageIndex()+1);
    		authorizeLogs = getAuthorizeLog(dbOpSms,sqlStr+(pageBean.getCurrentPageIndex()-1)*20+",20");
    	}
		else if(v.equals("las")){
    		dbOpSms = new DbOperation();
    		dbOpSms.init(DbOperation.DB);
    		pageBean.setCurrentPageIndex(pageBean.getTotalPageCount());
    		authorizeLogs = getAuthorizeLog(dbOpSms,sqlStr+(pageBean.getTotalPageCount()-1)*20+",20");
    	}
		else if(v.equals("1")){
    		dbOpSms = new DbOperation();
    		dbOpSms.init(DbOperation.DB);
    		pageBean.setCurrentPageIndex(1);
    		authorizeLogs = getAuthorizeLog(dbOpSms,sqlStr+(pageBean.getCurrentPageIndex()-1)*20+",20");
    	}else{
    		dbOpSms = new DbOperation();
    		dbOpSms.init(DbOperation.DB);
    		pageBean.setCurrentPageIndex(1);
    		authorizeLogs = getAuthorizeLog(dbOpSms,sqlStr+(pageBean.getCurrentPageIndex()-1)*20+",20");
    	}
	}
}else{
	dbOpSms = new DbOperation();
	dbOpSms.init(DbOperation.DB);
	authorizeLogs = getAuthorizeLog(dbOpSms,"select * from authorize order by create_time desc limit "+(pageBean.getCurrentPageIndex()-1)*20+",20");
}
request.getSession().setAttribute("auth_log_page", pageBean);
%>
<!DOCTYPE html>
<html>
<head>
    <title>授权记录管理</title>
    <link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery/jquery-1.7.1.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
    <script type="text/javascript">
    </script>
</head>
<body>
<%@include file="../../header.jsp"%>
<table cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
	<tr bgcolor="#4688D6" align="center"><td colspan="9"><font color="#EE2C2C">查看授权记录</font></td></tr>
	<tr><form action="authorizeLog.jsp?p=s&v=0" method="post">
		<td style="width:20%;height:23px;">用户ID:<input name="userId" type="text"/></td>
		<td style="width: 20%;height:23px;">用户名:<input name="userName" type="text"/></td>
		<td style="width: 20%;height:23px;">授权类型:<select name="authType">
			<option value="0">请选择</option>
			<option value="1">授权方</option>
			<option value="2">被授权方</option>
		</select></td>
		<td   style="width: 40%;height:23px;">时间: <input name="start_time" id="startTime" class="Wdate" size="21" type="text" value="" 
            	onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:00:00'})" />-<input name="end_time" class="Wdate" id="endTime"  type="text" size="21" value="" 
            	onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:00:00',minDate:'#F{$dp.$D(\'startTime\')}'})" /></td>
        <td style="width:20%;height:23px;"><input type="submit" value="搜索"/></td></form></tr>
    </table>
    <table>
    <tr bgcolor="#4688D6" align="center">
    	<td style="width: 5%;"><font color="#FFFFFF">序号</font></td>
        <td style="width: 10%;"><font color="#FFFFFF">授权人ID</font></td>
        <td style="width: 10%;"><font color="#FFFFFF">授权人用户名</font></td>
        <td style="width: 10%;"><font color="#FFFFFF">被授权人ID</font></td>
        <td style="width: 10%;"><font color="#FFFFFF">被授权人用户名</font></td>
        <td style="width: 10%;"><font color="#FFFFFF">开始时间</font></td>
        <td style="width: 10%;"><font color="#FFFFFF">结束时间</font></td>
        <td style="width: 10%;"><font color="#FFFFFF">状态</font></td>
        <td style="width: 10%;"><font color="#FFFFFF">操作</font></td>
    </tr>
    <%
    if(authorizeLogs!=null&&authorizeLogs.size()>0){
    	 for(int i=0;i<authorizeLogs.size();i++){
    		 Object[] objArray=authorizeLogs.get(i);%>
			    <tr bgcolor='#F8F8F8' align="center" class="dataRow">
			    <td><%=i+1%></td>
			    <td><%=objArray[1]%></td>
			    <td><%=objArray[6]%></td>
			    <td><%=objArray[2]%></td>
			    <td><%=objArray[3]%></td>
			    <td><%=objArray[4].toString().substring(0,19)%></td>
			    <td><%=objArray[5].toString().substring(0,19)%></td>
			    <td><%if("1".equals(objArray[7].toString())){%>已删除</td><td><span style="color: gray;">删除</span></td><%}
			    	else if(sdf.parse(objArray[5].toString()).getTime()<new Date().getTime()){%>已过期</td><td><a style="color:black;" href="authorize.jsp?t=d&id=<%=objArray[0]%>">删除</a></td><%}
			    	else if(sdf.parse(objArray[4].toString()).getTime()>new Date().getTime()){%>未开始</td><td><a style="color:black;" href="authorize.jsp?t=d&id=<%=objArray[0]%>">删除</a></td><%}
			    	else if("1".equals(objArray[8].toString())){%>使用中</td><td><a style="color:black;" href="authorizeLog.jsp?p=d&id=<%=objArray[0]%>">删除</a></td><%}
			    	else{%>正常</td><td><a style="color:black;" href="authorizeLog.jsp?p=d&id=<%=objArray[0]%>">删除</a></td><%}%>
			    </tr><%
	    }%>
    <tr align="center">
     	<td><%if(pageBean.getCurrentPageIndex()>1){
     	%><a href="authorizeLog.jsp?p=s&v=1&userId=<%=userId%>&userName=<%=userName%>&authType=<%=authType%>&start_time=<%=start_time%>&end_time=<%=end_time%>">首页</a><%}else{%>首页<%}%></td>
	 	<td><%if(pageBean.getCurrentPageIndex()>1){%>
	 	<a href="authorizeLog.jsp?p=s&v=pre&userId=<%=userId%>&userName=<%=userName%>&authType=<%=authType%>&start_time=<%=start_time%>&end_time=<%=end_time%>">上一页</a><%}else{%>上一页<%}%>
	 	</td>
		<td>第<%=pageBean.getCurrentPageIndex() %>页</td>
		<td><%if(pageBean.getCurrentPageIndex()<pageBean.getTotalPageCount()){%><a href="authorizeLog.jsp?p=s&v=nex&userId=<%=userId%>&userName=<%=userName%>&authType=<%=authType%>&start_time=<%=start_time%>&end_time=<%=end_time%>">下一页</a><%}else{%>下一页<%}%></td>
	 	<td><%if(pageBean.getCurrentPageIndex()<pageBean.getTotalPageCount()){%><a href="authorizeLog.jsp?p=s&v=las&userId=<%=userId%>&userName=<%=userName%>&authType=<%=authType%>&start_time=<%=start_time%>&end_time=<%=end_time%>">尾页</a><%}else{%>尾页<%}%></td>
	 	<td>共<%=pageBean.getTotalPageCount()%>页</td>
    </tr>
    <%}%>
</table>
</body>
</html>