<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*" %>
<%
ProductStockAction action = new ProductStockAction();
action.deleteIMEIProduct(request, response);

String result = (String) request.getAttribute("result");
int imeiCount = (Integer) request.getAttribute("imeiCount");
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
else if (imeiCount == 1) {%>
	<script>
	alert("删除成功！");
	document.location = "stockExchange.jsp?exchangeId=<%=request.getParameter("exchangeId")%>";
	</script>
<%
}
else {
	if("hasLoss".equals(result)){
		String back = (String) request.getAttribute("back");
	%>
	<script>
	alert("删除成功！但有产品出库量大于入库量，有损耗！");
	<%if(back == null || back.length() == 0){%>
	document.location="stockExchange.jsp?exchangeId=<%=request.getParameter("exchangeId")%>";
	<%} else {%>
		document.location = "<%=back%>";
	<%}%>
	</script>
	<%
	}
	else{
		String back = (String) request.getAttribute("back");
	%>
	<script>
	alert("删除成功！");
	<%if(back == null || back.length() == 0){%>
	document.location="stockExchange.jsp?exchangeId=<%=request.getParameter("exchangeId")%>";
	<%} else {%>
		document.location = "<%=back%>";
	<%}%>
	</script>
	<%
	}
}%>
