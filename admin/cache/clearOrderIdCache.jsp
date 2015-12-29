<%@ page contentType="text/html;charset=utf-8" %>
<%@ include file="../../taglibs.jsp"%>
<%@page import="java.sql.*,java.util.*"%>
<%@ page import="adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.service.*" %>
<%@ page import="adultadmin.bean.*" %>
<html>
<title>买卖宝后台</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<body>
<%@include file="../../header.jsp"%>
<%
	StatUtil.dayOrderIdMap = new HashMap();
	StatUtil.dayOrderIdMap2 = new HashMap();
	StatUtil.dateTimeOrderIdMap = new HashMap();
	StatUtil.todayOrderId = 0;
%>
<script type="text/javascript">
window.alert('操作成功');
</script>
<br/>
<br/>
</body>

