<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.bean.order.*" %>
<%
	String responseString = OrderStockBean.initAreaDeliverPriorityMap();
	response.getWriter().write(responseString);
%>