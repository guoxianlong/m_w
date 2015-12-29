<%@ page contentType="text/html;charset=utf-8"%>

<%@page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@page import="adultadmin.bean.stock.MailingBatchParcelBean"%>
<html>
<%
MailingBatchBean mailingBatch=(MailingBatchBean)request.getAttribute("mailingBatch");
MailingBatchParcelBean parcel=(MailingBatchParcelBean)request.getAttribute("parcel");
String orderCode=request.getParameter("orderCode");
if(orderCode!=null){%>
	<script type="text/javascript">
	window.opener.updateMailingBatchOrderCount();
	window.opener.updateMailingBatchTotalWeight();
	window.opener.updateMailingBatchParcelOrderCount(<%=parcel.getId()%>);
	window.opener.updateMailingBatchParcelTotalWeight(<%=parcel.getId()%>);
	window.opener.updateMailingBatchParcelTotalPrice(<%=parcel.getId()%>);
	window.opener.updateMailingBatchPackageList(<%=parcel.getId()%>);
	</script>
<%}%>

<head>
<title>添加包裹</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script type="text/javascript">
function check(){
	var orderCode=trim(document.getElementById("orderCode").value);
	var packageCode=trim(document.getElementById("packageCode").value);
	if(orderCode==""||orderCode=="输入订单编号..."){
		alert("订单号不能为空！");
		return false;
	}
	if(packageCode==""||packageCode=="输入包裹单号..."){
		alert("包裹单号不能为空！");
		return false;
	}
	document.getElementById("orderCode2").value=document.getElementById("orderCode").value;
	document.getElementById("packageCode2").value=document.getElementById("packageCode").value;
	return true;
}
function focusOrderCode(){
	var orderCode=document.getElementById("orderCode");
	if(orderCode.value=="输入订单编号..."){
		orderCode.value="";
	}
}
function blurOrderCode(){
	var orderCode=document.getElementById("orderCode");
	if(orderCode.value==""){
		orderCode.value="输入订单编号...";
	}
}
function focusPackageCode(){
	var packageCode=document.getElementById("packageCode");
	if(packageCode.value=="输入包裹单号..."){
		packageCode.value="";
	}
}
function blurPackageCode(){
	var packageCode=document.getElementById("packageCode");
	if(packageCode.value==""){
		packageCode.value="输入包裹单号...";
	}
}

function submitOrderCode(){
	document.getElementById("packageCode").focus();
	return false;
}

function submitPackageCode(){
	document.getElementById("orderCode2").value=document.getElementById("orderCode").value;
	document.getElementById("packageCode2").value=document.getElementById("packageCode").value;
	if(check()==true){
		document.getElementById("addPackage").submit();
	}
	return false;
}
</script>
</head>
<body onload="document.getElementById('orderCode').focus();">

<table border="0" style="border: solid thin black" cellspacing="0"
		bgcolor="#FFCC00" >
	<tr>
		<td>&nbsp;&nbsp;订单编号：</td>
		<td style="padding-top: 4mm">
			<form action="" onsubmit="return submitOrderCode();">
				<input id="orderCode" type="text" value="输入订单编号..." onfocus="focusOrderCode();" onblur="blurOrderCode();"/>
			</form>
		</td>
	</tr>
	<tr>
		<td>&nbsp;&nbsp;包裹单号：</td>
		<td style="padding-top: 4mm">
			<form action="" onsubmit="return submitPackageCode();">
				<input id="packageCode" type="text" value="输入包裹单号..." onfocus="focusPackageCode();" onblur="blurPackageCode();"/>
			</form>
		</td>
	</tr>
	<tr>
		<td style="padding-top: 1mm" colspan="2" align="center">
			<form name="addPackage" id="addPackage" action="mailingBatch.do?method=addMailingBatchPackage&mailingBatchId=<%=mailingBatch.getId() %>&parcelId=<%=parcel.getId() %>" method="post">
			<input type="hidden" id="orderCode2" name="orderCode" value=""/>
			<input type="hidden" id="packageCode2" name="packageCode" value=""/>
			<input type="hidden" name="mailingBatchId" value="<%=mailingBatch.getId() %>"/>
			<input type="hidden" name="parcelId" value="<%=parcel.getId() %>"/>
			<input type="submit" value="添加" onclick="return check();"/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="button" value="取消" onclick="javascript:window.close();"/>
			</form>
		</td>
	</tr>
</table>
</body>
</html>