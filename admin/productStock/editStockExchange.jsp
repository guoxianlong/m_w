<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, adultadmin.bean.stock.*" %>
<%
ProductStockAction action = new ProductStockAction();
action.editStockExchange(request, response);

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
	String back = (String) request.getAttribute("back");
%>
<script>
alert("修改成功！");
document.location = "<%=back%>";
</script>
<%
	return;
}
%>