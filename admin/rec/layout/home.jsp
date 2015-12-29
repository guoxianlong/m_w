<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
<jsp:include page="../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
	var portal;
	var col;
	$(function() {
		col = $('#portal div').length;
		portal = $('#portal').portal({
			border : false,
			fit : true,
		});
		
	});
</script>
</head>
<body class="easyui-layout" fit="true">
	<div region="center" style="overflow: hidden;" border="false">
		<div id="portal" style="position:relative;">
			<div></div>
			<div></div>
		</div>
	</div>
</body>
</html>