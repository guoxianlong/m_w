<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*" %>
<%
ProductStockAction action = new ProductStockAction();
action.batchCompleteStockExchange(request, response);

%>
<script>
document.location = "stockExchange.jsp?exchangeId=<%=request.getParameter("exchangeId")%>";
</script>