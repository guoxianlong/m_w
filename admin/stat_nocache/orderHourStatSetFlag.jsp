<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stat.*" %>
<%
String curDate = request.getParameter("date");
StatAction action = new StatAction();
action.setFlag(request, response);
response.sendRedirect("orderHourStat.jsp?date=" + curDate);
%>