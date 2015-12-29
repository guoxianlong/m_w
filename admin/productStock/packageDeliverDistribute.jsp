<%@ page contentType="text/html;charset=utf-8"%>

<%@page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@page import="adultadmin.bean.stock.MailingBatchParcelBean"%>
<html>
<%
//MailingBatchBean mailingBatch=(MailingBatchBean)request.getAttribute("mailingBatch");
//MailingBatchParcelBean parcel=(MailingBatchParcelBean)request.getAttribute("parcel");
 String parcelCode=request.getParameter("parcelCode");
 String mailingBatchCode=request.getParameter("mailingBatchCode");
%>

<head>
<title>新到波次配送入库</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script type="text/javascript">
function check(){
	var mailingBatchCode=trim(document.getElementById("mailingBatchCode").value);
	var parcelCode=trim(document.getElementById("parcelCode").value);
	if(mailingBatchCode==""||mailingBatchCode=="输入包裹单号..."){
		alert("包裹单号不能为空！");
		return false;
	}
	if(parcelCode==""||parcelCode=="输入包裹重量..."){
		alert("包裹重量不能为空！");
		return false;
	}
	
	document.getElementById("mailingBatchCode2").value=mailingBatchCode;
	document.getElementById("parcelCode2").value=parcelCode;
}

function focusmailingBatchCode(){
	var mailingBatchCode=document.getElementById("mailingBatchCode");
	if(mailingBatchCode.value=="输入订单编号..."){
		mailingBatchCode.value="";
	}
}
function blurmailingBatchCode(){
	var mailingBatchCode=document.getElementById("mailingBatchCode");
	if(mailingBatchCode.value==""){
		mailingBatchCode.value="输入订单编号...";
	}
}
function focusparcelCode(){
	var parcelCode=document.getElementById("parcelCode");
	if(parcelCode.value=="输入员工编号..."){
		parcelCode.value="";
	}
}
function blurparcelCode(){
	var parcelCode=document.getElementById("parcelCode");
	if(parcelCode.value==""){
		parcelCode.value="输入员工编号...";
	}
}
function submitOrderCode(){
	document.getElementById("mailingBatchCode").focus();
	return false;
}
function submitmailingBatchCode(){
	document.getElementById("parcelCode").focus();
	return false;
}
function submitparcelCode(){
	document.getElementById("mailingBatchCode2").value=document.getElementById("mailingBatchCode").value;
	document.getElementById("parcelCode2").value=document.getElementById("parcelCode").value;
	document.getElementById("addPackage").submit();
	return false;
}
</script>
</head>
<body onLoad="document.getElementById('mailingBatchCode').focus();">

<table border="0" style="border: solid thin black" cellspacing="0"
		bgcolor="#FFCC00" >
	
	<tr>
		<td>订单编号：</td>
		<td style="padding-top: 4mm">
			<form action="" onSubmit="return submitmailingBatchCode();">
				<input id="mailingBatchCode" type="text" value="输入订单编号..." onFocus="focusmailingBatchCode();" onBlur="blurmailingBatchCode();"/>
			</form>
		</td>
	</tr>
	<tr>
		<td>员工编号：</td>
		<td style="padding-top: 4mm">
			<form action="" onSubmit="return submitparcelCode();">
				<input id="parcelCode" type="text" value="输入员工编号..." onFocus="focusparcelCode();" onBlur="blurparcelCode();"/>
			</form>		
		</td>
	</tr>
	<tr>
		<td style="padding-top: 5mm">
			<form name="addPackage" action="<%=request.getContextPath()%>/admin/mailingBatch.do?method=packageDeliverDistribute" method="post">
			<input type="hidden" id="mailingBatchCode2" name="mailingBatchCode" value=""/>
			<input type="hidden" id="parcelCode2" name="parcelCode" value=""/>
			<input type="submit" value="确定分配" onClick="return check();"/>
			</form>
		</td>
		<td><input type="button" value="取消" onClick="javascript:window.close();"/></td>
	</tr>
</table>
</body>
</html>