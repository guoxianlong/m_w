<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="adultadmin.util.*"%>
<%@page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@page import="adultadmin.bean.stock.MailingBatchParcelBean"%>
<html>
<%
	//MailingBatchBean mailingBatch=(MailingBatchBean)request.getAttribute("mailingBatch");
	//MailingBatchParcelBean parcel=(MailingBatchParcelBean)request.getAttribute("parcel");
	String orderCode = request.getParameter("orderCode");
	String parcelCode = (String) request.getAttribute("parcelCode");
	String mailingBatchCode = (String) request.getAttribute("mailingBatchCode");
%>

<head>
<title>新到包裹入库</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script type="text/javascript">


function focusparcelCode(){
	var orderCode=document.getElementById("orderCode");
	if(orderCode.value=="输入发货邮包编号..."){
		orderCode.value="";
	}
}
function blurparcelCode(){
	var orderCode=document.getElementById("orderCode");
	if(orderCode.value==""){
		orderCode.value="输入发货邮包编号...";
	}
}
function submitOrderCode(){
	document.getElementById("mailingBatchCode").focus();
	return false;
}
function submitmailingBatchCode(){
	document.getElementById("orderCode").focus();
	return false;
}
function submitparcelCode(){
	document.getElementById("addPackage").submit();
	return false;
}
</script>
</head>
<body onLoad="document.getElementById('orderCode').focus();">
	<form name="addPackage"
		action="<%=request.getContextPath()%>/admin/mailingBatch.do?method=packageStockIn&parcelCode=<%=parcelCode%>&mailingBatchCode=<%=mailingBatchCode%>"
		method="post">
		<table border="0" style="border: solid thin black" cellspacing="0"
			bgcolor="#FFCC00">
			<tr>
				<td><br>订单编号：
			    <input id="orderCode"
					name="orderCode" type="text" 
					value="输入发货邮包编号..." onFocus="focusparcelCode();"
					onBlur="blurparcelCode();" /></td>
			</tr>
			<tr>
				<td style="padding-top: 5mm"><div align="center">
				  <input name="submit" type="submit"
					onClick="return check();" value="添加" />				  
				  <input type="button" value="取消"
					onClick="javascript:window.close();" />
			    </div></td>
			</tr>
		</table>
	</form>
</body>
</html>