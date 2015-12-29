<%@ page language="java" import="java.util.*,adultadmin.bean.stock.*,adultadmin.util.*,mmb.stock.stat.*,adultadmin.bean.*" pageEncoding="UTF-8"%>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*,mmb.stock.stat.*" %>
<%
	PagingBean paging = (PagingBean) request.getAttribute("paging");
	List list = (ArrayList) request.getAttribute("returnedSaleOutProductList");
	int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
	String wareAreaSelectLable = ProductWarePropertyService.getWeraAreaOptions(request,wareArea);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>My JSP 'returnOrderInfo.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
</head>
<body>
<center><h2>退货库已下架商品列表</h2></center>
<center>
	<form action="returnStorageAction.do?method=getReturnedSaleOutProductList" method="post">
		<%= wareAreaSelectLable %> 
		<input type="submit" value="查询" />
	</form>
</center>
<table  width="82%" align="center" border="0" cellspacing="1" cellpadding="0" bgcolor="#000000">
	<tr bgcolor="#00ccff">
		<td align="center">序号</td>
		<td align="center">产品编号</td>
		<td align="center">产品名称</td>
		<td align="center">货位库存(冻结量)</td>
		<td align="center">库地区</td>
		
	</tr>
	<% 
	if( list != null && list.size() > 0 ) {
		for( int i = 0 ; i < list.size(); i ++ ) 
		{
			ProductStockBean rpb = (ProductStockBean)list.get(i);
	%>
	<tr bgcolor="#ffffff">
		<td align="center"><%= paging.getCurrentPageIndex()*paging.getCountPerPage() + i + 1 %></td>
		<td align="center"><%= rpb.getProduct().getCode() %></td>
		<td align="center"><%= rpb.getProduct().getOriname()%></td>
		<td align="center"><%= rpb.getStock()%>(<%= rpb.getLockCount()%>)</td>
		<td align="center"><%= rpb.getAreaName(rpb.getArea())%></td>
	</tr>
	<%
		}
	} else {
	%>
		<tr bgcolor="#cccccc"><td colspan="5" align="center">暂时还没有退货库商品</td></tr>
	<%
		}
	%>
</table>
</br>


<%if(paging!=null){ %>
		<p align="center" style="MARGIN-LEFT: 20px"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
</body>
</html>
