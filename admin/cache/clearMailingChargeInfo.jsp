<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="cache.*, java.util.*"%>
<%
if(request.getParameter("change") != null){
	CacheAdmin.flushMailingChargeInfo();
	response.sendRedirect(response.encodeURL("clearMailingChargeInfo.jsp"));	
}
%>
<a href="<%=response.encodeURL("clearMailingChargeInfo.jsp?change=1")%>">清空物流收费标准</a><br/>