<%@ page contentType="text/html;charset=GB2312" %>
<%@page import="java.util.List"%>
<%@page import="mmb.stock.stat.OrderStockTimelyBean"%>

<%@page import="adultadmin.util.DateUtil"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=GB2312">
<title>导出</title>
<%
response.setContentType("application/vnd.ms-excel;");
response.setHeader("Content-disposition","attachment; filename=\"" +DateUtil.getNowDateStr().replace("-","")+new String("订单发货明细".getBytes("GB2312"),"ISO8859-1")+ ".xls\"");
%>
</head>
<body>
<%if(request.getAttribute("orderStockTimelyList")!=null){%>
	<%List orderStockTimelyList=(List)request.getAttribute("orderStockTimelyList"); %>
	<table border="1">
		<tr>
			<td>订单号</td>
			<td>订单状态</td>
			<td>申请发货次数</td>
			<td>申请发货时间</td>
			<td>申请人</td>
			<td>复核时间</td>
			<td>复核操作人</td>
		</tr>
		<%for(int i=0;i<orderStockTimelyList.size();i++){ %>
			<%OrderStockTimelyBean ostBean=(OrderStockTimelyBean)orderStockTimelyList.get(i); %>
		<tr align="center">
			<td><%=ostBean.getOrderCode() %></td>
			<td><%=ostBean.getStockOutUserId()==0?"待发货":"已复核" %></td>
			<td><%=ostBean.getOrderStockCount() %></td>
			<td><%=ostBean.getFirstOrderStockDatetime()==null?"-":ostBean.getFirstOrderStockDatetime().substring(0,19) %></td>
			<td><%=ostBean.getFirstOrderStockUserName().equals("")?"-":ostBean.getFirstOrderStockUserName() %></td>
			<td><%=ostBean.getStockOutDatetime()==null?"-":ostBean.getStockOutDatetime().substring(0,19) %></td>
			<td><%=ostBean.getStockOutUserName().equals("")?"-":ostBean.getStockOutUserName() %></td>
		</tr>
		<%} %>
	</table>
<%} %>
</body>
</html>