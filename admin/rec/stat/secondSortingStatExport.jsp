<%@page import="adultadmin.util.DateUtil"%>
<%@page import="java.util.*"%>
<%@page import="mmb.rec.stat.bean.SecondSortingStatBean"%>
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%
List<SecondSortingStatBean> list =(List<SecondSortingStatBean> )request.getAttribute("list");
String date = DateUtil.getNowDateStr();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>分播统计</title>
<%
response.setContentType("application/vnd.ms-excel;charset=UTF-8");
String fileName = date+"分播统计";
response.setHeader("Content-disposition","attachment; filename=\"" + new String(fileName.getBytes("GB2312"), "ISO8859-1") + ".xls\"");
%>
</head>
<body>
<table border="1">
<tr>
	<td>序号</td><td>日期</td><td>商品数量</td><td>订单数量</td><td>sku数量</td>
</tr>
<%if(list != null && list.size() > 0){
	int i = 1;
	for(SecondSortingStatBean bean : list){%>
<tr>
	<td><%=i %></td><td><%=bean.getDate() %></td><td><%=bean.getProductCount() %></td><td><%=bean.getOrderCount() %></td><td><%=bean.getSkuCount() %></td>
</tr>
<%	i++;
	} 
  }%>
</table>
</body>
</html>