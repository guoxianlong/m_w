<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.cargo.*"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page import="adultadmin.action.vo.voUser"%><html>
<html>
<head>
<title>创建调拨单</title>
<% 
	ProductStockBean psBean = new ProductStockBean();
	String result = (String)request.getAttribute("result");
	String area = (String)request.getSession().getAttribute("area");
	int areaId = StringUtil.toInt(area);
	String type = (String)request.getSession().getAttribute("operationType"); 
	String operationArea = (String)request.getSession().getAttribute("operationArea"); 
 %>
<script type="text/javascript">
function check(){
	var cartonningCode = document.getElementById("cartonningCode").value;
	if(cartonningCode == "" || cartonningCode == "扫描装箱单编号"){
		alert("装箱单编号不能为空！");
	}else{
		document.forms[0].submit();
		setTimeout("window.location='<%=request.getContextPath()%>/admin/cargo/soCreateDeploy.jsp';",5000);
	}
}
function checkEnter(){
	if(window.event.keyCode == 13){
		var cartonningCode = document.getElementById("cartonningCode").value;
		if(cartonningCode == "" || cartonningCode == "扫描装箱单编号"){
			alert("装箱单编号不能为空！");
		}else{
			document.forms[0].submit();
		}
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
	document.getElementById("cartonningCode").value = "";
	document.getElementById("cartonningCode").focus();
}
function clearText(){
	document.getElementById("cartonningCode").value="";
}
function prompt(){
	var operationCode = document.getElementById("cartonningCode").value;
	if(operationCode == ""){
		document.getElementById("cartonningCode").value ="扫描装箱单编号";
	}
}
</script>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden" onload="getFocus()" topmargin="3">
<form action="<%=request.getContextPath()%>/admin/stockOperation.do?method=createDeploy" method="post">
<table width="220" height="220" border="0" cellspacing="0">
<tr align="center">
	<td colspan="2"><font size="4"style="font-weight:bold">创建调拨单</font>
	<font color="red" size="4"><%=psBean.getAreaName(areaId) %></font></td>
</tr>
<tr align="center">
	<td colspan="2"><%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
	[<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout"><font color="red" size="2">注销</a></font>]</td>
</tr>
<tr align="center">
	<td colspan="2"><%=DateUtil.getNow() %></td>
</tr>
<% if(result != null){ %>
	<tr height="28">
		<td colspan="2" align="center"> <textarea cols="26" rows="4" name="" readonly="true"style="overflow-x:hidden;overflow-y:hidden"><%=result %></textarea></td>
	</tr>
<% }else{ %>
	<tr height="28">
		<td colspan="2" align="center"> <textarea cols="26" rows="4" readonly="true" style="overflow-x:hidden;overflow-y:hidden">请扫描装箱单编号.</textarea></td>
	</tr>
<% } %>
	<tr height="8">
		<td colspan="2" align="center"><input type="text" size="32" id="cartonningCode" name="cartonningCode"onblur="prompt()"onfocus="clearText()" onkeypress="checkEnter()"></td>	
	</tr>


	<tr height="8" align="center">
		<td><input type="button" value="确 定"  style="height:25px;width:100px;"onclick="check()"/></td>
		<td><input type="button" value="返 回" style="height:25px;width:100px" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=operationPageJump&toPage=deployLocal'" /></td>
	</tr>
		<td colspan="2" align="center"><input type="button" value="装箱记录管理" style="height:25px;width:200px" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=stockOperation&toPage=zhuangxiangguanli'" /></td>
	</tr>
	<tr >
	<td align="center" colspan="2"> 
		[<font size="2" style="font-weight:bold" ><% if("KC".equals(type)) {%>跨仓作业<% }else if("BD".equals(type)){ %>本地作业<% } %>→</font>
		<font size="2" color="red"style="font-weight:bold" ><% if("ZJ".equals(operationArea)) {%>整件作业<% }else if("SJ".equals(operationArea)){ %>散件作业<% }else if("HH".equals(operationArea)){ %>混合作业<%} %></font>]</td>
</tr>
</form>
</table>
</form>
</body>
</html>