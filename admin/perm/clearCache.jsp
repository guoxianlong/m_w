<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.action.vo.voUser" %>
<%
	voUser user = (voUser)session.getAttribute("userView");
	adultadmin.bean.UserGroupBean group = user.getGroup();
	if(!group.isFlag(0)) {
		response.sendRedirect("error.jsp");
		return;
	}
	adultadmin.framework.PermissionFrk.clearGroup();
%>
<script language="JavaScript">
window.history.back(-1);
</script>