<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*" %>
<%
ProductStockAction action = new ProductStockAction();
action.batchInStorage(request, response);

String result = (String) request.getAttribute("result");
String tip = (String) request.getAttribute("tip");
if("failure".equals(result)){
%>
<script>
alert("<%=tip%>");
history.back(-1);
</script>
<%
	return;
}else if("success".equals(result)){
	%>
	<script>
	alert("<%=tip%>");
	document.location.href="stockExchangeList.jsp";
	</script>
	<%
}
%>
