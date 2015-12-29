<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<script type="text/javascript">
var datagrid;
function getList() {
	var areaId = $("#areaId").combobox('getValue');
	if (areaId == null || areaId == -1) {
		alert("请选择仓库");
		$('#areaId').focus();
		return;
	}
	var startDate = $("#startDate").datebox('getValue');
	var endDate = $("#endDate").datebox('getValue');
	if (startDate == '' || endDate == '') {
		alert('请选择日期');
		return;
	}
	if(!validateDate(endDate,startDate)){
		alert('开始日期必须小于结束日期');
		$('#startDate').focus();
		return;
	}
	datagrid = $("#list").datagrid({
		title:"人力基础数据列表",
		idField : 'id',
		iconCls:'icon-ok',
		fitColumns:true,		
		height:500,
		pageNumber:1,
		pageSize:31,
		pageList:[31],
		url:'${pageContext.request.contextPath}/BIStoreController/getBIBaseCountList.mmx',
		queryParams:{
			startDate: '' + startDate,
			endDate: '' + endDate,
			areaId: '' + areaId
		},
		showFooter:true,
		striped:true,
		collapsible:true,
		loadMsg:'数据加载中...',
		rownumbers:true,
		singleSelect:true,//只选择一行后变色
		pagination:true,
		columns:[[
		           {field:'datetime', title:'日期', align:'center', rowspan : 2,
			        	formatter: function(value, row, index){
			        		return value.substring(0,10);
			        	}},
		          {title:'在职总人数', colspan:4, align:'center'},
		          {title:'在岗总人数', colspan:4, align:'center'},
			      {field:'tempCount',title:'临时工',align:'center', rowspan : 2 }  
		          ], [
			       
			        {field:'inTotal', title:'总人数', align:'center', formatter : function(v,r,i) {
			        	return '<span style="font-weight:bold;">'+ v +'</span>';
			        }},
			        {field:'inWare',title:'物流中心',align:'center'},
			        {field:'inDelivery',title:'配送部',align:'center'},
			        {field:'inAdmin',title:'职能',align:'center'},
			        {field:'onTotal',title:'总人数',align:'center', formatter : function(v,r,i) {
			        	return '<span style="font-weight:bold;">'+ v +'</span>';
			        }},
			        {field:'onWare',title:'物流中心',align:'center'},
			        {field:'onDelivery',title:'配送部',align:'center'},		        
			        {field:'onAdmin',title:'职能',align:'center'}  
			]]
	});
}

$(function(){
	$('#areaId').combobox({
	    url:'${pageContext.request.contextPath}/BIStoreController/getBIAllArea.mmx',
	    valueField:'id',
	    textField:'text',
	    editable:false
	});
});	
</script>
<title>人力基础数据</title>
</head>
<body>
<div>
		<fieldset>
			<legend>录入在职人力</legend>
			<table>
				<tr>
					<td><span style="font-size: 12px;">仓库&nbsp;&nbsp;&nbsp;</span><input id="areaId" style="width: 80px;" /></td>
					<td><span style="font-size: 12px;">日期&nbsp;</span><input class="easyui-datebox" editable="false" id="startDate">--</td>
					<td><input class="easyui-datebox" editable="false" id="endDate"></td>
					<td><a class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-search'" onclick="javascript:getList();">查询</a></td>
				</tr>
			</table>
		</fieldset>
		<table id="list"></table>
</div>
</body>
</html>