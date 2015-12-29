<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../../rec/inc/easyui.jsp"></jsp:include>
<jsp:include page="../../rec/inc/easyui-portal.jsp"></jsp:include>
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
			<div>欢迎使用帮助文档编辑器，可单击左侧菜单进行文档编辑。</div>
			<div></div>
		</div>
	</div>
</body>
</html>