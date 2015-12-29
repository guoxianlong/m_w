<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<html>
<head>
<title>货位调拨确认扫描页</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script type="text/javascript">
function focusExchangeCode(){
	var exchangeCode=trim(document.getElementById("exchangeCode").value);
	if(exchangeCode=="调拨单条码"){
		document.getElementById("exchangeCode").value="";
		document.getElementById("exchangeCode").style.color="#000000";
	}
}
function blurExchangeCode(){
	var exchangeCode=trim(document.getElementById("exchangeCode").value);
	if(exchangeCode==""){
		document.getElementById("exchangeCode").value="调拨单条码";
		document.getElementById("exchangeCode").style.color="#cccccc";
	}
}
function focusProductCode(){
	var productCode=trim(document.getElementById("productCode").value);
	if(productCode=="商品条码"){
		document.getElementById("productCode").value="";
		document.getElementById("productCode").style.color="#000000";
	}
}
function blurProductCode(){
	var productCode=trim(document.getElementById("productCode").value);
	if(productCode==""){
		document.getElementById("productCode").value="商品条码";
		document.getElementById("productCode").style.color="#cccccc";
	}
}
function check(){
	document.getElementById("exchangeCode2").value=document.getElementById("exchangeCode").value;
	var exchangeCode=trim(document.getElementById("exchangeCode2").value);
	var productCode=trim(document.getElementById("productCode").value);
	if(exchangeCode==""||exchangeCode=="调拨单条码"){
		alert("必须输入调拨单条码！");
		return false;
	}
	if(productCode==""||productCode=="商品条码"){
		alert("必须输入商品条码！");
		return false;
	}
	return true;
}
function submitExchangeCode(){
	document.getElementById("exchangeCode2").value=document.getElementById("exchangeCode").value;
	document.getElementById("productCode").focus();
	return false;
}
function resetForm(){
	document.getElementById("exchangeCode").value="";
	document.getElementById("exchangeCode2").value="";
	document.getElementById("productCode").value="";
	document.getElementById("exchangeCode").focus();
}
</script>
</head>
<body onload="document.getElementById('exchangeCode').focus();">
<h2>货位调拨确认扫描页</h2>
<%if(request.getAttribute("tip2")!=null){%>
	<div style="padding-left:4mm;"><font color="red"><%=request.getAttribute("tip2") %></font></div>
<%}%>
<%if(request.getAttribute("tip")!=null){ %>
	<script type="text/javascript">alert('<%=request.getAttribute("tip").toString()%>');</script>
<%} %>
<form action="" method="post" onsubmit="return submitExchangeCode();">
<table>
	<tr>
		<td align="right">调拨单条码：</td>
		<td><input type="text" id="exchangeCode" name="exchangeCode" value="调拨单条码" onfocus="focusExchangeCode();" onblur="blurExchangeCode();" style="color: #cccccc;"/></td>
	</tr>
</table>
</form>
<form action="cargoOperation.do?method=exchangeOperationConfirm" method="post" onsubmit="return check();">
<input type="hidden" id="exchangeCode2" name="exchangeCode" value=""/>
<table>
	<tr>
		<td align="right">&nbsp;&nbsp;&nbsp;商品条码：</td>
		<td><input type="text" id="productCode" name="productCode" value="商品条码" onfocus="focusProductCode();" onblur="blurProductCode();" style="color: #cccccc;"/></td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="submit" value="扫描确认"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="button" value="  取消  " onclick="resetForm();"/>
		</td>
	</tr>
</table>
</form>
</body>
</html>