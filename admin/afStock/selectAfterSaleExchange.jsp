<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>历史调拨查询</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
	datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getAfterSaleExchangeDatagrid.mmx',
	    toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    rownumbers : true,
	    singleSelect : true,
	    pageSize : 20,
	    pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    columns:[[
	        {field:'afterSaleDetectCode',title:'售后处理单号',width:40,align:'center'},  
	        {field:'exchangeCode',title:'调拨单号',width:40,align:'center'},  
	        {field:'createDatetime',title:'创建时间',width:25,align:'center',
	        	formatter : function(value,rowData,index){
	        		if(value != null){
	        			return value.substring(0,19);
	        		}
	        	}},
	        {field:'exchangeStatusName',title:'调拨状态',width:30,align:'center'},
	        {field:'createUserName',title:'创建人',width:15,align:'center'},  
	        {field:'auditUserName',title:'出库审核人',width:20,align:'center'},
	        {field:'sourceExchange',title:'调拨源库',width:20,align:'center'},
	        {field:'targeExchange',title:'调拨目的库',width:20,align:'center'},
	        {field:'productCode',title:'商品编号',width:20,align:'center'},
	        {field:'productOriName',title:'商品原名称',width:20,align:'center'},
	    ]]
	}); 
	$('#areaId').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getAreaType.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
	$('#stockType').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getStockType.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
    
    $('#outAreaId').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getAreaType.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
	$('#outStockType').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getStockType.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
    
    $('#exchangeStatus').combobox({
    	url : '${pageContext.request.contextPath}/Combobox/getStockExchangeStatus.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
});
function searchFun() {
	 var startTime = (new Date($('#startTime').datebox('getValue'))).getTime();//时间转换为毫秒
     var endTime = (new Date($('#endTime').datebox('getValue'))).getTime();
    
     if(endTime<startTime){
    	 $.messager.alert('警告','结束日期不能小于开始日期','info');//表示结束日期不能小于开始日期    
         return ;
     }
     if(parseInt(Math.abs(startTime - endTime ) / 1000 / 60 / 60 /24)>30){
    	 $.messager.alert('警告','时间间隔不能大于30天','info');//大于30不允许查询
         return ;
     }
	datagrid.datagrid('load', {
		startTime : $('#startTime').datebox('getValue'),
		endTime : $('#endTime').datebox('getValue'),
		afterSaleDetectCode :  $('#tb textarea[name=afterSaleDetectCode]').val(),
		exchangeCode : $('#exchangeCode').val(),
		areaId : $('#areaId').combobox('getValue'),
		stockType : $('#stockType').combobox('getValue'),
		createUserName :  $('#tb input[name=createUserName]').val(),
		outAreaId : $('#outAreaId').combobox('getValue'),
		outStockType : $('#outStockType').combobox('getValue'),
		productCode : $('#productCode').val(),
		exchangeStatus : $('#exchangeStatus').combobox('getValue')
	});
}
</script>
</head>
<body>
	<div id="tb"  style="height: auto;display: none;">
		<fieldset>
			<legend>筛选</legend>
			<table>
				<tr>
						<th align="right">售后处理单号：</th>
					<td><textarea id="afterSaleDetectCode" name="afterSaleDetectCode" style="width: 176px;height: 40px"></textarea></td>
					<th align="right">调拨单号：</th>
					<td><input type="text" id="exchangeCode" name="exchangeCode" style="width: 121px" /></td>
					<th align="right">创建时间：</th>
					<td>
						<input id="startTime" name="startTime" class="easyui-datebox" editable="false" style="width: 88px;" /> -
						<input id="endTime" name="endTime" class="easyui-datebox" editable="false" style="width: 88px;" /></td>
					<th >创建人：</th>
					<td align="left">
						<input id="createUserName" name="createUserName" style="width: 116px" /></td>
					<th >目的库类型：</th>
					<td align="left">
						<input id="stockType" name="stockType" style="width: 121px" /></td>
						<th >目的库地区：</th>
					<td align="left"><input id="areaId" name="areaId" style="width: 121px" /></td>	
				</tr>
				<tr>
					<th>商品编号：</th>
					<td align="left"><input type="text" id="productCode" name="productCode" style="width: 121px" /></td>
					<th>调拨状态：</th>
					<td align="left"><input id="exchangeStatus" name="exchangeStatus" style="width: 121px" /></td>
					<th>调拨源库类型：</th>
					<td align="left"><input id="outStockType" name="outStockType" style="width: 121px" /></td>
					<th>调拨源库地区：</th>
					<td align="left"><input id="outAreaId" name="outAreaId" style="width: 121px" /></td>	
					<td><a id="query" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-search'" onclick="searchFun()">查询</a></td>				
				</tr>
			</table>
		</fieldset>
	</div>
	<table id="datagrid"></table> 
</body>
</html>