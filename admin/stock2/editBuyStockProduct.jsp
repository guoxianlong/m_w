<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*" %>
<%
Stock2Action action = new Stock2Action();
action.editBuyStockProduct(request, response);

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
else if("该操作已经完成，不能再更改（签收时间除外）！".equals((String)request.getAttribute("tips"))){
%>
<script>
alert('<%="该操作已经完成，不能再更改（签收时间除外）！"%>');
document.location = "buyStock.jsp?stockId=<%=request.getParameter("stockId")%>";
</script>
<%
}else{

%>
<script>
alert("修改成功！");
document.location = "buyStock.jsp?stockId=<%=request.getParameter("stockId")%>";
</script>
<%
}
%>