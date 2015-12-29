<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String[][] statArray=(String[][])request.getAttribute("statArray");
String date=request.getAttribute("date").toString();
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
<title>每小时发货贴单量统计</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<script type="text/javascript">
function checkDate(){
	var date=trim(document.getElementById("date").value);
	if(date==""){
		alert('请输入贴单日期！');
		return false;
	}
	var re = /^((((((0[48])|([13579][26])|([2468][048]))00)|([0-9][0-9]((0[48])|([13579][26])|([2468][048]))))-02-29)|(((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-((((0[13578])|(1[02]))-31)|(((0[1,3-9])|(1[0-2]))-(29|30))|(((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-8]))))))$/i;
    if (!re.test(date)){
         alert('贴单日期，请输入正确的格式！如：2011-10-09');
         return false;
    }
	return true;
}
</script>
</head>
<body>
&nbsp;&nbsp;&nbsp;每小时贴单量统计
<form action="auditPackageStat.do?method=auditPackageStatByHour" method="post" onsubmit="return checkDate();">
贴单日期：
<input type="text" id="date" name="date" value="<%=date %>" size="10" onclick="SelectDate(this,'yyyy-MM-dd');"/>
&nbsp;&nbsp;&nbsp;
库地区：<%= wareAreaLable%>
<input type="submit" value="查询">
</form>
<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5">
<%for(int i=0;i<statArray.length;i++){ %>
<tr align="center" <%if(i==0){ %>bgcolor="#4688D6"<%} %>>
	<%for(int j=0;j<statArray[0].length;j++){ %>
		<%if(i==0){ %>
			<td align="center"><font color="#FFFFFF"><%=statArray[i][j]==null?"":statArray[i][j] %></font></td>
		<%}else if(i==statArray.length-1&&j==0){ %>
			<td colspan="2" <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>><%=statArray[i][j]==null?"0":statArray[i][j] %></td>
			<%j++; %>
		<%}else{ %>
			<td <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>><%=statArray[i][j]==null?"0":statArray[i][j] %></td>
		<%} %>
	<%} %>
</tr>
<%} %>
</table>
</body>
</html>