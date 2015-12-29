<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<title>等待返厂商品列表</title>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var flag_one;
var flag_two;
var applyFrom;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getWaitBackSupplierProductDatagrid.mmx',
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
	                      	{field:'id',title:'ID',hidden:true},
	          				{field:'code',title:'售后处理单号',width: 170,align:'center',
	          					formatter : function(value, row, index) {
	        				    	return '<a href="javascript:void(0);" class="editbutton" onclick="afterSaleDetectProduct('+row.afterSaleDetectProductId+')">'+row.code+'</a>';
	        					}},
	  	                 ]],
	    columns:[[ 
			{field:'afterSaleOrderCode',title:'售后单号',width:80,align:'center',
				formatter : function(value, row, index) {
				    return '<a href="javascript:void(0);" class="editbutton" onclick="afterSaleOrderId('+row.afterSaleOrderId+')">'+row.afterSaleOrderCode+'</a>';
				}},  
			{field:'cargoWholeCode',title:'货位号',width:50,align:'center'},
			{field:'shopName',title:'小店名称',width:80,align:'center'}  ,
			{field:'productCode',title:'商品编号',width:50,align:'center'},
			{field:'statusName',title:'状态',width:40,align:'center'},
			{field:'faultDescription',title:'故障描述',width:70,align:'center'},
			{field:'userName',title:'最后操作人',width:50,align:'center'},
			{field:'createDatetime',title:'最后操作时间',width:55,align:'center', formatter : function(value, rowData, rowIndex) {  
     
	  if(rowData.createDatetime ==null){
	    	return "";
	    }else{ 
	    	return  rowData.createDatetime.substring(0,16);  
	    }   
	
		} }
	]],
	
	}); 
	applyFrom = $('#applyFrom').form();
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
		onSelect : function(record){
			flag_one = true;
			$('#parentId2').combobox({
				url : '${pageContext.request.contextPath}/Combobox/getParentId2.mmx?parentId1=' + record.id,
				valueField : 'id',
				textField : 'text',
				editable : false,
				onSelect : function(record){
					flag_two = true;
					$('#parentId3').combobox({
						url : '${pageContext.request.contextPath}/Combobox/getParentId3.mmx?parentId2='+record.id,
						valueField : 'id',
						textField : 'text',
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
		parentId2 = $('#parentId2').combobox('getValue');
	}
	if(flag_two){
		parentId3 = $('#parentId3').combobox('getValue');
	}
	datagrid.datagrid('load', {
		code : $('#tb input[id=code]').val(),
		content : $('#tb input[id=content]').val(),
		areaId : $('#areaId').combobox('getValue'),
		status : $('#status').combobox('getValue'),
		parentId1 : $('#parentId1').combobox('getValue'),
		parentId2 : parentId2,
		parentId3 : parentId3,
		afterSaleOrderCode : $('#tb input[id=afterSaleOrderCode]').val(),
	});
}
function afterSaleDetectProduct(id){ 
	window.location.href ='${pageContext.request.contextPath}/admin/afStock/afterSaleDetectProductInfo.jsp?id='+id;
}
function afterSaleOrderId(id){ 
	window.location.href ='https://sales.ebinf.com/sale/admin/toEdit.mmx?id='+id;

}
function excel(){
	var afterSaleDetectCode =  $('#tb input[name=code]').val();
	var productCode =  $('#tb input[name=content]').val();
	var afterSaleOrderCode =  $('#tb input[name=afterSaleOrderCode]').val();
	var areaId = $('#areaId').combobox('getValue');
	var status = $('#status').combobox('getValue');
	var parentId1 = $('#parentId1').combobox('getValue');
	var parentId2 = '';
	var parentId3 = '';
	if(flag_one){
		parentId2 = $('#parentId2').combobox('getValue');
	}
	if(flag_two){
		parentId3 = $('#parentId3').combobox('getValue');
	}
	location.href = "${pageContext.request.contextPath}/admin/AfStock/excelSupplierProduct.mmx?status=0&afterSaleDetectCode=" + afterSaleDetectCode
		+ "&productCode=" + productCode + "&afterSaleOrderCode=" + afterSaleOrderCode + "&areaId=" + areaId + "&parentId1=" + parentId1 
		+ "&parentId2=" + parentId2 + "&parentId3=" + parentId3 + "&status=" + status;
}
</script>
</head>
<body>
<table id="datagrid"></table> 
<div id="tb" style="height: auto;">
		<fieldset>
			<legend>等待返厂商品列表</legend>
			<form id="applyFrom" method="post">
			<table class="tableForm" >
				<tr align="center" >
				 
					<th >售后处理单号</th>
					<td align="left">
						<input id="code" name="code" style="width: 116px"  /></td>
					<th >商品编号</th>
					<td align="left">
						<input id="content" name="content" style="width: 116px"  /></td>
					<th >返厂状态</th>
					<td colspan="1">
						<select id="status" class="easyui-combobox" name="status" style="width:121px;">   
						    <option value="-1"></option>   
						    <option value="3">等待返厂</option>   
						    <option value="6">检测不合格</option>   
						</select>  
					</td>	
				</tr>
				<tr align="center" >
				 
					<th >售后地区</th>
					<td align="left">
						<input id="areaId" name="areaId" style="width: 121px" /></td>
					<th >售后单号</th>
					<td align="left">
						<input id="afterSaleOrderCode" name="afterSaleOrderCode" style="width: 116px"  /></td>
					<th align="right">商品分类</th>
					<td colspan="1">
						<input id="parentId1" name="parentId1" style="width: 121px;"/>
						<input id="parentId2" name="parentId2" style="width: 121px;"/>
						<input id="parentId3" name="parentId3"  style="width: 121px;"/>
					</td>	
					<td align="right" >
						<a class="easyui-linkbutton" iconCls="icon-search"  onclick="searchFun();" href="javascript:void(0);">查找</a>
						<a class="easyui-linkbutton" iconCls="icon-redo" onclick="excel();" plain="true" href="javascript:void(0);">导出</a>
					</td>
				</tr>
			</table>
		</form>
		</fieldset>
		
	</div>
</body>
</html>