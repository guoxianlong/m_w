<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/highcharts.src.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/themes/dark-blue.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/modules/exporting.src.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var chart;
var times = [];
var dataSendNo;
var dataSendOk;
var dataSendApply;
var dataSendApplys;
var x_id;
var searchType;
$(document).ready(function() {
	var tab = $('#tt').tabs('getSelected');  
	var title = tab.panel('options').title;
	$('#tt').tabs({  
	    border:false,  
	    onSelect:function(title){  
	        if(title == '发货成功率统计'){
	        	setSucDate();
	        	loadSucData('');
	        }else if(title == '发货及时率统计'){
	        	setTimelyDate();
	        	loadTimelyData('');
	        }
	    }  
	}); 
});
function setSucDate(){
	var date = new Date()
	var d = date.getFullYear() + '-' + (date.getMonth()+1) + '-' + date.getDate()
	$('#dateTimeS').datebox('setValue',d);
}
function setTimelyDate(){
	var date = new Date()
	var d = date.getFullYear() + '-' + (date.getMonth()+1) + '-' + date.getDate()
	$('#dateTimeT').datebox('setValue',d);
}
function loadSucData(searchType){
	if(searchType == ''){
		searchType = $('#searchTypeS').val();
	}
	$.ajax({
		url : '${pageContext.request.contextPath}/OrderStockTimelyController/getTimes.mmx',
		type : 'post',
		data : {searchType : searchType},
		cache: false,
		dataType : "json",
		success: function(result){
			times = result;
			$.ajax({
				url : '${pageContext.request.contextPath}/OrderStockTimelyController/getSucSendNo.mmx',
				type : 'post',
				data : {searchType : searchType},
				cache: false,
				dataType : "json",
				success: function(result){
					sucSendNo = result;
					$.ajax({
						url : '${pageContext.request.contextPath}/OrderStockTimelyController/getSucSendOk.mmx',
						type : 'post',
						data : {searchType : searchType},
						cache: false,
						dataType : "json",
						success: function(result){
							sucSendOk = result;
							loadSucHighCharts();
						}
					});
				}
			});
		}
	});
	
}
function loadTimelyData(searchType){
	if(searchType == ''){
		searchType = $('#searchTypeT').val();
	}
	$.ajax({
		url : '${pageContext.request.contextPath}/OrderStockTimelyController/getTimes.mmx',
		type : 'post',
		data : {searchType : searchType},
		cache: false,
		dataType : "json",
		success: function(result){
			times = result;
			$.ajax({
				url : '${pageContext.request.contextPath}/OrderStockTimelyController/getTimelySendNo.mmx',
				type : 'post',
				data : {searchType : searchType},
				cache: false,
				dataType : "json",
				success: function(result){
					timSendNo = result;
					$.ajax({
						url : '${pageContext.request.contextPath}/OrderStockTimelyController/getTimelySendApply.mmx',
						type : 'post',
						data : {searchType : searchType},
						cache: false,
						dataType : "json",
						success: function(result){
							sendApply = result;
							$.ajax({
								url : '${pageContext.request.contextPath}/OrderStockTimelyController/getTimelySendApplys.mmx',
								type : 'post',
								data : {searchType : searchType},
								cache: false,
								dataType : "json",
								success: function(result){
									sendApplys = result;
									loadTimelyHighCharts();
								}
							});
						}
					});
				}
			});
		}
	});
	
}
function loadSucHighCharts(){
	chart = new Highcharts.Chart({
		chart: {
			renderTo: 'containerS',
			defaultSeriesType: 'column'
		},
		title: {
			text: '点击柱状图查看当日作业列表'
		},
		xAxis: {
			title: {
				text: '时间'
			},
			categories: times,
		},
		yAxis: {
			allowDecimals: false,
			min: 0,
			title: {
				text: '作业量'
			}
		},
		tooltip: {
			formatter: function() {
				return '<b>'+ this.x +'</b><br/>'+
					 this.series.name +': '+ this.y +'<br/>'+
					 'Total: '+ this.point.stackTotal;
			}
		},
		plotOptions: {
			series: {
				cursor: 'pointer',
				point: {
					events: {
						click: function() {
							x_id = this.x;
							var st = $('#searchTypeS').val();
							$.ajax({
								url : '${pageContext.request.contextPath}/OrderStockTimelyController/getDateTime.mmx',
								type : 'post',
								data : {searchType : st,xid : this.x},
								cache: false,
								dataType : "json",
								success: function(r){
									$('#dateTimeS').datebox('setValue',r[0]);
								}
							});
							$('#sucDatagrid').datagrid('load', {
								searchType : st,
								xid : this.x,
							});
						}		
					}
				}
			},
			column: {
				stacking: 'normal'
			}
		},
	    series: [{
				name: '未发货订单',
				data: sucSendNo,
				stack: 'male'
				},{
				name: '实际发货订单',
				data: sucSendOk,
				stack: 'male'
			}]
	});
}
function loadTimelyHighCharts(){
	chart = new Highcharts.Chart({
		chart: {
			renderTo: 'containerT',
			defaultSeriesType: 'column'
		},
		title: {
			text: '点击柱状图查看当日作业列表'
		},
		xAxis: {
			title: {
				text: '时间'
			},
			categories: times,
		},
		yAxis: {
			allowDecimals: false,
			min: 0,
			title: {
				text: '作业量'
			}
		},
		tooltip: {
			formatter: function() {
				return '<b>'+ this.x +'</b><br/>'+
					 this.series.name +': '+ this.y +'<br/>'+
					 'Total: '+ this.point.stackTotal;
			}
		},
		plotOptions: {
			series: {
				cursor: 'pointer',
				point: {
					events: {
						click: function() {
							x_id = this.x;
							var st = $('#searchTypeT').val();
							$.ajax({
								url : '${pageContext.request.contextPath}/OrderStockTimelyController/getDateTime.mmx',
								type : 'post',
								data : {searchType : st,xid : this.x},
								cache: false,
								dataType : "json",
								success: function(r){
									$('#dateTimeT').datebox('setValue',r[0]);
								}
							});
							$('#timDatagrid').datagrid('load', {
								searchType : st,
								xid : this.x,
							});
						}		
					}
				}
			},
			column: {
				stacking: 'normal'
			}
		},
	    series: [{
				name: '未发货订单',
				data: timSendNo,
				stack: 'male'
				},{
				name: '申请发货多次且已发货订单',
				data: sendApplys,
				stack: 'male'
				},{
				name: '申请发货一次且已发货订单',
				data: sendApply,
				stack: 'male'
				}]
	});
}
function searchSucFun(){
	$('#sucDatagrid').datagrid('load', {
		orderType : $('#sucForm input[name=orderType]').val(),
		dateTime : $('#sucForm input[name=dateTime]').val(),
		orderCode : $('#sucForm input[name=orderCode]').val(),
	});
}
function clearSucFun() {
	$('#sucTB input').val('');
	$('#sucDatagrid').datagrid('load', {});
}
function searchTimFun(){
	$('#timDatagrid').datagrid('load', {
		orderType : $('#timForm input[name=orderType]').val(),
		dateTime : $('#timForm input[name=dateTime]').val(),
		orderCode : $('#timForm input[name=orderCode]').val(),
	});
}
function clearTimFun() {
	$('#timTB input').val('');
	$('#timDatagrid').datagrid('load', {});
}
function excelTimDetail(){
	var d = $('#dateTimeT').datebox('getValue');
	var s = $('#searchTypeT').val();
	location.href = "${pageContext.request.contextPath}/OrderStockTimelyController/excelTimDetail.mmx?searchType=" + s + "&xid=" + x_id + "&date=" + d;
}
function excelSucDetail(){
	var d = $('#dateTimeS').datebox('getValue');
	var s = $('#searchTypeS').val();
	location.href = "${pageContext.request.contextPath}/OrderStockTimelyController/excelSucDetail.mmx?searchType=" + s + "&xid=" + x_id + "&date=" + d;
}
</script>
</head>
<body>
	<div id="tt" class="easyui-tabs" style="height: 850px">    
	    <div title="发货成功率统计" style="padding:10px;" >
		    <div id="sucTB" style="padding:3px;height: auto;">
				<fieldset>
				<legend>统计图表</legend>
					<div id="containerS" style="min-width:310px; height: 400px; margin: 0 auto"></div>
					<div align="right">
						<strong>统计周期:</strong>
						<select id="searchTypeS" onchange="loadSucData(this.value)" >
							<option value="day">按天</option>
							<option value="week">按周</option>
							<option value="month">按月</option>
						</select> 
					</div>
				</fieldset>
				<form id="sucForm" method="post">
					<table style="width: 100%;">
						<tr><td align="left" width="80%">
							订单状态:<select name="orderType" class="easyui-combobox" editable="false" style="width: 145px">
								<option value="">请选择</option>
								<option value="1">未发货订单</option>
								<option value="2">实际发货订单</option>
							</select> 
							时间:<input id="dateTimeS" name="dateTime" class="easyui-datebox" editable="false"/>
							订单号:<input id="" name="orderCode" type="text"/>
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchSucFun()">查 询 </a>
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="clearSucFun()">清 空 </a>
						</td>
						<td align="right" width="20%">
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-redo" plain="true" onclick="excelSucDetail()">导出明细到excel</a>
						</td></tr>
					</table>
				</form>
			</div>    
	       <table id="sucDatagrid" class="easyui-datagrid" style="height:auto;width:auto; display: none;"
				url="${pageContext.request.contextPath}/OrderStockTimelyController/getSucDatagrid.mmx"  
				nowrap="false" border="false" idField="id" fit="true" fitColumns="true" title=""
				pageSize ="10"pageList="[ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ]"
				toolbar="#sucTB" rownumbers="true" pagination="true" singleSelect="true"> 
				<thead>
				<tr>
					<th field="id" width="20" align="center" hidden="true" checkbox="false">ID</th>
					<th field="orderCode" width="100"  align="center">订单号</th>
					<th field="stockOutUserId" width="70" align="center"
						data-options="formatter : function(value, rowData, rowIndex) {
						if(rowData.stockOutUserId == 0){ return '待发货'}else{return '已复核'}}">订单状态</th>
					<th field="date" width="70" align="center" 
						data-options="formatter : function(value, rowData, rowIndex) {
						if(rowData.date != null){return rowData.date.substring(0,19);}}">日期</th>
					<th field="orderStockCount" width="50" align="center">申请发货次数</th>
					<th field="firstOrderStockDatetime" width="100" align="center" 
						data-options="formatter : function(value, rowData, rowIndex) {
						if(rowData.firstOrderStockDatetime != null){return rowData.firstOrderStockDatetime.substring(0,19);}}">申请发货时间</th>
					<th field="firstOrderStockUserName" width="70" align="center" >申请人</th>
					<th field="stockOutDatetime" width="70" align="center" 
						data-options="formatter : function(value, rowData, rowIndex) {
						if(rowData.stockOutDatetime != null){return rowData.stockOutDatetime.substring(0,19);}}">复核时间</th>
					<th field="stockOutUserName" width="70" align="center">复核操作人</th>
				</tr>
				</thead>
			</table>
	    </div>    
	    <div title="发货及时率统计" style="padding:20px;" >    
	        <div id="timTB" style="padding:3px;height: auto;">
				<fieldset>
				<legend>统计图表</legend>
					<div id="containerT" style="min-width:310px; height: 400px; margin: 0 auto"></div>
					<div align="right">
						<strong>统计周期:</strong>
						<select id="searchTypeT" onchange="loadTimelyData(this.value)" >
							<option value="day">按天</option>
							<option value="week">按周</option>
							<option value="month">按月</option>
						</select> 
					</div>
				</fieldset>
				<form id="timForm" method="post">
					<table style="width: 100%;">
						<tr><td align="left" width="80%">
							订单状态:<select name="orderType" class="easyui-combobox" editable="false" style="width: 145px">
								<option value="">请选择</option>
								<option value="1">未发货</option>
								<option value="2">已发货(申请发货多次)</option>
								<option value="3">已发货(申请发货一次)</option>
							</select> 
							时间:<input id="dateTimeT" name="dateTime" class="easyui-datebox" editable="false"/>
							申请出库次数:<input id="" name="orderStockCount" type="text"/>
							订单号:<input id="" name="orderCode" type="text"/>
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchTimFun()">查 询 </a>
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="clearTimFun()">清 空 </a>
						</td>
						<td align="right" width="20%">
							<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-redo" plain="true" onclick="excelTimDetail()">导出明细到excel</a>
						</td></tr>
					</table>
				</form>
			</div>
			<table id="timDatagrid" class="easyui-datagrid" style="height:auto;width:auto; display: none;"
				url="${pageContext.request.contextPath}/OrderStockTimelyController/getTimelyDatagrid.mmx"  
				nowrap="false" border="false" idField="id" fit="true" fitColumns="true" title=""
				pageSize ="10"pageList="[ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ]"
				toolbar="#timTB" rownumbers="true" pagination="false" singleSelect="true"> 
				<thead>
				<tr>
					<th field="id" width="20" align="center" hidden="true" checkbox="false">ID</th>
					<th field="orderCode" width="100"  align="center">订单号</th>
					<th field="stockOutUserId" width="70" align="center"
						data-options="formatter : function(value, rowData, rowIndex) {
						if(rowData.stockOutUserId == 0){ return '待发货'}else{return '已复核'}}">订单状态</th>
					<th field="date" width="70" align="center" 
						data-options="formatter : function(value, rowData, rowIndex) {
						if(rowData.date != null){return rowData.date.substring(0,19);}}">日期</th>
					<th field="orderStockCount" width="50" align="center">申请发货次数</th>
					<th field="firstOrderStockDatetime" width="100" align="center" 
						data-options="formatter : function(value, rowData, rowIndex) {
						if(rowData.firstOrderStockDatetime != null){return rowData.firstOrderStockDatetime.substring(0,19);}}">申请发货时间</th>
					<th field="firstOrderStockUserName" width="70" align="center" >申请人</th>
					<th field="stockOutDatetime" width="70" align="center" 
						data-options="formatter : function(value, rowData, rowIndex) {
						if(rowData.stockOutDatetime != null){return rowData.stockOutDatetime.substring(0,19);}}">复核时间</th>
					<th field="stockOutUserName" width="70" align="center">复核操作人</th>
				</tr>
				</thead>
			</table>   
	    </div>    
    </div> 
</body>
</html>