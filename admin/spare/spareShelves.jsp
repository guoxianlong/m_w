<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/timeAndOther.js"></script>
<title>备用机上架任务列表</title>
<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/spareManagerController/spareShelves.mmx',
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
	    pageList : [ 2, 6, 8, 12, 15, 18, 20, 22, 28, 30 ],
	    frozenColumns : [[
	                      {field:'id',title:'ID',hidden:true},
	                      {field:'productCode',title:'商品编号',width:$(this).width() * 0.15,align:'center'},
	                      {field:'oriname',title:'商品原名称',width:$(this).width() * 0.25,align:'center'}  ,
	  	                 ]],
	    columns:[[ 
				{field:'spareCode',title:'备用机单号',width:$(this).width() * 0.15,align:'center'},
				{field:'supplierName',title:'供应商名称',width:$(this).width() * 0.15,align:'center'},  
				{field:'areaName',title:'售后地区',width:$(this).width() * 0.15,align:'center'},
				{field:'createDatetime',title:'生成时间',width:$(this).width() * 0.15,align:'center',
					formatter : function(value,rowData,rowIndex){
	        		if(value != null){
	        			return value.substring(0,19);
	        		}
        		}},
				]],
				onLoadSuccess : function(data){
					if(data.rows.length==0 && data.tip!=null){
						$.messager.show({
							msg : data.tip,
							title : '提示'
						});
					}
			    }
	}); 
	$("#areaId").combobox({
		url : '${pageContext.request.contextPath}/Combobox/getSpareArea.mmx',
		valueField:'id',
		textField:'text',
		delay:500
	});
});
function searchFun() {
	var spareCode = $('#spareCode').val();
	var productCode = $('#productCode').val();
	var startTime =$('#startTime').datebox('getValue');
	var endTime = $('#endTime').datebox('getValue');
	if(spareCode=='' && productCode=='' && startTime=='' && endTime==''){
		$.messager.show({
			msg : '请输入售后地区以外的查询条件!',
			title : '提示'
		});
		return;
	}else{
		if(startTime!=''&& endTime!=''){
			var days = getValidateSubDays(endTime, startTime);
			if (days < 0) {
				$.messager.show({
					msg : "生成入库单查询的开始时间不能早于结束时间!",
					title : '提示'
				});
				return false;
			}
		}else if((startTime==''&& endTime!='') || (startTime!=''&& endTime=='')){
			$.messager.show({
				msg : "开始时间、结束时间必须成对出现!",
				title : '提示'
			});
			return false;
		}
	}
	
	datagrid.datagrid('load', {
		spareCode : $('#spareCode').val(),
		productCode : $('#productCode').val(),
		startTime :$('#startTime').datebox('getValue'),
		endTime : $('#endTime').datebox('getValue'),
		areaId :$('#areaId').combobox('getValue'),
	});
}
</script>
</head>
<body>
<table id="datagrid"></table> 
<div id="tb" style="height: auto;">
		<fieldset>
			<legend>备用机上架任务列表</legend>
			<form id="applyFrom" method="post">
			<table class="" >
				<tr align="center" >
					<th >备用机单号</th>
					<td align="left"><input id="spareCode" name="spareCode" style="width: 116px" /></td>
					<th >商品编号</th>
					<td align="left"><input id="productCode" name="productCode" style="width: 116px" /></td>
				   <th >生成时间</th>
					<td align="left">
						<input type="text" id="startTime" class="easyui-datebox" style="width:121px" name="startTime"/>
						--<input type="text"  name="endTime" id="endTime" class="easyui-datebox" style="width:121px"/></td>
					<th >售后地区</th>
					<td align="left"><input id="areaId" name="areaId" style="width: 121px" /></td>
					<td align="right" >
						<a class="easyui-linkbutton" iconCls="icon-search"  onclick="searchFun();" href="javascript:void(0);">查找</a>
					</td>
				</tr>
			</table>
		</form>
		</fieldset>
		
	</div>
</body>
</html>