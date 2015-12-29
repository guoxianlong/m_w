<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.cargo.*"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean" %>
<%@ page import="adultadmin.action.vo.voUser"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>货位异常处理</title>
<% 
	ProductStockBean psBean = new ProductStockBean();
	String area = (String)request.getSession().getAttribute("area"); 
	int areaId = StringUtil.toInt(area);	
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
	<td><font size="4" style="font-weight:bold">货位异常处理</font>
	<font color="red" size="4"><%=psBean.getAreaName(areaId) %></font></td>
</tr>
<tr align="center">
	<td><%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
	[<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout"><font color="red" size="2">注销</a></font>]</td>
</tr>
<tr align="center">
	<td><%=DateUtil.getNow() %></td>
</tr>
<tr align="center">
	<td><input id="firstButton" type="button" value="异常订单处理" style="height=35px;width=100px;font-weight:bolder" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=dealDeleteOS&flag=forDelete'"/></td>
</tr>
<!-- 
	<tr align="center">
		<td><input type="button" value="再次分拣" style="height=35px;width=100px;font-weight:bolder" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=sortingAgain'"/></td>
	</tr>
 -->
<tr align="center">
	<td><input type="button" value="异常商品处理" style="height=35px;width=100px;font-weight:bolder" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=abnormalProductHadle'"/></td>
</tr>
<tr align="center">
	<td><input type="button" value="货位异常盘点" style="height=35px;width=100px;font-weight:bolder" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=inventoryChoose'"/></td>
</tr>
<tr >
	<td align="center" colspan="2"> <input type="button" value=" 返   回 " style="height=25px;width=100px;" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=pdaOperation'"/></td>
</tr>
</table>
</body>
</html>