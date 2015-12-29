<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, adultadmin.util.*" %>
<%
OrderStockAction action = new OrderStockAction();
action.deleteOrderStock(request, response);

String result = (String) request.getAttribute("result");
String backType = (String) request.getParameter("backType");
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
alert("删除成功！");
<%if(backType != null && backType.equalsIgnoreCase("checkOrderStock")){%>
document.location = "checkOrderStockList.jsp?pageIndex=<%= pageIndex %>";
<%} else if (backType != null && backType.equals("easyuiOrderStock")) {
%>
document.location = "<%= request.getContextPath()%>/admin/rec/oper/orderStock/orderStock.jsp";
<%
} else {%>
document.location = "orderStockList.jsp?pageIndex=<%= pageIndex %>";
<%}%>
</script>