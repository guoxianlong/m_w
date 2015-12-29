<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<%@ include file="../rec/inc/easyui.jsp" %>
<!DOCTYPE html>
<html>
<head>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var stockArea;
var deliver;
$(function(){
	var myDate = new Date();
	var year = myDate.getFullYear();   
	var month = myDate.getMonth() + 1; 
	var day = myDate.getDate();
	$('#endTime').datebox("setValue",year + "-" + month + "-" + day);
	myDate.setDate(myDate.getDate() - 15);
	$('#startTime').datebox("setValue", myDate.getFullYear() + "-" + (myDate.getMonth() + 1) + "-" + myDate.getDate());
	//加载省市区各值
	reloadData();
	
	datagrid = $('#datagrid').datagrid({
		url : sysPath+'/EffectDeliverController/getEffectOrderInfoList.mmx',
		queryParams: {
			startTime:$('#startTime').datebox("getValue"),
			endTime:$('#endTime').datebox("getValue"),
			date : 1
		},
		toolbar : '#toolbar',
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
	    columns:[[  
            {field:'order_code',title:'mmb订单号',width:30,align:'center'},
	        {field:'pop_order_code',title:'京东订单号',width:30,align:'center'},
	        {field:'package_code',title:'包裹单号',width:20,align:'center'},
	        {field:'popName',title:'POP商家',width:20,align:'center'},  
	        {field:'stockAreaName',title:'仓',width:20,align:'center'},  
	        {field:'delivername',title:'快递公司',width:30,align:'center'}, 
	        {field:'time_level',title:'级别',width:20,align:'center'}, 
	        {field:'time',title:'时效H',width:20,align:'center'}, 
	        {field:'hours',title:'已出库时长H',width:25,align:'center'}, 
	        {field:'deliver_state',title:'配送状态',width:20,align:'center'},
	        {field:'order_type',title:'订单类型',width:30,align:'center'},
	        {field:'deliver_info',title:'配送描述',width:80,align:'center'},
	        {field:'post_time',title:'最后反馈时间',width:50,align:'center'},
	        {field:'address',title:'订单地址',width:80,align:'center'},
	        {field:'provinceName',title:'省',width:20,align:'center'},
	        {field:'cityName',title:'市',width:20,align:'center'},
	        {field:'areaName',title:'区',width:20,align:'center'},
	        {field:'username',title:'最后跟进人',width:20,align:'center'},
	        {field:'hdt',title:'最后跟进时间',width:40,align:'center'},
	        {field:'remark',title:'跟进情况描述',width:40,align:'center'}
	    ]],
	    onLoadError:function(){
	    	location.reload();
	    }
	}); 
});
function searchFun() {
	var popId = $("#popId").val();
	if(Number(popId) == -1){
		alert("请选择POP商家");
		return false;
	}
	datagrid.datagrid('load', {
		popId : $('#popId').val(),
		stockArea : $('#stockArea').combobox('getValue'),
		deliver : $('#deliver').combobox('getValue'),
		sheng : $("#sheng").combobox('getValue'),
		shi : $("#shi").combobox('getValue'),
		qu : $("#qu").combobox('getValue'),
		startTime : $('#startTime').datebox("getValue"),
		endTime : $('#endTime').datebox("getValue"),
		date : document.getElementById("date").value,
		shengName:$("#sheng").combobox('getText'),
		shiName:$("#shi").combobox('getText'),
		quName:$("#qu").combobox('getText')
	});
}
function exportFun() {
	var popId = $("#popId").val();
	if(Number(popId) == -1){
		alert("请选择POP商家");
		return false;
	}
	var shengName = $("#sheng").combobox('getText');
	var shiName = $("#shi").combobox('getText');
	var quName = $("#qu").combobox('getText');
	$("#shengName").val(shengName);
	$("#shiName").val(shiName);
	$("#quName").val(quName);
	
	$("#searchForm").submit();
}

function reloadData(dom){
	var popId = $("#popId").val();
	
	$('#stockArea').combobox({
		url : sysPath+'/Combobox/getStockoutAvailableArea.mmx?popId='+popId,
		valueField : 'id',
		textField : 'text',
		editable : false,
		panelHeight : 'auto'
	});
	$('#deliver').combobox({
		url : sysPath+'/SalesReturnController/getDeliverJSON.mmx?popId='+popId,
		valueField : 'id',
		textField : 'name',
		editable : false
	});
	$('#sheng').combobox({
		url : sysPath+'/EffectDeliverController/getProvicesByPOPId.mmx?popId='+popId,
		valueField : 'id',
		textField : 'name',
		editable : false,
		onChange:function (){
			var provinceId = $(this).combobox('getValue');
			$('#shi').combobox({
				url : sysPath+'/EffectDeliverController/getCitysByProvinceId.mmx?provinceId='+provinceId,
				valueField : 'id',
				textField : 'name',
				editable : false,
				onChange:function (){
					var cityId = $(this).combobox('getValue');
					$('#qu').combobox({
						url : sysPath+'/EffectDeliverController/getDistrictsByCityId.mmx?cityId='+cityId,
						valueField : 'id',
						textField : 'name',
						editable : false 
					});
				}
			});
		}
	});
	var provinceId = $("#sheng").combobox('getValue');
	
	$('#shi').combobox({
		url : sysPath+'/EffectDeliverController/getCitysByProvinceId.mmx?provinceId='+provinceId,
		valueField : 'id',
		textField : 'name',
		editable : false
	});
	var cityId = $("#shi").combobox('getValue');
	$('#qu').combobox({
		url : sysPath+'/EffectDeliverController/getDistrictsByCityId.mmx?cityId='+cityId,
		valueField : 'id',
		textField : 'name',
		editable : false 
	});
	 
}
</script>
</head>
<body>
<div id="toolbar" class="datagrid-toolbar" style="height: auto;">
			<form id="searchForm" action="${path}/EffectDeliverController/EffectOrderInfoExcel.mmx" method="post">
			<table>
				<tr>
					<td align="left">POP商家：</td>
					<td align="left">	
					 	<select id="popId" name="popId" onchange="reloadData(this)">
							<option value="-1">全部</option>
							<option value="0">买卖宝</option>
							<option value="2">京东</option>
		  				</select>
	  				</td>
	  				<td align="right">仓:</td>
					<td align="left"><input class='easyui-combobox' name='stockArea' id='stockArea' style="width:100px"/></td>
					<td align="left">快递公司:</td>
					<td align="left"><input class='easyui-combobox' name='deliver' id='deliver' style="width:100px"/></td>
					<td align="left">	
						<input class='easyui-combobox' name='province' id='sheng' style="width:100px"/>
						<input class='easyui-combobox' name='city' id='shi' style="width:100px"/>
						<input class='easyui-combobox' name='qu' id='qu' style="width:100px"/>
						<input type="hidden" name="shengName" id="shengName">
						<input type="hidden" name="shiName" id="shiName">
						<input type="hidden" name="quName" id="quName">
					</td>
				</tr>
				<tr>
					<td align="left" colspan="2">
						<select name="date" id="date" style="width:120px">
							<option value="1">所有已超期</option>
							<option value="2">超期1天</option>
							<option value="3">超期1-2天</option>
							<option value="4">超期2-3天</option>
							<option value="5">超期3天以上</option>
							<option value="6">明日超期</option>
						</select>
					</td>
					<td align="left">出库时间:</td>
					<td align="left" colspan="3">
						<input id="startTime" name="startTime"   class="easyui-datebox" editable="false" style="width: 120px;" />至
						<input id="endTime" name="endTime"   class="easyui-datebox" editable="false" style="width: 120px;" />
					</td>
					<td align="left">
						<mmb:permit value="3065">
						&nbsp;<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查找</a>
						&nbsp;<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-print',plain:true" onclick="exportFun();">导出</a>
						</mmb:permit>
					</td>
				</tr>
			</table>
			</form>
	</div>
	<table id="datagrid"></table> 
</body>
</html>