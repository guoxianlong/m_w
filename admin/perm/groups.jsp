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
	List list = new ArrayList(PermissionFrk.getGroupMap().values());
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
  <table cellpadding="5" cellspacing="1" bgcolor="#e8e8e8">
    <tr bgcolor="#4688D6">
      <td align="center"><font color="#FFFFFF">id</font></td>
      <td align="center"><font color="#FFFFFF">名称</font></td>
      <td align="center" width="60"><font color="#FFFFFF">查看组成员</font></td>
      <td align="center" width="400"><font color="#FFFFFF">备注</font></td>
    </tr>
<%for(int i = 0;i < list.size();i++){
UserGroupBean g = (UserGroupBean)list.get(i);
%><tr bgcolor='#F8F8F8'>
	<td align=left><%=g.getId()%></td>
	<td align=left ><a href="group.jsp?id=<%=g.getId()%>"><%=g.getName()%></a></td>
	<td align="center" width="80"><a href="users.jsp?groupId=<%=g.getId()%>">查看</a></td>
	<td align=left><%=g.getBak()%></td>
</tr>
<%}%>
</table>
<br>
<a href="groupa.jsp">添加权限组</a><br/>
<br>
<a href="clearCache.jsp" onclick="return confirm('此操作将清除所有权限缓存，确认？')">清除权限组缓存</a><br/>
<br>
<br>

</body>
</html>