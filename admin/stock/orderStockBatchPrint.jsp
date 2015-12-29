<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.*"%>
<%@ page import="adultadmin.bean.order.*"%>
<html>
<head>
<title>按批次打印发货清单</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<script type="text/javascript">
function checkDate(){
	var date = document.getElementById("date").value;
	if(date==""){
		alert('请输入打印日期！');
		return false;
	}
	if(date != ""){
		var re = /^((((((0[48])|([13579][26])|([2468][048]))00)|([0-9][0-9]((0[48])|([13579][26])|([2468][048]))))-02-29)|(((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-((((0[13578])|(1[02]))-31)|(((0[1,3-9])|(1[0-2]))-(29|30))|(((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-8]))))))$/i;
    	if (!re.test(date)){
        	alert('打印日期，请输入正确的格式！如：2011-08-10');
        	return false;
     	}
	}
    return true;
}
</script>
<%
List batchList=(List)request.getAttribute("batchList");
List oriCountList=(List)request.getAttribute("oriCountList");
//List printCountList=(List)request.getAttribute("printCountList");
List currentCountList=(List)request.getAttribute("currentCountList");
String date=request.getParameter("date");
%>

</head>
<body>
<div>按批次打印发货单</div>
<form action="orderStockBatchPrint.do" method="post">
	打印日期：<input type="text" id="date" name="date" size="10" value="<%=date==null?DateUtil.getNowDateStr():date %>" onclick="SelectDate(this,'yyyy-MM-dd');" />
	<input type="submit" value="查询" onclick="return checkDate();"/>
</form>
<%if(batchList!=null){ %>
<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5">
	<tr bgcolor="#4688D6">
		<td><font color="#FFFFFF">打印批次</font></td>
		<td><font color="#FFFFFF">打印时间</font></td>
		<td><font color="#FFFFFF">操作人</font></td>
		<td><font color="#FFFFFF">打印时订单数</font></td>
		<td><font color="#FFFFFF">当前实际订单数</font></td>
		<td><font color="#FFFFFF">操作</font></td>
	</tr>
	<%
	for(int i=0;i<batchList.size();i++){
		OrderStockPrintLogBean bean=(OrderStockPrintLogBean)batchList.get(i);
		String currentCount=currentCountList.get(i).toString();
		String oriCount=oriCountList.get(i).toString();
	%>
	<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
		<td><%=bean.getBatch() %></td>
		<td><%=bean.getTime().substring(11,19) %></td>
		<td><%=bean.getUserName() %></td>
		<td><%=oriCount %></td>
		<td><%=currentCount %></td>
		<%if(currentCount.equals("0")){ %>
		<td>
			<input type="button" value="查询" disabled="disabled"/>
			<input type="button" value="导单" disabled="disabled"/>
			<input type="button" value="打印" disabled="disabled"/>
		</td>
		<%}else{ %>
		<td>
			<input type="button" value="查询" onclick="javascript:window.location='orderStockExportPrint.do?batch=<%=bean.getBatch()%>&date=<%=date %>&flag=3'"/>
			<input type="button" value="导单" onclick="javascript:window.location='orderStockExportPrint.do?batch=<%=bean.getBatch()%>&date=<%=date %>&flag=1&printType=0&areano=0'"/>
			<input type="button" value="打印" onclick="javascript:window.open('orderStockExportPrint.do?batch=<%=bean.getBatch()%>&date=<%=date %>&flag=2&printType=1&areano=0&stockState=1')">
		</td>
		<%} %>
	</tr>
	<%} %>
</table>
<%} %>
</body>
</html>