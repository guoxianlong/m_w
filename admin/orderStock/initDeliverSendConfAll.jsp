<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.bean.order.*" %>
<%@ page import="adultadmin.framework.rpc.*"%>
<%
	String returnString = "";
	RPCClient client = new RPCClient();
	String url = "http://ware.ebinf.com/adult-admin/enter/rpc/initDeliverSendConfMap.jsp";
	boolean res = client.doRequest(url);
	if (res) {
		returnString +="刷新物流定时任务缓存成功</br>";
	} else {
		returnString +="刷新物流定时任务缓存失败</br>";
	}
	response.getWriter().write(returnString);
%>