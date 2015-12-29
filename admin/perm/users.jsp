<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voOrder"%><%@ page import="adultadmin.bean.UserGroupBean"%><%@ page import="java.util.*"%>
<%@ page import="adultadmin.action.vo.voUser" %><%@ page import="adultadmin.framework.*" %><%@ page import="adultadmin.bean.*" %>
<%
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
	if(!group.isFlag(0)) {
		response.sendRedirect("error.jsp");
		return;
	}
	CustomAction action = new CustomAction(request);
	int groupId = action.getParameterIntS("groupId");
	List list;
	if(groupId >= 0)
		list = PermissionFrk.getUserPermissionList("groups like '%,"+groupId+",%' order by id");
	else if(request.getParameter("u")!=null)
		list = PermissionFrk.getUserPermissionList("user_id=(select id from admin_user where username='"+request.getParameter("u")+"') order by id");
	else
		list = PermissionFrk.getUserPermissionList("1 order by id");

%>
<html>
<title>买卖宝后台</title>
<script>
</script>
<script type="text/javascript" src="../js/JS_functions.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body style="margin-left:12px;" onload="document.f1.u.focus();document.f1.u.select();">
<%@include file="../../header.jsp"%>
<br>
<form action="users.jsp" method=post name=f1>
<input type=text name="u" value="<%=user.getUsername()%>"><input type=submit value="搜索用户名">
</form>
<br>
  <table width="90%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
      <tr bgcolor="#4688D6">
      <td width="100" align="center"><font color="#FFFFFF">id</font></td>
      <td width="100" align="center"><font color="#FFFFFF">username</font></td>
      <td align="center"><font color="#FFFFFF">权限组</font></td>
      <td align="center" width="100"><font color="#FFFFFF">隶属部门</font></td>
      <td align="center" width="100"><font color="#FFFFFF">用户级别</font></td>
    </tr>
<%for(int i = 0;i < list.size();i++){
UserPermissionBean p = (UserPermissionBean)list.get(i);
voUser u = PermissionFrk.getAdminUser("id="+p.getUserId());
if(u==null){
	continue;
}
int[] groups = p.getGroups();
%><tr bgcolor='#F8F8F8'>
	<td width="100" align=left><%if(u!=null){%><%=p.getUserId()%><%}%></td>
	<td align=left width="100"><%if(u!=null){%><a href="user.jsp?id=<%=u.getId()%>"><%=u.getUsername()%></a><%}%></td>
	<td align=left><%
	
for(int i2=0;i2<groups.length;i2++){
	UserGroupBean g = PermissionFrk.getUserGroup(groups[i2]);
	if(g==null) continue;
	%><%if(i2!=0){%>,<%}%><%=g.getName()%><%
}

%></td>
	<td align=left><%if(p.getPermission()<UserGroupBean.deptNames.length){%><%=UserGroupBean.deptNames[p.getPermission()]%><%} %></td>
	<td align=left><%=UserGroupBean.securityLevelNames[p.getSecurityLevel()]%></td>
</tr>
<%}%>
</table>
<br>
<br>
</body>
</html>