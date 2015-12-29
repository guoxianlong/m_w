<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<%@ include file="../rec/inc/easyui.jsp" %>
<!DOCTYPE html>
<html>
<head>
<script type="text/javascript" src="${path}/admin/js/chinaProvince.js"></script>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var stockArea;
//var status = ${param.status};
$(function(){
	reloadData();
	
	datagrid = $('#datagrid').datagrid({
		url : sysPath+'/EffectDeliverController/getEffectDeliverTimeList.mmx',
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
	    frozenColumns : [[
	    ]],
	    columns:[[  
	        {field:'sheng',title:'省',width:50,align:'center'},
	        {field:'shi',title:'市',width:50,align:'center'},  
	        {field:'qu',title:'区',width:40,align:'center'}, 
	        {field:'stockName',title:'仓',width:40,align:'center'}, 
	        {field:'popName',title:'POP商家',width:40,align:'center'}, 
	        {field:'city_area_time',title:'区时效',width:40,align:'center'}, 
	        {field:'town_time',title:'乡镇时效',width:40,align:'center'}, 
	        {field:'village_time',title:'村时效',width:50,align:'center'}
	    ]],
	    onLoadError:function(){
	    	window.location.reload();
	    }
	}); 
});
function searchFun() {
	
	var popId = document.getElementById("popId").value;
	if(Number(popId) == -1){
		alert("请选择POP商家");
		return false;
	}
	datagrid.datagrid('load', {
		stockArea : $('#stockArea').combobox('getValue'),
		sheng : $("#sheng").combobox('getValue'),
		shi : $("#shi").combobox('getValue'),
		qu : $("#qu").combobox('getValue'),
		popId : document.getElementById("popId").value
	});
}
function exportFun() {
	$("#searchForm").submit();
}

function reloadData(){
	var popId = $("#popId").val();
	
	$('#stockArea').combobox({
		url : sysPath+'/Combobox/getStockoutAvailableArea.mmx?popId='+popId,
		valueField : 'id',
		textField : 'text',
		editable : false,
		panelHeight : 'auto'
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
<form id="searchForm" action="${path}/EffectDeliverController/getEffectDeliverTimeExcel.mmx" method="post">
			<table>
				<tr>
					<td>
						POP商家：
						<select id="popId" name="popId" onchange="reloadData()">
							<option value="-1">全部</option>
							<option value="0">买卖宝</option>
							<option value="2">京东</option>
		  				</select>
						仓:<input name='stockArea' id='stockArea' style="width:100px"/>
						 
						省：<input class='easyui-combobox' name='province' id='sheng' style="width:100px"/>
						市：<input class='easyui-combobox' name='city' id='shi' style="width:100px"/>
						区：<input class='easyui-combobox' name='qu' id='qu' style="width:100px"/>
						
						<mmb:permit value="3064">
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