<%@ page language="java" pageEncoding="UTF-8"%>
<%
	String hasZC = (String)request.getAttribute("hasZC");
	String hasWX = (String)request.getAttribute("hasWX");
%>
<!DOCTYPE html>
<html>
<head>
<title>待调度商品列表</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
</script>
</head>
<body>
	<div class="easyui-tabs" fit="true">
	<% if (hasZC.equals("1") && hasWX.equals("1")) { %>
	    <div title="无锡地区" data-options="href:'${pageContext.request.contextPath}/admin/rec/oper/areaStockExchange/wxExchangeTabs.jsp'"></div>
	    <div title="增城地区" data-options="href:'${pageContext.request.contextPath}/admin/rec/oper/areaStockExchange/zcExchangeTabs.jsp'"></div>
	<%} else if (hasWX.equals("1")) { %>
		<div title="无锡地区" data-options="href:'${pageContext.request.contextPath}/admin/rec/oper/areaStockExchange/wxExchangeTabs.jsp'"></div>
	<%} else if (hasZC.equals("1")) {%>
		<div title="增城地区" data-options="href:'${pageContext.request.contextPath}/admin/rec/oper/areaStockExchange/zcExchangeTabs.jsp'"></div>
	<%} %>
	</div>
</body>
</html>