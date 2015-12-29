<%@ page contentType="text/html;charset=utf-8"%>

<%@page import="adultadmin.bean.stock.MailingBatchParcelBean"%>
<html>
<head>
<%
MailingBatchParcelBean parcelBean=(MailingBatchParcelBean)request.getAttribute("parcelBean");
String parcelId=request.getParameter("parcelId");
%>
<title>邮包重量复核</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
<%if(request.getAttribute("tip")!=null){%>
	<%if(request.getAttribute("tip").equals("1")){%>
		alert("复核不通过！");
		window.opener.location.reload();
		window.close();
	<%}else if(request.getAttribute("tip").equals("0")){%>
		alert("复核通过！");
		window.opener.location.reload();
		window.close();
	<%}%>
<%}%>
function checkWeight(){
	var weight=document.getElementById("weight").value;
	var reg=/^[0-9]{1,3}(\.[0-9]{1,2}){0,1}$/;
	if(reg.exec(weight)==null){
		alert("包裹重量输入错误！");
		return false;
	}
	return true;
}
</script>
</head>
<body>
<form action="mailingBatch.do?method=checkParcelWeight" method="post">
<input type="hidden" name="parcelId" value="<%=parcelId %>"/>
<table border="0" style="border: solid thin black" cellspacing="0" bgcolor="#FFCC00">
	<tr>
		<td colspan="2">邮包重量复核</td>
	</tr>
	<tr>
		<td>发货邮包编号：</td>
		<td style="color: blue;"><%=parcelBean.getCode() %></td>
	</tr>
	<tr>
		<td>邮包复核重量：</td>
		<td><input type="text" id="weight" name="weight"/>KG</td>
	</tr>
	<tr align="center">
		<td><input type="submit" value="复核" onclick="return checkWeight();"/></td>
		<td><input type="button" value="取消" onclick="javascript:window.close();"/></td>
	</tr>
</table>
</form>
</body>
</html>