<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
$(function() {
	$('#getReturnPackageCheckInfoRealDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/SalesReturnController/getReturnPackageCheckInfoReal.mmx',
		fit : true,
		fitColumns : true,
		border : false,
		singleSelect : true,
		striped : true,
		idField : 'id',
		nowrap : false,
		showFooter:true,
		columns : [ [ {
			field : 'productCode',
			title : '实际扫描商品',
			width : 200,
			align : 'center'
		}, {
			field : 'count',
			title : '数量',
			width : 150,
			align : 'center'
		} ] ]
	});
	$('#getReturnPackageCheckInfoOutDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/SalesReturnController/getReturnPackageCheckInfoOut.mmx',
		fit : true,
		fitColumns : true,
		border : false,
		singleSelect : true,
		striped : true,
		idField : 'id',
		nowrap : false,
		showFooter:true,
		columns : [ [ {
			field : 'productCode',
			title : '出库单内商品',
			width : 200,
			align : 'center'
		}, {
			field : 'stockoutCount',
			title : '数量',
			width : 150,
			align : 'center'
		} ] ]
	});
});
function getReturnPackageCheckInfoDetail(checkId, checkResult, orderCode ) {
	$('#getReturnPackageCheckInfoRealDataGrid').datagrid('load', {
		checkId : checkId,
		checkResult : checkResult
	});
	$('#getReturnPackageCheckInfoOutDataGrid').datagrid('load', {
		orderCode : orderCode,
		checkResult : checkResult
	});
}
</script>
<div data-options="border:false" style="height:100%;width:300px;overflow: hidden;float: left;" >
	<table id="getReturnPackageCheckInfoRealDataGrid"></table>
</div>
<div data-options="border:false" style="height:100%;width:300px;overflow: hidden;float: right;">
	<table id="getReturnPackageCheckInfoOutDataGrid"></table>
</div>
