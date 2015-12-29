<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*" %>
<%
BuyStockinAction action = new BuyStockinAction();
action.transformToBuyStockin(request, response);

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
Integer stockinId = (Integer) request.getAttribute("stockinId");
Integer isBTwoC = (Integer)request.getAttribute("isBTwoC");
%>
<script>
alert("添加成功！");
document.location = "buyStockin.jsp?isBTwoC=<%=isBTwoC%>&id=<%= stockinId %>";
</script>