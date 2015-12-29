<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>用户配件货位列表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/fittingController/getFittingDatagrid.mmx?stockType=12',
	    toolbar : '#tb',
	    idField : 'fittingId',
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
	    columns : [[	    	
	    	{field:"cargoCode",title:'货位号',align:'center',width:120},
	    	{field:"fittingCode",title:'配件编号',align:'center',width:120},
	    	{field:"fittingName",title:'配件名称',align:'center',width:120},
	    	{field:"stockCount",title:'当前货位库存（其中冻结量）',align:'center',width:120},
	    ]]
	}); 
	$('#areaId').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getSignArea.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
});
function searchFun() {
	datagrid.datagrid('load', {
		cargoCode : $('#cargoCode').val(),
		fittingCode : $('#fittingCode').val(),
		fittingName : $('#fittingName').val(),
		areaId : $('#areaId').combobox('getValue'),
		shelfCode : $('#shelfCode').val(),
		floorNum : $('#floorNum').val()
	});
}
</script>
</head>
<body>
	<div id="tb" style="height: auto;">
		<fieldset>
			<legend>货位列表</legend>
			<form id="applyFrom" method="post">
			<table class="" >
				<tr align="center">
					<th align="right">货位号：</th>
					<td><input type="text" id="cargoCode" name="cargoCode" style="width: 116px" /></td>
					<th align="right">配件编号：</th>
					<td><input type="text" id="fittingCode" name="fittingCode"  style="width: 116px" /></td>
					<th align="right">配件名称：</th>
					<td><input type="text" id="fittingName" name="fittingName" style="width: 116px" /></td>
				</tr>
				<tr align="center" >
					<th align="right">库地区：</th>
					<td><input id="areaId" name="areaId" style="width: 121px" /></td>
					<th align="right">货架代号：</th>
					<td><input id="shelfCode" name="shelfCode" style="width: 116px"  /></td>
					<th>第几层：</th>
					<td><input type="text" id="floorNum" name="floorNum" style="width: 116px" /></td>
					<td><a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查询</a></td>
				</tr>
			</table>
		</form>
		</fieldset>
	</div>
	<table id="datagrid"></table> 
</body>
</html>