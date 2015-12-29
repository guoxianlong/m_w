<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*" %>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);
%>
对不起，其他人正在查询，请稍候再查。<br/>
现在在查询的人及所做操作是：<br/>
<% String db = request.getParameter("db"); %>
<%if("adult".equals(db)){ %>
<%=DbLock.operator%><br/>
<%} else if("adult_slave".equals(db)){%>
<%=DbLock.slaveServerOperator%><br/>
<%} else if("adult_stat".equals(db)){%>
<%=DbLock.statServerOperator%><br/>
<%} else if("adult_slave2".equals(db)){%>
<%=DbLock2.slaveServerOperator%><br/>
<%}%>