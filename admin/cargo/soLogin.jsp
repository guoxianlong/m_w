<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.util.DateUtil"%>

<%@page import="java.util.List"%>
<%@page import="mmb.stock.cargo.CartonningInfoBean"%>
<%@page import="adultadmin.action.vo.voProduct"%>
<%@page import="adultadmin.bean.cargo.CargoInfoBean"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8">
<title>仓储移动设备登陆</title>
<script type="text/javascript">
function getFocus(){
	document.getElementById("username").focus();
}
function closeWindow(){
	window.close();
}
</script>
<style type="text/css">
<!--
.STYLE2 {
	font-size: 22px;
	font-weight: bold;
}
-->
</style>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden" onload="getFocus()">

<form action="<%=request.getContextPath() %>/stockOperation/login.do" method="post">
 <table width="220" height="220" border="0" cellspacing="0">
<tr align="center">
	<td colspan="2"><span class="STYLE2">仓储移动设备登陆</span></td>
</tr>
<tr align="center">
	<td colspan="2"><%=DateUtil.getNow() %></td>
</tr>
<tr align="center">
	<td align="right">用户名:</td>
	<td align="left"><input type="text" id="username" name="username" size="13"/></td>
</tr>
<tr align="center">
	<td align="right">密     码:</td>
	<td align="left"><input type="password" id="password" name="password" size="13"/></td>
</tr>
<tr align="center">
	<div align="center">
	  <td colspan="2"><input type="submit" value="登陆"style="height=30px;width=80px;"/>	&nbsp	&nbsp	
	  <input type="reset" value="关闭" style="height=30px;width=80px;"/></td>	  
	  </div>
</tr>
</table>
</form>

</body>
</html>