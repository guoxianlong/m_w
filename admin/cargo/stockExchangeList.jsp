<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.bean.stock.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean" %>
<html>
<head>
<title>相关单据列表</title>
<%
List orderList=null;
List countList=null;
if(request.getAttribute("orderList")!=null){
	orderList=(List)request.getAttribute("orderList");
}
if(request.getAttribute("countList")!=null){
	countList=(List)request.getAttribute("countList");
}
%>
</head>
<body>
<p>货位号：<%=request.getParameter("cargoWholeCode") %>&nbsp;&nbsp;&nbsp;&nbsp;
产品编号：<%= request.getParameter("productCode")%></p>
<table cellpadding="3" cellspacing="1" border=1 width="35%">
	<tr align="center">
		<td>单据编号</td>
		<%if(request.getParameter("type").equals("stock")){ %>
		<td>未上架量</td>
		<%}else if(request.getParameter("type").equals("stockLock")){ %>
		<td>产品冻结量</td>
		<%}else if(request.getParameter("type").equals("spaceLock")){ %>
		<td>空间冻结量</td>
		<%}%>
	</tr>
	<%StockExchangeBean se=new StockExchangeBean(); %>
	<%CargoOperationBean co=new CargoOperationBean(); %>
	<%int total=0; %>
	<%for(int i=0;i<orderList.size();i++){ %>
	<tr align="center">
	<%if(request.getParameter("type").equals("stock")){ %>
		<%se=(StockExchangeBean)orderList.get(i);%>
		<td><a href="productStock/stockExchange.jsp?exchangeId=<%=se.getId() %>" target="_blank"><%=se.getCode() %></a></td>
	<%}else{ %>
		<td><%=orderList.get(i) %></td>
	<%} %>
		<td><%=countList.get(i) %></td>
		<%total+=Integer.parseInt(countList.get(i).toString()); %>
	</tr>
	<%} %>
	<tr align="center">
		<td>总计</td>
		<td><%=total %></td>
	</tr>
</table>
<p>货位号：<%=request.getParameter("cargoWholeCode") %>&nbsp;&nbsp;&nbsp;&nbsp;
产品编号：<%= request.getParameter("productCode")%></p>
</body>
</html>