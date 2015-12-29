<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.cargo.*"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean" %>
<%@ page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>订单出库</title>
<% 
	String result = (String)request.getAttribute("result");
	String area = (String)request.getSession().getAttribute("area");
	int areaId = StringUtil.toInt(area);
	ProductStockBean psBean = new ProductStockBean();
 %>
<script type="text/javascript">
function checkButton(){
	var batchCode = document.getElementById("batchCode").value;
	if(batchCode != "" && batchCode != "扫描或手工输入发货波次号"){
		document.forms[0].submit();
		setTimeout("window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=stockOperation&toPage=dingdanchuku';",5000);
	}else{
		alert("发货波次号不能为空！");
		document.getElementById("batchCode").focus();
	}
}
function checkEnter(){
	if(window.event.keyCode == 13){
		var batchCode = document.getElementById("batchCode").value;
		if(batchCode != "" && batchCode != "扫描或手工输入发货波次号"){
			document.forms[0].submit();
		}else{
			alert("发货波次号不能为空！");	
			document.getElementById("batchCode").focus();
			return false;
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
	document.getElementById("batchCode").value = "";
	document.getElementById("batchCode").focus();
}
function prompt(){
	var batchCode = document.getElementById("batchCode").value;
	if(batchCode == ""){
		document.getElementById("batchCode").value ="扫描或手工输入发货波次号";
	}
}
function clearText(){
	document.getElementById("batchCode").value = "";
}
</script>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden" onload="getFocus()" topmargin="11">
<table width="220" height="220" border="0" cellspacing="0">
<tr height="4">
	<td colspan="2" align="center"><font size="4"style="font-weight:bold">订单出库</font>
	<font color="red" size="4"><%=psBean.getAreaName(areaId) %></font></td>
</tr>
<tr height="4">
	<td colspan="2" align="center"><%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
	<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout">[<font color="red" size="2">注销</font>]</a></td>
</tr>
<tr height="4">
	<td colspan="2" align="center"><%=DateUtil.getNow() %></td>
</tr>

<form action="<%=request.getContextPath()%>/admin/stockOperation.do?method=batchParcelList" method="post">
<% if(result != null){ %>
	<tr height="28">
		<td colspan="2" align="center"> <textarea cols="26" rows="7" name="" readonly="true"style="overflow-x:hidden;overflow-y:hidden"><%=result %></textarea></td>
	</tr>
<% }else{ %>
	<tr height="28">
		<td colspan="2" align="center"> <textarea cols="26" rows="7" name="" readonly="true" style="overflow-x:hidden;overflow-y:hidden">扫描或手工输入发货波次号.</textarea></td>
	</tr>
<% } %>
	<tr height="8">
		<td colspan="2" align="center"><input type="text" size="32" id="batchCode" name="batchCode"onblur="prompt()" onfocus="clearText()" onkeypress="javascript:return checkEnter()"></td>	
	</tr>


	<tr height="8" align="center">
		<td><input type="button" value="确  定"  style="height:26px;width:100px;"onclick="checkButton()"/>
		<input type="button" value="返   回" style="height:26px;width:100px" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=pdaOperation';"/></td>
	</tr>
</form>
</table>
</body>
</html>