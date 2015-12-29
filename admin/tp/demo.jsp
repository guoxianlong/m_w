<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<jsp:include page="../../echarts/echarts_inc.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
$(function() {
	loadEchartsChart();
});
function loadEchartsChart() {
	var onechart = $("#oneChart");
	var myChart = echarts.init(onechart[0]);
	var option = {
		title : {
	        text: '访问数量',
	        x:"center"
	    },
	    tooltip : {
	        trigger: 'axis'
	    },
	    legend: {
            data:['日查询量'],
            y:"bottom"
	    },
	    xAxis : [
	        {
	            type : 'category',
	            boundaryGap : false,
	            data : ['周一','周二','周三','周四','周五','周六','周日']
	        }
	    ],
	    yAxis : [
	        {
                name : '日查询量',
                type : 'value'
	        }
	    ],
	    series : [
	        {
	            name:'日查询量',
	            type:'line',
	            data:[120, 132, 101, 134, 90, 230, 210]
	        }
	    ]
	};
	myChart.setOption(option);
}
</script>
</head>
<body>
<div id="oneChart" style="height:500px;border:1px solid #ccc;padding:10px;">></div>
</body>
</html>