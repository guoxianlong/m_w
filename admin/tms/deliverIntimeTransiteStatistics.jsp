<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>快递公司当日交接及时率统计</title>

<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<jsp:include page="/highcharts/inc.jsp"></jsp:include>

<script type='text/javascript'>
var startDate;
var endDate;
function searchFun(){
	var index = 0;
	var names = [];
	var areaNames = $('#areaId').combotree('getText');
	var deliverNames = $('#deliverId').combotree('getText');
	startDate = $('#startDate').datebox('getValue');
	endDate = $('#endDate').datebox('getValue');
	if(deliverNames == ''){
		$.messager.alert('提示消息','请选择快递公司,再查询!','info');
		return;
	}
	if(areaNames == ''){
		$.messager.alert('提示消息','请选择发货地区,再查询!','info');
		return;
	}
	if(startDate == '' || endDate == ''){
		$.messager.alert('提示消息','请选择起始结束时间,再查询!','info');
		return;
	}
	$.each(deliverNames.split(","), function(i, deliverName) {
		$.each(areaNames.split(","), function(j, areaName) {
			names[index] = deliverName + "_" + areaName;
			index++;
		});
	});
	loadHighstock(names);
}
function loadHighstock(names){
	var seriesOptions = [],
	yAxisOptions = [],
	seriesCounter = 0,
	colors = Highcharts.getOptions().colors;
	$.each(names, function(i, name) {
		
		$.getJSON('${pageContext.request.contextPath}/deliverController/getHighstockData.mmx?parameter=intimeTransite_' + name + "&startDate=" + startDate + "&endDate=" + endDate,	function(data) {
			seriesOptions[i] = {
				name: name,
				data: data
			};
			// As we're loading the data asynchronously, we don't know what order it will arrive. So
			// we keep a counter and create the chart when all the data is loaded.
			seriesCounter++;
	
			if (seriesCounter == names.length) {
				createChart();
			}
		});
	});
	// create the chart when all data is loaded
	function createChart() {
	
		$('#container').highcharts('StockChart', {
		    chart: {
		    },
		    rangeSelector: {
				inputEnabled: $('#container').width() > 480,
		        selected: 4
		    },
		    tooltip: {
		    	pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}%</b><br/>',
		    	valueDecimals: 2
		    },
		    series: seriesOptions
		});
	}
}
</script>
</head>
<body>
	<fieldset>
		<legend>筛选</legend>
			<div align="left">
				<select id="areaId" class="easyui-combotree" style="width:156px;"
				        data-options="url:'${pageContext.request.contextPath}/Combobox/getAreasLimit.mmx',required:true,multiple:true">
				</select>&nbsp;&nbsp;
				<select id="deliverId" class="easyui-combotree" style="width:186px;"
				        data-options="url:'${pageContext.request.contextPath}/Combobox/getDelivers.mmx',required:true,multiple:true">
				</select>&nbsp;&nbsp;
				<input id="startDate" name="startDate" class="easyui-datebox" required="required" editable="false"/> --
				<input id="endDate" name="endDate" class="easyui-datebox" required="required" editable="false"/>
				&nbsp;&nbsp;&nbsp;
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="flase" onclick="searchFun()">查 询 </a>
			</div>
	</fieldset>
	<div id="container" style="height: 400px; min-width: 310px"></div>
</body>
</html>

