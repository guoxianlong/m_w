<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, adultadmin.bean.buy.*" %>
<%
Stock2Action action = new Stock2Action();
action.editBuyStock(request, response);

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
else{
%>
<script>
alert("修改成功！");
document.location = "buyStock.jsp?stockId=<%=request.getParameter("stockId")%>";
</script>
<%
	return;
}
%>