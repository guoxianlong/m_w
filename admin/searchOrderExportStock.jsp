<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<html>
<head>
<%!
static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
%>
<%
	List orderList = (List)request.getAttribute("orderList");
	
	String fileName = "销售部物流组跟单表";
	response.setHeader("Content-Disposition","attachment;filename=" + new String(fileName.getBytes("GBK"), "iso8859-1"));
	response.setContentType("application/vnd.ms-excel;charset=gb2312");
	
%>
<meta http-equiv="Content-Type" content="text/html; charset=GB2312" />
</head>
<body>
<table width="100%" cellpadding="3" cellspacing="1" border="1">
	<tr align="center">
		<td>序号</td>
		<td>订单号</td>
		<td>产品名称</td>
		<td>金额</td>
		<td>包裹单号</td>
		<td>发货人</td>
	</tr>
	<%for(int i=0;i<orderList.size();i++){ %>
		<%voOrder order=(voOrder)orderList.get(i); %>
	<tr align="center">
		<td><%=i+1 %></td>
		<td><%=order.getCode() %></td>
		<td align="left">
		<%
			if(!StringUtil.convertNull(order.getProducts()).equals("")){
				String[] productNames = order.getProducts().split(",");
				for(int j=0;j<productNames.length;j++){
		%>
		<%=productNames[j] %><br/>
		<%
				}
			}
		%>
		</td>
		<td><%=order.getDprice() %></td>
		<td><%=order.getPackageNum() %></td>
		<td><%=order.getConsigner() %></td>
	</tr>
	<%} %>
</table>
</body>
</html>