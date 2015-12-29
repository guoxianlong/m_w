<%@ page import="java.util.*" %>
<%@page import="adultadmin.util.DateUtil"%>
<%@ include file="../taglibs.jsp"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>申请出库订单查询</title>
</head>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<fieldset style="width:34%;">
<legend>申请出库订单查询</legend>
<form method=post action="searchorderstockamount.do">
<%
String defaultDate=DateUtil.getNow().substring(0,10);
String startDate = (String)request.getParameter("startDate");
if(startDate == null)startDate="";
String endDate = (String)request.getParameter("endDate");
if(endDate == null)endDate="";

List halfHourList=(List)request.getAttribute("orderStockList");
List halfHourListAmount=(List)request.getAttribute("orderstockamount");

Calendar time = Calendar.getInstance();
time.set(Calendar.HOUR_OF_DAY,8);
time.set(Calendar.MINUTE,0);
%>
开始时间：<input type=text name="startDate" id="startDate" size="10" value="<%=startDate==""?defaultDate:startDate %>" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd');">
<select name="startTime" id="startTime">
<%
while(time.get(Calendar.HOUR_OF_DAY)>0||(time.get(Calendar.HOUR_OF_DAY)==0&&time.get(Calendar.MINUTE)!=30)){
 %>
 	<%if(request.getParameter("startTime")!=null&&request.getParameter("startTime").equals(DateUtil.formatDate(time.getTime(),"HH:mm"))){ %>
 	<option value="<%=DateUtil.formatDate(time.getTime(),"HH:mm")%>" selected=true><%=DateUtil.formatDate(time.getTime(),"HH:mm")%></option>
 	<%}else{ %>
 		<option value="<%=DateUtil.formatDate(time.getTime(),"HH:mm")%>"><%=DateUtil.formatDate(time.getTime(),"HH:mm")%></option>
 	<%} %>
<%
	time.add(Calendar.MINUTE,30);
}
 %>
</select>
<br>
截止时间：<input type=text name="endDate" id="endDate" size="10" value="<%=endDate==""?defaultDate:endDate%>" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd');">
<select name="endTime" id="endTime">
<%
time.set(Calendar.HOUR_OF_DAY,8);
time.set(Calendar.MINUTE,0);
while(time.get(Calendar.HOUR_OF_DAY)>0||(time.get(Calendar.HOUR_OF_DAY)==0&&time.get(Calendar.MINUTE)!=30)){
 %>
 	<%if(request.getParameter("endTime")!=null&&request.getParameter("endTime").equals(DateUtil.formatDate(time.getTime(),"HH:mm"))){%>
 	<option value="<%=DateUtil.formatDate(time.getTime(),"HH:mm")%>" selected=true><%=DateUtil.formatDate(time.getTime(),"HH:mm")%></option>
	<%}else{ %>
	<option value="<%=DateUtil.formatDate(time.getTime(),"HH:mm")%>"><%=DateUtil.formatDate(time.getTime(),"HH:mm")%></option>
	<%} %>
<%
	time.add(Calendar.MINUTE,30);
}
 %>
</select>
<br>
<input type=submit value="查询">
</form>
</fieldset><br/>
<form action="">
<%if(request.getAttribute("orderStockList")!=null){ %>
<b><%=request.getParameter("startDate")+" "+request.getParameter("startTime") %>
到<%=request.getParameter("endDate")+" "+request.getParameter("endTime")  %>的查询结果：</b><br/>
<table border="1" style="border-collapse:collapse;" bordercolor="#D8D8D5">
<tr align=center bgcolor=#4688D6>
<td><font color="#FFFFFF">时间范围</font></td>
<td><font color="#FFFFFF">申请出库订单数量</font></td>
</tr>

<%
if(request.getAttribute("orderStockList")!=null){
	for(int i=0;i<halfHourList.size();i++){
		String[] date=(String[])halfHourList.get(i);
		int amount=Integer.parseInt(halfHourListAmount.get(i).toString());%>
		<tr bgcolor=<%=i%2==0?"#eee9d9":"#ffffff" %>>
		<td><%=date[0] %>至<%=date[1] %></td><td align=right><%=amount %></td>
		</tr>
	<%}
}}%>
</table>
</form>
</body>
</html>