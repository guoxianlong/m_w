<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voOrder"%><%@ page import="java.util.*"%>
<%@ page import="adultadmin.action.vo.voUser" %><%@ page import="adultadmin.framework.*" %><%@ page import="adultadmin.framework.rpc.*" %><%@ page import="adultadmin.bean.*" %>
<%
	voUser user = (voUser)session.getAttribute("userView");
	adultadmin.bean.UserGroupBean group = user.getGroup();
	if(!group.isFlag(0)) {
		response.sendRedirect("error.jsp");
		return;
	}
%>
<html>
<title>买卖宝后台</title>
<script>
</script>
<script type="text/javascript" src="../js/JS_functions.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body style="margin-left:12px;line-height:150%;font-size:14px;">
<%@include file="../../header.jsp"%>
<br>
<a href="users.jsp">查看<font color=red>用户权限</font>设置</a><br/>
<a href="groups.jsp">查看<font color=red>权限组</font>设置</a><br/>
<a href="perms.jsp">查看<font color=red>权限</font>设置</a><br/>
<br/>
<br/>
<a href="clearCache.jsp" onclick="return confirm('此操作将清除所有权限缓存，确认？')">清除权限组缓存</a><br/>
<br/>
<br/>
<a href="../system/jobs.jsp"><font color=brown>定时任务</font>高级管理</a><br/>
<a href="../tree/viewTree3.jsp"><font color=green>左侧功能树</font>管理</a><br/>
<a href="../system/ipcontrol.jsp">内部IP管理</a><br/>
<br/>当前rpc服务器:<br/>
<%
if(RPCClient.shopServers!=null){
for(int i=0;i<RPCClient.shopServers.length;i++){%>
<%=RPCClient.shopServers[i]%><br/>
<%}}%>
<br/>
<form method=post action="index.jsp">
密码原文:<input type=text name=pwd><input type=submit value="加密">
</form>
<%
String pwd=request.getParameter("pwd");
if(pwd!=null){
%><input type=text size="30" value="<%=mmb.util.Secure.encryptPwd(pwd)%>"/><%}%>
</body>
</html>