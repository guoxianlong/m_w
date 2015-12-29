<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*" %>
<%
ProductStockAction action = new ProductStockAction();
action.createStockExchange(request, response);

String result = (String) request.getAttribute("result");
String forward = request.getParameter("forward");
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
alert("调拨单添加成功！");
<%if(forward != null && forward.equalsIgnoreCase("auto")){%>
document.location = "stockExchange.jsp?exchangeId=<%= request.getAttribute("exchangeId") %>";
<%} else if(forward != null && forward.equalsIgnoreCase("close")){%>
window.close();
<%} else {%>

<%}%>
</script>