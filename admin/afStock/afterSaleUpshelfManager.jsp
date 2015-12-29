<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<title>售后库上架任务列表</title>
<script type="text/javascript" charset="UTF-8">
var datagrid;

var applyFrom;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getSaleUpshelfgrid.mmx',
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
	                      {field:'shopName',title:'小店名称',width:$(this).width() * 0.25,align:'center'}  ,
	                     
	  	                 ]],
	    columns:[[ 
				{field:'code',title:'售后处理单号',width:$(this).width() * 0.15,align:'center'},
				{field:'handlingstatus',title:'售后处理单状态',width:$(this).width() * 0.15,align:'center'},  
				{field:'outCargoWholeCode',title:'原货位号',width:$(this).width() * 0.15,align:'center'},
				{field:'inCargoWholeCode',title:'目的货位号',width:$(this).width() * 0.15,align:'center'},
				{field:'createDatetime',title:'任务生成时间',width:$(this).width() * 0.15,align:'center', formatter : function(value, rowData, rowIndex) {  
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
});
function searchFun() {
	datagrid.datagrid('load', {
		code : $('#tb input[id=code]').val(),
		status : $('#status').combobox('getValue'),
		startTime :  $('#startTime').datebox('getValue'),
		endTime : $('#endTime').datebox('getValue'),
		areaId : $('#areaId').combobox('getValue')
	});
}
function excel(){
	var startTime= $('#startTime').datebox('getValue');
	var endTime=$('#endTime').datebox('getValue');
	var afterSaleDetectCode =  $('#tb input[name=code]').val();
	var status =  $('#status').combobox('getValue');
	var areaId = $('#areaId').combobox('getValue');
	location.href = "${pageContext.request.contextPath}/admin/AfStock/excelSaleUpshelf.mmx?startTime=" 
			+ startTime + "&endTime=" + endTime + "&afterSaleDetectCode=" + afterSaleDetectCode + "&status=" + status
			+ "&areaId=" + areaId;
}
</script>
</head>
<body>
<table id="datagrid"></table> 
<div id="tb" style="height: auto;">
		<fieldset>
			<legend>售后库上架任务列表</legend>
			<form id="applyFrom" method="post">
			<table class="" >
				<tr align="center" >
				 
					<th >售后处理单号</th>
					<td align="left">
						<input id="code" name="code" style="width: 116px" /></td>
					<th >售后处理单状态</th>
					<td align="left">
		          <select id="status" name="status" class="easyui-combobox" style="width: 121px" >
						<option value="">请选择</option>
						<option value="1">检测中</option>
						<option value="2">等待客户确认</option>
						<option value="3">退货</option>
						<option value="4">保修</option>
						<option value="5">换货</option>
						<option value="6">错发换货</option>
						<option value="7">付费维修</option>
						<option value="8">付费维修已完成</option>
						<option value="9">保修已完成</option>
						<option value="10">原品返回</option>
						<option value="11">原品返回</option>
						<option value="12">待封箱</option>
						<option value="13">封箱已完成</option>
						<option value="14">退货已完成</option>
						<option value="15">维修已完成</option>
						<option value="16">维修商品已退回</option>
						<option value="17">原品已退回</option>
						<option value="18">换货已发货</option>
						<option value="19">换货已退回</option>
						</select></td>
					<th >售后地区</th>
					<td align="left">
					<input id="areaId" name="areaId" style="width: 121px" /></td>
				   <th >任务生成时间</th>
					<td align="left">
						<input type="text" id="startTime" class="easyui-datebox" style="width:121px" name="startTime"/>
						--<input type="text"  name="endTime" id="endTime" class="easyui-datebox" style="width:121px"/></td>
						
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