<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.bean.order.*" %>
<%@ page import="adultadmin.framework.rpc.*"%>
<%
	String returnString = "";
	RPCClient client = new RPCClient();
	String url = "https://ware.ebinf.com/ware/enter/rpc/initAreaDeliverPriorityMap.jsp";
	boolean res = client.doRequest(url);
	if (res) {
		returnString +="刷新物流缓存成功</br>";
	} else {
		returnString +="刷新物流缓存失败</br>";
	}
	url = "http://ware.ebinf.com:8080/adult-admin/enter/rpc/initAreaDeliverPriorityMap.jsp";
	res = client.doRequest(url);
	if (res) {
		returnString +="刷新物流定时任务缓存成功</br>";
	} else {
		returnString +="刷新物流定时任务缓存失败</br>";
	}
	url = "https://sales.ebinf.com/sale/enter/rpc/initAreaDeliverPriorityMap.jsp";
	res = client.doRequest(url);
	if (res) {
		returnString +="刷新销售缓存成功</br>";
	} else {
		returnString +="刷新销售缓存失败</br>";
	}
	url = "http://sales.ebinf.com:8080/adult-admin/enter/rpc/initAreaDeliverPriorityMap.jsp";
	res = client.doRequest(url);
	if (res) {
		returnString +="刷新销售定时任务缓存成功";
	} else {
		returnString +="刷新销售定时任务缓存失败";
	}
	response.getWriter().write(returnString);
%>