<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*" %>
<%
ProductStockAction action = new ProductStockAction();
action.addStockExchangeItem(request, response);

String result = (String) request.getAttribute("result");
if("failure".equals(result)){
	String tip = (String) request.getAttribute("tip");
%>
<script>
alert("<%=tip%>");
history.back(-1);
</script>
<%
	return;
}
%>
<script>
document.location = "stockExchange.jsp?exchangeId=<%=request.getParameter("exchangeId")%>";
</script>