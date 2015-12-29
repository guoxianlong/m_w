<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var applyDialog;
var applyFrom;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/CargoInfoCity/getCargoInfoCitys.mmx',
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
	    pageSize : 2,
	    pageList : [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ],
	    frozenColumns : [[
					{field:'id',title:'ID',width:20,hidden:true},
					{field:'code',title:'code',width:200,align:'center',sortable : true},
	                 ]],
	    columns:[[  
	        {field:'name',title:'城市名称',width:30,align:'center',sortable : true},  
	        {field:'areaCount',title:'下属地区数量',width:30,align:'center'},  
	    ]],
	    onRowContextMenu : function(e, rowIndex, rowData) {
			e.preventDefault();
			$(this).datagrid('unselectAll');
			$(this).datagrid('selectRow', rowIndex);
			$('#menu').menu('show', {
				left : e.pageX-1,
				top : e.pageY-1
			});
		}
	}); 
	applyFrom = $('#applyFrom').form();
	applyDialog = $('#applyDialog').show().dialog({
		modal : true,
		minimizable : true,
		title : '新增城市',
		buttons : [{
			text : '确定',
			handler : function() {
				applyFrom.form('submit', {
					url : '${pageContext.request.contextPath}/CargoInfoCity/addCargoInfoCity.mmx',
					success : function(data) {
						console.info(data);
						var d = $.parseJSON(data);
						if (d) {
							applyDialog.dialog('close');
							$.messager.show({
								msg : d.msg,
								title : '提示'
							});
							datagrid.datagrid('reload');
						}
					}
				});
			}
		}]
	}).dialog('close');
});
function searchFun() {
	datagrid.datagrid('load', {
		code : $('#tb input[name=code]').val(),
		name : $('#tb input[name=name]').val(),
	});
}
function appendFun(){
	applyDialog.dialog('open');
}
function clearFun() {
	$('#tb input').val('');
	datagrid.datagrid('load', {});
}
</script>
</head>
<body>
	<table id="datagrid"></table> 
	<div id="applyDialog" style="overflow-y:auto; overflow-x:auto; display: none;">
		<form id="applyFrom" method="post">
			<table class="tableForm" >
				<tr>
					<th width="70px" align="center">城市代号:</th> 
					<td><input name="code" type="text" class="easyui-validatebox" required="required" style="width: 156px;" /></td>
					<th width="70px" align="center">城市名称:</th> 
					<td><input name="name" type="text" class="easyui-validatebox" required="required" style="width: 156px;" /></td>
				</tr>
			</table>
		</form>
	</div>
	<div id="tb"  style="height: auto;display: none;">
		<fieldset>
			<legend>筛选</legend>
			<table class="tableForm" >
				<tr align="center" >
					<th >城市代号</th>
					<td align="left">
						<input id="code" name="code" /></td>
					<th >城市名称</th>
					<td align="left">
						<input id="name" name="name"  /></td>
					<td align="right" >
						<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查找</a>
						<a class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="clearFun();" href="javascript:void(0);">重置</a></td>
				</tr>
			</table>
		</fieldset>
		<div>
			<a class="easyui-linkbutton" iconCls="icon-add" onclick="appendFun();" plain="true" href="javascript:void(0);">新增城市</a>
		</div>
	</div>
	<div id="menu" class="easyui-menu" style="width:100px;display: none;">
		<div onclick="appendFun();" iconCls="icon-add">新增城市</div>
		<mmb:permit value="939">
		</mmb:permit>
	</div>
</body>
</html>