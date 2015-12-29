<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String[][] statArray=(String[][])request.getAttribute("statArray");
int year=Integer.parseInt(request.getAttribute("year").toString());
int month=Integer.parseInt(request.getAttribute("month").toString());
int thisYear=Integer.parseInt(request.getAttribute("thisYear").toString());
int dayCount=Integer.parseInt(request.getAttribute("dayCount").toString());
String formArea=request.getParameter("area");
int fa = StringUtil.toInt(formArea);
String wareAreaLable = ProductWarePropertyService.getStockoutWeraAreaCustomized("area", "", fa, true,"");
%>

<%@page import="java.util.List"%>
<%@page import="mmb.stock.cargo.CargoDeptAreaService"%>
<%@page import="adultadmin.bean.stock.ProductStockBean,mmb.stock.stat.*,adultadmin.util.StringUtil"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>每日发货复核量统计</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<script type="text/javascript">
function formSubmit(isExport){
	if(isExport==1){
		document.getElementById("isExport").value="1";
	}else{
		document.getElementById("isExport").value="0";
	}
	document.forms[0].submit();
}
</script>
</head>
<body>
&nbsp;&nbsp;&nbsp;每日复核量统计
<form action="checkOrderStat.do?method=checkOrderStatByDate" method="post">
复核月份：
<select id="year" name="year">
	<option value="<%=thisYear-1 %>"><%=thisYear-1 %></option>
	<option value="<%=thisYear %>"><%=thisYear %></option>
</select>年
<select id="month" name="month">
	<%for(int i=1;i<=12;i++){ %>
		<option value="<%=i %>"><%=i %></option>
	<%} %>
</select>月&nbsp;&nbsp;&nbsp;
库地区：<%= wareAreaLable%>

<input type="button" onclick="formSubmit(0);" value="查询"/>
<input type="button" onclick="formSubmit(1);" value="导出excel文件"/>
<input type="hidden" id="isExport" name="isExport" value="0"/>
</form>
<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5">
<%for(int i=0;i<statArray.length;i++){ %>
<tr align="center" <%if(i==0||i==1){ %>bgcolor="#4688D6"<%} %>>
	<%for(int j=0;j<statArray[0].length;j++){ %>
		<%if(i==0){ %>
			<td <%if(j>0){ %>colspan="2"<%} %>><%if(j==(statArray[0].length-2)){ %><font color="#FFFFFF"><%=statArray[i][j]==null?"":statArray[i][j] %></font><%}else{ %><a href="checkOrderStat.do?method=checkOrderStatByName&userName=<%=statArray[i][j]==null?"":statArray[i][j] %>&dateStart=<%=year %>-<%=month<10?"0"+month:month %>-01&dateEnd=<%=year %>-<%=month<10?"0"+month:month %>-<%=dayCount<10?"0"+dayCount:dayCount %>" target="_blank" ><font color="#FFFFFF"><%=statArray[i][j]==null?"":statArray[i][j] %></font></a><%} %></td>
			<%if(j>0){j++;} %>
		<%}else if(i==1){ %>
			<td bgcolor="#4688D6"><font color="#FFFFFF"><%=statArray[i][j]==null?"0":statArray[i][j] %></font></td>
		<%}else{ %>
			<td <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>><%if(i!=statArray.length-1&&j==0){%><a href="checkOrderStat.do?method=checkOrderStatByHour&date=<%=statArray[i][j]==null?"0":statArray[i][j] %>" target="_blank"><%=statArray[i][j]==null?"0":statArray[i][j] %></a><%}else{%><%=statArray[i][j]==null?"0":statArray[i][j] %><%} %></td>
		<%} %>
	<%} %>
</tr>
<%} %>
</table>
<script type="text/javascript">
selectOption(document.getElementById("year"),"<%=year%>");
selectOption(document.getElementById("month"),"<%=month%>");
</script>
</body>
</html>