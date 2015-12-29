<%@page import="cache.ProductPreferenceCache"%>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.framework.rpc.*"%>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="cache.*" %>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();
if(group.isFlag(73)){
	RPCClient client = new RPCClient();
	String url = "/rpc/clearAutoPresentCache.jsp"; //正式
	//String url = "http://localhost:9080/wap/rpc/clearAutoPresentCache.jsp"; //测试
	String res = client.doRequestAll(url);
	AutoPresentFrk.init();
%><script>alert("自动发放赠品缓存清理完成<%=res%>");window.close();</script><%
	return;
} else {
	%><script>alert("你没有权限这么做.");window.close();</script><%
	return;
}
%>