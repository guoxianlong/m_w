<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getChangeRepairProductDatagrid.mmx',
	    toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : false,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    rownumbers : true,
	    singleSelect : true,
	    pageSize : 20,
	    pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    frozenColumns : [[
					{field:'id',title:'ID',width:20,hidden:true},
					{field:'productName',title:'商品名称',width:220,align:'center'}
	    ]],
	    columns:[[  
	        {field:'productOriName',title:'型号',width:30,align:'center'},  
	        {field:'afterSaleCode',title:'售后单号',width:30,align:'center'},  
	        {field:'afterSaleDetectCode',title:'售后处理单号',width:30,align:'center'},  
	        {field:'imeiCode',title:'IMEI',width:30,align:'center'},  
	        {field:'problemRmark',title:'故障描述',width:30,align:'center'},  
	        {field:'createDatetime',title:'发货日期',width:30,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return value.substring(0,19);
	        		}
        		}},  
	        {field:'statusName',title:'状态',width:30,align:'center'},  
	        {field:'returnDatetime',title:'寄回日期',width:30,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return value.substring(0,19);
	        		}
        		}}
	    ]]
	}); 
	$('#statusQ').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getBackSupplierStatus.mmx',
      	valueField:'id',
		textField:'text' 
    });
	$('#supplierQ').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getBackSupplier.mmx',
      	valueField:'id',
		textField:'text' 
    });
});
function searchFun() {
	datagrid.datagrid('load', {
		afterSaleCode :  $('#tb input[name=afterSaleCode]').val(),
		afterSaleDetectCode :  $('#tb input[name=afterSaleDetectCode]').val(),
		productName :  $('#tb input[name=productName]').val(),
		productCode :  $('#tb input[name=productCode]').val(),
		imeiCode :  $('#tb input[name=imeiCode]').val(),
		status : $('#statusQ').combobox('getValue'),
		supplier : $('#supplierQ').combobox('getValue'),
	});
}
function clearFun() {
	$('#tb input[name=afterSaleCode]').val('');
	$('#tb input[name=afterSaleDetectCode]').val('');
	$('#tb input[name=productName]').val('');
	$('#tb input[name=productCode]').val('');
	$('#tb input[name=imeiCode]').val('');
	$('#statusQ').combobox('setValue','');
	$('#supplierQ').combobox('setValue','');
	datagrid.datagrid('load', {});
}

function excel(){
	var afterSaleCode = $('#tb input[name=afterSaleCode]').val();
	var afterSaleDetectCode = $('#tb input[name=afterSaleDetectCode]').val();
	var productName = $('#tb input[name=productName]').val();
	var productCode = $('#tb input[name=productCode]').val();
	var imeiCode = $('#tb input[name=imeiCode]').val();
	var status = $('#statusQ').combobox('getValue');
	var supplier = $('#supplierQ').combobox('getValue');
	location.href = "${pageContext.request.contextPath}/admin/AfStock/excelChangeRepairProduct.mmx?afterSaleCode=" + afterSaleCode 
		+ "&afterSaleDetectCode=" + afterSaleDetectCode + "&productName=" + productName + "&productCode=" + productCode + "&imeiCode=" + imeiCode
		+ "&status=" + status + "&supplier=" + supplier;
}
</script>
</head>
<body>
	<div id="tb"  style="height: auto;display: none;">
		<fieldset>
			<legend>筛选</legend>
			<table class="tableForm">
				<tr>
					<th>售后单号：</th>
					<td><input id="afterSaleCode" name="afterSaleCode" style="width: 120px;"/></td>
					<th>售后处理单号：</th>
					<td><input id="afterSaleDetectCode" name="afterSaleDetectCode" style="width: 120px;"/></td>
					<th>寄回厂商：</th>
					<td><input id="supplierQ" name="supplier"  style="width: 120px;"/></td>
					<th>状态：</th>
					<td><input id="statusQ" name="status" editable="false" style="width: 120px;"/></td>
				</tr>
				<tr>
					<th>商品名称：</th>
					<td><input id="productName" name="productName" style="width: 120px;"/></td>
					<th>商品编号：</th>
					<td><input id="productCode" name="productCode" style="width: 120px;"/></td>
					<th>IMEI：</th>
					<td><input id="imeiCode" name="imeiCode" style="width: 120px;"/></td>
					<th></th>
					<td>
						<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查找</a>
						<a class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="clearFun();" href="javascript:void(0);">重置</a>
						<a class="easyui-linkbutton" iconCls="icon-redo" onclick="excel();" plain="true" href="javascript:void(0);">导出</a>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
	<table id="datagrid"></table> 
</body>
</html>