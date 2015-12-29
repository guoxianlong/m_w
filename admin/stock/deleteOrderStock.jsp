<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, adultadmin.util.*" %>
<%
StockAction action = new StockAction();
action.deleteOrderStock(request, response);

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
int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
%>
<script>
alert("修改成功！");
document.location = "orderStockList.jsp?pageIndex=<%= pageIndex %>";
</script>