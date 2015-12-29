<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*,adultadmin.bean.*,adultadmin.action.vo.*,adultadmin.action.admin.OldCustomersAction,adultadmin.util.StringUtil"%>
<%@ page import="adultadmin.framework.*" %>
<%
    //判断有没有权限
    if(!PermissionFrk.hasPermission(request, PermissionFrk.USER_ADMIN)){
		return;
    }
voUser user = (voUser) request.getAttribute("user");
voUser adminUser = (voUser)session.getAttribute("userView");
%>
<table border="1" width="100%">
<tr>
<td>用户ID：</td><td><font color="red"><%=user.getId()%></font></td>
</tr>
<tr>
<td>用户名：</td><td><font color="red"><%=user.getUsername()%></font></td>
</tr>
<%if(user.getSecurityLevel() == 10){%>
<tr>
<td>密码：</td><td><font color="red"><%=user.getPassword()%></font></td>
</tr>
<%}%>
<tr>
<td>姓名：</td><td><font color="red"><%=user.getName()%></font></td>
</tr>
<tr>
<td>电话：</td><td><font color="red"><%=user.getPhone()%></font></td>
</tr>
<tr>
<td>地址：</td><td><font color="red"><%=user.getAddress()%></font></td>
</tr>
<tr>
<td>邮编：</td><td><font color="red"><%=user.getPostcode()%></font></td>
</tr>
<tr>
<td>昵称：</td><td><font color="red"><%=user.getNick()%></font></td>
</tr>
</table>