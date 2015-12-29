<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.cargo.*"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page import="adultadmin.action.vo.voUser"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>选择作业区域</title>
<% 
	ProductStockBean psBean = new ProductStockBean();
	String area = (String)request.getSession().getAttribute("area"); 
	int areaId = StringUtil.toInt(area);
	String type = (String)request.getSession().getAttribute("operationType"); %>
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
	<td><font size="4" style="font-weight:bold">货位调拨</font>
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
	<td><input id="firstButton" type="button" value="散件区作业" style="height:35px;width:100px;font-weight:bolder" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=deployOperationScattered'"/></td>
</tr>
<tr align="center">
	<td><input type="button" value="整件区作业" style="height:35px;width:100px;font-weight:bolder" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=deployOperationWhole'"/></td>
</tr>
<tr align="center">
	<td><input type="button" value="混合区作业" style="height:35px;width:100px;font-weight:bolder" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=deployOperationMix'"/></td>
</tr>
<tr >
	<td align="center" colspan="2"> <input type="button" value=" 返   回 " style="height:25px;width:100px;" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=deployProduct'"/></td>
</tr>
<tr >
	<td align="center" colspan="2"> [<font size="2" style="font-weight:bold" >货位调拨→</font><font size="2" color="red"style="font-weight:bold" ><% if("KC".equals(type)) {%>跨仓作业<% }else if("BD".equals(type)){ %>本地作业<% } %></font>]</td>
</tr>
</table>
</body>
</html>