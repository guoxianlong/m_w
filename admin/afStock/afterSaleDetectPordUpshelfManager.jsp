<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<title>已检测商品上架任务列表</title>
<script type="text/javascript" charset="UTF-8">
var datagrid;

var applyFrom;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getProductUpshelfgrid.mmx',
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
	          			{field:'productCode',title:'商品编号',width:$(this).width() * 0.05,align:'center'},
	          			
	  	                 ]],
	    columns:[[ 
			{field:'shopName',title:'小店名称',width:$(this).width() * 0.25,align:'center'}  ,
			{field:'afterSaleOrderCode',title:'售后单号',width:$(this).width() * 0.15,align:'center'},
			{field:'afterSaleStatus',title:'售后单状态',width:$(this).width() * 0.10,align:'center'},  
	        {field:'afterSaleCode',title:'售后处理单号',width:$(this).width() * 0.15,align:'center'},
	        {field:'checkTime',title:'检测时间',width:$(this).width() * 0.10,align:'center', formatter : function(value, rowData, rowIndex) {  
	            if(rowData == null || rowData.checkTime == null)
	            	return "";
	        	return  rowData.checkTime.substring(0,16);  
	      } },
	      {field:'backSupplierStatus', title:'返厂状态', width:$(this).width() * 0.10, align:'center' },
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
	if(nDifTime < 0){
		$.messager.alert('提示', '起始日期不能大于结束日期!', 'error');
    	return false;
	}
	datagrid.datagrid('load', {
		code : $('#tb input[id=code]').val(),
		content : $('#tb input[id=content]').val(),
		startTime :  $('#startTime').datebox('getValue'),
		endTime : $('#endTime').datebox('getValue'),
		areaId : $('#areaId').combobox('getValue')
	});
	
}
function excel(){
	var startTime= $('#startTime').datebox('getValue');
	var endTime=$('#endTime').datebox('getValue');
	var afterSaleDetectCode =  $('#tb input[name=code]').val();
	var productCode = $('#tb input[name=content]').val();
	var areaId = $('#areaId').combobox('getValue');
	location.href = "${pageContext.request.contextPath}/admin/AfStock/excelProductUpshelf.mmx?startTime=" 
			+ startTime + "&endTime=" + endTime + "&afterSaleDetectCode=" + afterSaleDetectCode + "&productCode=" + productCode
			+ "&areaId=" + areaId;
}
</script>
</head>
<body>
<table id="datagrid"></table> 
<div id="tb" style="height: auto;">
		<fieldset>
			<legend>已检测商品上架任务列表</legend>
			<form id="applyFrom" method="post">
			<table class="" >
			<tr align="center" >
				  <th >检测完成时间</th>
					<td align="left">
						<input type="text" id="startTime" class="easyui-datebox" style="width:121px" name="startTime"/>
						--<input type="text"  name="endTime" id="endTime" class="easyui-datebox" style="width:121px"/></td>
					<th >售后处理单号</th>
					<td align="left">
						<input id="code" name="code" style="width: 116px" /></td>
					<th >商品编号</th>
					<td align="left">
						<input id="content" name="content" style="width: 116px" /></td>
					<th >售后地区</th>
					<td align="left">
						<input id="areaId" name="areaId" style="width: 121px" /></td>
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