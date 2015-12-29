<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.bean.stock.ProductStockBean" %>
<%@page import="adultadmin.util.*"%><html>
<head>
<title>仓内作业管理</title>
<% String area = (String)request.getSession().getAttribute("area");
	int areaId = StringUtil.toInt(area);
	ProductStockBean psBean = new ProductStockBean();
%>
<script type="text/javascript">
function getFocus(){
	document.getElementById("firstButton").focus();
	<%
		if(area==null){
	%>
			alert("登陆超时,请重新登录!");
			window.location='stockOperation.do?method=logout';
	<%
		}
	%>
}
</script>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden" onload="getFocus()">
 <table width="220" height="220" border="0" cellspacing="0">
<tr align="center">
	<td><font size="4" style="font-weight:bold">仓内作业</font>
	<font color="red" size="4"><%=psBean.getAreaName(areaId) %></font></td>
</tr>
<tr align="center">
	<td>
	<%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
	[<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout"><font color="red" size="2">注销</a></font>]</td>
</tr>
<tr align="center">
	<td><%=DateUtil.getNow() %></td>
</tr>
<tr align="center"><td><input id="firstButton" type="button" value="上架作业" style="height=35px;width=130px;font-weight:bolder" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=upProduct'"/></td></tr>
<tr align="center"><td><input type="button" value="下架作业" style="height=35px;width=130px;font-weight:bolder" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=downProduct'"/></td></tr>
<tr align="center"><td><input type="button" value="补货作业" style="height=35px;width=130px;font-weight:bolder" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=addProduct'"/></td></tr>
<tr align="center"><td><input type="button" value="货位间调拨" style="height=35px;width=130px;font-weight:bolder" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=deployProduct'"/></td></tr>
<tr align="center"><td><input type="button" name="button" value="返 回" style="height=28px;width=130px;" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=pdaOperation';" /></td></tr>
</table>
</body>
</html>