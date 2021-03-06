<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>报损报溢操作记录</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
$(function() {
	$('#operationRecordDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/ByBsController/getOperationRecord.mmx',
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
			field : 'information',
			title : '说明',
			width : 250,
			styler: function(value,row,index){
				return 'color:red';
			}
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
	<table id="operationRecordDataGrid"></table>
</body>
</html>
