<%@ page contentType="text/html;charset=utf-8"%>

<%@page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@page import="adultadmin.bean.stock.MailingBatchParcelBean"%>
<%@page import="adultadmin.bean.stock.MailingBatchPackageBean"%><html>
<head>
<title>包裹重量修正</title>
<%
MailingBatchBean mailingBatch=(MailingBatchBean)request.getAttribute("mailingBatch");
MailingBatchParcelBean parcel=(MailingBatchParcelBean)request.getAttribute("parcel");
MailingBatchPackageBean packageBean=(MailingBatchPackageBean)request.getAttribute("package");
String weight=request.getParameter("weight");
if(weight!=null){%>
<script type="text/javascript">
	window.opener.updateMailingBatchTotalWeight();
	window.opener.updateMailingBatchParcelTotalWeight(<%=parcel.getId()%>);
	window.opener.updateMailingBatchPackageList(<%=parcel.getId()%>);
	window.close();
</script>
<%}%>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script type="text/javascript">
function check(){
	var weight=trim(document.getElementById("weight").value);
	if(weight==""||weight==0||weight=="输入包裹重量..."){
		alert("包裹重量不能为空！");
		return false;
	}
	if(weight!="输入包裹重量..."){
		if(weight>120){
			alert("重量过大！");
			return false;
		}
		var reg=/^[0-9]{1,3}(\.[0-9]{1,2}){0,1}$/;
		if(reg.exec(weight)==null){
			alert("包裹重量输入错误！");
			return false;
		}
	}
	return true;
}
function focusWeight(){
	var weight=document.getElementById("weight");
	if(weight.value=="输入包裹重量..."){
		weight.value="";
	}
}
function blurWeight(){
	var weight=document.getElementById("weight");
	if(weight.value==""){
		weight.value="输入包裹重量...";
	}
}
</script>
</head>
<body>
<form action="mailingBatch.do?method=changePackageWeight&packageId=<%=packageBean.getId() %>" method="post" onsubmit="return check();">
<table border="3" style="border: solid thin black" cellspacing="0"
		bgcolor="#FFCC00" bordercolor="#FFCC00">
	<tr>
		<td>订单编号：</td>
		<td><%=packageBean.getOrderCode() %></td>
	</tr>
	<tr>
		<td>包裹重量：</td>
		<td><input type="text" id="weight" name="weight" value="输入包裹重量..." onfocus="focusWeight();" onblur="blurWeight();"/>kg</td>
	</tr>
	<tr>
		<td><input type="submit" value="修改" onclick="return check();"/></td>
		<td><input type="button" value="取消" onclick="javascript:window.close();"/></td>
	</tr>
</table>
</form>
</body>
</html>