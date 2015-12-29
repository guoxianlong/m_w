<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.cargo.*"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean" %>
<%@ page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>作业审核</title>
<%
	String result = (String)request.getAttribute("result");
	ProductStockBean psBean = new ProductStockBean();
	String area = (String)request.getSession().getAttribute("area");
	int areaId = StringUtil.toInt(area);
%>
<script type="text/javascript">
function checkText(){
	var operationCode = document.getElementById("operationCode").value;
	if(operationCode != "" && operationCode != "扫描汇总单编号"){
		return true;
	}else{
		alert("汇总单号不能为空!");
		document.getElementById("operationCode").focus();
		return false;
	}
}
function getFocus(){
	<%
		if(area==null){
	%>
			alert("登陆超时,请重新登录!");
			window.location='stockOperation.do?method=logout';
	<%
		}
	%>
	document.getElementById("operationCode").value = "";
	document.getElementById("operationCode").focus();
}
function clearText(){
	document.getElementById("operationCode").value="";
}
function prompt(){
	var operationCode = document.getElementById("operationCode").value;
	if(operationCode == ""){
		document.getElementById("operationCode").value ="扫描汇总单编号";
	}
}
</script>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg" onload="getFocus()" style="overflow:hidden">
<table width="220" height="220" border="0" cellspacing="0">
<tr >
	<td colspan="2" align="center"><font size="4" style="font-weight:bold">作业审核</font>
	<font color="red" size="4"><%=psBean.getAreaName(areaId) %></font></td>
</tr>
<tr ><td colspan="2" align="center">
	<%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
	<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout">[<font color="red" size="2">注销</font>]</a></td>
</tr>
<tr >
	<td colspan="2" align="center"><%=DateUtil.getNow() %></td>
</tr>
<form action="<%=request.getContextPath()%>/admin/stockOperation.do?method=operationAudit" method="post" onsubmit="return checkText();">
<% if(result != null){ %>
	<tr >
		<td colspan="2" align="center" > <textarea cols="26" rows="7" readonly="true" style="overflow-x:hidden;overflow-y:hidden"><%=result %></textarea></td>
	</tr>
<% }else{ %>
	<tr >
		<td colspan="2" align="center"> <textarea cols="26" rows="7" readonly="true" style="overflow-x:hidden;overflow-y:hidden">请扫描汇总单编号</textarea></td>
	</tr>
<% } %>
	<tr>
		<td colspan="2" align="center">
			<input type="text" size="32" id="operationCode"name="operationCode"onblur="prompt()"onfocus="clearText()"> 
		</td>
	</tr>
	<tr  align="center">
		<td><input type="button" value="完  成" style="height:26px;width:130px;" type="submit"/>
		<input type="button" value="返  回" style="height:26px;width:80px" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=stockOperation&toPage=zuoyejiaojie'"/></td>
	</tr>
</form>
</table>

</body>
</html>