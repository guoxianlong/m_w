<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*" %>
<%
StockAction action = new StockAction();
action.completeOrderStock(request, response);

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
alert("修改成功！");
document.location = "orderStock.jsp?id=<%=request.getParameter("operId")%>";
</script>