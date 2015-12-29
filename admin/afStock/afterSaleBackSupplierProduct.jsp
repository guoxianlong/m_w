<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>返厂商品列表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getBackSupplierProductDatagrid.mmx',
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
					{field:'productName',title:'商品名称',width:120,align:'center'}
	    ]],
	    columns:[[  
	        {field:'productOriName',title:'商品原名称',width:30,align:'center'},  
	        {field:'catalogName',title:'一级分类',width:20,align:'center'},  
	        {field:'productModel',title:'型号',width:25,align:'center'},
	        {field:'afterSaleDetectCode',title:'售后处理单号',width:35,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return '<a href=\"${pageContext.request.contextPath}/admin/afStock/afterSaleDetectProductInfo.jsp?id=' + rowData.detectId +'">' + value + '</a>';
	        		}
        		}},  
	        {field:'imeiCode',title:'IMEI',width:30,align:'center'},  
	        {field:'repair',title:'是否返修',width:20,align:'center'},  
	        {field:'problemCode',title:'故障代码',width:20,align:'center'},  
	        {field:'problemRmark',title:'故障描述',width:20,align:'center'},  
	        {field:'declareStatus',title:'申报状态',width:20,align:'center'},  
	        {field:'sendDatetime',title:'发货日期',width:25,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return value.substring(0,19);
	        		}
        		}},  
	        {field:'statusName',title:'状态',width:25,align:'center'},  
	        {field:'supplierName',title:'维修厂商',width:20,align:'center'},  
	        {field:'stockTypeName',title:'商品类型',width:20,align:'center'},
	        {field:'createDateTime',title:'检测日期',width:25,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return value.substring(0,19);
	        		}
        		}}, 
	    ]]
	}); 
	$('#statusQ').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getPartBackSupplierStatus.mmx',
      	valueField:'id',
		textField:'text' 
    });
	$('#supplierQ').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getBackSupplier.mmx',
      	valueField:'id',
		textField:'text' 
    });
	//$('#declareStatusQ').combobox({
    //  	url : '${pageContext.request.contextPath}/Combobox/getDeclareStatus.mmx',
    //  	valueField:'id',
	//	textField:'text' 
    //});
	$('#productType').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getProductType.mmx',
      	valueField:'id',
		textField:'text' 
    });
    
    $('#areaId').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getSignArea.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
    $('#parentId1').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getParentId1.mmx',
      	valueField:'id',
		textField:'text',
		editable : false,
    });
});
function searchFun() {
	datagrid.datagrid('load', {
		startTime : $('#startTime').datebox('getValue'),
		endTime : $('#endTime').datebox('getValue'),
		startTime_detect : $('#startTime_detect').datebox('getValue'),
		endTime_detect : $('#endTime_detect').datebox('getValue'),
		packageCode :  $('#tb input[name=packageCodes]').val(),
		afterSaleDetectCode :  $('#tb textarea[name=afterSaleDetectCode]').val(),
		productName :  $('#tb input[name=productName]').val(),
		imeiCode :  $('#tb input[name=imeiCode]').val(),
		status :  $('#statusQ').combobox('getValue'),
		productCode :  $('#tb input[name=productCode]').val(),
		//declareStatus : $('#declareStatusQ').combobox('getValue'),
		productType:$('#productType').combobox('getValue'),
		supplier:$('#supplierQ').combobox('getValue'),
		orderCode : $('#tb textarea[name=orderCode]').val(),
		areaId : $('#areaId').combobox('getValue'),
		parentId1 : $('#parentId1').combobox('getValue')
	});
}
function excel(){
	var startTime_detect = $('#startTime_detect').datebox('getValue');
	var endTime_detect = $('#endTime_detect').datebox('getValue');
	var startTime = $('#startTime').datebox('getValue');
	var endTime = $('#endTime').datebox('getValue');
	var packageCode =  $('#tb input[name=packageCodes]').val();
	var orderCode =  $('#tb textarea[name=orderCode]').val();
	var afterSaleDetectCode =  $('#tb textarea[name=afterSaleDetectCode]').val();
	var productName = $('#tb input[name=productName]').val();
	var imeiCode =  $('#tb input[name=imeiCode]').val();
	var status =  $('#statusQ').combobox('getValue');
	var productCode =  $('#tb input[name=productCode]').val();
	//var declareStatus = $('#declareStatusQ').combobox('getValue');
	var productType = $('#productType').combobox('getValue');
	var supplier = $('#supplierQ').combobox('getValue');
	var areaId = $('#areaId').combobox('getValue');
	var parentId1 = $('#parentId1').combobox('getValue');
	location.href = "${pageContext.request.contextPath}/admin/AfStock/excelBackSupplierProduct.mmx?startTime=" + 
		startTime + "&endTime=" + endTime + "&packageCode=" + packageCode + "&afterSaleDetectCode=" + afterSaleDetectCode + "&productName=" 
			+ productName + "&imeiCode=" + imeiCode + "&status=" + status + "&productCode=" + productCode 
			+ "&productType=" + productType + "&supplier=" + supplier + "&orderCode=" + orderCode + "&areaId="+areaId
			+ "&startTime_detect=" + startTime_detect + "&endTime_detect=" + endTime_detect + "&parentId1=" + parentId1;
}
function clearFun() {
	$('#startTime_detect').datebox('setValue',"");
	$('#endTime_detect').datebox('setValue',"");
	$('#startTime').datebox('setValue',"");
	$('#endTime').datebox('setValue',"");
	$('#tb input[name=packageCodes]').val('');
	$('#tb input[name=afterSaleDetectCode]').val('');
	$('#tb input[name=productName]').val('');
	$('#tb input[name=imeiCode]').val('');
	$('#tb input[name=productCode]').val('');
	//$('#declareStatusQ').combobox('setValue','');
	$('#productType').combobox('setValue','');
	$('#statusQ').combobox('setValue','');
	$('#supplierQ').combobox('setValue','');
	$('#areaId').combobox('setValue','');
	$('#parentId1').combobox('setValue','');
	datagrid.datagrid('load', {});
}
</script>
</head>
<body>
	<div id="tb"  style="height: auto;display: none;">
		<fieldset>
			<legend>筛选</legend>
			<table border="0">
				<tr>
					<th align="right">寄回厂商：</th>
					<td><input id="supplierQ" name="supplier"  editable="false" style="width: 121px;"/></td>
					<th align="right">IMEI：</th>
					<td><input id="imeiCode" name="imeiCode" style="width: 120px;"/></td>
					<th >售后地区：</th>
					<td align="left"><input id="areaId" name="areaId" style="width: 121px" /></td>
					<th align="right">发货日期：</th>
					<td colspan="1">
						<input id="startTime" name="startTime" class="easyui-datebox" editable="false" style="width: 120px;" /> -
						<input id="endTime" name="endTime" class="easyui-datebox" editable="false" style="width: 120px;" /></td>
				</tr>
				<tr>
					<th align="right">一级分类： </th>
					<td><input id="parentId1" name="parentId1" editable="false" style="width: 120px;"/></td>
					<th align="right">商品编号：</th>
					<td><input id="productCode" name="productCode" style="width: 120px;"/></td>
					<!--  <th align="right">申报状态：</th>
					<td><input id="declareStatusQ" name="declareStatus" editable="false" style="width: 120px;"/></td>
					-->
					<th align="right">商品名称：</th>
					<td><input id="productName" name="productName" style="width: 114px;"/></td>
				</tr>
				<tr>
					<th align="right">状态： </th>
					<td><input id="statusQ" name="status" editable="false" style="width: 120px;"/></td>
					<th align="right">运输单号：</th>
					<td><input id="packageCode" name="packageCodes" style="width: 120px;"/></td>
					<th align="right">商品类型：</th>
					<td><input id="productType" name="productType" editable="false" style="width: 120px;"/></td>
					<th align="right">检测日期：</th>
					<td colspan="1">
						<input id="startTime_detect" name="startTime_detect" class="easyui-datebox" editable="false" style="width: 120px;" /> -
						<input id="endTime_detect" name="endTime_detect" class="easyui-datebox" editable="false" style="width: 120px;" /></td>
				</tr>
				<tr>
					<th align="right">订单编号： </th>
					<td colspan="3"><textarea id="orderCode" name="orderCode" style="width: 200px;height: 30px"></textarea></td>
					<th align="right">售后处理单号：</th>
					<td colspan="2"><textarea id="afterSaleDetectCode" name="afterSaleDetectCode" style="width:200px;height: 30px"></textarea></td>
					<td align="right">
						<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查找</a>
						<a class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="clearFun();" href="javascript:void(0);">重置</a>
					</td>
				</tr>
			</table>
		</fieldset>
		<div align="left">
			<a class="easyui-linkbutton" iconCls="icon-redo" onclick="excel();" plain="true" href="javascript:void(0);">返厂商品导出</a> 
		</div>
	</div>
	<table id="datagrid"></table> 
</body>
</html>