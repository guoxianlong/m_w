<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
var returnReasonStatisticDetailedForm;
$(function() {
	$('#returnReasonStatisticDetailedDataGrid').datagrid({
		url : '${pageContext.request.contextPath}/SalesReturnController/returnsReasonStatisticDetail.mmx',
		queryParams : {
			flag : 'query'
		},
		fit : true,
		fitColumns : true,
		border : true,
		rownumbers : true,
		singleSelect : true,
		striped : true,
		idField : 'id',
		nowrap : false,
		showFooter:true,
		pagination :true,
		pageSize : 10,
		pageList : [ 10, 20, 30, 40, 50 ],
		toolbar : '#returnReasonStatisticDetailedtb',
		columns : [ [ {
			field : 'orderCode',
			title : '订单编号',
			width : 200,
			align : 'center'
		}, {
			field : 'packageCode',
			title : '包裹单号',
			width : 150,
			align : 'center'
		}, {
			field : 'deliverName',
			title : '快递公司',
			width : 200,
			align : 'center'
		}, {
			field : 'storageTime',
			title : '入库时间',
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
			field : 'storageStatus',
			title : '入库状态',
			width : 200,
			align : 'center',
			formatter : function(value, row, index) {
				if (value == 0) {
					return '<font color="green">正常入库</font>';
				} else if (value == 1) {
					return '<font color="red">商品缺失</font>';
				} else if (value == 2) {
					return '<font color="#CC3300">订单和包裹不匹配</font>';
				}
			}
		}, {
			field : 'reasonName',
			title : '退货原因',
			width : 200,
			align : 'center'
		} ] ]
	});
	
	$('#reasonId').combobox({   
		url:'<%=request.getContextPath()%>/SalesReturnController/getReturnReasons.mmx',  
		valueField : 'id',   
		textField : 'text',
		panelHeight : 'auto',
	    editable:false
	}); 
	
	returnReasonStatisticDetailedForm = $('#returnReasonStatisticDetailedForm').form({
		url : '<%=request.getContextPath()%>/SalesReturnController/returnsReasonStatisticDetail.mmx'
	});
});

function returnReasonStatisticDetailedSearch() {
	$('#returnReasonStatisticDetailedDataGrid').datagrid('load', {
		orderCode : $('#returnReasonStatisticDetailedForm').find('[name=orderCode]').val(),
		packageCode : $('#returnReasonStatisticDetailedForm').find('[name=packageCode]').val(),
		startTime : $('#startTime').datebox("getValue"),
		reasonId : $('#reasonId').combobox("getValue"),
		endTime : $('#endTime').datebox("getValue"),
		flag : 'query'
	});
}

function exportReturnReasonStatisticDetailed() {
	returnReasonStatisticDetailedForm.submit();
}
</script>
	<div id="returnReasonStatisticDetailedtb" style="padding:3px;height: auto;">
		<form id="returnReasonStatisticDetailedForm">
			<span>订单编号：</span>
			<input name='orderCode' id='orderCode' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
			<span>入库时间(起)：</span>
			<input class="easyui-datebox" name='startTime' id='startTime' style="width:152px;border:1px solid #ccc"/>&nbsp;&nbsp;
			<span>退货原因：</span>
			<input name='reasonId' id='reasonId' style="width:152px;border:1px solid #ccc"/>&nbsp;&nbsp;<br>
			<span>包裹单号：</span>
			<input name='packageCode' id='packageCode' style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;
			<span>入库时间(末)：</span>
			<input class="easyui-datebox" name='endTime' id='endTime' style="width:152px;border:1px solid #ccc"/>&nbsp;&nbsp;
			<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="returnReasonStatisticDetailedSearch();">查询</a>
			<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-save',plain:true" onclick="exportReturnReasonStatisticDetailed();">导出Excel</a>
		</form>
	</div>
	<table id="returnReasonStatisticDetailedDataGrid"></table>
