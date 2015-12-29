<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*" %>
<%
ProductStockAction action = new ProductStockAction();
action.addStockExchangeItem2(request, response);

String result = (String) request.getAttribute("result");
String tipBuf = (String) request.getAttribute("tipBuf");
if("failure".equals(result)){
	String tip = (String) request.getAttribute("tip");
%>
<script>
alert("<%=tip%>");
parent.document.location.reload();
</script>
<%
	return;
}
%>
<script>
<%if(tipBuf == null || tipBuf.length() == 0){%>
<%} else {%>
alert("该调拨操作<%= tipBuf %>商品已添加过,直接修改数量即可,不能重复添加!但没有添加过的商品依然会添加成功！");
<%}%>
parent.document.location.reload();
</script>