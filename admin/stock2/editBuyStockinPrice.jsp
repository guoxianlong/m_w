<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*" %>
<%
BuyStockinAction action = new BuyStockinAction();
action.editBuyStockinPrice(request, response);

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
document.location = "buyStockinPrice.jsp?id=<%=request.getParameter("buyStockinId")%>";
</script>
<%
}
%>