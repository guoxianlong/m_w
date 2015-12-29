<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>买卖宝新物流系统主页</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="utf-8">
	$(function() {
		$('#indexLayout').layout('collapse','east');  
	});
</script>
</head>
<body id="indexLayout" class="easyui-layout" fit="true">
	<div region="west" href="${pageContext.request.contextPath}/admin/help/layout/west.jsp" title="导航栏" split="false" style="width:200px;overflow: hidden;"></div>
	<div region="center" href="${pageContext.request.contextPath}/admin/help/layout/center.jsp" style="overflow: hidden;"></div>
	<div region="east" href="${pageContext.request.contextPath}/admin/help/layout/east.jsp"title="帮助" style="width:200px;overflow: hidden;"></div>
</body>
</html>