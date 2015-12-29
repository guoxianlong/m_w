<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*" %>
<%
BuyStockinOldAction action = new BuyStockinOldAction();
action.deleteBuyStockinItem(request, response);

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
alert("操作成功！");
document.location = "buyStockinOld.jsp?id=<%=request.getParameter("buyStockinId")%>";
</script>