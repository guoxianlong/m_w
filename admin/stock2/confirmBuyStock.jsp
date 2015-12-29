<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*" %>
<%
Stock2Action action = new Stock2Action();
action.confirmBuyStock(request, response);

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
}else if("return".equals(result)){
	String tip = (String) request.getAttribute("tip");
	session.setAttribute("errorProductList",request.getAttribute("errorProductList"));
%>
<script>
alert("<%=tip%>");
document.location = "buyStock.jsp?stockId=<%=request.getParameter("stockId")%>";
</script>
<%} %>
<script>
alert("修改成功！");
document.location = "buyStock.jsp?stockId=<%=request.getParameter("stockId")%>";
</script>