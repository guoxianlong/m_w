<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<title>厂商寄回商品上架任务列表</title>
<script type="text/javascript" charset="UTF-8">
var datagrid;

var applyFrom;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getSupplierProductgrid.mmx',
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
	          			{field:'productCode',title:'商品编号',width:$(this).width() * 0.05,align:'center'},
	          			
	  	                 ]],
	    columns:[[ 
				{field:'shopName',title:'小店名称',width:$(this).width() * 0.25,align:'center'}  ,
				{field:'code',title:'售后处理单号',width:$(this).width() * 0.15,align:'center'},
				{field:'handling',title:'处理意见',width:$(this).width() * 0.15,align:'center'},
				{field:'handlingstatus',title:'处理单状态',width:$(this).width() * 0.15,align:'center'},
				{field:'afterSaleOrderCode',title:'售后单号',width:$(this).width() * 0.15,align:'center'},  
				{field:'cargoWholeCode',title:'货位号',width:$(this).width() * 0.15,align:'center'},
				{field:'createDatetime',title:'发货日期',width:$(this).width() * 0.15,align:'center', 
					formatter : function(value, rowData, rowIndex) {  
					  if(rowData.returnDatetime !=null){
						  return  rowData.createDatetime.substring(0,10); 
					  }   
					} },
				{field:'returnDatetime',title:'厂商寄回时间',width:$(this).width() * 0.15,align:'center', 
					formatter : function(value, rowData, rowIndex) {  
					  if(rowData.returnDatetime !=null){
						  return  rowData.returnDatetime.substring(0,16);  
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
});
function searchFun() {
	datagrid.datagrid('load', {
		code: $('#tb input[id=code]').val(),
		content: $('#tb input[id=content]').val(),
		startTime :  $('#startTime').datebox('getValue'),
		endTime : $('#endTime').datebox('getValue'),
		startTime_send :  $('#startTime_send').datebox('getValue'),
		endTime_send : $('#endTime_send').datebox('getValue'),
		areaId : $('#areaId').combobox('getValue')
	});
}

function excel(){
	var startTime = $('#startTime').datebox('getValue');
	var endTime = $('#endTime').datebox('getValue');
	var startTime_send =  $('#startTime_send').datebox('getValue');
	var endTime_send = $('#endTime_send').datebox('getValue');
	var afterSaleDetectCode =  $('#tb input[name=code]').val();
	var productCode =  $('#tb input[name=content]').val();
	var areaId = $('#areaId').combobox('getValue');
	location.href = "${pageContext.request.contextPath}/admin/AfStock/excelSupplierProduct.mmx?afterSaleDetectCode=" + afterSaleDetectCode
		+ "&productCode=" + productCode + "&startTime=" + startTime + "&endTime=" + endTime + "&areaId=" + areaId + "&startTime_send=" + startTime_send
		+ "&endTime_send=" + endTime_send;
}
</script>
</head>
<body>
<table id="datagrid"></table> 
<div id="tb" style="height: auto;">
		<fieldset>
			<legend>厂商寄回商品上架任务列表</legend>
			<form id="applyFrom" method="post">
			<table class="" >
				<tr align="center" >
				  	<th >厂商寄回时间</th>
					<td align="left">
						<input type="text" id="startTime" class="easyui-datebox" style="width:121px" name="startTime"/>
						--<input type="text"  name="endTime" id="endTime" class="easyui-datebox" style="width:121px"/></td>
					<th >售后处理单号</th>
					<td align="left">
						<input id="code" name="code" style="width:116px" /></td>
					<th >商品编号</th>
					<td align="left">
						<input id="content" name="content" style="width:116px" /></td>
				  	<th >售后地区</th>
					<td align="left">
					<input id="areaId" name="areaId" style="width: 121px" /></td>
						
					<td align="right" >
						<a class="easyui-linkbutton" iconCls="icon-search"  onclick="searchFun();" href="javascript:void(0);">查找</a>
						<a class="easyui-linkbutton" iconCls="icon-redo" onclick="excel();" plain="true" href="javascript:void(0);">导出</a>
					</td>
				</tr>
				<tr align="center" >
				  	<th >发货日期</th>
					<td align="left">
						<input type="text" id="startTime_send" class="easyui-datebox" style="width:121px" name="startTime_send"/>
						--<input type="text"  name="endTime_send" id="endTime_send" class="easyui-datebox" style="width:121px"/></td>
				</tr>
			</table>
		</form>
		</fieldset>
		
	</div>
</body>
</html>