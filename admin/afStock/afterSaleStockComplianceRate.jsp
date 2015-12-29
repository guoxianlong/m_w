<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<title>仓内作业达标率</title>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/highcharts.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/grid.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/exporting.src.js"></script>
<script type="text/javascript" charset="UTF-8">
var isQuery = 0;
function searchFun() {
	isQuery = 1;
	var startTime =  $('#startTime').datebox('getValue');
	var endTime = $('#endTime').datebox('getValue');
	var flag = true;
	if ($.trim(startTime) != "" && $.trim(endTime) != "") {
		var days = getValidateSubDays(endTime, startTime);
		if (days < 0) {
			flag = false;
			$.messager.show({
				msg : "结束时间必须大于开始时间",
				title : '提示'
			});
		}
		if (days>30){
			flag = false;
			$.messager.show({
				msg : "日期时间段不得超过31天,请重新填写！",
				title : '提示'
			});
		}
	} 
	if(flag){
		loadAfterSaleConsumingDistribution(startTime,endTime);
	}
}

function loadAfterSaleConsumingDistribution(startTime,endTime){
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/getComplianceRate.mmx',
		type : 'post',
		dataType : 'json',
		cache : 'false',
		data : {
			startTime : startTime,
			endTime : endTime
		},
		success : function(result){
			if(result.success){
				$('#complianceRate').highcharts({
			         title: { 
			         	text: '仓内作业达标率' 
			         }, 
			         xAxis: [{ 
			         	categories: ['匹配','检测','寄回用户','维修返厂','入售后库','维修'] 
			         }],
			        yAxis: [{ 
				        // Primary yAxis 
				        labels: { 
				       		 style: { 
				       		 	color: '#89A54E' 
				       		 }
				        }
			         },
			         { // Secondary yAxis		        
			        	labels: {
			        		format: '{value}%',
			        		style: { 
			        			color: '#4572A7' 
			        		}
			        	 },
			        	 opposite: true,
			        	 title: {
				        	 text: '合格率',
				        	 style: { color: '#89A54E' } 
				        }
			        }],
			        tooltip: { 
			        	shared: true 
			        },	      
			        series: [{	
			        	name : '达标个数',		        	
						type: 'column',
						data: result.obj[0]
			        },{	
			        	name : '未达标个数',		        	
						type: 'column',
						data: result.obj[1]
			        },{
			        	name:  '合格率',
						type: 'spline',
			            yAxis: 1,
						data: result.obj[2],
						tooltip: {
			                valueSuffix: '%'
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
	<fieldset>
		<legend>仓内作业达标率</legend>
			<table class="" >
				<tr align="center" >
					<th >售后单创建时间段：</th>
					<td align="left">
						<input type="text" id="startTime" class="easyui-datebox" style="width:121px" name="startTime" required/>-- 
						<input type="text"  name="endTime" id="endTime" class="easyui-datebox" style="width:121px" required/>
					</td>
					<td align="right" >
						<a class="easyui-linkbutton" iconCls="icon-search"  onclick="searchFun();" href="javascript:void(0);">查询</a>
					</td>
				</tr>
			</table>
		</fieldset>
		<div id="complianceRate" style="min-width:310px; height: 500px; margin: 0 auto"></div>
</body>
</html>