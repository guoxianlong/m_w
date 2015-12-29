<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
var queryReturnPackageLogForm;
$(function() {
	$('#queryReturnPackageLogDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/SalesReturnController/returnPackageLog.mmx',
		fit : true,
		fitColumns : true,
		border : true,
		rownumbers : true,
		singleSelect : true,
		striped : true,
		idField : 'id',
		nowrap : false,
		showFooter:true,
		toolbar : '#queryReturnPackageLogtb',
		columns : [ [ {
			field : 'operTime',
			title : '操作时间',
			width : 200,
			align : 'center',
			formatter : function(value, row, index) {
				if (value == '' || value == null || value == 'undefined') {
					return '';
				} else {
					return value.substr(0,19);
				}
			}
		}, {
			field : 'operName',
			title : '操作人',
			width : 150,
			align : 'center'
		}, {
			field : 'remark',
			title : '操作内容',
			width : 400,
			align : 'center'
		} ] ],
		onLoadSuccess : function(data) {
			try {
				if (data.footer[0].packageCode != 'undefined' && data.footer[0].packageCode !=null && data.footer[0].packageCode != "") {
					$("#packageCode2").html("包裹<font color=\"red\">"+ data.footer[0].packageCode +" </font>的退货操作日志");
				} else {
					$("#packageCode2").html("");
				}
			} catch(e) {
				$.messager.alert("提示", "错误" ,"info");
			}
		}
	});
});
function queryReturnPackageLog() {
	$('#queryReturnPackageLogDataGrid').datagrid('load', {
		orderCode : $('#queryReturnPackageLogForm').find('[name=orderCode]').val(),
	});
}
</script>
<div id="queryReturnPackageLogtb" style="padding:3px;height: auto;" align="center">
	<form id="queryReturnPackageLogForm">
		<fieldset>
			<legend>筛选</legend>
			<span>订单号/包裹单号：</span>
			<input name='orderCode' id='orderCode' style="width: 150px;"/>&nbsp;&nbsp;
			<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="queryReturnPackageLog();">查询日志</a><br>
			<h3 id="packageCode2" name="packageCode2"></h3>
		</fieldset>
	</form>
</div>
<table id="queryReturnPackageLogDataGrid"></table>
