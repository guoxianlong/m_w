<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<title>待检测包裹列表</title>
<script type="text/javascript" charset="UTF-8">
var datagrid;

var applyFrom;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getDetectPackAgegrid.mmx',
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
          				{field:'packageCode',title:'包裹单号',width:$(this).width() * 0.15,align:'center'},
        ]],
	    columns:[[  
			{field:'createDatetime',title:'签收时间',width:$(this).width() * 0.15,align:'center', formatter : function(value, rowData, rowIndex) {  
				  if(rowData.createDatetime ==null){
				    	return "";
				    }else{ 
				    	return  rowData.createDatetime.substring(0,16);  
				    }   
			} },
			{field:'createUserName',title:'签收人',width:$(this).width() * 0.15,align:'center'}  ,
			{field:'afterSaleOrderCodes',title:'售后单号',width:$(this).width() * 0.15,align:'center'},  
	        {field:'orderCode',title:'订单号',width:$(this).width() * 0.15,align:'center'}
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
	var startTime= $('#startTime').datebox('getValue');
	var endTime=$('#endTime').datebox('getValue');
	var nDay_ms = 24*60*60*1000;
	var reg = new RegExp("-","g");
	var startDay = new Date(startTime.replace(reg,'-'));
	var endDay = new Date(endTime.replace(reg,'-'));
	var nDifTime = endDay.getTime()- startDay.getTime();
	var areaId;
	if($('#areaId').combobox('getValue') != ''){
		areaId = $('#areaId').combobox('getValue');
	}
	if(nDifTime < 0){
		$.messager.alert('提示', '起始日期不能大于结束日期!', 'error');
    	return false;
	}
	datagrid.datagrid('load', {
		orderCode : $('#tb input[id=orderCode]').val(),
		afterSaleCode : $('#tb input[id=afterSaleCode]').val(),
		packageCode : $('#tb input[id=packageCode]').val(),
		createUserName : $('#tb input[id=createUserName]').val(),
		areaId : areaId,
		startTime :  $('#startTime').datebox('getValue'),
		endTime : $('#endTime').datebox('getValue'),
	});
	
}

function excel(){
	var startTime= $('#startTime').datebox('getValue');
	var endTime=$('#endTime').datebox('getValue');
	var areaId = $('#areaId').combobox('getValue');
	var orderCode = $('#tb input[id=orderCode]').val();
	var afterSaleCode = $('#tb input[id=afterSaleCode]').val();
	var packageCode = $('#tb input[id=packageCode]').val();
	var createUserName = $('#tb input[id=createUserName]').val();
	location.href = "${pageContext.request.contextPath}/admin/AfStock/excelDetectPackageProduct.mmx?startTime=" + 
		startTime + "&endTime=" + endTime + "&orderCode=" + orderCode + "&afterSaleCode=" + afterSaleCode + "&createUserName=" 
		+ createUserName + "&areaId=" + areaId;
}
</script>
</head>
<body>
<table id="datagrid"></table> 
<div id="tb" style="height: auto;">
		<fieldset>
			<legend>待检测包裹列表</legend>
			<form id="applyFrom" method="post">
			<table class="" >
				<tr align="center" >
					<th >订单编号</th>
					<td align="left">
						<input id="orderCode" name="orderCode" style="width: 116px" /></td>
					<th >售后单号</th>
					<td align="left">
						<input id="afterSaleCode" name="afterSaleCode" style="width: 116px" /></td>
					<th >售后地区</th>
					<td align="left">
						<input id="areaId" name="areaId" style="width: 121px" /></td>
				</tr>
				<tr align="center" >
					<th >包裹单号</th>
					<td align="left">
						<input id="packageCode" name="packageCode" style="width: 116px" /></td>
					<th >签收人</th>
					<td align="left">
						<input id="createUserName" name="createUserName" style="width: 116px" /></td>
				    <th >签收时间</th>
					<td align="left">
						<input type="text" id="startTime" class="easyui-datebox" readonly  style="width:121px" name="startTime"/>--
						<input type="text"   name="endTime" id="endTime" class="easyui-datebox" style="width:121px"/></td>
						
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