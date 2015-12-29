<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>打印记录</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
$(function() {
	$('#printRecordDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/ByBsController/getOperationPrintRecord.mmx',
		queryParams : {
			opid : <%=request.getParameter("opid")%>
		},
		collapsible:true,
		fit : true,
		fitColumns : true,
		border : true,
		rownumbers : true,
		singleSelect : true,
		striped : true,
		idField : 'id',
		nowrap : false,
		showFooter:true,
		columns : [ [ {
			field : 'time',
			title : '时间',
			width : 130,
			align : 'center'
		}, {
			field : 'operator_name',
			title : '操作人员',
			width : 100,
			align : 'center'
		} ] ]
	});
});
</script>
</head>
<body>
	<table id="printRecordDataGrid"></table>
</body>
</html>
