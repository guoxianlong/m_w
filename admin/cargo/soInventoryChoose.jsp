<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.cargo.*"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean" %>
<%@ page import="adultadmin.action.vo.voUser"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>异常或为盘点选择盘点阶段</title>
<% 
	ProductStockBean psBean = new ProductStockBean();
	String area = (String)request.getSession().getAttribute("area"); 
	int areaId = StringUtil.toInt(area);	
	String error = (String)request.getAttribute("error");
	voUser user = (voUser)request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
%>
<script type="text/javascript">
<%if(error != null){%>
alert("<%=error%>");
<%}%>
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
	<td><font size="4" style="font-weight:bold">异常或为盘点选择盘点阶段</font>
	<font color="red" size="4"><%=psBean.getAreaName(areaId) %></font></td>
</tr>
<tr align="center">
	<td><%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
	[<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout"><font color="red" size="2">注销</a></font>]</td>
</tr>
<tr align="center">
	<td><%=DateUtil.getNow() %></td>
</tr>
<%if(group.isFlag(778)){ %>
<tr align="center">
	<td><input id="firstButton" type="button" value="一盘" style="height=35px;width=100px;font-weight:bolder" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=inventoryAbnormalCargo&inventoryFlag=1'"/></td>
</tr>
<%} 
if(group.isFlag(779)){ %>
<tr align="center">
	<td><input type="button" value="二盘" style="height=35px;width=100px;font-weight:bolder" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=inventoryAbnormalCargo&inventoryFlag=2'"/></td>
</tr>
<%} 
if(group.isFlag(780)){ %>
<tr align="center">
	<td><input type="button" value="终盘" style="height=35px;width=100px;font-weight:bolder" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=inventoryAbnormalCargo&inventoryFlag=3'"/></td>
</tr>
<%} %>
<tr >
	<td align="center" colspan="2"> <input type="button" value=" 返   回 " style="height=25px;width=100px;" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=stockOperation&toPage=huoweiyichang'"/></td>
</tr>
</table>
</body>
</html>