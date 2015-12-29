<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*" %>
<%
OrderStockAction action = new OrderStockAction();
action.checkOrderStock(request, response);
String back = request.getParameter("back");
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
}if(back!=null && back.trim().length()>0){
	%><script>
	alert("修改成功！");
	document.location = "<%=back%>?id=<%=request.getParameter("operId")%>";
	</script><%	
	return ;}%>
<script>
alert("申请成功！");
document.location = "orderStock.jsp?id=<%=request.getParameter("operId")%>";
</script>