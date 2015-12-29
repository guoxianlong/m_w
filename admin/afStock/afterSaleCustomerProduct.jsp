<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>客户库商品列表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var flag_one;
var flag_two;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getCustomerAfterSaleProductDatagrid.mmx',
	    queryParams : {
	    	stockType : '10',
	    },
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
					{field:'productCode',title:'商品编号',width:120,align:'center'}
	    ]],
	    columns:[[  
	        {field:'productName',title:'小店名称',width:40,align:'center'},  
	        {field:'productOriName',title:'原名称',width:40,align:'center'},  
	        {field:'wholeCode',title:'货位编号',width:25,align:'center'},  
	        {field:'content',title:'故障描述',width:25,align:'center'}, 
	        {field:'afterSaleDetectCode',title:'售后处理单号',width:25,align:'center'},  
	        {field:'statusName',title:'售后处理状态',width:20,align:'center'},
	        {field:'fitting',title:'配件名称',width:25,align:'center'},
	        {field:'afterSaleCode',title:'售后单号',width:30,align:'center'},  
	        {field:'createDatetime',title:'入库日期',width:25,align:'center',
	        	formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return value.substring(0,19);
	        		}
        		}},  
	    ]]
	});
	 
	$('#areaId').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getSignArea.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
	
	$('#parentId1').combotree({
      	url : '${pageContext.request.contextPath}/Combobox/getParentId1.mmx',
      	valueField:'id',
		textField:'text',
		multiple:true,
		editable : false,
		onCheck : function(node, checked){
			flag_one = true;
			var nodes = $('#parentId1').combotree('getValues');	// get checked nodes
			$('#parentId2').combotree({
				url : '${pageContext.request.contextPath}/Combobox/getParentId2s.mmx?parentId1s=' + nodes.join(","),
				valueField : 'id',
				textField : 'text',
				multiple:true,
				editable : false,
				onCheck : function(record){
					flag_two = true;
					nodes = $('#parentId2').combotree('getValues');
					$('#parentId3').combotree({
						url : '${pageContext.request.contextPath}/Combobox/getParentId3s.mmx?parentId2s=' + nodes.join(","),
						valueField : 'id',
						textField : 'text',
						multiple:true,
						editable : false
					});
				}
			});
		} 
    });
});
function searchFun() {
	var parentId2;
	var parentId3;
	if(flag_one){
		var nodes = $('#parentId2').combotree('getValues');
		parentId2 = nodes.join(",");
	}
	if(flag_two){
		var nodes = $('#parentId3').combotree('getValues');
		parentId3 = nodes.join(",");
	}
	datagrid.datagrid('load', {
		stockType:10,
		startTime : $('#startTime').datebox('getValue'),
		endTime : $('#endTime').datebox('getValue'),
		afterSaleCode :  $('#tb input[name=afterSaleCode]').val(),
		afterSaleDetectCode :  $('#tb input[name=afterSaleDetectCode]').val(),
		areaId : $('#areaId').combobox('getValue'),
		parentId1 : $('#parentId1').combotree('getValues').join(","),
		parentId2 : parentId2,
		parentId3 : parentId3,
		productCode :  $('#tb input[name=productCode]').val(),
		customerPhone :$("#customerPhone").val(),
		saleType : $("#saleType").val(),
	});
}
function clearFun() {
	$('#tb input[name=productCode]').val('');
	$('#tb input[name=afterSaleCode]').val('');
	$('#tb input[name=afterSaleDetectCode]').val('');
	$('#areaId').combobox('setValue','');
	$('#startTime').datebox('setValue','');
	$('#endTime').datebox('setValue','');
	$("#customerPhone").val('');
	$("#saleType").val('0');
	datagrid.datagrid('load', {stockType:10});
}
function excel(){
	var startTime = $('#startTime').datebox('getValue');
	var endTime = $('#endTime').datebox('getValue');
	var productCode =  $('#tb input[name=productCode]').val();
	var afterSaleCode =  $('#tb input[name=afterSaleCode]').val();
	var afterSaleDetectCode =  $('#tb input[name=afterSaleDetectCode]').val();
	var areaId = $('#areaId').combobox('getValue');
	var parentId1 =  $('#parentId1').combotree('getValues').join(",");
	var parentId2 = '';
	var parentId3 = '';
	if(flag_one){
		parentId2 = $('#parentId2').combotree('getValues').join(",");
	}
	if(flag_two){
		parentId3 = $('#parentId3').combotree('getValues').join(",");
	}
	
	var customerPhone = $("#customerPhone").val();
	var saleType = $("#saleType").val();
	location.href = "${pageContext.request.contextPath}/admin/AfStock/excelCustomerProduct.mmx?startTime=" + 
		startTime + "&endTime=" + endTime + "&productCode=" + productCode + "&afterSaleDetectCode=" + afterSaleDetectCode + "&afterSaleCode=" 
			+ afterSaleCode + "&areaId=" + areaId + "&parentId1=" + parentId1 + "&parentId2=" + parentId2 + "&parentId3=" + parentId3 
			+ "&customerPhone=" + customerPhone + "&saleType=" + saleType + "&stockType=10";
}
</script>
</head>
<body>
	<div id="tb"  style="height: auto;display: none;">
		<fieldset>
			<legend>筛选</legend>
			<table border="0">
				<tr>
					<th align="right">商品分类：</th>
					<td colspan="7">
						<input id="parentId1" name="parentId1" style="width: 170px;"/>
						<input id="parentId2" name="parentId2" style="width: 170px;"/>
						<input id="parentId3" name="parentId3"  style="width: 170px;"/>
					</td>
				</tr>
				<tr>
					<th align="right">商品编号：</th>
					<td><input id="productCode" name="productCode" style="width: 120px;"/></td>
					<th align="right">手机号：</th>
					<td><input id="customerPhone" name="customerPhone"   style="width: 120px;"/></td>
					<th >售后地区：</th>
					<td align="left">
						<input id="areaId" name="areaId" style="width: 121px" />
						<font >销售属性：</font><select id="saleType" name="saleType" style="width: 56px;">
							<option value="">全部</option>
							<option value="2">经销</option>
							<option value="1">代销</option>
						</select></td>
				</tr>
				<tr>
					<th align="right">售后单号：</th>
					<td><input id="afterSaleCode" name="afterSaleCode" style="width: 120px;"/></td>
					<th align="right">售后处理单号：</th>
					<td><input id="afterSaleDetectCode" name="afterSaleDetectCode" style="width: 120px;"/></td>
					<th align="right">入库日期：</th>
					<td colspan="1">
						<input id="startTime" name="startTime" class="easyui-datebox" editable="false" style="width: 120px;" /> -
						<input id="endTime" name="endTime" class="easyui-datebox" editable="false" style="width: 120px;" /></td>
					<td>
						<a class="easyui-linkbutton" iconCls="icon-redo" onclick="excel();" plain="true" href="javascript:void(0);">导出</a>
						<a class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="clearFun();" href="javascript:void(0);">重置</a>
						<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查询</a>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
	<table id="datagrid"></table> 
</body>
</html>