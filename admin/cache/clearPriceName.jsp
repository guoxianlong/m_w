<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.framework.rpc.*"%>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();
if(group.isFlag(73)){
	RPCClient client = new RPCClient();
	String url = "/rpc/cachePriceName.jsp";
	String res = client.doRequestAll(url);
%><script>alert("内容替换完成<%=res%>");window.close();</script><%
	return;
} else {
	%><script>alert("你没有权限这么做.");window.close();</script><%
	return;
}
%>