<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.bean.order.*" %>
<%
	String responseString = OrderStockBean.initDeliverSentConfMap();
	if (responseString.equals("")) {
		response.getWriter().write("刷新成功");
	} else {
		response.getWriter().write("刷新失败");
	}
%>