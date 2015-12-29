<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*" %>
<html>
<%
String code = StringUtil.convertNull(request.getParameter("code"));//波次号或者出库单号
String productCode = StringUtil.convertNull(request.getParameter("productCode"));//SKU编号
String cargoWholeCode = StringUtil.convertNull(request.getParameter("cargoWholeCode"));//SKU编号
%>
  <head>
    
    <title>商品物流分类</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<script type="text/javascript">
	</script>

  </head>
  <body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden">
  <div style="margin-left:3px;margin-top:3px;">
   		<div style="width:220px;height:130px;">
   			<form action="<%= request.getContextPath()%>/admin/stockOperation.do?method=soAbnormalProductHandleNext" method="post" onSubmit="return check();">
   				<table align="center" cellspacing="4" border="0" width="220px" height="270px">
   					<tr>
   						<td colspan="2" align="center"><font size="5" style="font-weight:bold">分拣异常商品处理</font></td>
   					</tr>
	   				<tr>
	   					<td align="left"><font size="2">波次号/出库单号：</font></td>
	   					<td align="left"><%=code %></td>
	   				</tr>
	   				<tr>
	   					<td align="left"><font size="2">产品编号：</font></td>
	   					<td align="left"><%=productCode %></td>
	   				</tr>
	   				<tr>
	   					<td align="left"><font size="2">推荐货位号：</font></td>
	   					<td align="left"><%=cargoWholeCode %></td>
	   				</tr>
	   				<tr>
	   					<td align="left"><font size="2">货位号：</font></td>
	   					<td align="left"><input type="text" name="cwCode" id="cwCode" onBlur="checkName(this);" size="13"/></td>
	   				</tr>
	   				<tr>
	   					<td align="left"><input type="submit" value="提交 " /></td>
	   					<td align="left"><input type="button" value="返回" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=stockOperation&toPage=huoweiyichang';"/></td>
	   				</tr>
   				</table>
   				<input type="hidden" name="code" value='<%=code%>'> 
   				<input type="hidden" name="productCode" value='<%=productCode%>'> 
   				<input type="hidden" name="cargoWholeCode" value='<%=cargoWholeCode%>'> 
   			</form>
   		</div>
   		<br>
   	</div>
</body>
</html>
