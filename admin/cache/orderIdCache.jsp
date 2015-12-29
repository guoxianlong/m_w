<%@ page contentType="text/html;charset=utf-8" %>
<%@ include file="../../taglibs.jsp"%>
<%@page import="java.sql.*,java.util.*"%>
<%@ page import="adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.service.*" %>
<%@ page import="adultadmin.bean.*" %>
<%
    try {
%>
<html>
<title>买卖宝后台</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<body>
<%@include file="../../header.jsp"%>
<table width="20%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=center width="2%" colspan="2">某个时间前的最后一个orderID</td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center width="8%">缓存日期</td>
		<td align=center width="5%">订单ID</td>
	</tr>

<%
    		Map dayOrderIdMap = StatUtil.dayOrderIdMap;
			Iterator iter = dayOrderIdMap.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry entry = (Map.Entry)iter.next();
				String date = (String)entry.getKey();
				Integer id = (Integer)entry.getValue();    			
%>
			<tr bgcolor='#F8F8F8'>
				<td align=center><%=date %></td>
				<td align=center><%=id %></td>
			</tr>
<%			}
%>
</table>
<br/>
<br/>
<br/>
<table width="20%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=center width="2%" colspan="2">某个时间点的第一个orderID</td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center width="8%">缓存日期</td>
		<td align=center width="5%">订单ID</td>
	</tr>

<%
    		Map dayOrderIdMap2 = StatUtil.dayOrderIdMap2;
			Iterator iter2 = dayOrderIdMap2.entrySet().iterator();
			while(iter2.hasNext()){
				Map.Entry entry = (Map.Entry)iter2.next();
				String date = (String)entry.getKey();
				Integer id = (Integer)entry.getValue();    			
%>
			<tr bgcolor='#F8F8F8'>
				<td align=center><%=date %></td>
				<td align=center><%=id %></td>
			</tr>
<%		   }
%>
</table>
<br/>
<br/>
<table width="20%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=center width="2%" colspan="2">获取某个时间点以后的订单起始ID</td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center width="8%">缓存日期</td>
		<td align=center width="5%">订单ID</td>
	</tr>

<%
    		Map dateTimeOrderIdMap = StatUtil.dateTimeOrderIdMap;
			Iterator iter3 = dateTimeOrderIdMap.entrySet().iterator();
			while(iter3.hasNext()){
				Map.Entry entry = (Map.Entry)iter3.next();
				String date = (String)entry.getKey();
				Integer id = (Integer)entry.getValue();    			
%>
			<tr bgcolor='#F8F8F8'>
				<td align=center><%=date %></td>
				<td align=center><%=id %></td>
			</tr>
<%		   }
%>
</table>
<br/>
<center>
<a href="clearOrderIdCache.jsp" target="_blank">清空订单ID缓存</a>
</center>
<%
	} catch (Exception e) {
		e.printStackTrace();
	}
%>
<br/>
<br/>
</body>

