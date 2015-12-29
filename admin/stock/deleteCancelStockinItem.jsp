<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*" %>
<%
StockAction action = new StockAction();
action.deleteCancelStockinItem(request, response);

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
alert("删除成功！");
document.location = "cancelStockin.jsp?id=<%=request.getParameter("operId")%>";
</script>