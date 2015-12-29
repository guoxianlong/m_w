<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<title>未完结售后单状态分布</title>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/highcharts.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/grid.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/exporting.src.js"></script>
<script type="text/javascript" charset="UTF-8">
$(function () {
	loadData();
});		
function refreshFun(){
	loadData();
}		
function loadData(){
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/getAfterSaleUnfinishedOrder.mmx',
		type : 'post',
		dataType : 'json',
		success : function(result){
			if(result.success){
				$('#container').highcharts({
			        title: {
			            text: '未完结售后单状态分布'
			        },
			        xAxis: {
			            categories: [
			                '等待用户寄回','用户打款中','售后联系中','等待客户确认',
			                's单待发货','待重新申请确认费用','质检支撑中','待匹配',
			                '待检测','待入售后库','售后待发货',
			                '待返厂','财务退款中','维修中','s单发货中'
			            ]
			        },
			        yAxis: {
			            min: 0
			        },
			        tooltip: {			          
			            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
			            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
			                '<td style="padding:0"><b>{point.y} 个</b></td></tr>',
			            footerFormat: '</table>',
			            shared: true,
			            useHTML: true
			        },
			        series: [{
			            name: '售后单数',
			            type: 'column',
			            data: result.obj,
			            dataLabels: {
			                enabled: true,
			                rotation: -90,
			                color: '#FFFFFF',
			                align: 'right',
			                x: 4,
			                y: 10,
			                style: {
			                    fontSize: '13px',
			                    fontFamily: 'Verdana, sans-serif',
			                    textShadow: '0 0 3px black'
			                }
			            }
			        }]
			    });
			}
		}
	});
}
</script>
</head>
<body>
	<a class="easyui-linkbutton"  data-options="iconCls:'icon-ok',plain:true" onclick="refreshFun();" href="javascript:void(0);">刷新</a>
	<div id="container"></div>
</body>
</html>