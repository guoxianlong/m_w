<%@ page contentType="text/html;charset=utf-8" %>
<%@page import="java.sql.*,java.util.*"%>
<%@ page import="adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.framework.*" %>
<%@ page import="adultadmin.bean.*" %>
<html>
<title>买卖宝后台</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<body>
<table width="20%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=center width="2%" colspan="2">某个时间前的最后一个orderID</td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center width="8%">未处理订单起始ID</td>
		<td align=center width="5%">电话失败订单起始ID</td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center><%=OrderDealUtil.orderIdStatus0 %></td>
		<td align=center><%=OrderDealUtil.orderIdStatus1 %></td>
	</tr>
</table>
</body>
</html>