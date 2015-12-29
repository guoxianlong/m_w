<%@page import="cache.ProductPreferenceCache"%>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.framework.rpc.*"%>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();
if(group.isFlag(73)){
	RPCClient client = new RPCClient();
	String url = "/rpc/clearProductPreferenceCache.jsp"; //正式
	//String url = "http://localhost:9080/wap/rpc/clearProductPreferenceCache.jsp"; //测试
	String res = client.doRequestAll(url);
   	String adminUrl = "/enter/rpc/clearAdminProductPreferenceCache.jsp";
	String adminres = client.doAdminRequestAll(adminUrl);
%><script>alert("满减活动缓存清理完成<%=res+"\\n"+adminres%>");window.close();</script><%
	return;
} else {
	%><script>alert("你没有权限这么做.");window.close();</script><%
	return;
}
%>