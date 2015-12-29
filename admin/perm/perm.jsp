<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voOrder"%><%@ page import="java.util.*"%>
<%@ page import="adultadmin.action.vo.voUser" %><%@ page import="adultadmin.framework.*" %><%@ page import="adultadmin.bean.*" %>
<%
	voUser user = (voUser)session.getAttribute("userView");
	adultadmin.bean.UserGroupBean group = user.getGroup();
	if(!group.isFlag(0)) {
		response.sendRedirect("error.jsp");
		return;
	}
	
	CustomAction action = new CustomAction(request);
	int id = action.getParameterInt("id");
	PermissionBean g = PermissionFrk.getPermission(id);
	if(g==null){
		response.sendRedirect("perms.jsp");
		return;
	}
	
	if(!action.isMethodGet()) {
		g.setParent(action.getParameterInt("parent"));
		g.setName(request.getParameter("name"));
		g.setBak(request.getParameter("bak"));
		PermissionFrk.updatePermission(g, false);
		session.setAttribute("promptMsg", "修改权限成功");
		response.sendRedirect("perms.jsp");
		return;
	}
%>
<html>
<title>买卖宝后台</title>
<script>
</script>
<script type="text/javascript" src="../js/JS_functions.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body style="margin-left:12px;">
<%@include file="../../header.jsp"%>
<br>
<form action="perm.jsp?id=<%=g.getId()%>" method=post>
名称:<input type=text name="name" value="<%=g.getName()%>">(id : <%=g.getId()%>)<br/>
备注:<textarea name="bak" cols=60 rows=5><%=g.getBak()%></textarea><br/><br/>
父权限:<input type=text name="parent" value="<%=g.getParent()%>"><br/>
<input type="submit" value="确认修改">
</form>
<br>

</body>
</html>