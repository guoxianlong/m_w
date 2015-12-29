<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="cache.*, java.util.*"%>
<%
if(request.getParameter("change") != null){
	CacheAdmin.flushAll();
	response.sendRedirect(response.encodeURL("cacheAdmin.jsp"));	
}
%>
<a href="<%=response.encodeURL("cacheAdmin.jsp?change=1")%>">重新载入缓存数据</a><br/>