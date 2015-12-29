<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*" %>
<html>
  <head>
    
    <title>分拣异常商品处理</title>
    
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
   		<div style="width:220px;height:130px;">
   			<form action="<%= request.getContextPath()%>/admin/stockOperation.do?method=abnormalProductHandle" method="post" onSubmit="return check();">
   				<table align="center" cellspacing="20">
   					<tr>
   						<td colspan="2" align="center"><font size="5" style="font-weight:bold">分拣异常商品处理</font></td>
   					</tr>
	   				<tr>
	   					<td align="left"><font size="2">波次号/出库单号：</font></td>
	   					<td align="left"><input type="text" name="code" id="code" onBlur="checkName(this);" size="14"/></td>
	   				</tr>
	   				<tr>
	   					<td align="left"><font size="2">产品编号：</font></td>
	   					<td align="left"><input type="text" name="productCode" id="productCode" onBlur="checkName(this);" size="14"/></td>
	   				</tr>
	   				<tr>
	   					<td align="left"><input type="submit" style="widht:100px;heigth:26px;" value="下一步" /></td>
	   					<td align="left"><input type="button" style="widht:70px;heigth:26px;" value="返回" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=stockOperation&toPage=huoweiyichang';"/></td>
	   				</tr>
   				</table>
   			</form>
   		</div>
</body>
</html>